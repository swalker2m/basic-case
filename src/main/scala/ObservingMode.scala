// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic

import basic.syntax.all._
import gem.enum._

sealed trait ObservingMode

object ObservingMode {

  sealed trait Spectroscopy extends ObservingMode {
    def instrument:           Instrument
    def disperser:            Any // todo
    def fpu:                  Any // todo
    def resolution(λ: Int):   Int
    def simultaneousCoverage: Int
  }

  object Spectroscopy {

    final case class GmosNorth(disperser: GmosNorthDisperser, fpu: GmosNorthFpu) extends Spectroscopy {
      val instrument                = Instrument.GmosN
      def resolution(λ: Int): Int   = disperser.resolution(λ, fpu.effectiveSlitWidth)
      def simultaneousCoverage: Int = disperser.simultaneousCoverage
    }

  }

}