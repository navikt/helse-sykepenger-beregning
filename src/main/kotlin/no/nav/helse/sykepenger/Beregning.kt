package no.nav.helse.sykepenger

import java.math.*
import java.time.*
import java.time.temporal.ChronoUnit.*

val arbeidsdagerPrÅr = 260

fun beregn(beregningsgrunnlag: Beregningsgrunnlag): List<Dagsats> {
   val dagsats = beregnDagsats(beregningsgrunnlag.sykepengegrunnlag)

   return finnPeriode(beregningsgrunnlag.søknad.fom, beregningsgrunnlag.sisteUtbetalingsdato)
      .filterNot(::erHelg)
      .map { Dagsats(it, dagsats, true) }
      .map { avkortFravær(it, beregningsgrunnlag.søknad.permisjon) }
      .map { avkortFravær(it, beregningsgrunnlag.søknad.ferie) }
      .map { avkortSykmeldingsgrad(beregningsgrunnlag.sykmeldingsgrad, it) }
}

internal fun beregnDagsats(sykepengegrunnlag: Sykepengegrunnlag) =
   BigDecimal.valueOf(sykepengegrunnlag.sykepengegrunnlag)
      .divide(BigDecimal(arbeidsdagerPrÅr), 0, RoundingMode.HALF_UP)
      .longValueExact()

private fun finnPeriode(fom: LocalDate, tom: LocalDate) =
   (0 .. DAYS.between(fom, tom)).map { fom.plusDays(it) }

private fun erHelg(dag: LocalDate) =
    dag.dayOfWeek == DayOfWeek.SATURDAY || dag.dayOfWeek == DayOfWeek.SUNDAY

private fun avkortSykmeldingsgrad(sykmeldingsgrad: Int, dagsats: Dagsats): Dagsats {
   return if (sykmeldingsgrad < 100) {
      val sats = BigDecimal(dagsats.sats).percentage(sykmeldingsgrad).longValueExact(RoundingMode.HALF_UP)
      dagsats.copy(sats = sats)
   } else {
      dagsats
   }
}

private fun avkortFravær(dagsats: Dagsats, fravær: Fravær?): Dagsats {
   return fravær?.let {
      if (dagsats.dato >= fravær.fom && dagsats.dato <= fravær.tom)
         dagsats.copy(skalUtbetales = false)
      else
         dagsats
   } ?: dagsats
}
