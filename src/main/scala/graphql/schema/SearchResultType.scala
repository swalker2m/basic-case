// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.graphql.schema

import basic.search.Search
import sangria.schema._

object SearchResultType {

  def apply(): UnionType[Unit] =
    UnionType(
      name        = "SearchResult",
      description = None,
      types       = List(spectroscopy)
    )

  def spectroscopy: ObjectType[Unit, Search.Result.Spectroscopy] =
    ObjectType(
      name  = "SearchResultSpectroscopy",
      fields = fields(

        Field(
          name        = "observingMode",
          fieldType   = ObservingModeType.SpectroscopyType,
          resolve     = _.value.mode
        ),

        Field(
          name        = "itcResults",
          fieldType   = ItcResultType(),
          resolve     = _.value.itc
        ),

      )
    )

}