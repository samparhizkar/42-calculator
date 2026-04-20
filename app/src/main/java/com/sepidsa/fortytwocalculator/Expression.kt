package com.sepidsa.fortytwocalculator

import android.content.Context
import java.math.BigDecimal
import java.math.MathContext
import java.util.StringTokenizer
import kotlin.math.ceil

class Expression(s: String, _isRad_flag: Boolean, applicationContext: Context) {
    private val mContext: Context = applicationContext

    /*
     * Strings used for storing expression.
     */
    private var PERCENT_FLAG = false
    private var mDeg_flag: Boolean = _isRad_flag

    var s: String = ""
    var x: String
    var last: BigDecimal? = null
    private val mTest: String = mContext.resources.getString(R.string.powertwoTag)

    /*
     * Term evaluator for number literals.
     */
    private fun term(): BigDecimal? {
        var ans: BigDecimal? = null
        val temp = StringBuffer("")

        var neg = false
        if (this.s[0] == '−' || this.s[0] == '-') {
            neg = true
            this.s = this.s.substring(1)
        }

        if (this.s.isNotEmpty() && this.s[0] == 'e') {
            this.s = this.s.substring(1)
            ans = BigDecimal(Math.E)
        } else if (this.s.isNotEmpty() && this.s[0] == 'π') {
            this.s = this.s.substring(1)
            ans = BigDecimal(Math.PI)
        } else if (this.s.isNotEmpty() && this.s.indexOf("rand") == 0) {
            this.s = this.s.substring(4)
            ans = BigDecimal(Math.random())
        } else {
            while (this.s.isNotEmpty() && Character.isDigit(this.s[0])) {
                temp.append(Integer.parseInt("" + this.s[0]))
                this.s = this.s.substring(1)
            }
            if (this.s.isNotEmpty() && this.s[0] == '.') {
                temp.append('.')
                this.s = this.s.substring(1)
                while (this.s.isNotEmpty() && Character.isDigit(this.s[0])) {
                    temp.append(Integer.parseInt("" + this.s[0]))
                    this.s = this.s.substring(1)
                }
            }
            if (this.s.isNotEmpty() && this.s[0] == 'E') {
                temp.append('E')
                this.s = this.s.substring(1)
                temp.append(this.s[0])
                this.s = this.s.substring(1)
                while (this.s.isNotEmpty() && Character.isDigit(this.s[0])) {
                    temp.append(Integer.parseInt("" + this.s[0]))
                    this.s = this.s.substring(1)
                }
            }
            if (temp == StringBuffer("")) {
                return BigDecimal(0)
            } else {
                ans = BigDecimal(temp.toString())
            }
        }
        return ans
    }

    private fun paren(): BigDecimal? {
        var ans: BigDecimal? = null

        if (s.isNotEmpty()) {
            ans = if (s[0] == '(') {
                s = s.substring(1)
                val inner = add()
                if (s.isNotEmpty() && s[0] == ')') {
                    s = s.substring(1)
                }
                inner
            } else {
                term()
            }
        }
        return ans
    }

    private fun factorial(): BigDecimal? {
        var ans = paren()

        while (s.isNotEmpty()) {
            if (s.indexOf(mTest) == 0) {
                s = s.substring(1)
                ans = if (ans != null) BigDecimalUtils.intPower(ans, 2, 6) else null
                break
            } else if (s.indexOf(mContext.resources.getString(R.string.powerthreeTag)) == 0) {
                s = s.substring(1)
                ans = if (ans != null) BigDecimalUtils.intPower(ans, 3, 6) else null
                break
            }
            if (s[0] == '!') {
                s = s.substring(1)
                if (ans != null) {
                    try {
                        if (ans.scale() > 0 || ans.compareTo(BigDecimal.ZERO) == -1) {
                            throw NumberFormatException()
                        } else {
                            val rounded = ans.round(MathContext(6))
                            ans = compute_factorial(rounded, rounded)
                        }
                    } finally {
                        //
                    }
                }
            } else if (s[0] == '%') {
                PERCENT_FLAG = true
                break
            } else {
                break
            }
        }
        return ans
    }

