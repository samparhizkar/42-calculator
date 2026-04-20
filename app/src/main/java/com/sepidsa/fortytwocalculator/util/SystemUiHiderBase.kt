package com.sepidsa.fortytwocalculator.util

import android.app.Activity
import android.view.View
import android.view.WindowManager

/**
 * A base implementation of [SystemUiHider]. Uses APIs available in all
 * API levels to show and hide the status bar.
 */
open class SystemUiHiderBase(
    activity: Activity,
    anchorView: View,
    flags: Int,
) : SystemUiHider(activity, anchorView, flags) {
    /**
     * Whether or not the system UI is currently visible. This is a cached value
     * from calls to [hide] and [show].
     */
    private var mVisible = true

    override fun setup() {
        if ((mFlags and FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES) == 0) {
            mActivity.window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            )
        }
    }

    override fun isVisible(): Boolean = mVisible

    override fun hide() {
        if ((mFlags and FLAG_FULLSCREEN) != 0) {
            mActivity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
            )
        }
        mOnVisibilityChangeListener.onVisibilityChange(false)
        mVisible = false
    }

    override fun show() {
        if ((mFlags and FLAG_FULLSCREEN) != 0) {
            mActivity.window.setFlags(
                0,
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
            )
        }
        mOnVisibilityChangeListener.onVisibilityChange(true)
        mVisible = true
    }
}
