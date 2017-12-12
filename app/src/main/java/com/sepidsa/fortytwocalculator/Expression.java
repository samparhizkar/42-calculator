package com.sepidsa.fortytwocalculator;

import android.content.Context;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.StringTokenizer;

public class  Expression{
    private final Context mContext;
    /*
        * Strings used for storing expression.
        */
    private boolean PERCENT_FLAG = false;
    private boolean mDeg_flag;
    public String s, x;
    public BigDecimal last;
    private String mTest;
    /*
    * Term evaluator for number literals.
    */

    BigDecimal term(){
        BigDecimal ans = null;
        StringBuffer temp = new StringBuffer("");

        boolean neg = false;
        if( s.charAt(0) == '−' || s.charAt(0) == '-' ){
            neg = true;
            s = s.substring( 1 );
        }

        if( s.length() > 0 && s.charAt( 0 ) == 'e') {
            s = s.substring(1);
            ans =new BigDecimal( Math.E);
        } else
        if( s.length() > 0 && s.charAt( 0 ) == 'π'){
            s = s.substring( 1 );
            ans =new BigDecimal( Math.PI);

        }else if( s.length() > 0 && s.indexOf("rand") == 0){
            s = s.substring( 4 );
            ans =new BigDecimal( Math.random());

        }else{
            while( s.length() > 0 && Character.isDigit( s.charAt( 0 ) ) ){
                temp.append(Integer.parseInt( "" + s.charAt( 0 ) ));
                s = s.substring( 1 );
            }
            if( s.length() > 0 && s.charAt( 0 ) == '.' ){
                temp.append( '.' );
                s = s.substring( 1 );
                while( s.length() > 0 && Character.isDigit( s.charAt( 0 ) ) ){
                    temp.append(Integer.parseInt( "" + s.charAt( 0 ) ));
                    s = s.substring( 1 );
                }
            }
            if( s.length() > 0 &&  s.charAt(0) == 'E' ){
                temp.append( 'E' );
                s = s.substring( 1 );
                temp.append( s.charAt( 0 ) );
                s = s.substring( 1 );
                while( s.length() > 0 && Character.isDigit( s.charAt( 0 ) ) ){
                    temp.append(Integer.parseInt( "" + s.charAt( 0 ) ));
                    s = s.substring( 1 );

                }
            }
           /* if(s.length() > 0 && (s.charAt(0) == ')')){
                ans = (double)1;
                return ans;

            }
*/
            if(temp.equals("") == true) {
                return new BigDecimal(0);

            }else {
                ans = new BigDecimal(temp.toString());

            }
//            if(neg ){
//                if(ans != null){
//                    ans = ans.negate();
//                }else {
//                    ans = new BigDecimal(-1);
//                }
//            }
        }
        return ans;
    }




    /*public BigDecimal compute_factorial(BigDecimal n) {
        double[] fact = {1, 1, 2, 6, 24, 120, 720, 5040, 40320,
                362880, 3628800, 39916800, 479001600};
        return fact[n.intValue()];
    }
*/



    static BigDecimal compute_factorial(BigDecimal n, BigDecimal acc) {
        if (n.equals(BigDecimal.ONE)) {
            return acc;
        }
        BigDecimal lessOne = n.subtract(BigDecimal.ONE);
        return compute_factorial(lessOne, acc.multiply(lessOne));
    }



    BigDecimal paren(){
        BigDecimal ans = null;

        if(s.length()!=0)   {
            if (s.charAt(0) == '(') {
                s = s.substring(1);
                ans = add();
                if (s.length()>0 && s.charAt(0) == ')') {
                    s = s.substring(1);
                }
            }else {
                ans = term();
            }
        }
        return ans;
    }


