package basic

import basic.misc._
import gem.enum._

/** Target properties we need to know at phase zero. */
final case class TargetProfile(
  spatialProfile:       SpatialProfile,
  spectralDistribution: SpectralDistribution,
  magnitude:            Double,
  magnitudeSystem:      MagnitudeSystem,
  magnitudeBand:        MagnitudeBand,
  redshift:             Redshift
)


