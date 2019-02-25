// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.syntax

import java.time.Duration

final class DurationOps(val self: Duration) {

  def *(scalar: Int): Duration =
    self.multipliedBy(scalar.toLong)

}

trait ToDurationOps {
  implicit def toDurationOps(self: Duration): DurationOps =
    new DurationOps(self)
}

object duration extends ToDurationOps
