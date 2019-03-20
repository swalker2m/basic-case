// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic

package object syntax {

  object all
    extends ToDynamicConfigGmosNorthCompanionOps
       with ToDynamicConfigGmosNorthOps
       with ToDurationOps
       with ToFiniteDurationOps
       with ToGmosDetectorOps
       with ToGmosNorthDisperserOps
       with ToGmosNorthFilterCompanionOps
       with ToGmosNorthFilterOps
       with ToGmosNorthFpuOps
       with ToGmosSouthDisperserOps
       with ToInstrumentOps
       with ToIntOps
       with ToMagnitudeBandOps
       with ToMagnitudeSystemOps
       with ToMoreStateLensOps
       with ToMoreStateOptionalOps
       with ToNumericOps

}