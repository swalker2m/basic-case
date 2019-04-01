// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.syntax

import gem.enum.GmosNorthDisperser
import gem.enum.GmosNorthDisperser._
import gem.math.{ Angle, Wavelength }

/**
 * Syntax extensions for missing properties. These need to be folded back into the Gem enumerations.
 */
final class GmosNorthDisperserOps(val self: GmosNorthDisperser) extends AnyVal {

  /**
   * Reference wavelength (nm) and resolution for 0.5" slit.
   * @see http://www.gemini.edu/sciops/instruments/gmos/spectroscopy-overview/gratings
   */
  private def reference: (Int, Int) =
    self match {
      case B1200_G5301 => (463, 3744)
      case R831_G5302  => (757, 4396)
      case B600_G5303  => (461, 1688) // obsolete
      case B600_G5307  => (461, 1688)
      case R600_G5304  => (926, 3744)
      case R400_G5305  => (764, 1918)
      case R150_G5306  => (717,  631) // obsolete
      case R150_G5308  => (717,  631)
    }

  /**
   * Δλ for 0.5" slit.
   * @see http://hyperphysics.phy-astr.gsu.edu/hbase/phyopt/gratres.html
   */
  private def Δλ: Double = {
    val (λ, r) = reference
     λ.toDouble / r.toDouble // r = λ / Δλ
  }

  /** Resolution at λ with the specified slit width (arcsec). */
  def resolution(λ: Wavelength, slitWidth: Angle): Int =
    ((Wavelength.fromNanometers.reverseGet(λ) / Δλ) * (0.5 / Angle.signedArcseconds.get(slitWidth))).toInt

  /**
   * Simultaneous coverage with Hamamatsu detectors.
   * @see http://www.gemini.edu/sciops/instruments/gmos/spectroscopy-overview/gratings
   */
  def simultaneousCoverage: Wavelength =
    self match {
      case B1200_G5301 => Wavelength.fromNanometers.unsafeGet( 164)
      case R831_G5302  => Wavelength.fromNanometers.unsafeGet( 235)
      case B600_G5303  => Wavelength.fromNanometers.unsafeGet( 276) // obsolete, value with old e2v detector
      case B600_G5307  => Wavelength.fromNanometers.unsafeGet( 317)
      case R600_G5304  => Wavelength.fromNanometers.unsafeGet( 328)
      case R400_G5305  => Wavelength.fromNanometers.unsafeGet( 472)
      case R150_G5306  => Wavelength.fromNanometers.unsafeGet(1071) // obsolete, value with old e2v detector
      case R150_G5308  => Wavelength.fromNanometers.unsafeGet(1219)
    }

  /**
   * Dispersion (pm/pixel) with Hamamatsu detectors.
   * @see http://www.gemini.edu/sciops/instruments/gmos/spectroscopy-overview/gratings
   */
  def dispersion: Int =
    self match {
      case B1200_G5301 =>  26
      case R831_G5302  =>  38
      case B600_G5303  =>  45 // obsolete, value with old e2v detector
      case B600_G5307  =>  50
      case R600_G5304  =>  52
      case R400_G5305  =>  74
      case R150_G5306  => 174 // obsolete, value with old e2v detector
      case R150_G5308  => 193
    }

  // pedantic: tags are the same in OCS2 and OCS3 but this is just a coincidence
  def ocs2Tag: String =
    self match {
      case B1200_G5301 => "B1200_G5301"
      case R831_G5302  => "R831_G5302"
      case B600_G5303  => "B600_G5303"
      case B600_G5307  => "B600_G5307"
      case R600_G5304  => "R600_G5304"
      case R400_G5305  => "R400_G5305"
      case R150_G5306  => "R150_G5306"
      case R150_G5308  => "R150_G5308"
    }

}

trait ToGmosNorthDisperserOps {
  implicit def toGmosNorthDisperserOps(self: GmosNorthDisperser): GmosNorthDisperserOps =
    new GmosNorthDisperserOps(self)
}

object gmosnorthdisperser extends ToGmosNorthDisperserOps