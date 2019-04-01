// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic

import basic.enum._

import cats._

/**
 * Observing conditions.
 */
final case class ObservingConditions(
  cc: CloudCover,
  iq: ImageQuality,
  sb: SkyBackground,
  wv: WaterVapor
)

object ObservingConditions {

  implicit val EqObservingConditions: Eq[ObservingConditions] =
    Eq.instance { (a, b) =>
      // TODO: when we have Enumerated instances for these, we can use ===
      (a.cc == b.cc) && (a.iq == b.iq) && (a.sb == b.sb) && (a.wv == b.wv)
    }
}