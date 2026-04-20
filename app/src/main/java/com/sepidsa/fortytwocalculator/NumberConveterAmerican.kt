package com.sepidsa.fortytwocalculator

import java.math.BigDecimal

class NumberConveterAmerican {
    companion object {
        private val tensNames = arrayOf(
            "",
            " ten",
            " twenty",
            " thirty",
            " forty",
            " fifty",
            " sixty",
            " seventy",
            " eighty",
            " ninety",
        )

        private val subTwentiesumNames = arrayOf(
            "",
            " one",
            " two",
            " three",
            " four",
            " five",
            " six",
            " seven",
            " eight",
            " nine",
            " ten",
            " eleven",
            " twelve",
            " thirteen",
            " fourteen",
            " fifteen",
            " sixteen",
            " seventeen",
            " eighteen",
            " nineteen",
        )

        private val orderName = arrayOf(
            "",
            " thousand",
            " million",
            " billion",
            " trillion",
            " quadrillion",
            " quintillion",
            " sextillion",
            " septillion",
            " octillion",
            " nonillion",
            " decillion",
            " undecillion",
            " duodecillion",
            " tredecillion",
            " quattuordecillion",
            " quindecillion",
            " sexdecillion",
            " septendecillion",
            " octodecillion",
            " novemdecillion",
            " vigintillion",
            " quindecillion",
            " sexdecillion",
            " septendecillion",
        )

        private fun convertLessThanOneThousand(number: Int): String {
            var soFar = ""
            soFar = subTwentiesumNames[number / 100]
            if (soFar != "") {
                soFar += " hundred "
            }
            soFar += if (number % 100 < 20) {
                subTwentiesumNames[number % 100]
            } else {
                tensNames[(number % 100) / 10] + subTwentiesumNames[number % 10]
            }
            return soFar
        }

        @JvmStatic
        fun convert(number: String): String {
            var numberVar = number
            var result = ""
            if (BigDecimal(numberVar).compareTo(BigDecimal.ZERO) == 0) return " zero "

            // pad with "0"
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
                result = tarjome3tayi(workingNumber, counter, result)
                index += 3
                counter--
            }

            // remove extra spaces!
            return result.replace("^\\s+".toRegex(), "").replace("\\b\\s{2,}\\b".toRegex(), " ")
        }

        @JvmStatic
        fun tarjome3tayi(input: Int, partNumber: Int, _result: String): String {
            val translation = when (input) {
                0 -> ""
                else -> convertLessThanOneThousand(input) + orderName[partNumber]
            }
            return _result + translation
        }
    }
}
