package com.sepidsa.fortytwocalculator;

import java.math.BigDecimal;

public class NumberConveterPersianPartI {
     String mResult="";
    String mRecentTranslation="";
  static String[] subTwentiesumNames = {
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
    };
    static String[] tensNames = {
            "",
            "ده ",
           "بیست ",
           "سی ",
            "چهل ",
           "پنجاه ",
          "شصت ",
            "هفتاد ",
           "هشتاد ",
            "نود "
    };
   static String[] hundredsNames = {
           "",
           "صد ",
           "دویست ",
           "سیصد ",
           "چهارصد ",
           "پانصد ",
           "ششصد ",
           "هفتصد ",
           "هشتصد ",
           "نهصد "
    };

    public NumberConveterPersianPartI() {

    }

    static String convertLessThanOneThousand(int number) {
        String result = "";

        String hundreds = hundredsNames[number / 100];

        if (number % 100 < 20){
            String onesAndTens = subTwentiesumNames[number % 100];
            if(hundreds != "" && onesAndTens != "" ){
                result = hundreds + "و " +  onesAndTens;
            }else {
                result = hundreds + onesAndTens;
            }
        }
        else {

            String  tens = tensNames[ (number%100) / 10] ;
            String  ones = subTwentiesumNames[number % 10];

            if(hundreds!=""){
                if(tens !="" | ones!=""){
                    hundreds += "و ";
                }
            }
            if(tens != "" && ones!=""){
                tens += "و ";
            }
            result = hundreds+ tens + ones ;
        }


        return result;
    }



     String convert(String number) {

         if (new BigDecimal(number).compareTo(BigDecimal.ZERO) == 0) { return "صفر "; }

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
             tarjome3tayi(workingNumber, counter);
             index +=3;
         }

        // remove extra spaces!
        return mResult.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
    }

   public String tarjome3tayi(int input, int partNumber){

        String Translation;
        switch (input) {
            case 0:
                Translation = "";
                break;

            default :
                Translation = convertLessThanOneThousand(input)
                        + orderName(partNumber) ;
        }
        if ( mRecentTranslation != "" && Translation!= "") {
            mResult += " و ";
        }
        mResult =  mResult + Translation;
       if(Translation!= ""){
        mRecentTranslation = Translation;
       }
        return  mResult;

    }


    public static String orderName(int part)
    {
        String output = "";
        switch (part)
        {
            case 0:
                output = "";
                break;
            case 1:
                output = "هزار ";
                break;
            case 2:
                output = "میلیون ";
                break;
            case 3:
                output = "میلیارد ";
                break;
            case 4:
                output = "بیلیون ";
                break;
            case 5:
                output = "بیلیارد ";
                break;
            case 6:
                output = "تریلیون ";
                break;
            case 7:
                output = "تریلیارد ";
                break;

            case 8:
                output = "کوآدریلیون ";
                break;

            case 9:
                output = "کادریلیارد ";
                break;


            case 10:
                output = "کوینتیلیون ";
                break;

            case 11:
                output = "کوانتینیارد ";
                break;

            case 12:
                output = "سکستیلیون ";
                break;

            case 13:
                output = "سکستیلیارد ";
                break;


            case 14:
                output = "سپتیلیون ";
                break;

            case 15:
                output = "سپتیلیارد ";
                break;


            case 16:
                output = "اکتیلیون ";
                break;


            case 17:
                output = "اکتیلیارد ";
                break;

            case 18:
                output = "نانیلیون ";
                break;

            case 19:
                output = "نانیلیارد ";
                break;


            case 20:
                output = "دسیلیون ";
                break;

            case 21:
                output = "دسیلیارد ";
                break;




            default:
                break;
        }
        return output;
    }

}

