package com.sepidsa.fortytwocalculator

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout.LayoutParams
import android.widget.ListView
import android.widget.TextView

/**
 * This animation class is animating the expanding and reducing the size of a view.
 * The animation toggles between the Expand and Reduce, depending on the current state of the view
 *
 * @author Udinic
 */
class ExpandAnimation(
    context: Context,
    private val mListView: ListView,
    private val mParentView: View,
    private val mAnimatedView: View,
    duration: Int,
) : Animation() {
    private val mViewLayoutParams: LayoutParams = mAnimatedView.layoutParams as LayoutParams
    private val mMarginStart: Int
    private val mMarginEnd: Int
    private var mIsVisibleAfter: Boolean
    private var mWasEndedAlready = false
    private val mArrow: TextView = mParentView.findViewById(R.id.arrow)

    init {
        setDuration(duration.toLong())

        // decide to show or hide the view
        mIsVisibleAfter = (mAnimatedView.visibility == View.VISIBLE)

        mMarginStart = mViewLayoutParams.bottomMargin
        mMarginEnd = if (mMarginStart == 0) (0 - mAnimatedView.height) else 0

        setClipView(mAnimatedView, false)
        mAnimatedView.visibility = View.VISIBLE
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        super.applyTransformation(interpolatedTime, t)

        if (interpolatedTime < 1.0f) {
            mIsVisibleAfter = false
            mAnimatedView.visibility = View.VISIBLE

            // Calculating the new bottom margin, and setting it
            mViewLayoutParams.bottomMargin = mMarginStart + ((mMarginEnd - mMarginStart) * interpolatedTime).toInt()

            // Invalidating the layout, making us seeing the changes we made
            mAnimatedView.requestLayout()
            mListView.post {
                val rect = Rect(mAnimatedView.left, mParentView.top, mAnimatedView.right, mAnimatedView.bottom)
                mListView.requestChildRectangleOnScreen(mParentView, rect, false)
            }
            if (mMarginStart != 0) {
                mArrow.rotation = 180 * interpolatedTime
                mArrow.translationY = 150 * interpolatedTime
                mArrow.alpha = 1 - interpolatedTime
            } else {
                mArrow.rotation = 180 * (1 - interpolatedTime)
                mArrow.translationY = 150 * (1 - interpolatedTime)
                mArrow.alpha = interpolatedTime
            }
        } else if (!mWasEndedAlready) {
            mViewLayoutParams.bottomMargin = mMarginEnd
            mAnimatedView.requestLayout()

            if (mIsVisibleAfter) {
                mAnimatedView.visibility = View.GONE
            }
            mWasEndedAlready = true
        }
    }

    companion object {
        private fun setClipView(view: View?, clip: Boolean) {
            if (view != null) {
                val parent: ViewParent? = view.parent
                if (parent is ViewGroup) {
                    parent.clipChildren = clip
                    parent.clipToPadding = clip
                }
            }
        }
    }
}
