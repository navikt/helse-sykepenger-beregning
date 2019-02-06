package no.nav.helse.sykepenger.beregning

import java.time.LocalDate

data class Dagsats(val dato: LocalDate, val sats: Long, val skalUtbetales: Boolean)
