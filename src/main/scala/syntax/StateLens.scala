// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.syntax

import cats.{Eval, Now}
import cats.data.IndexedStateT
import monocle.PLens


/**
 * Our monocle version is missing operations like `assign_` that are in the
 * current monocle `StateLensSyntax`.
 */
final class MoreStateLensOps[S, T, A, B](val self: PLens[S, T, A, B]) extends AnyVal {

  /**
   * Set the value viewed through the lens and ignores both values. (This is
   * missing in our version of monocle, apparently.)
   */
  def assign_(b: B): IndexedStateT[Eval, S, T, Unit] =
    IndexedStateT(s => Now((self.set(b)(s), ())))

  /**
   * Alias for assign_.
   */
  def :=(b: B): IndexedStateT[Eval, S, T, Unit] =
    assign_(b)

}

trait ToMoreStateLensOps {
  implicit def toMoreStateLensOps[S, T, A, B](self: PLens[S, T, A, B]): MoreStateLensOps[S, T, A, B] =
    new MoreStateLensOps[S, T, A, B](self)
}

object statelens extends ToMoreStateLensOps
