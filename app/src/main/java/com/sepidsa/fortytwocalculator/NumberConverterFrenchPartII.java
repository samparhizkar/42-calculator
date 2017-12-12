


package com.sepidsa.fortytwocalculator;

public class NumberConverterFrenchPartII {


    public static String convert(String number)
    {

        String result = " virgule";

        for(int index = 0; index <number.length(); index++){
            result += ones[Integer.valueOf(number.charAt(index)+"")];

        }
        return result;
    }


    private static final String[] ones = {
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
            " dix"

    };



}



