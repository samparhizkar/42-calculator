package com.sepidsa.fortytwocalculator;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This animation class is animating the expanding and reducing the size of a view.
 * The animation toggles between the Expand and Reduce, depending on the current state of the view
 * @author Udinic
 *
 */
public class ExpandAnimation extends Animation {
    private View mAnimatedView;
    private ListView mListView;
    private View mParentView;
    private LayoutParams mViewLayoutParams;
    private int mMarginStart, mMarginEnd;
    private boolean mIsVisibleAfter = false;
    private boolean mWasEndedAlready = false;
    private TextView mArrow;

    /**
     * Initialize the animation
     * @param view The layout we want to animate
     * @param duration The duration of the animation, in ms
     */
    public ExpandAnimation(Context context, ListView listView, View parent, View view, int duration) {

        setDuration(duration);
        mAnimatedView = view;
        mViewLayoutParams = (LayoutParams) view.getLayoutParams();
        mListView = listView;
        mParentView = parent;

        // decide to show or hide the view
        mIsVisibleAfter = (view.getVisibility() == View.VISIBLE);

        mMarginStart = mViewLayoutParams.bottomMargin;
        mMarginEnd = (mMarginStart == 0 ? (0- view.getHeight()) : 0);

        mArrow = (TextView) parent.findViewById(R.id.arrow);
//        mArrow.setTextColor(((MainActivity)context).getDialpadFontColor());

//        setClipView(mArrow, false);
//        setClipView(parent, false);
        setClipView(view, false);

        view.setVisibility(View.VISIBLE);
    }

    private static void setClipView(View view, boolean clip) {
        if (view != null) {
            ViewParent parent = view.getParent();
            if(parent instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view.getParent();
                viewGroup.setClipChildren(clip);
                viewGroup.setClipToPadding(clip);
//                setClipView(viewGroup, clip);
            }
        }
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);

        if ((interpolatedTime < 1.0f)) {
            mIsVisibleAfter = false;
            mAnimatedView.setVisibility(View.VISIBLE);
            // Calculating the new bottom margin, and setting it
            mViewLayoutParams.bottomMargin = mMarginStart
                    + (int) ((mMarginEnd - mMarginStart) * interpolatedTime);

            // Invalidating the layout, making us seeing the changes we made
            mAnimatedView.requestLayout();
            mListView.post(new Runnable() {

                @Override
                public void run() {
                    Rect rect = new Rect(mAnimatedView.getLeft(), mParentView.getTop(), mAnimatedView.getRight(), mAnimatedView.getBottom());
                    mListView.requestChildRectangleOnScreen(mParentView,
                            rect, false);
                }

            });
            if (mMarginStart != 0) {
                mArrow.setRotation(180 * interpolatedTime);
                mArrow.setTranslationY(150 * interpolatedTime);
                mArrow.setAlpha(1 - interpolatedTime);
            } else {
                mArrow.setRotation(180 * (1 - interpolatedTime));
                mArrow.setTranslationY(150 * (1 - interpolatedTime));
                mArrow.setAlpha(interpolatedTime);
                            }
            // Making sure we didn't run the ending before (it happens!)
        } else if (!mWasEndedAlready) {
            mViewLayoutParams.bottomMargin = mMarginEnd;
            mAnimatedView.requestLayout();

            if (mIsVisibleAfter) {
                mAnimatedView.setVisibility(View.GONE);
            }
            mWasEndedAlready = true;
        }
    }
}
