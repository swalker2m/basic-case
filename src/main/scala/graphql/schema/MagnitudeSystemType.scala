// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic
package graphql.schema

import gem.enum._
import sangria.schema._

object MagnitudeSystemType {

  def enumType: EnumType[MagnitudeSystem] =
    EnumType(
      name       = "MagnitudeSystem",
      description = Some("Identifier for a magnitude system."),
      values      = MagnitudeSystem.all.map { s =>
        EnumValue(
          name        = s.tag.toUpperCase,
          description = None,
          value       = s
        )
      }
    )

}