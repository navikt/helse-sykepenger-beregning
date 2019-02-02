package no.nav.helse.sykepenger

import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.stream.Collectors
import java.util.stream.Stream

fun beregn(beregningsgrunnlag: Beregningsgrunnlag): List<Dagsats> {
    val dagsats = beregnDagsats(beregningsgrunnlag.sykepengegrunnlag/*, beregningsgrunnlag.grunnbeløp*/)
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

internal fun beregnDagsats(sykepengegrunnlag: Long): BigDecimal {
    return BigDecimal(sykepengegrunnlag).divide(BigDecimal(260))
}

internal fun Stream<LocalDate>.settDagsats(dagsats: BigDecimal): Stream<Dagsats> {
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
    val sykmeldingsgradSomProsent = BigDecimal(sykmeldingsgrad).divide(BigDecimal(100))
    return map {dagsats ->
        if (sykmeldingsgrad < 100) {
            val sats = dagsats.sats.times(sykmeldingsgradSomProsent)
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
