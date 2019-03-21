// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.graphql

package object syntax {

  object all
    extends ToFromInputOps
       with ToFromInputCompanionOps
       with ToToInputOps
       with ToToInputCompanionOps
}