    /*
     * Exponentiation solver.
     */
    private fun exp(): BigDecimal? {
        var neg = false
        if (s.isNotEmpty() && (s[0] == '−' || s[0] == '-')) {
            neg = true
            s = s.substring(1)
            return BigDecimal(-1)
        }
        var ans = factorial()
        while (s.isNotEmpty()) {
            if (s.indexOf("^") == 0) {
                s = s.substring(1)
                var expNeg = false
                // Checking with both types of minus character
                if (s.isNotEmpty() && (s[0] == '−' || s[0] == '-')) {
                    expNeg = true
                    s = s.substring(1)
                }
                val e = factorial()

                if (ans != null && e != null) {
                    if (ans.toDouble() < 0) { // if it's negative
                        var x = BigDecimal("1")
                        if (ceil(e.toDouble()) == e.toDouble()) { // only raise to an integer
                            var eVar = e
                            if (expNeg) eVar = eVar.multiply(BigDecimal("-1"))
                            if (eVar.toDouble() == 0.0) ans = BigDecimal("1")
                            else if (eVar.toDouble() > 0) {
                                for (i in 0 until eVar.toDouble().toInt()) x = x.multiply(ans!!)
                            } else {
                                for (i in 0 until (-eVar.toDouble()).toInt()) x = x.divide(ans!!, MathContext(6))
                            }
                            ans = x
                        } else {
                            ans = BigDecimal(Math.log(-1.0)) // otherwise make it NaN
                        }
                    } else {
                        val n1 = ans
                        val n2 = e
                        ans = bigdecimalPower(ans, n1, n2, expNeg)
                    }
                }
            } else break
        }
        if (neg) ans = ans?.negate()
        return ans
    }

    private fun bigdecimalPower(ansIn: BigDecimal?, n1: BigDecimal?, n2: BigDecimal?, expNeg: Boolean): BigDecimal? {
        var ans = ansIn
        var n2Var = n2
        val signOf2 = n2Var!!.signum()
        if (expNeg) ans = ans!!.negate()
        try {
            // Perform X^(A+B)=X^A*X^B (B = remainder)
            val dn1 = n1!!.toDouble()
            // Compare the same row of digits according to context
            if (n1.toDouble() != dn1) throw Exception() // Cannot convert n1 to double
            n2Var = n2Var.multiply(BigDecimal(signOf2.toLong())) // n2 is now positive
            val remainderOf2 = n2Var.remainder(BigDecimal.ONE)
            val n2IntPart = n2Var.subtract(remainderOf2)
            // Calculate big part of the power using context -
            // bigger range and performance but lower accuracy
            val intPow = n1.pow(n2IntPart.intValueExact(), MathContext(6))
            val doublePow = BigDecimal(Math.pow(dn1, remainderOf2.toDouble()))
            ans = intPow.multiply(doublePow)
        } catch (error: Exception) {
            error.printStackTrace()
        }
        // Fix negative power
        if (expNeg) {
            ans = BigDecimal.ONE.divide(ans, 6, BigDecimal.ROUND_HALF_UP)
        }
        return ans
    }

