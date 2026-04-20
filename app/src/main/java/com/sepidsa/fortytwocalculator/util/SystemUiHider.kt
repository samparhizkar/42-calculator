package com.sepidsa.fortytwocalculator.util

import android.app.Activity
import android.os.Build
import android.view.View

/**
 * A utility class that helps with showing and hiding system UI such as the
 * status bar and navigation/system bar.
 */
abstract class SystemUiHider(
    protected val mActivity: Activity,
    protected val mAnchorView: View,
    protected val mFlags: Int,
) {
    protected var mOnVisibilityChangeListener: OnVisibilityChangeListener = sDummyListener

    abstract fun setup()
    abstract fun isVisible(): Boolean
    abstract fun hide()
    abstract fun show()

    fun toggle() {
        if (isVisible()) hide() else show()
    }

    fun setOnVisibilityChangeListener(listener: OnVisibilityChangeListener?) {
        mOnVisibilityChangeListener = listener ?: sDummyListener
    }

    fun interface OnVisibilityChangeListener {
        fun onVisibilityChange(visible: Boolean)
    }

    companion object {
        /**
         * When this flag is set, the
         * [android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN]
         * flag will be set on older devices.
         */
        const val FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES: Int = 0x1

        /**
         * When this flag is set, [show] and [hide] will toggle
         * the visibility of the status bar.
         */
        const val FLAG_FULLSCREEN: Int = 0x2

        /**
         * When this flag is set, [show] and [hide] will toggle
         * the visibility of the navigation bar, if it's present.
         */
        const val FLAG_HIDE_NAVIGATION: Int = FLAG_FULLSCREEN or 0x4

        @JvmStatic
        fun getInstance(activity: Activity, anchorView: View, flags: Int): SystemUiHider {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                SystemUiHiderHoneycomb(activity, anchorView, flags)
            } else {
                SystemUiHiderBase(activity, anchorView, flags)
            }
        }

        private val sDummyListener = OnVisibilityChangeListener { }
    }
}
