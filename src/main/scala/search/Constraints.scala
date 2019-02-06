// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic
package search

import gem.enum._
import gem.math.{ Angle, Wavelength }

sealed trait Constraints {
  def search: List[ObservingMode]
}
object Constraints {

  final case class Spectroscopy(
    wavelength:         Wavelength,
    wavelengthCoverage: Wavelength     = Wavelength.fromAngstroms.getOption(2000).get, // TODO: add fromNanometers
    resolution:         Int            = 3000, // todo: resolution type
    targetMagnitude:    Int            = 18,   // todo: data type .. MagnitudeValue may just need a public ctor
    spatialProfile:     SpatialProfile = SpatialProfile.PointSource,
    minimumFieldOfView: Angle          = Angle.arcseconds.reverseGet(5),
    maximumIQ:          Angle          = Angle.arcseconds.reverseGet(1)
  ) extends Constraints {

    def search: List[ObservingMode] = {

      // It seems like the only things that matter here are the wavelength coverage (determined by
      // the grating's ruling density and the FPU's field of view) and the mimimum FoV.

      val gmosSouth: List[ObservingMode] =
        for {
          fpu <- GmosSouthFpu.all
          dis <- GmosSouthDisperser.all
        } yield ObservingMode.Spectroscopy.GmosSouth(dis, fpu)

      val gmosNorth: List[ObservingMode] =
        for {
          fpu <- GmosNorthFpu.all
          dis <- GmosNorthDisperser.all
        } yield ObservingMode.Spectroscopy.GmosNorth(dis, fpu)

      // Todo: filter these

      gmosSouth ++ gmosNorth

    }

  }

}