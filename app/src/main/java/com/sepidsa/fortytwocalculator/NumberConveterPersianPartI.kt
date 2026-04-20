package com.sepidsa.fortytwocalculator

import java.math.BigDecimal

class NumberConveterPersianPartI {
    private var mResult = ""
    private var mRecentTranslation = ""

    fun convert(number: String): String {
        var numberVar = number

        if (BigDecimal(numberVar).compareTo(BigDecimal.ZERO) == 0) return "صفر "

        val remainder = numberVar.length % 3
        val zerosToBeAdded = 3 - remainder
        if (remainder != 0) {
            repeat(zerosToBeAdded) {
                numberVar = "0$numberVar"
            }
        }

        var counter = (numberVar.length / 3) - 1
        var index = 0
        while (index < numberVar.length) {
            val test = numberVar.substring(index, index + 3)
            val workingNumber = test.toInt()
            tarjome3tayi(workingNumber, counter)
            index += 3
            counter--
        }

        // remove extra spaces!
        return mResult.replace("^\\s+".toRegex(), "").replace("\\b\\s{2,}\\b".toRegex(), " ")
    }

    fun tarjome3tayi(input: Int, partNumber: Int): String {
        val translation = when (input) {
            0 -> ""
            else -> convertLessThanOneThousand(input) + orderName(partNumber)
        }
        if (mRecentTranslation != "" && translation != "") {
            mResult += " و "
        }
        mResult += translation
        if (translation != "") {
            mRecentTranslation = translation
        }
        return mResult
    }

    companion object {
        private val subTwentiesumNames = arrayOf(
            "",
            "یک ",
            "دو ",
            "سه ",
            "چهار ",
            "پنج ",
            "شش ",
            "هفت ",
            "هشت ",
            "نه ",
            "ده ",
            "یازده ",
            "دوازده ",
            "سیزده ",
            "چهارده ",
            "پانزده ",
            "شانزده ",
            "هفده ",
            "هیجده ",
            "نوزده ",
        )

        private val tensNames = arrayOf(
            "",
            "ده ",
            "بیست ",
            "سی ",
            "چهل ",
            "پنجاه ",
            "شصت ",
            "هفتاد ",
            "هشتاد ",
            "نود ",
        )

        private val hundredsNames = arrayOf(
            "",
            "صد ",
            "دویست ",
            "سیصد ",
            "چهارصد ",
            "پانصد ",
            "ششصد ",
            "هفتصد ",
            "هشتصد ",
            "نهصد ",
        )

        private fun convertLessThanOneThousand(number: Int): String {
            val hundreds = hundredsNames[number / 100]

            return if (number % 100 < 20) {
                val onesAndTens = subTwentiesumNames[number % 100]
                if (hundreds != "" && onesAndTens != "") {
                    hundreds + "و " + onesAndTens
                } else {
                    hundreds + onesAndTens
                }
            } else {
                var hundredsVar = hundreds
                var tens = tensNames[(number % 100) / 10]
                val ones = subTwentiesumNames[number % 10]

                if (hundredsVar != "") {
                    if (tens != "" || ones != "") {
                        hundredsVar += "و "
                    }
                }
                if (tens != "" && ones != "") {
                    tens += "و "
                }
                hundredsVar + tens + ones
            }
        }

        @JvmStatic
        fun orderName(part: Int): String {
            return when (part) {
                0 -> ""
                1 -> "هزار "
                2 -> "میلیون "
                3 -> "میلیارد "
                4 -> "بیلیون "
                5 -> "بیلیارد "
                6 -> "تریلیون "
                7 -> "تریلیارد "
                8 -> "کوآدریلیون "
                9 -> "کادریلیارد "
                10 -> "کوینتیلیون "
                11 -> "کوانتینیارد "
                12 -> "سکستیلیون "
                13 -> "سکستیلیارد "
                14 -> "سپتیلیون "
                15 -> "سپتیلیارد "
                16 -> "اکتیلیون "
                17 -> "اکتیلیارد "
                18 -> "نانیلیون "
                19 -> "نانیلیارد "
                20 -> "دسیلیون "
                21 -> "دسیلیارد "
                else -> ""
            }
        }
    }
}
