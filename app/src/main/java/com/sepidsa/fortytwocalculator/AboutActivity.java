package com.sepidsa.fortytwocalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class AboutActivity extends Activity implements View.OnClickListener {

    private static final String TAG ="About Activity" ;
    ImageButton mMailToFarshid,mMailToEhsan,
            mInstagramFarshid,mInstagramEhsan,
            mLinkedInFarshid, mLinkedInEhsan,
            mBackButton;

    Button webpageButton;
     ImageButton mInstagramSepidsa,mInfoMailSepidsa,mFacebookSepidsa,mWebSiteSepidsa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_dialog);
        TextView ehsanNameTV = (TextView) findViewById(R.id.ehsan_name);
        TextView farshidNameTV = (TextView) findViewById(R.id.farshid_name);
        ehsanNameTV.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "notoregular.ttf"));
        farshidNameTV.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "notoregular.ttf"));
        mMailToEhsan = (ImageButton)findViewById(R.id.mail_to_ehsan);
        mMailToFarshid = (ImageButton)findViewById(R.id.mail_to_farshid);
        mLinkedInEhsan = (ImageButton)findViewById(R.id.linked_in_ehsan);
        mLinkedInFarshid = (ImageButton)findViewById(R.id.linked_in_farshid);
        mInstagramEhsan = (ImageButton)findViewById(R.id.instagram_ehsan);
        mInstagramFarshid = (ImageButton)findViewById(R.id.instagram_farshid);
        mInstagramSepidsa = (ImageButton)findViewById(R.id.instagram_sepidsa);
        mFacebookSepidsa = (ImageButton)findViewById(R.id.facebook_sepidsa);
        mInfoMailSepidsa = (ImageButton)findViewById(R.id.email_info_sepidsa);
        mWebSiteSepidsa = (ImageButton)findViewById(R.id.sepidsa_webpage_icon_button);
        mBackButton = (ImageButton)findViewById(R.id.button_back_about);
        webpageButton = (Button)findViewById(R.id.sepidsa_web_page_button);
        webpageButton.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "notoregular.ttf"));

        prepareButtons();

    }

    private void prepareButtons() {


        mMailToEhsan.setOnClickListener(this);
        mLinkedInEhsan.setOnClickListener(this);
        mInstagramEhsan.setOnClickListener(this);

        mMailToFarshid.setOnClickListener(this);
        mLinkedInFarshid.setOnClickListener(this);
        mInstagramFarshid.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
        webpageButton.setOnClickListener(this);
        mInstagramSepidsa.setOnClickListener(this);
        mFacebookSepidsa.setOnClickListener(this);
        mInfoMailSepidsa.setOnClickListener(this);
        mWebSiteSepidsa.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Uri uri;
        Intent intent;
        switch (v.getId()){
            case R.id.mail_to_ehsan:
                sendEmail(this,"ehsan@sepidsa.com","","",null);
                break;
            case R.id.email_info_sepidsa:
                sendEmail(this,"info@sepidsa.com","","",null);
                break;
            case R.id.linked_in_ehsan:
                uri = Uri.parse("https://www.linkedin.com/pub/ehsan-parhizkar/97/843/b79");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.instagram_ehsan:
                uri = Uri.parse("http://instagram.com/EHS4NPAR");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.mail_to_farshid:
                sendEmail(this,"farshid@sepidsa.com","","",null);
                break;
            case R.id.linked_in_farshid:
                uri = Uri.parse("https://ir.linkedin.com/pub/farshid-imanipour/97/74a/a93");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.instagram_farshid:
                uri = Uri.parse("http://instagram.com/f4rsh");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.button_back_about:
                this.finish();
                break;
            case R.id.sepidsa_web_page_button:
            case R.id.sepidsa_webpage_icon_button:
                goToURL("http://blog.sepidsa.com");
                break;
            case R.id.instagram_sepidsa:
                uri = Uri.parse("http://instagram.com/teamsepidsa");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.facebook_sepidsa:
                uri = Uri.parse("http://facebook.com/teamsepidsa");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
        }

    }

    private void goToURL(String input) {
        Uri uri = Uri.parse(input);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }

    public static void sendEmail(final Context p_context, final String recepient, final String p_subject, final String p_body, final ArrayList<String> p_attachments)
    {
        try
        {
            PackageManager pm = p_context.getPackageManager();
            ResolveInfo selectedEmailActivity = null;

            Intent emailDummyIntent = new Intent(Intent.ACTION_SENDTO);
            emailDummyIntent.setData(Uri.parse("mailto:"+recepient));

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
                    sendEmailUsingSelectedEmailApp(p_context,recepient, p_subject, p_body, p_attachments, selectedEmailActivity);
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
                            sendEmailUsingSelectedEmailApp(p_context,recepient, p_subject, p_body, p_attachments, emailActivitiesForDialog.get(which));
                        }
                    });

                    builder.create().show();
                }
            }
            else
            {
                sendEmailUsingSelectedEmailApp(p_context,recepient, p_subject, p_body, p_attachments, null);
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Can't send email", ex);
        }
    }

    protected static void sendEmailUsingSelectedEmailApp(Context p_context,String recepient, String p_subject, String p_body, ArrayList<String> p_attachments, ResolveInfo p_selectedEmailApp)
    {
        try
        {
            Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

            String aEmailList[] = { recepient};

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
}
