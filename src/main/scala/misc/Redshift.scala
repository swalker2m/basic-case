// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.misc

import gem.math.PhysicalConstants.{ SpeedOfLight => SpeedOfLightMSec }
import scala.math.sqrt

case class Redshift(z: Double) extends Serializable {

  def toRadialVelocity: Double =
    Redshift.SpeedOfLightKmSec * ((z + 1) * (z + 1) - 1) / ((z + 1) * (z + 1) + 1)

  def toApparentRadialVelocity: Double =
    Redshift.SpeedOfLightKmSec * z

}

object Redshift {

  val SpeedOfLightKmSec = SpeedOfLightMSec.toDouble / 1000.0

  // in km/sec
  def fromRadialVelocity(v: Double): Redshift =
    Redshift(sqrt((1 + v / SpeedOfLightKmSec) / (1 - v / SpeedOfLightKmSec)) - 1)

  // in km/sec
  def fromApparentRadialVelocity(v: Double): Redshift =
    Redshift(v / SpeedOfLightKmSec)

  val Zero: Redshift =
    Redshift(0)

}
