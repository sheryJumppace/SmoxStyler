package com.ibcemobile.smoxstyler.fragment

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ibcemobile.smoxstyler.App
import com.ibcemobile.smoxstyler.MainActivity
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.SessionManager
import com.ibcemobile.smoxstyler.activities.BaseActivity
import com.ibcemobile.smoxstyler.activities.SignInActivity
import com.ibcemobile.smoxstyler.activities.SubscriptionActivity
import com.kaopiz.kprogresshud.KProgressHUD

open class BaseFragment : Fragment() {
    lateinit var progressHUD: KProgressHUD
    lateinit var sessionManager: SessionManager
    lateinit var app: App
    var isTablet: Boolean = false
    protected var activity: BaseActivity? = null
    var homeActivity: MainActivity? = null
    var signupSetupActivity: SignInActivity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isTablet = resources.getBoolean(R.bool.isTablet)
        app = App.instance
        context?.let {
            sessionManager = SessionManager.getInstance(it)
            progressHUD = KProgressHUD(it)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
        }

        activity = getActivity() as BaseActivity?
        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )
        requireActivity().window
            .setBackgroundDrawable(
                ColorDrawable(
                    ContextCompat.getColor(
                        requireActivity(),
                        android.R.color.white
                    )
                )
            )
        if (activity is MainActivity)
            homeActivity = (activity as MainActivity)
        else if (activity is SignInActivity)
            signupSetupActivity = (activity as SignInActivity)

    }

    fun cancelSubscription() {
        if (!sessionManager.isSubscribed){
            val intent = Intent(requireContext(), SubscriptionActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)

        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onPause() {
        super.onPause()
        progressHUD.dismiss()
    }

    open fun onBackPressed() {
        requireActivity().supportFragmentManager.popBackStack()
    }

}
