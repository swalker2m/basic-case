// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.enum

sealed abstract class CloudCover(
  val tag:           String,
  val name:          String,
  val percentage:    Int,
  val magAdjustment: Double,
  val ocs2tag:       String
) extends Product with Serializable

object CloudCover {

  case object Percent50  extends CloudCover("Percent50",  "50%/Clear",  50,  0.0, "PERCENT_50")
  case object Percent70  extends CloudCover("Percent70",  "70%/Cirrus", 70, -0.3, "PERCENT_70")
  case object Percent80  extends CloudCover("Percent80",  "80%/Cloudy", 80, -1.0, "PERCENT_80")
  case object PercentAny extends CloudCover("PercentAny", "Any",       100, -3.0, "ANY")

}