// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.syntax

import gem.math.Angle

final class AngleOps(val self: Angle) extends AnyVal {

  /** Returns this `Angle` in double-precision arcseconds. */
  def toDoubleArcseconds: Double =
    self.toDoubleDegrees * 60 * 60

}

trait ToAngleOps {
  implicit def toAngleOps(self: Angle): AngleOps =
    new AngleOps(self)
}

object angle extends ToAngleOps
