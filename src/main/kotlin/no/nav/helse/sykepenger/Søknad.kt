package no.nav.helse.sykepenger

import java.time.LocalDate

class Søknad(val fom: LocalDate, val ferie: Ferie? = null, val permisjon: Permisjon? = null)
