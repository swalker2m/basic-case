// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.gen.gmos

/**
 * Exception raised during GMOS longslit sequence calculation.
 */
final case class GmosLongslitException(problem: GmosLongslitProblem)
  extends RuntimeException(problem.message)
