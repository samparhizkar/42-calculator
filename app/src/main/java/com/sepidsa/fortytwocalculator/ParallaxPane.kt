package com.sepidsa.fortytwocalculator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ParallaxPane : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutId = arguments?.getInt(LAYOUT_ID, -1) ?: -1
        return inflater.inflate(layoutId, container, false) as ViewGroup
    }

    companion object {
        const val LAYOUT_ID: String = "layoutid"

        @JvmStatic
        fun newInstance(layoutId: Int): ParallaxPane {
            val pane = ParallaxPane()
            val args = Bundle()
            args.putInt(LAYOUT_ID, layoutId)
            pane.arguments = args
            return pane
        }
    }
}