    BigDecimal factorial(){
        BigDecimal ans = paren();


        while( s.length() > 0 ){
            if (s.indexOf(mTest) == 0) {
                s = s.substring(1);
                ans = BigDecimalUtils.intPower(ans, 2, 6);
                break;
            }
            else if (s.indexOf(mContext.getResources().getString(R.string.powerthreeTag)) == 0) {
                s = s.substring(1);
                ans = BigDecimalUtils.intPower(ans, 3, 6);
                break;
            }
            if( s.charAt( 0 ) == '!' ) {
                s = s.substring(1);
                try{
                if(ans.scale()>0 || ans.compareTo(BigDecimal.ZERO)== -1){
                        throw new NumberFormatException();
                }else {
                    ans = compute_factorial(ans.round(new MathContext(6)), ans.round(new MathContext(6)));
                }
                }finally {
                    //
                }
                //todo
            }else if ( s.charAt( 0 ) == '%'){
                PERCENT_FLAG = true;
                //s = s.substring(1);
                break;
            }else {
                break;
            }
        }
        return ans;
    }



    /*
	* Exponentiation solver.
	*/
    BigDecimal exp(){
        boolean neg = false;
        if(  s.charAt( 0 ) == '−' || s.charAt(0) == '-'  ){
            neg = true;
            s = s.substring( 1 );
            return new BigDecimal(-1);
        }
        BigDecimal ans = factorial();
        while( s.length() > 0 ){
            if( s.indexOf( "^" ) == 0 ){
                s = s.substring( 1 );
                boolean expNeg = false;
                // Checking with both types of minus charecter (they look the same here but in real life they look diff
                if( s.charAt( 0 ) == '−' || s.charAt(0) == '-' ){
                    expNeg = true;
                    s = s.substring( 1 );
                }
                BigDecimal e = factorial();

                if( ans.doubleValue() < 0 ){		// if it's negative
                    BigDecimal x = new BigDecimal("1");
                    if( Math.ceil(e.doubleValue()) == e.doubleValue() ){	// only raise to an integer
                        if( expNeg )
                            e = e.multiply(new BigDecimal("-1"));
                        if( e.doubleValue() == 0 )
                            ans= new BigDecimal("1");
                        else if( e.doubleValue() > 0 )
                            for( int i = 0; i < e.doubleValue(); i++ )
                                x = x.multiply( ans );
                        else
                            for( int i = 0; i < -e.doubleValue(); i++ )
                                x = x.divide(ans,new MathContext(6));
                        ans = x;
                    } else {
                        ans = new BigDecimal(Math.log(-1));	// otherwise make it NaN
                    }
                }

                else{
//                    ans = new BigDecimal( Math.exp( e.multiply(new BigDecimal( Math.log(ans.doubleValue()) )).doubleValue())) ;
//                    ans = ans.pow(e.intValue()) ;
//                    BigDecimal logx = BigDecimalUtils.(x) ;
//                    BigDecimal ylogx = y.multiply(logx) ;
//                    BigDecimal resul = exp(ylogx) ;
//                    ans = BigDecimalMath.pow(ans, e);
//                    BigDecimal d = ans;
//                    ans =   BigFunctions.exp( BigFunctions.ln(e,100).multiply(ans),100 );
                    BigDecimal n1 = ans;
                    BigDecimal n2 = e;
                    ans = bigdecimalPower(ans, n1, n2, expNeg);
                }
            } else
                break;
        }
        if( neg )
            ans = ans.negate();
        return ans;
    }

    private BigDecimal bigdecimalPower(BigDecimal ans, BigDecimal n1, BigDecimal n2, boolean expNeg) {
        int signOf2 = n2.signum();
        if(expNeg) ans = ans.negate();
        try {
            // Perform X^(A+B)=X^A*X^B (B = remainder)
            double dn1 = n1.doubleValue();
            // Compare the same row of digits according to context
            if (n1.doubleValue()!= dn1)
                throw new Exception(); // Cannot convert n1 to double
            n2 = n2.multiply(new BigDecimal(signOf2)); // n2 is now positive
            BigDecimal remainderOf2 = n2.remainder(BigDecimal.ONE);
            BigDecimal n2IntPart = n2.subtract(remainderOf2);
            // Calculate big part of the power using context -
            // bigger range and performance but lower accuracy
            BigDecimal intPow = n1.pow(n2IntPart.intValueExact(),
                    new MathContext(6));
            BigDecimal doublePow =
                    new BigDecimal(Math.pow(dn1, remainderOf2.doubleValue()));
            ans = intPow.multiply(doublePow);
        } catch (Exception error) {
            error.printStackTrace();
        }
        // Fix negative power
        if (expNeg) {
            ans = BigDecimal.ONE.divide(ans, 6, BigDecimal.ROUND_HALF_UP);
        }
        return ans;
    }


