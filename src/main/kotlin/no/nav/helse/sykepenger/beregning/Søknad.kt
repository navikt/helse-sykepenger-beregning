package no.nav.helse.sykepenger.beregning

import java.time.LocalDate

class Søknad(val fom: LocalDate, val ferie: Ferie? = null, val permisjon: Permisjon? = null)
