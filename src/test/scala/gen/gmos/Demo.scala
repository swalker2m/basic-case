// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic
package gen.gmos

import basic.enum._
import basic.misc._

import gem.Step
import gem.enum._
import gem.enum.GmosNorthDisperser.R831_G5302
import gem.enum.GmosNorthFilter.GG455
import gem.enum.GmosNorthFpu.LongSlit_0_75
import gem.math.Wavelength

import cats.implicits._
import cats.effect.{ ExitCode, IO, IOApp }
import cats.effect.concurrent.Ref

import fs2.Stream

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

/**
 * GmosN Longslit Sequence generation demonstration.
 */
object Demo extends IOApp {

  // A stand-in ITC that just picks from among three possible results at random.
  val dummyItc: Itc[IO] =
    new Itc[IO] {
      override def calculate(
        targetProfile: TargetProfile,
        observingMode: ObservingMode,
        signalToNoise: Int
      ): IO[Itc.Result] =
        IO {
          Random.nextInt(3) match {
            case 0 => Itc.Result.Success(1200 seconds, 14, 1022)
            case 1 => Itc.Result.Success(1100 seconds, 13, 1003)
            case _ => Itc.Result.Success(1000 seconds, 12, 1017)
          }
        }
    }

  // Needed to call the ITC but not really used.
  val dummyTargetProfile: TargetProfile =
    TargetProfile(
      SpatialProfile.PointSource,
      SpectralDistribution.PowerLaw(1.0),
      1.0,
      MagnitudeSystem.Vega,
      MagnitudeBand.R,
      Redshift(1.0)
    )

  // The example basic-case observing mode.
  val observingMode: ObservingMode.Spectroscopy.GmosNorth =
    ObservingMode.Spectroscopy.GmosNorth(
      Wavelength.fromNanometers.getOption(600).get,
      R831_G5302,
      LongSlit_0_75,
      Some(GG455)
    )

  // A coin flip after each "image through the slit" to decide whether we're
  // done with the acquisition.
  val acquired: IO[Boolean] =
    IO(Random.nextBoolean())

  val conditions: IO[ObservingConditions] =
    IO {
      ObservingConditions(
        CloudCover.Percent50,
        ImageQuality.Percent70,
        SkyBackground.Percent50,
        WaterVapor.Percent50
      )
    }

  // Sequence state.
  final case class Database(acc: Double, steps: Vector[(Step.GmosN, Double)]) {

    def +(step: Step.GmosN, stepSn: Double): Database =
      Database(acc + stepSn * stepSn, steps :+ ((step, stepSn)))

    def totalSn: Double =
      math.sqrt(acc)

  }

  object Database {

    val zero: Database =
      Database(0.0, Vector.empty)

  }

  def run(args: List[String]): IO[ExitCode] = {

    val db: Ref[IO, Database] =
      Ref.unsafe(Database.zero)

    def reachedS2N(goal: Int): IO[Boolean] =
      db.get.map { _.totalSn >= goal.toDouble }

    SequenceFormat.printHeader *>
      IO(println(" S/N"))      *>
      GmosNLongslitD(dummyItc, conditions, dummyTargetProfile, observingMode, 1000)
        .sequence(acquired, reachedS2N)
        .zipLeft(Stream.awakeDelay[IO](1 seconds)) // pause a bit between steps
        .evalTap[IO] { case (s, sn) => db.update(_ + (s, sn)) }
        .evalTap[IO] { case (s, _ ) => SequenceFormat.printStep(s) *> db.get.map(d => println(f" ${d.totalSn}%6.1f")) }
        .compile
        .drain
        .as(ExitCode.Success)
  }
}