package com.sepidsa.fortytwocalculator

import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import java.io.File
import java.util.ArrayList

class AboutActivity : Activity(), View.OnClickListener {

    private lateinit var mMailToFarshid: ImageButton
    private lateinit var mMailToEhsan: ImageButton
    private lateinit var mInstagramFarshid: ImageButton
    private lateinit var mInstagramEhsan: ImageButton
    private lateinit var mLinkedInFarshid: ImageButton
    private lateinit var mLinkedInEhsan: ImageButton
    private lateinit var mBackButton: ImageButton

    private lateinit var webpageButton: Button
    private lateinit var mInstagramSepidsa: ImageButton
    private lateinit var mInfoMailSepidsa: ImageButton
    private lateinit var mFacebookSepidsa: ImageButton
    private lateinit var mWebSiteSepidsa: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_dialog)

        val ehsanNameTV = findViewById<TextView>(R.id.ehsan_name)
        val farshidNameTV = findViewById<TextView>(R.id.farshid_name)
        ehsanNameTV.typeface = Typeface.createFromAsset(applicationContext.assets, "notoregular.ttf")
        farshidNameTV.typeface = Typeface.createFromAsset(applicationContext.assets, "notoregular.ttf")

        mMailToEhsan = findViewById(R.id.mail_to_ehsan)
        mMailToFarshid = findViewById(R.id.mail_to_farshid)
        mLinkedInEhsan = findViewById(R.id.linked_in_ehsan)
        mLinkedInFarshid = findViewById(R.id.linked_in_farshid)
        mInstagramEhsan = findViewById(R.id.instagram_ehsan)
        mInstagramFarshid = findViewById(R.id.instagram_farshid)
        mInstagramSepidsa = findViewById(R.id.instagram_sepidsa)
        mFacebookSepidsa = findViewById(R.id.facebook_sepidsa)
        mInfoMailSepidsa = findViewById(R.id.email_info_sepidsa)
        mWebSiteSepidsa = findViewById(R.id.sepidsa_webpage_icon_button)
        mBackButton = findViewById(R.id.button_back_about)
        webpageButton = findViewById(R.id.sepidsa_web_page_button)

        webpageButton.typeface = Typeface.createFromAsset(applicationContext.assets, "notoregular.ttf")

        prepareButtons()
    }

    private fun prepareButtons() {
        mMailToEhsan.setOnClickListener(this)
        mLinkedInEhsan.setOnClickListener(this)
        mInstagramEhsan.setOnClickListener(this)

        mMailToFarshid.setOnClickListener(this)
        mLinkedInFarshid.setOnClickListener(this)
        mInstagramFarshid.setOnClickListener(this)
        mBackButton.setOnClickListener(this)
        webpageButton.setOnClickListener(this)
        mInstagramSepidsa.setOnClickListener(this)
        mFacebookSepidsa.setOnClickListener(this)
        mInfoMailSepidsa.setOnClickListener(this)
        mWebSiteSepidsa.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_about, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.mail_to_ehsan) {
            sendEmail(this, "ehsan@sepidsa.com", "", "", null)
        } else if (id == R.id.email_info_sepidsa) {
            sendEmail(this, "info@sepidsa.com", "", "", null)
        } else if (id == R.id.linked_in_ehsan) {
            val uri = Uri.parse("https://www.linkedin.com/pub/ehsan-parhizkar/97/843/b79")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } else if (id == R.id.instagram_ehsan) {
            val uri = Uri.parse("http://instagram.com/EHS4NPAR")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } else if (id == R.id.mail_to_farshid) {
            sendEmail(this, "farshid@sepidsa.com", "", "", null)
        } else if (id == R.id.linked_in_farshid) {
            val uri = Uri.parse("https://ir.linkedin.com/pub/farshid-imanipour/97/74a/a93")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } else if (id == R.id.instagram_farshid) {
            val uri = Uri.parse("http://instagram.com/f4rsh")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } else if (id == R.id.button_back_about) {
            finish()
        } else if (id == R.id.sepidsa_web_page_button || id == R.id.sepidsa_webpage_icon_button) {
            goToURL("http://blog.sepidsa.com")
        } else if (id == R.id.instagram_sepidsa) {
            val uri = Uri.parse("http://instagram.com/teamsepidsa")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } else if (id == R.id.facebook_sepidsa) {
            val uri = Uri.parse("http://facebook.com/teamsepidsa")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    private fun goToURL(input: String) {
        val uri = Uri.parse(input)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "About Activity"

        fun sendEmail(
            pContext: Context,
            recepient: String,
            pSubject: String?,
            pBody: String?,
            pAttachments: ArrayList<String>?,
        ) {
            try {
                val pm: PackageManager = pContext.packageManager
                var selectedEmailActivity: ResolveInfo? = null

                val emailDummyIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$recepient")
                }

                var emailActivities = pm.queryIntentActivities(emailDummyIntent, 0)

                if (emailActivities.isNullOrEmpty()) {
                    val emailDummyIntentRFC822 = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                        type = "message/rfc822"
                    }

                    emailActivities = pm.queryIntentActivities(emailDummyIntentRFC822, 0)
                }

                if (emailActivities != null) {
                    selectedEmailActivity = if (emailActivities.size == 1) {
                        emailActivities[0]
                    } else {
                        emailActivities.firstOrNull { it.isDefault }
                    }

                    if (selectedEmailActivity != null) {
                        sendEmailUsingSelectedEmailApp(
                            pContext,
                            recepient,
                            pSubject,
                            pBody,
                            pAttachments,
                            selectedEmailActivity,
                        )
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
                                recepient,
                                pSubject,
                                pBody,
                                pAttachments,
                                emailActivitiesForDialog[which],
                            )
                        }

                        builder.create().show()
                    }
                } else {
                    sendEmailUsingSelectedEmailApp(pContext, recepient, pSubject, pBody, pAttachments, null)
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Can't send email", ex)
            }
        }

        protected fun sendEmailUsingSelectedEmailApp(
            pContext: Context,
            recepient: String,
            pSubject: String?,
            pBody: String?,
            pAttachments: ArrayList<String>?,
            pSelectedEmailApp: ResolveInfo?,
        ) {
            try {
                val emailIntent = Intent(Intent.ACTION_SEND_MULTIPLE)

                val aEmailList = arrayOf(recepient)

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
    }
}

