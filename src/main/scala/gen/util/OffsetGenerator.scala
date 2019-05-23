// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gen.util

import gem.math.{ Angle, Offset }

import cats.implicits._
import cats.effect.Sync
import scala.util.Random

/**
 * Common offset pattern generators.
 */
object OffsetGenerator {

  /**
   * Generates an offset grid of dimension `size`, with `Δ` steps and a `center`
   * position.
   *
   * @param size width and height dimensions
   * @param Δ step distance in p and q between positions in the grid
   * @param center center of the grid or (0, 0) if `None`
   */
  def grid(
    size:   (Int, Int),
    Δ:      Offset,
    center: Option[Offset]
  ): List[Offset] = {

    val cʹ = center.getOrElse(Offset.Zero)

    for {
      q <- List.iterate(cʹ.q + (Δ.q * ((size._2 - 1)/2.0)), size._2)(_ - Δ.q)
      p <- List.iterate(cʹ.p - (Δ.p * ((size._1 - 1)/2.0)), size._1)(_ + Δ.p)
    } yield Offset(p, q)

  }

  /**
   * Generates Fermat's spiral with `count` points and given `size` diameter
   * centered at `center`.
   *
   * @param count number of elements in the spiral
   * @param size rough diameter of the spiral
   * @param center center point of the spiral, or (0, 0) if `None`
   */
  def spiral(
    count:  Int,
    size:   Angle,
    center: Option[Offset]
  ): List[Offset] = {

    val cʹ    = center.getOrElse(Offset.Zero)

    val θ     = 2.399963 // 137.50776 degrees, the golden angle
    val scale = Angle.signedArcseconds.get(size).toDouble /
                2.0                                       /
                math.sqrt(
                  count * (
                    math.pow(math.cos(count * θ), 2.0) +
                    math.pow(math.sin(count * θ), 2.0)
                  )
                )

    List.range(0, count).map { n =>
      val d = n.toDouble
      val s = scale * math.sqrt(d)

      Offset(
        cʹ.p + Offset.P(Angle.signedArcseconds.reverseGet(s * math.cos(d * θ))),
        cʹ.q + Offset.Q(Angle.signedArcseconds.reverseGet(s * math.sin(d * θ)))
      )
    }
  }

  /**
   * Generates a repulsive random list of `Offset` position with `count`
   * elements contained in a `size` area centered on `center`.  "Repulsive" is
   * used to indicate that the presence of an offset position implies that it is
   * less likely that there will be another offset position nearby.
   *
   * @param count number of elements in the offset list
   * @param size size of one side of a square grid containing all the offsets
   * @param center center point
   */
  def random[F[_]: Sync](
    count:  Int,
    size:   Angle,
    center: Option[Offset]
  ): F[List[Offset]] = {

    def go: F[List[Offset]] = {

      // Generate a square grid
      val n = math.ceil(math.sqrt(count.toDouble)).toInt
      val d = Angle.signedArcseconds.modify(_/n)(size)
      val g = grid((n, n), Offset(Offset.P(d), Offset.Q(d)), center)

      // Pick a random point near an offset position
      val nextAngle: F[Angle] =
        Sync[F].delay(d * (Random.nextDouble - 0.5))

      def nextOffset(pos: Offset): F[Offset] =
        for {
          pʹ <- nextAngle.map(a => pos.p - Offset.P(a))
          qʹ <- nextAngle.map(a => pos.q - Offset.Q(a))
        } yield Offset(pʹ, qʹ)

      // Pick random points near each offset in the grid, shuffle and take the
      // first `count` elements.
      for {
        os0 <- g.traverse(nextOffset)
        os1 <- Sync[F].delay(Random.shuffle(os0))
      } yield os1.take(count)

    }

    if ((count > 0) && (Angle.signedArcseconds.get(size) > 0.0)) go
    else Sync[F].delay(Nil)
  }

  // TODO: Move these
  private implicit class AngleOps(a: Angle) {

    private def mod(f: Long => Long): Angle =
      Angle.fromMicroarcseconds(f(a.toMicroarcseconds))

    def *(n: Double): Angle =
      mod(µas => (µas * n).round)

    def *(n: Int): Angle =
      mod(µas => µas * n)

    def /(n: Double): Angle =
      mod(µas => (µas / n).round)

    def /(n: Int): Angle =
      mod(µas => µas / n)

  }

  // TODO: Move these
  private implicit class POps(p: Offset.P) {

    def -(p0: Offset.P): Offset.P =
      p + -p0

    def *(n: Double): Offset.P =
      Offset.P(p.toAngle * n)

    def *(n: Int): Offset.P =
      Offset.P(p.toAngle * n)

  }

  // TODO: Move these
  private implicit class QOps(q: Offset.Q) {

    def -(q0: Offset.Q): Offset.Q =
      q + -q0

    def *(n: Double): Offset.Q =
      Offset.Q(q.toAngle * n)

    def *(n: Int): Offset.Q =
      Offset.Q(q.toAngle * n)

  }

  // TODO: Move and also define for SplitEpi?
  private implicit class SplitMonoOps[S, A](m: gem.optics.SplitMono[S, A]) {

    def modify(f: A => A): S => S =
      s => m.reverseGet(f(m.get(s)))

  }

}
