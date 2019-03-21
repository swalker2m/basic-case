package basic.graphql

package object syntax {

  object all
    extends ToFromInputOps
       with ToFromInputCompanionOps
       with ToToInputOps
       with ToToInputCompanionOps
}