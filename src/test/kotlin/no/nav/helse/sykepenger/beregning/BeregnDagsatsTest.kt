package no.nav.helse.sykepenger.beregning

import no.nav.helse.sykepenger.beregning.Sykepengegrunnlag
import no.nav.helse.sykepenger.beregning.beregnDagsats
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BeregnDagsatsTest {

    @Test
    fun `dagsats skal utgjøre sykepengegrunnlaget delt på 260`() {
        val sykepengegrunnlag = 260L
        val grunnbeløp = 50L
        val dagsats = beregnDagsats(Sykepengegrunnlag(sykepengegrunnlag, grunnbeløp))

        assertEquals(1, dagsats)
    }

    @Test
    fun `dagsats skal runde av til nærmeste krone`() {
        val sykepengegrunnlag = 300L
        val grunnbeløp = 50L
        val dagsats = beregnDagsats(Sykepengegrunnlag(sykepengegrunnlag, grunnbeløp))

        assertEquals(1, dagsats)
    }

    @Test
    fun `sykepengegrunnlaget kan ikke overstige seks ganger grunnbeløpet`() {
        val sykepengegrunnlag = 3000L
        val grunnbeløp = 50L
        val dagsats = beregnDagsats(Sykepengegrunnlag(sykepengegrunnlag, grunnbeløp))

        assertEquals(1, dagsats)
    }

    @Test
    fun `sykepengegrunnlaget kan være mindre enn 6G`() {
        assertEquals(500, Sykepengegrunnlag(500, 100).sykepengegrunnlag)
    }

    @Test
    fun `sykepengegrunnlaget kan ikke være mer enn 6G`() {
        assertEquals(600, Sykepengegrunnlag(601, 100).sykepengegrunnlag)
    }
}
