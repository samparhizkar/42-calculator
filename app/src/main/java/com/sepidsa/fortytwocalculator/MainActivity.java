package com.sepidsa.fortytwocalculator;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.sepidsa.fortytwocalculator.data.ConstantContract;
import com.sepidsa.fortytwocalculator.data.LogContract;
import com.sepidsa.fortytwocalculator.sync.CurrencySyncAdapter;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Stack;



public class MainActivity extends FragmentActivity implements View.OnClickListener,  CompoundButton.OnCheckedChangeListener {
    private static final String BAZAAR_PACKAGE_NAME = "com.farsitel.bazaar";
    OnHeadlineSelectedListener mCallback;

    private static final String FRAGMENT_TAG_LOG_ = "log fragment";
    public static final byte LANGUAGE_ARABIC = 3 ;
    static final String TAG = "mainactivity";

    private static final String LOG_DATA_KEY = "log data";
    public static final int FONT_DIGITAL_7 = 3;
    public static final int FONT_YEKAN = 4;
    public static final int FONT_MAJALLA = 7;
    private long mLatestInsertedId;
    private Drawer mDrawer;
    Serializable mListView ;
    //TextSwitcher mResultTextSwitcher = null;
    //the reason it's an editText and not a TextView is solely for supporting the scrolling function
    public AutoResizeTextView mTranslationBox;

    Button mButton ;

    ViewPager mViewPager;
    private static final byte TOTAL_PAGE_COUNT = 3;
    private byte mDefaultPage;

//    private byte mDelta = -2;
     /*  Contstant for viewpager page selection
    there's the versions of this values a portrait value and a Wide Tablet value. Instead of
    making two sets of these constant for each mode I devised Another! constant called Delta
    adding these with Delta results in the desired index for viewpager whether it's portrait or
    tablet landscape . for example in portrait viewpager.getItem(Log_Fragment+ Delta) we need
     index 0 so Delta in this mode is Zero.But in Landscape Mode Page for zero'th index is Scientific
     index so here the Delta would be -2 hence viewpager.getItem(SCIENTIFIC_FRAGMENT+ Delta) i.e.
     viewpager.getItem(2 - 2)
    */

    private static final byte
            LOG_FRAGMENT = 0,
            DIALPAD_FRAGMENT = 1,

    SCIENTIFIC_FRAGMENT_LANDSCAPE_TABLET = 0,
            COLOR_PICKER_FRAGMENT_LANDSCAPE_TABLET = 1,
            COLOR_PICKER_FRAGMENT_PORTRAIT = 3 ,
            COLOR_PICKER_FRAGMENT_LANDSCAPE_PHONE = 2 ;

    int mSelectedColorCal0 = 0;

    public static final int FONT_ROBOTO_THIN = 0;
    public static final int FONT_ROBOTO_LIGHT = 1;
    public static final int FONT_ROBOTO_REGULAR = 2;
    public static final int FONT_MITRA = 5;
    private Typeface mRobotoLight;
    private Typeface mRobotoRegular;

    private static final byte PORTRAIT_PHONE = 0,
            LANDSCAPE_PHONE = 1,
            LANDSCAPE_TABLET = 2;
    public byte mLayoutState ;
    // This will be synced with the viewpager . and is provided kindly by www.viewpagerindicator.com
    private CirclePageIndicator mViewPagerIndicator = null;

    AutoResizeTextView resultTextView;
    String mResultToDisplay = "0";


    // for "2.4+4.5 this variable is 4.right after the first operator we see
//    private byte mPermittedIndexOFDotCharRepetition;
    Boolean mJustPressedExecuteButton = true;
    //This is the string work with during all the calculations.We don't necessarily display it though
    private StringBuilder mExpressionBuffer = new StringBuilder();
    //Mainly used for backspace operations
    private Stack<String> mButtonsStack = new Stack<String>();
    private static final byte DECIMAL_FRACTION_LENGTH = 3;



    /*
    Although this app is ICS+ and ICS supports Roboto out of the box.But robot is attached nevertheless
    because A) it's the latest version included in Android L which has numerous refinements one digits and letters and spacing'
    B) I have a unified vision of how the typeface looks like on various android versions.95% of app uses this typeface
    */
    Typeface mTranslationBoxNumericFont = null;
    //    this typeface will be used for translation box
    private Typeface mTranslationBoxLetterFont = null;
    // Languages supported for "number to word" translation . will use theme Later for Localization
    static final byte LANGUAGE_PERSIAN = 0
            , LANGUAGE_ENGLISH = 1
            , LANGUAGE_FRENCH = 2;
    private static final byte DEFAULT_LANGUAGE = LANGUAGE_PERSIAN;



    //These are modes for UI update
    private static final byte DIGIT_BUTTON_PRESSED = 1
            , EXECUTE_BUTTON_PRESSED = 3;
    private View result_textView_holder;
    BigDecimal mRawResult = new BigDecimal(0);
    BigDecimal mMemoryVariable = new BigDecimal(0);
    TextView mMemoryVariableTextView;
    private Animation mErrorBlink;
    public Animation mBlink;
    private SoundPool mSoundPool;
    private boolean mSoundPoolLoaded = false;
    private int numericButtonSoundID
            , executeButtonSoundID
            , clearAllButtonSoundID
            , operatorsButtonSoundID
            , errorSoundID
            , mAddStarSoundID
            , mHasVolumeSoundID
            , backSpaceButtonSoundID ;

