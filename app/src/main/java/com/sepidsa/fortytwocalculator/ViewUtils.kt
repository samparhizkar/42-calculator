package com.sepidsa.fortytwocalculator

import android.view.View
import androidx.core.view.ViewCompat

object ViewUtils {
    @JvmStatic
    fun hitTest(v: View, x: Int, y: Int): Boolean {
        val tx = (ViewCompat.getTranslationX(v) + 0.5f).toInt()
        val ty = (ViewCompat.getTranslationY(v) + 0.5f).toInt()
        val left = v.left + tx
        val right = v.right + tx
        val top = v.top + ty
        val bottom = v.bottom + ty

        return (x >= left) && (x <= right) && (y >= top) && (y <= bottom)
    }
}
