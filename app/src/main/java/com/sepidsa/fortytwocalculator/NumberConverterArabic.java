package com.sepidsa.fortytwocalculator;

import java.math.BigDecimal;

public class NumberConverterArabic
{
	/** Group Levels: 987,654,321.234
	 234 : Group Level -1
	 321 : Group Level 0
	 654 : Group Level 1
	 987 : Group Level 2
	*/

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Varaibles & Properties

	/** 
	 integer part
	*/
	private BigDecimal _intergerValue;

	/** 
	 Decimal Part
	*/
	private String _decimalValue = "";

	/** 
	 Number to be converted
	*/
	private BigDecimal Number = BigDecimal.ZERO;
	public final BigDecimal getNumber()
	{
		return Number;
	}
	public final void setNumber(BigDecimal value)
	{
		Number = value;
	}


	public NumberConverterArabic(BigDecimal number)
	{
		InitializeClass(number, "", "only.", "فقط", "لا غير");
	}




	public NumberConverterArabic(BigDecimal number, String englishPrefixText, String englishSuffixText, String arabicPrefixText, String arabicSuffixText)
	{
		InitializeClass(number,englishPrefixText, englishSuffixText, arabicPrefixText, arabicSuffixText);
	}


	private void InitializeClass(BigDecimal number, String englishPrefixText, String englishSuffixText, String arabicPrefixText, String arabicSuffixText)
	{
		setNumber(number);

		ExtractIntegerAndDecimalParts();
	}



	/**
	 Eextract Interger and Decimal parts
	*/
	private void ExtractIntegerAndDecimalParts()
	{
		String[] splits = getNumber().toString().split("[.]", -1);


		_intergerValue = new BigDecimal(splits[0]);

		if (splits.length > 1)
		{
//			_decimalValue = Integer.parseInt(GetDecimalValue(splits[1]));
			_decimalValue = splits[1];
		}
	}


