// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.graphql.schema

import basic.ObservingMode
import sangria.schema._

object ObservingModeType {

  lazy val SpectroscopyType: UnionType[Unit] =
    UnionType(
      name        = "Spectroscopy",
      description = Some("Spectrographic observing mode."),
      types       = List(GmosNorthSpectroscopyType)
    )

  lazy val GmosNorthSpectroscopyType: ObjectType[Unit, ObservingMode.Spectroscopy.GmosNorth] =
    ObjectType(
      name  = "GmosNorthSpectroscopy",
      fields = fields(

        Field(
          name        = "instrument",
          fieldType   = StringType,
          resolve     = _.value.instrument.longName
        ),

        Field(
          name        = "disperser",
          fieldType   = StringType,
          resolve     = _.value.disperser.longName
        ),

        Field(
          name        = "fpu",
          description = Some("Focal plane unit (mask, slit, IFU)."),
          fieldType   = StringType,
          resolve     = _.value.fpu.longName
        ),

        Field(
          name        = "filter",
          description = Some("Filter."),
          fieldType   = OptionType(StringType),
          resolve     = _.value.filter.map(_.longName) // TODO
        ),

        Field(
          name        = "coverage",
          description = Some("Wavelength coverage."),
          fieldType   = OptionType(CoverageType.range),
          resolve     = _.value.coverage.range
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