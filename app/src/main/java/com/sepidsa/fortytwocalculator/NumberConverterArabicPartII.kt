package com.sepidsa.fortytwocalculator

import java.math.BigDecimal

class NumberConverterArabicPartII {
    companion object {
        private val ones = arrayOf("صفر ", "واحد ", "اثنان ", "ثلاث ة", "أربعة ", "خمسة ", "ستة ", "سبعة ", "ثمانية ", "تسعة ")

        @JvmStatic
        fun convert(number: String): String {
            if (BigDecimal(number).compareTo(BigDecimal.ZERO) == 0) return ""

            var result = " فاصل "
            for (index in number.indices) {
                result += ones[(number[index].toString()).toInt()]
            }
            return result
        }
    }
}
