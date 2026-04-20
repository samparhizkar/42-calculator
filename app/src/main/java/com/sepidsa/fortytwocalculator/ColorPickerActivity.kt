package com.sepidsa.fortytwocalculator

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentActivity

class ColorPickerActivity : FragmentActivity(), ColorPickerSwatch.OnColorSelectedListener, View.OnClickListener {

    var mAccentcolorCode: Int = 0
    var mKeypadBackgroundColorCode: Int = 0
    private lateinit var mAccentLayout: View
    private lateinit var mKeypadLayout: View
    private lateinit var mACcentPallete: ColorPickerPalette
    private lateinit var mKeypadPallete: ColorPickerPalette
    private lateinit var mYekanFont: Typeface
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mYekanFont = Typeface.createFromAsset(assets, "yekan.ttf")

        setContentView(R.layout.activity_color_picker)
        mAccentLayout = findViewById(R.id.layout_color_picker_accent)
        mKeypadLayout = findViewById(R.id.layout_color_picker_keypad)

        setTypefaces()
        backButton = findViewById(R.id.button_back)
        backButton.setOnClickListener(this)

        mAccentcolorCode = intent.getIntExtra("accentColor", Color.parseColor("#1abc9c"))
        mKeypadBackgroundColorCode = intent.getIntExtra("keyPadColor", Color.WHITE)

        mACcentPallete = findViewById(R.id.color_picker_accent)
        mACcentPallete.init(18, 6, this)
        mACcentPallete.isSelected = true

        refreshPalette(
            mACcentPallete,
            Utils.ColorUtils.colorChoice(applicationContext),
            indexOf(Utils.ColorUtils.colorChoice(applicationContext), accentColorCode),
        )
        onColorSelected(accentColorCode)

        mKeypadPallete = findViewById(R.id.color_picker_keypad)
        mKeypadPallete.init(18, 6, this)
        mKeypadPallete.isSelected = true

        refreshPalette(
            mKeypadPallete,
            Utils.ColorUtils.colorChoiceForKeypad(applicationContext),
            indexOf(Utils.ColorUtils.colorChoiceForKeypad(applicationContext), keypadBackgroundColorCode),
        )
        onColorSelected(accentColorCode)

        mAccentLayout.setBackgroundColor(accentColorCode)
        mKeypadLayout.setBackgroundColor(keypadBackgroundColorCode)
    }

    private fun setTypefaces() {
        var fontChaange = findViewById<TextView>(R.id.textview_accent)
//        fontChaange.typeface = mYekanFont
//        fontChaange = findViewById(R.id.textview_use_classic)
        fontChaange.typeface = mYekanFont
        fontChaange = findViewById(R.id.textView_keypad)
        fontChaange.typeface = mYekanFont
        fontChaange.setTextColor(dialpadFontColor)
    }

    val dialpadFontColor: Int
        get() {
            val colorArray = resources.getStringArray(R.array.dialpad_font_color_choice_values)
            val indexOfCurrentBackgroundColor =
                indexOf(Utils.ColorUtils.colorChoiceForKeypad(applicationContext), keypadBackgroundColorCode)
            return Color.parseColor(colorArray[indexOfCurrentBackgroundColor])
        }

    override fun getIntent(): Intent {
        return super.getIntent()
    }

    private fun refreshPalette(pallette: ColorPickerPalette?, colors: IntArray?, selectedColor: Int) {
        if (pallette != null && colors != null) {
            pallette.drawPalette(colors, selectedColor)
        }
    }

    private fun arrayContains(parent: IntArray?, child: Int): Int {
        if (parent == null) return -1
        for (index in parent) {
            if (index == child) {
                return index
            }
        }
        return -1
    }

    private fun indexOf(parent: IntArray?, child: Int): Int {
        if (parent == null) return 0
        for (index in parent.indices) {
            if (parent[index] == child) {
                return index
            }
        }
        return 0
    }

    override fun onColorSelected(color: Int) {
        val temp = Utils.ColorUtils.colorChoice(applicationContext)
        val selectedAccentColor = arrayContains(temp, color) != -1
        if (selectedAccentColor) {
            saveAccentColorCode(color)
            mAccentLayout.setBackgroundColor(accentColorCode)
            mACcentPallete.drawPalette(Utils.ColorUtils.colorChoice(applicationContext), color, null)
        } else {
            saveKeypadBackgroundColorCode(color)
            mKeypadLayout.setBackgroundColor(keypadBackgroundColorCode)
            mKeypadPallete.drawPalette(Utils.ColorUtils.colorChoiceForKeypad(applicationContext), color, null)
            val fontChaange = findViewById<TextView>(R.id.textView_keypad)
            fontChaange.setTextColor(dialpadFontColor)
        }
    }

    // Saving the selected color theme to prefrence
    fun saveAccentColorCode(colorCode: Int) {
        val appPreferences = applicationContext.getSharedPreferences("THEME", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = appPreferences.edit()
        editor.putInt("ACCENT_COLOR_CODE", colorCode)
        editor.commit()
    }

    val accentColorCode: Int
        get() {
            val appPreferences = applicationContext.getSharedPreferences("THEME", MODE_PRIVATE)
            return appPreferences.getInt("ACCENT_COLOR_CODE", Color.parseColor("#009688"))
        }

    val keypadBackgroundColorCode: Int
        get() {
            val appPreferences = applicationContext.getSharedPreferences("THEME", Context.MODE_PRIVATE)
            return appPreferences.getInt("KEYPAD_BACKGROUND_COLOR_CODE", Color.WHITE)
        }

    private fun saveKeypadBackgroundColorCode(themeNumber: Int) {
        val appPreferences = applicationContext.getSharedPreferences("THEME", Context.MODE_PRIVATE)
        val editor = appPreferences.edit()
        editor.putInt("KEYPAD_BACKGROUND_COLOR_CODE", themeNumber)
        editor.commit()
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.button_back) {
            finish()
        }
    }

    companion object {
        const val SIZE_LARGE: Int = 1
    }
}

