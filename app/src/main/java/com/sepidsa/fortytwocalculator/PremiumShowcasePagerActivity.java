package com.sepidsa.fortytwocalculator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sepidsa.fortytwocalculator.util.IabHelper;
import com.sepidsa.fortytwocalculator.util.IabResult;
import com.sepidsa.fortytwocalculator.util.Inventory;
import com.sepidsa.fortytwocalculator.util.Purchase;

import java.util.List;


public class PremiumShowcasePagerActivity extends FragmentActivity {

    static final int NUM_PAGES = 6;
    static final String SKU_PREMIUM = "premium_upgrade";
    static final int RC_REQUEST = 10001;
    boolean mThreadLock = false;
    static final String TAG = "in_app_billing";

    ViewPager pager;
    PagerAdapter pagerAdapter;
    LinearLayout circles;
    Button skip;
    Button done;
    ImageButton next;
    Button buyPre;
    boolean mHasBazaar =false;
    IabHelper.QueryInventoryFinishedListener
            mQueryFinishedListener;

    // The helper object
    com.sepidsa.fortytwocalculator.util.IabHelper mHelper;

    public String base64EncodedPublicKey;
    /*
        This is nasty but as the transparency of the fragments increases when swiping the underlying
        Activity becomes visible, so we change the pager opacity on the last slide in
        setOnPageChangeListener below
     */
    boolean isOpaque = true;
    private static final String BAZAAR_PACKAGE_NAME = "com.farsitel.bazaar";
    private boolean mSetupFinished =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        base64EncodedPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwCgNUrs2KdQY911EkU3jcroP73iRap4P48t6pK3O3+NHum0/GYibcC5WAdw7YSIcirAlKr8niYErlVmbx9pkAAACMepMF11xQABddFvkgKMOLa+MGt/V2TAACeoA7DvLN8YyG8U6HwC1juu+honao7IW0mxbmOT34Xv4ff9wHajVB/Cm1S00Un7Ro0DBZQ3VBwShSbmqxVMOHx6e5ObuzE0gTqDdsNcgGab4lFf4wkCAwEAAQ==";


        /*
            Setting this makes sure we draw fullscreen, without this the transparent Activity shows
            the bright orange notification header from the main Activity below
        */
//        Window window = getWindow();
//        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.activity_parallax_pager);

        skip = Button.class.cast(findViewById(R.id.skip));
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTutorial();
            }
        });

        next = ImageButton.class.cast(findViewById(R.id.next));
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(pager.getCurrentItem() + 1, true);
            }
        });


        done = Button.class.cast(findViewById(R.id.done));
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getPremiumPreference()){
                    endTutorial();
                }else{
                    buyPremium();
                }
            }
        });



        pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(6);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setPageTransformer(true, new CrossfadePageTransformer());
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //See note above for why this is needed
                if (position == NUM_PAGES - 2 && positionOffset > 0) {
                    if (isOpaque) {
                        pager.setBackgroundColor(Color.TRANSPARENT);
                        isOpaque = false;
                    }
                } else {
                    if (!isOpaque) {
                        pager.setBackgroundColor(getResources().getColor(R.color.tutorial_background_opaque));
                        isOpaque = true;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
                if (position == NUM_PAGES - 2) {
                    skip.setVisibility(View.GONE);
                    next.setVisibility(View.GONE);
                    done.setVisibility(View.VISIBLE);
                } else if (position < NUM_PAGES - 2) {
                    skip.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                    done.setVisibility(View.GONE);
                } else if (position == NUM_PAGES - 1) {
                    if(getPremiumPreference()){
                        endTutorial();
                    }else{
                        buyPremium();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Unused
            }
        });

        buildCircles();
        if(isBazaarPackageInstalled(getApplicationContext(),BAZAAR_PACKAGE_NAME)) {
            setupMHelper();
        }
    }

    private void setupMHelper() {
        mHelper = new IabHelper(this, base64EncodedPublicKey);
//        mHelper.enableDebugLogging(false);
        try{
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    Log.d(TAG, "Setup finished.");

                    if (!result.isSuccess()) {
                        // Oh noes, there was a problem.
//                        complain("Problem setting up in-app billing: " + result);
                        endTutorial();
                        return;
                    }

                    // Have we been disposed of in the meantime? If so, quit.
                    if (mHelper == null) return;

                    // IAB is fully set up. Now, let's get an inventory of stuff we own.
//                    Log.d(TAG, "Setup successful. Querying inventory.");
                    mSetupFinished = true;
                    mHelper.queryInventoryAsync(mGotInventoryListener);
//                buyPre.setVisibility(View.VISIBLE);
                }
            });}catch (SecurityException se){
            if(mHelper != null){
                mHelper.dispose();
                mHelper = null;
            }
            Toast.makeText(getApplicationContext(),"برنامه را از طریق بازار نصب کنید",Toast.LENGTH_LONG).show();

        }
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

    //    private boolean mGotInventory =false;
    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
