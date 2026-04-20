package com.sepidsa.fortytwocalculator

import android.content.Context
import android.content.res.Resources
import android.graphics.RectF
import android.graphics.Typeface
import android.text.Layout.Alignment
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.SparseIntArray
import android.util.TypedValue
import android.widget.TextView

/**
 * a textView that is able to self-adjust its font size depending on the min and max size of the font, and its own size.<br/>
 * code is heavily based on this StackOverflow thread:
 * http://stackoverflow.com/questions/16017165/auto-fit-textview-for-android/21851239#21851239 <br/>
 * It should work fine with most Android versions, but might have some issues on Android 3.1 - 4.04, as setTextSize will only work for the first time. <br/>
 * More info here: https://code.google.com/p/android/issues/detail?id=22493 and here in case you wish to fix it: http://stackoverflow.com/a/21851239/878126
 */
class AutoResizeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : TextView(context, attrs, defStyle) {

    private val _availableSpaceRect = RectF()
    private val _textCachedSizes = SparseIntArray()
    private val _sizeTester: SizeTester
    private var _maxTextSize: Float
    private var _spacingMult = 1.0f
    private var _spacingAdd = 0.0f
    private var _minTextSize: Float
    private var _widthLimit = 0
    private var _maxLines = 0
    private var _enableSizeCache = true
    private var _initiallized = false
    private var paint: TextPaint? = null

    private fun interface SizeTester {
        /**
         * @param suggestedSize  Size of text to be tested
         * @param availableSpace available space in which text must fit
         * @return an integer < 0 if after applying [suggestedSize] to
         * text, it takes less space than [availableSpace], > 0
         * otherwise
         */
        fun onTestSize(suggestedSize: Int, availableSpace: RectF): Int
    }

    init {
        // using the minimal recommended font size
        _minTextSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            12f,
            resources.displayMetrics,
        )
        _maxTextSize = textSize
        if (_maxLines == 0) {
            // no value was assigned during construction
            _maxLines = NO_LINE_LIMIT
        }

        _sizeTester = object : SizeTester {
            private val textRect = RectF()

            override fun onTestSize(suggestedSize: Int, availableSpace: RectF): Int {
                paint?.textSize = suggestedSize.toFloat()
                val text = this@AutoResizeTextView.text.toString()
                val singleLine = maxLines == 1
                if (singleLine) {
                    textRect.bottom = paint?.fontSpacing ?: 0f
                    textRect.right = paint?.measureText(text) ?: 0f
                } else {
                    val layout = StaticLayout(
                        text,
                        paint,
                        _widthLimit,
                        Alignment.ALIGN_NORMAL,
                        _spacingMult,
                        _spacingAdd,
                        true,
                    )
                    // return early if we have more lines
                    if (maxLines != NO_LINE_LIMIT && layout.lineCount > maxLines) {
                        return 1
                    }
                    textRect.bottom = layout.height.toFloat()
                    var maxWidth = -1
                    for (i in 0 until layout.lineCount) {
                        val lineWidth = (layout.getLineRight(i) - layout.getLineLeft(i)).toInt()
                        if (maxWidth < lineWidth) {
                            maxWidth = lineWidth
                        }
                    }
                    textRect.right = maxWidth.toFloat()
                }
                textRect.offsetTo(0f, 0f)
                if (availableSpace.contains(textRect)) {
                    // may be too small, don't worry we will find the best match
                    return -1
                }
                // else, too big
                return 1
            }
        }

