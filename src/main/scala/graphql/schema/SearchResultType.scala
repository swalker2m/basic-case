// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.graphql.schema

import basic.search.SearchResult
import cats.effect._
import cats.implicits._
import sangria.schema._

object SearchResultType {

  def apply[F[_]: Effect]: UnionType[Unit] =
    UnionType(
      name        = "SearchResult",
      description = Some("Observing mode search result."),
      types       = List(spectroscopy[F])
    )

  def spectroscopy[F[_]: Effect]: ObjectType[Unit, SearchResult.Spectroscopy] =
    ObjectType(
      name  = "SpectroscopySearchResult",
      fields = fields(

        Field(
          name        = "constraints",
          fieldType   = ConstraintsType.spectroscopy[F],
          description = Some("Observing constraints."),
          resolve     = _.value.constraints
        ),

        Field(
          name        = "candidateModes",
          fieldType   = ListType(CandidateModeType.spectroscopy[F]),
          description = Some("Candidate observing modes, compatible with associated constraints."),
          resolve     = c => c.value.observingModes.tupleLeft(c.value.constraints)
        ),

      )
    )

}
