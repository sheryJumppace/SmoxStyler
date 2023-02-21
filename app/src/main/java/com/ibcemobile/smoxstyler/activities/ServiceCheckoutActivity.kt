package com.ibcemobile.smoxstyler.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.databinding.ActivityServiceCheckoutBinding
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.manager.Constants.API.PAY_MESSAGE
import com.ibcemobile.smoxstyler.manager.Constants.API.PAY_STATUS
import com.stripe.android.PaymentConfiguration
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.payments.paymentlauncher.PaymentLauncher
import com.stripe.android.payments.paymentlauncher.PaymentResult
import kotlinx.coroutines.launch

/***
 * 🦌🦌🦌🦌🦌🦌 Arun Android 🦌🦌🦌🦌🦌🦌🦌
 */

class ServiceCheckoutActivity : BaseActivity() {
    lateinit var binding: ActivityServiceCheckoutBinding
    private lateinit var paymentIntentClientSecret: String
    private lateinit var paymentLauncher: PaymentLauncher
    lateinit var timer: CountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_service_checkout)

        paymentIntentClientSecret = intent.getStringExtra(Constants.API.PAYMENT_INTENT).toString()
        PaymentConfiguration.init(this, Constants.KStripe.publishableKey)
        val paymentConfiguration = PaymentConfiguration.getInstance(this.applicationContext)
        paymentLauncher = PaymentLauncher.Companion.create(
            this,
            paymentConfiguration.publishableKey,
            paymentConfiguration.stripeAccountId
        ) { paymentResult ->
            if (progressHUD.isShowing)
                progressHUD.dismiss()
            val intent= Intent()
            when (paymentResult) {
                is PaymentResult.Completed -> {
                    "Completed!"
                    Log.e("PayByCardAct", "Completed ")
                    intent.putExtra(PAY_STATUS, "Success")
                    intent.putExtra(PAY_MESSAGE, "Your payment is success")
                    setResult(RESULT_OK, intent)
                    finish()
                }
                is PaymentResult.Canceled -> {
                    Log.e("PayByCardAct", ": Canceled")
                    intent.putExtra(PAY_STATUS, "Canceled")
                    intent.putExtra(PAY_MESSAGE, "Your payment is Canceled")
                    setResult(RESULT_CANCELED, intent)
                    finish()

                }
                is PaymentResult.Failed -> {
                    Log.e("PayByCardAct", ": Failed")
                    "Failed: " + paymentResult.throwable.message
                    intent.putExtra(PAY_STATUS, "Failed")
                    intent.putExtra(PAY_MESSAGE, paymentResult.throwable.message)
                    setResult(RESULT_CANCELED, intent)
                    finish()
                    "Failed: " + paymentResult.throwable.message
                }
            }
        }

        binding.txtPay.setOnClickListener {
            hideKeyboard()
            stopTimer()
            binding.cardWidget.paymentMethodCreateParams?.let { params ->
                progressHUD.show()
                val confirmParams = ConfirmPaymentIntentParams
                    .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret)
                lifecycleScope.launch {
                    paymentLauncher.confirm(confirmParams)
                }
            }
        }
        binding.cardWidget.requestFocus()
        //openKeyboard(this)
        startTimer()

    }

    private fun startTimer() {
        timer = object : CountDownTimer(120000, 1000) {
            override fun onFinish() {
                hideKeyboard()
                setResult(RESULT_CANCELED)
                finish()
            }

            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val min = (millisUntilFinished /60000) % 60
                val sec = (millisUntilFinished / 1000) % 60
                binding.waitTimer.text = String.format(getString(R.string.timer), min, sec)
            }
        }
        timer.start()
    }

    private fun stopTimer(){
        timer.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("payment card entry page", "onDestroy: ", )
    }
}