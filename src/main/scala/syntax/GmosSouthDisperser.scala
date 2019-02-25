// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.syntax

import gem.enum.GmosSouthDisperser
import gem.enum.GmosSouthDisperser._

final class GmosSouthDisperserOps(val self: GmosSouthDisperser) extends AnyVal {

  /**
   * Dispersion (pm/pixel) with Hamamatsu detectors.
   * @see http://www.gemini.edu/sciops/instruments/gmos/spectroscopy-overview/gratings
   */
  def dispersion: Int =
    self match {
      case B1200_G5321 =>  26
      case R831_G5322  =>  38
      case B600_G5323  =>  50
      case R600_G5324  =>  52
      case R400_G5325  =>  74
      case R150_G5326  => 193
    }

}

trait ToGmosSouthDisperserOps {
  implicit def toGmosSouthDisperserOps(self: GmosSouthDisperser): GmosSouthDisperserOps =
    new GmosSouthDisperserOps(self)
}

object gmossouthdisperser extends ToGmosSouthDisperserOps
