package com.sepidsa.fortytwocalculator

class NumberConveterAmericanPartII {
    companion object {
        private val ones = arrayOf(
            " zero",
            " one",
            " two",
            " three",
            " four",
            " five",
            " six",
            " seven",
            " eight",
            " nine",
        )

        @JvmStatic
        fun convert(number: String): String {
            var result = " point"
            for (index in number.indices) {
                result += ones[(number[index].toString()).toInt()]
            }
            return result
        }
    }
}
