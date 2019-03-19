// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.syntax

import gem.enum.Instrument
import scala.concurrent.duration._

final class InstrumentOps(val self: Instrument) extends AnyVal {

  /** Minimum exposure time allowed by the instrument. Always positive. */
  def minExposureTime: FiniteDuration =
    self match {
      case Instrument.GmosN => 1.second
      case Instrument.GmosS => 1.second
    }

  /** Minimum exposure time allowed by the instrument. Always greater than `minExposureTime`. */
  def maxExposureTime: FiniteDuration =
    self match {
      case Instrument.GmosN => 20.minutes
      case Instrument.GmosS => 20.minutes
    }

  /** True if the instrument requires exposure times in integral seconds. */
  def integralDurations: Boolean =
    self match {
      case Instrument.GmosN => true
      case Instrument.GmosS => true
    }

}

trait ToInstrumentOps {
  implicit def toInstrumentOps(self: Instrument): InstrumentOps =
    new InstrumentOps(self)
}

object instrument extends ToInstrumentOps
