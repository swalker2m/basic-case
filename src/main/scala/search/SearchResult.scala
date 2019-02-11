// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic
package search

/** A set of observing constraints paired with a compatible observing mode. */
sealed trait SearchResult

object SearchResult {

  case class Spectroscopy(
    constraints:    Constraints.Spectroscopy,
    observingModes: List[ObservingMode.Spectroscopy]
  ) extends SearchResult

}