    /*
    * Trigonometric function solver.
    */
    BigDecimal trig(){
        BigDecimal ans = new BigDecimal("0");
        boolean found = false;
        if(s.length() > 0 ) {
            if (s.indexOf("asinh") == 0) {
                s = s.substring(5);
                ans = trig();
                double doubleAns = ans.doubleValue();
                ans = new BigDecimal(Math.log(doubleAns + Math.sqrt(Math.pow(doubleAns, 2) + 1.0)));

                found = true;
            }else if (s.indexOf("sinh") == 0) {
                s = s.substring(4);
                ans = new BigDecimal(Math.sinh(trig().doubleValue()));
                found = true;
            }else if (s.indexOf("asin") == 0) {
                s = s.substring(4);
                if (mDeg_flag) {
                    ans = new BigDecimal(Math.toDegrees(Math.asin(trig().doubleValue())));
                } else {
//                    ans = new BigDecimal(Math.toRadians(Math.asin(trig().doubleValue())));
                    ans = new BigDecimal(Math.asin(trig().doubleValue()));

                }
                found = true;
            }
            else if (s.indexOf("sin") == 0) {
                s = s.substring(3);
                if (mDeg_flag) {
                    ans = new BigDecimal(Math.sin(Math.toRadians(trig().doubleValue())));

                } else {
                    ans = new BigDecimal(Math.sin(trig().doubleValue()));
                }
                found = true;
            }
//        Math.log(x + Math.sqrt(x*x + 1.0));
            //CSC is 1/sin(X)
            else if (s.indexOf("acsch") == 0) {
                // = asinh (1/x)
                s = s.substring(5);
                ans = trig();
                double doubleAns = 1 / ans.doubleValue();
                ans = new BigDecimal(Math.log(doubleAns + Math.sqrt(Math.pow(doubleAns, 2) + 1.0)));

                found = true;


            } else if (s.indexOf("acsc") == 0) {
                s = s.substring(4);
                if (mDeg_flag) {
                    ans = new BigDecimal(Math.pow(Math.asin(Math.toDegrees(trig().doubleValue())), -1));

                    //1/asin
                } else {
                    ans = new BigDecimal(Math.pow(Math.asin(trig().doubleValue()), -1));

                }
                found = true;
            }else if (s.indexOf("csch") == 0) {
                // = 1/sinh (x)
                s = s.substring(4);
                ans = new BigDecimal(Math.pow(Math.sinh(trig().doubleValue()),-1));
                found = true;


            } else if (s.indexOf("csc") == 0) {
                s = s.substring(3);
                if (mDeg_flag) {
                    ans = new BigDecimal(Math.pow(Math.sin(trig().doubleValue()), -1));
                } else {
                    ans = new BigDecimal(Math.pow(Math.sin(Math.toRadians(trig().doubleValue())), -1));
                }
                found = true;
            }else if (s.indexOf("acosh") == 0) {
                s = s.substring(5);
                ans = trig();
                double doubleAns = ans.doubleValue();
                ans = new BigDecimal(Math.log(doubleAns + Math.sqrt(Math.pow(doubleAns, 2) - 1.0)));

                found = true;
            }

            else if (s.indexOf("acos") == 0) {
                s = s.substring(4);
                if (mDeg_flag) {
                    ans = new BigDecimal(Math.toDegrees(Math.acos(trig().doubleValue())));

                } else {
                    ans = new BigDecimal(Math.acos(trig().doubleValue()));

                }
                found = true;

            }
            else if (s.indexOf("cosh") == 0) {
                s = s.substring(4);
                ans = new BigDecimal(Math.cosh(trig().doubleValue()));
                found = true;
            }else if (s.indexOf("cos") == 0) {
                s = s.substring(3);
                if (mDeg_flag) {
                    ans = new BigDecimal(Math.cos(Math.toRadians(trig().doubleValue())));

                } else {
                    ans = new BigDecimal(Math.cos(trig().doubleValue()));
                }
                found = true;
            } else if (s.indexOf("asech") == 0) {
                s = s.substring(5);
                ans = trig();
                double doubleAns = 1 / ans.doubleValue();
                ans = new BigDecimal(Math.log(doubleAns + Math.sqrt(Math.pow(doubleAns, 2) - 1.0)));

                found = true;
            } else if (s.indexOf("asec") == 0) {
                s = s.substring(4);
                if (mDeg_flag) {
                    ans = new BigDecimal(Math.pow(Math.acos(Math.toDegrees(trig().doubleValue())), -1));

                } else {
                    ans = new BigDecimal(Math.pow(Math.acos(trig().doubleValue()), -1));

                }
                found = true;
            }  else if (s.indexOf("sech") == 0) {
                // = asinh (1/x)
                s = s.substring(4);
                ans = new BigDecimal(Math.pow(Math.cosh(trig().doubleValue()),-1));
                found = true;


            } else if (s.indexOf("sec") == 0) {
                s = s.substring(3);
                if (mDeg_flag) {
                    ans = new BigDecimal(Math.pow(Math.cos(trig().doubleValue()), -1));
                } else {
                    ans = new BigDecimal(Math.pow(Math.cos(Math.toRadians(trig().doubleValue())), -1));
                }
                found = true;
            } else if (s.indexOf("atanh") == 0) {
                s = s.substring(5);
                ans = trig();
                double doubleAns = ans.doubleValue();
//                return 0.5*Math.log( (x + 1.0) / (1 - x) );
                ans = new BigDecimal(0.5 * Math.log((doubleAns + 1.0) / (1 - doubleAns )));

                found = true;
            }
            else if (s.indexOf("atan") == 0) {
                s = s.substring(4);
                if (mDeg_flag) {
                    ans = new BigDecimal(Math.toDegrees(Math.atan(trig().doubleValue())));

                } else {
                    ans = new BigDecimal(Math.atan(trig().doubleValue()));

                }
                found = true;
            }else if (s.indexOf("tanh") == 0) {
                s = s.substring(4);
                ans = new BigDecimal(Math.tanh(trig().doubleValue()));
                found = true;
            }
            else if (s.indexOf("tan") == 0) {
                s = s.substring(3);
                if (mDeg_flag) {
//                    double input = trig().doubleValue();
//                    double sin = Math.sin((Math.toRadians(input)));
//                    double cos = Math.round(Math.cos((Math.toRadians(input))));
//                    double natije = sin/cos;
//
//                    ans = new BigDecimal(natije);
                    ans = new BigDecimal(Math.tan(Math.toRadians(trig().doubleValue())));
                } else {
                    double input = trig().doubleValue();
                    double sin = Math.sin((Math.toRadians(input)));
                    double cos = Math.round(Math.cos((Math.toRadians(input))));
                    double natije = sin/cos;

                    ans = new BigDecimal(natije);
//                    ans = new BigDecimal(Math.tan(trig().doubleValue()));

                }
                found = true;

            }
            else if (s.indexOf("acoth") == 0) {
                s = s.substring(5);
                ans = trig();
                double doubleAns = ans.doubleValue();
//                return 0.5*Math.log( (x + 1.0) / (x - 1.0) );
                ans = new BigDecimal(0.5 * Math.log((doubleAns + 1.0) / (doubleAns - 1.0)));
                found = true;
            }
            else if (s.indexOf("acot") == 0) {
                s = s.substring(4);
                if (mDeg_flag) {
                    ans = new BigDecimal(1.0 /Math.toDegrees( Math.atan(trig().doubleValue())));
                } else {
                    ans = new BigDecimal(1.0 / Math.atan(trig().doubleValue()));
                }
                found = true;

            }
            else if (s.indexOf("coth") == 0) {
                s = s.substring(4);
                ans = new BigDecimal(1.0 / Math.tanh(trig().doubleValue()));
                found = true;
            }
            else if (s.indexOf("cot") == 0) {
                s = s.substring(3);
                if (mDeg_flag) {
                    ans = new BigDecimal(1.0 / Math.tan(Math.toRadians(trig().doubleValue())));

                } else {
                    ans = new BigDecimal(1.0 / Math.tan(trig().doubleValue()));

                }
                found = true;

            }  else if (s.indexOf("log") == 0) {
                s = s.substring(3);
                ans = new BigDecimal(Math.log10(trig().doubleValue()));
                found = true;
            }else if (s.indexOf("10ˣ") == 0) {

                s = s.substring(3);
                ans = trig();
                double doubleAns = ans.doubleValue();

                ans = new BigDecimal(Math.pow(10,doubleAns));
//                BigDecimalUtils.intPower()
                found = true;
            }else if (s.indexOf("eˣ") == 0) {
                s = s.substring(2);

                ans = trig();
                double doubleAns = ans.doubleValue();

                ans = new BigDecimal(Math.pow(Math.E,doubleAns));
                found = true;
            }

            else if (s.indexOf("ln") == 0) {
                s = s.substring(2);
                ans = new BigDecimal(Math.log(trig().doubleValue()));
                found = true;
            } else if (s.charAt(0) == '\u221a') {
                s = s.substring(1);
                ans = BigDecimalUtils.sqrt(trig(), 6);
                found = true;

            }
        }


        if( !found ){
            ans = exp();
        }

        BigDecimal infiniteNumber= new BigDecimal(1.633123935319537E16);

        if(ans.equals(infiniteNumber)){
            ans = new BigDecimal(Double.NaN);
        }
        return ans;
    }


