// Copyright (c) 2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package basic.enum

sealed abstract class NonStellarLibrarySpectrum(
  val tag:         String,
  val shortName:   String,
  val sedSpectrum: String
) extends Product with Serializable

object NonStellarLibrarySpectrum {

  case object EllipticalGalaxy  extends NonStellarLibrarySpectrum("EllipticalGalaxy", "Elliptical Galaxy", "elliptical-galaxy")
  case object SpiralGalaxy      extends NonStellarLibrarySpectrum("SpiralGalaxy", "Spiral Galaxy (Sc)", "spiral-galaxy")
  case object QS0               extends NonStellarLibrarySpectrum("QS0", "QSO (80-855nm)", "QSO")
  case object QS02              extends NonStellarLibrarySpectrum("QS02", "QSO (276-3520nm)", "QSO2")
  case object OrionNebula       extends NonStellarLibrarySpectrum("OrionNebula", "HII region (Orion)", "Orion-nebula")
  case object PlanetaryNebula   extends NonStellarLibrarySpectrum("PlanetaryNebula", "Planetary nebula (NGC7009: 100-1100nm)", "Planetary-nebula")
  case object PlanetaryNebula2  extends NonStellarLibrarySpectrum("PlanetaryNebula2", "Planetary nebula (IC5117: 480-2500nm)", "Planetary-nebula2")
  case object PlanetaryNebula3  extends NonStellarLibrarySpectrum("PlanetaryNebula3", "Planetary nebula (NGC7027)", "Planetary-nebula-NGC7027")
  case object StarburstGalaxy   extends NonStellarLibrarySpectrum("StarburstGalaxy", "Starburst galaxy (M82)", "Starburst-galaxy")
  case object PmsStar           extends NonStellarLibrarySpectrum("PmsStar", "Pre-main sequence star (HD100546)", "PMS-star")
  case object GalacticCenter    extends NonStellarLibrarySpectrum("GalacticCenter", "Galactic center", "Galactic-center")
  case object Afgl230           extends NonStellarLibrarySpectrum("Afgl230", "AFGL230 (M10II star, silicate absorp.)", "afgl230")
  case object Afgl3068          extends NonStellarLibrarySpectrum("Afgl3068", "AFGL3068 (Late N-type star)", "afgl3068")
  case object AlphaBoo          extends NonStellarLibrarySpectrum("AlphaBoo", "Alpha Boo (K1.5III star)", "alphaboo")
  case object AlphaCar          extends NonStellarLibrarySpectrum("AlphaCar", "Alpha Car (F0II star)", "alphacar")
  case object BetaAnd           extends NonStellarLibrarySpectrum("BetaAnd", "Beta And (M0IIIa star)", "betaand")
  case object BetaGru           extends NonStellarLibrarySpectrum("BetaGru", "Beta Gru (M5III star)", "betagru")
  case object GammaCas          extends NonStellarLibrarySpectrum("GammaCas", "Gamma Cas (B0IVe star)", "gammacas")
  case object GammaDra          extends NonStellarLibrarySpectrum("GammaDra", "Gamma Dra (K5III star)", "gammadra")
  case object L1511Irs          extends NonStellarLibrarySpectrum("L1511Irs", "l1551irs (young stellar object)", "l1551irs")
  case object NGC1068           extends NonStellarLibrarySpectrum("NGC1068", "NGC 1068 (Dusty active galaxy)", "ngc1068")
  case object NGC2023           extends NonStellarLibrarySpectrum("NGC2023", "NGC2023 (Reflection Nebula)", "ngc2023")
  case object NGC2440           extends NonStellarLibrarySpectrum("NGC2440", "NGC2440 (line dominated PN)", "ngc2440")
  case object OCet              extends NonStellarLibrarySpectrum("OCet", "O Cet (M7IIIa Star, silicate emission)", "ocet")
  case object OrionBar          extends NonStellarLibrarySpectrum("OrionBar", "Orion Bar (Dusty HII region)", "orionbar")
  case object Rscl              extends NonStellarLibrarySpectrum("Rscl", "rscl (N-type Dusty Carbon Star, SiC em.)","rscl")
  case object Txpsc             extends NonStellarLibrarySpectrum("Txpsc", "txpsc (N-type Visible Carbon Star)", "txpsc")
  case object Wr104             extends NonStellarLibrarySpectrum("Wr104", "WR 104 (Wolf-Rayet Star + dust)", "wr104")
  case object Wr34              extends NonStellarLibrarySpectrum("Wr34", "WR 34 (Wolf-Rayet Star)", "wr34")
  case object Mars              extends NonStellarLibrarySpectrum("Mars", "Mars", "Mars")
  case object Jupiter           extends NonStellarLibrarySpectrum("Jupiter", "Jupiter", "Jupiter")
  case object Saturn            extends NonStellarLibrarySpectrum("Saturn", "Saturn", "Saturn")
  case object Uranus            extends NonStellarLibrarySpectrum("Uranus", "Uranus", "Uranus")
  case object Neptune           extends NonStellarLibrarySpectrum("Neptune", "Neptune", "Neptune")

}

