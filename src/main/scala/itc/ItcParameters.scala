// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic
package itc.client

case class ItcParameters(
  source:      ItcSourceDefinition,
  observation: ItcObservationDetails,
  conditions:  ItcObservingConditions,
  wfs:         ItcWavefrontSensor,
  instrument:  ObservingMode
)
