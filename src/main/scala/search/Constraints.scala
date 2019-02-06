package basic.search

import gem.math.{ Angle, Wavelength }

sealed trait Constraints
object Constraints {

  final case class Spectroscopy(
    wavelength:         Wavelength,
    wavelengthCoverage: Wavelength     = Wavelength.fromAngstroms.getOption(2000).get, // TODO: add fromNanometers
    resolution:         Int            = 3000, // todo: resolution type
    targetMagnitude:    Int            = 18,   // todo: data type .. MagnitudeValue may just need a public ctor
    spatialProfile:     SpatialProfile = SpatialProfile.PointSource,
    minimumFieldOfView: Angle          = Angle.arcseconds.reverseGet(5),
    maximumIQ:          Angle          = Angle.arcseconds.reverseGet(1)
  ) {

    // would be nice to have a wavelength range as a data type, which we may be able to get for
    // free(ish) from spire

  }

}