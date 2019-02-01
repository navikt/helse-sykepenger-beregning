package no.nav.helse.sykepenger

import java.math.BigDecimal
import java.time.LocalDate

data class Dagsats(val dato: LocalDate, val sats: BigDecimal, val skalUtbetales: Boolean)
