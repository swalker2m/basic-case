// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic
package gen.gmos

import gem.Step
import gem.Step.Base
import gem.Step.Base.SmartGcal
import gem.config.DynamicConfig.GmosN
import gem.config.TelescopeConfig
import gem.enum._
import gem.math.{ Angle, Wavelength }
import gem.math.Offset.{P, Q}

import cats.effect.IO

import monocle.std.option.some
import monocle.{Lens, Optional}

import java.time.Duration


/**
 * Throwaway code for formatting sequence into a table.
 */
object SequenceFormat {

  final case class Column[A](
    name:         String,
    getter:       Step.GmosN => Option[A],
    format:       A => String,
    size:         Int = 10,
    rightJustify: Boolean = false
  ) {
    def formatStep: Step.GmosN => Option[String] =
      s => getter(s).map(format)
  }

  private def optional[A](o: Optional[GmosN, A]): Step.GmosN => Option[A] =
    (Step.GmosN.dynamicConfig composeOptional o).getOption(_)

  private def lens[A](l: Lens[GmosN, A]): Step.GmosN => Option[A] =
    s => Some((Step.GmosN.dynamicConfig composeLens l).get(s))

  private val stepType: Column[Base] = Column(
    "Type",
    s => Some(Step.GmosN.base.get(s)),
    {
      case Base.Bias         => "Bias"
      case Base.Dark         => "Dark"
      case Base.Gcal(_)      => "GCAL"
      case Base.Science(_)   => ""
      case Base.SmartGcal(t) => s"Smart ${t.tag}"
    },
    10
  )

  private val fpu: Column[GmosNorthFpu] = Column(
    "FPU",
    optional(GmosN.builtinFpu),
    _.shortName,
    10
  )

  private val disperser: Column[GmosNorthDisperser] = Column(
    "Disperser",
    optional(GmosN.disperser),
    _.shortName,
    9
  )

  private val filter: Column[GmosNorthFilter] = Column(
    "Filter",
    optional(GmosN.filter composePrism some),
    _.shortName,
    7
  )

  private val wavelength: Column[Wavelength] = Column(
    "λ (nm)",
    optional(GmosN.wavelength),
    w => Wavelength.fromNanometers.reverseGet(w).toString,
    6,
    rightJustify = true
  )

  private val exposureTime: Column[Duration] = Column(
    "Secs",
    {
      case Step.GmosN(_, SmartGcal(_)) => None
      case Step.GmosN(dyn, _) => Some(GmosN.exposureTime.get(dyn))
    },
    _.getSeconds.toString,
    4,
    rightJustify = true
  )

  private val xbin: Column[GmosXBinning] = Column(
    "Xbin",
    lens(GmosN.xBinning),
    _.shortName,
    4,
    rightJustify = true
  )

  private val ybin: Column[GmosYBinning] = Column(
    "Ybin",
    lens(GmosN.yBinning),
    _.shortName,
    4,
    rightJustify = true
  )

  private val roi: Column[GmosRoi] = Column(
    "ROI",
    lens(GmosN.roi),
    _.shortName,
    5
  )

  private val p: Column[P] = Column(
    "p",
    (Step.GmosN.base composePrism Base.telescopeConfig composeLens TelescopeConfig.p).getOption(_),
    p => f"${Angle.signedArcseconds.get(p.toAngle)}%.1f",
    4,
    rightJustify = true
  )

  private val q: Column[Q] = Column(
    "q",
    (Step.GmosN.base composePrism Base.telescopeConfig composeLens TelescopeConfig.q).getOption(_),
    q => f"${Angle.signedArcseconds.get(q.toAngle)}%.1f",
    4,
    rightJustify = true
  )

  private val columns = List(
    stepType,
    fpu,
    disperser,
    filter,
    wavelength,
    exposureTime,
    xbin,
    ybin,
    roi,
    p,
    q
  )

  def formatHeader: String =
    columns.map(c => String.format(s"%-${c.size}s", c.name)).mkString("| ", " | ", " |")

  def printHeader: IO[Unit] =
    IO(print(formatHeader))

  def formatStep(s: Step.GmosN): String =
    columns.map { c =>
      val fmt = s"%${if (c.rightJustify) "" else "-"}${c.size}s"

      String.format(fmt, c.formatStep(s).getOrElse(""))
    }.mkString("| ", " | ", " |")

  def printStep(s: Step.GmosN): IO[Unit] =
    IO(print(formatStep(s)))

}
