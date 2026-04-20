package com.sepidsa.fortytwocalculator

import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.database.Cursor
import android.graphics.Color
import android.graphics.Typeface
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import android.text.InputType
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Switch
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import com.sepidsa.fortytwocalculator.ui.calculator.CalculatorFragment
import com.sepidsa.fortytwocalculator.ui.history.HistoryFragment
import com.sepidsa.fortytwocalculator.ui.currency.CurrencyFragment
import com.sepidsa.fortytwocalculator.ui.scientific.ScientificFragment
import com.sepidsa.fortytwocalculator.ui.favorites.FavoritesFragment
import com.sepidsa.fortytwocalculator.ui.constants.ConstantSelectFragment
import java.io.File
import java.io.Serializable
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.ArrayList
import java.util.Arrays
import java.util.Collection
import java.util.Locale
import java.util.Stack

class MainActivity : FragmentActivity(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    var mCallback: OnHeadlineSelectedListener? = null

    private var mLatestInsertedId: Long = 0
    private lateinit var mDrawerLayout: DrawerLayout
    var mListView: Serializable? = null

    // the reason it's an editText and not a TextView is solely for supporting the scrolling function
    lateinit var mTranslationBox: AutoResizeTextView

    private var mButton: Button? = null

    lateinit var mViewPager: ViewPager
    private var mDefaultPage: Byte = 0

    var mSelectedColorCal0: Int = 0

    private lateinit var mRobotoLight: Typeface
    private lateinit var mRobotoRegular: Typeface

    var mLayoutState: Byte = 0

    // This will be synced with the viewpager . and is provided kindly by www.viewpagerindicator.com
    private var mViewPagerIndicator: CirclePageIndicator? = null

    private lateinit var resultTextView: AutoResizeTextView
    private var mResultToDisplay: String = "0"

    var mJustPressedExecuteButton: Boolean = true

    // This is the string work with during all the calculations. We don't necessarily display it though
    private var mExpressionBuffer: StringBuilder = StringBuilder()

    // Mainly used for backspace operations
    private var mButtonsStack: Stack<String> = Stack()

    private var mTranslationBoxNumericFont: Typeface? = null
    private var mTranslationBoxLetterFont: Typeface? = null

    private var resultTextViewHolder: View? = null
    private var mRawResult: BigDecimal = BigDecimal(0)
    private var mMemoryVariable: BigDecimal = BigDecimal(0)
    private lateinit var mMemoryVariableTextView: TextView
    private lateinit var mErrorBlink: Animation
    lateinit var mBlink: Animation
    private lateinit var mSoundPool: SoundPool
    private var mSoundPoolLoaded: Boolean = false
    private var numericButtonSoundID: Int = 0
    private var executeButtonSoundID: Int = 0
    private var clearAllButtonSoundID: Int = 0
    private var operatorsButtonSoundID: Int = 0
    private var errorSoundID: Int = 0
    private var mAddStarSoundID: Int = 0
    private var mHasVolumeSoundID: Int = 0
    private var backSpaceButtonSoundID: Int = 0

    private lateinit var outAnimExecute: Animation
    private lateinit var inAnim: Animation

    private lateinit var mFlatIcon: Typeface
    var mDecimal_fraction: String = ""
    private lateinit var mScientificModeTextView: TextView
    private lateinit var mMajalla: Typeface
    private lateinit var mMitra: Typeface
    private lateinit var mFavoritesList: Button

    private var doubleBackToExitPressedOnce: Boolean = false
    private var mHandler: Handler? = null

    private val mRunnable = Runnable { doubleBackToExitPressedOnce = false }

    private lateinit var mAddStars: Button
    private lateinit var mAddLabel: Button
    private lateinit var mPhalls: Typeface
    private lateinit var mDigital7: Typeface
    private lateinit var mRobotoThin: Typeface

    private lateinit var outAnimClear: Animation
    private lateinit var mLogFragment: HistoryFragment
    private lateinit var mDialpadFragment: CalculatorFragment

    private lateinit var mTextSwitcher: TextSwitcher

    // Container Activity must implement this interface
    interface OnHeadlineSelectedListener {
        fun onArticleSelected(position: Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG_recreate, "Activity oncreate")
        super.onCreate(savedInstanceState)

        showSplashAndTour()
        setTypeFaces()
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            mJustPressedExecuteButton = true
        }
        val fragmentManager = supportFragmentManager

        setMViewPager(fragmentManager)
        setMViewPagerIndicator()

        mMemoryVariableTextView = findViewById(R.id.m_vaiable_textview)
        mTranslationBox = findViewById(R.id.translationEditText)
        resultTextView = findViewById(R.id.result)
        mScientificModeTextView = findViewById(R.id.scientific_mode_textview)
        resultTextViewHolder = findViewById(R.id.MotherTop)
        mFavoritesList = findViewById(R.id.favorites_list)
        mAddStars = findViewById(R.id.btn_add_star)
        mAddLabel = findViewById(R.id.add_label)
        mFavoritesList.setOnClickListener(this)
        mAddStars.setOnClickListener(this)
        mAddLabel.setOnClickListener(this)

        mTextSwitcher = findViewById(R.id.text_switcher)

        prepareBottomIcons()
        refreshFonts()
        setIconButtons()
        buildNavigationDrawer()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if ((mLayoutState == LANDSCAPE_TABLET.toByte()) ||
                    (mViewPager.currentItem == DIALPAD_FRAGMENT.toInt())
                ) {
                    if (doubleBackToExitPressedOnce) {
                        finish()
                        return
                    }
                    doubleBackToExitPressedOnce = true
                    Toast.makeText(this@MainActivity, "دوباره لطفا", Toast.LENGTH_SHORT).show()
                    mHandler = Handler()
                    mHandler?.postDelayed(mRunnable, 2000)
                } else {
                    mViewPager.currentItem = DIALPAD_FRAGMENT.toInt()
                }
            }
        })
    }

    private fun buildNavigationDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener { item ->
            mDrawerLayout.closeDrawer(GravityCompat.END)
            val id = item.itemId
            when (id) {
                R.id.drawer_help -> displayHelp()
                R.id.drawer_rate -> displayRateUs()
                R.id.drawer_about -> displayAbout()
                R.id.drawer_contact -> displayContactUs()
            }
            true
        }
    }

    private fun displayRateUs() {
        val giveStarsIntent = Intent(
            Intent.ACTION_EDIT,
            Uri.parse("http://cafebazaar.ir/app/com.sepidsa.fortytwocalculator/?l=fa"),
        )
        startActivity(giveStarsIntent)
    }

    private fun displayContactUs() {
        sendEmail(this, getString(R.string.farsi_about_42_calc), "", null)
    }

    companion object {
        private const val FRAGMENT_TAG_LOG_ = "log fragment"
        const val LANGUAGE_ARABIC: Byte = 3
        const val TAG: String = "mainactivity"
        private const val LOG_DATA_KEY = "log data"

        const val FONT_DIGITAL_7: Int = 3
        const val FONT_YEKAN: Int = 4
        const val FONT_MAJALLA: Int = 7

        private const val TOTAL_PAGE_COUNT: Byte = 3

        private const val LOG_FRAGMENT: Byte = 0
        private const val DIALPAD_FRAGMENT: Byte = 1

        private const val SCIENTIFIC_FRAGMENT_LANDSCAPE_TABLET: Byte = 0
        private const val COLOR_PICKER_FRAGMENT_LANDSCAPE_TABLET: Byte = 1
        private const val COLOR_PICKER_FRAGMENT_PORTRAIT: Byte = 3
        private const val COLOR_PICKER_FRAGMENT_LANDSCAPE_PHONE: Byte = 2

        const val FONT_ROBOTO_THIN: Int = 0
        const val FONT_ROBOTO_LIGHT: Int = 1
        const val FONT_ROBOTO_REGULAR: Int = 2
        const val FONT_MITRA: Int = 5

        private const val PORTRAIT_PHONE: Byte = 0
        private const val LANDSCAPE_PHONE: Byte = 1
        private const val LANDSCAPE_TABLET: Byte = 2

        const val TAG_recreate: String = "recreate"

        private const val DEFAULT_LANGUAGE: Byte = LANGUAGE_PERSIAN
        const val LANGUAGE_PERSIAN: Byte = 0
        const val LANGUAGE_ENGLISH: Byte = 1
        const val LANGUAGE_FRENCH: Byte = 2

        fun sendEmail(pContext: Context, pSubject: String, pBody: String, pAttachments: ArrayList<String>?) {
            try {
                val pm = pContext.packageManager
                var selectedEmailActivity: ResolveInfo? = null

                val emailDummyIntent = Intent(Intent.ACTION_SENDTO)
                emailDummyIntent.data = Uri.parse("mailto:feedback@sepidsa.com")

                var emailActivities = pm.queryIntentActivities(emailDummyIntent, 0)

                if (emailActivities == null || emailActivities.size == 0) {
                    val emailDummyIntentRFC822 = Intent(Intent.ACTION_SEND_MULTIPLE)
                    emailDummyIntentRFC822.type = "message/rfc822"

                    emailActivities = pm.queryIntentActivities(emailDummyIntentRFC822, 0)
                }

                if (emailActivities != null) {
                    selectedEmailActivity = if (emailActivities.size == 1) {
                        emailActivities[0]
                    } else {
                        emailActivities.firstOrNull { it.isDefault }
                    }

                    if (selectedEmailActivity != null) {
                        sendEmailUsingSelectedEmailApp(pContext, pSubject, pBody, pAttachments, selectedEmailActivity)
                    } else {
                        val emailActivitiesForDialog = emailActivities
                        val availableEmailAppsName = Array(emailActivitiesForDialog.size) { i ->
                            emailActivitiesForDialog[i].activityInfo.applicationInfo.loadLabel(pm).toString()
                        }

                        val builder = AlertDialog.Builder(pContext)
                        builder.setTitle(pContext.getString(R.string.farsi_choose_email_app))
                        builder.setItems(availableEmailAppsName) { _, which ->
                            sendEmailUsingSelectedEmailApp(
                                pContext,
                                pSubject,
                                pBody,
                                pAttachments,
                                emailActivitiesForDialog[which],
                            )
                        }
                        builder.create().show()
                    }
                } else {
                    sendEmailUsingSelectedEmailApp(pContext, pSubject, pBody, pAttachments, null)
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Can't send email", ex)
            }
        }

        protected fun sendEmailUsingSelectedEmailApp(
            pContext: Context,
            pSubject: String?,
            pBody: String?,
            pAttachments: ArrayList<String>?,
            pSelectedEmailApp: ResolveInfo?,
        ) {
            try {
                val emailIntent = Intent(Intent.ACTION_SEND_MULTIPLE)

                val aEmailList = arrayOf("feedback@sepidsa.com")

                emailIntent.putExtra(Intent.EXTRA_EMAIL, aEmailList)
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, pSubject ?: "")
                emailIntent.putExtra(Intent.EXTRA_TEXT, pBody ?: "")

                if (pAttachments != null && pAttachments.size > 0) {
                    val attachmentsUris = ArrayList<Uri>()
                    for (currAttachemntPath in pAttachments) {
                        val fileIn = File(currAttachemntPath)
                        val currAttachemntUri = Uri.fromFile(fileIn)
                        attachmentsUris.add(currAttachemntUri)
                    }
                    emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachmentsUris)
                }

                if (pSelectedEmailApp != null) {
                    Log.d(TAG, "Sending email using $pSelectedEmailApp")
                    emailIntent.component = ComponentName(
                        pSelectedEmailApp.activityInfo.packageName,
                        pSelectedEmailApp.activityInfo.name,
                    )
                    pContext.startActivity(emailIntent)
                } else {
                    val emailAppChooser = Intent.createChooser(emailIntent, "Select Email app")
                    pContext.startActivity(emailAppChooser)
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Error sending email", ex)
            }
        }

        fun setClipView(view: View?, clip: Boolean) {
            if (view != null) {
                val parent: ViewParent? = view.parent
                if (parent is ViewGroup) {
                    val viewGroup = view.parent as ViewGroup
                    viewGroup.clipChildren = clip
                    viewGroup.clipToPadding = clip
                    setClipView(viewGroup, clip)
                }
            }
        }
    }

    private fun displayAbout() {
        val myIntent = Intent(this@MainActivity, AboutActivity::class.java)
        startActivity(myIntent)
    }

    private fun displayHelp() {
        val myIntent = Intent(this@MainActivity, HelpActivity::class.java)
        startActivity(myIntent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("buttonStack", mButtonsStack)
        outState.putString("mResultText", resultTextView.text.toString())
        outState.putString("mScientificModeTextView", mScientificModeTextView.text.toString())
        outState.putCharSequence("mTranslationText", mTranslationBox.text.toString())
        outState.putSerializable("mButtonStack", mButtonsStack)
        outState.putString("mExpressionBuffer", mExpressionBuffer.toString())
        outState.putBoolean("mJustPressedExecuteButton", mJustPressedExecuteButton)
        outState.putSerializable("mMemoryVariable", mMemoryVariable)
        outState.putString("mResultToDisplay", mResultToDisplay)
        outState.putString("mDecimal_fraction", mDecimal_fraction)
        Log.d(TAG_recreate, "Activity onSaveInstanceState ")
    }

    @Throws(PackageManager.NameNotFoundException::class)
    fun getVersion(): String {
        return applicationContext.packageManager.getPackageInfo(packageName, 0).versionName
    }

    // Java-compat wrappers (minimize behavior surface-area changes)
    fun getAccentColorCode(): Int = accentColorCode

    fun getAngleMode(): Boolean = angleMode

    fun getKeypadBackgroundColorCode(): Int = keypadBackgroundColorCode

    fun getDialpadFontColor(): Int = dialpadFontColor

    fun getTranslationLanguage(): Int = translationLanguage

    fun getClearSoundID(): Int = clearSoundID

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.d(TAG_recreate, "Activity onRestoreInstanceState and is $savedInstanceState")

        if (savedInstanceState != null) {
            mTranslationBox = mTranslationEditText
            mTranslationBox.text = savedInstanceState.getString("mTranslationText")
            mJustPressedExecuteButton = savedInstanceState.getBoolean("mJustPressedExecuteButton")
            mButtonsStack = Stack()
            @Suppress("UNCHECKED_CAST")
            mButtonsStack.addAll(savedInstanceState.getSerializable("mButtonStack") as Collection<String>)

            mExpressionBuffer = StringBuilder()
            val exTemp = savedInstanceState.getString("mExpressionBuffer")

            mMemoryVariable = savedInstanceState.getSerializable("mMemoryVariable") as BigDecimal
            mMemoryVariableTextView.text = " M = ${mMemoryVariable}"
            mResultToDisplay = savedInstanceState.getString("mResultToDisplay") ?: mResultToDisplay
            mDecimal_fraction = savedInstanceState.getString("mDecimal_fraction") ?: ""

            mExpressionBuffer = if (exTemp != null) StringBuilder(exTemp) else StringBuilder()

            resultTextView.text = savedInstanceState.getString("mResultText")
        }
    }

    override fun onStart() {
        super.onStart()
        setResultTextBox()
        mTranslationBox = findViewById(R.id.translationEditText)

        when (translationLanguage) {
            LANGUAGE_PERSIAN.toInt() -> mTranslationEditText.typeface = mTranslationBoxLetterFont
            else -> mTranslationEditText.typeface = getFontForComponent("RESULT_FONT")
        }

        resultTextView.setBackgroundColor(accentColorCode)
        if (angleMode) {
            mScientificModeTextView.text = "DEG"
        } else {
            mScientificModeTextView.text = "RAD"
        }

        val runnable = Runnable {
            prepareAnimationStuff()
            prepareSoundStuff()
            populateConstantDatabaseFirstRun()
        }
        Thread(runnable).start()
    }

    override fun onResume() {
        if (!mJustPressedExecuteButton) {
            mTranslationBox.typeface = mTranslationBoxNumericFont
        }
        redrawAccent()
        redrawKeypadBackground()
        super.onResume()
    }

    private fun prepareBottomIcons() {
        (findViewById<Button>(R.id.buttonSettings)).typeface = mFlatIcon
        (findViewById<Button>(R.id.buttonSettings)).setTextColor(accentColorCode)
        (findViewById<Button>(R.id.buttonSettings)).setTextColor(Color.parseColor("#BDBDBD"))
        (findViewById<View>(R.id.buttonSettings)).setOnClickListener(this)

        (findViewById<Button>(R.id.buttonMute)).typeface = mFlatIcon
        (findViewById<Button>(R.id.buttonMute)).setTextColor(Color.parseColor("#BDBDBD"))
        findViewById<View>(R.id.buttonMute).setOnClickListener(this)
        if (volumeFromPreference) {
            (findViewById<Button>(R.id.buttonMute)).text = resources.getText(R.string.volume_high)
        } else {
            (findViewById<Button>(R.id.buttonMute)).text = resources.getText(R.string.volume_off)
        }

        (findViewById<Button>(R.id.buttonColors)).typeface = mFlatIcon
        (findViewById<Button>(R.id.buttonColors)).setTextColor(Color.parseColor("#BDBDBD"))
        findViewById<View>(R.id.buttonColors).setOnClickListener(this)

        (findViewById<Button>(R.id.buttonHamburgerMenu)).typeface = mFlatIcon
        (findViewById<Button>(R.id.buttonHamburgerMenu)).setTextColor(Color.parseColor("#BDBDBD"))
        findViewById<View>(R.id.buttonHamburgerMenu).setOnClickListener(this)
    }

    private val volumeFromPreference: Boolean
        get() {
            val appPreferences = applicationContext.getSharedPreferences("volumeState", Context.MODE_PRIVATE)
            return appPreferences.getBoolean("hasVolume", true)
        }

    override fun onDestroy() {
        Log.d(TAG_recreate, "Activity ondestroy")
        if (!isFinishing) {
            if (mHandler != null) {
                mHandler?.removeCallbacks(mRunnable)
            }
        }
        super.onDestroy()
    }

    // Mind casting of viewpagerindicator. there's 6 types of this object in the library available
    private fun setMViewPagerIndicator() {
        mViewPagerIndicator = findViewById(R.id.view_pager_indicator)
        if (mViewPagerIndicator != null) {
            mViewPagerIndicator?.setViewPager(mViewPager)
            mViewPagerIndicator?.setCurrentItem(mDefaultPage.toInt())
            mViewPagerIndicator?.setFillColor(accentColorCode)
        }
    }

    private fun setMViewPager(fragmentManager: FragmentManager) {
        mViewPager = findViewById(R.id.viewpager)
        if (this::mViewPager.isInitialized && mViewPager.visibility == View.VISIBLE) {
            val fList: MutableList<Fragment> = ArrayList()
            mViewPager.offscreenPageLimit = 0
            mLogFragment = HistoryFragment()
            fList.add(mLogFragment)
            fList.add(CalculatorFragment())
            if (mViewPager.tag == "portrait_phone") {
                fList.add(ScientificFragment())
                mLayoutState = PORTRAIT_PHONE
            } else {
                mLayoutState = LANDSCAPE_PHONE
            }

            mViewPager.adapter = ViewPagerAdapter(fragmentManager, fList)
            mViewPager.currentItem = DIALPAD_FRAGMENT.toInt()
            setmDefaultPage(DIALPAD_FRAGMENT)
        } else {
            mLayoutState = LANDSCAPE_TABLET
            mLogFragment = supportFragmentManager.findFragmentByTag("fragment_log_tablet_land") as HistoryFragment
        }
    }

    private fun setTypeFaces() {
        mRobotoLight = Typeface.createFromAsset(applicationContext.assets, "roboto_light.ttf")
        mRobotoRegular = Typeface.createFromAsset(applicationContext.assets, "roboto_regular.ttf")
        mRobotoThin = Typeface.createFromAsset(applicationContext.assets, "roboto_thin.ttf")

        mMajalla = Typeface.createFromAsset(applicationContext.assets, "yekan.ttf")
        mFlatIcon = Typeface.createFromAsset(applicationContext.assets, "flaticon.ttf")
        mMitra = Typeface.createFromAsset(applicationContext.assets, "mitra.ttf")
        mPhalls = Typeface.createFromAsset(applicationContext.assets, "yekan.ttf")
        mDigital7 = Typeface.createFromAsset(applicationContext.assets, "digital_7.ttf")
    }

    private fun setIconButtons() {
        mFavoritesList.typeface = mFlatIcon
        mFavoritesList.text = resources.getString(R.string.list)
        mFavoritesList.textSize = 30f

        mAddLabel.typeface = mFlatIcon
        mAddStars.typeface = mFlatIcon
    }

    private fun setResultTextBox() {
        resultTextView.setBackgroundColor(accentColorCode)
        resultTextViewHolder?.setBackgroundColor(accentColorCode)
        resultTextView.setTextColor(Color.WHITE)
        resultTextView.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
    }

    val accentColorCode: Int
        get() {
            val appPreferences = applicationContext.getSharedPreferences("THEME", MODE_PRIVATE)
            return appPreferences.getInt("ACCENT_COLOR_CODE", Color.parseColor("#009688"))
        }

    val angleMode: Boolean
        get() {
            val appPreferences = applicationContext.getSharedPreferences("angleMode", MODE_PRIVATE)
            return appPreferences.getBoolean("isDeg", true)
        }

    fun setAngleMode(isDeg: Boolean) {
        val appPreferences = applicationContext.getSharedPreferences("angleMode", MODE_PRIVATE)
        val editor = appPreferences.edit()
        if (isDeg) {
            editor.putBoolean("isDeg", true)
            mScientificModeTextView.text = "DEG"
        } else {
            editor.putBoolean("isDeg", false)
            mScientificModeTextView.text = "RAD"
        }
        editor.apply()
    }

    val keypadBackgroundColorCode: Int
        get() {
            val appPreferences = applicationContext.getSharedPreferences("THEME", Context.MODE_PRIVATE)
            return appPreferences.getInt("KEYPAD_BACKGROUND_COLOR_CODE", Color.WHITE)
        }

    private fun redrawAccent() {
        mFavoritesList.setTextColor(accentColorCode)
        if (this::mViewPager.isInitialized) {
            mViewPagerIndicator?.setFillColor(accentColorCode)
        }
    }

    private fun indexOf(parent: IntArray, child: Int): Int {
        for (index in parent.indices) {
            if (parent[index] == child) {
                return index
            }
        }
        return 0
    }

    val dialpadFontColor: Int
        get() {
            val colorArray = resources.getStringArray(R.array.dialpad_font_color_choice_values)
            val indexOfCurrentBackgroundColor =
                indexOf(Utils.ColorUtils.colorChoiceForKeypad(applicationContext), keypadBackgroundColorCode)
            return Color.parseColor(colorArray[indexOfCurrentBackgroundColor])
        }

    private fun redrawKeypadBackground() {
        val activityView = findViewById<View>(R.id.activity_body)
        mTranslationBox.setBackgroundColor(keypadBackgroundColorCode)
        mTranslationBox.setTextColor(dialpadFontColor)
        activityView.setBackgroundColor(keypadBackgroundColorCode)
    }

    val translationLanguage: Int
        get() {
            val appPreferences = applicationContext.getSharedPreferences("LanguagePreference", MODE_PRIVATE)
            return appPreferences.getInt("LANGUAGE", DEFAULT_LANGUAGE.toInt())
        }

    val mTranslationEditText: AutoResizeTextView
        get() = findViewById(R.id.translationEditText)

    fun setTranslationText(text: String) {
        val translationBox: AutoResizeTextView = findViewById(R.id.translationEditText)
        translationBox.text = text
        translationBox.movementMethod = ScrollingMovementMethod.getInstance()
    }

    // ==================== ViewController implementation
    fun aButtonIsPressed(currentButtonValue: String) {
        switchToMainFragment()

        if (currentButtonValue == resources.getString(R.string.clear)) {
            performClearResult()
            return
        }
        if (currentButtonValue == resources.getString(R.string.mc)) {
            performMC()
            return
        }
        if (currentButtonValue == resources.getString(R.string.mr)) {
            performMR()
            return
        }
        if (currentButtonValue == resources.getString(R.string.mplus)) {
            performMPlus()
            return
        }
        if (currentButtonValue == resources.getString(R.string.mminus)) {
            performMMinus()
            return
        }
        if (currentButtonValue == resources.getString(R.string.backSpace)) {
            performBackspace()
            return
        }

        if (mJustPressedExecuteButton) {
            if (Character.isDigit(currentButtonValue[0]) ||
                currentButtonValue == resources.getString(R.string.Pi) ||
                currentButtonValue == resources.getString(R.string.EXP)
            ) {
                setMExpressionString("")
                mButtonsStack.clear()
            }
        }
        mJustPressedExecuteButton = false
        val shouldPrevent = preventCommonErrors(currentButtonValue[0])
        if (shouldPrevent) {
            playSound(errorSoundID)
            mTranslationBox.startAnimation(mErrorBlink)
            return
        }

        if (currentButtonValue == "=") {
            if (mExpressionString.length > 0) {
                if (calculateResult(null) == 0.toByte()) {
                    mJustPressedExecuteButton = true
                    updateUIExecute(true)
                }
            }
        } else if (isNonDigit(currentButtonValue)) {
            appendMExpressionString(currentButtonValue)
            pushLastButton(currentButtonValue)
            playSound(operatorsButtonSoundID)
            updateUIOperator()
        } else {
            playSound(numericButtonSoundID)
            if (calculateResult(currentButtonValue) != 2.toByte()) {
                updateUIDigit()
            }
        }
        checkCLRButtonSendIntent()
    }

    fun switchToMainFragment() {
        if (this::mViewPager.isInitialized && mViewPager.currentItem != mDefaultPage.toInt()) {
            mViewPager.currentItem = mDefaultPage.toInt()
        }
    }

    private fun performMMinus() {
        mMemoryVariable =
            mMemoryVariable.subtract(BigDecimal(resultTextView.text.toString().replace(",", "")))

        val df = DecimalFormat()
        df.isGroupingUsed = true
        df.groupingSize = 3
        df.maximumFractionDigits = 6
        df.decimalFormatSymbols = DecimalFormatSymbols(Locale.US)
        var result = df.format(mMemoryVariable)
        result = result.replace("^-(?=0(.0*)?$)".toRegex(), "")
        mButtonsStack.clear()
        mButtonsStack.push(mResultToDisplay)
        mJustPressedExecuteButton = true
        playSound(clearAllButtonSoundID)
        checkCLRButtonSendIntent()
        mMemoryVariableTextView.text = " M = $result"

        Toast.makeText(applicationContext, "M-", Toast.LENGTH_SHORT).show()
    }

    private fun performMPlus() {
        mMemoryVariable =
            mMemoryVariable.add(BigDecimal(resultTextView.text.toString().replace(",", "")))
        val df = DecimalFormat()
        df.isGroupingUsed = true
        df.groupingSize = 3
        df.maximumFractionDigits = 6
        df.decimalFormatSymbols = DecimalFormatSymbols(Locale.US)
        var result = df.format(mMemoryVariable)
        result = result.replace("^-(?=0(.0*)?$)".toRegex(), "")
        mButtonsStack.clear()
        mButtonsStack.push(mResultToDisplay)
        mJustPressedExecuteButton = true
        playSound(clearAllButtonSoundID)
        checkCLRButtonSendIntent()
        mMemoryVariableTextView.text = " M = $result"
        Toast.makeText(applicationContext, "M+", Toast.LENGTH_SHORT).show()
    }

    private fun performMR() {
        addNumberToCalculation(mMemoryVariable.toPlainString())
        Toast.makeText(applicationContext, "MR", Toast.LENGTH_SHORT).show()
    }

    fun addNumberToCalculation(inputString: String) {
        val rawExpressionString = mExpressionString.toString()
        if (rawExpressionString.isNotEmpty() && (rawExpressionString[rawExpressionString.length - 1] == '+' ||
                rawExpressionString[rawExpressionString.length - 1] == '−' ||
                rawExpressionString[rawExpressionString.length - 1] == '\u00d7' ||
                rawExpressionString[rawExpressionString.length - 1] == '÷')
        ) {
            setMExpressionString(mExpressionString.append(inputString).toString())
            val expression = Expression(mExpressionString.toString(), angleMode, applicationContext)
            mResultToDisplay = evaluateResult(expression)
            mRawResult = BigDecimal(mResultToDisplay.replace(",", ""))
            resultTextView.text = mResultToDisplay
            val mr: CharSequence = inputString
            for (index in mr.indices) {
                mButtonsStack.push(mr[index].toString())
            }
            updateUIDigit()
        } else {
            val df = DecimalFormat()
            df.isGroupingUsed = true
            df.groupingSize = 3
            df.maximumFractionDigits = 6
            df.decimalFormatSymbols = DecimalFormatSymbols(Locale.US)
            val inputDecimal = BigDecimal(inputString.replace(",", ""))
            mResultToDisplay = df.format(inputDecimal)
            mResultToDisplay = mResultToDisplay.replace("^-(?=0(.0*)?$)".toRegex(), "")

            mRawResult = BigDecimal(mResultToDisplay.replace(",", ""))

            mButtonsStack.clear()
            val mr: CharSequence = mResultToDisplay.replace(",", "")
            for (index in mr.indices) {
                mButtonsStack.push(mr[index].toString())
            }
            mJustPressedExecuteButton = true
            setMExpressionString(mRawResult.toString())
            mDecimal_fraction = ""
            if (mResultToDisplay.indexOf(".") != -1) {
                mDecimal_fraction = mResultToDisplay.substring(mResultToDisplay.indexOf(".") + 1)
            }
            mTextSwitcher.startAnimation(outAnimExecute)
            displayTranslation(true)
            checkCLRButtonSendIntent()
        }
        playSound(clearAllButtonSoundID)
    }

    private fun performMC() {
        mMemoryVariable = BigDecimal(0)
        mMemoryVariableTextView.text = " M = 0"
        mDecimal_fraction = ""
        Toast.makeText(applicationContext, "MC", Toast.LENGTH_SHORT).show()
        playSound(clearAllButtonSoundID)
    }

    private fun reverseVolume(): Boolean {
        return if (volumeFromPreference) {
            setVolumeInPreference(false)
            false
        } else {
            setVolumeInPreference(true)
            playSound(mHasVolumeSoundID)
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
        if (!splashAndTourViewed) {
            setSplashAndTourViewed(true)
            val intent = Intent(this, ParallaxPagerActivity::class.java)
            overridePendingTransition(R.anim.appear, R.anim.disappear)
            startActivity(intent)
        }
    }

    private fun populateConstantDatabaseFirstRun() {
        if (!populateConstantDatabase) {
            val names = resources.getStringArray(R.array.constant_default_names)
            val numbers = resources.getStringArray(R.array.constant_default_numbers)
            val selections = resources.getStringArray(R.array.constant_default_selections)

            for (index in names.indices) {
                val newName = names[index]
                val newNumber = numbers[index].toDouble()
                val selection = selections[index].toInt()
                val values = ContentValues()

                values.put(ConstantContract.ConstantEntry.COLUMN_NAME, newName)
                values.put(ConstantContract.ConstantEntry.COLUMN_NUMBER, newNumber)
                values.put(ConstantContract.ConstantEntry.COLUMN_SELECTED, selection)

                contentResolver.insert(ConstantContract.ConstantEntry.CONTENT_URI, values)
            }

            setPopulateConstantDatabase(true)
        }
    }

    private fun setPopulateConstantDatabase(hasPopulated: Boolean) {
        val appPreferences = applicationContext.getSharedPreferences("APP", MODE_PRIVATE)
        val editor = appPreferences.edit()
        editor.putBoolean("hasPopulatedConstantDatabase", hasPopulated)
        editor.apply()
    }

    private fun setSplashAndTourViewed(hasViewed: Boolean) {
        val appPreferences = applicationContext.getSharedPreferences("APP", MODE_PRIVATE)
        val editor = appPreferences.edit()
        editor.putBoolean("hasViewedTour", hasViewed)
        editor.apply()
    }

    private val populateConstantDatabase: Boolean
        get() {
            val appPreferences = applicationContext.getSharedPreferences("APP", MODE_PRIVATE)
            return appPreferences.getBoolean("hasPopulatedConstantDatabase", false)
        }

    private val splashAndTourViewed: Boolean
        get() {
            val appPreferences = applicationContext.getSharedPreferences("APP", MODE_PRIVATE)
            return appPreferences.getBoolean("hasViewedTour", false)
        }

    private fun performBackspace() {
        if (mButtonsStack.size > 0) {
            val tempexpString = mExpressionString.substring(0, mExpressionString.length - lastButtonLength())
            setMExpressionString(tempexpString)
            popPressedButton()

            if (isNonDigit(peekLastButton())) {
                updateUIOperator()
            } else {
                calculateResult(null)
                updateUIDigit()
            }
        }
        playSound(backSpaceButtonSoundID)
        checkCLRButtonSendIntent()
    }

    private fun lastButtonLength(): Int {
        return peekLastButton().length
    }

    fun pushLastButton(input: String) {
        mButtonsStack.push(input)
    }

    fun popPressedButton(): String {
        return mButtonsStack.pop()
    }

    fun peekLastButton(): String {
        return mButtonsStack.peek()
    }

    private fun performClearResult() {
        setMExpressionString("")
        setTranslationText("")
        mResultToDisplay = "0"
        resultTextView.startAnimation(outAnimClear)

        mButtonsStack.clear()
        playSound(clearAllButtonSoundID)
        mAddStars.visibility = View.GONE
        mAddLabel.visibility = View.GONE
    }

    private fun stackSize(): Int {
        return mButtonsStack.size
    }

    fun checkCLRButtonSendIntent() {
        if (stackSize() <= 1) {
            sendClearButtonMessage(resources.getString(R.string.clear))
        } else {
            Log.d(TAG_recreate, "Activity Right before sendng message")
            sendClearButtonMessage(resources.getString(R.string.backSpace))
        }
    }

    private fun updateUIExecute(sendLogMessage: Boolean): Boolean {
        mDecimal_fraction = ""
        val decimalIndex = mResultToDisplay.indexOf(".").toByte()
        if (decimalIndex.toInt() != -1) {
            mDecimal_fraction = mResultToDisplay.substring(mResultToDisplay.indexOf(".") + 1)
        }

        try {
            mAddStars.alpha = 1f
            mAddStars.rotation = 0f
            mAddStars.text = resources.getString(R.string.star_outline)
            mAddStars.setTextColor(Color.WHITE)
            mAddStars.scaleX = 1f
            mAddStars.scaleY = 1f
            mAddStars.translationY = 0f
            mAddStars.visibility = View.VISIBLE
            mAddLabel.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
            playSound(errorSoundID)
            return true
        }

        mButtonsStack.clear()
        mButtonsStack.push(resultTextView.text.toString().replace(",", ""))

        displayTranslation(sendLogMessage)

        if (sendLogMessage) {
            playSound(executeButtonSoundID)
            mTextSwitcher.startAnimation(outAnimExecute)
        } else {
            resultTextView.text = mResultToDisplay
        }

        return false
    }

    private fun updateUIDigit() {
        if (mExpressionString.isNotEmpty()) {
            resultTextView.text = mResultToDisplay
            val gg = mExpressionString.toString().replace(
                "(?<!\\.\\d{0,6})\\d+?(?=(?:\\d{3})+(?:\\D|$))".toRegex(),
                "$0,",
            )
            mTranslationBox.typeface = mTranslationBoxNumericFont
            mTranslationBox.text = gg

            mAddStars.visibility = View.GONE
            mAddLabel.visibility = View.GONE
        }
    }

    private fun calculateResult(currentButtonValue: String?): Byte {
        mResultToDisplay = "0"
        var testSubject: String = mExpressionBuffer.toString()
        if (currentButtonValue != null) {
            testSubject += currentButtonValue
        }
        try {
            val withoutcomas = testSubject.replace(",", "")
            val expression = Expression(withoutcomas, angleMode, applicationContext)
            mResultToDisplay = evaluateResult(expression)
            mExpressionBuffer = StringBuilder(testSubject)
            if (currentButtonValue != null) {
                pushLastButton(currentButtonValue)
            }
        } catch (e: ArithmeticException) {
            mExpressionBuffer = StringBuilder(testSubject)
            if (currentButtonValue != null) {
                pushLastButton(currentButtonValue)
            }
            mResultToDisplay = "∞"
            return 1
        } catch (e: NumberFormatException) {
            mExpressionBuffer = StringBuilder(testSubject)
            if (currentButtonValue != null) {
                pushLastButton(currentButtonValue)
            }
            mResultToDisplay = "error"
            return 1
        } catch (e: Exception) {
            e.printStackTrace()
            playSound(errorSoundID)
            mJustPressedExecuteButton = false
            return 2
        }
        return 0
    }

    private fun updateUIOperator() {
        val gg = mExpressionString.toString().replace(
            "(?<!\\.\\d{0,6})\\d+?(?=(?:\\d{3})+(?:\\D|$))".toRegex(),
            "$0,",
        )
        mTranslationBox.typeface = mTranslationBoxNumericFont
        mTranslationBox.text = gg
        mAddStars.visibility = View.GONE
        mAddLabel.visibility = View.GONE
    }

    fun displayTranslation(animate: Boolean) {
        mTranslationBox.typeface = mTranslationBoxLetterFont
        printResultWithTranslation(mDecimal_fraction, translationLanguage)
        if (animate) {
            mTranslationBox.startAnimation(mBlink)
        }
    }

    private fun printResultWithTranslation(decimal_fraction: String, Language: Int) {
        val resultWithoutCommas = mResultToDisplay.replace(",", "")
        val pointIndex = resultWithoutCommas.indexOf(".")
        var integerFraction = ""
        val resultIsNegative = mRawResult.compareTo(BigDecimal(-0.0000009)) < 0

        integerFraction = if (pointIndex == -1) {
            resultWithoutCommas
        } else {
            resultWithoutCommas.substring(0, pointIndex)
        }
        if (resultIsNegative && integerFraction.isNotEmpty()) {
            integerFraction = integerFraction.substring(1)
        }

        when (Language) {
            LANGUAGE_ENGLISH.toInt() -> {
                if (resultIsNegative) {
                    mTranslationBox.text = "minus " + NumberConveterAmerican.convert(integerFraction.substring(0))
                } else {
                    mTranslationBox.text = NumberConveterAmerican.convert(integerFraction)
                }
                if (decimal_fraction != "") {
                    val partII = NumberConveterAmericanPartII.convert(decimal_fraction)
                    mTranslationBox.append(partII)
                }
            }

            LANGUAGE_FRENCH.toInt() -> {
                if (resultIsNegative) {
                    mTranslationBox.text = "moins " + NumberConverterFrench.convert(integerFraction)
                } else {
                    mTranslationBox.text = NumberConverterFrench.convert(integerFraction)
                }
                if (decimal_fraction != "") {
                    val partII = NumberConverterFrenchPartII.convert(decimal_fraction)
                    mTranslationBox.append(partII)
                }
            }

            LANGUAGE_ARABIC.toInt() -> {
                if (resultIsNegative) {
                    val arabic = NumberConverterArabic(mRawResult.abs())
                    mTranslationBox.text = "ناقص " + arabic.ConvertToArabic()
                } else {
                    val arabic = NumberConverterArabic(mRawResult)
                    mTranslationBox.text = arabic.ConvertToArabic()
                }
            }

            LANGUAGE_PERSIAN.toInt() -> {
                mTranslationBox.text = NumberConveterPersianPartI().convert(integerFraction)
                if (decimal_fraction != "") {
                    val partII = NumberConverterPersianPartII.convert(decimal_fraction)
                    if (BigDecimal(resultWithoutCommas).compareTo(BigDecimal.ONE) >= 0 ||
                        BigDecimal(resultWithoutCommas).compareTo(BigDecimal(-1)) <= 0
                    ) {
                        mTranslationBox.append("ممیز $partII")
                    } else {
                        mTranslationBox.text = partII
                    }
                }
                if (resultIsNegative) {
                    mTranslationBox.text = "منفی " + mTranslationBox.text
                }
            }
        }
    }

    private fun isOperator(inputChar: Char): Boolean {
        return when (inputChar) {
            '+', '−', '÷', '*', 'x', '\u00d7' -> true
            else -> false
        }
    }

    private fun preventCommonErrors(currentChar: Char): Boolean {
        var hasError1 = false
        var hasError2 = false
        var hasError3 = false
        if (mExpressionString.isEmpty()) {
            hasError1 = fixFirstChar(currentChar)
        }
        hasError2 = fixSuccessiveOperators(currentChar)

        if (currentChar == '.') {
            hasError3 = fixDoublePoints(currentChar)
        }
        return hasError1 || hasError2 || hasError3
    }

    private fun fixFirstChar(input: Char): Boolean {
        if (mExpressionString.isEmpty()) {
            when (input) {
                '+', '÷', 'x', '*', '\u00d7', ')', '=' -> return true
            }
        }
        return false
    }

    private fun fixSuccessiveOperators(input: Char): Boolean {
        val lastIndex = mExpressionString.length - 1
        if (mExpressionString.length > 1) {
            if (mExpressionString[lastIndex] == '\u00d7' || mExpressionString[lastIndex] == '÷') {
                if (input == '−') {
                    return false
                }
            }

            if (isOperator(input)) {
                if (isOperator(mExpressionString[lastIndex])) {
                    return true
                }
            }
        }
        return false
    }

    private fun fixDoublePoints(input: Char): Boolean {
        var legalStart = -1
        for (index in 0 until mExpressionString.length) {
            if (!Character.isDigit(mExpressionString[index])) {
                if (mExpressionString[index] != '.') {
                    legalStart = index
                }
            }
        }
        val lastOccurrence = mExpressionString.lastIndexOf(".")
        if (lastOccurrence <= legalStart) {
            return false
        }
        return true
    }

    private fun evaluateResult(exp: Expression): String {
        mRawResult = exp.evaluate()
        val df = DecimalFormat()
        df.isGroupingUsed = true
        df.groupingSize = 3
        df.maximumFractionDigits = 6
        df.decimalFormatSymbols = DecimalFormatSymbols(Locale.US)
        return df.format(mRawResult)
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
            createNewSoundPool()
        } else {
            createOldSoundPool()
        }
        mSoundPool.setOnLoadCompleteListener { _, _, _ -> mSoundPoolLoaded = true }
        numericButtonSoundID = mSoundPool.load(applicationContext, R.raw.keypress, 1)
        executeButtonSoundID = mSoundPool.load(applicationContext, R.raw.equal, 1)
        clearAllButtonSoundID = mSoundPool.load(applicationContext, R.raw.clear, 1)
        operatorsButtonSoundID = mSoundPool.load(applicationContext, R.raw.keypress, 1)
        errorSoundID = mSoundPool.load(applicationContext, R.raw.error, 1)
        backSpaceButtonSoundID = mSoundPool.load(applicationContext, R.raw.backspace, 1)
        mHasVolumeSoundID = mSoundPool.load(applicationContext, R.raw.backspace, 1)
        mAddStarSoundID = mSoundPool.load(applicationContext, R.raw.backspace, 1)
    }

    val clearSoundID: Int
        get() = clearAllButtonSoundID

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun createNewSoundPool(): SoundPool {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        return SoundPool.Builder()
            .setAudioAttributes(attributes)
            .build()
    }

    @Suppress("DEPRECATION")
    private fun createOldSoundPool(): SoundPool {
        return SoundPool(10, AudioManager.STREAM_MUSIC, 0)
    }

    private fun prepareAnimationStuff() {
        inAnim = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        outAnimExecute = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right)
        outAnimExecute.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                mTextSwitcher.setCurrentText(mResultToDisplay)
                mTextSwitcher.startAnimation(inAnim)

                @Suppress("DEPRECATION")
                val asyncTask: AsyncTask<Any?, Any?, Any?> = object : AsyncTask<Any?, Any?, Any?>() {
                    override fun doInBackground(vararg params: Any?): Any? {
                        addLogEntry(mExpressionString.toString(), mResultToDisplay)
                        try {
                            Thread.sleep(600)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                        return null
                    }

                    override fun onPostExecute(result: Any?) {
                        // scrollToLast is now handled by HistoryFragment observing ViewModel
                    }
                }
                asyncTask.execute()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        outAnimExecute.duration = 100

        outAnimClear = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right)
        outAnimClear.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                resultTextView.text = mResultToDisplay
                mTextSwitcher.startAnimation(inAnim)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        outAnimClear.duration = 100
        prepareBlinkingErrorAnimation()
        prepareBlinkingAnimation()
    }

    private fun prepareBlinkingErrorAnimation() {
        mErrorBlink = AlphaAnimation(0.0f, 1.0f)
        mErrorBlink.duration = 100
        mErrorBlink.repeatMode = Animation.REVERSE
        mErrorBlink.repeatCount = 4
    }

    private fun prepareBlinkingAnimation() {
        mBlink = AlphaAnimation(0.0f, 1.0f)
        mBlink.duration = 100
        mBlink.repeatMode = Animation.REVERSE
        mBlink.repeatCount = 0
    }

    fun setMExpressionString(input: String) {
        mExpressionBuffer.replace(0, mExpressionBuffer.length, input)
    }

    val mExpressionString: StringBuilder
        get() = mExpressionBuffer

    fun appendMExpressionString(input: CharSequence) {
        mExpressionBuffer.append(input)
    }

    fun deleteMExpressionStringAt(index: Int) {
        mExpressionBuffer.deleteCharAt(index)
    }

    fun updateExpression(text: String) {
        mTranslationBox.text = text
    }

    fun updateResult(text: String) {
        resultTextView.text = text
    }

    fun sendClearButtonMessage(value: String) {
        if (mLayoutState != LANDSCAPE_TABLET) {
            val adapter = mViewPager.adapter as ViewPagerAdapter
            val dialpadFragment = adapter.getItem(1) as CalculatorFragment
            dialpadFragment.setClearButtonText(value)
        } else {
            val dialpadFragment = supportFragmentManager.findFragmentByTag("dialpad_fragment_tag") as CalculatorFragment
            dialpadFragment.setClearButtonText(value)
        }
    }

    fun sendChangeFontThicknessMessage() {
        if (mLayoutState != LANDSCAPE_TABLET) {
            val adapter = mViewPager.adapter as ViewPagerAdapter
            val dialpadFragment = adapter.getItem(1) as CalculatorFragment
            dialpadFragment.changeFontThickness()
        } else {
            val dialpadFragment = supportFragmentManager.findFragmentByTag("dialpad_fragment_tag") as CalculatorFragment
            dialpadFragment.changeFontThickness()
        }
    }

    private fun isNonDigit(currentButtonValue: String): Boolean {
        val nonDigitStrings = arrayOf(
            "(", ")", resources.getString(R.string.point),
            resources.getString(R.string.sin) + "(",
            resources.getString(R.string.cos) + "(",
            resources.getString(R.string.tan) + "(",
            resources.getString(R.string.cot) + "(",
            resources.getString(R.string.csc) + "(",
            resources.getString(R.string.sec) + "(",
            resources.getString(R.string.asin) + "(",
            resources.getString(R.string.acos) + "(",
            resources.getString(R.string.atan) + "(",
            resources.getString(R.string.acot) + "(",
            resources.getString(R.string.acsc) + "(",
            resources.getString(R.string.asec) + "(",
            resources.getString(R.string.sinh) + "(",
            resources.getString(R.string.cosh) + "(",
            resources.getString(R.string.tanh) + "(",
            resources.getString(R.string.coth) + "(",
            resources.getString(R.string.sech) + "(",
            resources.getString(R.string.csch) + "(",
            resources.getString(R.string.asinh) + "(",
            resources.getString(R.string.acosh) + "(",
            resources.getString(R.string.atanh) + "(",
            resources.getString(R.string.acoth) + "(",
            resources.getString(R.string.asech) + "(",
            resources.getString(R.string.acsch) + "(",
            resources.getString(R.string.ln_tag),
            resources.getString(R.string.power),
            resources.getString(R.string.log_tag),
            resources.getString(R.string.tenpowerx) + "(",
            "E",
            resources.getString(R.string.epowerx) + "(",
            resources.getString(R.string.sqrt),
            "+", "−", "-", "÷", "×",
        )

        val myList = Arrays.asList(*nonDigitStrings)
        return myList.contains(currentButtonValue)
    }

    fun setFontForComponent(key: String, value: Int) {
        val appPreferences = getSharedPreferences("typography", Context.MODE_PRIVATE)
        val editor = appPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getFontForComponent(key: String): Typeface {
        val appPreferences = getSharedPreferences("typography", Context.MODE_PRIVATE)
        val outputFont: Int = when (key) {
            "DIALPAD_FONT" -> appPreferences.getInt(key, FONT_ROBOTO_THIN)
            "SCIENTIFIC_FONT" -> appPreferences.getInt(key, FONT_ROBOTO_LIGHT)
            "RESULT_FONT" -> appPreferences.getInt(key, FONT_ROBOTO_THIN)
            "TRANSLATION_NUMERIC_FONT" -> appPreferences.getInt(key, FONT_ROBOTO_LIGHT)
            "TRANSLATION_LITERAL_FONT" -> appPreferences.getInt(key, FONT_MITRA)
            else -> 0
        }

        return when (outputFont) {
            FONT_ROBOTO_THIN -> mRobotoThin
            FONT_ROBOTO_LIGHT -> mRobotoLight
            FONT_ROBOTO_REGULAR -> mRobotoRegular
            FONT_DIGITAL_7 -> mDigital7
            FONT_YEKAN -> mPhalls
            FONT_MITRA -> mMitra
            FONT_MAJALLA -> mMajalla
            else -> mRobotoThin
        }
    }

    fun setmDefaultPage(_defaultPage: Byte) {
        mDefaultPage = _defaultPage
    }

    override fun onClick(v: View) {
        val id = v.id

        if (id == R.id.switch_deg_rad) {
            val on = (v as Switch).isChecked
            if (on) {
                setAngleMode(true)
            } else {
                setAngleMode(false)
            }
        } else if (id == R.id.favorites_list) {
            val fm = supportFragmentManager
            val favoritesDialog = FavoritesFragment()
            favoritesDialog.show(fm, "fragment_favorites")
        } else if (id == R.id.btn_add_star) {
            val selection = LogContract.LogEntry._ID + "=?"
            val cursor = contentResolver.query(
                LogContract.LogEntry.CONTENT_URI,
                null,
                selection,
                arrayOf(mLatestInsertedId.toString()),
                null,
            )
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val currentStarredStatus =
                        cursor.getInt(cursor.getColumnIndexOrThrow(LogContract.LogEntry.COLUMN_STARRED))
                    if (currentStarredStatus == 0) {
                        val values = ContentValues()
                        values.put(LogContract.LogEntry.COLUMN_STARRED, 1)
                        contentResolver.update(
                            LogContract.LogEntry.CONTENT_URI,
                            values,
                            selection,
                            arrayOf(mLatestInsertedId.toString()),
                        )
                    }
                }
                cursor.close()
            }
            playSound(mAddStarSoundID)
            setClipView(mAddStars, false)
            mAddStars.text = resources.getString(R.string.star_icon)
            mAddStars.setTextColor(Color.parseColor("#FFC107"))
            mAddStars.animate()
                .translationY(200f)
                .scaleX(0.5f)
                .scaleY(0.5f)
                .alpha(0f)
                .rotation(180f)
                .setDuration(1000)
        } else if (id == R.id.add_label) {
            val selection = LogContract.LogEntry._ID + "=?"
            val cursor = contentResolver.query(
                LogContract.LogEntry.CONTENT_URI,
                null,
                selection,
                arrayOf(mLatestInsertedId.toString()),
                null,
            )
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val currentLabel = cursor.getString(cursor.getColumnIndexOrThrow(LogContract.LogEntry.COLUMN_TAG))
                    val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                    builder.setTitle(getString(R.string.farsi_label))

                    val input = EditText(this)
                    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

                    input.inputType = InputType.TYPE_CLASS_TEXT
                    builder.setView(input)
                    builder.setCancelable(false)
                    input.setText(currentLabel)
                    input.requestFocus()
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

                    builder.setPositiveButton(getString(R.string.farsi_ok)) { _, _ ->
                        val newLabel = input.text.toString()
                        val values = ContentValues()
                        values.put(LogContract.LogEntry.COLUMN_TAG, newLabel)
                        contentResolver.update(
                            LogContract.LogEntry.CONTENT_URI,
                            values,
                            selection,
                            arrayOf(mLatestInsertedId.toString()),
                        )
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
                                KeyEvent.KEYCODE_ENTER,
                                -> {
                                    dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).performClick()
                                    return@OnKeyListener true
                                }
                            }
                        }
                        false
                    }
                    input.setOnKeyListener(keyListener)
                }
                cursor.close()
            }
        } else if (id == R.id.buttonSettings) {
            val cdc = CustomDialogClass(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth)
            cdc.show()
        } else if (id == R.id.buttonHamburgerMenu) {
            mDrawerLayout.openDrawer(GravityCompat.END)
        } else if (id == R.id.buttonMute) {
            reverseVolume()
            if (volumeFromPreference) {
                (v as Button).text = resources.getText(R.string.volume_high)
            } else {
                (v as Button).text = resources.getText(R.string.volume_off)
            }
        } else if (id == R.id.buttonColors) {
            val myIntent = Intent(this@MainActivity, ColorPickerActivity::class.java)
            myIntent.putExtra("accentColor", accentColorCode)
            myIntent.putExtra("keyPadColor", keypadBackgroundColorCode)
            startActivityForResult(myIntent, 2)
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        val on = buttonView.isChecked
        if (on) {
            setAngleMode(true)
        } else {
            setAngleMode(false)
        }
    }

    fun refreshFonts() {
        resultTextView.typeface = getFontForComponent("RESULT_FONT")
        mTranslationBoxLetterFont = getFontForComponent("TRANSLATION_LITERAL_FONT")
        mTranslationBoxNumericFont = getFontForComponent("TRANSLATION_NUMERIC_FONT")
        if (mJustPressedExecuteButton) {
            mTranslationBox.typeface = mTranslationBoxLetterFont
        } else {
            mTranslationBox.typeface = mTranslationBoxNumericFont
        }
    }
}

