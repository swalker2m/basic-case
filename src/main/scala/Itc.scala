// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic

import gem.math.MagnitudeValue
import scala.concurrent.duration.FiniteDuration

trait Itc[F[_]] {

  def integrationTime(
    mode:      ObservingMode,
    magnitude: MagnitudeValue,
    // what else do we need?
  ): F[FiniteDuration]

}