    /*
     * Trigonometric function solver.
     */
    private fun trig(): BigDecimal {
        var ans = BigDecimal("0")
        var found = false
        if (s.isNotEmpty()) {
            if (s.indexOf("asinh") == 0) {
                s = s.substring(5)
                ans = trig()
                val doubleAns = ans.toDouble()
                ans = BigDecimal(Math.log(doubleAns + Math.sqrt(Math.pow(doubleAns, 2.0) + 1.0)))

                found = true
            } else if (s.indexOf("sinh") == 0) {
                s = s.substring(4)
                ans = BigDecimal(Math.sinh(trig().toDouble()))
                found = true
            } else if (s.indexOf("asin") == 0) {
                s = s.substring(4)
                ans = if (mDeg_flag) {
                    BigDecimal(Math.toDegrees(Math.asin(trig().toDouble())))
                } else {
                    BigDecimal(Math.asin(trig().toDouble()))
                }
                found = true
            } else if (s.indexOf("sin") == 0) {
                s = s.substring(3)
                ans = if (mDeg_flag) {
                    BigDecimal(Math.sin(Math.toRadians(trig().toDouble())))
                } else {
                    BigDecimal(Math.sin(trig().toDouble()))
                }
                found = true
            } else if (s.indexOf("acsch") == 0) {
                // = asinh (1/x)
                s = s.substring(5)
                ans = trig()
                val doubleAns = 1 / ans.toDouble()
                ans = BigDecimal(Math.log(doubleAns + Math.sqrt(Math.pow(doubleAns, 2.0) + 1.0)))

                found = true
            } else if (s.indexOf("acsc") == 0) {
                s = s.substring(4)
                ans = if (mDeg_flag) {
                    BigDecimal(Math.pow(Math.asin(Math.toDegrees(trig().toDouble())), -1.0))
                } else {
                    BigDecimal(Math.pow(Math.asin(trig().toDouble()), -1.0))
                }
                found = true
            } else if (s.indexOf("csch") == 0) {
                // = 1/sinh (x)
                s = s.substring(4)
                ans = BigDecimal(Math.pow(Math.sinh(trig().toDouble()), -1.0))
                found = true
            } else if (s.indexOf("csc") == 0) {
                s = s.substring(3)
                ans = if (mDeg_flag) {
                    BigDecimal(Math.pow(Math.sin(trig().toDouble()), -1.0))
                } else {
                    BigDecimal(Math.pow(Math.sin(Math.toRadians(trig().toDouble())), -1.0))
                }
                found = true
            } else if (s.indexOf("acosh") == 0) {
                s = s.substring(5)
                ans = trig()
                val doubleAns = ans.toDouble()
                ans = BigDecimal(Math.log(doubleAns + Math.sqrt(Math.pow(doubleAns, 2.0) - 1.0)))

                found = true
            } else if (s.indexOf("acos") == 0) {
                s = s.substring(4)
                ans = if (mDeg_flag) {
                    BigDecimal(Math.toDegrees(Math.acos(trig().toDouble())))
                } else {
                    BigDecimal(Math.acos(trig().toDouble()))
                }
                found = true
            } else if (s.indexOf("cosh") == 0) {
                s = s.substring(4)
                ans = BigDecimal(Math.cosh(trig().toDouble()))
                found = true
            } else if (s.indexOf("cos") == 0) {
                s = s.substring(3)
                ans = if (mDeg_flag) {
                    BigDecimal(Math.cos(Math.toRadians(trig().toDouble())))
                } else {
                    BigDecimal(Math.cos(trig().toDouble()))
                }
                found = true
            } else if (s.indexOf("asech") == 0) {
                s = s.substring(5)
                ans = trig()
                val doubleAns = 1 / ans.toDouble()
                ans = BigDecimal(Math.log(doubleAns + Math.sqrt(Math.pow(doubleAns, 2.0) - 1.0)))

                found = true
            } else if (s.indexOf("asec") == 0) {
                s = s.substring(4)
                ans = if (mDeg_flag) {
                    BigDecimal(Math.pow(Math.acos(Math.toDegrees(trig().toDouble())), -1.0))
                } else {
                    BigDecimal(Math.pow(Math.acos(trig().toDouble()), -1.0))
                }
                found = true
            } else if (s.indexOf("sech") == 0) {
                // = 1/cosh (x)
                s = s.substring(4)
                ans = BigDecimal(Math.pow(Math.cosh(trig().toDouble()), -1.0))
                found = true
            } else if (s.indexOf("sec") == 0) {
                s = s.substring(3)
                ans = if (mDeg_flag) {
                    BigDecimal(Math.pow(Math.cos(trig().toDouble()), -1.0))
                } else {
                    BigDecimal(Math.pow(Math.cos(Math.toRadians(trig().toDouble())), -1.0))
                }
                found = true
            } else if (s.indexOf("atanh") == 0) {
                s = s.substring(5)
                ans = trig()
                val doubleAns = ans.toDouble()
                ans = BigDecimal(0.5 * Math.log((doubleAns + 1.0) / (1 - doubleAns)))

                found = true
            } else if (s.indexOf("atan") == 0) {
                s = s.substring(4)
                ans = if (mDeg_flag) {
                    BigDecimal(Math.toDegrees(Math.atan(trig().toDouble())))
                } else {
                    BigDecimal(Math.atan(trig().toDouble()))
                }
                found = true
            } else if (s.indexOf("tanh") == 0) {
                s = s.substring(4)
                ans = BigDecimal(Math.tanh(trig().toDouble()))
                found = true
            } else if (s.indexOf("tan") == 0) {
                s = s.substring(3)
                ans = if (mDeg_flag) {
                    BigDecimal(Math.tan(Math.toRadians(trig().toDouble())))
                } else {
                    val input = trig().toDouble()
                    val sin = Math.sin(Math.toRadians(input))
                    val cos = Math.round(Math.cos(Math.toRadians(input)))
                    val natije = sin / cos
                    BigDecimal(natije)
                }
                found = true
            } else if (s.indexOf("acoth") == 0) {
                s = s.substring(5)
                ans = trig()
                val doubleAns = ans.toDouble()
                ans = BigDecimal(0.5 * Math.log((doubleAns + 1.0) / (doubleAns - 1.0)))
                found = true
            } else if (s.indexOf("acot") == 0) {
                s = s.substring(4)
                ans = if (mDeg_flag) {
                    BigDecimal(1.0 / Math.toDegrees(Math.atan(trig().toDouble())))
                } else {
                    BigDecimal(1.0 / Math.atan(trig().toDouble()))
                }
                found = true
            } else if (s.indexOf("coth") == 0) {
                s = s.substring(4)
                ans = BigDecimal(1.0 / Math.tanh(trig().toDouble()))
                found = true
            } else if (s.indexOf("cot") == 0) {
                s = s.substring(3)
                ans = if (mDeg_flag) {
                    BigDecimal(1.0 / Math.tan(Math.toRadians(trig().toDouble())))
                } else {
                    BigDecimal(1.0 / Math.tan(trig().toDouble()))
                }
                found = true
            } else if (s.indexOf("log") == 0) {
                s = s.substring(3)
                ans = BigDecimal(Math.log10(trig().toDouble()))
                found = true
            } else if (s.indexOf("10ˣ") == 0) {
                s = s.substring(3)
                ans = trig()
                val doubleAns = ans.toDouble()
                ans = BigDecimal(Math.pow(10.0, doubleAns))
                found = true
            } else if (s.indexOf("eˣ") == 0) {
                s = s.substring(2)
                ans = trig()
                val doubleAns = ans.toDouble()
                ans = BigDecimal(Math.pow(Math.E, doubleAns))
                found = true
            } else if (s.indexOf("ln") == 0) {
                s = s.substring(2)
                ans = BigDecimal(Math.log(trig().toDouble()))
                found = true
            } else if (s[0] == '\u221a') {
                s = s.substring(1)
                ans = BigDecimalUtils.sqrt(trig(), 6)
                found = true
            }
        }

        if (!found) {
            ans = exp()!!
        }

        val infiniteNumber = BigDecimal(1.633123935319537E16)

        if (ans == infiniteNumber) {
            ans = BigDecimal(Double.NaN)
        }
        return ans
    }

