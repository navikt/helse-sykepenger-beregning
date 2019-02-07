package no.nav.helse.sykepenger.beregning

import java.time.LocalDate

data class Beregningsgrunnlag(val fom: LocalDate, val ferie: Ferie? = null, val permisjon: Permisjon? = null, val sykmeldingsgrad: Int, val sykepengegrunnlag: Sykepengegrunnlag, val sisteUtbetalingsdato: LocalDate)
