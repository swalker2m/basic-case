// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic
package search

import basic.syntax.all._
import basic.search.gmosnorth._
import cats.implicits._
import gem.enum._

object Search {

  def search(constraints: Constraints): SearchResult =
    constraints match {
      case cs: Constraints.Spectroscopy => spectroscopy(cs)
    }

  def spectroscopy(constraints: Constraints.Spectroscopy): SearchResult.Spectroscopy = {

    // As a first pass we'll generate every possible configuration and then filter them at the end.
    // This lets us apply the constraints in one place rather than duplicating the filtering logic
    // for each instrument (at the cost of dealing with some large sets in memory).

    val gmosNorthModes: List[ObservingMode.Spectroscopy] =
      for {
        disp   <- GmosNorthDisperser.all
        fpu    <- GmosNorthFpu.all.filterNot(_.isNodAndShuffle) // don't consider for now
        filter <- GmosNorthFilterSelector.selectBlocking(disp, fpu, constraints.λ).toList
      } yield ObservingMode.Spectroscopy.GmosNorth(constraints.λ, disp, fpu, filter)

    // more instruments ...

    // Every spectrographic observing mode
    val allModes: List[ObservingMode.Spectroscopy] =
      gmosNorthModes // ++ ...

    // Now filter down the list.
    val compatibleModes: List[ObservingMode.Spectroscopy] =
      allModes
        .filter(_.simultaneousCoverage >= constraints.simultaneousCoverage)
        .filter(_.resolution >= constraints.resolution)

    SearchResult.Spectroscopy(constraints, compatibleModes)

  }

}