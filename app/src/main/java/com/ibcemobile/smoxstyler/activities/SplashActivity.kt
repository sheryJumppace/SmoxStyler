package com.ibcemobile.smoxstyler.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.android.volley.Request
import com.ibcemobile.smoxstyler.MainActivity
import com.ibcemobile.smoxstyler.databinding.ActivitySplashBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class SplashActivity : BaseActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //FirebaseAnalytics.getInstance(this)
        Handler(mainLooper).postDelayed({
            val activity: Class<*> = if (sessionManager.userData == "") {
                SignInActivity::class.java
            } else {
                MainActivity::class.java
            }
            val intent = Intent(this@SplashActivity, activity)
            startActivity(intent)
            this@SplashActivity.finish()
        }, 2000)

        getHashKey()

    }


    /* private fun checkSubscription() {

         if (sessionManager.isSubscribed) {
             if (userID.isNotEmpty()) {
                 cancelSubscription("false", userID)
             }
         } else {
             if (userID.isNotEmpty()) {
                 cancelSubscription("true", userID)
             }
         }

     }*/

    private fun getHashKey() {
        try {
            val info = packageManager.getPackageInfo(
                applicationContext.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }
    }


    private fun cancelSubscription(isCancel: String, userID: String) {
        val params = HashMap<String, String>()
        params["userId"] = userID
        params["isCancel"] = isCancel
        APIHandler(
            applicationContext,
            Request.Method.POST,
            Constants.API.cancelSubscription,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    val error = result.getBoolean("error")
                    if (isCancel == "false") {
                        //openNextScreen()
                    } else {
                        val intent = Intent(this@SplashActivity, SubscriptionActivity::class.java)
                        startActivity(intent)
                        this@SplashActivity.finish()
                    }
                    if (!error) {
                        val subsCancelResult = result.getBoolean("result")
                        sessionManager.isSubscribed = subsCancelResult
                        Log.e("isSubscribed", sessionManager.isSubscribed.toString())
                    }
                }

                override fun onFail(error: String?) {
                    //progressHUD.dismiss()
                    Toast.makeText(
                        applicationContext,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            })
    }


    override fun onStart() {
        if (sessionManager.isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        super.onStart()

    }
}

