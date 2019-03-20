// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.enum

sealed abstract class ImageQuality(
  val tag:           String,
  val name:          String,
  val percentage:    Int,
  val magAdjustment: Double,
  val ocs2Tag:       String
) extends Product with Serializable

object ImageQuality {

  case object Percent20  extends ImageQuality("Percent20",  "20%/Best",  20,  0.5, "PERCENT_20")
  case object Percent70  extends ImageQuality("Percent70",  "70%/Good",  70,  0.0, "PERCENT_70")
  case object Percent85  extends ImageQuality("Percent85",  "85%/Poor",  85, -0.5, "PERCENT_85")
  case object PercentAny extends ImageQuality("PercentAny", "Any",      100, -1.0, "ANY")

}
