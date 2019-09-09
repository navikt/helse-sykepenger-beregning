package no.nav.helse.sykepenger.beregning

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class BeregningTest {

   @Test
   fun `når grad er 100 skal dagsats være sykepengegrunnlag delt på 260, i perioden det søkes for`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 100

      val grunnbeløp = 96883L
      val sykepengegrunnlag = 260000L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-02")
      val grunnlag = Beregningsgrunnlag(fom, emptyList(), emptyList(), grad, Sykepengegrunnlag(sykepengegrunnlag, grunnbeløp), sisteUtbetalingsdato)

      val expected = listOf(
         Dagsats(LocalDate.parse("2019-01-01"), 1000, true),
         Dagsats(LocalDate.parse("2019-01-02"), 1000, true)
      )
      val actual: List<Dagsats> = beregn(grunnlag).dagsatser

      assertEquals(expected, actual)
   }

   @Test
   fun `helgedager skal fjernes fra resultatet`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 100

      val grunnbeløp = 96883L
      val sykepengegrunnlag = 260000L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-08")
      val grunnlag = Beregningsgrunnlag(fom, emptyList(), emptyList(), grad, Sykepengegrunnlag(sykepengegrunnlag, grunnbeløp), sisteUtbetalingsdato)


      val beregnet: List<Dagsats> = beregn(grunnlag).dagsatser
      assertEquals(6, beregnet.size)
   }

   @Test
   fun `dagsats skal reduseres med sykmeldingsgrad`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 50

      val grunnbeløp = 96883L
      val sykepengegrunnlag = 260000L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-08")
      val grunnlag = Beregningsgrunnlag(fom, emptyList(), emptyList(), grad, Sykepengegrunnlag(sykepengegrunnlag, grunnbeløp), sisteUtbetalingsdato)

      val beregnet: List<Dagsats> = beregn(grunnlag).dagsatser

      val forventetSats = 500L
      beregnet.forEach { assertEquals(forventetSats, it.sats) }
   }

   @Test
   fun `dagsats skal rundes av til nærmeste krone`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 50

      val grunnbeløp = 96883L
      val sykepengegrunnlag = 260300L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-08")
      val grunnlag = Beregningsgrunnlag(fom, emptyList(), emptyList(), grad, Sykepengegrunnlag(sykepengegrunnlag, grunnbeløp), sisteUtbetalingsdato)

      val beregnet: List<Dagsats> = beregn(grunnlag).dagsatser

      val forventetSats = 501L
      beregnet.forEach { assertEquals(forventetSats, it.sats) }
   }

   @Test
   fun `sykepengegrunnlaget skal begrenses til 6G`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 100

      val grunnbeløp = 96883L
      val sykepengegrunnlag = 600000L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-08")
      val grunnlag = Beregningsgrunnlag(fom, emptyList(), emptyList(), grad, Sykepengegrunnlag(sykepengegrunnlag, grunnbeløp), sisteUtbetalingsdato)

      val beregnet: List<Dagsats> = beregn(grunnlag).dagsatser

      val forventetSats = Math.round(6*grunnbeløp / 260.toDouble())
      beregnet.forEach { assertEquals(forventetSats, it.sats) }
   }

   @Test
   fun `skal ikke utbetale sykepenger under ferie`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 100

      val ferieFom = LocalDate.parse("2019-01-05")
      val ferieTom = LocalDate.parse("2019-01-07")

      val grunnbeløp = 96883L
      val sykepengegrunnlag = 260000L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-08")
      val grunnlag = Beregningsgrunnlag(fom, listOf(Ferie(ferieFom, ferieTom)), emptyList(), grad, Sykepengegrunnlag(sykepengegrunnlag, grunnbeløp), sisteUtbetalingsdato)


      val expected = listOf(
         Dagsats(LocalDate.parse("2019-01-01"), 1000, true),
         Dagsats(LocalDate.parse("2019-01-02"), 1000, true),
         Dagsats(LocalDate.parse("2019-01-03"), 1000, true),
         Dagsats(LocalDate.parse("2019-01-04"), 1000, true),
         // 7/1 ferie
         Dagsats(LocalDate.parse("2019-01-07"), 1000, false),
         Dagsats(LocalDate.parse("2019-01-08"), 1000, true)
      )
      val actual: List<Dagsats> = beregn(grunnlag).dagsatser
      assertEquals(expected, actual)
   }

   @Test
   fun `skal ikke utbetale sykepenger under permisjon`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 100

      val permisjonFom = LocalDate.parse("2019-01-05")
      val permisjonTom = LocalDate.parse("2019-01-07")

      val grunnbeløp = 96883L
      val sykepengegrunnlag = 260000L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-08")
      val grunnlag = Beregningsgrunnlag(fom, emptyList(), listOf(Permisjon(permisjonFom, permisjonTom)), grad, Sykepengegrunnlag(sykepengegrunnlag, grunnbeløp), sisteUtbetalingsdato)


      val expected = listOf(
         Dagsats(LocalDate.parse("2019-01-01"), 1000, true),
         Dagsats(LocalDate.parse("2019-01-02"), 1000, true),
         Dagsats(LocalDate.parse("2019-01-03"), 1000, true),
         Dagsats(LocalDate.parse("2019-01-04"), 1000, true),
         // 7/1 permisjon
         Dagsats(LocalDate.parse("2019-01-07"), 1000, false),
         Dagsats(LocalDate.parse("2019-01-08"), 1000, true)
      )
      val actual: List<Dagsats> = beregn(grunnlag).dagsatser
      assertEquals(expected, actual)
   }
}
