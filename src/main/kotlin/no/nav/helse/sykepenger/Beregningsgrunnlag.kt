package no.nav.helse.sykepenger

import java.time.LocalDate

data class Beregningsgrunnlag(val søknad: Søknad, val grunnbeløp: Long, val sykmeldingsgrad: Int, val sykepengegrunnlag: Long, val sisteUtbetalingsdato: LocalDate)
