package no.nav.helse.sykepenger.beregning

import java.time.LocalDate

data class Beregningsgrunnlag(val søknad: Søknad, val sykmeldingsgrad: Int, val sykepengegrunnlag: Sykepengegrunnlag, val sisteUtbetalingsdato: LocalDate)
