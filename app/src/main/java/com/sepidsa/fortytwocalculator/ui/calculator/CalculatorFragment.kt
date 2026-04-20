package com.sepidsa.fortytwocalculator.ui.calculator

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.ToggleButton
import com.sepidsa.fortytwocalculator.ui.currency.CurrencyFragment

/**
 * @author Ehsan
 */
class CalculatorFragment : Fragment(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private val viewModel: CalculatorViewModel by viewModels()

    private var arcIsOn: Boolean = false
    private val TAG: String = "recreate"
    private lateinit var mView: View
    private lateinit var defaultFont: Typeface

    private lateinit var idList: IntArray
    private lateinit var scientificFont: Typeface

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d(TAG, "Fragment OnCreateView")
        mView = inflater.inflate(R.layout.fragment_dialpad_flat, container, false)

        // Set the listener for all the gray_buttons
        idList = getAllButtonsID()

        defaultFont = (activity as MainActivity).getFontForComponent("DIALPAD_FONT")
        scientificFont = (activity as MainActivity).getFontForComponent("SCIENTIFIC_FONT")

        for (id in getDialpadButtonsID()) {
            val v = mView.findViewById<View>(id)
            if (v != null) {
                v.setOnClickListener(this)
                if (v is Button) {
                    v.typeface = defaultFont
                }
            }
        }

        for (id in getScientificButtonsID()) {
            val v = mView.findViewById<View>(id)
            if (v != null) {
                v.setOnClickListener(this)
                if (v is Button) {
                    v.typeface = scientificFont
                }
            }
        }

        if (mView.findViewById<View>(R.id.switch_deg_rad) != null) {
            (mView.findViewById<View>(R.id.switch_deg_rad) as ToggleButton).isChecked =
                (activity as MainActivity).angleMode
            (mView.findViewById<View>(R.id.switch_deg_rad) as ToggleButton).setOnCheckedChangeListener(this)
            mView.findViewById<View>(R.id.switch_deg_rad).setOnClickListener(this)
        }

        if (mView.findViewById<View>(R.id.switch_deg_rad) != null) {
            (mView.findViewById<View>(R.id.switch_deg_rad) as ToggleButton).isChecked =
                (activity as MainActivity).angleMode
            (mView.findViewById<View>(R.id.switch_deg_rad) as ToggleButton).setOnCheckedChangeListener(this)
            (mView.findViewById<View>(R.id.buttonConstant) as Button).typeface =
                Typeface.createFromAsset(requireActivity().assets, "lotus.ttf")
            (mView.findViewById<View>(R.id.switch_deg_rad)).setOnClickListener(this)
            (mView.findViewById<View>(R.id.buttonConstant)).setOnClickListener(this)
            (mView.findViewById<View>(R.id.switch_deg_rad) as ToggleButton).textSize = scientificToggleTextSize
            (mView.findViewById<View>(R.id.buttonInverse) as ToggleButton).textSize = scientificToggleTextSize
            (mView.findViewById<View>(R.id.buttonARC) as ToggleButton).textSize = scientificToggleTextSize
        }

        mView.findViewById<View>(R.id.buttonClear).setOnLongClickListener {
            viewModel.onButtonPressed("C")
            setClearButtonText(resources.getString(R.string.clear))
            true
        }

        mView.findViewById<View>(R.id.buttonEquals).setOnLongClickListener {
            viewModel.onButtonPressed("MR")
            true
        }

        mView.findViewById<View>(R.id.buttonTimes).setOnLongClickListener {
            viewModel.onButtonPressed("MC")
            true
        }

        mView.findViewById<View>(R.id.buttonMinus).setOnLongClickListener {
            viewModel.onButtonPressed("M-")
            setClearButtonText(resources.getString(R.string.clear))
            true
        }
        mView.findViewById<View>(R.id.buttonPlus).setOnLongClickListener {
            viewModel.onButtonPressed("M+")
            true
        }

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                (activity as MainActivity).updateExpression(state.expression)
                (activity as MainActivity).updateResult(state.result)
                // TODO: Update memory display, angle mode, etc.
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.newLogEntry.collect { (expression, result) ->
                (activity as MainActivity).addLogEntry(expression, result)
            }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Fragment onCreate")
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).checkCLRButtonSendIntent()
        redrawKeypadInFlatTheme()
        Log.d(TAG, "Fragment onStart")
    }

    fun changeFontThickness() {
        defaultFont = (activity as MainActivity).getFontForComponent("DIALPAD_FONT")
        for (id in idList) {
            val v = mView.findViewById<View>(id)
            if (v != null && v is Button) {
                v.typeface = defaultFont
            }
        }
        refreshCButtonTypeface()
    }

    override fun onResume() {
        super.onResume()
        changeFontThickness()
        Log.d(TAG, "Fragment onResume ")
    }

    override fun onClick(view: View) {
        val id = view.id

        if (id == R.id.buttonInverse) {
            if ((view as ToggleButton).isChecked) {
                applyInverse(true)
            } else {
                applyInverse(false)
            }
        } else if (id == R.id.switch_deg_rad) {
            val on = (view as ToggleButton).isChecked
            viewModel.setAngleMode(on)
        } else if (id == R.id.buttonARC) {
            if ((view as ToggleButton).isChecked) {
                arcIsOn = true
                applyArc(true)
            } else {
                arcIsOn = false
                applyArc(false)
            }
        } else if (id == R.id.buttonConstant) {
            val fm: FragmentManager = requireActivity().supportFragmentManager
            val constantUseDialog = CurrencyFragment()
            constantUseDialog.show(fm, "fragment_constant_use")
        } else {
            val buttonValue = if (view.tag.toString() == "trigonomic") {
                (view as Button).text.toString() + "("
            } else {
                (view as Button).tag.toString()
            }
            viewModel.onButtonPressed(buttonValue)
        }
    }

    private fun getAllButtonsID(): IntArray {
        return intArrayOf(
            R.id.button9, R.id.button8, R.id.button7,
            R.id.button6, R.id.button5, R.id.button4, R.id.button1, R.id.button2, R.id.button3, R.id.button0,
            R.id.buttonPlus, R.id.buttonMinus, R.id.buttonDevide, R.id.buttonTimes, R.id.buttonEquals, R.id.buttonClear, R.id.buttonPoint,
            R.id.openParen, R.id.closeParen, R.id.buttonPercent,
            R.id.buttonSinus, R.id.buttonCosinus, R.id.buttonTan, R.id.buttonln, R.id.buttonlog, R.id.buttonlog, R.id.buttonpie, R.id.buttonEXP, R.id.buttonpower,
            R.id.buttonrad, R.id.buttonfact, R.id.switch_deg_rad, R.id.buttonPow2, R.id.buttonPow3, R.id.buttonInverse, R.id.buttonSinusH, R.id.buttonCosinusH, R.id.buttonTanH, R.id.buttonRandom,
            R.id.buttonConstant, R.id.buttonARC,
        )
    }

    private fun getOperatorButtonsID(): IntArray {
        return intArrayOf(
            R.id.buttonPlus, R.id.buttonMinus, R.id.buttonDevide, R.id.buttonTimes,
            R.id.openParen, R.id.closeParen, R.id.buttonPercent,
        )
    }

    private fun getNonAccentButtonsID(): IntArray {
        return intArrayOf(
            R.id.button9, R.id.button8, R.id.button7,
            R.id.button6, R.id.button5, R.id.button4, R.id.button1, R.id.button2, R.id.button3, R.id.button0,
            R.id.buttonPoint,
            R.id.buttonSinus, R.id.buttonCosinus, R.id.buttonTan, R.id.buttonln, R.id.buttonlog, R.id.buttonlog, R.id.buttonpie, R.id.buttonEXP, R.id.buttonpower,
            R.id.buttonrad, R.id.buttonfact, R.id.switch_deg_rad, R.id.buttonPow2, R.id.buttonPow3, R.id.buttonInverse, R.id.buttonSinusH, R.id.buttonCosinusH, R.id.buttonTanH, R.id.buttonRandom,
            R.id.buttonConstant, R.id.buttonARC,
        )
    }

    private fun getScientificButtonsID(): IntArray {
        return intArrayOf(
            R.id.buttonSinus, R.id.buttonCosinus, R.id.buttonTan, R.id.buttonln, R.id.buttonlog, R.id.buttonlog, R.id.buttonpie, R.id.buttonEXP, R.id.buttonpower,
            R.id.buttonrad, R.id.buttonfact, R.id.switch_deg_rad, R.id.buttonPow2, R.id.buttonPow3, R.id.buttonInverse, R.id.buttonSinusH, R.id.buttonCosinusH, R.id.buttonTanH, R.id.buttonRandom,
            R.id.buttonConstant, R.id.buttonARC,
        )
    }

    private fun getDialpadButtonsID(): IntArray {
        return intArrayOf(
            R.id.buttonClear, R.id.buttonPlus, R.id.buttonMinus, R.id.buttonDevide, R.id.buttonTimes, R.id.buttonEquals,
            R.id.openParen, R.id.closeParen, R.id.buttonPercent,
            R.id.button9, R.id.button8, R.id.button7,
            R.id.button6, R.id.button5, R.id.button4, R.id.button1, R.id.button2, R.id.button3, R.id.button0,
            R.id.buttonPoint,
        )
    }

    private fun redrawKeypadInFlatTheme() {
        setTextColorState(getOperatorButtonsID(), getThemeColorStateList())
        setTextColorState(getNonAccentButtonsID(), getNonAccentColorStateList())
    }

    private fun getThemeColorStateList(): ColorStateList {
        val states = getStateAray()
        val colors = intArrayOf(
            Color.WHITE,
            accentColorCode,
        )

        return ColorStateList(states, colors)
    }

    private val accentColorCode: Int
        get() {
            val appPreferences: SharedPreferences = requireActivity().getSharedPreferences("THEME", Context.MODE_PRIVATE)
            return appPreferences.getInt("ACCENT_COLOR_CODE", Color.parseColor("#009688"))
        }

    private fun getNonAccentColorStateList(): ColorStateList {
        val states = getStateAray()
        val colors = intArrayOf(
            accentColorCode,
            (activity as MainActivity).dialpadFontColor,
        )

        return ColorStateList(states, colors)
    }

    private fun getStateAray(): Array<IntArray> {
        return arrayOf(
            intArrayOf(android.R.attr.state_pressed),
            intArrayOf(-android.R.attr.state_pressed),
        )
    }

    private fun setTextColorState(buttonsIdArray: IntArray, textColor: ColorStateList) {
        for (buttonId in buttonsIdArray) {
            val v = mView.findViewById<View>(buttonId)
            if (v is Button) {
                v.setTextColor(textColor)
            }
        }
    }

    private fun setBackgroundColorForOperators() {
        mView.findViewById<View>(R.id.buttonTimes).background.setColorFilter((activity as MainActivity).accentColorCode, PorterDuff.Mode.SRC_ATOP)
        mView.findViewById<View>(R.id.buttonDevide).background.setColorFilter((activity as MainActivity).accentColorCode, PorterDuff.Mode.SRC_ATOP)
        mView.findViewById<View>(R.id.buttonPlus).background.setColorFilter((activity as MainActivity).accentColorCode, PorterDuff.Mode.SRC_ATOP)
        mView.findViewById<View>(R.id.buttonMinus).background.setColorFilter((activity as MainActivity).accentColorCode, PorterDuff.Mode.SRC_ATOP)
        mView.findViewById<View>(R.id.openParen).background.setColorFilter((activity as MainActivity).accentColorCode, PorterDuff.Mode.SRC_ATOP)
        mView.findViewById<View>(R.id.closeParen).background.setColorFilter((activity as MainActivity).accentColorCode, PorterDuff.Mode.SRC_ATOP)
        mView.findViewById<View>(R.id.buttonPercent).background.setColorFilter((activity as MainActivity).accentColorCode, PorterDuff.Mode.SRC_ATOP)
    }

    fun setClearButtonText(input: String) {
        mView.findViewById<View>(R.id.buttonClear).tag = input
        refreshCButtonTypeface()
    }

    private fun refreshCButtonTypeface() {
        if (mView.findViewById<View>(R.id.buttonClear).tag == "C") {
            (mView.findViewById<View>(R.id.buttonClear) as Button).text = "C"
            (mView.findViewById<View>(R.id.buttonClear) as Button).typeface = defaultFont
            (mView.findViewById<View>(R.id.buttonClear) as Button).textSize =
                resources.getDimension(R.dimen.btn_clear_text_size) / resources.displayMetrics.density
        } else {
            (mView.findViewById<View>(R.id.buttonClear) as Button).textSize =
                resources.getDimension(R.dimen.retro_backspace_text_size) / resources.displayMetrics.density
            (mView.findViewById<View>(R.id.buttonClear) as Button).typeface =
                Typeface.createFromAsset(requireActivity().assets, "flaticon.ttf")
            (mView.findViewById<View>(R.id.buttonClear) as Button).text = resources.getString(R.string.backSpace)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "Fragment onSaveInstanceState")
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        Log.d(TAG, "Fragment onAttach ")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "Fragment onDetach")
    }

    override fun onPause() {
        Log.d(TAG, "Fragment onpause")
        super.onPause()
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        val on = (buttonView as ToggleButton).isChecked
        if (on) {
            // result in Radian
            (activity as MainActivity).setAngleMode(true)
        } else {
            // result in Degress
            (activity as MainActivity).setAngleMode(false)
        }
    }

    companion object {
        var scientificToggleTextSize: Float = 18f
    }
}