//            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
//                complain("Failed to query inventory: " + result);
                return;
            }

//            Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */
//            mGotInventory = true;
            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            boolean mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            setPremiumPreference (mIsPremium);
            if(getPremiumPreference()){
                Toast.makeText(getApplicationContext(),"شما در حال حاضر طلایی هستید",Toast.LENGTH_LONG).show();
                endTutorial();

            }
//            Log.d(TAG, "User is " + (getPremiumPreference() ? "PREMIUM" : "NOT PREMIUM"));

//            updateUi();
//            setWaitScreen(false);
//            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };




    private void buyPremium() {
        //TODO INSERT BUY PREMIUM CODE
        if(!isBazaarPackageInstalled(getApplicationContext(), BAZAAR_PACKAGE_NAME)){
            showDownloadBazaarDialog();
        }else {
            if (mHelper != null && mSetupFinished) {
//                try {
                mHelper.flagEndAsync();
                mHelper.launchPurchaseFlow(this, SKU_PREMIUM, RC_REQUEST,
                        mPurchaseFinishedListener, "");
                // Fade the premium tour
//                } catch (Exception e) {
//                    mHelper.dispose();
//                    mHelper = null;
//                }
            } else {
                Toast.makeText(getApplicationContext(), "لطفا در بازار با اکانت خود وارد شوید و دوباره امتحان کنید", Toast.LENGTH_LONG).show();

            }
        }

    }

    void showDownloadBazaarDialog(){
        AlertDialog.Builder adb = new AlertDialog.Builder(this);


        adb.setTitle("ظاهرا بازار نصب نیست. لطفا بازار رو دانلود کنید و برنامه رو از طریق بازار مجددا نصب کنید تا بتونید طلایی بشید");


        adb.setPositiveButton("دانلود بازار", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                goToUrl("http://cafebazaar.ir/download/bazaar.apk");
            } });

        adb.setNegativeButton("باشه بعدا", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            } });
        adb.show();
    }
    private void goToUrl (String address) {
        Uri uriUrl = Uri.parse(address);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }


    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {

//                complain("Error purchasing: " + result);
//                setWaitScreen(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
//                setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");


            if (purchase.getSku().equals(SKU_PREMIUM)) {
                // bought the premium upgrade!
                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
//                endTutorial();
                Toast.makeText(getApplicationContext(),"شما طلایی هستید...مبارک باشه!",Toast.LENGTH_LONG).show();
//                mIsPremium = true;
                setPremiumPreference(true);
                endTutorial();
//todo update ui for finished
//                updateUi();
//                setWaitScreen(false);
            }
        }
    };



    public void settHasbazaar(boolean hasBazaar){
        //TODO set a cool default theme color
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("APP", MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putBoolean("IS_BAZAAR_INSTALLED", hasBazaar);
        editor.commit();
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    public boolean getPremiumPreference(){
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("purchases", Context.MODE_PRIVATE);
        return  appPreferences.getBoolean("isPremium",false);
    }

    public void setPremiumPreference(boolean isPremium){

        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("purchases", MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putBoolean("isPremium", isPremium);
        editor.apply();


    }

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == resultCode ){


        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
        if(resultCode == 0){
            endTutorial();
        }
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            try {
//              mHelper.flagEndAsync();
                mHelper.dispose();
            }catch (Exception e){}

            mHelper = null;
        }
        Log.d(TAG, "Destroying helper.");

        super.onDestroy();


    }

    /*
            The last fragment is transparent to enable the swipe-to-finish behaviour seen on Google's apps
            So our viewpager circle indicator needs to show NUM_PAGES - 1
         */
    private void buildCircles(){
        circles = LinearLayout.class.cast(findViewById(R.id.circles));

        float scale = getResources().getDisplayMetrics().density;
        int padding = (int) (5 * scale + 0.5f);

        for(int i = 0 ; i < NUM_PAGES - 1 ; i++){
            ImageView circle = new ImageView(this);
            circle.setImageResource(R.drawable.empty_circle);
            circle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            circle.setAdjustViewBounds(true);
            circle.setPadding(padding, 0, padding, 0);
            circles.addView(circle);
        }

        setIndicator(0);
    }

    private void setIndicator(int index){
        if(index < NUM_PAGES){
            for(int i = 0 ; i < NUM_PAGES - 1 ; i++){
                ImageView circle = (ImageView) circles.getChildAt(i);
                if(i == index){
                    circle.setImageResource(R.drawable.full_circle);
                }else {
                    circle.setImageResource(R.drawable.empty_circle);
                }
            }
        }
    }

    private void endTutorial(){
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private Fragment[] mFragments;

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
            mFragments = new Fragment[NUM_PAGES];
            mFragments[0] = ParallaxPane.newInstance(R.layout.fragment_premium_tour_pane_three); //history
            mFragments[1] = ParallaxPane.newInstance(R.layout.fragment_premium_tour_pane_four);//labels

            mFragments[2] = ParallaxPane.newInstance(R.layout.fragment_premium_tour_pane_two); //clasic theme
            mFragments[3] = ParallaxPane.newInstance(R.layout.fragment_premium_tour_pane_one); //pallet color
            mFragments[4] = ParallaxPane.newInstance(R.layout.fragment_premium_tour_pane_five);//support

            mFragments[5] = ParallaxPane.newInstance(R.layout.fragment_parallax_pane_transparent);
            Intent intent = getIntent();
            int page = intent.getIntExtra("page", 0);
            Fragment temp = mFragments[0];
            mFragments[0] = mFragments[page];
            mFragments[page] = temp;

            Typeface flatIcon = Typeface.createFromAsset(getAssets(), "yekan.ttf");
//            for (Fragment fg:mFragments){
//
//                TextView tv = (TextView)(fg.getView().findViewById(R.id.tour_title));
////                tv.setTypeface(flatIcon);
////                tv = (TextView)(fg.getView().findViewById(R.id.tour_description));
////                tv.setTypeface(flatIcon);
//            }
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];

        }
        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public class CrossfadePageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();

            View backgroundView = page.findViewById(R.id.background);
            View text = page.findViewById(R.id.tour_title);

            View phone = page.findViewById(R.id.tour_descriptive_icon);
            View map = page.findViewById(R.id.tour_screenshot);
            View mountain = null;
            View mountainNight = null;
            View rain = page.findViewById(R.id.tour_description);;
            View hands = page.findViewById(R.id.tour_screenshot);
            buyPre = Button.class.cast(findViewById(R.id.btn_buy_premium));


            if(buyPre != null) {
                buyPre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buyPremium();
                    }
                });
            }

            if (position <= 1) {
                page.setTranslationX(pageWidth * -position);
            }

            if(position <= -1.0f || position >= 1.0f) {
            } else if( position == 0.0f ) {
            } else {
                if(backgroundView != null) {
                    backgroundView.setAlpha(1.0f - Math.abs(position));
                }

                //Text both translates in/out and fades in/out
                if (text != null) {
                    text.setTranslationX(pageWidth * position);
                    text.setAlpha(1.0f - Math.abs(position));
                }

                //Map + tour_descriptive_icon - tour_screenshot simple translate, tour_descriptive_icon parallax effect
                if(map != null){
                    map.setTranslationX(pageWidth * position);
                }

                if(phone != null){
                    phone.setTranslationX((float)(pageWidth/1.2 * position));
                }

                //Mountain day - fade in/out
                if(mountain != null){
                    mountain.setAlpha(1.0f - Math.abs(position));
                }

                //Mountain night - fade in, but translate out, rain fades in but parallax translate out
                if(mountainNight != null){
                    if(position < 0){
                        mountainNight.setTranslationX(pageWidth * position);
                    }else{
                        mountainNight.setAlpha(1.0f - Math.abs(position));
                    }
                }

                if(rain != null){
                    if(position < 0){
                        rain.setTranslationX((float)(pageWidth/1.2 * position));
                    }else{
                        rain.setAlpha(1.0f - Math.abs(position));
                    }
                }

                //Long click device + hands - translate both way but only fade out
                if(hands != null){
                    hands.setTranslationX(pageWidth * position);
                    if(position < 0) {
                        hands.setAlpha(1.0f - Math.abs(position));
                    }
                }
            }
        }
    }
}