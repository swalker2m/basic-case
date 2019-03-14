// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.itc.client

import basic.enum._

final case class ItcObservingConditions(
  iq:      ImageQuality,
  cc:      CloudCover,
  wv:      WaterVapor,
  sb:      SkyBackground,
  airmass: Double
)