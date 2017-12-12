package com.sepidsa.fortytwocalculator;

public class NumberConveterAmericanPartII {


    public static String convert(String number)
    {

    String result = " point";

       for(int index = 0; index <number.length(); index++){
           result += ones[Integer.valueOf(number.charAt(index)+"")];

        }
        return result;
    }


    private static final String[] ones = {
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

    };



}


