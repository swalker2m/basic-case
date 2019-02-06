// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic

import gem.enum._
import gem.math._

sealed trait ObservingMode

object ObservingMode {

  sealed trait Spectroscopy extends ObservingMode {
    def fieldOfView:        Offset     // x, y .. lame?
    def resolution:         Double     // dimensionless
    def wavelengthCoverage: Wavelength // dλ
  }

  object Spectroscopy {

    final case class GmosNorth(disperser: GmosNorthDisperser, fpu: GmosNorthFpu) extends Spectroscopy {
      def fieldOfView = ???
      def resolution = ???
      def wavelengthCoverage = ???
    }

    final case class GmosSouth(disperser: GmosSouthDisperser, fpu: GmosSouthFpu) extends Spectroscopy {
      def fieldOfView = ???
      def resolution = ???
      def wavelengthCoverage = ???
    }

  }

}