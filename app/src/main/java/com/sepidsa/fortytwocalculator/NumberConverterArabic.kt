package com.sepidsa.fortytwocalculator

import java.math.BigDecimal

class NumberConverterArabic(number: BigDecimal) {
    /**
     * integer part
     */
    private var _intergerValue: BigDecimal? = null

    /**
     * Decimal Part
     */
    private var _decimalValue: String = ""

    /**
     * Number to be converted
     */
    private var number: BigDecimal = BigDecimal.ZERO

    fun getNumber(): BigDecimal = number

    fun setNumber(value: BigDecimal) {
        number = value
    }

    init {
        initializeClass(number, "", "only.", "فقط", "لا غير")
    }

    constructor(
        number: BigDecimal,
        englishPrefixText: String,
        englishSuffixText: String,
        arabicPrefixText: String,
        arabicSuffixText: String,
    ) : this(number) {
        initializeClass(number, englishPrefixText, englishSuffixText, arabicPrefixText, arabicSuffixText)
    }

    private fun initializeClass(
        number: BigDecimal,
        englishPrefixText: String,
        englishSuffixText: String,
        arabicPrefixText: String,
        arabicSuffixText: String,
    ) {
        setNumber(number)
        extractIntegerAndDecimalParts()
    }

    /**
     * Extract Integer and Decimal parts
     */
    private fun extractIntegerAndDecimalParts() {
        val splits = getNumber().toString().split(".")
        _intergerValue = BigDecimal(splits[0])

        if (splits.size > 1) {
            _decimalValue = splits[1]
        }
    }

    private fun processGroup(groupNumber: Int): String {
        var tens = groupNumber % 100
        val hundreds = groupNumber / 100
        var retVal = ""

        if (hundreds > 0) {
            retVal = String.format("%1\$s %2\$s", englishOnes[hundreds], englishGroup[0])
        }
        if (tens > 0) {
            if (tens < 20) {
                retVal += (if (retVal != "") " " else "") + englishOnes[tens]
            } else {
                val ones = tens % 10
                tens = (tens / 10) - 2 // 20's offset
                retVal += (if (retVal != "") " " else "") + englishTens[tens]
                if (ones > 0) {
                    retVal += (if (retVal != "") " " else "") + englishOnes[ones]
                }
            }
        }
        return retVal
    }

    private fun getDigitFeminineStatus(digit: Int, groupLevel: Int): String {
        return arabicOnes[digit]
    }

    private fun processArabicGroup(groupNumber: Int, groupLevel: Int, remainingNumber: BigDecimal): String {
        var tens = groupNumber % 100
        val hundreds = groupNumber / 100
        var retVal = ""

        if (hundreds > 0) {
            retVal = if (tens == 0 && hundreds == 2) {
                String.format("%1\$s", arabicAppendedTwos[0])
            } else {
                String.format("%1\$s", arabicHundreds[hundreds])
            }
        }

        if (tens > 0) {
            if (tens < 20) {
                if (tens == 2 && hundreds == 0 && groupLevel > 0) {
                    retVal =
                        if (
                            _intergerValue!!.compareTo(BigDecimal("2000")) == 0 ||
                                _intergerValue!!.compareTo(BigDecimal("2000000")) == 0 ||
                                _intergerValue!!.compareTo(BigDecimal("2000000000")) == 0 ||
                                _intergerValue!!.compareTo(BigDecimal("2000000000000")) == 0 ||
                                _intergerValue!!.compareTo(BigDecimal("2000000000000000000")) == 0
                        ) {
                            String.format("%1\$s", arabicAppendedTwos[groupLevel])
                        } else {
                            String.format("%1\$s", arabicTwos[groupLevel])
                        }
                } else {
                    if (retVal != "") {
                        retVal += " و "
                    }
                    retVal += if (tens == 1 && groupLevel > 0 && hundreds == 0) {
                        " "
                    } else {
                        if ((tens == 1 || tens == 2) &&
                            (groupLevel == 0 || groupLevel == -1) &&
                            hundreds == 0 &&
                            remainingNumber.compareTo(BigDecimal.ZERO) == 0
                        ) {
                            ""
                        } else {
                            getDigitFeminineStatus(tens, groupLevel)
                        }
                    }
                }
            } else {
                val ones = tens % 10
                tens = (tens / 10) - 2 // 20's offset

                if (ones > 0) {
                    if (retVal != "") {
                        retVal += " و "
                    }
                    retVal += getDigitFeminineStatus(ones, groupLevel)
                }

                if (retVal != "") {
                    retVal += " و "
                }
                retVal += arabicTens[tens]
            }
        }

        return retVal
    }

