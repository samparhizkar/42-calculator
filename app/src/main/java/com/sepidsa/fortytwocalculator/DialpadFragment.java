/**
 *
 */
package com.sepidsa.fortytwocalculator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

//import android.support.annotation.Nullable;


/**
 * @author Ehsan
 *
 */
public class DialpadFragment extends android.support.v4.app.Fragment implements OnClickListener,  CompoundButton.OnCheckedChangeListener {

    private boolean arcIsOn = false;
    String TAG = "recreate";
    View mView;
    private Typeface defaultFont;

    private BroadcastReceiver mThemeChangedReciever;


    private int[] idList;
    boolean mIsRetroOn = false ;
    private Typeface scientificFont;
    static float scientific_toggle_textSize = 18f;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG,"Fragment OnCreateView");
        mIsRetroOn = ((MainActivity)getActivity()).isRetroThemeSelected();
        if(mIsRetroOn) {
            mView = inflater.inflate(R.layout.fragment_dialpad_retro, container, false);
        }else {
            mView = inflater.inflate(R.layout.fragment_dialpad_flat, container, false);

        }

        // Set the listener for all the gray_buttons
        idList = getAllButtonsID();

        defaultFont = ((MainActivity)getActivity()).getFontForComponent("DIALPAD_FONT");
        scientificFont = ((MainActivity)getActivity()).getFontForComponent("SCIENTIFIC_FONT");






        for(int id : getDialpadButtonsID()) {
            View v = mView.findViewById(id);
            if (v != null) {
                v.setOnClickListener(this);
                if( v instanceof Button){
                    if(mIsRetroOn){
                        ((Button) v).setTypeface(scientificFont);
                    }else{
                        ((Button) v).setTypeface(defaultFont);
                    }
                }
            }
        }

        for(int id : getScientificButtonsID()) {
            View v = mView.findViewById(id);
            if (v != null) {
                v.setOnClickListener(this);
                if( v instanceof Button){
                    ((Button) v).setTypeface(scientificFont);
                }
            }
        }

        if(mView.findViewById(R.id.switch_deg_rad) != null) {
            ((ToggleButton) (mView.findViewById(R.id.switch_deg_rad))).setChecked(((MainActivity) getActivity()).getAngleMode());
            ((ToggleButton) (mView.findViewById(R.id.switch_deg_rad))).setOnCheckedChangeListener(this);
            mView.findViewById(R.id.switch_deg_rad).setOnClickListener(this);


        }

        if(mView.findViewById(R.id.switch_deg_rad) != null) {
            ((ToggleButton) (mView.findViewById(R.id.switch_deg_rad))).setChecked(((MainActivity) getActivity()).getAngleMode());
            ((ToggleButton) (mView.findViewById(R.id.switch_deg_rad))).setOnCheckedChangeListener(this);
            if(mIsRetroOn){
                ((Button) (mView.findViewById(R.id.buttonConstant))).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "yekan.ttf"));

            }else {
                ((Button) (mView.findViewById(R.id.buttonConstant))).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "lotus.ttf"));
            }
            (mView.findViewById(R.id.switch_deg_rad)).setOnClickListener(this);
            (mView.findViewById(R.id.buttonConstant)).setOnClickListener(this);
            ((ToggleButton) (mView.findViewById(R.id.switch_deg_rad))).setTextSize(scientific_toggle_textSize);
            ((ToggleButton) (mView.findViewById(R.id.buttonInverse))).setTextSize(scientific_toggle_textSize);
            ((ToggleButton) (mView.findViewById(R.id.buttonARC))).setTextSize(scientific_toggle_textSize);
        }

        (mView.findViewById(R.id.buttonClear)).setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                ((MainActivity)getActivity()).aButtonIsPressed("C");
                setClearButtonText(getResources().getString(R.string.clear));

                return true;
            }
        });

        (mView.findViewById(R.id.buttonEquals)).setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                ((MainActivity)getActivity()).aButtonIsPressed("MR");

                return true;
            }
        });

        (mView.findViewById(R.id.buttonTimes)).setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                ((MainActivity)getActivity()).aButtonIsPressed("MC");

                return true;
            }
        });


        (mView.findViewById(R.id.buttonMinus)).setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                ((MainActivity)getActivity()).aButtonIsPressed("M-");
                setClearButtonText(getResources().getString(R.string.clear));

                return true;
            }
        });
        (mView.findViewById(R.id.buttonPlus)).setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                ((MainActivity)getActivity()).aButtonIsPressed("M+");

                return true;
            }
        });



