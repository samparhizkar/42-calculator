package com.sepidsa.fortytwocalculator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class CustomDialogClass extends Dialog implements View.OnClickListener {

    private final Typeface mRobotoLight;
    private final Typeface mRobotoRegular;
    private final Typeface mYekan;
    public Button sendFeedBack;
    Context mContext;
    Typeface  mRobotoThin ;
    Typeface mDastnevis ;
    Typeface mMitra ;
    private EditText feedbackEditText;
    private Button dialpad_textSize_Button ;
    private static final int DIALPAD_FONT_ROBOTO_THIN = 0;
    private static final int DIALPAD_FONT_ROBOTO_LIGHT = 1;
    private static final int DIALPAD_FONT_ROBOTO_REGULAR = 2;
    private static final int PERSIAN_TRANSLATION_FONT_MITRA = 5;
    private static final int PERSIAN_TRANSLATION_FONT_DASTNEVIS = 6;
    private AutoResizeTextView fortyTwoSample;
    private TextView  fortyTwoSampleTranslation;


    /**
     * Create a Dialog window that uses a custom dialog style.
     *
     * @param context The Context in which the Dialog should run. In particular, it
     *                uses the window manager and theme from this context to
     *                present its UI.
     * @param theme   A style resource describing the theme to use for the
     *                window. See <a href="{@docRoot}guide/topics/resources/available-resources.html#stylesandthemes">Style
     *                and Theme Resources</a> for more information about defining and using
     *                styles.  This theme is applied on top of the current theme in
     */
    public CustomDialogClass(Context context, int theme) {
        super(context, theme);
        mRobotoThin = Typeface.createFromAsset(context.getAssets(), "roboto_thin.ttf");
        mRobotoLight = Typeface.createFromAsset(context.getAssets(), "roboto_light.ttf");
        mRobotoRegular =  Typeface.createFromAsset(context.getAssets(), "roboto_regular.ttf");
        mMitra = Typeface.createFromAsset(context.getAssets(), "mitra.ttf");
        mYekan = Typeface.createFromAsset(context.getAssets(), "yekan.ttf");
        mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        Button contact_us_Button =  (Button) findViewById(R.id.btn_CHANGE_TRANSLATION_FONT);
        contact_us_Button.setOnClickListener(this);
        contact_us_Button.setTypeface(mYekan);
        TextView app_version = (TextView)findViewById(R.id.scientific_mode_textview);
        try {
            app_version.setText("ver " + ((MainActivity) mContext).getVersion());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ;
        dialpad_textSize_Button =  (Button) findViewById(R.id.dialpad_text_size);
        dialpad_textSize_Button.setTypeface(mYekan);
        dialpad_textSize_Button.setOnClickListener(this);

        Button give_Stars = (Button) findViewById(R.id.give_stars) ;
        give_Stars.setOnClickListener(this);
        give_Stars.setTypeface(mYekan);

        fortyTwoSample = (AutoResizeTextView) findViewById(R.id.fortyTwoSample) ;
        fortyTwoSample.setOnClickListener(this);
        fortyTwoSampleTranslation = (TextView) findViewById(R.id.fortyTwoSampleTranslation) ;
        set_dialpad_textSize_Button_typeface();
        set_persian_translation_Button_typeface();
        refreshSampleTypeface(((MainActivity)mContext).getTranslationLanguage());
         Button tips = (Button) findViewById(R.id.language_selection) ;
        tips.setOnClickListener(this);
        tips.setTypeface(mYekan);

    }


    private void set_dialpad_textSize_Button_typeface() {
        SharedPreferences appPreferences = mContext.getSharedPreferences("typography", Context.MODE_PRIVATE);
        switch (appPreferences.getInt("DIALPAD_FONT",DIALPAD_FONT_ROBOTO_THIN)) {
            case 0 :
                fortyTwoSample.setTypeface(mRobotoThin);
                break;

            case 1 :
                fortyTwoSample.setTypeface(mRobotoLight);
                break;
            case 2 :
                fortyTwoSample.setTypeface(mRobotoRegular);
                break;
        }
    }

    private void set_persian_translation_Button_typeface() {
        SharedPreferences appPreferences = mContext.getSharedPreferences("typography", Context.MODE_PRIVATE);
        switch (appPreferences.getInt("PERSIAN_FONT_PREFERENCE",PERSIAN_TRANSLATION_FONT_MITRA)) {
            case 0 :
                fortyTwoSampleTranslation.setTypeface(mMitra);
                break;
            case 1 :
                fortyTwoSampleTranslation.setTypeface(mDastnevis);
                break;
            default:
                fortyTwoSampleTranslation.setTypeface(mMitra);
                break;
        }

    }

    private void set_persian_translation_Button_language() {
        SharedPreferences appPreferences = mContext.getSharedPreferences("typography", Context.MODE_PRIVATE);
        switch (appPreferences.getInt("PERSIAN_FONT_PREFERENCE",PERSIAN_TRANSLATION_FONT_MITRA)) {
            case 0 :
                fortyTwoSampleTranslation.setTypeface(mMitra);
                break;
            case 1 :
                fortyTwoSampleTranslation.setTypeface(mDastnevis);
                break;
            default:
                fortyTwoSampleTranslation.setTypeface(mMitra);
                break;
        }

    }


    // Send an Intent with an action named "my-event".
    void sendChangeDialpadTypefaceMessage(int font) {
        ((MainActivity) mContext).setFontForComponent("DIALPAD_FONT",font);
        Intent intent = new Intent("themeIntent");
        intent.putExtra("message", "changeDialpadFont");
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fortyTwoSample:
            case R.id.give_stars:

                Intent giveStarsIntent = new Intent(
                        Intent.ACTION_EDIT,
                        Uri.parse("http://cafebazaar.ir/app/com.sepidsa.fortytwocalculator/?l=fa"));
                mContext.startActivity(giveStarsIntent);
                dismiss();
                break;

            case R.id.language_selection:

                showSpinner();
                break;

            case R.id.dialpad_text_size:
                SharedPreferences fontSizePreference = mContext.getSharedPreferences("typography", Context.MODE_PRIVATE);
                SharedPreferences.Editor fontSizeeditor = fontSizePreference.edit();
                switch (fontSizePreference.getInt("DIALPAD_FONT", DIALPAD_FONT_ROBOTO_THIN)) {
                    case 2:
                        fontSizeeditor.putInt("DIALPAD_FONT", DIALPAD_FONT_ROBOTO_THIN);
                        fontSizeeditor.apply();
                        fortyTwoSample.setTypeface(mRobotoThin);
                        sendChangeDialpadTypefaceMessage(0);
                        break;

                    case 0:
                        fontSizeeditor.putInt("DIALPAD_FONT", DIALPAD_FONT_ROBOTO_LIGHT);
                        fontSizeeditor.apply();
                        fortyTwoSample.setTypeface(mRobotoLight);
                        sendChangeDialpadTypefaceMessage(1);
                        break;

                    case 1:
                        fontSizeeditor.putInt("DIALPAD_FONT", DIALPAD_FONT_ROBOTO_REGULAR);
                        fontSizeeditor.apply();
                        fortyTwoSample.setTypeface(mRobotoRegular);

                        sendChangeDialpadTypefaceMessage(2);
                        break;
                }
                set_dialpad_textSize_Button_typeface();
                ((MainActivity) mContext).sendChangeFontThicknessMessage();
                break;


            case R.id.btn_CHANGE_TRANSLATION_FONT:

                try{
                    Intent myIntent = new Intent(mContext, PremiumShowcasePagerActivity.class);
                    mContext.startActivity(myIntent);
                }
                catch (Exception e ){
                    Toast.makeText(mContext,"مشکل در اتصال به بازار",Toast.LENGTH_LONG);

                }

                dismiss();
                break;

        }
    }

    public void showSpinner() {

        AlertDialog.Builder b = new AlertDialog.Builder(mContext);
// todo google play edition
        String[] options = {"فارسی","انگلیسی","فرانسه","عربی"};
        b.setTitle("زبان نتیجه محاسبه" );
        b.setSingleChoiceItems(options, -1,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences appPreferences = mContext.getApplicationContext().getSharedPreferences("LanguagePreference", ((MainActivity) mContext).MODE_PRIVATE);
                        SharedPreferences.Editor editor = appPreferences.edit();

                        dialog.dismiss();
                        switch (which) {
                            case 0: {
                                editor.putInt("LANGUAGE", MainActivity.LANGUAGE_PERSIAN);
                                refreshSampleTypeface(MainActivity.LANGUAGE_PERSIAN);

                                break;
                            }
                            case 1: {
                                editor.putInt("LANGUAGE",  MainActivity.LANGUAGE_ENGLISH);
                                refreshSampleTypeface(MainActivity.LANGUAGE_ENGLISH);
                                break;

                            }
                            case 2: {
                                editor.putInt("LANGUAGE", MainActivity.LANGUAGE_FRENCH);
                                refreshSampleTypeface(MainActivity.LANGUAGE_FRENCH);
                                break;

                            }
                            case 3: {
                                editor.putInt("LANGUAGE",  MainActivity.LANGUAGE_ARABIC);
                                refreshSampleTypeface(MainActivity.LANGUAGE_ARABIC);
                                break;
                            }

                        }
                        editor.commit();

                        fortyTwoSampleTranslation.setTypeface(((MainActivity) mContext).getFontForComponent("TRANSLATION_LITERAL_FONT"));
//                        fortyTwoSampleTranslation.setTextSize(60);


                        ((MainActivity) mContext).refreshFonts();
                        if (((MainActivity) mContext).mDecimal_fraction != null) {
                            if (((MainActivity) mContext).mJustPressedExecuteButton) {
                                ((MainActivity) mContext).displayTranslation(true);
                            } else {
                                ((MainActivity) mContext).mTranslationBox.startAnimation(((MainActivity) mContext).mBlink);
                            }
                        }

                    }


                }
        );
        b.show();
    }

    void refreshSampleTypeface(int language){
        switch (language){


            case MainActivity.LANGUAGE_PERSIAN:
                fortyTwoSampleTranslation.setText(mContext.getString(R.string.persian_42));

                if(((MainActivity) mContext).isRetroThemeSelected()){
                    ((MainActivity) mContext).setFontForComponent("TRANSLATION_LITERAL_FONT", ((MainActivity) mContext).FONT_YEKAN);
                }else {
                    ((MainActivity) mContext).setFontForComponent("TRANSLATION_LITERAL_FONT", ((MainActivity) mContext).FONT_MITRA);
                }
                break;

            case MainActivity.LANGUAGE_ENGLISH:
                fortyTwoSampleTranslation.setText(mContext.getString(R.string.english_42));

                if(((MainActivity) mContext).isRetroThemeSelected()){
                    ((MainActivity) mContext).setFontForComponent("TRANSLATION_LITERAL_FONT", ((MainActivity) mContext).FONT_DIGITAL_7);

                }else {
                    ((MainActivity) mContext).setFontForComponent("TRANSLATION_LITERAL_FONT", ((MainActivity) mContext).FONT_ROBOTO_THIN);
                }
                break;

            case MainActivity.LANGUAGE_FRENCH:
                fortyTwoSampleTranslation.setText(mContext.getString(R.string.french_42));

                if(((MainActivity) mContext).isRetroThemeSelected()){
                    ((MainActivity) mContext).setFontForComponent("TRANSLATION_LITERAL_FONT", ((MainActivity) mContext).FONT_DIGITAL_7);

                }else {
                    ((MainActivity) mContext).setFontForComponent("TRANSLATION_LITERAL_FONT", ((MainActivity) mContext).FONT_ROBOTO_THIN);
                }
                break;
            case MainActivity.LANGUAGE_ARABIC:
                ((MainActivity) mContext).setFontForComponent("TRANSLATION_LITERAL_FONT", ((MainActivity) mContext).FONT_MAJALLA);
                fortyTwoSampleTranslation.setText(mContext.getString(R.string.arabic_42));
                break;


        }
    }

}


