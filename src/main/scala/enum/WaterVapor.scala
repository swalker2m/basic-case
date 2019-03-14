// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.enum

sealed abstract class WaterVapor(
  val tag:        String,
  val name:       String,
  val percentage: Int,
  val ocs2Tag:    String
) extends Product with Serializable


object WaterVapor {

  case object Percent20  extends WaterVapor("Percent20",  "20%/Low",    20, "PERCENT_20")
  case object Percent50  extends WaterVapor("Percent50",  "50%/Median", 50, "PERCENT_50")
  case object Percent80  extends WaterVapor("Percent80",  "80%/High",   80, "PERCENT_80")
  case object PercentAny extends WaterVapor("PercentAny", "Any",       100, "ANY")

}