    /*
     * Multiplication, division expression solver.
     */
    private fun mul(): BigDecimal {
        var tempAnswer = BigDecimal("0")
        var ans = trig()

        if (s.isNotEmpty()) {
            // For 20% * 3
            if (s.length > 1 && s[1] == '\u00d7') if (s[0] == '%') {
                PERCENT_FLAG = false
                ans = ans.divide(BigDecimal("100"), MathContext(6))
                s = s.substring(1)
            }

            while (s.isNotEmpty()) {
                if (s[0] == '\u00d7' || s[0] == '*') {
                    s = s.substring(1)

                    tempAnswer = trig()

                    if (PERCENT_FLAG) {
                        ans = (ans.divide(BigDecimal("100"), MathContext(6)).multiply(tempAnswer))
                        PERCENT_FLAG = false
                        s = s.substring(1)
                    } else {
                        ans = ans.multiply(tempAnswer)
                    }
                } else if (s[0] == '/' || s[0] == '÷') {
                    s = s.substring(1)
                    tempAnswer = trig()

                    if (PERCENT_FLAG) {
                        ans = (ans.multiply(BigDecimal("100"), MathContext(6)).divide(tempAnswer))
                        PERCENT_FLAG = false
                        s = s.substring(1)
                    } else {
                        ans = ans.divide(tempAnswer, MathContext(6))
                    }
                } else {
                    if (s[0] != '+' && s[0] != '−' && s[0] != ')' && s[0] != '%') {
                        ans = ans.multiply(mul())
                    }
                    break
                }
            }
        }
        return ans
    }

