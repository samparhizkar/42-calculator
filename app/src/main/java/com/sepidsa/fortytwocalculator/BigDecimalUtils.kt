package com.sepidsa.fortytwocalculator

import java.math.BigDecimal
import java.math.BigInteger

/**
 * Several useful BigDecimal mathematical functions.
 */
object BigDecimalUtils {
    /**
     * Compute x^exponent to a given scale.  Uses the same
     * algorithm as class numbercruncher.mathutils.IntPower.
     *
     * @param x the value x
     * @param exponent the exponent value
     * @param scale the desired scale of the result
     * @return the result value
     */
    @JvmStatic
    fun intPower(x: BigDecimal, exponent: Long, scale: Int): BigDecimal {
        var xVar = x
        var exponentVar = exponent

        // If the exponent is negative, compute 1/(x^-exponent).
        if (exponentVar < 0) {
            return BigDecimal.valueOf(1).divide(intPower(xVar, -exponentVar, scale), scale, BigDecimal.ROUND_HALF_EVEN)
        }

        var power = BigDecimal.valueOf(1)

        // Loop to compute value^exponent.
        while (exponentVar > 0) {
            // Is the rightmost bit a 1?
            if ((exponentVar and 1L) == 1L) {
                power = power.multiply(xVar).setScale(scale, BigDecimal.ROUND_HALF_EVEN)
            }

            // Square x and shift exponent 1 bit to the right.
            xVar = xVar.multiply(xVar).setScale(scale, BigDecimal.ROUND_HALF_EVEN)
            exponentVar = exponentVar shr 1

            Thread.yield()
        }

        return power
    }

    /**
     * Compute the integral root of x to a given scale, x >= 0.
     * Use Newton's algorithm.
     *
     * @param x the value of x
     * @param index the integral root value
     * @param scale the desired scale of the result
     * @return the result value
     */
    @JvmStatic
    fun intRoot(x: BigDecimal, index: Long, scale: Int): BigDecimal {
        var xVar = x

        // Check that x >= 0.
        if (xVar.signum() < 0) {
            throw IllegalArgumentException("x < 0")
        }

        val sp1 = scale + 1
        val n = xVar
        val i = BigDecimal.valueOf(index)
        val im1 = BigDecimal.valueOf(index - 1)
        val tolerance = BigDecimal.valueOf(5).movePointLeft(sp1)
        var xPrev: BigDecimal

        // The initial approximation is x/index.
        xVar = xVar.divide(i, scale, BigDecimal.ROUND_HALF_EVEN)

        // Loop until the approximations converge
        // (two successive approximations are equal after rounding).
        do {
            // x^(index-1)
            val xToIm1 = intPower(xVar, index - 1, sp1)

            // x^index
            val xToI = xVar.multiply(xToIm1).setScale(sp1, BigDecimal.ROUND_HALF_EVEN)

            // n + (index-1)*(x^index)
            val numerator = n.add(im1.multiply(xToI)).setScale(sp1, BigDecimal.ROUND_HALF_EVEN)

            // (index*(x^(index-1))
            val denominator = i.multiply(xToIm1).setScale(sp1, BigDecimal.ROUND_HALF_EVEN)

            // x = (n + (index-1)*(x^index)) / (index*(x^(index-1)))
            xPrev = xVar
            xVar = numerator.divide(denominator, sp1, BigDecimal.ROUND_DOWN)

            Thread.yield()
        } while (xVar.subtract(xPrev).abs().compareTo(tolerance) > 0)

        return xVar
    }

    /**
     * Compute e^x to a given scale.
     * Break x into its whole and fraction parts and
     * compute (e^(1 + fraction/whole))^whole using Taylor's formula.
     *
     * @param x the value of x
     * @param scale the desired scale of the result
     * @return the result value
     */
    @JvmStatic
    fun exp(x: BigDecimal, scale: Int): BigDecimal {
        // e^0 = 1
        if (x.signum() == 0) {
            return BigDecimal.valueOf(1)
        }

        // If x is negative, return 1/(e^-x).
        if (x.signum() == -1) {
            return BigDecimal.valueOf(1).divide(exp(x.negate(), scale), scale, BigDecimal.ROUND_HALF_EVEN)
        }

        // Compute the whole part of x.
        var xWhole = x.setScale(0, BigDecimal.ROUND_DOWN)

        // If there isn't a whole part, compute and return e^x.
        if (xWhole.signum() == 0) return expTaylor(x, scale)

        // Compute the fraction part of x.
        val xFraction = x.subtract(xWhole)

        // z = 1 + fraction/whole
        val z = BigDecimal.valueOf(1).add(xFraction.divide(xWhole, scale, BigDecimal.ROUND_HALF_EVEN))

        // t = e^z
        val t = expTaylor(z, scale)

        val maxLong = BigDecimal.valueOf(Long.MAX_VALUE)
        var result = BigDecimal.valueOf(1)

        // Compute and return t^whole using intPower().
        // If whole > Long.MAX_VALUE, then first compute products
        // of e^Long.MAX_VALUE.
        while (xWhole.compareTo(maxLong) >= 0) {
            result = result.multiply(intPower(t, Long.MAX_VALUE, scale)).setScale(scale, BigDecimal.ROUND_HALF_EVEN)
            xWhole = xWhole.subtract(maxLong)

            Thread.yield()
        }
        return result.multiply(intPower(t, xWhole.toLong(), scale)).setScale(scale, BigDecimal.ROUND_HALF_EVEN)
    }

