// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic

import basic.syntax.all._
import basic.misc._
import gem.enum._
import gem.math.Wavelength

sealed trait ObservingMode {
  def instrument: Instrument
}

object ObservingMode {

  sealed trait Spectroscopy extends ObservingMode {
    def λ:          Wavelength
    def resolution: Int
    def coverage:   Coverage
  }

  object Spectroscopy {

    final case class GmosNorth(
      λ:         Wavelength,
      disperser: GmosNorthDisperser,
      fpu:       GmosNorthFpu,
      filter:    Option[GmosNorthFilter]
    ) extends Spectroscopy {

      val instrument: Instrument =
        Instrument.GmosN

      def resolution: Int =
        disperser.resolution(λ, fpu.effectiveSlitWidth)

      def coverage: Coverage =
        filter.foldLeft(disperser.coverage(λ))(_ ⋂ _.coverage)

    }

  }

}