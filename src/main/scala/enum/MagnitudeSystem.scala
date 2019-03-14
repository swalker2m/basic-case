// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.enum

sealed abstract class MagnitudeSystem(
  val tag:       String,
  val shortName: String
) extends Product with Serializable

object MagnitudeSystem {

  case object Vega           extends MagnitudeSystem("Vega", "Vega")
  case object AB             extends MagnitudeSystem("AB", "AB")
  case object Jy             extends MagnitudeSystem("Jy", "Jy")
  case object Watts          extends MagnitudeSystem("Watts", "W/m²/µm")
  case object ErgsWavelength extends MagnitudeSystem("ErgsWavelength", "erg/s/cm²/Å")
  case object ErgsFrequency  extends MagnitudeSystem("ErgsFrequency", "erg/s/cm²/Hz")

}
