package no.nav.helse.sykepenger

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.stream.Collectors
import java.util.stream.Stream

val sykepengegrunnlagdivisor = 260

fun beregn(beregningsgrunnlag: Beregningsgrunnlag): List<Dagsats> {
    val dagsats = beregnDagsats(beregningsgrunnlag.sykepengegrunnlag)
    return finnPeriode(beregningsgrunnlag.søknad.fom, beregningsgrunnlag.sisteUtbetalingsdato)
            .fjernHelgedager()
            .settDagsats(dagsats)
            .avkortning(beregningsgrunnlag)
            .collect(Collectors.toList())
}

internal fun finnPeriode(fom: LocalDate, tom: LocalDate) = fom.datesUntil(tom.plusDays(1))

internal fun Stream<LocalDate>.fjernHelgedager() = filter { date ->
    date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY
}

internal fun beregnDagsats(sykepengegrunnlag: Sykepengegrunnlag): Long {
    return BigDecimal.valueOf(sykepengegrunnlag.sykepengegrunnlag)
            .divide(BigDecimal(sykepengegrunnlagdivisor), 0, RoundingMode.HALF_UP).longValueExact()
}

internal fun Stream<LocalDate>.settDagsats(dagsats: Long): Stream<Dagsats> {
    return map {dato ->
        Dagsats(dato, dagsats, true)
    }
}

internal fun Stream<Dagsats>.avkortning(beregningsgrunnlag: Beregningsgrunnlag): Stream<Dagsats> {
    return avkortFravær(beregningsgrunnlag.søknad.ferie)
            .avkortFravær(beregningsgrunnlag.søknad.permisjon)
            .avkortSykmeldingsgrad(beregningsgrunnlag.sykmeldingsgrad)
}

internal fun Stream<Dagsats>.avkortSykmeldingsgrad(sykmeldingsgrad: Int): Stream<Dagsats> {
    return map {dagsats ->
        if (sykmeldingsgrad < 100) {
            val sats = BigDecimal(dagsats.sats * sykmeldingsgrad).divide(BigDecimal(100),0, RoundingMode.HALF_UP).longValueExact()
            dagsats.copy(sats = sats)
        } else {
            dagsats
        }
    }
}

internal fun Stream<Dagsats>.avkortFravær(fravær: Fravær?): Stream<Dagsats> {
    return map { dagsats ->
        if (fravær != null && dagsats.dato >= fravær.fom && dagsats.dato <= fravær.tom) {
            dagsats.copy(skalUtbetales = false)
        } else {
            dagsats
        }
    }
}
