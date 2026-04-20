package com.sepidsa.fortytwocalculator

import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView

class CustomDialogClass(context: Context, theme: Int) : Dialog(context, theme), View.OnClickListener {

    private val mRobotoLight: Typeface = Typeface.createFromAsset(context.assets, "roboto_light.ttf")
    private val mRobotoRegular: Typeface = Typeface.createFromAsset(context.assets, "roboto_regular.ttf")
    private val mYekan: Typeface = Typeface.createFromAsset(context.assets, "yekan.ttf")
    private val mRobotoThin: Typeface = Typeface.createFromAsset(context.assets, "roboto_thin.ttf")
    private val mMitra: Typeface = Typeface.createFromAsset(context.assets, "mitra.ttf")
    private val mDastnevis: Typeface? = null

    var sendFeedBack: Button? = null
    private val mContext: Context = context

    private lateinit var dialpadTextSizeButton: Button

    private lateinit var fortyTwoSample: AutoResizeTextView
    private lateinit var fortyTwoSampleTranslation: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog)

        val contactUsButton = findViewById<Button>(R.id.btn_CHANGE_TRANSLATION_FONT)
        contactUsButton.setOnClickListener(this)
        contactUsButton.typeface = mYekan

        val appVersion = findViewById<TextView>(R.id.scientific_mode_textview)
        try {
            appVersion.text = "ver ${(mContext as MainActivity).getVersion()}"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        dialpadTextSizeButton = findViewById(R.id.dialpad_text_size)
        dialpadTextSizeButton.typeface = mYekan
        dialpadTextSizeButton.setOnClickListener(this)

        val giveStars = findViewById<Button>(R.id.give_stars)
        giveStars.setOnClickListener(this)
        giveStars.typeface = mYekan

        fortyTwoSample = findViewById(R.id.fortyTwoSample)
        fortyTwoSample.setOnClickListener(this)
        fortyTwoSampleTranslation = findViewById(R.id.fortyTwoSampleTranslation)

        setDialpadTextSizeButtonTypeface()
        setPersianTranslationButtonTypeface()
        refreshSampleTypeface((mContext as MainActivity).translationLanguage)

        val tips = findViewById<Button>(R.id.language_selection)
        tips.setOnClickListener(this)
        tips.typeface = mYekan
    }

    private fun setDialpadTextSizeButtonTypeface() {
        val appPreferences = mContext.getSharedPreferences("typography", Context.MODE_PRIVATE)
        when (appPreferences.getInt("DIALPAD_FONT", MainActivity.FONT_ROBOTO_THIN)) {
            MainActivity.FONT_ROBOTO_THIN -> fortyTwoSample.typeface = mRobotoThin
            MainActivity.FONT_ROBOTO_LIGHT -> fortyTwoSample.typeface = mRobotoLight
            MainActivity.FONT_ROBOTO_REGULAR -> fortyTwoSample.typeface = mRobotoRegular
        }
    }

    private fun setPersianTranslationButtonTypeface() {
        val appPreferences = mContext.getSharedPreferences("typography", Context.MODE_PRIVATE)
        when (appPreferences.getInt("PERSIAN_FONT_PREFERENCE", MainActivity.FONT_MITRA)) {
            MainActivity.FONT_MITRA -> fortyTwoSampleTranslation.typeface = mMitra
            6 -> fortyTwoSampleTranslation.typeface = mDastnevis
            else -> fortyTwoSampleTranslation.typeface = mMitra
        }
    }

    // Send an Intent with an action named "my-event".
    private fun sendChangeDialpadTypefaceMessage(font: Int) {
        (mContext as MainActivity).setFontForComponent("DIALPAD_FONT", font)
        val intent = Intent("themeIntent")
        intent.putExtra("message", "changeDialpadFont")
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent)
    }

    override fun onClick(v: View) {
        val id = v.id

        if (id == R.id.fortyTwoSample || id == R.id.give_stars) {
            val packageName = mContext.packageName
            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName"),
            )

            try {
                mContext.startActivity(marketIntent)
            } catch (_: ActivityNotFoundException) {
                mContext.startActivity(webIntent)
            }
            dismiss()
        } else if (id == R.id.language_selection) {
            showSpinner()
        } else if (id == R.id.dialpad_text_size) {
            val fontSizePreference = mContext.getSharedPreferences("typography", Context.MODE_PRIVATE)
            val fontSizeEditor = fontSizePreference.edit()
            when (fontSizePreference.getInt("DIALPAD_FONT", MainActivity.FONT_ROBOTO_THIN)) {
                MainActivity.FONT_ROBOTO_REGULAR -> {
                    fontSizeEditor.putInt("DIALPAD_FONT", MainActivity.FONT_ROBOTO_THIN)
                    fontSizeEditor.apply()
                    fortyTwoSample.typeface = mRobotoThin
                    sendChangeDialpadTypefaceMessage(MainActivity.FONT_ROBOTO_THIN)
                }

                MainActivity.FONT_ROBOTO_THIN -> {
                    fontSizeEditor.putInt("DIALPAD_FONT", MainActivity.FONT_ROBOTO_LIGHT)
                    fontSizeEditor.apply()
                    fortyTwoSample.typeface = mRobotoLight
                    sendChangeDialpadTypefaceMessage(MainActivity.FONT_ROBOTO_LIGHT)
                }

                MainActivity.FONT_ROBOTO_LIGHT -> {
                    fontSizeEditor.putInt("DIALPAD_FONT", MainActivity.FONT_ROBOTO_REGULAR)
                    fontSizeEditor.apply()
                    fortyTwoSample.typeface = mRobotoRegular
                    sendChangeDialpadTypefaceMessage(MainActivity.FONT_ROBOTO_REGULAR)
                }
            }
            setDialpadTextSizeButtonTypeface()
            (mContext as MainActivity).sendChangeFontThicknessMessage()
        } else if (id == R.id.btn_CHANGE_TRANSLATION_FONT) {
            dismiss()
        }
    }

    fun showSpinner() {
        val b = AlertDialog.Builder(mContext)
        // todo google play edition
        val options = arrayOf("فارسی", "انگلیسی", "فرانسه", "عربی")
        b.setTitle("زبان نتیجه محاسبه")
        b.setSingleChoiceItems(options, -1) { dialog: DialogInterface, which: Int ->
            val appPreferences = mContext.applicationContext.getSharedPreferences(
                "LanguagePreference",
                Context.MODE_PRIVATE,
            )
            val editor = appPreferences.edit()

            dialog.dismiss()
            val language = when (which) {
                0 -> MainActivity.LANGUAGE_PERSIAN.toInt()
                1 -> MainActivity.LANGUAGE_ENGLISH.toInt()
                2 -> MainActivity.LANGUAGE_FRENCH.toInt()
                3 -> MainActivity.LANGUAGE_ARABIC.toInt()
                else -> MainActivity.LANGUAGE_PERSIAN.toInt()
            }
            editor.putInt("LANGUAGE", language)
            refreshSampleTypeface(language)
            (mContext as MainActivity).calculatorViewModel.setLanguage(language)
            editor.commit()

            fortyTwoSampleTranslation.typeface = (mContext as MainActivity).getFontForComponent("TRANSLATION_LITERAL_FONT")

            (mContext as MainActivity).refreshFonts()
        }
        b.show()
    }

    private fun refreshSampleTypeface(language: Int) {
        when (language.toByte()) {
            MainActivity.LANGUAGE_PERSIAN -> {
                fortyTwoSampleTranslation.text = mContext.getString(R.string.persian_42)
                (mContext as MainActivity).setFontForComponent("TRANSLATION_LITERAL_FONT", MainActivity.FONT_MITRA)
            }

            MainActivity.LANGUAGE_ENGLISH -> {
                fortyTwoSampleTranslation.text = mContext.getString(R.string.english_42)
                (mContext as MainActivity).setFontForComponent(
                    "TRANSLATION_LITERAL_FONT",
                    MainActivity.FONT_ROBOTO_THIN,
                )
            }

            MainActivity.LANGUAGE_FRENCH -> {
                fortyTwoSampleTranslation.text = mContext.getString(R.string.french_42)
                (mContext as MainActivity).setFontForComponent(
                    "TRANSLATION_LITERAL_FONT",
                    MainActivity.FONT_ROBOTO_THIN,
                )
            }

            MainActivity.LANGUAGE_ARABIC -> {
                (mContext as MainActivity).setFontForComponent("TRANSLATION_LITERAL_FONT", MainActivity.FONT_MAJALLA)
                fortyTwoSampleTranslation.text = mContext.getString(R.string.arabic_42)
            }
        }
    }

    private companion object {
        private const val PERSIAN_TRANSLATION_FONT_DASTNEVIS = 6
    }
}
