// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.syntax

import gem.enum.MagnitudeBand

final class MagnitudeBandOps(val self: MagnitudeBand) extends AnyVal {
  import MagnitudeBand._

  def ocs2Tag: String =
    self match {
      case SloanU => "_u"
      case SloanG => "_g"
      case SloanR => "_r"
      case SloanI => "_i"
      case SloanZ => "_z"
      case U      => "U"
      case B      => "B"
      case V      => "V"
      case Uc     => "UC"
      case R      => "R"
      case I      => "I"
      case Y      => "Y"
      case J      => "J"
      case H      => "H"
      case K      => "K"
      case L      => "L"
      case M      => "M"
      case N      => "N"
      case Q      => "Q"
      case Ap     => sys.error("Apparent magnitude has no OCS2 tag.")
    }

}

trait ToMagnitudeBandOps {
  implicit def toMagnitudeBandOps(self: MagnitudeBand): MagnitudeBandOps =
    new MagnitudeBandOps(self)
}

object magnitudeband extends ToMagnitudeBandOps
