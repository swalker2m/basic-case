// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.itc

import basic.enum._
import io.circe.Encoder

final case class ItcObservingConditions(
  iq:      ImageQuality,
  cc:      CloudCover,
  wv:      WaterVapor,
  sb:      SkyBackground,
  airmass: Double
)

object ItcObservingConditions {

  implicit val encoder: Encoder[ItcObservingConditions] =
    Encoder.forProduct5("iq", "cc", "wv", "sb", "airmass") { a =>
      (a.iq.ocs2Tag, a.cc.ocs2Tag, a.wv.ocs2Tag, a.sb.ocs2Tag, a.airmass)
    }

}