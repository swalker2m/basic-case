// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic
package graphql.schema

import basic.misc._
import basic.search._
import cats.effect._
import cats.effect.implicits._
import cats.temp.par._
import gem.enum._
import gem.math.Wavelength
import sangria.schema._

object QueryType {
  import RedshiftType.implicits._
  import SpatialProfileType.implicits._
  import SpectralDistributionType.implicits._

  val Wavelength: Argument[Wavelength] =
    Argument(
      name         = "wavelength",
      description  = "Observing wavelength in nanometers.",
      argumentType = WavelengthType.scalar,
    )

  val SimultaneousCoverage: Argument[Wavelength] =
    Argument(
      name         = "simultaneousCoverage",
      description  = "Minimum desired simultaneous wavelength coverage in nanometers.",
      argumentType = OptionInputType(WavelengthType.scalar),
      defaultValue = 200,
    )

  val Resolution: Argument[Int] =
    Argument(
      name         = "resolution",
      description  = "Minimum desired resolution.",
      argumentType = OptionInputType(IntType),
      defaultValue = 1000,
    )

  val SpatialProfileArg: Argument[SpatialProfile] =
    Argument(
      name         = "spatialProfile",
      argumentType = SpatialProfileType.inputType,
      defaultValue = SpatialProfile.PointSource : SpatialProfile
    )

  val SpectralDistributionArg: Argument[SpectralDistribution] =
    Argument(
      name         = "spectralDistribution",
      argumentType = SpectralDistributionType.inputType
    )

  val MagnitudeArg: Argument[Double] =
    Argument(
      name         = "magnitude",
      argumentType = FloatType
    )

  val MagnitudeSystemArg: Argument[MagnitudeSystem] =
    Argument(
      name         = "magnitudeSystem",
      argumentType = MagnitudeSystemType.enumType,
    )

  val MagnitudeBandArg: Argument[MagnitudeBand] =
    Argument(
      name         = "magnitudeBand",
      argumentType = MagnitudeBandType.enumType,
    )

  val RedshiftArg: Argument[Redshift] =
    Argument(
      name         = "redshift",
      argumentType = RedshiftType.scalar,
      defaultValue = Redshift(0)
    )

  val SignalToNoiseArg: Argument[Int] =
    Argument(
      name         = "signalToNoise",
      description  = "Minimum desired signal-to-noise ratio.",
      argumentType = IntType,
    )

  def apply[F[_]: Par: Effect: Itc]: ObjectType[Unit, Unit] =
    ObjectType(
      name  = "Query",
      fields = fields(
        Field(
          name        = "spectroscopy",
          fieldType   = ListType(SearchResultType()),
          description = None,
          arguments   = List(
            Wavelength,
            SimultaneousCoverage,
            Resolution,
            SignalToNoiseArg,
            SpatialProfileArg,
            SpectralDistributionArg,
            MagnitudeArg,
            MagnitudeSystemArg,
            MagnitudeBandArg,
            RedshiftArg
          ),
          resolve     = c =>
            Search.spectroscopy(
              Constraints.Spectroscopy(
                c.arg(Wavelength),
                c.arg(SimultaneousCoverage),
                c.arg(Resolution)
              ),
              TargetProfile(
                c.arg(SpatialProfileArg),
                c.arg(SpectralDistributionArg),
                c.arg(MagnitudeArg),
                c.arg(MagnitudeSystemArg),
                c.arg(MagnitudeBandArg),
                c.arg(RedshiftArg)
              ),
              c.arg(SignalToNoiseArg)
            ).toIO.unsafeToFuture
        ),

      )
    )

}
