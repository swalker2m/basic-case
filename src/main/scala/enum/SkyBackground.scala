// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.enum

sealed abstract class SkyBackground(
  val tag:           String,
  val name:          String,
  val percentage:    Int,
  val maxBrightness: Double,
  val magAdjustment: Double,
  val ocs2tag:       String
 ) extends Product with Serializable

object SkyBackground {

  case object Percent20  extends SkyBackground("Percent20",  "20%/Darkest", 20, 21.37,  0.0, "PERCENT_20")
  case object Percent50  extends SkyBackground("Percent50",  "50%/Dark",    50, 20.78,  0.0, "PERCENT_50")
  case object Percent80  extends SkyBackground("Percent80",  "80%/Grey",    80, 19.61, -0.3, "PERCENT_80")
  case object PercentAny extends SkyBackground("PercentAny", "Any/Bright", 100,  0.00, -0.5, "ANY")

}

