package no.nav.helse.sykepenger

data class Sykepengegrunnlag(val fastsattInntekt: Long, val grunnbeløp: Long) {

    val sykepengegrunnlag = Math.min(6*grunnbeløp, fastsattInntekt)
}
