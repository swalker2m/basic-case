package basic
package search

import fs2.Stream

trait Search[F[_]] {

  def search(c: Constraints): Stream[F, ObservingMode] // something like this?

}