// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.syntax

import gem.enum.GmosNorthDisperser
import gem.enum.GmosNorthDisperser._
import gem.math.Wavelength

/**
 * Syntax extensions for missing properties. These need to be folded back into the Gem enumerations.
 */
final class GmosNorthDisperserOps(val self: GmosNorthDisperser) extends AnyVal {
  import wavelength._

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

  /** Resolution at λ (nm) with the specified slit width (arcsec). */
  def resolution(λ: Wavelength, slitWidth: Double): Int =
    ((Wavelength.fromNanometers.reverseGet(λ) / Δλ) * (0.5 / slitWidth)).toInt

  /**
   * Simultaneous coverage (nm) with Hamamatsu detectors.
   * @see http://www.gemini.edu/sciops/instruments/gmos/spectroscopy-overview/gratings
   */
  def simultaneousCoverage: Int =
    self match {
      case B1200_G5301 =>  164
      case R831_G5302  =>  235
      case B600_G5303  =>  276 // obsolete, value with old e2v detector
      case B600_G5307  =>  317
      case R600_G5304  =>  328
      case R400_G5305  =>  472
      case R150_G5306  => 1071 // obsolete, value with old e2v detector
      case R150_G5308  => 1219
    }

}

trait ToGmosNorthDisperserOps {
  implicit def toGmosNorthDisperserOps(self: GmosNorthDisperser): GmosNorthDisperserOps =
    new GmosNorthDisperserOps(self)
}

object gmosnorthdisperser extends ToGmosNorthDisperserOps