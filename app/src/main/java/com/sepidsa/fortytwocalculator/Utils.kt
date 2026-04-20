package com.sepidsa.fortytwocalculator

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color

object Utils {
    object ColorUtils {
        @JvmStatic
        fun colorChoice(context: Context): IntArray? {
            val colorArray = context.resources.getStringArray(R.array.default_color_choice_values)
            if (colorArray.isNotEmpty()) {
                val choices = IntArray(colorArray.size)
                for (i in colorArray.indices) {
                    choices[i] = Color.parseColor(colorArray[i])
                }
                return choices
            }
            return null
        }

        @JvmStatic
        fun colorChoiceForKeypad(context: Context): IntArray? {
            val colorArray = context.resources.getStringArray(R.array.keypad_color_choice_values)
            if (colorArray.isNotEmpty()) {
                val choices = IntArray(colorArray.size)
                for (i in colorArray.indices) {
                    choices[i] = Color.parseColor(colorArray[i])
                }
                return choices
            }
            return null
        }

        @JvmStatic
        fun parseWhiteColor(): Int = Color.parseColor("#FFFFFF")
    }

    @JvmStatic
    fun isTablet(context: Context): Boolean {
        return (context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }
}
