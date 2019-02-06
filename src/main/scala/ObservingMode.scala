package basic

import gem.enum._
import gem.math._

sealed trait ObservingMode

sealed trait SpectroscopyMode extends ObservingMode {
  def fieldOfView:        Offset     // x, y .. lame?
  def resolution:         Double     // dimensionless
  def wavelengthCoverage: Wavelength // dλ
}

object SpectroscopyMode {

  final case class GmosNorth(disperser: GmosNorthDisperser) extends SpectroscopyMode {
    def fieldOfView = ???
    def resolution = ???
    def wavelengthCoverage = ???
  }

  final case class GmosSouth(disperser: GmosSouthDisperser) extends SpectroscopyMode {
    def fieldOfView = ???
    def resolution = ???
    def wavelengthCoverage = ???
  }

}
