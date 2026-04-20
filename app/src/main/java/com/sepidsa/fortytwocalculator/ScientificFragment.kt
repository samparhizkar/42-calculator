package com.sepidsa.fortytwocalculator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.ToggleButton

/**
 * Created by Ehsan on 5/29/14.
 */
class ScientificFragment : Fragment(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private lateinit var mView: View

    private lateinit var mThemeChangedReciever: BroadcastReceiver
    private var defaultFont: Typeface? = null
    private lateinit var idList: IntArray
    private var inversed: Boolean = false
    private var arcIsOn: Boolean = false
    private var scientificToggleTextSize: Float = 18f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mView = inflater.inflate(R.layout.fragment_scientific_flat, container, false)
        idList = getScientificButtonsID()
        for (id in idList) {
            val v = mView.findViewById<View>(id)
            if (v != null) {
                v.setOnClickListener(this)
                if (v is Button) {
                    v.typeface = Typeface.createFromAsset(requireActivity().assets, "roboto_light.ttf")
                }
            }
        }
        if (mView.findViewById<View>(R.id.switch_deg_rad) != null) {
            (mView.findViewById<View>(R.id.buttonConstant) as Button).typeface =
                Typeface.createFromAsset(requireActivity().assets, "lotus.ttf")
            (mView.findViewById<View>(R.id.switch_deg_rad) as ToggleButton).isChecked =
                (activity as MainActivity).angleMode
            (mView.findViewById<View>(R.id.switch_deg_rad) as ToggleButton).setOnCheckedChangeListener(this)
            mView.findViewById<View>(R.id.switch_deg_rad).setOnClickListener(this)
        }
        mThemeChangedReciever = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val message = intent.getStringExtra("message")
                when (message) {
                    "changeKeypadFontColor" -> setTextColorState(idList, nonAccentColorStateList)
                }
            }
        }

        return mView
    }

    private fun setTextColorState(buttonsIdArray: IntArray, textColor: ColorStateList) {
        for (buttonId in buttonsIdArray) {
            val v = mView.findViewById<View>(buttonId)
            if (v is Button) {
                v.setTextColor(textColor)
            }
        }
    }

    private val nonAccentColorStateList: ColorStateList
        get() {
            val states = stateAray
            val colors = intArrayOf(
                (activity as MainActivity).accentColorCode,
                (activity as MainActivity).dialpadFontColor,
            )

            return ColorStateList(states, colors)
        }

    private val stateAray: Array<IntArray>
        get() = arrayOf(
            intArrayOf(android.R.attr.state_pressed),
            intArrayOf(-android.R.attr.state_pressed),
        )

    override fun onClick(view: View) {
        val id = view.id

        if (id == R.id.buttonInverse) {
            if ((view as ToggleButton).isChecked) {
                applyInverse(true)
            } else {
                applyInverse(false)
            }
        } else if (id == R.id.buttonARC) {
            if ((view as ToggleButton).isChecked) {
                arcIsOn = true
                applyArc(true)
            } else {
                arcIsOn = false
                applyArc(false)
            }
        } else if (id == R.id.switch_deg_rad) {
            val on = (view as ToggleButton).isChecked
            if (on) {
                // result shown  in Degress
                (activity as MainActivity).setAngleMode(true)
            } else {
                // result shown in Radian
                (activity as MainActivity).setAngleMode(false)
            }
        } else if (id == R.id.buttonConstant) {
            val fm: FragmentManager = (activity as MainActivity).supportFragmentManager
            val constantUseDialog = ConstantUseFragment()
            constantUseDialog.show(fm, "fragment_constant_use")
        } else {
            if (view.tag.toString() == "trigonomic") {
                (activity as MainActivity).aButtonIsPressed((view as Button).text.toString() + "(")
            } else {
                (activity as MainActivity).aButtonIsPressed(view.tag.toString())
            }
            sendClearButtonIntent()
        }
    }

    private fun applyArc(isArc: Boolean) {
        if (isArc) {
            (mView.findViewById<View>(R.id.buttonSinus) as Button).text =
                "a" + (mView.findViewById<View>(R.id.buttonSinus) as Button).text
            (mView.findViewById<View>(R.id.buttonCosinus) as Button).text =
                "a" + (mView.findViewById<View>(R.id.buttonCosinus) as Button).text
            (mView.findViewById<View>(R.id.buttonTan) as Button).text =
                "a" + (mView.findViewById<View>(R.id.buttonTan) as Button).text

            (mView.findViewById<View>(R.id.buttonSinusH) as Button).text =
                "a" + (mView.findViewById<View>(R.id.buttonSinusH) as Button).text
            (mView.findViewById<View>(R.id.buttonCosinusH) as Button).text =
                "a" + (mView.findViewById<View>(R.id.buttonCosinusH) as Button).text
            (mView.findViewById<View>(R.id.buttonTanH) as Button).text =
                "a" + (mView.findViewById<View>(R.id.buttonTanH) as Button).text
        } else {
            (mView.findViewById<View>(R.id.buttonSinus) as Button).text =
                (mView.findViewById<View>(R.id.buttonSinus) as Button).text.toString().substring(1)
            (mView.findViewById<View>(R.id.buttonCosinus) as Button).text =
                (mView.findViewById<View>(R.id.buttonCosinus) as Button).text.toString().substring(1)
            (mView.findViewById<View>(R.id.buttonTan) as Button).text =
                (mView.findViewById<View>(R.id.buttonTan) as Button).text.toString().substring(1)

            (mView.findViewById<View>(R.id.buttonSinusH) as Button).text =
                (mView.findViewById<View>(R.id.buttonSinusH) as Button).text.toString().substring(1)
            (mView.findViewById<View>(R.id.buttonCosinusH) as Button).text =
                (mView.findViewById<View>(R.id.buttonCosinusH) as Button).text.toString().substring(1)
            (mView.findViewById<View>(R.id.buttonTanH) as Button).text =
                (mView.findViewById<View>(R.id.buttonTanH) as Button).text.toString().substring(1)
        }
    }

    private fun applyInverse(isOn: Boolean) {
        if (isOn) {
            mView.findViewById<View>(R.id.buttonpie).tag = resources.getString(R.string.neper)
            (mView.findViewById<View>(R.id.buttonpie) as Button).text = resources.getString(R.string.neper)

            if (arcIsOn) {
                (mView.findViewById<View>(R.id.buttonSinus) as Button).text = "a" + resources.getString(R.string.csc)
                (mView.findViewById<View>(R.id.buttonCosinus) as Button).text = "a" + resources.getString(R.string.sec)
                (mView.findViewById<View>(R.id.buttonTan) as Button).text = "a" + resources.getString(R.string.taninverse)

                (mView.findViewById<View>(R.id.buttonSinusH) as Button).text = "a" + resources.getString(R.string.csch)
                (mView.findViewById<View>(R.id.buttonCosinusH) as Button).text = "a" + resources.getString(R.string.sech)
                (mView.findViewById<View>(R.id.buttonTanH) as Button).text = "a" + resources.getString(R.string.coth)
            } else {
                (mView.findViewById<View>(R.id.buttonSinus) as Button).text = resources.getString(R.string.csc)
                (mView.findViewById<View>(R.id.buttonCosinus) as Button).text = resources.getString(R.string.sec)
                (mView.findViewById<View>(R.id.buttonTan) as Button).text = resources.getString(R.string.taninverse)

                (mView.findViewById<View>(R.id.buttonSinusH) as Button).text = resources.getString(R.string.csch)
                (mView.findViewById<View>(R.id.buttonCosinusH) as Button).text = resources.getString(R.string.sech)
                (mView.findViewById<View>(R.id.buttonTanH) as Button).text = resources.getString(R.string.coth)
            }
        } else {
            mView.findViewById<View>(R.id.buttonpie).tag = resources.getString(R.string.Pi)
            (mView.findViewById<View>(R.id.buttonpie) as Button).text = resources.getString(R.string.Pi)

            if (arcIsOn) {
                (mView.findViewById<View>(R.id.buttonSinus) as Button).text = "a" + resources.getString(R.string.sin)
                (mView.findViewById<View>(R.id.buttonCosinus) as Button).text = "a" + resources.getString(R.string.cos)
                (mView.findViewById<View>(R.id.buttonTan) as Button).text = "a" + resources.getString(R.string.tan)

                (mView.findViewById<View>(R.id.buttonSinusH) as Button).text = "a" + resources.getString(R.string.sinh)
                (mView.findViewById<View>(R.id.buttonCosinusH) as Button).text = "a" + resources.getString(R.string.cosh)
                (mView.findViewById<View>(R.id.buttonTanH) as Button).text = "a" + resources.getString(R.string.tanh)
            } else {
                (mView.findViewById<View>(R.id.buttonSinus) as Button).text = resources.getString(R.string.sin)
                (mView.findViewById<View>(R.id.buttonCosinus) as Button).text = resources.getString(R.string.cos)
                (mView.findViewById<View>(R.id.buttonTan) as Button).text = resources.getString(R.string.tan)

                (mView.findViewById<View>(R.id.buttonSinusH) as Button).text = resources.getString(R.string.sinh)
                (mView.findViewById<View>(R.id.buttonCosinusH) as Button).text = resources.getString(R.string.cosh)
                (mView.findViewById<View>(R.id.buttonTanH) as Button).text = resources.getString(R.string.tanh)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        redrawKeypadInFlatTheme()
        LocalBroadcastManager.getInstance(requireActivity().applicationContext)
            .registerReceiver(mThemeChangedReciever, IntentFilter("themeIntent"))
    }

    private fun redrawKeypadInFlatTheme() {
        idList = getScientificButtonsID()
        for (id in idList) {
            val v = mView.findViewById<View>(id)
            if (v is Button) {
                v.setTextColor((activity as MainActivity).dialpadFontColor)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireActivity().applicationContext).unregisterReceiver(mThemeChangedReciever)
    }

    // Send an Intent with an action named "clearintent". when a scientific button is pressed clear button's text should change to <- backspace
    private fun sendClearButtonIntent() {
        val intent = Intent("clearIntent")
        intent.putExtra("buttonValue", resources.getString(R.string.backSpace))
        LocalBroadcastManager.getInstance(requireActivity().applicationContext).sendBroadcast(intent)
    }

    private fun getScientificButtonsID(): IntArray {
        return intArrayOf(
            R.id.buttonSinus, R.id.buttonCosinus, R.id.buttonTan, R.id.buttonln, R.id.buttonlog, R.id.buttonlog, R.id.buttonpie, R.id.buttonEXP, R.id.buttonpower,
            R.id.buttonrad, R.id.buttonfact, R.id.buttonPow2, R.id.buttonPow3, R.id.buttonSinusH, R.id.buttonCosinusH, R.id.buttonTanH, R.id.buttonRandom,
            R.id.buttonConstant, R.id.buttonARC, R.id.switch_deg_rad, R.id.buttonInverse,
        )
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        val on = (buttonView as ToggleButton).isChecked

        if (on) {
            // result in Degress
            (activity as MainActivity).setAngleMode(true)
        } else {
            // result in Radian
            (activity as MainActivity).setAngleMode(false)
        }
    }

    companion object {
        const val EXTRA_MESSAGE: String = "EXTRA_MESSAGE"

        @JvmStatic
        fun newInstance(message: String): ScientificFragment {
            val f = ScientificFragment()
            val bdl = Bundle(1)
            bdl.putString(EXTRA_MESSAGE, message)
            f.arguments = bdl
            return f
        }
    }
}

