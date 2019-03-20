// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

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


