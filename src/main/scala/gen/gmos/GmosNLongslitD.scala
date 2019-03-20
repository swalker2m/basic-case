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
import cats.effect.{ Sync, Timer }
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
   * Generates an acquisition sequence which terminates when the provided
   * `acquired` effect evaluates `true`.
   *
   * @param itc integration time calculator
   * @param acquired a program that can be evaluated to determine whether the
   *                 acquisition has completed
   *
   * @return a sequence that acquires the target
   */
  def acquisition(
    itc:      Itc[F],
    acquired: F[Boolean]
  ): Stream[F, Step.GmosN]

  /**
   * Generates a reacquisition sequence which terminates when the provided
   * `acquired` effect evaluates `true`.  This is appropriate for guiding with
   * PWFS options.
   *
   * @param itc integration time calculator
   * @param acquired a program that can be evaluated to determine whether the
   *                 reacquisition has completed
   *
   * @return a sequence that reacquires the target
   */
  def reacquisition(
    itc:      Itc[F],
    acquired: F[Boolean]
  ): Stream[F, Step.GmosN]

  /**
   * Generates a science sequence which terminates when the provided `reachedS2N`
   * effect evalutes `true`. Computes the science sequence as a stream of
   * "science/flat" "atoms" where the offset in Q and observing wavelength vary.
   *
   * @param itc integration time calculator
   * @param reachedS2N a program that can be evaluated to determine whether the
   *                   desired s/n ratio has been reached
   *
   * @return a science sequence that termines when the desired s/n ratio is
   *         achieved
   */
  def science(
    itc:        Itc[F],
    reachedS2N: F[Boolean]
  ): Stream[F, Stream[F, Step.GmosN]]

  /**
   * Generates a sequence, including initial acquisition and reacquisition as
   * necessary, that is as long as necessary to achieve the desired s/n ratio.
   * Integration time is adjusted throughout to match conditions.  This version
   * combines `acquisition`, `reacquisition`, and `science` into a single
   * complete sequence.  Since it includes reacquisition, this version is
   * appropriate for guiding with PWFS options.
   *
   * @param itc integration time calculator
   * @param acquired a program that can be evaluated to determine whether the
   *                 acquisition has completed
   * @param reachedS2N a program that can be evaluated to determine whether the
   *                   desired s/n ratio has been reached
   * @param reacquirePeriod perform a reacquire every time this period passes
   *
   * @return a sequence that acquires (and reacquires as necessary) the target
   *         and collects science data and calibrations until the desired s/n
   *         ratio is achieved
   */
  def sequenceWithReacquisition(
    itc:             Itc[F],
    acquired:        F[Boolean],
    reachedS2N:      F[Boolean],
    reacquirePeriod: FiniteDuration
  ): Stream[F, Step.GmosN]

  /**
   * Generates a sequence, including the initial acquisition, that is as long as
   * necessary to achieve the desired s/n ratio.  Integration time is adjusted
   * throughout to match conditions. This version combines `acquisition` and
   * `science` into a single complete sequence. It omits `reacquisition` to
   * provide an alternative when OI is used for guiding.
   *
   * @param itc integration time calculator
   * @param acquired a program that can be evaluated to determine whether an
   *                 the acquisition has completed
   * @param reachedS2N a program that can be evaluated to determine whether the
   *                   desired s/n ratio has been reached
   * @param reacquirePeriod perform a reacquire every time this period passes
   *
   * @return a sequence that acquires the target and collects science data and
   *         calibrations until the desired s/n ratio is achieved
   */
  def sequence(
    itc:             Itc[F],
    acquired:        F[Boolean],
    reachedS2N:      F[Boolean]
  ): Stream[F, Step.GmosN] =
    sequenceWithReacquisition(itc, acquired, reachedS2N, FiniteDuration(Long.MaxValue, NANOSECONDS))

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

  /**
   * Unique step configurations used to form an acquisition sequence. Steps
   * `ccd2` and `slit` are missing exposure time which must be set based upon
   * ITC calculations.
   *
   * @param ccd2 initial image with CCD2 ROI
   * @param p10  image through the slit at offset p 10
   * @param slit image through the slit
   */
  final case class AcquisitionSteps(
    ccd2: Step.GmosN,
    p10:  Step.GmosN,
    slit: Step.GmosN
  )

  object AcquisitionSteps extends GmosNOps {

    // Find the filter with the closest wavelength.
    private def filter(mode: ObservingMode.Spectroscopy.GmosNorth): GmosNorthFilter =
      GmosNorthFilter.allAcquisition.minBy { f =>
        (mode.λ.toPicometers - f.wavelength.toPicometers).abs
      }

    /**
     * Creates an AcquisitionSteps from the observing mode.
     */
    def apply(mode: ObservingMode.Spectroscopy.GmosNorth): AcquisitionSteps =
      eval {
        for {
          _  <- GmosN.filter       := Some(filter(mode))
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
        } yield AcquisitionSteps(s0, s1, s2)
      }

  }

  /**
   * Unique step configurations used to form a science sequence. The science
   * steps are missing exposure time which must be set via ITC calculations.
   *
   * @param science0 science step at offset (0, 0) and requested λ
   * @param flat0    smart flat matching `science0`
   * @param science1 science step at offset (0, 15) and λ + Δ
   * @param flat1    smart flat matching `science1`
   */
  final case class ScienceSteps(
    science0: Step.GmosN, // ((0, 0), λ)
    flat0:    Step.GmosN,
    science1: Step.GmosN, // ((0, 15), λ + Δ)
    flat1:    Step.GmosN
  )

  object ScienceSteps extends GmosNOps with GmosLongslitMath {

    // Adds two wavelength values. This is unsafe in general because of the
    // possibility of overflow.  Here we know that Δ is at most 30 nm and λ is
    // a reasonable observing wavelength so it cannot fail.  He he.
    private def sum(λ: Wavelength, Δ: Wavelength): Wavelength = {
      import gem.syntax.prism._
      Wavelength.fromPicometers.unsafeGet(λ.toPicometers + Δ.toPicometers)
    }

    /**
     * Creates ScienceSteps from the observing mode.
     */
    def apply(mode: ObservingMode.Spectroscopy.GmosNorth): ScienceSteps =
      eval {
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
        } yield ScienceSteps(s0, f0, s1, f1)
      }

  }

  def apply[F[_]: Sync : Timer](
    mode:      ObservingMode.Spectroscopy.GmosNorth,
    magnitude: MagnitudeValue
  ): GmosNLongslitD[F] =

    new GmosNLongslitD[F] with GmosNOps with GmosLongslitMath {

      val emptySequence: Stream[F, Step.GmosN] =
        Stream.empty.covaryAll[F, Step.GmosN]

      // This is a placeholder for the integration time lookup.  It will
      // surely need other parameters for current conditions and to
      // distinguish acquisition from science.
      def acquisitionTime(itc: Itc[F]): F[FiniteDuration] =
        itc.integrationTime(mode, magnitude)

      // Computes the acquisition sequence, which terminates after 2 or more
      // steps when the provided `acquired` effect evaluates `true`.
      override def acquisition(
        itc:      Itc[F],
        acquired: F[Boolean]
      ): Stream[F, Step.GmosN] = {

        val steps = AcquisitionSteps(mode)

        // An effect that computes the initial CCD2 image step.
        val ccd2: F[Step.GmosN] =
          exposureTime.evalSet(acquisitionTime(itc))(steps.ccd2)

        // Runs step s0 (with exposure time from ITC), s1, and then continually
        // repeates s2 (with continually updated exposure time from ITC) until
        // acquisition is complete.
        Stream.eval(ccd2)              ++
          Stream(steps.p10)            ++
          reacquisition(itc, acquired)

      }

      override def reacquisition(
        itc:      Itc[F],
        acquired: F[Boolean]
      ): Stream[F, Step.GmosN] = {

        val steps = AcquisitionSteps(mode)

        // An effect that computes the "through-slit image" part of acqusition.
        val slitImage: F[Step.GmosN] =
          exposureTime.evalSet(acquisitionTime(itc).map(_ * 4))(steps.slit)

        Stream.eval(slitImage) ++
          Stream.repeatEval(slitImage).evalTakeWhileNot(acquired)

      }

      override def science(
        itc:        Itc[F],
        reachedS2N: F[Boolean]
      ): Stream[F, Stream[F, Step.GmosN]] = {

        val steps = ScienceSteps(mode)

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

      override def sequenceWithReacquisition(
        itc:             Itc[F],
        acquired:        F[Boolean],
        reachedS2N:      F[Boolean],
        reacquirePeriod: FiniteDuration
      ): Stream[F, Step.GmosN] = {

        // An infinite Stream[F, Stream[F, Step.GmosN]] where at each step we
        // check whether a reacquisition is necessary
        val reacquireAsNecessary: Stream[F, Stream[F, Step.GmosN]] =
          Stream.every[F](reacquirePeriod).drop(1).map { doReacq =>
            if (doReacq) reacquisition(itc, acquired) else emptySequence
          }

        // Executes the science stream for as long as necessary to reach desired
        // signal to noise, performing (re)acquisitions between "atoms" as
        // necessary.
        acquisition(itc, acquired) ++
          (reacquireAsNecessary.zipWith(science(itc, reachedS2N))(_ ++ _)).flatten
      }


    }

}