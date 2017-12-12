package com.sepidsa.fortytwocalculator;

import java.math.BigDecimal;

public class NumberConverterArabicPartII
{

    public static String convert(String number)
    {

        if (new BigDecimal(number).compareTo(BigDecimal.ZERO) == 0) { return ""; }

        String result = " فاصل ";

        for(int index = 0; index <number.length(); index++){
            result += ones[Integer.valueOf(number.charAt(index)+"")];

        }
        return result;
    }




    private static String[] ones = new String[] {"صفر ", "واحد ", "اثنان ", "ثلاث ة", "أربعة ", "خمسة ", "ستة ", "سبعة ", "ثمانية ", "تسعة "};


}