// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic

import basic.syntax.all._
import gem.enum._
import gem.math.Wavelength

sealed trait ObservingMode

object ObservingMode {

  sealed trait Spectroscopy extends ObservingMode {
    def instrument:                Instrument
    def disperser:                 Any // todo
    def fpu:                       Any // todo
    def resolution(λ: Wavelength): Int
    def simultaneousCoverage:      Int
  }

  object Spectroscopy {

    final case class GmosNorth(disperser: GmosNorthDisperser, fpu: GmosNorthFpu) extends Spectroscopy {
      val instrument                = Instrument.GmosN
      def resolution(λ: Wavelength) = disperser.resolution(λ, fpu.effectiveSlitWidth)
      def simultaneousCoverage      = disperser.simultaneousCoverage
    }

  }

}