	private static String[] englishOnes = new String[] {"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};

	private static String[] englishTens = new String[] {"Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

	private static String[] englishGroup = new String[] {"Hundred", "Thousand", "Million", "Billion", "Trillion", "Quadrillion", "Quintillion", "Sextillian", "Septillion", "Octillion", "Nonillion", "Decillion", "Undecillion", "Duodecillion", "Tredecillion", "Quattuordecillion", "Quindecillion", "Sexdecillion", "Septendecillion", "Octodecillion", "Novemdecillion", "Vigintillion", "Unvigintillion", "Duovigintillion", "10^72", "10^75", "10^78", "10^81", "10^84", "10^87", "Vigintinonillion", "10^93", "10^96", "Duotrigintillion", "Trestrigintillion"};
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	/**
	 Process a group of 3 digits

	 @param groupNumber The group number to process
	 @return
	*/
	private String ProcessGroup(int groupNumber)
	{
		int tens = groupNumber % 100;

		int hundreds = groupNumber / 100;

		String retVal = "";

		if (hundreds > 0)
		{
			retVal = String.format("%1$s %2$s", englishOnes[hundreds], englishGroup[0]);
		}
		if (tens > 0)
		{
			if (tens < 20)
			{
				retVal += ((!retVal.equals("")) ? " " : "") + englishOnes[tens];
			}
			else
			{
				int ones = tens % 10;

				tens = (tens / 10) - 2; // 20's offset

				retVal += ((!retVal.equals("")) ? " " : "") + englishTens[tens];

				if (ones > 0)
				{
					retVal += ((!retVal.equals("")) ? " " : "") + englishOnes[ones];
				}
			}
		}

		return retVal;
	}



	private static String[] arabicOnes = new String[] {"", "واحد", "اثنان", "ثلاثة", "أربعة", "خمسة", "ستة", "سبعة", "ثمانية", "تسعة", "عشرة", "أحد عشر", "اثنا عشر", "ثلاثة عشر", "أربعة عشر", "خمسة عشر", "ستة عشر", "سبعة عشر", "ثمانية عشر", "تسعة عشر"};

	private static String[] arabicFeminineOnes = new String[] {"", "إحدى", "اثنتان", "ثلاث", "أربع", "خمس", "ست", "سبع", "ثمان", "تسع", "عشر", "إحدى عشرة", "اثنتا عشرة", "ثلاث عشرة", "أربع عشرة", "خمس عشرة", "ست عشرة", "سبع عشرة", "ثماني عشرة", "تسع عشرة"};

	private static String[] arabicTens = new String[] {"عشرون", "ثلاثون", "أربعون", "خمسون", "ستون", "سبعون", "ثمانون", "تسعون"};

	private static String[] arabicHundreds = new String[] {"", "مائة", "مئتان", "ثلاثمائة", "أربعمائة", "خمسمائة", "ستمائة", "سبعمائة", "ثمانمائة","تسعمائة"};

	private static String[] arabicAppendedTwos = new String[] {"مئتا", "ألفا", "مليونا", "مليارا", "تريليونا", "كوادريليونا", "كوينتليونا", "سكستيليونا"};

	private static String[] arabicTwos = new String[] {"مئتان", "ألفان", "مليونان", "ملياران", "تريليونان", "كوادريليونان", "كوينتليونان", "سكستيليونان"};

	private static String[] arabicGroup = new String[] {"مائة", "ألف", "مليون", "مليار", "تريليون", "كوادريليون", "كوينتليون", "سكستيليون"};

	private static String[] arabicAppendedGroup = new String[] {"", "ألفاً", "مليوناً", "ملياراً", "تريليوناً", "كوادريليوناً", "كوينتليوناً", "سكستيليوناً"};

	private static String[] arabicPluralGroups = new String[] {"", "آلاف", "ملايين", "مليارات", "تريليونات", "كوادريليونات", "كوينتليونات", "سكستيليونات"};
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	/**
	 Get Feminine Status of one digit

	 @param digit The Digit to check its Feminine status
	 @param groupLevel Group Level
	 @return
	*/
	private String GetDigitFeminineStatus(int digit, int groupLevel)
	{
//		if (groupLevel == -1)
//		{ // if it is in the decimal part
//			if (getCurrency().getIsCurrencyPartNameFeminine())
//			{
//				return arabicFeminineOnes[digit]; // use feminine field
//			}
//			else
//			{
//				return arabicOnes[digit];
//			}
//		}
//		else
//		{
//			if (groupLevel == 0)
//			{
//				if (getCurrency().getIsCurrencyNameFeminine())
//				{
//					return arabicFeminineOnes[digit]; // use feminine field
//				}
//				else
//				{
//					return arabicOnes[digit];
//				}
//			}
//			else
//			{
//				return arabicOnes[digit];
//			}
//		}
        return arabicOnes[digit];

    }

	/**
	 Process a group of 3 digits

	 @param groupNumber The group number to process
	 @return
	*/
	private String ProcessArabicGroup(int groupNumber, int groupLevel, BigDecimal remainingNumber)
	{
		int tens = groupNumber % 100;

		int hundreds = groupNumber / 100;

		String retVal = "";

		if (hundreds > 0)
		{
			if (tens == 0 && hundreds == 2) // حالة المضاف
			{
				retVal = String.format("%1$s", arabicAppendedTwos[0]);
			}
			else //  الحالة العادية
			{
				retVal = String.format("%1$s", arabicHundreds[hundreds]);
			}
		}

		if (tens > 0)
		{
			if (tens < 20)
			{ // if we are processing under 20 numbers
				if (tens == 2 && hundreds == 0 && groupLevel > 0)
				{ // This is special case for number 2 when it comes alone in the group
					if ((_intergerValue.compareTo(new BigDecimal("2000"))== 0 ||
                            _intergerValue.compareTo(new BigDecimal("2000000")) == 0 ||
                            _intergerValue.compareTo(new BigDecimal("2000000000"))== 0 ||
                            _intergerValue.compareTo(new BigDecimal("2000000000000"))== 0  ||
                            _intergerValue.compareTo(new BigDecimal("2000000000000000000"))== 0 ) )
					{
						retVal = String.format("%1$s", arabicAppendedTwos[groupLevel]); // في حالة الاضافة
					}
					else
					{
						retVal = String.format("%1$s", arabicTwos[groupLevel]); //  في حالة الافراد
					}
				}
				else
				{ // General case
					if (!retVal.equals(""))
					{
						retVal += " و ";
					}

					if (tens == 1 && groupLevel > 0 && hundreds == 0)
					{
						retVal += " ";
					}
					else
					{
						if ((tens == 1 || tens == 2) && (groupLevel == 0 || groupLevel == -1) && hundreds == 0 && remainingNumber.compareTo(BigDecimal.ZERO) == 0)
						{
							retVal += ""; // Special case for 1 and 2 numbers like: ليرة سورية و ليرتان سوريتان
						}
						else
						{
							retVal += GetDigitFeminineStatus(tens, groupLevel); // Get Feminine status for this digit
						}
					}
				}
			}
			else
			{
				int ones = tens % 10;
				tens = (tens / 10) - 2; // 20's offset

				if (ones > 0)
				{
					if (!retVal.equals(""))
					{
						retVal += " و ";
					}

					// Get Feminine status for this digit
					retVal += GetDigitFeminineStatus(ones, groupLevel);
				}

				if (!retVal.equals(""))
				{
					retVal += " و ";
				}

				// Get Tens text
				retVal += arabicTens[tens];
			}
		}

		return retVal;
	}

	/**
	 Convert stored number to words using selected currency

	 @return
	*/
	public final String ConvertToArabic()
	{
		BigDecimal tempNumber = getNumber();

		if (tempNumber.compareTo(BigDecimal.ZERO) == 0)
		{
			return "صفر";
		}

		// Get Text for the decimal part
//		String decimalString = ProcessArabicGroup(_decimalValue, -1, BigDecimal.ZERO);

		String retVal = "";
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: Byte group = 0;
		byte group = 0;
		while (tempNumber.compareTo(BigDecimal.ONE) >= 0)
		{
			// seperate number into groups
			int numberToProcess = tempNumber.remainder(new BigDecimal(1000)).intValue();

			tempNumber = tempNumber.divide(new BigDecimal(1000));

			// convert group into its text
			String groupDescription = ProcessArabicGroup(numberToProcess, group, tempNumber);

			if (!groupDescription.equals(""))
			{ // here we add the new converted group to the previous concatenated text
				if (group > 0)
				{
					if (!retVal.equals(""))
					{
						retVal = String.format("%1$s %2$s", "و", retVal);
					}

					if (numberToProcess != 2)
					{
						if (numberToProcess % 100 != 1)
						{
							if (numberToProcess >= 3 && numberToProcess <= 10) // for numbers between 3 and 9 we use plural name
							{
								retVal = String.format("%1$s %2$s", arabicPluralGroups[group], retVal);
							}
							else
							{
								if (!retVal.equals("")) // use appending case
								{
									retVal = String.format("%1$s %2$s", arabicAppendedGroup[group], retVal);
								}
								else
								{
									retVal = String.format("%1$s %2$s", arabicGroup[group], retVal); // use normal case
								}
							}
						}
						else
						{
							retVal = String.format("%1$s %2$s", arabicGroup[group], retVal); // use normal case
						}
					}
				}

				retVal = String.format("%1$s %2$s", groupDescription, retVal);
			}

			group++;
		}

		String formattedNumber = "";
	  //  formattedNumber += (ArabicPrefixText != String.Empty) ? String.Format("{0} ", ArabicPrefixText) : String.Empty;
		formattedNumber += (!retVal.equals("")) ? retVal : "صفر ";
		//if (_intergerValue != 0)
		//{ // here we add currency name depending on _intergerValue : 1 ,2 , 3--->10 , 11--->99
		//    int remaining100 = (int)(_intergerValue % 100);

		//    if (remaining100 == 0)
		//        formattedNumber += Currency.Arabic1CurrencyName;
		//    else
		//        if (remaining100 == 1)
		//            formattedNumber += Currency.Arabic1CurrencyName;
		//        else
		//            if (remaining100 == 2)
		//            {
		//                if (_intergerValue == 2)
		//                    formattedNumber += Currency.Arabic2CurrencyName;
		//                else
		//                    formattedNumber += Currency.Arabic1CurrencyName;
		//            }
		//            else
		//                if (remaining100 >= 3 && remaining100 <= 10)
		//                    formattedNumber += Currency.Arabic310CurrencyName;
		//                else
		//                    if (remaining100 >= 11 && remaining100 <= 99)
		//                        formattedNumber += Currency.Arabic1199CurrencyName;
		//}
		formattedNumber += (_decimalValue != "") ?  NumberConverterArabicPartII.convert(_decimalValue) :""    ;
		//if (_decimalValue != 0)
		//{ // here we add currency part name depending on _intergerValue : 1 ,2 , 3--->10 , 11--->99
		//    formattedNumber += " ";

		//    int remaining100 = (int)(_decimalValue % 100);

		//    if (remaining100 == 0)
		//        formattedNumber += Currency.Arabic1CurrencyPartName;
		//    else
		//        if (remaining100 == 1)
		//            formattedNumber += Currency.Arabic1CurrencyPartName;
		//        else
		//            if (remaining100 == 2)
		//                formattedNumber += Currency.Arabic2CurrencyPartName;
		//            else
		//                if (remaining100 >= 3 && remaining100 <= 10)
		//                    formattedNumber += Currency.Arabic310CurrencyPartName;
		//                else
		//                    if (remaining100 >= 11 && remaining100 <= 99)
		//                        formattedNumber += Currency.Arabic1199CurrencyPartName;
		//}
		//formattedNumber += (ArabicSuffixText != String.Empty) ? String.Format(" {0}", ArabicSuffixText) : String.Empty;


//		formattedNumber += (!getArabicSuffixText().equals("")) ? String.format(" %1$s", getArabicSuffixText()) : "";

		return formattedNumber;
	}
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion
}