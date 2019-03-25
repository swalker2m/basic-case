// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic

import basic.enum._
import basic.itc.ItcImpl
import basic.misc._
import cats.effect._
import cats.tests._
import gem.enum._
import gem.math.Wavelength
import org.scalatest.Assertion
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class ItcTest extends CatsSuite {

  implicit val contextShift: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)

  def targetProfile(magnitude: Double) =
    TargetProfile(
      spatialProfile       = SpatialProfile.UniformSource,
      spectralDistribution = SpectralDistribution.Library(Right(NonStellarLibrarySpectrum.Neptune)),
      magnitude            = magnitude,
      magnitudeSystem      = MagnitudeSystem.Vega,
      magnitudeBand        = MagnitudeBand.R,
      redshift             = Redshift(0.0)
    )

  val observingMode =
    ObservingMode.Spectroscopy.GmosNorth(
      λ         = Wavelength.fromNanometers.unsafeGet(755),
      disperser = GmosNorthDisperser.B600_G5307,
      fpu       = GmosNorthFpu.LongSlit_0_25,
      filter    = None // Some(GmosNorthFilter.OG515)
    )

  def withItc(f: Itc[IO] => IO[Assertion]): Assertion =
    ItcImpl.forHeroku[IO].use(f).unsafeRunSync

  test("source too bright") {
    withItc { itc =>
      itc.calculate(targetProfile(1.0), observingMode, 1000).map {
        _ should be (Itc.Result.SourceTooBright)
      }
    }
  }

  test("single exposure") {
    withItc { itc =>
      itc.calculate(targetProfile(9.0), observingMode, 1000).map {
        _ should be (Itc.Result.Success(99.seconds, 1, 1034))
      }
    }
  }

  test("multiple exposures") {
    withItc { itc =>
      itc.calculate(targetProfile(15.0), observingMode, 1000).map {
        _ should be (Itc.Result.Success(20.minutes, 21, 1015))
      }
    }
  }

}
