package no.nav.helse.sykepenger

import java.time.LocalDate

class Beregningsgrunnlag(val søknad: Søknad, val sykmeldingsgrad: Int, val sykepengegrunnlag: Long, val sisteUtbetalingsdato: LocalDate)
