package no.nav.helse.sykepenger.beregning

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.longValueExact(mode: RoundingMode) = setScale(0, mode).longValueExact()

fun BigDecimal.percentage(percent: Int): BigDecimal {
   if (percent < 0 || percent > 100)
      throw IllegalArgumentException("percent must be a number [0, 100]")
   return multiply(BigDecimal(percent).movePointLeft(2))
      .stripTrailingZeros()
}
