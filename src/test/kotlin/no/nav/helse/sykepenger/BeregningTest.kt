package no.nav.helse.sykepenger

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.time.*

class BeregningTest {

   @Test
   fun `første og siste dato skal være fom og sisteUtbetalingsdato`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 100

      val søknad = Søknad(fom)

      val grunnbeløp = 96883L
      val sykepengegrunnlag = 260000L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-02")
      val grunnlag = Beregningsgrunnlag(søknad, grad, Sykepengegrunnlag(sykepengegrunnlag, grunnbeløp), sisteUtbetalingsdato)

      val expected = listOf(
         Dagsats(LocalDate.parse("2019-01-01"), 1000,  true),
         Dagsats(LocalDate.parse("2019-01-02"), 1000,  true)
      )
      val actual: List<Dagsats> = beregn(grunnlag)

      assertEquals(expected, actual)
   }

   @Test
   fun `dagsats er sykepengegrunnlaget delt på 260`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 100

      val søknad = Søknad(fom)

      val grunnbeløp = 96883L
      val sykepengegrunnlag = 260000L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-02")
      val grunnlag = Beregningsgrunnlag(søknad, grad, Sykepengegrunnlag(sykepengegrunnlag, grunnbeløp), sisteUtbetalingsdato)

      val expected = listOf(
         Dagsats(LocalDate.parse("2019-01-01"), 1000,  true),
         Dagsats(LocalDate.parse("2019-01-02"), 1000,  true)
      )
      val actual: List<Dagsats> = beregn(grunnlag)

      assertEquals(expected, actual)
   }

   @Test
   fun `helgedager skal fjernes fra resultatet`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 100

      val søknad = Søknad(fom)

      val grunnbeløp = 96883L
      val sykepengegrunnlag = 260000L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-08")
      val grunnlag = Beregningsgrunnlag(søknad, grad, Sykepengegrunnlag(sykepengegrunnlag, grunnbeløp), sisteUtbetalingsdato)


      val beregnet: List<Dagsats> = beregn(grunnlag)
      assertEquals(6, beregnet.size)
   }

   @Test
   fun `dagsats skal reduseres med sykmeldingsgrad`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 50

      val søknad = Søknad(fom)

      val grunnbeløp = 96883L
      val sykepengegrunnlag = 260000L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-08")
      val grunnlag = Beregningsgrunnlag(søknad, grad, Sykepengegrunnlag(sykepengegrunnlag, grunnbeløp), sisteUtbetalingsdato)

      val beregnet: List<Dagsats> = beregn(grunnlag)

      val forventetSats = 500L
      beregnet.forEach { assertEquals(forventetSats, it.sats) }
   }

   @Test
   fun `dagsats skal rundes av til nærmeste krone`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 50

      val søknad = Søknad(fom)

      val grunnbeløp = 96883L
      val sykepengegrunnlag = 260300L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-08")
      val grunnlag = Beregningsgrunnlag(søknad, grad, Sykepengegrunnlag(sykepengegrunnlag, grunnbeløp), sisteUtbetalingsdato)

      val beregnet: List<Dagsats> = beregn(grunnlag)

      val forventetSats = 501L
      beregnet.forEach { assertEquals(forventetSats, it.sats) }
   }

   @Test
   fun `sykepengegrunnlaget skal begrenses til 6G`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 100

      val søknad = Søknad(fom)

      val grunnbeløp = 96883L
      val sykepengegrunnlag = 600000L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-08")
      val grunnlag = Beregningsgrunnlag(søknad, grad, Sykepengegrunnlag(sykepengegrunnlag, grunnbeløp), sisteUtbetalingsdato)

      val beregnet: List<Dagsats> = beregn(grunnlag)

      val forventetSats = Math.round(6*grunnbeløp / 260.toDouble())
      beregnet.forEach { assertEquals(forventetSats, it.sats) }
   }

   @Test
   fun `skal ikke utbetale sykepenger under ferie`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 100

      val ferieFom = LocalDate.parse("2019-01-05")
      val ferieTom = LocalDate.parse("2019-01-07")

      val søknad = Søknad(fom, Ferie(ferieFom, ferieTom))

      val grunnbeløp = 96883L
      val sykepengegrunnlag = 260000L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-08")
      val grunnlag = Beregningsgrunnlag(søknad, grad, Sykepengegrunnlag(sykepengegrunnlag, grunnbeløp), sisteUtbetalingsdato)


      val expected = listOf(
         Dagsats(LocalDate.parse("2019-01-01"), 1000,  true),
         Dagsats(LocalDate.parse("2019-01-02"), 1000,  true),
         Dagsats(LocalDate.parse("2019-01-03"), 1000,  true),
         Dagsats(LocalDate.parse("2019-01-04"), 1000,  true),
         // 7/1 ferie
         Dagsats(LocalDate.parse("2019-01-07"), 1000,  false),
         Dagsats(LocalDate.parse("2019-01-08"), 1000,  true)
      )
      val actual: List<Dagsats> = beregn(grunnlag)
      assertEquals(expected, actual)
   }

   @Test
   fun `skal ikke utbetale sykepenger under permisjon`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 100

      val permisjonFom = LocalDate.parse("2019-01-05")
      val permisjonTom = LocalDate.parse("2019-01-07")

      val søknad = Søknad(fom, null, Permisjon(permisjonFom, permisjonTom))

      val grunnbeløp = 96883L
      val sykepengegrunnlag = 260000L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-08")
      val grunnlag = Beregningsgrunnlag(søknad, grad, Sykepengegrunnlag(sykepengegrunnlag, grunnbeløp), sisteUtbetalingsdato)


      val expected = listOf(
         Dagsats(LocalDate.parse("2019-01-01"), 1000,  true),
         Dagsats(LocalDate.parse("2019-01-02"), 1000,  true),
         Dagsats(LocalDate.parse("2019-01-03"), 1000,  true),
         Dagsats(LocalDate.parse("2019-01-04"), 1000,  true),
         // 7/1 permisjon
         Dagsats(LocalDate.parse("2019-01-07"), 1000,  false),
         Dagsats(LocalDate.parse("2019-01-08"), 1000,  true)
      )
      val actual: List<Dagsats> = beregn(grunnlag)
      assertEquals(expected, actual)
   }
}
