package com.liveperson.sample.app

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.liveperson.monitoring.model.LPMonitoringIdentity
import com.liveperson.monitoring.sdk.api.LivepersonMonitoring
import com.liveperson.monitoring.sdk.callbacks.MonitoringErrorType
import com.liveperson.monitoring.sdk.callbacks.SdeCallback
import com.liveperson.monitoring.sdk.responses.LPEngagementResponse
import com.liveperson.monitoring.sdk.responses.LPSdeResponse
import com.liveperson.sample.app.Utils.SampleAppStorage
import com.liveperson.sdk.MonitoringParams
import com.liveperson.sdk.callbacks.EngagementCallback
import kotlinx.android.synthetic.main.activity_monitoring.*
import org.json.JSONArray
import org.json.JSONException
import java.lang.Exception
import java.util.*

class MonitoringActivity : AppCompatActivity() {

    private val TAG = "MonitoringActivity"


    var entryPoinstsEditText: EditText? = null
    var engagementAttributesEditText: EditText? = null

    var currentCampaignId : String? = null
    var currentEngagementId : String? = null
    var currentSessionId : String? = ""
    var currentVisitorId : String? = ""
    var currentEngagementContextId : String? = ""

    private var progressBar: LinearLayout? = null
    private var engagementResultsTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitoring)

        // Buttons
        val getEngagementButton = findViewById<Button>(R.id.get_engagement_button)
        val sendSdeButton = findViewById<Button>(R.id.send_sde_button)
        val openMessagingButton = findViewById<Button>(R.id.open_messaging_button)

        // EditTexts
        val accountIdEditText = findViewById<EditText>(R.id.account_id_edit_text)
        val consumerIdEditText = findViewById<EditText>(R.id.consumer_id_edit_text)
        val pageIdEditText = findViewById<EditText>(R.id.page_id_edit_text)

        // TextViews
        val sdkVersionTextView = findViewById<TextView>(R.id.sdk_version_text_view)

        entryPoinstsEditText = findViewById<EditText>(R.id.entry_points_edit_text)
        engagementAttributesEditText = findViewById<EditText>(R.id.engagement_attributes_edit_text)

        entryPoinstsEditText?.setOnFocusChangeListener { view, hasFocus -> changeHeight(view, hasFocus) }
        engagementAttributesEditText?.setOnFocusChangeListener { view, hasFocus -> changeHeight(view, hasFocus)}

        // TextViews
        engagementResultsTextView = findViewById<TextView>(R.id.resultTextView)

        // Progress bar
        progressBar = findViewById<LinearLayout>(R.id.action_progress_bar)

        var allParamsValid = false

        // Display the SDK version
        sdkVersionTextView.text = "SDK Version: ${LivepersonMonitoring.getSDKVersion()}"

        // Set values to editTests
        consumerIdEditText.setText(SampleAppStorage.getInstance(this).consumerId)
        pageIdEditText.setText(SampleAppStorage.getInstance(this).pageId)

        /////////////// Get Engagement ////////////////////////////
        getEngagementButton.setOnClickListener({

            showProgressBar()
            try {
                SampleAppStorage.getInstance(this@MonitoringActivity).consumerId = consumerIdEditText.text.toString()

                var lpMonitoringIdentityList = Arrays.asList(LPMonitoringIdentity(consumerIdEditText.text.toString(), "brandIssuer"))

                LivepersonMonitoring.getEngagement(this@MonitoringActivity, lpMonitoringIdentityList, buildSde(false), object : EngagementCallback{
                    override fun onSuccess(lpEngagementResponse: LPEngagementResponse) {

                        hideProgressBar()

                        // Store the received campaignId, engagementId, sessionId and visitorId as the current ones. This is used to send them to the Messaging TestApp
                        val engagementList = lpEngagementResponse.engagementDetailsList
                        if(engagementList != null && !engagementList.isEmpty()) {
                            // For demo we display the first engagement only
                            currentCampaignId = engagementList[0].campaignId
                            currentEngagementId = engagementList[0].engagementId
                            currentEngagementContextId = engagementList[0].contextId
                            currentSessionId = lpEngagementResponse.sessionId
                            currentVisitorId = lpEngagementResponse.visitorId
                        }

                        updateResult(lpEngagementResponse.toString())
                        pageIdEditText.setText(lpEngagementResponse.pageId)
                        SampleAppStorage.getInstance(this@MonitoringActivity).pageId = lpEngagementResponse.pageId

                    }

                    override fun onError(errorType: MonitoringErrorType, exception: Exception?) {
                        hideProgressBar()
                        updateResult("getEngagement failed. ${errorType.name}, ${exception?.message ?: ""}")
                    }

                })
            } catch (e: JSONException) { // If there is a problem with the EnrtyPoint or EngagementAttr data
                updateResult("Data incompatible")
                hideProgressBar()
            }

        })

        //////////////// Send SDE ////////////////////////////
        sendSdeButton.setOnClickListener({

            if (TextUtils.isEmpty(engagementAttributesEditText?.text.toString())) {

                updateResult("Engagement Attributes are mandatory for Send SDE.")
                return@setOnClickListener
            }

            showProgressBar()
            try {
                SampleAppStorage.getInstance(this@MonitoringActivity).consumerId = consumerIdEditText.text.toString()
                SampleAppStorage.getInstance(this@MonitoringActivity).pageId = pageIdEditText.text.toString()

                var lpMonitoringIdentityList = Arrays.asList(LPMonitoringIdentity(consumerIdEditText.text.toString(), "brandIssuer"))
                LivepersonMonitoring.sendSde(this@MonitoringActivity, lpMonitoringIdentityList, buildSde(true), object : SdeCallback{
                    override fun onSuccess(lpSdeResponse: LPSdeResponse) {
                        hideProgressBar()
                        updateResult(lpSdeResponse.toString())
                        pageIdEditText.setText(lpSdeResponse.pageId)
                    }

                    override fun onError(errorType: MonitoringErrorType, exception: Exception?) {
                        hideProgressBar()
                        updateResult("sendSde failed. ${errorType.name}, ${exception?.message ?: ""}")
                    }

                })
            } catch (e: JSONException) {
                updateResult("Data incompatible")
                hideProgressBar()
            }

        })

        ///////////// Open Messaging Button //////////////////////////////
        openMessagingButton.setOnClickListener({
            // Open the Messaging Activity only if both CampaignId and EngagementId are available
            if (!TextUtils.isEmpty(currentCampaignId) && !TextUtils.isEmpty(currentEngagementId)) {
                val messagingIntent = Intent(MonitoringActivity@this, MessagingActivity::class.java)
                messagingIntent.putExtra(MessagingActivity.CAMPAIGN_ID_KEY, currentCampaignId)
                messagingIntent.putExtra(MessagingActivity.ENGAGEMENT_ID_KEY, currentEngagementId)
                messagingIntent.putExtra(MessagingActivity.SESSION_ID_KEY, currentSessionId)
                messagingIntent.putExtra(MessagingActivity.VISITOR_ID_KEY, currentVisitorId)
                messagingIntent.putExtra(MessagingActivity.ENGAGEMENT_CONTEXT_ID_KEY, currentEngagementContextId)
                startActivity(messagingIntent)
            }
            else{
                Toast.makeText(this@MonitoringActivity, "CampaingId or EngagementId are not available", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        hideProgressBar()
    }

    private fun changeHeight(view: View?, expand: Boolean) {
        if (view is EditText) {
            val layoutParams = view.layoutParams
            if (expand) {
                view.textSize = 16f
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                view.layoutParams = layoutParams
            }
            else{
                view.textSize = 12f
                layoutParams.height = 120
                view.layoutParams = layoutParams
            }
        }

    }

    private fun updateResult(result : String){
        engagementResultsTextView?.text = result

    }

    private fun showProgressBar(){
        progressBar?.visibility = View.VISIBLE
    }

    private fun hideProgressBar(){
        progressBar?.visibility = View.GONE
    }

    /**
     * Build SDE from entryPoints and engagementAttributes
     */
    fun buildSde(withPageId : Boolean) : MonitoringParams {

        var entryPoints : JSONArray? = null
        var engagementAttributes : JSONArray? = null

        if (!TextUtils.isEmpty(entryPoinstsEditText?.text.toString())) {

            entryPoints = JSONArray(entryPoinstsEditText?.text.toString())
        }

        if (!TextUtils.isEmpty(engagement_attributes_edit_text.text.toString())) {

            engagementAttributes = JSONArray(engagementAttributesEditText?.text.toString())
        }

        val pageId : String? = if (withPageId)  page_id_edit_text.text.toString() else null
        return MonitoringParams(pageId, entryPoints, engagementAttributes)
    }


}
