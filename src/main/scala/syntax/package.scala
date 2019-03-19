// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic

package object syntax {

  object all
    extends ToDurationOps
       with ToDynamicConfigGmosNorthOps
       with ToDynamicConfigGmosNorthCompanionOps
       with ToGmosDetectorOps
       with ToGmosNorthDisperserOps
       with ToGmosNorthFilterOps
       with ToGmosNorthFilterCompanionOps
       with ToGmosNorthFpuOps
       with ToGmosSouthDisperserOps
       with ToIntOps
       with ToMoreStateLensOps
       with ToMoreStateOptionalOps
       with ToMagnitudeBandOps
       with ToMagnitudeSystemOps

}