// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.syntax

import gem.math.Angle
import java.time.Duration

/**
 * Integer syntax.  This may be a bit sketchy?
 */
final class IntOps(val self: Int) extends AnyVal {

  def seconds: Duration =
    Duration.ofSeconds(self.toLong)

  def arcsec: Angle =
    Angle.signedArcseconds.reverseGet(self)

}

trait ToIntOps {
  implicit def toIntOps(self: Int): IntOps =
    new IntOps(self)
}

object integer extends ToIntOps