    /*
    * Multiplication, division expression solver.
    */
    BigDecimal mul(){
        BigDecimal tempAnswer = new BigDecimal("0");
        BigDecimal ans = trig();


        if( s.length() > 0 ){

//            For 20% * 3
            if((s.length()>1 && s.charAt(1) == '\u00d7') )
                if( (s.charAt(0) == '%'))
                {
                    PERCENT_FLAG = false;
                    ans = ans.divide(new BigDecimal("100"),new MathContext(6));
                    s = s.substring( 1 );
                }

            while( s.length() > 0 ){

                if( s.charAt( 0 ) == '\u00d7' || s.charAt( 0 ) == '*' ){
                    s = s.substring( 1 );

                    tempAnswer = trig();

                    if(PERCENT_FLAG == true){
                        ans = (ans.divide(new BigDecimal("100"),new MathContext(6)).multiply(tempAnswer));
                        PERCENT_FLAG = false;
                        tempAnswer = new BigDecimal(0);
                        s = s.substring( 1 );
                    }else {
                        // if(s.length()>0)
                        ans = ans.multiply( tempAnswer);

                    }


                } else if( s.charAt( 0 ) == '/'|| s.charAt( 0 ) == '÷' ) {
                    s = s.substring(1);
                    tempAnswer = trig();

                    if (PERCENT_FLAG == true) {
                        ans = (ans.multiply(new BigDecimal("100"),new MathContext(6)).divide(tempAnswer));
                        PERCENT_FLAG = false;
                        tempAnswer = new BigDecimal(0);
                        s = s.substring( 1 );
                    }
                    else {
                        //  tempAnswer = trig();
                        // if(s.length()>0)
                        ans = ans.divide( tempAnswer,new MathContext(6));
                    }
                }


                //2 % 300
                // 2 * 3%
                // 20% + 2
                // 2 + 10%
                // 2%



                else{
                    //  s= s.substring( 1 );
                    if (s.charAt(0) != '+'
                            && s.charAt(0) != '−'
                            && s.charAt(0) != ')'
                            && s.charAt(0) != '%') {

                        ans = ans.multiply(mul());
                    }


                    break;
                }
            }
        }
        return ans;
    }

