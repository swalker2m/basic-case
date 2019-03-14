// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.enum

sealed abstract class SurfaceBrightness(
  val tag:       String,
  val shortName: String,
  val ocs2Tag:   String
) extends Product with Serializable

object SurfaceBrightness {

  case object Vega           extends SurfaceBrightness("Vega", "Vega mag/arcsec²", "Vega")
  case object AB             extends SurfaceBrightness("AB", "AB mag/arcsec²", "AB")
  case object Jy             extends SurfaceBrightness("Jy", "Jy/arcsec²", "Jy")
  case object Watts          extends SurfaceBrightness("Watts", "W/m²/µm/arcsec²", "Watts")
  case object ErgsWavelength extends SurfaceBrightness("ErgsWavelength", "erg/s/cm²/Å/arcsec²", "ErgsWavelength")
  case object ErgsFrequency  extends SurfaceBrightness("ErgsFrequency", "erg/s/cm²/Hz/arcsec²", "ErgsFrequency")

}


