package com.ibcemobile.smoxstyler.activities
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.view.Gravity
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ibcemobile.smoxstyler.App
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.SessionManager
import com.ibcemobile.smoxstyler.fragment.BaseFragment
import com.ibcemobile.smoxstyler.utils.TransitionUtil
import com.ibcemobile.smoxstyler.utils.showSnackbar
import com.kaopiz.kprogresshud.KProgressHUD
import kotlin.system.exitProcess

private const val RESTART_DELAY = 200

open class BaseActivity : AppCompatActivity() {
    protected lateinit var progressHUD: KProgressHUD
    protected lateinit var sessionManager: SessionManager
    protected lateinit var app: App
    private var isTablet: Boolean = false
    var doubleBackToExitPressedOnce = false

    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        progressHUD = KProgressHUD(this)
        progressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
        isTablet = resources.getBoolean(R.bool.isTablet)


        sessionManager = SessionManager.getInstance(applicationContext)
        app = App.instance
        app.currentActivity = this::class.java
        when {
            !isInternetConnected() ->
                Toast.makeText(applicationContext, R.string.error_connection, Toast.LENGTH_LONG)
                    .show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        hideProgressDialog()
    }

    open fun showAlertDialog(
        title: String?, message: String?,
        onPositiveButtonClickListener: DialogInterface.OnClickListener?,
        positiveText: String,
        onNegativeButtonClickListener: DialogInterface.OnClickListener?,
        negativeText: String?
    ) {
        val builder = AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener)
        if (negativeText != null) {
            builder.setNegativeButton(negativeText, onNegativeButtonClickListener)
        }
        builder.show()
    }

    open fun showAlertDialog(
        message: String?
    ) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(message)
            // if the dialog is cancelable
            .setCancelable(false)
            .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()

            })

        val alert = dialogBuilder.create()
        alert.show()
    }

    protected fun showErrorSnackbar(
        @StringRes resId: Int,
        e: Exception?,
        clickListener: View.OnClickListener?
    ) {
        val rootView = window.decorView.findViewById<View>(android.R.id.content)
        rootView?.let {
            showSnackbar(it, resId, e, R.string.dlg_retry, clickListener)
        }
    }

    protected fun showProgressDialog(@StringRes messageId: Int) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this)
            progressDialog!!.isIndeterminate = true
            progressDialog!!.setCancelable(false)
            progressDialog!!.setCanceledOnTouchOutside(false)

            // Disable the back button
            val keyListener = DialogInterface.OnKeyListener { _,
                                                              keyCode,
                                                              _ ->
                keyCode == KeyEvent.KEYCODE_BACK
            }
            progressDialog!!.setOnKeyListener(keyListener)
        }
        progressDialog!!.setMessage(getString(messageId))
        progressDialog!!.show()
    }

    private fun hideProgressDialog() {
        progressDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    fun restartApp(context: Context) {
        // Application needs to restart when user declined some permissions at runtime
        val restartIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val intent =
            PendingIntent.getActivity(context, 0, restartIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + RESTART_DELAY, intent)
        exitProcess(0)
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun isInternetConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }


    fun replaceFragment(fragment: BaseFragment?) {
        replaceFragment(fragment, false, true, false)
    }

    fun replaceFragment(fragment: BaseFragment?, isAdd: Boolean) {
        replaceFragment(fragment, isAdd, true, false)
    }

    fun replaceFragment(fragment: BaseFragment?, isAdd: Boolean, addtobs: Boolean) {
        replaceFragment(fragment, isAdd, addtobs, false)
    }

    private fun replaceFragment(
        fragment: BaseFragment?,
        isAdd: Boolean,
        addtobs: Boolean,
        forceWithoutAnimation: Boolean
    ) {
        replaceFragment(fragment, isAdd, addtobs, forceWithoutAnimation, null)
    }

    open fun replaceFragment(
        fragment: BaseFragment?,
        isAdd: Boolean,
        addtobs: Boolean,
        forceWithoutAnimation: Boolean,
        transition: Transition?
    ) {
        //to do in child activity


        if (fragment == null)
            return
        val ft = supportFragmentManager.beginTransaction()
        val tag = fragment.javaClass.simpleName
        ft.setReorderingAllowed(true)
        if (!forceWithoutAnimation) {
            if (isAndroid5()) {
                if (transition != null) {
                    fragment.enterTransition = transition
                } else {
                    fragment.enterTransition = TransitionUtil.slide(Gravity.END)
                }
            } else {
                ft.setCustomAnimations(
                    R.anim.pull_in_right,
                    R.anim.push_out_left,
                    R.anim.pull_in_left,
                    R.anim.push_out_right
                )
            }
        }
        if (isAdd)
            ft.add(android.R.id.content, fragment, tag)
        else
            ft.replace(android.R.id.content, fragment, tag)
        if (addtobs)
            ft.addToBackStack(tag)
        ft.commitAllowingStateLoss()

    }

    private fun isAndroid5(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    @SuppressLint("RestrictedApi")
    override fun onLowMemory() {
        super.onLowMemory()
        try {
            for (fragment in supportFragmentManager.fragments)
                fragment.onLowMemory()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    open fun getCurrentFragment(): BaseFragment {
        return supportFragmentManager.findFragmentById(R.id.nav_barber) as BaseFragment
    }

    /*override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            getCurrentFragment().onBackPressed()
        } else {
            if (this is MainActivity) {
                if (doubleBackToExitPressedOnce) {
                    finish()
                }
                this.doubleBackToExitPressedOnce = true
                App.instance.snackBarExit(this)

                Handler(mainLooper).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
            } else {
                finish()
            }
        }
    }*/
    /*override fun onBackPressed() {
        val navigationController = nav_host_fragment.findNavController()
        if (navigationController.currentDestination?.id == R.id.firstFragment) {
            finish()
        } else if (navigationController.currentDestination?.id == R.id.secondFragment) {
            // do nothing
        } else {
            super.onBackPressed()
        }

    }*/


}