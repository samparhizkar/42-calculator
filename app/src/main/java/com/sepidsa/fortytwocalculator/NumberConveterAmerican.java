package com.sepidsa.fortytwocalculator;


import java.math.BigDecimal;

public class NumberConveterAmerican {

    private static final String[] tensNames = {
            "",
            " ten",
            " twenty",
            " thirty",
            " forty",
            " fifty",
            " sixty",
            " seventy",
            " eighty",
            " ninety"
    };

    private static final String[] subTwentiesumNames = {
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
            " nineteen"
    };

    private static final String[] orderName = {
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
            " octodecillion" ,
            " novemdecillion",
            " vigintillion",
            " quindecillion",
            " sexdecillion",
            " septendecillion",
    };




    public NumberConveterAmerican() {


    }


    static String convertLessThanOneThousand(int number) {
        String soFar = "";
        soFar = subTwentiesumNames[number / 100];
        if(soFar != ""){
            soFar+= " hundred ";
        }
        if (number % 100 < 20){
            soFar += subTwentiesumNames[number % 100];
        }
        else {
            soFar += tensNames[ (number%100) / 10] ;
            soFar += subTwentiesumNames[number % 10];
        }

        return soFar;
    }

    public  static String convert(String number) {

        String result = "";
        if (new BigDecimal(number).compareTo(BigDecimal.ZERO) == 0) { return " zero "; }



        // pad with "0"

        int remainder = number.length() %3;
        int zerosToBeAdded = 3 - remainder;
        if(remainder !=0) {
            for (int index = 0; index < zerosToBeAdded; index++) {
                number = "0" + number;
            }
        }


        int counter = (number.length() /3)-1;
        for ( int index = 0 ; index < number.length(); counter -- ){
            String  test = number.substring(index,index+3);
            int workingNumber = Integer.parseInt(test);
          result=  tarjome3tayi(workingNumber, counter,result);
            index +=3;
        }

        // remove extra spaces!
        return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
    }

    public static String tarjome3tayi(int input, int partNumber, String _result){

        String Translation;
        switch (input) {
            case 0:
                Translation = "";
                break;

            default :
                Translation = convertLessThanOneThousand(input)
                        + orderName[partNumber] ;
        }

        _result =  _result + Translation;
        return  _result;

    }


}


/*

--Code for Dollars and Cents


String phrase = "12345.67" ;
Float num = new Float( phrase ) ;
int dollars = (int)Math.floor( num ) ;
int cent = (int)Math.floor( ( num - dollars ) * 100.0f ) ;

String s = "$ " + EnglishNumberToWords.convert( dollars ) + " and "
        + EnglishNumberToWords.convert( cent ) + " cents" ;*/



