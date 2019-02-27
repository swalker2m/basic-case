// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.syntax

import gem.enum.GmosNorthFilter
import gem.enum.GmosNorthFilter._

import cats.implicits._


final class GmosNorthFilterCompanionOps(val self: GmosNorthFilter.type) extends AnyVal {

  /**
   * Filters to use for acquisition, sorted by wavelength.
   */
  def allAcquisition: List[GmosNorthFilter] =
    List(UPrime, GPrime, RPrime, IPrime, ZPrime).sortBy(_.wavelength)

}

trait ToGmosNorthFilterCompanionOps {
  implicit def toGmosNorthFilterCompanionOps(self: GmosNorthFilter.type): GmosNorthFilterCompanionOps =
    new GmosNorthFilterCompanionOps(self)
}

object gmosnorthfilter extends ToGmosNorthFilterCompanionOps
