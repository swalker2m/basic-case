// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.syntax

import gem.enum.GmosNorthFilter
import gem.enum.GmosNorthFilter._

import cats.implicits._

final class GmosNorthFilterOps(val self: GmosNorthFilter) extends AnyVal {

  def ocs2Tag: String =
    self match {
      case GPrime           => "g_G0301"
      case RPrime           => "r_G0303"
      case IPrime           => "i_G0302"
      case ZPrime           => "z_G0304"
      case Z                => "Z_G0322"
      case Y                => "Y_G0323"
      case GG455            => "GG455_G0305"
      case OG515            => "OG515_G0306"
      case RG610            => "RG610_G0307"
      case CaT              => "CaT_G0309"
      case Ha               => "Ha_G0310"
      case HaC              => "HaC_G0311"
      case DS920            => "DS920_G0312"
      case SII              => "SII_G0317"
      case OIII             => "OIII_G0318"
      case OIIIC            => "OIIIC_G0319"
      case HeII             => "HeII_G0320"
      case HeIIC            => "HeIIC_G0321"
      case HartmannA_RPrime => "HartmannA_G0313_r_G0303"
      case HartmannB_RPrime => "HartmannB_G0314_r_G0303"
      case GPrime_GG455     => "g_G0301_GG455_G0305"
      case GPrime_OG515     => "g_G0301_OG515_G0306"
      case RPrime_RG610     => "r_G0303_RG610_G0307"
      case IPrime_CaT       => "i_G0302_CaT_G0309"
      case ZPrime_CaT       => "z_G0304_CaT_G0309"
      case UPrime           => "u_G0308"
    }

}

trait ToGmosNorthFilterOps {
  implicit def toGmosNorthFilterOps(self: GmosNorthFilter): GmosNorthFilterOps =
    new GmosNorthFilterOps(self)
}


final class GmosNorthFilterCompanionOps(val self: GmosNorthFilter.type) extends AnyVal {

  /** Filters to use for acquisition, sorted by wavelength. */
  def allAcquisition: List[GmosNorthFilter] =
    List(UPrime, GPrime, RPrime, IPrime, ZPrime).sortBy(_.wavelength)

}

trait ToGmosNorthFilterCompanionOps {
  implicit def toGmosNorthFilterCompanionOps(self: GmosNorthFilter.type): GmosNorthFilterCompanionOps =
    new GmosNorthFilterCompanionOps(self)
}

object gmosnorthfilter
  extends ToGmosNorthFilterOps
     with ToGmosNorthFilterCompanionOps