        _initiallized = true
    }

    override fun setTypeface(tf: Typeface?) {
        if (paint == null) {
            paint = TextPaint(paint)
        }
        paint?.typeface = tf
        adjustTextSize()
        super.setTypeface(tf)
    }

    override fun setTextSize(size: Float) {
        _maxTextSize = size
        _textCachedSizes.clear()
        adjustTextSize()
    }

    override fun setMaxLines(maxlines: Int) {
        super.setMaxLines(maxlines)
        _maxLines = maxlines
        reAdjust()
    }

    override fun getMaxLines(): Int {
        return _maxLines
    }

    override fun setSingleLine() {
        super.setSingleLine()
        _maxLines = 1
        reAdjust()
    }

    override fun setSingleLine(singleLine: Boolean) {
        super.setSingleLine(singleLine)
        _maxLines = if (singleLine) 1 else NO_LINE_LIMIT
        reAdjust()
    }

    override fun setLines(lines: Int) {
        super.setLines(lines)
        _maxLines = lines
        reAdjust()
    }

    override fun setTextSize(unit: Int, size: Float) {
        val c = context
        val r: Resources = c.resources ?: Resources.getSystem()
        _maxTextSize = TypedValue.applyDimension(unit, size, r.displayMetrics)
        _textCachedSizes.clear()
        adjustTextSize()
    }

    override fun setLineSpacing(add: Float, mult: Float) {
        super.setLineSpacing(add, mult)
        _spacingMult = mult
        _spacingAdd = add
    }

    /**
     * Set the lower text size limit and invalidate the view
     */
    fun setMinTextSize(minTextSize: Float) {
        _minTextSize = minTextSize
        reAdjust()
    }

    private fun reAdjust() {
        adjustTextSize()
    }

    private fun adjustTextSize() {
        // This is a workaround for truncated text issue on ListView, as shown here: https://github.com/AndroidDeveloperLB/AutoFitTextView/pull/14
        // TODO think of a nicer, elegant solution.
        if (!_initiallized) {
            return
        }
        val startSize = _minTextSize.toInt()
        val heightLimit = measuredHeight - compoundPaddingBottom - compoundPaddingTop
        _widthLimit = measuredWidth - compoundPaddingLeft - compoundPaddingRight
        if (_widthLimit <= 0) {
            return
        }
        _availableSpaceRect.right = _widthLimit.toFloat()
        _availableSpaceRect.bottom = heightLimit.toFloat()
        superSetTextSize(startSize)
    }

    private fun superSetTextSize(startSize: Int) {
        super.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            efficientTextSizeSearch(
                startSize,
                _maxTextSize.toInt(),
                _sizeTester,
                _availableSpaceRect,
            ).toFloat(),
        )
    }

    /**
     * Enables or disables size caching, enabling it will improve performance
     * where you are animating a value inside TextView. This stores the font
     * size against getText().length() Be careful though while enabling it as 0
     * takes more space than 1 on some fonts and so on.
     */
    fun setEnableSizeCache(enable: Boolean) {
        _enableSizeCache = enable
        _textCachedSizes.clear()
        adjustTextSize()
    }

    private fun efficientTextSizeSearch(
        start: Int,
        end: Int,
        sizeTester: SizeTester,
        availableSpace: RectF,
    ): Int {
        if (!_enableSizeCache) {
            return binarySearch(start, end, sizeTester, availableSpace)
        }
        val text = this.text.toString()
        val key = text.length
        var size = _textCachedSizes.get(key)
        if (size != 0) {
            return size
        }
        size = binarySearch(start, end, sizeTester, availableSpace)
        _textCachedSizes.put(key, size)
        return size
    }

    private fun binarySearch(
        start: Int,
        end: Int,
        sizeTester: SizeTester,
        availableSpace: RectF,
    ): Int {
        var lastBest = start
        var lo = start
        var hi = end - 1
        var mid: Int
        while (lo <= hi) {
            mid = lo + hi ushr 1
            val midValCmp = sizeTester.onTestSize(mid, availableSpace)
            if (midValCmp < 0) {
                lastBest = lo
                lo = mid + 1
            } else if (midValCmp > 0) {
                hi = mid - 1
                lastBest = hi
            } else {
                return mid
            }
        }
        // make sure to return last best
        // this is what should always be returned
        return lastBest
    }

    override fun onTextChanged(text: CharSequence, start: Int, before: Int, after: Int) {
        super.onTextChanged(text, start, before, after)
        reAdjust()
    }

    override fun onSizeChanged(width: Int, height: Int, oldwidth: Int, oldheight: Int) {
        _textCachedSizes.clear()
        super.onSizeChanged(width, height, oldwidth, oldheight)
        if (width != oldwidth || height != oldheight) {
            reAdjust()
        }
    }

    private companion object {
        private const val NO_LINE_LIMIT = -1
    }
}

