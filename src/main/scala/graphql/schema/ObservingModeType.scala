// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.graphql.schema

import basic.ObservingMode
import sangria.schema._

object ObservingModeType {

  object SpectroscopyType {

    def apply[F[_]]: UnionType[Unit] =
      UnionType(
        name        = "Spectroscopy",
        description = Some("Spectrographic observing mode."),
        types       = List(GmosNorthType[F])
      )

    object GmosNorthType {

      def apply[F[_]]: ObjectType[Unit, ObservingMode.Spectroscopy.GmosNorth] =
        ObjectType(
          name  = "GmosNorthSpectroscopy",
          fields = fields(

            Field(
              name        = "instrument",
              fieldType   = StringType,
              resolve     = _.value.instrument.tag
            ),

            Field(
              name        = "disperser",
              fieldType   = StringType,
              resolve     = _.value.disperser.tag
            ),

            Field(
              name        = "fpu",
              description = Some("Focal plane unit (mask, slit, IFU)."),
              fieldType   = StringType,
              resolve     = _.value.fpu.tag
            ),

            Field(
              name        = "filter",
              description = Some("Filter."),
              fieldType   = OptionType(StringType),
              resolve     = _.value.filter.map(_.tag) // TODO
            ),

            Field(
              name        = "simultaneousCoverage",
              description = Some("Simultaneous wavelength coverage."),
              fieldType   = WavelengthType.scalar,
              resolve     = _.value.simultaneousCoverage
            ),

            Field(
              name        = "resolution",
              description = Some("Resolution at the observing wavelength."),
              fieldType   = IntType,
              resolve     = _.value.resolution
            ),

          )
        )
    }

  }

}