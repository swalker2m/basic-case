// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic
package gen.gmos

import basic.syntax.all._

import gem.Step
import gem.config.DynamicConfig.GmosN
import gem.config.GmosConfig._
import gem.enum._
import gem.math.{ MagnitudeValue, Wavelength }

import cats.Functor
import cats.effect.Sync
import cats.implicits._
import fs2.Stream
import monocle.{Prism, Optional}

import java.time.Duration
import java.util.concurrent.TimeUnit.NANOSECONDS

import scala.concurrent.duration.FiniteDuration

/**
 * Dynamic sequence generation for GMOS North Longslit.
 */
sealed trait GmosNLongslitD[F[_]] {

  /**
   * Generates a sequence that is as long as necessary to achieve the desired
   * s/n ratio.  Integration time is adjusted throughout to match conditions.
   *
   * @param itc integration time calculator
   * @param acquired a program that can be evaluated to determine whether an
   *                 acquisition (or reacquisition) is necessary
   * @param reachedS2N a program that can be evaluated to determine whether the
   *                   desired s/n ratio has been reached
   * @tparam F effect
   *
   * @return a sequence that acquires (and reacquires as necessary) the target
   *         and collects science data and calibrations until the desired s/n
   *         ratio is achieved
   */
  def sequence(
    itc:        Itc[F],
    acquired:   F[Boolean],
    reachedS2N: F[Boolean]
  ): Stream[F, Step.GmosN]

}


object GmosNLongslitD {

  object exposureTime {

    // Maximum `java.time.Duration` that can be represented in a
    // `FiniteDuration`.
    val Max: Duration =
      Duration.ofNanos(Long.MaxValue)

    private def toFiniteDuration(d: Duration): Option[FiniteDuration] =
      Some(d).filter(_.compareTo(Max) <= 0).as(FiniteDuration(d.toMillis, NANOSECONDS))

    private def toDuration(fd: FiniteDuration): Duration =
      Duration.ofNanos(fd.toNanos)

    // Converter between Java and Scala durations.
    val duration: Prism[Duration, FiniteDuration] =
      Prism(toFiniteDuration)(toDuration)

    // Optional to set exposure time in a step.
    val optional: Optional[Step.GmosN, FiniteDuration] =
      Step.GmosN.dynamicConfig composeLens GmosN.exposureTime composePrism duration

    // Lookup the exposure time and set it in the given step.
    def evalSet[F[_]: Sync](time: F[FiniteDuration])(s: Step.GmosN): F[Step.GmosN] =
      time.map { optional.set(_)(s) }

  }

  // TODO: somewhere else?
  final implicit class StreamOps[F[_], O](s: Stream[F, O]) {

    // Takes from the Stream while the provided stop condition evaluates `true`.
    def evalTakeWhile(f: F[Boolean]): Stream[F, O] =
      s zipLeft Stream.repeatEval(f).takeWhile(identity)

    // Takes from the Stream while the provided stop condition evaluates `false`.
    def evalTakeWhileNot(f: F[Boolean])(implicit ev: Functor[F]): Stream[F, O] =
      evalTakeWhile(f.map(b => !b))

  }

  def apply[F[_]: Sync](
    mode:      ObservingMode.Spectroscopy.GmosNorth,
    magnitude: MagnitudeValue
  ): GmosNLongslitD[F] =

