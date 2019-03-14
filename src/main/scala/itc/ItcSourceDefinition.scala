// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.itc.client

import basic.enum.{ MagnitudeSystem, SurfaceBrightness }
import basic.misc.{ SpatialProfile, Redshift, SpectralDistribution }
import gem.enum.MagnitudeBand

final case class ItcSourceDefinition(
  profile:      SpatialProfile,
  distribution: SpectralDistribution,
  norm:         Double,
  units:        Either[MagnitudeSystem, SurfaceBrightness],
  normBand:     MagnitudeBand,
  redshift:     Redshift
)
