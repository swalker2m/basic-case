// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic
package itc

import basic.enum.SurfaceBrightness
import basic.misc.{ SpatialProfile, Redshift, SpectralDistribution }
import basic.syntax.all._
import gem.enum._
import io.circe.{ Encoder, Json }
import io.circe.generic.semiauto._

final case class ItcSourceDefinition(
  profile:      SpatialProfile,
  distribution: SpectralDistribution,
  norm:         Double,
  units:        Either[MagnitudeSystem, SurfaceBrightness],
  normBand:     MagnitudeBand,
  redshift:     Redshift
)

object ItcSourceDefinition {

  def fromTargetProfile(p: TargetProfile): ItcSourceDefinition =
    ItcSourceDefinition(
      p.spatialProfile,
      p.spectralDistribution,
      p.magnitude,
      p.spatialProfile match {
        case SpatialProfile.GaussianSource(_) => Left(p.magnitudeSystem)
        case SpatialProfile.PointSource       => Left(p.magnitudeSystem)
        case SpatialProfile.UniformSource     =>
          Right {
            p.magnitudeSystem match {
              case MagnitudeSystem.Vega => SurfaceBrightness.Vega
              case MagnitudeSystem.AB   => SurfaceBrightness.AB
              case MagnitudeSystem.Jy   => SurfaceBrightness.Jy
            }
          }
      },
      p.magnitudeBand,
      p.redshift
    )

  private implicit val spatialProfileEncoder: Encoder[SpatialProfile] =
    new Encoder[SpatialProfile] {
      import SpatialProfile._
      def apply(a: SpatialProfile): Json =
        a match {
          case PointSource           => Json.obj("PointSource"    -> Json.obj())
          case UniformSource         => Json.obj("UniformSource"  -> Json.obj())
          case g @ GaussianSource(_) =>
            Json.obj(
              "GaussianSource" -> Json.obj("fwhm" -> Json.fromDoubleOrNull(GaussianSource.arcsec.get(g).toDouble))
            )
        }
    }

  private implicit val spectralDistributionEncoder: Encoder[SpectralDistribution] =
    new Encoder[SpectralDistribution] {
      import SpectralDistribution._
      def apply(a: SpectralDistribution): Json =
        a match {
          case BlackBody(t)       => Json.obj("BlackBody" -> Json.obj("temperature"    -> Json.fromDoubleOrNull(t)))
          case PowerLaw(i)        => Json.obj("PowerLaw"  -> Json.obj("index"          -> Json.fromDoubleOrNull(i)))
          case Library(Left(s))   => Json.obj("Library"   -> Json.obj("LibraryStar"    -> Json.fromString(s.ocs2Tag)))
          case Library(Right(ns)) => Json.obj("Library"   -> Json.obj("LibraryNonStar" -> Json.fromString(ns.ocs2Tag)))
        }
    }

  private implicit val unitEncoder: Encoder[Either[MagnitudeSystem, SurfaceBrightness]] =
    new Encoder[Either[MagnitudeSystem, SurfaceBrightness]] {
      def apply(a: Either[MagnitudeSystem, SurfaceBrightness]): Json =
        a match {
          case Left(ms)  => Json.obj("MagnitudeSystem"   -> Json.fromString(ms.ocs2Tag))
          case Right(sb) => Json.obj("SurfaceBrightness" -> Json.fromString(sb.ocs2Tag))
        }
    }

  private implicit val magnitudeBandEncoder: Encoder[MagnitudeBand] =
    Encoder[String].contramap(_.ocs2Tag)

  private implicit val redshiftEncoder: Encoder[Redshift] =
    Encoder.forProduct1("z")(_.z)

  implicit val encoder: Encoder[ItcSourceDefinition] =
    deriveEncoder

}