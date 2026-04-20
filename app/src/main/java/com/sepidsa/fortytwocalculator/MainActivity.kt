package com.sepidsa.fortytwocalculator

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sepidsa.fortytwocalculator.data.ConstantEntity
import com.sepidsa.fortytwocalculator.ui.calculator.CalculatorUiEvent
import com.sepidsa.fortytwocalculator.ui.calculator.CalculatorViewModel
import com.sepidsa.fortytwocalculator.ui.calculator.SoundType
import com.sepidsa.fortytwocalculator.ui.constants.ConstantViewModel
import com.sepidsa.fortytwocalculator.ui.history.HistoryViewModel
import com.sepidsa.fortytwocalculator.ui.main.MainScreen
import com.sepidsa.fortytwocalculator.ui.theme.AppTheme
import com.sepidsa.fortytwocalculator.ui.dialogs.ColorPickerDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.ArrayList
import java.util.Locale

class MainActivity : FragmentActivity() {

    private var mLatestInsertedId: Long = 0

    private lateinit var mSoundPool: SoundPool
    private var mSoundPoolLoaded: Boolean = false
    private var numericButtonSoundID: Int = 0
    private var executeButtonSoundID: Int = 0
    private var clearAllButtonSoundID: Int = 0
    private var operatorsButtonSoundID: Int = 0
    private var errorSoundID: Int = 0
    private var backSpaceButtonSoundID: Int = 0

    private var doubleBackToExitPressedOnce: Boolean = false
    private var mHandler: Handler? = null

    private val mRunnable = Runnable { doubleBackToExitPressedOnce = false }

    val historyViewModel: HistoryViewModel by viewModels()
    val constantViewModel: ConstantViewModel by viewModels()
    val calculatorViewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        showSplashAndTour()

        setContentView(ComposeView(this).apply {
            setContent {
                val calculatorState by calculatorViewModel.uiState.collectAsStateWithLifecycle()
                val historyState by historyViewModel.uiState.collectAsStateWithLifecycle()

                var showColorPicker by remember { mutableStateOf(false) }

                AppTheme {
                    MainScreen(
                        calculatorState = calculatorState,
                        historyState = historyState,
                        onCalculatorKeyPress = calculatorViewModel::onButtonPressed,
                        onAngleModeToggle = calculatorViewModel::setAngleMode,
                        onDeleteHistoryItem = historyViewModel::deleteLogEntry,
                        onStarHistoryItem = historyViewModel::starLogEntry,
                        onUpdateHistoryTag = historyViewModel::updateTag,
                        onClearHistory = historyViewModel::clearHistory,
                        onShareHistoryItem = { log -> /* Handle share */ },
                        onScientificKeyPress = calculatorViewModel::onButtonPressed,
                        onInverseToggle = { calculatorViewModel.setInverseMode(!calculatorState.inverseMode) },
                        onArcToggle = { calculatorViewModel.setArcMode(!calculatorState.arcMode) },
                        onConstantClick = { /* Show constants dialog */ },
                        onSettingsClick = {
                            // Settings dialog is now handled within MainScreen
                        },
                        onSettingsFontChanged = { fontCode ->
                            // Persist dialpad font change
                            val prefs = getSharedPreferences("typography", MODE_PRIVATE)
                            prefs.edit().putInt("DIALPAD_FONT", fontCode).apply()
                        },
                        onSettingsLanguageChanged = { langCode ->
                            // Update the calculator ViewModel language
                            calculatorViewModel.setLanguage(langCode)
                        },
                        onRateUs = {
                            displayRateUs()
                        },
                        onMuteClick = { reverseVolume() },
                        onColorsClick = { showColorPicker = true },
                        onContactUs = {
                            displayContactUs()
                        },
                        onAddStarClick = {
                            lifecycleScope.launch {
                                historyViewModel.starLogEntry(mLatestInsertedId, true)
                            }
                        },
                        onAddLabelClick = {
                            showAddLabelDialog()
                        },
                        onAboutClick = {
                            // About dialog is handled within MainScreen
                        },
                        onHelpClick = {
                            // Help dialog is handled within MainScreen
                        },
                        isMuted = !volumeFromPreference
                    )

                    if (showColorPicker) {
                        ColorPickerDialog(
                            initialAccentColor = accentColorCode,
                            initialKeypadColor = keypadBackgroundColorCode,
                            isClassicTheme = isClassicTheme,
                            onAcceptColors = { accentColor, keypadColor, useClassicTheme ->
                                saveAccentColorCode(accentColor)
                                saveKeypadBackgroundColorCode(keypadColor)
                                saveClassicTheme(useClassicTheme)
                                // Recreate activity to apply new theme colors
                                recreate()
                            },
                            onDismiss = { showColorPicker = false }
                        )
                    }
                }
            }
        })