    fun convertToArabic(): String {
        var tempNumber = getNumber()

        if (tempNumber.compareTo(BigDecimal.ZERO) == 0) {
            return "صفر"
        }

        var retVal = ""
        var group: Byte = 0
        while (tempNumber.compareTo(BigDecimal.ONE) >= 0) {
            val numberToProcess = tempNumber.remainder(BigDecimal(1000)).toInt()
            tempNumber = tempNumber.divide(BigDecimal(1000))

            val groupDescription = processArabicGroup(numberToProcess, group.toInt(), tempNumber)

            if (groupDescription != "") {
                if (group.toInt() > 0) {
                    if (retVal != "") {
                        retVal = String.format("%1\$s %2\$s", "و", retVal)
                    }

                    if (numberToProcess != 2) {
                        if (numberToProcess % 100 != 1) {
                            retVal =
                                if (numberToProcess in 3..10) {
                                    String.format("%1\$s %2\$s", arabicPluralGroups[group.toInt()], retVal)
                                } else {
                                    if (retVal != "") {
                                        String.format("%1\$s %2\$s", arabicAppendedGroup[group.toInt()], retVal)
                                    } else {
                                        String.format("%1\$s %2\$s", arabicGroup[group.toInt()], retVal)
                                    }
                                }
                        } else {
                            retVal = String.format("%1\$s %2\$s", arabicGroup[group.toInt()], retVal)
                        }
                    }
                }
                retVal = String.format("%1\$s %2\$s", groupDescription, retVal)
            }

            group++
        }

        var formattedNumber = ""
        formattedNumber += if (retVal != "") retVal else "صفر "
        formattedNumber += if (_decimalValue != "") NumberConverterArabicPartII.convert(_decimalValue) else ""
        return formattedNumber
    }

    companion object {
        private val englishOnes = arrayOf(
            "Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen",
        )

        private val englishTens = arrayOf("Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety")

        private val englishGroup = arrayOf(
            "Hundred", "Thousand", "Million", "Billion", "Trillion", "Quadrillion", "Quintillion", "Sextillian",
            "Septillion", "Octillion", "Nonillion", "Decillion", "Undecillion", "Duodecillion", "Tredecillion",
            "Quattuordecillion", "Quindecillion", "Sexdecillion", "Septendecillion", "Octodecillion", "Novemdecillion",
            "Vigintillion", "Unvigintillion", "Duovigintillion", "10^72", "10^75", "10^78", "10^81", "10^84", "10^87",
            "Vigintinonillion", "10^93", "10^96", "Duotrigintillion", "Trestrigintillion",
        )

        private val arabicOnes = arrayOf(
            "", "واحد", "اثنان", "ثلاثة", "أربعة", "خمسة", "ستة", "سبعة", "ثمانية", "تسعة",
            "عشرة", "أحد عشر", "اثنا عشر", "ثلاثة عشر", "أربعة عشر", "خمسة عشر", "ستة عشر", "سبعة عشر", "ثمانية عشر", "تسعة عشر",
        )

        private val arabicFeminineOnes = arrayOf(
            "", "إحدى", "اثنتان", "ثلاث", "أربع", "خمس", "ست", "سبع", "ثمان", "تسع",
            "عشر", "إحدى عشرة", "اثنتا عشرة", "ثلاث عشرة", "أربع عشرة", "خمس عشرة", "ست عشرة", "سبع عشرة", "ثماني عشرة", "تسع عشرة",
        )

        private val arabicTens = arrayOf("عشرون", "ثلاثون", "أربعون", "خمسون", "ستون", "سبعون", "ثمانون", "تسعون")

        private val arabicHundreds = arrayOf("", "مائة", "مئتان", "ثلاثمائة", "أربعمائة", "خمسمائة", "ستمائة", "سبعمائة", "ثمانمائة", "تسعمائة")

        private val arabicAppendedTwos = arrayOf("مئتا", "ألفا", "مليونا", "مليارا", "تريليونا", "كوادريليونا", "كوينتليونا", "سكستيليونا")

        private val arabicTwos = arrayOf("مئتان", "ألفان", "مليونان", "ملياران", "تريليونان", "كوادريليونان", "كوينتليونان", "سكستيليونان")

        private val arabicGroup = arrayOf("مائة", "ألف", "مليون", "مليار", "تريليون", "كوادريليون", "كوينتليون", "سكستيليون")

        private val arabicAppendedGroup = arrayOf("", "ألفاً", "مليوناً", "ملياراً", "تريليوناً", "كوادريليوناً", "كوينتليوناً", "سكستيليوناً")

        private val arabicPluralGroups = arrayOf("", "آلاف", "ملايين", "مليارات", "تريليونات", "كوادريليونات", "كوينتليونات", "سكستيليونات")
    }
}
