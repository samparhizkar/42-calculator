package com.sepidsa.fortytwocalculator

import java.text.DecimalFormat

class NumberConverterFrench private constructor() {
    companion object {
        private val dizaineNames = arrayOf(
            "",
            "",
            "vingt",
            "trente",
            "quarante",
            "cinquante",
            "soixante",
            "soixante",
            "quatre-vingt",
            "quatre-vingt",
        )

        private val uniteNames1 = arrayOf(
            "",
            "un",
            "deux",
            "trois",
            "quatre",
            "cinq",
            "six",
            "sept",
            "huit",
            "neuf",
            "dix",
            "onze",
            "douze",
            "treize",
            "quatorze",
            "quinze",
            "seize",
            "dix-sept",
            "dix-huit",
            "dix-neuf",
        )

        private val uniteNames2 = arrayOf(
            "",
            "",
            "deux",
            "trois",
            "quatre",
            "cinq",
            "six",
            "sept",
            "huit",
            "neuf",
            "dix",
        )

        private fun convertZeroToHundred(number: Int): String {
            val laDizaine = number / 10
            var lUnite = number % 10
            var resultat = ""

            when (laDizaine) {
                1, 7, 9 -> lUnite += 10
            }

            // séparateur "-" "et"  ""
            var laLiaison = ""
            if (laDizaine > 1) laLiaison = "-"

            // cas particuliers
            when (lUnite) {
                0 -> laLiaison = ""
                1 -> {
                    laLiaison = if (laDizaine == 8) "-" else " et "
                }

                11 -> {
                    if (laDizaine == 7) laLiaison = " et "
                }
            }

            resultat = when (laDizaine) {
                0 -> uniteNames1[lUnite]
                8 -> if (lUnite == 0) dizaineNames[laDizaine] else dizaineNames[laDizaine] + laLiaison + uniteNames1[lUnite]
                else -> dizaineNames[laDizaine] + laLiaison + uniteNames1[lUnite]
            }
            return resultat
        }

        private fun convertLessThanOneThousand(number: Int): String {
            val lesCentaines = number / 100
            val leReste = number % 100
            val sReste = convertZeroToHundred(leReste)

            return when (lesCentaines) {
                0 -> sReste
                1 -> if (leReste > 0) "cent $sReste" else "cent"
                else -> if (leReste > 0) "${uniteNames2[lesCentaines]} cent $sReste" else "${uniteNames2[lesCentaines]} cents"
            }
        }

        @JvmStatic
        fun convert(number: String): String {
            // 0 à 999 999 999 999
            if (number == "0") return "zéro"

            val mask = "000000000000"
            val df = DecimalFormat(mask)
            val snumber = mask.substring(0, mask.length - number.length) + number

            // XXXnnnnnnnnn
            val lesMilliards = snumber.substring(0, 3).toInt()
            // nnnXXXnnnnnn
            val lesMillions = snumber.substring(3, 6).toInt()
            // nnnnnnXXXnnn
            val lesCentMille = snumber.substring(6, 9).toInt()
            // nnnnnnnnnXXX
            val lesMille = snumber.substring(9, 12).toInt()

            val tradMilliards = when (lesMilliards) {
                0 -> ""
                1 -> convertLessThanOneThousand(lesMilliards) + " milliard "
                else -> convertLessThanOneThousand(lesMilliards) + " milliards "
            }
            var resultat = tradMilliards

            val tradMillions = when (lesMillions) {
                0 -> ""
                1 -> convertLessThanOneThousand(lesMillions) + " million "
                else -> convertLessThanOneThousand(lesMillions) + " millions "
            }
            resultat += tradMillions

            val tradCentMille = when (lesCentMille) {
                0 -> ""
                1 -> "mille "
                else -> convertLessThanOneThousand(lesCentMille) + " mille "
            }
            resultat += tradCentMille

            val tradMille = convertLessThanOneThousand(lesMille)
            resultat += tradMille

            return resultat
        }
    }
}