    /*
     * Addition, subtraction expression solver.
     */
    private fun add(): BigDecimal {
        var tempAnswer = BigDecimal("0")
        var ans = mul()

        // for 20% + 200
        if ((s.length == 1) ||
            (s.length > 1 && ((s[1] == '+') || (s[1] == '\u00d7')
                    || (s[1] == '−') || (s[1] == '÷')))
        ) if ((s[0] == '%')) {
            PERCENT_FLAG = false
            ans = ans.divide(BigDecimal("100"), MathContext(6))
            s = s.substring(1)
        }

        while (s.isNotEmpty()) {
            if (s[0] == '+') {
                s = s.substring(1)
                tempAnswer = mul()

                // for 200 + 2%
                if (PERCENT_FLAG) {
                    ans = ans.add((ans.multiply(tempAnswer)).divide(BigDecimal("100"), MathContext(6)))
                    PERCENT_FLAG = false
                    s = s.substring(1)
                } else {
                    ans = ans.add(tempAnswer)
                }
            } else if (s[0] == '−' || s[0] == '-') {
                s = s.substring(1)

                // for 200 - 2%
                tempAnswer = mul()
                if (PERCENT_FLAG) {
                    ans = ans.subtract((ans.multiply(tempAnswer)).divide(BigDecimal("100"), MathContext(6)))
                    PERCENT_FLAG = false
                    s = s.substring(1)
                } else {
                    ans = ans.subtract(tempAnswer)
                }
            } else if (s[0] == '%') {
                // for 2 % 200
                s = s.substring(1)
                tempAnswer = mul()
                ans = (ans.divide(BigDecimal("100"), MathContext(6)).multiply(tempAnswer))
                PERCENT_FLAG = false
            } else {
                break
            }
        }
        return ans
    }

    /*
     * Public access method to evaluate this expression.
     */
    fun evaluate(): BigDecimal? {
        s = x.intern()
        this.s = this.s.replace(",", "")
        last = add()
        return last
    }

    override fun toString(): String = x.intern()

    companion object {
        private fun compute_factorial(n: BigDecimal, acc: BigDecimal): BigDecimal {
            if (n == BigDecimal.ONE) {
                return acc
            }
            val lessOne = n.subtract(BigDecimal.ONE)
            return compute_factorial(lessOne, acc.multiply(lessOne))
        }
    }

    init {
        val b = StringBuffer()
        var t = StringTokenizer(s, " ")
        while (t.hasMoreElements()) b.append(t.nextToken())
        t = StringTokenizer(b.toString(), "\t")
        val b2 = StringBuffer()
        while (t.hasMoreElements()) b2.append(t.nextToken())
        x = b2.toString()
    }
}
