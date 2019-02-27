// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.syntax

import gem.config.DynamicConfig.GmosN
import gem.config.GmosConfig.{ GmosCcdReadout, GmosCommonDynamicConfig }
import gem.config.TelescopeConfig
import gem.enum.{ GmosRoi, GmosXBinning, GmosYBinning }
import gem.enum.SmartGcalType.Flat
import gem.Step

import java.time.Duration
import monocle.Lens

final class DynamicConfigGmosNorthOps(val self: GmosN) extends AnyVal {

  def asScience(t: TelescopeConfig): Step.GmosN =
    Step.GmosN(self, Step.Base.Science(t))

  def asSmartFlat: Step.GmosN =
    Step.GmosN(self, Step.Base.SmartGcal(Flat))

}

trait ToDynamicConfigGmosNorthOps {
  implicit def toDynamicConfigGmosNorthOps(self: GmosN): DynamicConfigGmosNorthOps =
    new DynamicConfigGmosNorthOps(self)
}

final class DynamicConfigGmosNorthCompanionOps(val self: GmosN.type) extends AnyVal {

  def xBinning: Lens[GmosN, GmosXBinning] =
    GmosN.common composeLens GmosCommonDynamicConfig.ccdReadout composeLens GmosCcdReadout.xBinning

  def yBinning: Lens[GmosN, GmosYBinning] =
    GmosN.common composeLens GmosCommonDynamicConfig.ccdReadout composeLens GmosCcdReadout.yBinning

  def exposureTime: Lens[GmosN, Duration] =
    GmosN.common composeLens GmosCommonDynamicConfig.exposureTime

  def roi: Lens[GmosN, GmosRoi] =
    GmosN.common composeLens GmosCommonDynamicConfig.roi

}

trait ToDynamicConfigGmosNorthCompanionOps {
  implicit def toDynamicConfigGmosNorthCompanionOps(self: GmosN.type): DynamicConfigGmosNorthCompanionOps =
    new DynamicConfigGmosNorthCompanionOps(self)
}

object dynamicconfiggmosnorth extends ToDynamicConfigGmosNorthOps
                                 with ToDynamicConfigGmosNorthCompanionOps
