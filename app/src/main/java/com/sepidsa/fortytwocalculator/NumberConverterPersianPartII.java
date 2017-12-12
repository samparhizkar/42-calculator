package com.sepidsa.fortytwocalculator;

/**
 * Created by ehsanparhizkar on 7/16/14.
 */
public class NumberConverterPersianPartII {


    public static String convert(String number)
    {
        String initial_translation = new NumberConveterPersianPartI() .convert(number);
        switch (number.length()){
            case 0 :
                return "";
            case 1 :
                return  initial_translation + "دهم" ;
            case 2 :
                return  initial_translation + "صدم" ;
            case 3 :
                return  initial_translation + "هزارم" ;
            case 4 :
                return  initial_translation + "ده هزارم" ;
            case 5 :
                return  initial_translation + "صد هزارم" ;
            case 6 :
                return initial_translation +"میلیونم" ;
            case 7 :
                return "" ;


        }
        return "";
    }

}
