// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.syntax

import scala.Numeric.Implicits._

final class NumericOps[A: Numeric](self: A) {

  def squared: A = self * self

}

trait ToNumericOps {
  implicit def toNumericOps[A: Numeric](self: A): NumericOps[A] =
    new NumericOps(self)
}

object numeric extends ToNumericOps