    new GmosNLongslitD[F] with GmosNOps with GmosLongslitMath {

      // Computes the acquisition sequence, which terminates after 2 or more
      // steps when the provided `acquired` effect evaluates `true`.
      private def acquisition(
        itc:      Itc[F],
        acquired: F[Boolean]
      ): Stream[F, Step.GmosN] = {

        // Find the filter with the closest wavelength.
        val filter = GmosNorthFilter.allAcquisition.minBy { f =>
          (mode.λ.toPicometers - f.wavelength.toPicometers).abs
        }

        // Names the 3 unique configurations that are required.
        final case class Steps(
          s0: Step.GmosN,
          s1: Step.GmosN,
          s2: Step.GmosN
        )

        val steps: Steps = eval {
          for {
            _  <- GmosN.filter       := Some(filter)
            _  <- GmosN.fpu          := None
            _  <- GmosN.grating      := None
            _  <- GmosN.xBinning     := GmosXBinning.Two
            _  <- GmosN.yBinning     := GmosYBinning.Two
            _  <- GmosN.roi          := GmosRoi.Ccd2
            s0 <- scienceStep(0.arcsec, 0.arcsec)

            _  <- GmosN.exposureTime := 20.seconds
            _  <- GmosN.fpu          := Some(Right(mode.fpu))
            _  <- GmosN.xBinning     := GmosXBinning.One
            _  <- GmosN.yBinning     := GmosYBinning.One
            _  <- GmosN.roi          := GmosRoi.CentralStamp
            s1 <- scienceStep(10.arcsec, 0.arcsec)

            s2 <- scienceStep(0.arcsec, 0.arcsec)
          } yield Steps(s0, s1, s2)
        }

        // This is a placeholder for the integration time lookup.  It will
        // surely need other parameters for current conditions and to
        // distinguish acquisition from science.
        val t = itc.integrationTime(mode, magnitude)

        // An effect that computes the "through-slit image" part of acqusition.
        val slitImage = exposureTime.evalSet(t.map(_ * 4))(steps.s2)

        // Runs step s0 (with exposure time from ITC), s1, and then continually
        // repeates s2 (with continually updated exposure time from ITC) until
        // acquisition is complete.
        Stream.eval(exposureTime.evalSet(t)(steps.s0)) ++
          Stream(steps.s1)                             ++
          Stream.eval(slitImage)                       ++
          Stream.repeatEval(slitImage).evalTakeWhileNot(acquired)

      }

      // Computes the science sequence as a stream of "science/flat" "atoms"
      // where the offset in Q and observing wavelength vary. Continues until
      // the provided `reachedS2N` effect evalues `true`.
      private def science(
        itc:        Itc[F],
        reachedS2N: F[Boolean]
      ): Stream[F, Stream[F, Step.GmosN]] = {

        // Adds two wavelength values. This is unsafe in general because of the
        // possibility of overflow.  Here we know that Δ is at most 30 nm and λ is
        // a reasonable observing wavelength so it cannot fail.  He he.
        def sum(λ: Wavelength, Δ: Wavelength): Wavelength = {
          import gem.syntax.prism._
          Wavelength.fromPicometers.unsafeGet(λ.toPicometers + Δ.toPicometers)
        }

        // Names the 4 unique configurations that are required.
        final case class Steps(
          science0: Step.GmosN, // ((0, 0), λ)
          flat0:    Step.GmosN,
          science1: Step.GmosN, // ((0, 15), λ + Δ)
          flat1:    Step.GmosN
        )

        val steps: Steps = eval {
          for {
            _  <- GmosN.xBinning     := xbin(mode.fpu)
            _  <- GmosN.yBinning     := GmosYBinning.Two
            _  <- GmosN.grating      := Some(GmosGrating(mode.disperser, GmosDisperserOrder.One, mode.λ))
            _  <- GmosN.filter       := mode.filter
            _  <- GmosN.fpu          := Some(Right(mode.fpu))
            s0 <- scienceStep(0.arcsec, 0.arcsec)
            f0 <- smartFlatStep

            _  <- GmosN.wavelength   := sum(mode.λ, Δλ(mode.disperser))
            s1 <- scienceStep(0.arcsec, 15.arcsec)
            f1 <- smartFlatStep
          } yield Steps(s0, f0, s1, f1)
        }

        // A mini stream containing two steps, one a science dataset and the
        // other a smart flat.  These shouldn't be broken up by acquisition so
        // we'll make the nested stream visible to the caller.
        def substream(a: Step.GmosN, b: Step.GmosN): Stream[F, Step.GmosN] =
          Stream.force(itc.integrationTime(mode, magnitude).map { fd =>
            val f = exposureTime.optional.set(fd)
            Stream(f(a), f(b)).covary[F]
          })

        Stream(
          substream(steps.science0, steps.flat0   ),
          substream(steps.flat1,    steps.science1),
          substream(steps.science1, steps.flat1   ),
          substream(steps.flat0,    steps.science1)
        ).covary[F]
         .repeat
         .evalTakeWhileNot(reachedS2N)

      }

      private val emptySequence: Stream[F, Step.GmosN] =
        Stream.empty.covaryAll[F, Step.GmosN]

      override def sequence(
        itc:        Itc[F],
        acquired:   F[Boolean],
        reachedS2N: F[Boolean]
      ): Stream[F, Step.GmosN] = {

        // The raw acquisition sequence
        val acqSequence: Stream[F, Step.GmosN] =
          acquisition(itc, acquired)

        // An infinite Stream[F, Stream[F, Step.GmosN]] where at each step we
        // check whether an acquisition is necessary and output an empty Stream
        // if not, or else an acquisition sequence if so.
        val reacquireAsNecessary: Stream[F, Stream[F, Step.GmosN]] =
          Stream.repeatEval {
            acquired.map { a => if (a) emptySequence else acqSequence }
          }.cons1(acqSequence) // always do an acquisition sequence at first

        // Executes the science stream for as long as necessary to reach desired
        // signal to noise, performing (re)acquisitions between "atoms" as
        // necessary.
        (reacquireAsNecessary.zipWith(science(itc, reachedS2N))(_ ++ _)).flatten
      }


    }

}