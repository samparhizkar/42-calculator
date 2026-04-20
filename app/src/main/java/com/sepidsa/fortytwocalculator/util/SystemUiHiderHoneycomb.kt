package com.sepidsa.fortytwocalculator.util

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager

/**
 * An API 11+ implementation of [SystemUiHider]. Uses APIs available in
 * Honeycomb and later (specifically [View.setSystemUiVisibility]) to
 * show and hide the system UI.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class SystemUiHiderHoneycomb(
    activity: Activity,
    anchorView: View,
    flags: Int,
) : SystemUiHiderBase(activity, anchorView, flags) {
    private val mShowFlags: Int
    private val mHideFlags: Int
    private val mTestFlags: Int

    private var mVisible = true

    init {
        var showFlags = View.SYSTEM_UI_FLAG_VISIBLE
        var hideFlags = View.SYSTEM_UI_FLAG_LOW_PROFILE
        var testFlags = View.SYSTEM_UI_FLAG_LOW_PROFILE

        if ((mFlags and FLAG_FULLSCREEN) != 0) {
            showFlags = showFlags or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            hideFlags = hideFlags or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        if ((mFlags and FLAG_HIDE_NAVIGATION) != 0) {
            showFlags = showFlags or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            hideFlags = hideFlags or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            testFlags = testFlags or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

        mShowFlags = showFlags
        mHideFlags = hideFlags
        mTestFlags = testFlags
    }

    override fun setup() {
        mAnchorView.setOnSystemUiVisibilityChangeListener(mSystemUiVisibilityChangeListener)
    }

    override fun hide() {
        mAnchorView.systemUiVisibility = mHideFlags
    }

    override fun show() {
        mAnchorView.systemUiVisibility = mShowFlags
    }

    override fun isVisible(): Boolean = mVisible

    private val mSystemUiVisibilityChangeListener = View.OnSystemUiVisibilityChangeListener { vis ->
        if ((vis and mTestFlags) != 0) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                mActivity.actionBar?.hide()
                mActivity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                )
            }
            mOnVisibilityChangeListener.onVisibilityChange(false)
            mVisible = false
        } else {
            mAnchorView.systemUiVisibility = mShowFlags
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                mActivity.actionBar?.show()
                mActivity.window.setFlags(
                    0,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                )
            }
            mOnVisibilityChangeListener.onVisibilityChange(true)
            mVisible = true
        }
    }
}
