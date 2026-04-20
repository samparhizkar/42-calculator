package com.sepidsa.fortytwocalculator

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout

class ParallaxPagerActivity : FragmentActivity() {

    private lateinit var pager: ViewPager
    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var circles: LinearLayout
    private lateinit var skip: Button
    private lateinit var done: Button
    private lateinit var next: ImageButton

    /*
        This is nasty but as the transparency of the fragments increases when swiping the underlying
        Activity becomes visible, so we change the pager opacity on the last slide in
        setOnPageChangeListener below
     */
    private var isOpaque: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_parallax_pager)

        skip = findViewById<Button>(R.id.skip).also {
            it.setOnClickListener { endTutorial() }
        }

        next = findViewById<ImageButton>(R.id.next).also {
            it.setOnClickListener { pager.setCurrentItem(pager.currentItem + 1, true) }
        }

        done = findViewById<Button>(R.id.done).also {
            it.setOnClickListener { endTutorial() }
        }

        pager = findViewById(R.id.pager)
        pager.offscreenPageLimit = 5
        pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        pager.adapter = pagerAdapter
        pager.setPageTransformer(true, CrossfadePageTransformer())
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // See note above for why this is needed
                if (position == NUM_PAGES - 2 && positionOffset > 0) {
                    if (isOpaque) {
                        pager.setBackgroundColor(Color.TRANSPARENT)
                        isOpaque = false
                    }
                } else {
                    if (!isOpaque) {
                        pager.setBackgroundColor(context.getColor(R.color.tutorial_background_opaque))
                        isOpaque = true
                    }
                }
            }

            override fun onPageSelected(position: Int) {
                setIndicator(position)
                if (position == NUM_PAGES - 2) {
                    skip.visibility = View.GONE
                    next.visibility = View.GONE
                    done.visibility = View.VISIBLE
                } else if (position < NUM_PAGES - 2) {
                    skip.visibility = View.VISIBLE
                    next.visibility = View.VISIBLE
                    done.visibility = View.GONE
                } else if (position == NUM_PAGES - 1) {
                    endTutorial()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                // Unused
            }
        })

        buildCircles()
    }

    /*
        The last fragment is transparent to enable the swipe-to-finish behaviour seen on Google's apps
        So our viewpager circle indicator needs to show NUM_PAGES - 1
     */
    private fun buildCircles() {
        circles = findViewById(R.id.circles)

        val scale = resources.displayMetrics.density
        val padding = (5 * scale + 0.5f).toInt()

        for (i in 0 until NUM_PAGES - 1) {
            val circle = ImageView(this)
            circle.setImageResource(R.drawable.empty_circle)
            circle.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            circle.adjustViewBounds = true
            circle.setPadding(padding, 0, padding, 0)
            circles.addView(circle)
        }

        setIndicator(0)
    }

    private fun setIndicator(index: Int) {
        if (index < NUM_PAGES) {
            for (i in 0 until NUM_PAGES - 1) {
                val circle = circles.getChildAt(i) as ImageView
                if (i == index) {
                    circle.setImageResource(R.drawable.full_circle)
                } else {
                    circle.setImageResource(R.drawable.empty_circle)
                }
            }
        }
    }

    private fun endTutorial() {
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onBackPressed() {
        if (pager.currentItem == 0) {
            super.onBackPressed()
        } else {
            pager.setCurrentItem(pager.currentItem - 1)
        }
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        private val mFragments: Array<Fragment?> = arrayOfNulls(NUM_PAGES)

        init {
            mFragments[0] = ParallaxPane.newInstance(R.layout.fragment_app_tour_pane_one)
            mFragments[1] = ParallaxPane.newInstance(R.layout.fragment_app_tour_pane_two)
            mFragments[2] = ParallaxPane.newInstance(R.layout.fragment_app_tour_pane_three)
            mFragments[3] = ParallaxPane.newInstance(R.layout.fragment_app_tour_pane_four)
            // mFragments[4] = ParallaxPane.newInstance(R.layout.fragment_app_tour_pane_five);
            mFragments[4] = ParallaxPane.newInstance(R.layout.fragment_parallax_pane_transparent)
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]!!
        }

        override fun getCount(): Int {
            return NUM_PAGES
        }
    }

    inner class CrossfadePageTransformer : ViewPager.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            val pageWidth = page.width

            val backgroundView = page.findViewById<View>(R.id.background)
            val text = page.findViewById<View>(R.id.tour_title)

            val phone = page.findViewById<View>(R.id.tour_descriptive_icon)
            val map = page.findViewById<View>(R.id.tour_screenshot)
            val mountain: View? = null
            val mountainNight: View? = null
            val rain: View? = null
            val hands = page.findViewById<View>(R.id.tour_screenshot)

            if (position <= 1) {
                page.translationX = pageWidth * -position
            }

            if (position <= -1.0f || position >= 1.0f) {
                // no-op
            } else if (position == 0.0f) {
                // no-op
            } else {
                backgroundView?.alpha = 1.0f - kotlin.math.abs(position)

                // Text both translates in/out and fades in/out
                text?.let {
                    it.translationX = pageWidth * position
                    it.alpha = 1.0f - kotlin.math.abs(position)
                }

                // Map + tour_descriptive_icon - tour_screenshot simple translate, tour_descriptive_icon parallax effect
                map?.translationX = pageWidth * position

                phone?.translationX = (pageWidth / 1.2 * position).toFloat()

                // Mountain day - fade in/out
                mountain?.alpha = 1.0f - kotlin.math.abs(position)

                // Mountain night - fade in, but translate out, rain fades in but parallax translate out
                mountainNight?.let {
                    if (position < 0) {
                        it.translationX = pageWidth * position
                    } else {
                        it.alpha = 1.0f - kotlin.math.abs(position)
                    }
                }

                rain?.let {
                    if (position < 0) {
                        it.translationX = (pageWidth / 1.2 * position).toFloat()
                    } else {
                        it.alpha = 1.0f - kotlin.math.abs(position)
                    }
                }

                // Long click device + hands - translate both way but only fade out
                hands?.let {
                    it.translationX = pageWidth * position
                    if (position < 0) {
                        it.alpha = 1.0f - kotlin.math.abs(position)
                    }
                }
            }
        }
    }

    companion object {
        const val NUM_PAGES: Int = 5
    }
}

