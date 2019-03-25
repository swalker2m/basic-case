// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic
package graphql.schema

import gem.enum._
import sangria.schema._

object MagnitudeBandType {

  val enumType: EnumType[MagnitudeBand] =
    EnumType(
      name       = "MagnitudeBand",
      description = Some("Identifier for a magnitude band."),
      values      = MagnitudeBand.all.map { s =>
        EnumValue(
          name        = s.tag.toUpperCase,
          description = None,
          value       = s
        )
      }
    )

}