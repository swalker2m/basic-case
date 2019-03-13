// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic
package search

import gem.math.Wavelength

/** Observing constraints, used to narrow the space of compatible observing modes. */
sealed trait Constraints

object Constraints {

  /** Observing constraints for spectroscopy. */
  final case class Spectroscopy(
    λ:                    Wavelength,
    simultaneousCoverage: Wavelength,
    resolution:           Int, // todo: Resolution
  ) extends Constraints

}