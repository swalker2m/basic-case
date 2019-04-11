// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.graphql.schema

import basic.misc.Coverage
import sangria.schema._

object CoverageType {

  val range: ObjectType[Unit, Coverage.Range] =
    ObjectType(
      name        = "CoverageRange",
      description = "Coverage over a specified range of wavelengths.",
      fields = fields(

        Field(
          name        = "min",
          fieldType   = WavelengthType.scalar,
          description = Some("Minimum wavelength."),
          resolve     = _.value.min
        ),

        Field(
          name        = "max",
          fieldType   = WavelengthType.scalar,
          description = Some("Maximum wavelength."),
          resolve     = _.value.max
        ),

        Field(
          name        = "width",
          fieldType   = WavelengthType.scalar,
          description = Some("Simultaneous coverage."),
          resolve     = _.value.width
        ),

        )
    )


}