    Animation out_anim_execute,in_anim;
    private Typeface mFlatIcon;
    public String mDecimal_fraction = "";
    private TextView mScientificModeTextView;
    private Typeface mMajalla;
    private Typeface mMitra;
    //    PopoverView popoverView;
    Button mFavoritesList;
//    EditText mTagHimself;




    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler;

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };
    private Button mAddStars;
    private Button mAddLabel;
    private Typeface mPhalls,mDigital_7;
    private Typeface mRobotoThin;

    // SKUs for our products: the premium upgrade (non-consumable)
    // Does the user have the premium upgrade?
    boolean mIsPremium = false;
    boolean mHasPuyrchasedClassicTheme = false;

    // (arbitrary) request code for the purchase flow
    static final String TAG_recreate = "recreate";


    private Button mCurrencyList;
    private Animation out_anim_clear;
    private AnimatedLogFragment mLogFragment;
    private DialpadFragment mDialpadFragment;

    private TextSwitcher mTextSwitcher;
    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(int position);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_recreate, "Activity oncreate");
        super.onCreate(savedInstanceState);




        showSplashAndTour();
        if(!getGoGoldNotifViewed()&&!getPremiumPreference()){
            setGoGoldNotifViewed(true);
            goGoldNotif();
        }
        setTypeFaces();
        if(isRetroThemeSelected()){
            setContentView(R.layout.activity_main_retro);
        }else {
            setContentView(R.layout.activity_main);
        }

        if (savedInstanceState == null) {

            mJustPressedExecuteButton = true;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();

        setMViewPager(fragmentManager);
        setMViewPagerIndicator();

        mMemoryVariableTextView =  (TextView) findViewById(R.id.m_vaiable_textview);
        mTranslationBox = (AutoResizeTextView) findViewById(R.id.translationEditText);
        resultTextView = (AutoResizeTextView) findViewById(R.id.result);
        mScientificModeTextView = (TextView) findViewById(R.id.scientific_mode_textview);
        result_textView_holder = findViewById(R.id.MotherTop);
        mFavoritesList = (Button)findViewById(R.id.favorites_list);
        mAddStars = (Button)findViewById(R.id.btn_add_star);
        mAddLabel = (Button)findViewById(R.id.add_label);
        mCurrencyList =  (Button)findViewById(R.id.currency_list);
        mFavoritesList.setOnClickListener(this);
        mAddStars.setOnClickListener(this);
        mAddLabel.setOnClickListener(this);
        mCurrencyList.setOnClickListener(this);

        mTextSwitcher = (TextSwitcher)findViewById(R.id.text_switcher);
//        If it's a new instance of application i.e. Not because of rotation or configuration changes =================
        prepareBottomIcons();
        refreshFonts();
        setIconButtons();
        buildNavigationDrawer();
        CurrencySyncAdapter.initializeSyncAdapter(this);


    }



    public static boolean isBazaarPackageInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent == null) {
            return false;
        }
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void buildNavigationDrawer() {
        // Navigation Drawer Codes //

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.forty_two_header_1440)
                .addProfiles(
                        new ProfileDrawerItem()
//                                .withName("ماشین حساب 42")
//                                .withIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                )
//                .withCompactStyle(true)
                .withAlternativeProfileHeaderSwitching(false)
                .withProfileImagesClickable(false)
//                .withSelectionFistLineShown(false)
                .withSelectionListEnabled(false)
//                .withTranslucentStatusBar(false)
                .build();


        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withDrawerGravity(Gravity.RIGHT)
                .withFullscreen(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("راهنما").withIcon(getResources().getDrawable(R.drawable.ic_lightbulb_outline_grey600_24dp)),
                        new PrimaryDrawerItem().withName("امتیاز و نظر").withIcon(getResources().getDrawable(R.drawable.ic_thumb_up_outline_grey600_24dp)),
                        new PrimaryDrawerItem().withName("درباره").withIcon(getResources().getDrawable(R.drawable.about_us)),
                        new PrimaryDrawerItem().withName("پیام به ما").withIcon(getResources().getDrawable(R.drawable.ic_email_outline_grey600_24dp)),
                        new PrimaryDrawerItem().withName("نسخه طلایی").withIcon(getResources().getDrawable(R.drawable.badge))

                )
                .withSelectedItem(-1)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
//                        Toast.makeText(getApplicationContext(), "Position " + position + " pressed", Toast.LENGTH_SHORT).show();
                        switch (position) {
                            case 0:
                                displayHelp();
                                break;
                            case 1:
                                displayRateUs();
                                break;
                            case 2:
                                displayAbout();
                                break;
                            case 3:
                                displayContactUs();
                                break;
                            case 4:
                                displayUpgradeToPremium(0);
                                break;
                            default:
                        }
                        return true;
                    }
                })
                .build();
        mDrawer = result;
    }

    private void displayRateUs() {
        Intent giveStarsIntent = new Intent(
                Intent.ACTION_EDIT,
                Uri.parse("http://cafebazaar.ir/app/com.sepidsa.fortytwocalculator/?l=fa"));
        startActivity(giveStarsIntent);

    }


    private void displayUpgradeToPremium(int i) {
        try{
            Intent myIntent = new Intent(MainActivity.this, PremiumShowcasePagerActivity.class);
            myIntent.putExtra("page", i);
            MainActivity.this.startActivity(myIntent);
        }
        catch (RuntimeException e ){
            Toast.makeText(getApplicationContext(),"مشکل در اتصال به بازار",Toast.LENGTH_LONG);
        }



    }

    private void displayContactUs() {
        sendEmail(this, getString(R.string.farsi_about_42_calc), "", null);

    }

    public static void sendEmail(final Context p_context, final String p_subject, final String p_body, final ArrayList<String> p_attachments)
    {
        try
        {
            PackageManager pm = p_context.getPackageManager();
            ResolveInfo selectedEmailActivity = null;

            Intent emailDummyIntent = new Intent(Intent.ACTION_SENDTO);
            emailDummyIntent.setData(Uri.parse("mailto:feedback@sepidsa.com"));

            List<ResolveInfo> emailActivities = pm.queryIntentActivities(emailDummyIntent, 0);

            if (null == emailActivities || emailActivities.size() == 0)
            {
                Intent emailDummyIntentRFC822 = new Intent(Intent.ACTION_SEND_MULTIPLE);
                emailDummyIntentRFC822.setType("message/rfc822");

                emailActivities = pm.queryIntentActivities(emailDummyIntentRFC822, 0);
            }

            if (null != emailActivities)
            {
                if (emailActivities.size() == 1)
                {
                    selectedEmailActivity = emailActivities.get(0);
                }
                else
                {
                    for (ResolveInfo currAvailableEmailActivity : emailActivities)
                    {
                        if (true == currAvailableEmailActivity.isDefault)
                        {
                            selectedEmailActivity = currAvailableEmailActivity;
                        }
                    }
                }

                if (null != selectedEmailActivity)
                {
                    // Send email using the only/default email activity
                    sendEmailUsingSelectedEmailApp(p_context, p_subject, p_body, p_attachments, selectedEmailActivity);
                }
                else
                {
                    final List<ResolveInfo> emailActivitiesForDialog = emailActivities;

                    String[] availableEmailAppsName = new String[emailActivitiesForDialog.size()];
                    for (int i = 0; i < emailActivitiesForDialog.size(); i++)
                    {
                        availableEmailAppsName[i] = emailActivitiesForDialog.get(i).activityInfo.applicationInfo.loadLabel(pm).toString();
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(p_context);
                    builder.setTitle(p_context.getString(R.string.farsi_choose_email_app));
                    builder.setItems(availableEmailAppsName, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendEmailUsingSelectedEmailApp(p_context, p_subject, p_body, p_attachments, emailActivitiesForDialog.get(which));
                        }
                    });

                    builder.create().show();
                }
            }
            else
            {
                sendEmailUsingSelectedEmailApp(p_context, p_subject, p_body, p_attachments, null);
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Can't send email", ex);
        }
    }

    protected static void sendEmailUsingSelectedEmailApp(Context p_context, String p_subject, String p_body, ArrayList<String> p_attachments, ResolveInfo p_selectedEmailApp)
    {
        try
        {
            Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

            String aEmailList[] = { "feedback@sepidsa.com"};

            emailIntent.putExtra(Intent.EXTRA_EMAIL, aEmailList);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, null != p_subject ? p_subject : "");
            emailIntent.putExtra(Intent.EXTRA_TEXT, null != p_body ? p_body : "");

            if (null != p_attachments && p_attachments.size() > 0)
            {
                ArrayList<Uri> attachmentsUris = new ArrayList<Uri>();

                // Convert from paths to Android friendly Parcelable Uri's
                for (String currAttachemntPath : p_attachments)
                {
                    File fileIn = new File(currAttachemntPath);
                    Uri currAttachemntUri = Uri.fromFile(fileIn);
                    attachmentsUris.add(currAttachemntUri);
                }
                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachmentsUris);
            }

            if (null != p_selectedEmailApp)
            {
                Log.d(TAG, "Sending email using " + p_selectedEmailApp);
                emailIntent.setComponent(new ComponentName(p_selectedEmailApp.activityInfo.packageName, p_selectedEmailApp.activityInfo.name));

                p_context.startActivity(emailIntent);
            }
            else
            {
                Intent emailAppChooser = Intent.createChooser(emailIntent, "Select Email app");

                p_context.startActivity(emailAppChooser);
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error sending email", ex);
        }
    }

    private void displayAbout() {
        Intent myIntent = new Intent(MainActivity.this, AboutActivity.class);
        MainActivity.this.startActivity(myIntent);

    }

    private void displayHelp() {
        Intent myIntent = new Intent(MainActivity.this, HelpActivity.class);
        MainActivity.this.startActivity(myIntent);

    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);


        outState.putSerializable("buttonStack", mButtonsStack);
        outState.putString("mResultText", resultTextView.getText().toString());
        outState.putString("mScientificModeTextView", mScientificModeTextView.getText().toString());

        outState.putCharSequence("mTranslationText", mTranslationBox.getText().toString());
        outState.putSerializable("mButtonStack", mButtonsStack);
        outState.putString("mExpressionBuffer", mExpressionBuffer.toString());
        outState.putBoolean("mJustPressedExecuteButton", mJustPressedExecuteButton);
        outState.putBoolean("mISRetroThemeOn", isRetroThemeSelected());
        outState.putSerializable("mMemoryVariable", mMemoryVariable);
        outState.putString("mResultToDisplay", mResultToDisplay);
        outState.putString("mDecimal_fraction", mDecimal_fraction);
        Log.d(TAG_recreate, "Activity onSaveInstanceState ");
//        mViewPager.removeAllViews();
//        mViewPager = null;



    }

    public String getVersion() throws PackageManager.NameNotFoundException {
        return  getApplicationContext().getPackageManager()
                .getPackageInfo(getPackageName(), 0).versionName;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG_recreate, "Activity onRestoreInstanceState and is " + savedInstanceState);

        // todo mtranslationbox restore

        if(savedInstanceState!= null) {
            mTranslationBox = getMTranslationEditText();
            mTranslationBox.setText(savedInstanceState.getString("mTranslationText"));
            mJustPressedExecuteButton = savedInstanceState.getBoolean("mJustPressedExecuteButton");
            mButtonsStack = new Stack<String>();
            mButtonsStack.addAll((Collection<String>) savedInstanceState.getSerializable("mButtonStack"));

            //TODO
            mExpressionBuffer = new StringBuilder();
            String exTemp = savedInstanceState.getString("mExpressionBuffer");


            mMemoryVariable = (BigDecimal) savedInstanceState.getSerializable("mMemoryVariable");
            mMemoryVariableTextView.setText(" M = " + mMemoryVariable.toString());
            mResultToDisplay = savedInstanceState.getString("mResultToDisplay");
            mDecimal_fraction = savedInstanceState.getString("mDecimal_fraction");



            if (exTemp != null)
                mExpressionBuffer = new StringBuilder(exTemp);
            else
                mExpressionBuffer = new StringBuilder();

            resultTextView.setText(savedInstanceState.getString("mResultText"));

        }
    }

