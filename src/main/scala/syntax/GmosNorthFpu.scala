// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.syntax

import gem.enum.GmosNorthFpu
import gem.enum.GmosNorthFpu._

/**
 * Syntax extensions for missing properties. These need to be folded back into the Gem enumerations.
 */
final class GmosNorthFpuOps(val self: GmosNorthFpu) extends AnyVal {

  def effectiveSlitWidth: Double =
    self match {
      case Ifu1          => 0.31
      case Ifu2          => 0.31
      case Ifu3          => 0.31
      case Ns0           => 0.25
      case Ns1           => 0.50
      case Ns2           => 0.75
      case Ns3           => 1.00
      case Ns4           => 1.50
      case Ns5           => 2.00
      case LongSlit_0_25 => 0.25
      case LongSlit_0_50 => 0.50
      case LongSlit_0_75 => 0.75
      case LongSlit_1_00 => 1.00
      case LongSlit_1_50 => 1.50
      case LongSlit_2_00 => 2.00
      case LongSlit_5_00 => 5.00
    }

  def isNodAndShuffle: Boolean =
    self match {
      case Ifu1          => false
      case Ifu2          => false
      case Ifu3          => false
      case Ns0           => true
      case Ns1           => true
      case Ns2           => true
      case Ns3           => true
      case Ns4           => true
      case Ns5           => true
      case LongSlit_0_25 => false
      case LongSlit_0_50 => false
      case LongSlit_0_75 => false
      case LongSlit_1_00 => false
      case LongSlit_1_50 => false
      case LongSlit_2_00 => false
      case LongSlit_5_00 => false
    }

}

trait ToGmosNorthFpuOps {
  implicit def toGmosNorthFpuOps(self: GmosNorthFpu): GmosNorthFpuOps =
    new GmosNorthFpuOps(self)
}

object gmosnorthfpu extends ToGmosNorthFpuOps