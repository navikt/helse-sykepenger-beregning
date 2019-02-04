package no.nav.helse.sykepenger

import java.time.LocalDate

data class Dagsats(val dato: LocalDate, val sats: Long, val skalUtbetales: Boolean)
