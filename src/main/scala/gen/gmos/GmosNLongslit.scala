// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.gen.gmos

import basic.ObservingMode
import basic.syntax.all._

import gem.Step
import gem.config.DynamicConfig.GmosN
import gem.config.GmosConfig._
import gem.enum._
import gem.math.Wavelength

import fs2.{Pure, Stream}

import java.time.Duration

/**
 * Sequence generation for GMOS North Longslit.
 */
sealed trait GmosNLongslit {

  /**
   * Produces an infinite acquisition sequence. The idea is that the sequence
   * should be stopped when the target is acquired.
   */
  def acquisition(e: Duration): Stream[Pure, Step.GmosN]

  /**
   * Produces an infinite science sequence.  Each sequence element is a finite
   * mini-sequence containing steps that must be executed together. The
   * sequence should be stopped when the desired S/N ratio has been met.
   */
  def science(e: Duration): Stream[Pure, Stream[Pure, Step.GmosN]]

}

object GmosNLongslit {

  // TODO: I don't know why, but if this is imported at the top, say just after
  // import gem.math.{Angle, Offset, Wavelength} then the syntax is not found.
  import gem.syntax.prism._

  def apply(mode: ObservingMode.Spectroscopy.GmosNorth): GmosNLongslit =

    new GmosNLongslit with GmosNOps with GmosLongslitMath {

      override def acquisition(e: Duration): Stream[Pure, Step.GmosN] = {

        // Find the filter with the closest wavelength.
        val filter = GmosNorthFilter.allAcquisition.minBy { f =>
          (mode.λ.toPicometers - f.wavelength.toPicometers).abs
        }

        eval {
          for {
            _  <- GmosN.exposureTime := e
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

            _  <- GmosN.exposureTime := e * 4
            s2 <- scienceStep(0.arcsec, 0.arcsec)
          } yield Stream(s0, s1) ++ Stream(s2).repeat
        }

      }

      override def science(e: Duration): Stream[Pure, Stream[Pure, Step.GmosN]] = {

        // Adds two wavelength values. This is unsafe in general because of the
        // possibility of overflow.  Here we know that Δ is at most 30 nm and λ is
        // a reasonable observing wavelength so it cannot fail.  He he.
        def sum(λ: Wavelength, Δ: Wavelength): Wavelength =
          Wavelength.fromPicometers.unsafeGet(λ.toPicometers + Δ.toPicometers)

        eval {
          for {
            _  <- GmosN.exposureTime := e
            _  <- GmosN.xBinning     := xbin(mode.fpu)
            _  <- GmosN.yBinning     := GmosYBinning.Two
            _  <- GmosN.grating      := Some(GmosGrating(mode.disperser, GmosDisperserOrder.One, mode.λ))
            // _  <- GmosN.filter       := tbd
            _  <- GmosN.fpu          := Some(Right(mode.fpu))
            s0 <- scienceStep(0.arcsec, 0.arcsec)
            f0 <- smartFlatStep

            _  <- GmosN.wavelength   := sum(mode.λ, Δλ(mode.disperser))
            s1 <- scienceStep(0.arcsec, 15.arcsec)
            f1 <- smartFlatStep
          } yield Stream(
                    Stream(s0, f0),
                    Stream(f1, s1),
                    Stream(s1, f1),
                    Stream(f0, s0)
                  ).repeat
        }

      }

    }

}
