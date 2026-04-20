package com.sepidsa.fortytwocalculator

class NumberConverterFrenchPartII {
    companion object {
        private val ones = arrayOf(
            " zéro",
            " un",
            " deux",
            " trois",
            " quatre",
            " cinq",
            " six",
            " sept",
            " huit",
            " neuf",
            " dix",
        )

        @JvmStatic
        fun convert(number: String): String {
            var result = " virgule"
            for (index in number.indices) {
                result += ones[(number[index].toString()).toInt()]
            }
            return result
        }
    }
}