//        mThemeChangedReciever = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
////                if (isAdded()) {
//                // Extract data included in the Intent
//                String message = intent.getStringExtra("message");
//                if (!((MainActivity) getActivity()).isRetroThemeSelected()) {
//
//                    switch (message) {
//                        case "changeDialpadFont":
//                            defaultFont = ((MainActivity) getActivity()).getFontForComponent("DIALPAD_FONT");
//                            for (int id : idList) {
//                                View v = mView.findViewById(id);
//                                if (v != null && (v instanceof Button)) {
//                                    if(!mIsRetroOn)
//                                    ((Button) v).setTypeface(defaultFont);
//                                }
//                            }
//                            refreshCButtonTypeface();
//                            break;
//
//                    }
//                }
//            }
////            }
//        };

        return mView;
    }


    private void applyArc(boolean isArc) {
        if(isArc) {
            ((Button) mView.findViewById(R.id.buttonSinus)).setText("a"+ ((Button) mView.findViewById(R.id.buttonSinus)).getText());
            ((Button) mView.findViewById(R.id.buttonCosinus)).setText("a"+ ((Button) mView.findViewById(R.id.buttonCosinus)).getText());
            ((Button) mView.findViewById(R.id.buttonTan)).setText("a"+ ((Button) mView.findViewById(R.id.buttonTan)).getText());

            ((Button) mView.findViewById(R.id.buttonSinusH)).setText("a"+ ((Button) mView.findViewById(R.id.buttonSinusH)).getText());
            ((Button) mView.findViewById(R.id.buttonCosinusH)).setText("a"+ ((Button) mView.findViewById(R.id.buttonCosinusH)).getText());
            ((Button) mView.findViewById(R.id.buttonTanH)).setText("a"+ ((Button) mView.findViewById(R.id.buttonTanH)).getText());
        }else {
            ((Button) mView.findViewById(R.id.buttonSinus)).setText(((Button) mView.findViewById(R.id.buttonSinus)).getText().toString().substring(1));
            ((Button) mView.findViewById(R.id.buttonCosinus)).setText(((Button) mView.findViewById(R.id.buttonCosinus)).getText().toString().substring(1));
            ((Button) mView.findViewById(R.id.buttonTan)).setText(((Button) mView.findViewById(R.id.buttonTan)).getText().toString().substring(1));

            ((Button) mView.findViewById(R.id.buttonSinusH)).setText(((Button) mView.findViewById(R.id.buttonSinusH)).getText().toString().substring(1));
            ((Button) mView.findViewById(R.id.buttonCosinusH)).setText(((Button) mView.findViewById(R.id.buttonCosinusH)).getText().toString().substring(1));
            ((Button) mView.findViewById(R.id.buttonTanH)).setText(((Button) mView.findViewById(R.id.buttonTanH)).getText().toString().substring(1));
        }
    }

    private void applyInverse(boolean isOn) {

        if(isOn){
            (mView.findViewById(R.id.buttonpie)).setTag(getResources().getString(R.string.neper));
            ((Button) mView.findViewById(R.id.buttonpie)).setText(getResources().getString(R.string.neper));

            if(arcIsOn) {
                ((Button) mView.findViewById(R.id.buttonSinus)).setText("a"+ getResources().getString(R.string.csc));
                ((Button) mView.findViewById(R.id.buttonCosinus)).setText("a"+ getResources().getString(R.string.sec));
                ((Button) mView.findViewById(R.id.buttonTan)).setText("a"+ getResources().getString(R.string.taninverse));

                ((Button) mView.findViewById(R.id.buttonSinusH)).setText("a"+ getResources().getString(R.string.csch));
                ((Button) mView.findViewById(R.id.buttonCosinusH)).setText("a"+ getResources().getString(R.string.sech));
                ((Button) mView.findViewById(R.id.buttonTanH)).setText("a"+ getResources().getString(R.string.coth));
            }else{

                ((Button) mView.findViewById(R.id.buttonSinus)).setText(getResources().getString(R.string.csc));
                ((Button) mView.findViewById(R.id.buttonCosinus)).setText(getResources().getString(R.string.sec));
                ((Button) mView.findViewById(R.id.buttonTan)).setText(getResources().getString(R.string.taninverse));

                ((Button) mView.findViewById(R.id.buttonSinusH)).setText(getResources().getString(R.string.csch));
                ((Button) mView.findViewById(R.id.buttonCosinusH)).setText(getResources().getString(R.string.sech));
                ((Button) mView.findViewById(R.id.buttonTanH)).setText(getResources().getString(R.string.coth));
            }
        }else {
            ( mView.findViewById(R.id.buttonpie)).setTag(getResources().getString(R.string.Pi));
            ((Button) mView.findViewById(R.id.buttonpie)).setText(getResources().getString(R.string.Pi));


            if(arcIsOn) {
                ((Button) mView.findViewById(R.id.buttonSinus)).setText("a"+ getResources().getString(R.string.sin));
                ((Button) mView.findViewById(R.id.buttonCosinus)).setText("a"+ getResources().getString(R.string.cos));
                ((Button) mView.findViewById(R.id.buttonTan)).setText("a"+ getResources().getString(R.string.tan));

                ((Button) mView.findViewById(R.id.buttonSinusH)).setText("a"+ getResources().getString(R.string.sinh));
                ((Button) mView.findViewById(R.id.buttonCosinusH)).setText("a"+ getResources().getString(R.string.cosh));
                ((Button) mView.findViewById(R.id.buttonTanH)).setText("a"+ getResources().getString(R.string.tanh));
            }else{
                ((Button) mView.findViewById(R.id.buttonSinus)).setText(getResources().getString(R.string.sin));
                ((Button) mView.findViewById(R.id.buttonCosinus)).setText(getResources().getString(R.string.cos));
                ((Button) mView.findViewById(R.id.buttonTan)).setText(getResources().getString(R.string.tan));

                ((Button) mView.findViewById(R.id.buttonSinusH)).setText(getResources().getString(R.string.sinh));
                ((Button) mView.findViewById(R.id.buttonCosinusH)).setText(getResources().getString(R.string.cosh));
                ((Button) mView.findViewById(R.id.buttonTanH)).setText(getResources().getString(R.string.tanh));
            }

        }

    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Fragment onCreate");




    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
//        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(mThemeChangedReciever, new IntentFilter("themeIntent"));

        ((MainActivity)getActivity()).checkCLRButtonSendIntent();
        if(!mIsRetroOn){
            redrawKeypadInFlatTheme();
        }
        //If no retro theme is selected then apply flat theme colors to keys



        Log.d(TAG, "Fragment onStart");

    }


    public void changeFontThickness() {
        defaultFont = ((MainActivity) getActivity()).getFontForComponent("DIALPAD_FONT");
        for (int id : idList) {
            View v = mView.findViewById(id);
            if (v != null && (v instanceof Button)) {
                if(!mIsRetroOn)
                    ((Button) v).setTypeface(defaultFont);
            }
        }
        refreshCButtonTypeface();
    }

    @Override
    public void onResume() {
        super.onResume();

        changeFontThickness();

        Log.d(TAG, "Fragment onResume ");



    }

    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.buttonInverse:
                if(   ((ToggleButton)view).isChecked() == true){
                    applyInverse(true);
                }else {
                    applyInverse(false);

                }

                break;


            case R.id.switch_deg_rad:

                boolean on = ((ToggleButton) view).isChecked();
                if (on) {
                    //result in Degree
                    ((MainActivity)getActivity()).  setAngleMode(true);
                } else {
                    //result in Radian
                    ((MainActivity)getActivity()). setAngleMode(false);

                }
                break;

            case R.id.buttonARC:
                if(   ((ToggleButton)view).isChecked() == true){
                    arcIsOn = true;
                    applyArc(true);
                }else {
                    arcIsOn = false;
                    applyArc(false);

                }
                break;

            case R.id.buttonConstant: {

                FragmentManager fm = getActivity().getSupportFragmentManager();
                ConstantUseFragment constantUseDialog = new ConstantUseFragment();
                constantUseDialog.show(fm, "fragment_constant_use");

            }
            break;

            default:
                if(view.getTag().toString().equals( "trigonomic")){
                    ((MainActivity) getActivity()).aButtonIsPressed(((Button)view).getText().toString()+"(");

                }else {
                    ((MainActivity) getActivity()).aButtonIsPressed(((Button)view).getTag().toString());
                }        }

    }



    int[] getAllButtonsID(){
        int [] buttonsPointerArray;

        buttonsPointerArray = new int[]{R.id.button9, R.id.button8, R.id.button7,
                R.id.button6, R.id.button5, R.id.button4, R.id.button1, R.id.button2, R.id.button3, R.id.button0,
                R.id.buttonPlus, R.id.buttonMinus, R.id.buttonDevide, R.id.buttonTimes, R.id.buttonEquals, R.id.buttonClear, R.id.buttonPoint
                , R.id.openParen, R.id.closeParen, R.id.buttonPercent,
                R.id.buttonSinus, R.id.buttonCosinus, R.id.buttonTan, R.id.buttonln, R.id.buttonlog, R.id.buttonlog,  R.id.buttonpie, R.id.buttonEXP, R.id.buttonpower,
                R.id.buttonrad , R.id.buttonfact, R.id.switch_deg_rad, R.id.buttonPow2 , R.id.buttonPow3 ,R.id.buttonInverse , R.id.buttonSinusH , R.id.buttonCosinusH , R.id.buttonTanH , R.id.buttonRandom
                , R.id.buttonConstant , R.id.buttonARC


        };

        return buttonsPointerArray;

    }
    int[] getOperatorButtonsID(){
        int [] buttonsPointerArray;

        buttonsPointerArray = new int[]{
                R.id.buttonPlus, R.id.buttonMinus, R.id.buttonDevide, R.id.buttonTimes
                , R.id.openParen, R.id.closeParen, R.id.buttonPercent
        };
        return buttonsPointerArray;

    }

    int[] getNonAccentButtonsID(){
        int [] buttonsPointerArray;

        buttonsPointerArray = new int[]{R.id.button9, R.id.button8, R.id.button7,
                R.id.button6, R.id.button5, R.id.button4, R.id.button1, R.id.button2, R.id.button3, R.id.button0,
                R.id.buttonPoint,


                R.id.buttonSinus, R.id.buttonCosinus, R.id.buttonTan, R.id.buttonln, R.id.buttonlog, R.id.buttonlog,  R.id.buttonpie, R.id.buttonEXP, R.id.buttonpower,
                R.id.buttonrad , R.id.buttonfact, R.id.switch_deg_rad, R.id.buttonPow2 , R.id.buttonPow3 ,R.id.buttonInverse , R.id.buttonSinusH , R.id.buttonCosinusH , R.id.buttonTanH , R.id.buttonRandom
                , R.id.buttonConstant , R.id.buttonARC


        };
        return buttonsPointerArray;
    }

    int[] getScientificButtonsID(){
        int [] buttonsPointerArray;

        buttonsPointerArray = new int[]{


                R.id.buttonSinus, R.id.buttonCosinus, R.id.buttonTan, R.id.buttonln, R.id.buttonlog, R.id.buttonlog,  R.id.buttonpie, R.id.buttonEXP, R.id.buttonpower,
                R.id.buttonrad , R.id.buttonfact, R.id.switch_deg_rad, R.id.buttonPow2 , R.id.buttonPow3 ,R.id.buttonInverse , R.id.buttonSinusH , R.id.buttonCosinusH , R.id.buttonTanH , R.id.buttonRandom
                , R.id.buttonConstant , R.id.buttonARC


        };
        return buttonsPointerArray;
    }

    int[] getDialpadButtonsID(){
        int [] buttonsPointerArray;

        buttonsPointerArray = new int[]{

                R.id.buttonClear, R.id.buttonPlus, R.id.buttonMinus, R.id.buttonDevide, R.id.buttonTimes, R.id.buttonEquals,
                R.id.openParen, R.id.closeParen, R.id.buttonPercent,
                R.id.button9, R.id.button8, R.id.button7,
                R.id.button6, R.id.button5, R.id.button4, R.id.button1, R.id.button2, R.id.button3, R.id.button0,
                R.id.buttonPoint,
        };
        return buttonsPointerArray;
    }
    void redrawKeypadInFlatTheme(){
        setTextColorState(getOperatorButtonsID(), getThemeColorStateList());
        setTextColorState(getNonAccentButtonsID(), getNonAccentColorStateList());
//        int x = BuildConfig.VERSION_CODE;
//        if(BuildConfig.VERSION_CODE<21) {
//            setBackgroundColorForOperators();
//        }
    }

    private ColorStateList getThemeColorStateList() {
        int[][] states = getStateAray();
        int[] colors = new int[] {
                Color.WHITE,
               getAccentColorCode()
        };

        return new ColorStateList(states, colors);
    }
    public int getAccentColorCode(){
        //TODO set a cool default theme color
        SharedPreferences appPreferences =this.getActivity().getSharedPreferences("THEME", Context.MODE_PRIVATE);
        return appPreferences.getInt("ACCENT_COLOR_CODE", Color.parseColor("#009688"));
    }

    private ColorStateList getNonAccentColorStateList() {
        int[][] states = getStateAray();
        int[] colors;
        colors = new int[]{
                getAccentColorCode(), ((MainActivity) getActivity()).getDialpadFontColor(),

        };

        return new ColorStateList(states, colors);
    }

    int[][] getStateAray (){
        return new int[][] {

                new int[] { android.R.attr.state_pressed} , // pressed
                new int[] { -android.R.attr.state_pressed}  // pressed
        };

    }

    private void setTextColorState(int[] buttonsIdArray, ColorStateList textColor) {
        for(int buttonId:buttonsIdArray){
            if((Button) mView.findViewById(buttonId)!= null)
                ((Button) mView.findViewById(buttonId)).setTextColor(textColor);
        }
    }

    private void setBackgroundColorForOperators() {

        mView.findViewById(R.id.buttonTimes).getBackground().setColorFilter(((MainActivity) getActivity()).getAccentColorCode(), PorterDuff.Mode.SRC_ATOP);
        mView.findViewById(R.id.buttonDevide).getBackground().setColorFilter(((MainActivity) getActivity()).getAccentColorCode(), PorterDuff.Mode.SRC_ATOP);
        mView.findViewById(R.id.buttonPlus).getBackground().setColorFilter(((MainActivity) getActivity()).getAccentColorCode(), PorterDuff.Mode.SRC_ATOP);
        mView.findViewById(R.id.buttonMinus).getBackground().setColorFilter(((MainActivity) getActivity()).getAccentColorCode(), PorterDuff.Mode.SRC_ATOP);
        mView.findViewById(R.id.openParen).getBackground().setColorFilter(((MainActivity) getActivity()).getAccentColorCode(), PorterDuff.Mode.SRC_ATOP);
        mView.findViewById(R.id.closeParen).getBackground().setColorFilter(((MainActivity) getActivity()).getAccentColorCode(), PorterDuff.Mode.SRC_ATOP);
        mView.findViewById(R.id.buttonPercent).getBackground().setColorFilter(((MainActivity) getActivity()).getAccentColorCode(), PorterDuff.Mode.SRC_ATOP);

    }

    void setClearButtonText(String input){

        mView.findViewById(R.id.buttonClear).setTag(input);

        refreshCButtonTypeface();

    }

    private void refreshCButtonTypeface() {

        if( (mView.findViewById(R.id.buttonClear)).getTag().equals("C"))
        {
            //Change Clear Button's Text to C
            ((Button) mView.findViewById(R.id.buttonClear)).setText("C");
            if(mIsRetroOn){
                ((Button) mView.findViewById(R.id.buttonClear)).setTypeface(scientificFont);

            }else{
                ((Button) mView.findViewById(R.id.buttonClear)).setTypeface(defaultFont);

            }
            ((Button) mView.findViewById(R.id.buttonClear)).setTextSize(getResources().getDimension(R.dimen.btn_clear_text_size) / getResources().getDisplayMetrics().density);

        }else {
            //Change Clear Button's Text to Backspace

            ((Button) mView.findViewById(R.id.buttonClear)).setTextSize(getResources().getDimension(R.dimen.retro_backspace_text_size) / getResources().getDisplayMetrics().density);
            ((Button) mView.findViewById(R.id.buttonClear)).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "flaticon.ttf"));
            if(mIsRetroOn){
                ((Button) mView.findViewById(R.id.buttonClear)).setText(getResources().getString(R.string.backSpace_retro));
            }else{

                ((Button) mView.findViewById(R.id.buttonClear)).setText(getResources().getString(R.string.backSpace));

            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG,"Fragment onSaveInstanceState");


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG,"Fragment onAttach ");
    }

    @Override
    public void onDetach() {

        super.onDetach();
        Log.d(TAG,"Fragment onDetach");

    }

    @Override
    public void onPause() {
        Log.d(TAG,"Fragment onpause");

//        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(mThemeChangedReciever);
//        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(mClearButtonChangedReceiver);
        super.onPause();
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        boolean on = ((ToggleButton) buttonView).isChecked();


        if (on) {
            //result in Radian
            ((MainActivity)getActivity()). setAngleMode(true);
        } else {
            //result in Degress
            ((MainActivity)getActivity()). setAngleMode(false);

        }
    }



}
