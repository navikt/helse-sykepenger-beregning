package no.nav.helse.sykepenger

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class BeregningTest {

   @Test
   fun `første og siste dato skal være fom og sisteUtbetalingsdato`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 100

      val søknad = Søknad(fom)

      val dagsats = 600L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-02")
      val inndataTilBeregning = Beregningsgrunnlag(søknad, grad, dagsats, sisteUtbetalingsdato)


      val ut: List<Dagsats> = beregn(inndataTilBeregning)

      assertTrue(ut.isNotEmpty())
      assertEquals(fom, ut[0].dato)
      assertEquals(sisteUtbetalingsdato, ut[ut.size - 1].dato)
   }

   @Test
   fun `dagsats skal forbli uendret når sykmeldingsgraden er 100 prosent`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 100

      val søknad = Søknad(fom)

      val dagsats = 600L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-02")
      val inndataTilBeregning = Beregningsgrunnlag(søknad, grad, dagsats, sisteUtbetalingsdato)


      val ut: List<Dagsats> = beregn(inndataTilBeregning)
      assertEquals(2, ut.size)
      assertEquals(BigDecimal(dagsats), ut[0].sats)
      assertEquals(BigDecimal(dagsats), ut[1].sats)
   }

   @Test
   fun `helgedager skal fjernes fra resultatet`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 100

      val søknad = Søknad(fom)

      val dagsats = 600L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-08")
      val inndataTilBeregning = Beregningsgrunnlag(søknad, grad, dagsats, sisteUtbetalingsdato)


      val ut: List<Dagsats> = beregn(inndataTilBeregning)
      assertEquals(6, ut.size)
   }

   @Test
   fun `grad påvirker dagsats`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 50

      val søknad = Søknad(fom)

      val dagsats = 600L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-08")
      val inndataTilBeregning = Beregningsgrunnlag(søknad, grad, dagsats, sisteUtbetalingsdato)


      val ut: List<Dagsats> = beregn(inndataTilBeregning)
      for (dagsats in ut) {
         assertEquals(BigDecimal(300), dagsats.sats)
      }
   }

   @Test
   fun `dagsats blir ikke rundet ned eller opp`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 50

      val søknad = Søknad(fom)

      val dagsats = 601L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-08")
      val inndataTilBeregning = Beregningsgrunnlag(søknad, grad, dagsats, sisteUtbetalingsdato)


      val ut: List<Dagsats> = beregn(inndataTilBeregning)
      for (dagsats in ut) {
         assertEquals(BigDecimal.valueOf(300.5), dagsats.sats)
      }
   }

   @Test
   fun `skal ikke utbetale sykepenger under ferie`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 100

      val ferieFom = LocalDate.parse("2019-01-05")
      val ferieTom = LocalDate.parse("2019-01-07")

      val søknad = Søknad(fom, Ferie(ferieFom, ferieTom))

      val dagsats = 600L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-08")
      val inndataTilBeregning = Beregningsgrunnlag(søknad, grad, dagsats, sisteUtbetalingsdato)


      val ut: List<Dagsats> = beregn(inndataTilBeregning)
      assertEquals(6, ut.size)

      assertTrue(ut[0].skalUtbetales) // tirsdag
      assertTrue(ut[1].skalUtbetales) // onsdag
      assertTrue(ut[2].skalUtbetales) // torsdag
      assertTrue(ut[3].skalUtbetales) // fredag
      // helg bortfaller fra resultatet
      assertFalse(ut[4].skalUtbetales) // mandag, ferie utbetales ikke
      assertTrue(ut[5].skalUtbetales) // tirsdag
   }

   @Test
   fun `skal ikke utbetale sykepenger under permisjon`() {
      val fom = LocalDate.parse("2019-01-01") // tirsdag
      val grad = 100

      val permisjonFom = LocalDate.parse("2019-01-05")
      val permisjonTom = LocalDate.parse("2019-01-07")

      val søknad = Søknad(fom, null, Permisjon(permisjonFom, permisjonTom))

      val dagsats = 600L

      val sisteUtbetalingsdato = LocalDate.parse("2019-01-08")
      val inndataTilBeregning = Beregningsgrunnlag(søknad, grad, dagsats, sisteUtbetalingsdato)


      val ut: List<Dagsats> = beregn(inndataTilBeregning)
      assertEquals(6, ut.size)

      assertTrue(ut[0].skalUtbetales) // tirsdag
      assertTrue(ut[1].skalUtbetales) // onsdag
      assertTrue(ut[2].skalUtbetales) // torsdag
      assertTrue(ut[3].skalUtbetales) // fredag
      // helg bortfaller fra resultatet
      assertFalse(ut[4].skalUtbetales) // mandag, permisjon utbetales ikke
      assertTrue(ut[5].skalUtbetales) // tirsdag
   }
}