public void goGoldNotif() {
    // First let's define the intent to trigger when notification is selected
// Start out by creating a normal intent (in this case to open an activity)
    Intent intent = new Intent(this, PremiumShowcasePagerActivity.class);
    // Next, let's turn this into a PendingIntent using
//   public static PendingIntent getActivity(Context context, int requestCode,
//       Intent intent, int flags)
    int requestID = (int) System.currentTimeMillis(); //unique requestID to differentiate between various notification with same NotifId
    int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one
    PendingIntent pIntent = PendingIntent.getActivity(this, requestID, intent, flags);
    // Now we can attach this to the notification using setContentIntent
    Notification noti =
            new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("ماشین حساب ۴۲")
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                    .setContentText("نسخه طلایی ماشین حساب ۴۲ رو امتحان کردی ؟")
                    .setContentIntent(pIntent).build();

// Hide the notification after its selected
//    noti.setAutoCancel(true);
    noti.flags |= Notification.FLAG_AUTO_CANCEL;

    NotificationManager mNotificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
    mNotificationManager.notify(0, noti);

}
    @Override
    protected void onStart() {

        super.onStart();

        setResultTextBox();

        mTranslationBox = (AutoResizeTextView)findViewById(R.id.translationEditText);

        switch (getTranslationLanguage()) {
            case LANGUAGE_PERSIAN:
                getMTranslationEditText().setTypeface(mTranslationBoxLetterFont);
                break;

            default:
                getMTranslationEditText().setTypeface(getFontForComponent("RESULT_FONT"));
        }

        if(!isRetroThemeSelected()) {
            resultTextView.setBackgroundColor(getAccentColorCode());
        }
        if(getAngleMode() == true){
            mScientificModeTextView.setText("DEG");
        }else{
            mScientificModeTextView.setText("RAD");
        }
        Runnable runnable = new Runnable() {
            public void run() {
                prepareAnimationStuff();
                prepareSoundStuff();
                populateConstantDatabaseFirstRun();

            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();




    }




    @Override
    protected void onResume() {
        if (!mJustPressedExecuteButton) {
            mTranslationBox.setTypeface(mTranslationBoxNumericFont);
        }

        redrawAccent();
        redrawKeypadBackground();

        super.onResume();

    }

    private void prepareBottomIcons() {


        ( (Button)findViewById(R.id.buttonSettings)).setTypeface(mFlatIcon);
        ( (Button)findViewById(R.id.buttonSettings)).setTextColor(getAccentColorCode());
        ( (Button)findViewById(R.id.buttonSettings)).setTextColor(Color.parseColor("#BDBDBD"));
        ( findViewById(R.id.buttonSettings)).setOnClickListener(this);


        ( (Button)findViewById(R.id.buttonMute)).setTypeface(mFlatIcon);
        ( (Button)findViewById(R.id.buttonMute)).setTextColor(Color.parseColor("#BDBDBD"));
        findViewById(R.id.buttonMute).setOnClickListener(this);
        if(getVolumeFromPreference()) {
            ((Button) findViewById(R.id.buttonMute)).setText(getResources().getText(R.string.volume_high));
        }else {
            ( (Button)findViewById(R.id.buttonMute)).setText(getResources().getText(R.string.volume_off));
        }

        ( (Button)findViewById(R.id.buttonColors)).setTypeface(mFlatIcon);
        ( (Button)findViewById(R.id.buttonColors)).setTextColor(Color.parseColor("#BDBDBD"));
        ( findViewById(R.id.buttonColors)).setOnClickListener(this);


        ( (Button)findViewById(R.id.buttonHamburgerMenu)).setTypeface(mFlatIcon);
        ( (Button)findViewById(R.id.buttonHamburgerMenu)).setTextColor((Color.parseColor("#BDBDBD")));
        ( findViewById(R.id.buttonHamburgerMenu)).setOnClickListener(this);


    }

    private boolean getVolumeFromPreference(){
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("volumeState", Context.MODE_PRIVATE);
        return  appPreferences.getBoolean("hasVolume",true);
    }



    @Override
    protected void onDestroy() {
        Log.d(TAG_recreate, "Activity ondestroy");

        if (isFinishing()) {
            // todo clear database if user if non-premium
            if(!getPremiumPreference()) {
                getContentResolver().delete(LogContract.LogEntry.CONTENT_URI, null, null);
            }


        } else {
            //It's an orientation change.
            if (mHandler != null) {
                mHandler.removeCallbacks(mRunnable);
            }
        }
        super.onDestroy();


    }

    @Override
    public void onBackPressed() {

        //TODO check if it works for tablet landscape
        // if we're in default page prompt for exit else switch to default page TODO set default page here

        if ((mLayoutState == LANDSCAPE_TABLET )||
                (mViewPager.getCurrentItem() == DIALPAD_FRAGMENT )) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this,"دوباره لطفا", Toast.LENGTH_SHORT).show();

            mHandler = new Handler();
            mHandler.postDelayed(mRunnable, 2000);

        } else  {
            mViewPager.setCurrentItem(DIALPAD_FRAGMENT);

        }



    }

    //  Mind casting of viewpagerindicator .there's 6 types of this object in the library availble
    private void setMViewPagerIndicator() {
        mViewPagerIndicator = (CirclePageIndicator) findViewById(R.id.view_pager_indicator);
        if (mViewPagerIndicator  != null) {
            mViewPagerIndicator.setViewPager(mViewPager);
            mViewPagerIndicator.setCurrentItem(mDefaultPage);
            mViewPagerIndicator.setFillColor(getAccentColorCode());

        }
    }

    //    if we're in portrait mode there are 5 pages in the indicator but in tablet mode there are 2 tabs only
    private void setMViewPager(FragmentManager fragmentManager) {
        // This view is only present in the portrait xml file. I use it as measure for portrait/landscape judgement!
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        if (mViewPager!= null && mViewPager.getVisibility() == View.VISIBLE) {
            List<Fragment> fList = new ArrayList<Fragment>();
            mViewPager.setOffscreenPageLimit(0);
            mLogFragment = new AnimatedLogFragment();
            fList.add(mLogFragment);
            fList.add(new DialpadFragment());
            if( mViewPager.getTag().equals("portrait_phone")) {
                // if in landscape phone also add a page for scientific
                fList.add(new ScientificFragment());
                mLayoutState = PORTRAIT_PHONE;
            }else {
                mLayoutState = LANDSCAPE_PHONE;
            }
            // if in Portrait Phone

            mViewPager.setAdapter(new ViewPagerAdapter(fragmentManager, fList));
            mViewPager.setCurrentItem(DIALPAD_FRAGMENT);
            setmDefaultPage(DIALPAD_FRAGMENT);

        }
        else {
            mLayoutState = LANDSCAPE_TABLET;
            mLogFragment = (AnimatedLogFragment)getSupportFragmentManager().findFragmentByTag("fragment_log_tablet_land");
        }

    }


    private void setTypeFaces() {

        mRobotoLight = Typeface.createFromAsset(getApplicationContext().getAssets(), "roboto_light.ttf");
        mRobotoRegular = Typeface.createFromAsset(getApplicationContext().getAssets(), "roboto_regular.ttf");
        mRobotoThin = Typeface.createFromAsset(getApplicationContext().getAssets(), "roboto_thin.ttf");

        mMajalla = Typeface.createFromAsset(getApplicationContext().getAssets(), "yekan.ttf");
        mFlatIcon = Typeface.createFromAsset(getApplicationContext().getAssets(), "flaticon.ttf");
        mMitra = Typeface.createFromAsset(getApplicationContext().getAssets(), "mitra.ttf");
        mPhalls =  Typeface.createFromAsset(getApplicationContext().getAssets(), "yekan.ttf");
        mDigital_7 =  Typeface.createFromAsset(getApplicationContext().getAssets(), "digital_7.ttf");


    }

    private void setIconButtons() {
        //todo refactor code
        mCurrencyList.setTypeface(mFlatIcon);
        mFavoritesList.setTypeface(mFlatIcon);
        mFavoritesList.setText(getResources().getString(R.string.list));
        mFavoritesList.setTextSize(30);

        mAddLabel.setTypeface(mFlatIcon);
        mAddStars.setTypeface(mFlatIcon);

    }

    private void setResultTextBox(){

        if(!isRetroThemeSelected()) {
            resultTextView.setBackgroundColor(getAccentColorCode());
            result_textView_holder.setBackgroundColor(getAccentColorCode());
            resultTextView.setTextColor(Color.WHITE);
        }else {
            resultTextView.setBackgroundColor(Color.BLACK);
            resultTextView.setTextColor(Color.BLACK);

        }
        resultTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
    }

    public int getLastSavedAppVersion(){
        //TODO set a cool default theme color
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("APP", MODE_PRIVATE);
        return Integer.parseInt(appPreferences.getString("appVersion", "2.00"));
    }
    public int getAccentColorCode(){
        //TODO set a cool default theme color
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("THEME", MODE_PRIVATE);
        return appPreferences.getInt("ACCENT_COLOR_CODE", Color.parseColor("#009688"));
    }