        observeViewModel()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finish()
                    return
                }
                doubleBackToExitPressedOnce = true
                Toast.makeText(this@MainActivity, "دوباره لطفا", Toast.LENGTH_SHORT).show()
                mHandler = Handler()
                mHandler?.postDelayed(mRunnable, 2000)
            }
        })

        prepareSoundStuff()
        populateConstantDatabaseFirstRun()
    }

    private fun showAddLabelDialog() {
        lifecycleScope.launch {
            val logEntry = historyViewModel.getLogById(mLatestInsertedId)
            if (logEntry != null) {
                val currentLabel = logEntry.tag ?: ""
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle(getString(R.string.farsi_label))

                val input = EditText(this@MainActivity)
                val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

                input.inputType = InputType.TYPE_CLASS_TEXT
                builder.setView(input)
                builder.setCancelable(false)
                input.setText(currentLabel)
                input.requestFocus()
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

                builder.setPositiveButton(getString(R.string.farsi_ok)) { _, _ ->
                    val newLabel = input.text.toString()
                    historyViewModel.updateTag(mLatestInsertedId, newLabel)
                    inputMethodManager.hideSoftInputFromWindow(input.windowToken, 0)
                }
                builder.setNegativeButton(getString(R.string.farsi_cancel)) { dialog, _ ->
                    inputMethodManager.hideSoftInputFromWindow(input.windowToken, 0)
                    dialog.cancel()
                }
                val dialog = builder.create()
                dialog.show()
                val keyListener = View.OnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        when (keyCode) {
                            KeyEvent.KEYCODE_DPAD_CENTER,
                            KeyEvent.KEYCODE_ENTER -> {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick()
                                return@OnKeyListener true
                            }
                        }
                    }
                    false
                }
                input.setOnKeyListener(keyListener)
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    calculatorViewModel.uiEvents.collectLatest { event ->
                        handleUiEvent(event)
                    }
                }
                launch {
                    calculatorViewModel.newLogEntry.collectLatest { (expression, result) ->
                        mLatestInsertedId = historyViewModel.addLogEntry(expression, result).await()
                    }
                }
            }
        }
    }

    private fun handleUiEvent(event: CalculatorUiEvent) {
        when (event) {
            is CalculatorUiEvent.PlaySound -> {
                val soundId = when (event.soundType) {
                    SoundType.Numeric -> numericButtonSoundID
                    SoundType.Operator -> operatorsButtonSoundID
                    SoundType.Execute -> executeButtonSoundID
                    SoundType.Clear -> clearAllButtonSoundID
                    SoundType.Backspace -> backSpaceButtonSoundID
                    SoundType.Error -> errorSoundID
                }
                playSound(soundId)
            }
            else -> {}
        }
    }

    private fun displayRateUs() {
        val packageName = packageName
        val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$packageName"),
        )

        try {
            startActivity(marketIntent)
        } catch (_: ActivityNotFoundException) {
            startActivity(webIntent)
        }
    }

    private fun displayContactUs() {
        sendEmail(this, getString(R.string.farsi_about_42_calc), "", null)
    }

    companion object {
        const val TAG: String = "mainactivity"

        private const val DEFAULT_LANGUAGE: Byte = 0
        const val LANGUAGE_PERSIAN: Byte = 0

        fun sendEmail(pContext: Context, pSubject: String, pBody: String, pAttachments: ArrayList<String>?) {
            // ... (keep the same as before)
        }

        protected fun sendEmailUsingSelectedEmailApp(
            pContext: Context,
            pSubject: String?,
            pBody: String?,
            pAttachments: ArrayList<String>?,
            pSelectedEmailApp: android.content.pm.ResolveInfo?,
        ) {
            // ... (keep the same as before)
        }
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

    val isClassicTheme: Boolean
        get() {
            val appPreferences = applicationContext.getSharedPreferences("THEME", Context.MODE_PRIVATE)
            return appPreferences.getBoolean("CLASSIC_THEME", false)
        }

    fun saveAccentColorCode(colorCode: Int) {
        val appPreferences = applicationContext.getSharedPreferences("THEME", Context.MODE_PRIVATE)
        val editor = appPreferences.edit()
        editor.putInt("ACCENT_COLOR_CODE", colorCode)
        editor.apply()
    }

    fun saveKeypadBackgroundColorCode(colorCode: Int) {
        val appPreferences = applicationContext.getSharedPreferences("THEME", Context.MODE_PRIVATE)
        val editor = appPreferences.edit()
        editor.putInt("KEYPAD_BACKGROUND_COLOR_CODE", colorCode)
        editor.apply()
    }

    fun saveClassicTheme(useClassicTheme: Boolean) {
        val appPreferences = applicationContext.getSharedPreferences("THEME", Context.MODE_PRIVATE)
        val editor = appPreferences.edit()
        editor.putBoolean("CLASSIC_THEME", useClassicTheme)
        editor.apply()
    }

    val volumeFromPreference: Boolean
        get() {
            val appPreferences = applicationContext.getSharedPreferences("volumeState", Context.MODE_PRIVATE)
            return appPreferences.getBoolean("hasVolume", true)
        }

    private fun reverseVolume(): Boolean {
        return if (volumeFromPreference) {
            setVolumeInPreference(false)
            false
        } else {
            setVolumeInPreference(true)
            true
        }
    }

    private fun setVolumeInPreference(hasVolume: Boolean) {
        val appPreferences = applicationContext.getSharedPreferences("volumeState", MODE_PRIVATE)
        val editor = appPreferences.edit()
        editor.putBoolean("hasVolume", hasVolume)
        editor.apply()
    }

    private fun showSplashAndTour() {
        val appPreferences = applicationContext.getSharedPreferences("APP", MODE_PRIVATE)
        val splashAndTourViewed = appPreferences.getBoolean("hasViewedTour", false)
        if (!splashAndTourViewed) {
            val editor = appPreferences.edit()
            editor.putBoolean("hasViewedTour", true)
            editor.apply()
            val intent = Intent(this, ParallaxPagerActivity::class.java)
            startActivity(intent)
        }
    }

    private fun populateConstantDatabaseFirstRun() {
        val appPreferences = applicationContext.getSharedPreferences("APP", MODE_PRIVATE)
        val populateConstantDatabase = appPreferences.getBoolean("hasPopulatedConstantDatabase", false)
        if (!populateConstantDatabase) {
            val names = resources.getStringArray(R.array.constant_default_names)
            val numbers = resources.getStringArray(R.array.constant_default_numbers)
            val selections = resources.getStringArray(R.array.constant_default_selections)

            for (index in names.indices) {
                constantViewModel.insertConstant(
                    ConstantEntity(
                        name = names[index],
                        number = numbers[index].toDouble(),
                        selected = selections[index].toInt()
                    )
                )
            }

            val editor = appPreferences.edit()
            editor.putBoolean("hasPopulatedConstantDatabase", true)
            editor.apply()
        }
    }

    fun playSound(id: Int) {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        val volume = if (volumeFromPreference) maxVolume else 0f

        if (mSoundPoolLoaded) {
            mSoundPool.play(id, volume, volume, 1, 0, 0.99f)
        }
    }

    private fun prepareSoundStuff() {
        mSoundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build()
        } else {
            SoundPool(10, AudioManager.STREAM_MUSIC, 0)
        }
        mSoundPool.setOnLoadCompleteListener { _, _, _ -> mSoundPoolLoaded = true }
        numericButtonSoundID = mSoundPool.load(applicationContext, R.raw.keypress, 1)
        executeButtonSoundID = mSoundPool.load(applicationContext, R.raw.equal, 1)
        clearAllButtonSoundID = mSoundPool.load(applicationContext, R.raw.clear, 1)
        operatorsButtonSoundID = mSoundPool.load(applicationContext, R.raw.keypress, 1)
        errorSoundID = mSoundPool.load(applicationContext, R.raw.error, 1)
        backSpaceButtonSoundID = mSoundPool.load(applicationContext, R.raw.backspace, 1)
    }
}
