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