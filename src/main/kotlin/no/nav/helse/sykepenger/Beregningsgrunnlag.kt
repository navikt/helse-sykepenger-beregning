package no.nav.helse.sykepenger

import java.time.LocalDate

data class Beregningsgrunnlag(val søknad: Søknad, val sykmeldingsgrad: Int, val sykepengegrunnlag: Sykepengegrunnlag, val sisteUtbetalingsdato: LocalDate)
