package no.nav.helse.sykepenger.beregning

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.math.RoundingMode

class BigDecimalTest {

   @Test
   fun `ugyldig prosent skal kaste exception`() {
      assertThrows<IllegalArgumentException> {
         BigDecimal(1).percentage(101)
      }
      assertThrows<IllegalArgumentException> {
         BigDecimal(1).percentage(-1)
      }
   }

   @Test
   fun `skal returnere bigdecimal med riktig verdi uten unødvendig nuller`() {
      assertEquals(BigDecimal("0.5"), BigDecimal(1).percentage(50))
   }

   @Test
   fun `skal returnere bigdecimal med riktig verdi med riktig antall desimaler`() {
      assertEquals(BigDecimal("0.66"), BigDecimal(1).percentage(66))
   }

   @Test
   fun `skal returnere samme verdi for 100 %`() {
      assertEquals(BigDecimal("1"), BigDecimal(1).percentage(100))
   }

   @Test
   fun `skal returnere 0 for 0 %`() {
      assertEquals(BigDecimal("0"), BigDecimal(1).percentage(0))
   }

   @Test
   fun `skal returnere en avrundet long`() {
      assertEquals(1, BigDecimal("0.9").longValueExact(RoundingMode.CEILING))
      assertEquals(1, BigDecimal("0.50").longValueExact(RoundingMode.HALF_UP))
      assertEquals(0, BigDecimal("0.49").longValueExact(RoundingMode.HALF_UP))
      assertEquals(0, BigDecimal("0.9").longValueExact(RoundingMode.FLOOR))
   }
}
