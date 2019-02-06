// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic
package search

import fs2.Stream

trait Search[F[_]] {

  def search(c: Constraints): Stream[F, ObservingMode] // something like this?

}