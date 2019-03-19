package basic

import basic.enum._
import basic.itc.ItcImpl
import basic.misc._
import cats.effect._
import gem.enum._
import gem.math.Wavelength

object Test extends IOApp {

  val targetProfile =
    TargetProfile(
      spatialProfile       = SpatialProfile.UniformSource,
      spectralDistribution = SpectralDistribution.Library(Right(NonStellarLibrarySpectrum.Neptune)),
      magnitude            = 12,
      magnitudeSystem      = MagnitudeSystem.Vega,
      magnitudeBand        = MagnitudeBand.R,
      redshift             = Redshift(0.0)
    )

  val observingMode =
    ObservingMode.Spectroscopy.GmosNorth(
      λ         = Wavelength.fromNanometers.unsafeGet(755),
      disperser = GmosNorthDisperser.B600_G5307,
      fpu       = GmosNorthFpu.LongSlit_0_25,
      filter    = Some(GmosNorthFilter.OG515)
    )

  def run(args: List[String]): IO[ExitCode] =
    ItcImpl.forHeroku[IO].use { itc =>
      for {
        r <- itc.calculate(targetProfile, observingMode, 1000)
        _ <- IO(println(s"Result is $r"))
      } yield ExitCode.Success
    }

}
