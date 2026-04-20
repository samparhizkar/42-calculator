package com.sepidsa.fortytwocalculator

class NumberConverterPersianPartII {
    companion object {
        @JvmStatic
        fun convert(number: String): String {
            val initialTranslation = NumberConveterPersianPartI().convert(number)
            return when (number.length) {
                0 -> ""
                1 -> initialTranslation + "دهم"
                2 -> initialTranslation + "صدم"
                3 -> initialTranslation + "هزارم"
                4 -> initialTranslation + "ده هزارم"
                5 -> initialTranslation + "صد هزارم"
                6 -> initialTranslation + "میلیونم"
                7 -> ""
                else -> ""
            }
        }
    }
}
