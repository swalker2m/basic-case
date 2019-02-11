// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.syntax

import gem.math.Wavelength
import gem.optics.Format
import gem.syntax.prism._

/** Syntax extensions for wavelenth. */
final class WavelengthCompanionOps(val self: Wavelength.type) {

  val fromNanometers: Format[Int, Wavelength] =
    Wavelength.fromAngstroms.asFormat.imapA(_ / 10, _ * 10)

}

trait ToWavelengthCompanionOps {
  implicit def toWavelengthOps(self: Wavelength.type): WavelengthCompanionOps =
    new WavelengthCompanionOps(self)
}

object wavelength extends ToWavelengthCompanionOps