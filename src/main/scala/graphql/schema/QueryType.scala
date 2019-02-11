// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.graphql.schema

import basic.search._
import cats.effect._
import gem.math.Wavelength
import sangria.schema._

object QueryType {

  val Wavelength: Argument[Wavelength] =
    Argument(
      name         = "wavelength",
      description  = "Observing wavelength in nanometers.",
      argumentType = WavelengthType.scalar,
    )

  val SimultaneousCoverage: Argument[Int] =
    Argument(
      name         = "simultaneousCoverage",
      description  = "Minimum desired simultaneous wavelength coverage in nanometers.",
      argumentType = OptionInputType(IntType),
      defaultValue = 200,
    )

  val Resolution: Argument[Int] =
    Argument(
      name         = "resolution",
      description  = "Minimum desired resolution.",
      argumentType = OptionInputType(IntType),
      defaultValue = 1000,
    )

  def apply[F[_]: Effect]: ObjectType[Unit, Unit] =
    ObjectType(
      name  = "Query",
      fields = fields(

        Field(
          name        = "spectroscopy",
          fieldType   = SearchResultType.spectroscopy[F],
          description = None,
          arguments   = List(Wavelength, SimultaneousCoverage, Resolution),
          resolve     = c =>
            Search.spectroscopy(Constraints.Spectroscopy(
              c.arg(Wavelength),
              c.arg(SimultaneousCoverage),
              c.arg(Resolution)
            ))
        ),

      )
    )

}