    /**
     * Compute e^x to a given scale by the Taylor series.
     *
     * @param x the value of x
     * @param scale the desired scale of the result
     * @return the result value
     */
    private fun expTaylor(x: BigDecimal, scale: Int): BigDecimal {
        var factorial = BigDecimal.valueOf(1)
        var xPower = x
        var sumPrev: BigDecimal

        // 1 + x
        var sum = x.add(BigDecimal.valueOf(1))

        // Loop until the sums converge
        // (two successive sums are equal after rounding).
        var i = 2
        do {
            // x^i
            xPower = xPower.multiply(x).setScale(scale, BigDecimal.ROUND_HALF_EVEN)

            // i!
            factorial = factorial.multiply(BigDecimal.valueOf(i.toLong()))

            // x^i/i!
            val term = xPower.divide(factorial, scale, BigDecimal.ROUND_HALF_EVEN)

            // sum = sum + x^i/i!
            sumPrev = sum
            sum = sum.add(term)

            ++i
            Thread.yield()
        } while (sum.compareTo(sumPrev) != 0)

        return sum
    }

    /**
     * Compute the natural logarithm of x to a given scale, x > 0.
     */
    @JvmStatic
    fun ln(x: BigDecimal, scale: Int): BigDecimal {
        // Check that x > 0.
        if (x.signum() <= 0) {
            throw IllegalArgumentException("x <= 0")
        }

        // The number of digits to the left of the decimal point.
        val magnitude = x.toString().length - x.scale() - 1

        return if (magnitude < 3) {
            lnNewton(x, scale)
        } else {
            // Compute magnitude*ln(x^(1/magnitude)).

            // x^(1/magnitude)
            val root = intRoot(x, magnitude.toLong(), scale)

            // ln(x^(1/magnitude))
            val lnRoot = lnNewton(root, scale)

            // magnitude*ln(x^(1/magnitude))
            BigDecimal.valueOf(magnitude.toLong()).multiply(lnRoot).setScale(scale, BigDecimal.ROUND_HALF_EVEN)
        }
    }

    /**
     * Compute the natural logarithm of x to a given scale, x > 0.
     * Use Newton's algorithm.
     */
    private fun lnNewton(x: BigDecimal, scale: Int): BigDecimal {
        var xVar = x

        val sp1 = scale + 1
        val n = xVar
        var term: BigDecimal

        // Convergence tolerance = 5*(10^-(scale+1))
        val tolerance = BigDecimal.valueOf(5).movePointLeft(sp1)

        // Loop until the approximations converge
        // (two successive approximations are within the tolerance).
        do {
            // e^x
            val eToX = exp(xVar, sp1)

            // (e^x - n)/e^x
            term = eToX.subtract(n).divide(eToX, sp1, BigDecimal.ROUND_DOWN)

            // x - (e^x - n)/e^x
            xVar = xVar.subtract(term)

            Thread.yield()
        } while (term.compareTo(tolerance) > 0)

        return xVar.setScale(scale, BigDecimal.ROUND_HALF_EVEN)
    }

    /**
     * Compute the arctangent of x to a given scale, |x| < 1
     *
     * @param x the value of x
     * @param scale the desired scale of the result
     * @return the result value
     */
    @JvmStatic
    fun arctan(x: BigDecimal, scale: Int): BigDecimal {
        // Check that |x| < 1.
        if (x.abs().compareTo(BigDecimal.valueOf(1)) >= 0) {
            throw IllegalArgumentException("|x| >= 1")
        }

        // If x is negative, return -arctan(-x).
        return if (x.signum() == -1) {
            arctan(x.negate(), scale).negate()
        } else {
            arctanTaylor(x, scale)
        }
    }

    /**
     * Compute the arctangent of x to a given scale
     * by the Taylor series, |x| < 1
     *
     * @param x the value of x
     * @param scale the desired scale of the result
     * @return the result value
     */
    private fun arctanTaylor(x: BigDecimal, scale: Int): BigDecimal {
        val sp1 = scale + 1
        var i = 3
        var addFlag = false

        var power = x
        var sum = x
        var term: BigDecimal

        // Convergence tolerance = 5*(10^-(scale+1))
        val tolerance = BigDecimal.valueOf(5).movePointLeft(sp1)

        // Loop until the approximations converge
        // (two successive approximations are within the tolerance).
        do {
            // x^i
            power = power.multiply(x).multiply(x).setScale(sp1, BigDecimal.ROUND_HALF_EVEN)

            // (x^i)/i
            term = power.divide(BigDecimal.valueOf(i.toLong()), sp1, BigDecimal.ROUND_HALF_EVEN)

            // sum = sum +- (x^i)/i
            sum = if (addFlag) sum.add(term) else sum.subtract(term)

            i += 2
            addFlag = !addFlag

            Thread.yield()
        } while (term.compareTo(tolerance) > 0)

        return sum
    }

    /**
     * Compute the square root of x to a given scale, x >= 0.
     * Use Newton's algorithm.
     *
     * @param x the value of x
     * @param scale the desired scale of the result
     * @return the result value
     */
    @JvmStatic
    fun sqrt(x: BigDecimal, scale: Int): BigDecimal {
        // Check that x >= 0.
        if (x.signum() < 0) {
            throw IllegalArgumentException("x < 0")
        }

        // n = x*(10^(2*scale))
        val n: BigInteger = x.movePointRight(scale shl 1).toBigInteger()

        // The first approximation is the upper half of n.
        val bits = (n.bitLength() + 1) shr 1
        var ix = n.shiftRight(bits)
        var ixPrev: BigInteger

        // Loop until the approximations converge
        // (two successive approximations are equal after rounding).
        do {
            ixPrev = ix

            // x = (x + n/x)/2
            ix = ix.add(n.divide(ix)).shiftRight(1)

            Thread.yield()
        } while (ix.compareTo(ixPrev) != 0)

        return BigDecimal(ix, scale)
    }
}
