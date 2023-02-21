package com.ibcemobile.smoxstyler.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.android.volley.Request
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.ibcemobile.smoxstyler.MainActivity
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.Barber
import com.ibcemobile.smoxstyler.model.type.ApplicationStatus
import com.ibcemobile.smoxstyler.model.type.UserType
import org.json.JSONException
import org.json.JSONObject


open class BaseLoginActivity : BaseActivity(), GoogleApiClient.OnConnectionFailedListener {
    private lateinit var userType: UserType
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var callbackManager: CallbackManager
    private val RC_SIGN_IN = 7
    private val FACEBOOK_REQUEST = 64206
    var keep_email: String = ""
    var keep_password: String = ""
    private lateinit var context: Context
    private var isFromSignup: Boolean = false
    private var userID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        FacebookSdk.fullyInitialize()
        AppEventsLogger.activateApp(application)
        callbackManager = CallbackManager.Factory.create()
        context = applicationContext
        ///////////Google Login Initialization/////////////
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this@BaseLoginActivity, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        initializeFacebook()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
            handleSignInResult(result!!)
        } else if (requestCode == FACEBOOK_REQUEST && resultCode == Activity.RESULT_OK) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
        // Log.e("In OnActivityResult:", "$requestCode,$resultCode")
    }

    override fun onStart() {
        super.onStart()
        mGoogleApiClient.connect()
    }

    public override fun onDestroy() {
        super.onDestroy()
        mGoogleApiClient.disconnect()
    }

    override fun onStop() {
        super.onStop()
        if (mGoogleApiClient.isConnected) {
            mGoogleApiClient.disconnect()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mGoogleApiClient.isConnected) {
            mGoogleApiClient.connect()
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(
            applicationContext,
            p0.errorMessage, Toast.LENGTH_LONG
        ).show()
    }

    fun loginWithFacebook(type: UserType, fromSignup: Boolean) {
        userType = type
        isFromSignup = fromSignup
        LoginManager.getInstance()
            .logInWithReadPermissions(this, listOf("public_profile", "email", "user_friends"))

    }

    fun loginWithGoogle(type: UserType, fromSignup: Boolean) {
        userType = type
        isFromSignup = fromSignup
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    /////////Google plus result////////////////
    private fun handleSignInResult(result: GoogleSignInResult) {
        Log.e("", "handleSignInResult:$result")
        if (result.isSuccess) {
            // Signed in successfully, show authenticated UI.
            val acct = result.signInAccount
            val socialID = acct!!.id
            var email = ""
            var firstName = ""
            var lastName = ""
            if (acct.givenName != null && acct.givenName != "") {
                firstName = acct.givenName.toString()
            }
            if (acct.familyName != null && acct.familyName != "") {
                lastName = acct.familyName.toString()
            }
            var imageUrl = ""
            if (acct.photoUrl != null && acct.photoUrl.toString() != "") {
                imageUrl = acct.photoUrl.toString()
            }
            if (acct.email != null && acct.email != "") {
                email = acct.email.toString()
            }

            loginWithSocialAccount(
                "google",
                email,
                firstName,
                lastName,
                imageUrl,
                socialID
            )
        }
    }

    //==========Facebook initialize
    private fun initializeFacebook() {
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {

                    val accessToken = AccessToken.getCurrentAccessToken()
                    if (accessToken != null) {
                        val request = GraphRequest.newMeRequest(
                            accessToken
                        ) { _, response ->
                            if (response != null) {
                                Log.e("TAG", "onSuccess: $response")
                                try {
                                    val data = response.jsonObject
                                    var socialID = ""
                                    if (data.has("id")) {
                                        socialID = data.getString("id")
                                    }
                                    var email = ""
                                    if (data.has("email")) {
                                        email = data.getString("email")
                                    }
                                    var firstName = ""
                                    if (data.has("first_name")) {
                                        firstName = data.getString("first_name")
                                    }
                                    var lastName = ""
                                    if (data.has("last_name")) {
                                        lastName = data.getString("last_name")
                                    }
                                    val image =
                                        "http://graph.facebook.com/$socialID/picture?type=large"
                                    loginWithSocialAccount(
                                        "facebook",
                                        email,
                                        firstName,
                                        lastName,
                                        image,
                                        socialID
                                    )
                                } catch (e: Exception) {
                                    //e.printStackTrace()
                                    Toast.makeText(
                                        applicationContext,
                                        "Something went wrong.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            }
                        }
                        val parameters = Bundle()
                        parameters.putString(
                            "fields",
                            "id, name, first_name, last_name, picture.type(large), email, link"
                        )
                        request.parameters = parameters
                        request.executeAsync()
                    }
                }

                override fun onCancel() {
                    // App code
                    Toast.makeText(
                        this@BaseLoginActivity,
                        "Need to Add App to Play store console",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(exception: FacebookException) {
                    // App code

                    Log.e("-FBException-", exception.localizedMessage!!)
                }
            })
    }

    private fun loginWithSocialAccount(
        type: String,
        email: String?,
        firstName: String?,
        lastName: String?,
        image: String?,
        socialID: String?
    ) {
        val params = HashMap<String, String>()
        params["type"] = type
        params["email"] = email ?: ""
        params["first_name"] = firstName ?: ""
        params["last_name"] = lastName ?: ""
        params["image_url"] = image ?: ""
        params["link"] = ""
        params["social_id"] = socialID ?: ""
        params["user_type"] = UserType.Barber.name

        progressHUD.show()
        APIHandler(
            applicationContext,
            Request.Method.POST,
            Constants.API.loginWithSocial,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    didCompleteLogin(result, true)
                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                }
            })
    }

    fun loginWithEmail(email: String, password: String, userType: String) {
        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = password
        params["type"] = userType
        progressHUD.show()

        APIHandler(
            applicationContext,
            Request.Method.POST,
            Constants.API.login,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    didCompleteLogin(result, true)
                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        applicationContext,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            })
    }


    fun signUpWithEmail(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        password: String,
        type: UserType
    ) {
        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = password
        params["phone_number"] = phone
        params["first_name"] = firstName
        params["last_name"] = lastName
        params["type"] = type.name
        progressHUD.show()
        APIHandler(
            applicationContext,
            Request.Method.POST,
            Constants.API.signUp,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    didCompleteLogin(result, true)
                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        applicationContext,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    fun didCompleteLogin(result: JSONObject, isSignup: Boolean) {
        val error: Boolean
        try {
            val gso: GoogleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)
            googleSignInClient.signOut()

            error = result.getBoolean("error")
            // Check for error node in json
            if (!error) {
                Log.e("TAG", "Login Result: $result")
                if (result.has("result")) {
                    val json = result.getJSONObject("result")
                    if (json.getString("account_type").equals("Barber", true)) {

                        if (json.has("api_key")) {
                            val apiKey = json.getString("api_key")
                            sessionManager.apiKey = apiKey
                            sessionManager.accountType = json.getString("account_type").toString()

                            val user = Barber(json)
                            app.currentUser = user
                            sessionManager.userData = app.currentUser.getJsonString()
                            sessionManager.Sp_publishableKey = user.stripe_public_key
                            sessionManager.Sp_privateKey = user.stripe_secret_key


                            userID = app.currentUser.id.toString()
                            sessionManager.userId = app.currentUser.id

                            sessionManager.customerStripeId = user.stripe_customer_id
                        }

                        if (json.has("is_subscribed")) {
                            sessionManager.isSubscribed = json.getBoolean("is_subscribed")
                        }

                        if (json.has("sell_products")) {
                            sessionManager.sellProducts = json.getBoolean("sell_products")
                        }

                        if (json.has("subscription_enddate")) {
                            sessionManager.subscription_enddate =
                                json.getString("subscription_enddate")
                        }

                        if (json.has("open_hours") && !json.isNull("open_hours")) {
                            sessionManager.userDataOpenDays =
                                json.getJSONObject("open_hours").toString()
                            // Log.e("Open days Data:- ", json.getJSONObject("open_hours").toString())
                        }

                        if (isInternetConnected()) {
                            nextToBarberMainScreen()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                R.string.error_connection,
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }

                    } else {
                        Toast.makeText(
                            this,
                            "You are a customer,Please login in Customer app",
                            Toast.LENGTH_LONG
                        ).show()

                        val intent = Intent(this@BaseLoginActivity, SignInActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

                if (sessionManager.loginKeep && (!TextUtils.isEmpty(keep_email) && !TextUtils.isEmpty(
                        keep_password
                    ))
                ) {
                    sessionManager.keep_me_email = keep_email
                    sessionManager.keep_me_password = keep_password
                } else {
                    sessionManager.keep_me_email = ""
                    sessionManager.keep_me_password = ""
                }

                app.applicationStatus = ApplicationStatus.Login


            } else {
                progressHUD.dismiss()

               /* val intent = Intent(this@BaseLoginActivity, SignInActivity::class.java)
                startActivity(intent)
                finish()*/
                // Error in login. Get the error message
                val errorMsg = result.getString("message")
                Toast.makeText(
                    applicationContext,
                    errorMsg, Toast.LENGTH_LONG
                ).show()


            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(
                applicationContext,
                e.localizedMessage, Toast.LENGTH_LONG
            ).show()
        }
    }


    fun keepMeLogin(keep_email: String, keep_password: String) {
        this.keep_email = keep_email
        this.keep_password = keep_password
    }

    fun cancelSubscription(isCancel: String, userID: String) {
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
                    progressHUD.dismiss()
                    val error = result.getBoolean("error")
                    if (!error) {
                        val subsCancelResult = result.getBoolean("result")
                        sessionManager.isSubscribed = !subsCancelResult
                    }
                    nextToBarberMainScreen()
                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        applicationContext,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun nextToBarberMainScreen() {
        val intent = Intent(this@BaseLoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}
