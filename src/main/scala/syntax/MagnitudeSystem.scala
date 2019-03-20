// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.syntax

import gem.enum.MagnitudeSystem

final class MagnitudeSystemOps(val self: MagnitudeSystem) extends AnyVal {
  import MagnitudeSystem._

  def ocs2Tag: String =
    self match {
      case Vega => "Vega"
      case AB   => "AB"
      case Jy   => "Jy"
    }

}

trait ToMagnitudeSystemOps {
  implicit def toMagnitudeSystemOps(self: MagnitudeSystem): MagnitudeSystemOps =
    new MagnitudeSystemOps(self)
}

object magnitudesystem extends ToMagnitudeSystemOps
