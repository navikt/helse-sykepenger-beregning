package no.nav.helse.sykepenger

import java.time.LocalDate

sealed class Fravær(open val fom: LocalDate, open val tom: LocalDate)
data class Ferie(override val fom: LocalDate, override val tom: LocalDate): Fravær(fom, tom)
data class Permisjon(override val fom: LocalDate, override val tom: LocalDate): Fravær(fom, tom)