    /*
    * Addition, subtraction expression solver.
    */
    BigDecimal add(){
        BigDecimal tempAnswer = new BigDecimal("0");
        BigDecimal ans = mul();

        // for 20% + 200
        if((s.length()== 1  ) ||
                (s.length()>1 &&( (s.charAt(1) == '+')||(s.charAt(1) == '\u00d7')
                        ||(s.charAt(1) == '−') || (s.charAt(1) == '÷'))  ) )
            if( (s.charAt(0) == '%'))
            {
                PERCENT_FLAG = false;
                ans = ans.divide(new BigDecimal("100"),new MathContext(6));
                s = s.substring( 1 );
            }


        while( s.length() > 0 ){

            if( s.charAt( 0 ) == '+' )
            {
                s = s.substring( 1 );
                tempAnswer = mul();

                // for 200 + 2%
                if(PERCENT_FLAG == true){
                    ans = ans.add((ans.multiply(tempAnswer)).divide(new BigDecimal("100"),new MathContext(6)));
                    PERCENT_FLAG = false;
                    s = s.substring( 1 );
                }else {
                    // if(s.length()>0)
                    ans = ans.add( tempAnswer);
                }

            } else if( s.charAt( 0 ) == '−' || s.charAt(0) == '-' ){
                s = s.substring( 1 );

                // for 200 - 2%
                tempAnswer = mul();
                if(PERCENT_FLAG == true){
                    ans = ans.subtract((ans.multiply( tempAnswer)).divide(new BigDecimal("100"),new MathContext(6)));
                    PERCENT_FLAG = false;
                    s = s.substring( 1 );
                    tempAnswer = new BigDecimal(0);
                }else {
                    // if(s.length()>0)
                    ans = ans.subtract( tempAnswer);
                }
            }

            // for 2 % 200
            else if(s.charAt( 0 ) == '%' ){
                s = s.substring( 1 );
                tempAnswer = mul();
                // if(s.length()>0)
                ans = (ans.divide(new BigDecimal("100"),new MathContext(6)).multiply(tempAnswer));
                PERCENT_FLAG = false;
            }

            else{
                // 2%*5
                /*if( s.charAt( 0 ) != ')'){
                    // s = s.substring( 1 );
                    ans = ans.multiply( mul());
                    PERCENT_FLAG = false;


                }else {
                    break;
                }
*/
                break;
            }
        }
        return ans;
    }




    /*
    * Public access method to evaluate this expression.
    */
    public BigDecimal evaluate(){
        s = x.intern();
        s= s.replace(",","");
        last = add();
        return last;
    }

    /*
    * Creates new Expression.
    */
    public Expression(String s, boolean _isRad_flag, Context applicationContext){
        // remove white space, assume only spaces or tabs
        mContext = applicationContext;
        mTest = mContext.getResources().getString(R.string.powertwoTag);
        int i = mTest.length();
        mDeg_flag = _isRad_flag;
        StringBuffer b = new StringBuffer();
        StringTokenizer t = new StringTokenizer( s, " " );
        while( t.hasMoreElements() )
            b.append( t.nextToken() );
        t = new StringTokenizer( b.toString(), "\t" );
        b = new StringBuffer();
        while( t.hasMoreElements() )
            b.append( t.nextToken() );

        x = b.toString();
    }

    /*
    * The String value of this Expression.
    */
    public String toString(){
        return x.intern();
    }

    /*
    * Test our Expression class by evaluating the command-line
    * argument and then returning.
    */

}