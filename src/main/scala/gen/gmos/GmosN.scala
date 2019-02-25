// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.gen.gmos

import gem.Step
import gem.config.DynamicConfig.GmosN
import gem.config.GmosConfig._
import gem.config.TelescopeConfig
import gem.math.{ Angle, Offset }
import gem.enum._

import cats.data.State
import java.time.Duration


/**
 * Setup and operations intended to be shared across GMOS North cases.
 */
private[gmos] trait GmosNOps {

  /**
   * Initial starting point GMOS North configuration.
   */
  val initialConfig: GmosN =
    GmosN(
      GmosCommonDynamicConfig(
        GmosCcdReadout(
          GmosXBinning.One,
          GmosYBinning.One,
          GmosAmpCount.Twelve,
          GmosAmpGain.Low,
          GmosAmpReadMode.Fast
        ),
        GmosDtax.Zero,
        Duration.ofSeconds(0),
        GmosRoi.FullFrame
      ),
      None,
      None,
      None
    )

  /**
   * Evaluates a State "program" to produce a value.
   */
  def eval[A](prog: State[GmosN, A]): A =
    prog.runA(initialConfig).value

  /**
   * Produces a "science" step based upon the current instrument configuration
   * state and the given telescope configuration.
   *
   * @param t telescope config to associate with the step
   */
  def scienceStep(t: TelescopeConfig): State[GmosN, Step.GmosN] =
    State.inspect(Step.GmosN(_, Step.Base.Science(t)))

  /**
   * Produces a "science" step based upon the current instrument configuration
   * state and the given offset.
   *
   * @param p offset in p
   * @param q offset in q
   */
  def scienceStep(p: Angle, q: Angle): State[GmosN, Step.GmosN] =
    scienceStep(TelescopeConfig(Offset.P(p), Offset.Q(q)))

  /**
   * Produces a smart flat based upon the current instrument configuration.
   */
  def smartFlatStep: State[GmosN, Step.GmosN] =
    State.inspect(Step.GmosN(_, Step.Base.SmartGcal(SmartGcalType.Flat)))

}
