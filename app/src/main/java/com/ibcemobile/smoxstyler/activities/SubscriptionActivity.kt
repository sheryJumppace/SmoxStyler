package com.ibcemobile.smoxstyler.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.BillingProcessor.IBillingHandler
import com.anjlab.android.iab.v3.TransactionDetails
import com.ibcemobile.smoxstyler.MainActivity
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.databinding.ActivitySubscriptionBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.Barber
import org.json.JSONObject


class SubscriptionActivity : BaseActivity(), IBillingHandler {
    private lateinit var binding: ActivitySubscriptionBinding
    private var priceValue: Double? = null
    private var bp: BillingProcessor? = null
    private var purchaseTransactionDetails: TransactionDetails? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bp = BillingProcessor(this, resources.getString(R.string.play_console_license), this)
        bp!!.initialize()

        binding.imgBack.setOnClickListener {
            val intent =
                Intent(this@SubscriptionActivity, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
            finish()
        }

    }
    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        Log.e(TAG, "onProductPurchased: ")
        barberSubscribe( details!!)
    }

    override fun onPurchaseHistoryRestored() {
        Log.e(TAG, "onPurchaseHistoryRestored: ")
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Log.e(TAG, "onBillingError: $errorCode  error"+error?.message)
    }

    override fun onBillingInitialized() {
        Log.e(TAG, "onBillingInitialized: ")
        val premium = resources.getString(R.string.premium)
        //Toast.makeText(this,premium,Toast.LENGTH_LONG).show()
        purchaseTransactionDetails = bp!!.getSubscriptionTransactionDetails(premium)
        bp!!.loadOwnedPurchasesFromGoogle()
        val data = bp!!.getSubscriptionListingDetails(premium)
        if (data!=null){


        priceValue =data.priceValue
        binding.txtPrice.text = data.priceText
        binding.txtTitle.text = data.title
        }
        binding.rlSubscribed.setOnClickListener {
            if (bp!!.isSubscriptionUpdateSupported) {
                bp!!.subscribe(this, premium)
            } else {
                Toast.makeText(this, "Subscription updated is not supported", Toast.LENGTH_LONG).show()
            }
        }
        binding.txtBtn.setOnClickListener {
            if (bp!!.isSubscriptionUpdateSupported) {
                bp!!.purchase(this, premium)
            } else {
                Toast.makeText(this, "Subscription updated is not supported", Toast.LENGTH_LONG).show()
            }
        }
        if (hasSubscription()) {
            binding.txtBtn.text = "Already Subscribed"
            binding.txtBtn.isClickable=false
            binding.txtBtn.isEnabled=false
            barberSubscribe(purchaseTransactionDetails!!)
        } else {
            binding.txtBtn.text = "Subscribe"
        }
    }
    private fun hasSubscription(): Boolean {
        return if (purchaseTransactionDetails != null) {
            purchaseTransactionDetails!!.purchaseInfo != null
        } else false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!bp!!.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)

        }
    }

    private fun barberSubscribe(
        transactionDetails: TransactionDetails,
    ) {
        val prodData=transactionDetails.purchaseInfo.purchaseData
        val receiptData = JSONObject()
        receiptData.put("orderId", prodData.orderId)
        receiptData.put("packageName", prodData.packageName)
        receiptData.put("productId", prodData.productId)
        receiptData.put("purchaseTime", prodData.purchaseTime.time)
        receiptData.put("purchaseState", prodData.purchaseState)
        receiptData.put("purchaseToken", prodData.purchaseToken)
        receiptData.put("autoRenewing", prodData.autoRenewing)


        val params = HashMap<String, String>()
        params["transactionId"] = prodData.orderId
        params["receiptData"] = receiptData.toString()
        params["deviceType"] = "1"
        params["amount"] = priceValue.toString()
        progressHUD.show()
        APIHandler(
            applicationContext,
            Request.Method.POST,
            Constants.API.add_subscription,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    Log.e(TAG, "result : $result")
                    progressHUD.dismiss()
                    Toast.makeText(
                        this@SubscriptionActivity,
                        resources.getString(R.string.subscribe_success), Toast.LENGTH_LONG
                    ).show()

                    val subJsonObj = result.getJSONObject("result")

                    if (subJsonObj.has("is_subscribed")) {
                        sessionManager.isSubscribed = subJsonObj.getBoolean("is_subscribed")
                    }

                    if (subJsonObj.has("subscription_enddate")) {
                        sessionManager.subscription_enddate =
                            subJsonObj.getString("subscription_enddate")
                    }
                    sessionManager.isSubscribed = true


                    val user = sessionManager.userData?.let { Barber(it) }
                    if (user != null) {
                        app.currentUser = user
                    }

                    val intent = Intent(this@SubscriptionActivity, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
                    finish()
                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        applicationContext,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }

    override fun onBackPressed() {
        val intent = Intent(this@SubscriptionActivity, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
        finish()

    }

    companion object {
        const val TAG = "SubscriptionActivity"
    }

    override fun onDestroy() {
        if (bp != null) {
            bp!!.release()
        }
        super.onDestroy()
    }
}