//    public boolean hasWatchedAppTour(){
//
//        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("APP", MODE_PRIVATE);
//        return appPreferences.getBoolean("has_watched_app_tour_v2.00", false);
//    }
//
//    private void setWatchedAppTour(){
//        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("APP", MODE_PRIVATE);
//        SharedPreferences.Editor editor = appPreferences.edit();
//        editor.putBoolean("has_watched_app_tour_v2.00", true);
//        editor.commit();
//    }

    public boolean getAngleMode(){
        //TODO set a cool default theme color
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("angleMode", MODE_PRIVATE);
        return appPreferences.getBoolean("isDeg",true);
    }

    void setAngleMode(boolean isDeg){
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("angleMode", MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();
        if(isDeg){
            editor.putBoolean("isDeg", true);
            mScientificModeTextView.setText("DEG");

        }else{
            editor.putBoolean("isDeg", false);
            mScientificModeTextView.setText("RAD");
        }
        editor.apply();


    }

    public int getKeypadBackgroundColorCode(){
        //TODO set a cool default theme color
        int  currentThemeNumber = -1;
        SharedPreferences appPreferences =getApplicationContext().getSharedPreferences("THEME", Context.MODE_PRIVATE);
        currentThemeNumber = appPreferences.getInt("KEYPAD_BACKGROUND_COLOR_CODE",Color.WHITE);

        return currentThemeNumber;
    }

    void redrawAccent(){

        if(!isRetroThemeSelected()) {
            mFavoritesList.setTextColor(getAccentColorCode());
            mCurrencyList.setTextColor(getAccentColorCode());
            if(mViewPager!=null) {
                mViewPagerIndicator.setFillColor(getAccentColorCode());
            }
        }else {
            resultTextView.setBackgroundColor(Color.TRANSPARENT);
            result_textView_holder.setBackgroundColor(Color.TRANSPARENT);
            mFavoritesList.setTextColor(Color.BLACK);
            mCurrencyList.setTextColor(Color.BLACK);
            mAddStars.setTextColor(Color.BLACK);
            if(mViewPager!=null) {
                mViewPagerIndicator.setFillColor(Color.parseColor("#9e9e9e"));
            }
        }
    }

    int indexOf(int[] parent,int child){

        for(int index=0; index <parent.length;index++){
            if(parent[index] == child) {
                return index;
            }
        }
        return 0;
    }

    public int getDialpadFontColor() {
        String[] color_array = this.getResources().getStringArray(R.array.dialpad_font_color_choice_values);
        int indexOfCurrentBackgroundColor = indexOf(Utils.ColorUtils.colorChoiceForKeypad(getApplicationContext()), getKeypadBackgroundColorCode());
        return  Color.parseColor(color_array[indexOfCurrentBackgroundColor]);
    }

    private void redrawKeypadBackground() {
        View activityView = findViewById(R.id.activity_body);
        if (isRetroThemeSelected()){
//                activityView.setBackground(getDrawable(R.drawable.background_normal));
            mTranslationBox.setBackgroundColor(Color.TRANSPARENT);

        }else{
//            fragmentContainer.setBackground(key());
            mTranslationBox.setBackgroundColor(getKeypadBackgroundColorCode());
            mTranslationBox.setTextColor(getDialpadFontColor());
            activityView.setBackgroundColor(getKeypadBackgroundColorCode());

        }
    }



    public int getTranslationLanguage(){
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("LanguagePreference", MODE_PRIVATE);
        //Todo make sure this works with byte constants
        return appPreferences.getInt("LANGUAGE", DEFAULT_LANGUAGE);
    }
    public AutoResizeTextView getMTranslationEditText(){
        return (AutoResizeTextView) findViewById(R.id.translationEditText);
    }
    public void setTranslationText(String text){
        AutoResizeTextView translationBox;
        translationBox = (AutoResizeTextView) findViewById(R.id.translationEditText);
        translationBox.setText(text);
        translationBox.setMovementMethod(ScrollingMovementMethod.getInstance());
    }



    /// ==================== ViewController implementation

    void aButtonIsPressed(String currentButtonValue){
        // currentButtonValue is tag element of Buttons
        // ordering of this if matters!! don't move it down bellow
        //todo mdelta goes here

        switchToMainFragment();


        if (currentButtonValue.equals(getResources().getString(R.string.clear))) {
            performClearResult();

            return;
        }

        if (currentButtonValue.equals(getResources().getString(R.string.mc))) {
            performMC();
            return;
        }


        if (currentButtonValue.equals(getResources().getString(R.string.mr))) {
            performMR();
            return;
        }

        if (currentButtonValue.equals(getResources().getString(R.string.mplus))) {
            performMPlus();
            return;
        }

        if (currentButtonValue.equals(getResources().getString(R.string.mminus))) {
            performMMinus();
            return;
        }

        if (currentButtonValue.equals(getResources().getString(R.string.backSpace))) {
            performBackspace();
            return;
        }



        if  ( mJustPressedExecuteButton){
            if ( Character.isDigit(currentButtonValue.charAt(0))
                    || currentButtonValue.equals(getResources().getString(R.string.Pi))
                    || currentButtonValue.equals(getResources().getString(R.string.EXP))) {
                setMExpressionString("");
                mButtonsStack.clear();
            }
        }
        mJustPressedExecuteButton = false;
        boolean  shouldPrevent = preventCommonErrors(currentButtonValue.charAt(0));
        if(shouldPrevent) {
            playSound(errorSoundID);
            mTranslationBox.startAnimation(mErrorBlink);
            return;
        }


        if( currentButtonValue.equals("=") ) {
            if(getMExpressionString().length() > 0){
                if (calculateResult(null) == 0) {
                    mJustPressedExecuteButton = true;
                    updateUIExecute(true);
                }
            }
        }
        else
            //TODO Here a button is pressed which we don't want to be evaluated (non DIGIT or Functions)
            if (isNonDigit(currentButtonValue)){
                appendMExpressionString(currentButtonValue);
                pushLastButton(currentButtonValue);
                playSound(operatorsButtonSoundID);
                updateUIOperator();
            }
            else {
                //else it is deffinately a digit
                playSound(numericButtonSoundID);

                if (calculateResult(currentButtonValue) != 2){
                    updateUIDigit();
                }
            }
        checkCLRButtonSendIntent();
    }

    public void switchToMainFragment() {
        if  (mViewPager!=null && mViewPager.getCurrentItem() != this.mDefaultPage) {
            mViewPager.setCurrentItem(this.mDefaultPage);
        }

    }

    private void performMMinus() {

        mMemoryVariable =  mMemoryVariable.subtract(new BigDecimal(resultTextView.getText().toString().replace(",", "")));

        //todo add mmvariable with what's on display right now
        DecimalFormat df = new DecimalFormat();
        df.setGroupingUsed(true);
        df.setGroupingSize(3);
        df.setMaximumFractionDigits(6);
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        String result = df.format(mMemoryVariable);
        result = result.replaceAll("^-(?=0(.0*)?$)", "");
        mButtonsStack.clear();
        mButtonsStack.push(mResultToDisplay);
        mJustPressedExecuteButton = true;
        playSound(clearAllButtonSoundID);
        checkCLRButtonSendIntent();
        mMemoryVariableTextView.setText(" M = " + result);

        Toast.makeText(getApplicationContext(), "M-",Toast.LENGTH_SHORT).show();
    }

    private void performMPlus() {


        //todo add mmvariable with what's on display right now
        mMemoryVariable =  mMemoryVariable.add(new BigDecimal(resultTextView.getText().toString().replace(",","")));
        DecimalFormat df = new DecimalFormat();
        df.setGroupingUsed(true);
        df.setGroupingSize(3);
        df.setMaximumFractionDigits(6);
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        String result = df.format(mMemoryVariable);
        result = result.replaceAll("^-(?=0(.0*)?$)", "");
        mButtonsStack.clear();
        mButtonsStack.push(mResultToDisplay);
        mJustPressedExecuteButton = true;
        playSound(clearAllButtonSoundID);
        checkCLRButtonSendIntent();
        mMemoryVariableTextView.setText(" M = " + result);
        Toast.makeText(getApplicationContext(),"M+",Toast.LENGTH_SHORT).show();

    }

    private void performMR() {

        addNumberToCalculation(mMemoryVariable.toPlainString().toString());
        Toast.makeText(getApplicationContext(), "MR",Toast.LENGTH_SHORT).show();

    }

    public void addNumberToCalculation(String inputString) {
        String rawExpressionString = getMExpressionString().toString();
        if( rawExpressionString.length()>0 && (rawExpressionString.charAt(rawExpressionString.length()-1) == '+' ||
                rawExpressionString.charAt(rawExpressionString.length()-1) == '−' ||
                rawExpressionString.charAt(rawExpressionString.length()-1) == '\u00d7' ||
                rawExpressionString.charAt(rawExpressionString.length()-1) == '÷')
                ) {

            setMExpressionString(getMExpressionString().append(inputString).toString());
            Expression expression = new Expression(getMExpressionString().toString(),getAngleMode(),getApplicationContext());
            mResultToDisplay = evaluateResult(expression);
            mRawResult = new BigDecimal(mResultToDisplay.replace(",",""));
            resultTextView.setText(mResultToDisplay);
            CharSequence mr = inputString;
            if(mr != null) {
                for(int index=0; index < mr.length(); index++) {
                    mButtonsStack.push(String.valueOf(mr.charAt(index)));
                }
            }
            updateUIDigit();

        }else {

            DecimalFormat df = new DecimalFormat();
            df.setGroupingUsed(true);
            df.setGroupingSize(3);
            df.setMaximumFractionDigits(6);
            df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
            BigDecimal inputDecimal = new BigDecimal(inputString.replace(",",""));
            mResultToDisplay = df.format(inputDecimal);
            mResultToDisplay = mResultToDisplay.replaceAll("^-(?=0(.0*)?$)", "");

            mRawResult = new BigDecimal(mResultToDisplay.replace(",",""));

            //new code
            mButtonsStack.clear();
            CharSequence mr = mResultToDisplay.replace(",","");
            if(mr != null) {
                for(int index=0; index < mr.length(); index++) {
                    mButtonsStack.push(String.valueOf(mr.charAt(index)));
                }
            }
            mJustPressedExecuteButton = true;
            setMExpressionString(mRawResult.toString());
            mDecimal_fraction = "";
            if(mResultToDisplay.indexOf(".") != -1){
                mDecimal_fraction = mResultToDisplay.substring(mResultToDisplay.indexOf(".")+ 1);
            }
//            resultTextView.startAnimation(out_anim_execute);
            mTextSwitcher.startAnimation(out_anim_execute);
            displayTranslation(true);
            checkCLRButtonSendIntent();

        }


        playSound(clearAllButtonSoundID);
    }

    private void performMC() {
        mMemoryVariable = new BigDecimal(0);
        mMemoryVariableTextView.setText(" M = 0");
        mDecimal_fraction = "";
        Toast.makeText(getApplicationContext(),"MC",Toast.LENGTH_SHORT).show();
        playSound(clearAllButtonSoundID);

    }


    private boolean reverseVolume() {
        //if user has volume ... the mute it
        if (getVolumeFromPreference()) {

            setVolumeInPreference(false);
            //  sendVolumeMessage(false);
            //  sendVolumeMessage(false);
            return false;

        }else{
            // turn the volume up
            // sendVolumeMessage(true);
            setVolumeInPreference(true);

            playSound(mHasVolumeSoundID);
            return true;
        }
    }



    private void setVolumeInPreference(boolean hasVolume) {
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("volumeState", MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();
        if (hasVolume) {

            editor.putBoolean("hasVolume", true);

        } else {

            editor.putBoolean("hasVolume", false);

        }
        editor.apply();


    }


    void showSplashAndTour(){
        if(!getSplashAndTourViewed()){
            setSplashAndTourViewed(true);

            Intent intent = new Intent(this, ParallaxPagerActivity.class);
            overridePendingTransition(R.anim.appear, R.anim.disappear);
            startActivity(intent);
        }
    }

    void populateConstantDatabaseFirstRun(){
        if(!getPopulateConstantDatabase()){

            String[] names = getResources().getStringArray(R.array.constant_default_names);
            String[] numbers = getResources().getStringArray(R.array.constant_default_numbers);
            String[] selections = getResources().getStringArray(R.array.constant_default_selections);

            for(int index = 0 ; index < names.length ; index++) {
                String newName = names[index];
                Double newNumber = Double.parseDouble(numbers[index]);
                int selection = Integer.parseInt(selections[index]);
                ContentValues values = new ContentValues();

                values.put(ConstantContract.ConstantEntry.COLUMN_NAME, newName);
                values.put(ConstantContract.ConstantEntry.COLUMN_NUMBER, newNumber);
                values.put(ConstantContract.ConstantEntry.COLUMN_SELECTED, selection);

                getContentResolver().insert(
                        ConstantContract.ConstantEntry.CONTENT_URI,
                        values
                );
            }

            setPopulateConstantDatabase(true);
        }

    }

    private void setPopulateConstantDatabase(boolean hasPopulated) {
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("APP", MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();

        editor.putBoolean("hasPopulatedConstantDatabase", hasPopulated);


        editor.apply();
    }

    private void setSplashAndTourViewed(boolean hasViewed) {
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("APP", MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();

        editor.putBoolean("hasViewedTour", hasViewed);


        editor.apply();
    }
 private void setGoGoldNotifViewed(boolean hasViewed) {
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("APP", MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();

        editor.putBoolean("hasViewedGoGoldNotif", hasViewed);
        editor.apply();
    }
    private boolean getGoGoldNotifViewed() {
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("APP", MODE_PRIVATE);
        return  appPreferences.getBoolean("hasViewedGoGoldNotif",false);
    }


    private boolean getPopulateConstantDatabase() {
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("APP", MODE_PRIVATE);
       return  appPreferences.getBoolean("hasPopulatedConstantDatabase",false);
    }

    private boolean getSplashAndTourViewed() {
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("APP", MODE_PRIVATE);
        return  appPreferences.getBoolean("hasViewedTour",false);
    }

    private void performBackspace() {

        // First we cut the last button that we pressed


        if (mButtonsStack.size() > 0) {
            String tempexpString = getMExpressionString()
                    .substring(0, getMExpressionString().length() - lastButtonLength());
            this.setMExpressionString(tempexpString);
            popPressedButton();

            if (isNonDigit(peekLastButton())) {
                //last button was non-Digit so no need to calculate result and face an Error !
                updateUIOperator();

            } else {
                calculateResult(null);
                updateUIDigit();
            }
        }
        playSound(backSpaceButtonSoundID);
        checkCLRButtonSendIntent();

    }
    private int lastButtonLength(){

        return peekLastButton().length();
    }
    public void pushLastButton(String input){
        mButtonsStack.push(input) ;

    }
    public String popPressedButton(){
        return mButtonsStack.pop();

    }
    public String peekLastButton(){
        return mButtonsStack.peek();
    }

    private void performClearResult(){

        setMExpressionString("");
        setTranslationText("");
//        mResult.setText(getResources().getString(R.string.zero)); todo
        mResultToDisplay = "0";
        resultTextView.startAnimation(out_anim_clear);

        mButtonsStack.clear();
        playSound(clearAllButtonSoundID);
        mAddStars.setVisibility(View.GONE);
        mAddLabel.setVisibility(View.GONE);
    }

    int stackSize(){

        return  mButtonsStack.size();
    }


    void checkCLRButtonSendIntent(){
        if(stackSize()<= 1) {
            sendClearButtonMessage(getResources().getString(R.string.clear));
        }
        else{
            Log.d(TAG_recreate, "Activity Right before sendng message");
            sendClearButtonMessage(getResources().getString(R.string.backSpace));
        }
    }


    private boolean updateUIExecute(boolean sendLogMessage) {

        if(isRetroThemeSelected()){
            if(!getPremiumPreference()){
                displayUpgradeToPremium(1);
                return false;
            }
        }
        mDecimal_fraction = "";
        byte decimalIndex = (byte) mResultToDisplay.indexOf(".");
        if(decimalIndex != -1){
            mDecimal_fraction = mResultToDisplay.substring(mResultToDisplay.indexOf(".")+1);
        }

        try {

            mAddStars.setAlpha(1);
            mAddStars.setRotation(0);
            mAddStars.setText(getResources().getString(R.string.star_outline));
            if(isRetroThemeSelected()){
                mAddStars.setTextColor(Color.BLACK);
            }else {
                mAddStars.setTextColor(Color.WHITE);
            }
            mAddStars.setScaleX(1);
            mAddStars.setScaleY(1);
            mAddStars.setTranslationY(0);
            mAddStars.setVisibility(View.VISIBLE);


            mAddLabel.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
            playSound(errorSoundID);
            return true;
        }



        mButtonsStack.clear();
        mButtonsStack.push(resultTextView.getText().toString().replace(",", ""));

        displayTranslation(sendLogMessage);

        if(sendLogMessage) {
            playSound(executeButtonSoundID);
//            resultTextView.startAnimation(out_anim_execute);
            mTextSwitcher.startAnimation(out_anim_execute);

        }else{
            resultTextView.setText(mResultToDisplay);

        }

        return false;
    }

    //log added == true
    //
    private void updateUIDigit() {
        if(getMExpressionString().length()>0) {
            //any button But Equal pressed ..refresh result With mMathexpression
            //Also mResult With Value of mExp
            resultTextView.setText(mResultToDisplay);
            String gg = getMExpressionString().toString().replaceAll("(?<!\\.\\d{0,6})\\d+?(?=(?:\\d{3})+(?:\\D|$))", "$0,");
            mTranslationBox.setTypeface(mTranslationBoxNumericFont);
            mTranslationBox.setText(gg);

            mAddStars.setVisibility(View.GONE);
            mAddLabel.setVisibility(View.GONE);

        }
    }

    private byte calculateResult(String currentButtonValue) {
        mResultToDisplay = "0";
        String testSubject ;
        testSubject = mExpressionBuffer.toString();
        if(currentButtonValue != null){
            testSubject += currentButtonValue ;
        }
        try {
            String withoutcomas = testSubject.toString().replace(",","");
            Expression expression = new Expression(withoutcomas,getAngleMode(), getApplicationContext());
            mResultToDisplay = evaluateResult(expression);
            mExpressionBuffer = new StringBuilder(testSubject);
            if(currentButtonValue != null) {
                pushLastButton(currentButtonValue);
            }


        } catch (ArithmeticException e) {

            mExpressionBuffer = new StringBuilder(testSubject);
            if(currentButtonValue != null) {
                pushLastButton(currentButtonValue);
            }
            mResultToDisplay = "∞";

            // Happens if it is devision by zero
            return 1;
        }
        catch (NumberFormatException e) {

            mExpressionBuffer = new StringBuilder(testSubject);
            if(currentButtonValue != null) {
                pushLastButton(currentButtonValue);
            }
            mResultToDisplay = "error";
            // Happens if it is devision by zero
            return 1;
        }



        catch (Exception e) {
            e.printStackTrace();
            playSound(errorSoundID);
            mJustPressedExecuteButton = false;
            //TODO decide the fate of clear button ?
            // Happens for generals exceptions

            return 2;
        }
        // returning zero means there's no problems what so ever
        return 0;
    }

    private void updateUIOperator() {
        String gg = getMExpressionString().toString().replaceAll("(?<!\\.\\d{0,6})\\d+?(?=(?:\\d{3})+(?:\\D|$))", "$0,");
        mTranslationBox.setTypeface(mTranslationBoxNumericFont);
        mTranslationBox.setText(gg);
        mAddStars.setVisibility(View.GONE);
        mAddLabel.setVisibility(View.GONE);
        return;
    }
    boolean isRetroThemeSelected(){
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("THEME", MODE_PRIVATE);
        return appPreferences.getBoolean("is_retro_theme_selected", false);
    }

    void setRetrothemeSelected(boolean _isSelected) {
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("THEME", MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putBoolean("is_retro_theme_selected", _isSelected);
        editor.apply();

    }

    public void displayTranslation(boolean animate) {


        mTranslationBox.setTypeface(mTranslationBoxLetterFont);
        printResultWithTranslation(mDecimal_fraction, getTranslationLanguage());
        if(animate) {
            mTranslationBox.startAnimation(mBlink);
        }
    }



    private void printResultWithTranslation(String decimal_fraction, int Language) {
        String resultWithoutCommas = mResultToDisplay.replace(",", "");
        int pointIndex = resultWithoutCommas.indexOf(".");
        String integerFraction = "";
        boolean resultIsNegative = mRawResult.compareTo(new BigDecimal(-0.0000009)) < 0;

        if(pointIndex == -1){
            integerFraction = resultWithoutCommas;
        }else {
            integerFraction = resultWithoutCommas.substring(0, pointIndex);
        }
        if(resultIsNegative && integerFraction.length()>0){
            integerFraction = integerFraction.substring(1);
        }
        String partII;


        switch (Language){
            case LANGUAGE_ENGLISH:
                if(resultIsNegative){
                    mTranslationBox.setText("minus " + NumberConveterAmerican.convert(integerFraction.substring(0)));
                }
                else {
                    mTranslationBox.setText(NumberConveterAmerican.convert(integerFraction));
                }
                if (!decimal_fraction.equals("")) {
                    partII = NumberConveterAmericanPartII.convert(decimal_fraction);
                    mTranslationBox.append(partII);
                }
                break;

            case LANGUAGE_FRENCH:
                if(resultIsNegative) {
                    mTranslationBox.setText("moins " + NumberConverterFrench.convert(integerFraction));
                }
                else {
                    mTranslationBox.setText(NumberConverterFrench.convert(integerFraction));
                }

                if (!decimal_fraction.equals("")) {
                    partII = NumberConverterFrenchPartII.convert(decimal_fraction);
                    mTranslationBox.append(partII);
                }

                break;


            case LANGUAGE_ARABIC:
                if(resultIsNegative) {
                    NumberConverterArabic arabic = new NumberConverterArabic(mRawResult.abs());
                    mTranslationBox.setText("ناقص " + arabic.ConvertToArabic());
                }
                else {
                    NumberConverterArabic arabic = new NumberConverterArabic(mRawResult);
                    mTranslationBox.setText(arabic.ConvertToArabic());
                }
                break;

            case LANGUAGE_PERSIAN:

                mTranslationBox.setText(new NumberConveterPersianPartI().convert(integerFraction));
                if (!decimal_fraction.equals("")) {
                    partII = NumberConverterPersianPartII.convert(decimal_fraction);
                    if (new BigDecimal(resultWithoutCommas).compareTo(BigDecimal.ONE) >= 0 ||new BigDecimal(resultWithoutCommas).compareTo(new BigDecimal(-1)) <= 0 ) {
                        mTranslationBox.append("ممیز " + partII);
                    }   else {
                        mTranslationBox.setText(partII);
                    }
                }
                if(resultIsNegative) {
                    mTranslationBox.setText("منفی " + mTranslationBox.getText());
                }
                break;
        }
    }

    private Boolean isOperator(Character inputChar){
        switch (inputChar) {
            case '+':
            case '−':
            case '÷':
            case '*':
            case 'x':
            case '\u00d7':
                return true;

        /*    case '(':
            case ')':*/
            default:
                return false;
        }
    }

    // Error - Fixing Methods

    private boolean preventCommonErrors(char currentChar){

        boolean hasError1 = false, hasError2 = false,hasError3 = false;
        // TODO change char to string
        if (getMExpressionString().length() == 0)
            hasError1 = fixFirstChar(currentChar);
        hasError2 = fixSuccessiveOperators(currentChar);


        if ((currentChar) == '.') {
            hasError3 = fixDoublePoints(currentChar);
        }
        return (hasError1 || hasError2 || hasError3);
    }
    private boolean fixFirstChar(char input) {
        if(getMExpressionString().length() == 0) {
            switch (input) {
                case '+':
                case '÷':
                case 'x':
                case '*':
                case '\u00d7':
                case ')':
                case '=':
                    return true;


            }
        }
        return false;
    }
    private boolean fixSuccessiveOperators(char input) {

        int lastIndex = getMExpressionString().length() - 1;

        // allow 2*-
        if ((getMExpressionString().length() > 1)) {
            if ((getMExpressionString().charAt(lastIndex) == '\u00d7' )||
                    ( getMExpressionString().charAt(lastIndex)== '÷'))
                if (input == '−') {
                    return false;
                }

            // deny 2++
            if (isOperator(input))
            {
                if (isOperator(getMExpressionString().charAt(lastIndex)))
                    return true;
            }
        }
        return false;
    }
    private boolean fixDoublePoints(char input){
        int legalStart = -1;

        for(int index = 0;index< getMExpressionString().length();index ++){
            if(!Character.isDigit(getMExpressionString().charAt(index))){
                if(getMExpressionString().charAt(index) != '.')
                    legalStart = index;
            }
        }
        int lastOccurrence ;
        lastOccurrence = getMExpressionString().lastIndexOf(".");
        //secondOccurrence = getMExpressionString().length()  ;
        if (lastOccurrence <= legalStart){
            return false;
        }
        return true;
    }
    private void deleteLastChar(){
        int lastIndex = getMExpressionString().length() - 1;
        if(isOperator(getMExpressionString().charAt(lastIndex)))
            deleteMExpressionStringAt(lastIndex);

    }




    private String evaluateResult(Expression exp) {

        String result ="";
        mRawResult = exp.evaluate();
//        mRawResult.round(new MathContext(6, RoundingMode.HALF_UP));
        DecimalFormat df = new DecimalFormat();

        df.setGroupingUsed(true);
        df.setGroupingSize(3);
        df.setMaximumFractionDigits(6);
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));

        result  =   df.format(mRawResult);
//        result = result.replaceAll("^-(?=0(.0*)?$)", "");

        return result;

    }


    public void playSound(int id) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        float maxVolume = (float) audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume;


        if (getVolumeFromPreference() == true) {
            volume =  maxVolume;
        } else {
            volume = 0;
        }

        // Is the sound mSoundPoolLoaded already?
        if (mSoundPoolLoaded) {
            mSoundPool.play(id, volume, volume, 1, 0, 0.99f);



        }
    }
    void prepareSoundStuff(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSoundPool =  createNewSoundPool();
        }else{
            mSoundPool =  createOldSoundPool();
        }
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                mSoundPoolLoaded = true;
            }
        });
        if (isRetroThemeSelected()) {
            numericButtonSoundID = mSoundPool.load(getApplicationContext(), R.raw.keypress_retro, 1);
            executeButtonSoundID = mSoundPool.load(getApplicationContext(), R.raw.equal_retro, 1);
            clearAllButtonSoundID = mSoundPool.load(getApplicationContext(), R.raw.clear_retro, 1);
            operatorsButtonSoundID = mSoundPool.load(getApplicationContext(), R.raw.operator_retro, 1);
            errorSoundID = mSoundPool.load(getApplicationContext(), R.raw.error_retro, 1);
        }else{
            numericButtonSoundID = mSoundPool.load(getApplicationContext(), R.raw.keypress, 1);
            executeButtonSoundID = mSoundPool.load(getApplicationContext(), R.raw.equal, 1);
            clearAllButtonSoundID = mSoundPool.load(getApplicationContext(), R.raw.clear, 1);
            operatorsButtonSoundID = mSoundPool.load(getApplicationContext(), R.raw.keypress, 1);
            errorSoundID = mSoundPool.load(getApplicationContext(), R.raw.error, 1);
        }
        backSpaceButtonSoundID = mSoundPool.load(getApplicationContext(), R.raw.backspace, 1);
        mHasVolumeSoundID = mSoundPool.load(getApplicationContext(), R.raw.backspace, 1);
        mAddStarSoundID = mSoundPool.load(getApplicationContext(), R.raw.fairy, 1);


    }

    public  int getClearSoundID(){
        return clearAllButtonSoundID;
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected SoundPool createNewSoundPool(){
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        SoundPool sounds = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
        return sounds;
    }

    @SuppressWarnings("deprecation")
    protected SoundPool createOldSoundPool(){
        SoundPool  sounds = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        return sounds;
    }

    void prepareAnimationStuff() {

        in_anim = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        out_anim_execute = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        out_anim_execute.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //revert here
//                resultTextView.setText(mResultToDisplay);
//                resultTextView.startAnimation(in_anim);
                mTextSwitcher.setCurrentText(mResultToDisplay);
                mTextSwitcher.startAnimation(in_anim);


//                Runnable runnable = new Runnable() {
//                    public void run() {
//                        sendLogMessage(getMExpressionString().toString(), mResultToDisplay, false, "");
//                        if(mLogFragment != null) {
//                            mLogFragment.scrollToLast();
//                        }
//                    }
//                };
//                Thread mythread = new Thread(runnable);
//                mythread.start();

                AsyncTask asyncTask = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        sendLogMessage(getMExpressionString().toString(), mResultToDisplay, false, "");
                        try {
                            Thread.sleep(600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        mLogFragment.scrollToLast();
                    }
                };
                asyncTask.execute();



            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        out_anim_execute.setDuration(100);

        out_anim_clear = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        out_anim_clear.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                resultTextView.setText(mResultToDisplay);
//                resultTextView.startAnimation(in_anim);
                mTextSwitcher.startAnimation(in_anim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        out_anim_clear.setDuration(100);
        prepareBlinkingErrorAnimation();
        prepareBlinkingAnimation();
    }
    void prepareBlinkingErrorAnimation(){
        mErrorBlink = new AlphaAnimation(0.0f, 1.0f);
        mErrorBlink.setDuration(100); //You can manage the time of the blink with this parameter
        mErrorBlink.setRepeatMode(Animation.REVERSE);
        mErrorBlink.setRepeatCount(4);
    }

    void prepareBlinkingAnimation(){
        mBlink = new AlphaAnimation(0.0f, 1.0f);
        mBlink.setDuration(100); //You can manage the time of the blink with this parameter
        mBlink.setRepeatMode(Animation.REVERSE);
        mBlink.setRepeatCount(0);

    }

    public void setMExpressionString(String input){
        mExpressionBuffer.replace(0, mExpressionBuffer.length(), input);
    }
    public StringBuilder getMExpressionString(){
        return mExpressionBuffer;
    }

    public void appendMExpressionString(CharSequence input){
        mExpressionBuffer.append(input);
    }

    public void deleteMExpressionStringAt(int index){
        mExpressionBuffer.deleteCharAt(index);
    }

    // Send an Intent with an action named "my-event".
    void sendLogMessage(String Operation, String Result, boolean starred, String tagText) {
        Intent intent = new Intent("LogIntent");
        // add data
        intent.putExtra("OPERATION", Operation );
        intent.putExtra("RESULT", Result);
        intent.putExtra("RESULT_NO_COMMA", Result.replace(",", ""));
        intent.putExtra("STARRED", starred);
        intent.putExtra("TAG", tagText);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        ContentValues cv = new ContentValues();
        cv.put(LogContract.LogEntry.COLUMN_RESULT,Result);
        cv.put(LogContract.LogEntry.COLUMN_RESULT_NO_COMMA,Result.replace(",",""));
        cv.put(LogContract.LogEntry.COLUMN_OPERATION,Operation);
        cv.put(LogContract.LogEntry.COLUMN_TAG,tagText);
        cv.put(LogContract.LogEntry.COLUMN_STARRED, 0);

        mLatestInsertedId = ContentUris.parseId(getContentResolver().insert(LogContract.LogEntry.CONTENT_URI,cv));
        setMExpressionString(mResultToDisplay.replace(",", ""));

    }


    public void sendClearButtonMessage(String value) {
        if(mLayoutState != LANDSCAPE_TABLET) {
            ViewPagerAdapter adapter = (ViewPagerAdapter) mViewPager.getAdapter();
            DialpadFragment dialpadFragment = (DialpadFragment)adapter.getItem(1);
            dialpadFragment.setClearButtonText(value);
        }else{
            //it means we're in tablet mode
            DialpadFragment dialpadFragment = (DialpadFragment)getSupportFragmentManager().findFragmentByTag("dialpad_fragment_tag");
            dialpadFragment.setClearButtonText(value);
        }

    }
    public void sendChangeFontThicknessMessage() {
        if(mLayoutState != LANDSCAPE_TABLET) {
            ViewPagerAdapter adapter = (ViewPagerAdapter) mViewPager.getAdapter();
            DialpadFragment dialpadFragment = (DialpadFragment)adapter.getItem(1);
            dialpadFragment.changeFontThickness();
        }else{
            //it means we're in tablet mode
            DialpadFragment dialpadFragment = (DialpadFragment)getSupportFragmentManager().findFragmentByTag("dialpad_fragment_tag");
            dialpadFragment.changeFontThickness();
        }

    }



    private boolean isNonDigit (String currentButtonValue){

        String[] nonDigitStrings = { "(", ")" , getResources().getString(R.string.point),
                getResources().getString(R.string.sin) + "(",
                getResources().getString(R.string.cos) + "(",
                getResources().getString(R.string.tan)+ "(",
                getResources().getString(R.string.cot) + "(",
                getResources().getString(R.string.csc) + "(",
                getResources().getString(R.string.sec)+"(",

                getResources().getString(R.string.asin) + "(" ,
                getResources().getString(R.string.acos) + "("  ,
                getResources().getString(R.string.atan) + "("  ,
                getResources().getString(R.string.acot) + "(" ,
                getResources().getString(R.string.acsc) + "(",
                getResources().getString(R.string.asec) + "(",

                getResources().getString(R.string.sinh) + "(",
                getResources().getString(R.string.cosh) + "(",
                getResources().getString(R.string.tanh) + "(",
                getResources().getString(R.string.coth) + "(",
                getResources().getString(R.string.sech) + "(",
                getResources().getString(R.string.csch) + "(",

                getResources().getString(R.string.asinh) + "(" ,
                getResources().getString(R.string.acosh) + "("  ,
                getResources().getString(R.string.atanh) + "("  ,
                getResources().getString(R.string.acoth) + "(" ,
                getResources().getString(R.string.asech) + "(",
                getResources().getString(R.string.acsch) + "(",

                getResources().getString(R.string.ln_tag), getResources().getString(R.string.power), getResources().getString(R.string.log_tag), getResources().getString(R.string.tenpowerx) + "(","E",
                getResources().getString(R.string.epowerx) + "(",
                getResources().getString(R.string.sqrt), "+","−","-","÷","×"};


        List<String> myList = Arrays.asList(nonDigitStrings);
        if(myList.contains(currentButtonValue)) return true;

        return false;
    }


    void setFontForComponent(String key, int value) {

        SharedPreferences appPreferences = getSharedPreferences("typography", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    Typeface getFontForComponent(String key) {

        SharedPreferences appPreferences =getSharedPreferences("typography", Context.MODE_PRIVATE);
        int outputFont = 0 ;

        switch (key) {
            case "DIALPAD_FONT":
                outputFont = appPreferences.getInt(key, FONT_ROBOTO_THIN);
                break;
            case "SCIENTIFIC_FONT":
                outputFont = appPreferences.getInt(key, FONT_ROBOTO_LIGHT);
                break;
            case "RESULT_FONT":
                outputFont = appPreferences.getInt(key, FONT_ROBOTO_THIN);
                break;
            case "TRANSLATION_NUMERIC_FONT":
                outputFont = appPreferences.getInt(key, FONT_ROBOTO_LIGHT);
                break;
            case "TRANSLATION_LITERAL_FONT":
                outputFont = appPreferences.getInt(key, FONT_MITRA);
                break;

        }

        switch (outputFont) {
            case FONT_ROBOTO_THIN:
                return mRobotoThin;
            case FONT_ROBOTO_LIGHT:
                return mRobotoLight;
            case FONT_ROBOTO_REGULAR:
                return mRobotoRegular;
            case FONT_DIGITAL_7:
                return mDigital_7;
            case FONT_YEKAN:
                return mPhalls;
            case FONT_MITRA:
                return mMitra;
            case FONT_MAJALLA:
                return mMajalla;
        }

        return mRobotoThin;
    }


    public void setmDefaultPage(byte _defaultPage) {
        this.mDefaultPage = (byte) (_defaultPage);
    }

    void paintIconColors(){
    }

    public void switchTheme() {
//        SharedPreferences appPreferences = getSharedPreferences("typography", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = appPreferences.edit();

        //switching from modern theme to clasic theme
        if (isRetroThemeSelected()) {

            switch (getTranslationLanguage()) {
                case LANGUAGE_PERSIAN:
                case LANGUAGE_ARABIC:
                    setFontForComponent("TRANSLATION_LITERAL_FONT", FONT_YEKAN);
                    break;
                default:
                    setFontForComponent("TRANSLATION_LITERAL_FONT", FONT_DIGITAL_7);
            }
            setFontForComponent("TRANSLATION_NUMERIC_FONT", FONT_DIGITAL_7);
            setFontForComponent("RESULT_FONT", FONT_DIGITAL_7);
            setFontForComponent("DIALPAD_FONT", FONT_ROBOTO_LIGHT);
        } else {
            switch (getTranslationLanguage()) {
                case LANGUAGE_PERSIAN:
                    setFontForComponent("TRANSLATION_LITERAL_FONT", FONT_MITRA);
                    break;
                case LANGUAGE_ARABIC:
                    setFontForComponent("TRANSLATION_LITERAL_FONT", FONT_MAJALLA);
                    break;
                default:
                    setFontForComponent("TRANSLATION_LITERAL_FONT", FONT_ROBOTO_THIN);
            }
            setFontForComponent("TRANSLATION_NUMERIC_FONT", FONT_ROBOTO_LIGHT);
            setFontForComponent("RESULT_FONT", FONT_ROBOTO_THIN);
            setFontForComponent("DIALPAD_FONT", FONT_ROBOTO_THIN);
        }
//        setFontForComponent("SCIENTIFIC_FONT", FONT_ROBOTO_LIGHT);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){


            case R.id.switch_deg_rad:

                boolean on = ((Switch) v).isChecked();

                if (on) {
                    //result in Radian
                    setAngleMode(true);
                } else {
                    //result in Degress
                    setAngleMode(false);

                }
                break;

            case R.id.favorites_list:

                FragmentManager fm = getSupportFragmentManager();
                FavoritesFragment favoritesDialog = new FavoritesFragment();
                favoritesDialog.show(fm, "fragment_favorites");
                break;

            case R.id.btn_add_star: {

                String selection = LogContract.LogEntry._ID + "=?";
                Cursor cursor = getContentResolver().query(LogContract.LogEntry.CONTENT_URI, null, selection, new String[]{Long.toString(mLatestInsertedId)}, null);
                if (cursor.moveToFirst()) {
                    int currentStarredStatus = cursor.getInt(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_STARRED));
                    if (currentStarredStatus == 0) {
                        ContentValues values = new ContentValues();
                        values.put(LogContract.LogEntry.COLUMN_STARRED, 1);
                        getContentResolver().update(LogContract.LogEntry.CONTENT_URI, values, selection, new String[]{Long.toString(mLatestInsertedId)});
                    }
                }
                cursor.close();
                playSound(mAddStarSoundID);
                setClipView(mAddStars, false);
                mAddStars.setText(getResources().getString(R.string.star_icon));
                mAddStars.setTextColor(Color.parseColor("#FFC107"));
                mAddStars.animate()
                        .translationY(200)
                        .scaleX(0.5f)
                        .scaleY(0.5f)
                        .alpha(0)
                        .rotation(180)
                        .setDuration(1000);
            }

            break;

            case R.id.add_label: {
                final String  selection = LogContract.LogEntry._ID + "=?";
                Cursor cursor = getContentResolver().query(LogContract.LogEntry.CONTENT_URI, null, selection, new String[]{Long.toString(mLatestInsertedId)}, null);
                if (cursor.moveToFirst()) {
                    String currentLabel = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_TAG));
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                    builder.setTitle(getString(R.string.farsi_label));

                    // Set up the input

                    final EditText input = new EditText(this);
                    final InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);

                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text

                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setCancelable(false);
                    input.setText(currentLabel);
                    input.requestFocus();
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    // Set up the buttons

                    builder.setPositiveButton(getString(R.string.farsi_ok), new DialogInterface.OnClickListener() {


                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newLabel = input.getText().toString();
                            ContentValues values = new ContentValues();
                            values.put(LogContract.LogEntry.COLUMN_TAG, newLabel);
                            getContentResolver().update(
                                    LogContract.LogEntry.CONTENT_URI,
                                    values,
                                    selection,
                                    new String[]{Long.toString(mLatestInsertedId)}
                            );
                            inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);

                        }
                    });
                    builder.setNegativeButton(getString(R.string.farsi_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
                            dialog.cancel();

                        }
                    });
                    final android.support.v7.app.AlertDialog dialog = builder.create();
                    dialog.show();
                    EditText.OnKeyListener keyListener = new EditText.OnKeyListener() {
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                switch (keyCode) {
                                    case KeyEvent.KEYCODE_DPAD_CENTER:
                                    case KeyEvent.KEYCODE_ENTER:
                                        dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).performClick();
                                        return true;
                                    default:
                                        break;
                                }
                            }
                            return false;
                        }
                    };
                    input.setOnKeyListener(keyListener);

                }

                cursor.close();



            }

            break;


            case R.id.currency_list:
                CurrencyUseFragment currencyDialog = new CurrencyUseFragment();
                currencyDialog.show(getSupportFragmentManager() , "fragment_currency_use");
                break;


            case R.id.buttonSettings:
                CustomDialogClass cdc = new CustomDialogClass(this , android.R.style.Theme_Holo_Light_Dialog_MinWidth);
                cdc.show();
                break;

            case R.id.buttonHamburgerMenu:
                mDrawer.openDrawer();
                break;

            case R.id.buttonMute:
                reverseVolume();
                if (getVolumeFromPreference() == true) {
                    ((Button)v).setText(getResources().getText(R.string.volume_high));
                }else {
                    ((Button)v).setText(getResources().getText(R.string.volume_off));
                }
                break;




            case R.id.buttonColors:

                Intent myIntent = new Intent(MainActivity.this, ColorPickerActivity.class);
                myIntent.putExtra("isPremium",getPremiumPreference());
                myIntent.putExtra("isRetroTheme",isRetroThemeSelected());
                myIntent.putExtra("accentColor",getAccentColorCode());
                myIntent.putExtra("keyPadColor",getKeypadBackgroundColorCode());

                MainActivity.this.startActivityForResult(myIntent, 2);
        }
        return;
    }

    public boolean getPremiumPreference(){
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("purchases", Context.MODE_PRIVATE);
        return  appPreferences.getBoolean("isPremium",false);
    }

    public static void setClipView(View view, boolean clip) {
        if (view != null) {
            ViewParent parent = view.getParent();
            if(parent instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view.getParent();
                viewGroup.setClipChildren(clip);
                viewGroup.setClipToPadding(clip);
                setClipView(viewGroup, clip);

            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        boolean on = buttonView.isChecked();

        if (on) {
            //result in Radian
            setAngleMode(true);
        } else {
            //result in Degress
            setAngleMode(false);

        }
    }

    public void refreshFonts() {

        resultTextView.setTypeface(getFontForComponent("RESULT_FONT"));
        mTranslationBoxLetterFont = getFontForComponent("TRANSLATION_LITERAL_FONT");
        mTranslationBoxNumericFont = getFontForComponent("TRANSLATION_NUMERIC_FONT");
        if(mJustPressedExecuteButton){
            mTranslationBox.setTypeface(mTranslationBoxLetterFont);
        }else{
            mTranslationBox.setTypeface(mTranslationBoxNumericFont);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == resultCode ){
            if(data.getBooleanExtra("switchTheme",true)){
                switchTheme();
                Log.d(TAG_recreate, "Acrivity Right before recreate");
                recreate();
            }
        }
    }
}
