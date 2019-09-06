package no.nav.helse.sykepenger.beregning

import java.time.LocalDate

data class Beregningsgrunnlag(val fom: LocalDate, val ferie: List<Ferie>? = null, val permisjon: List<Permisjon>? = null, val sykmeldingsgrad: Int, val sykepengegrunnlag: Sykepengegrunnlag, val sisteUtbetalingsdato: LocalDate)
