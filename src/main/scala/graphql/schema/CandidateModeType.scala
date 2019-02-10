// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.graphql.schema

import basic.ObservingMode
import basic.search.Constraints
import cats.effect._
import sangria.schema._

object CandidateModeType {

  def apply[F[_]: Effect]: UnionType[Unit] =
    UnionType(
      name        = "CandidateModeType",
      description = Some("Candidate observing mode."),
      types       = List(spectroscopy[F])
    )

  def spectroscopy[F[_]: Effect]: ObjectType[Unit, (Constraints.Spectroscopy, ObservingMode.Spectroscopy)] =
    ObjectType(
      name  = "SpectroscopyObservingMode",
      fields = fields(

        Field(
          name        = "instrument",
          fieldType   = StringType,
          resolve     = _.value._2.instrument.tag
        ),

        Field(
          name        = "disperser",
          fieldType   = StringType,
          resolve     = _.value._2.disperser.toString // TODO
        ),

        Field(
          name        = "fpu",
          description = Some("Focal plane unit (mask, slit, IFU)."),
          fieldType   = StringType,
          resolve     = _.value._2.fpu.toString // TODO
        ),

        Field(
          name        = "simultaneousCoverage",
          description = Some("Simultaneous wavelength coverage in nanometers."),
          fieldType   = IntType,
          resolve     = _.value._2.simultaneousCoverage
        ),

        Field(
          name        = "resolution",
          description = Some("Resolution at the observing wavelength."),
          fieldType   = IntType,
          resolve     = c => c.value._2.resolution(c.value._1.λ)
        ),

      )
    )

}