package no.nav.helse.sykepenger.beregning

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit.DAYS

val arbeidsdagerPrÅr = 260

fun beregn(beregningsgrunnlag: Beregningsgrunnlag): Beregningsresultat {
   val dagsats = beregnDagsats(beregningsgrunnlag.sykepengegrunnlag)

   val delresultater = mutableListOf<Delresultat>()

   return finnPeriode(beregningsgrunnlag.fom, beregningsgrunnlag.sisteUtbetalingsdato)
      .map { Dagsats(it, dagsats, true) }
      .also { delresultater.add(Delresultat(it, "Når trygden yter sykepenger, utgjør sykepengegrunnlaget pr. dag 1/260 av sykepengegrunnlaget pr. år.", "§ 8-10 tredje ledd")) }
      .filterNot(::erHelg)
      .also { delresultater.add(Delresultat(it, "Trygden yter sykepenger for alle dagene i uken unntatt lørdag og søndag.", "§ 8-11")) }
      .map { avkortFravær(it, beregningsgrunnlag.permisjon) }
      .also { delresultater.add(Delresultat(it, "Det ytes ikke sykepenger fra trygden under lovbestemt ferie etter lov 29. april 1988 nr. 21 om ferie § 5 og permisjon, se også § 8-3 tredje ledd.", "§ 8-17 andre ledd")) }
      .map { avkortFravær(it, beregningsgrunnlag.ferie) }
      .also { delresultater.add(Delresultat(it, "Det ytes ikke sykepenger fra trygden under lovbestemt ferie etter lov 29. april 1988 nr. 21 om ferie § 5 og permisjon, se også § 8-3 tredje ledd.", "§ 8-17 andre ledd")) }
      .map { avkortSykmeldingsgrad(beregningsgrunnlag.sykmeldingsgrad, it) }
      .also { delresultater.add(Delresultat(it, "Sykepengenes størrelse skal beregnes på grunnlag av reduksjon i arbeidstiden og/eller inntektstap.", "§ 8-13 andre ledd")) }
      .let { Beregningsresultat(it, delresultater.toList()) }
}

internal fun beregnDagsats(sykepengegrunnlag: Sykepengegrunnlag) =
   BigDecimal.valueOf(sykepengegrunnlag.sykepengegrunnlag)
      .divide(BigDecimal(arbeidsdagerPrÅr), 0, RoundingMode.HALF_UP)
      .longValueExact()

private fun finnPeriode(fom: LocalDate, tom: LocalDate) =
   (0 .. DAYS.between(fom, tom)).map { fom.plusDays(it) }

private fun erHelg(dagsats: Dagsats) =
    dagsats.dato.dayOfWeek == DayOfWeek.SATURDAY || dagsats.dato.dayOfWeek == DayOfWeek.SUNDAY

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
