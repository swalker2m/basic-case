// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.graphql.schema

import basic.search.Constraints
import cats.effect._
import sangria.schema._

object ConstraintsType {

  def apply[F[_]: Effect]: UnionType[Unit] =
    UnionType(
      name        = "Constraints",
      description = Some("Observing constraints."),
      types       = List(spectroscopy[F])
    )

  def spectroscopy[F[_]: Effect]: ObjectType[Unit, Constraints.Spectroscopy] =
    ObjectType(
      name  = "SpectroscopyConstraints",
      fields = fields(

        Field(
          name        = "wavelength",
          fieldType   = WavelengthType.scalar,
          description = Some("Observing wavelength in nanometers."),
          resolve     = _.value.λ
        ),

        Field(
          name        = "simultaneousCoverage",
          fieldType   = WavelengthType.scalar,
          description = Some("Minimum desired simultaneous wavelength coverage in nanometers."),
          resolve     = _.value.simultaneousCoverage
        ),

        Field(
          name        = "resolution",
          fieldType   = IntType,
          description = Some("Minimum desired resolution."),
          resolve     = _.value.resolution
        ),

      )
    )

}