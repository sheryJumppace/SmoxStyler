package com.ibcemobile.smoxstyler

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import com.ibcemobile.smoxstyler.activities.BarberCartActivity
import com.ibcemobile.smoxstyler.activities.BaseActivity
import com.ibcemobile.smoxstyler.activities.OrderActivity
import com.ibcemobile.smoxstyler.activities.SignInActivity
import com.ibcemobile.smoxstyler.adapter.NavItemAdapter
import com.ibcemobile.smoxstyler.adapter.loadCircleImage
import com.ibcemobile.smoxstyler.databinding.ActivityMainBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.Barber
import com.ibcemobile.smoxstyler.utils.ANDROID
import com.ibcemobile.smoxstyler.utils.PushNotificationUtils
import com.ibcemobile.smoxstyler.viewmodels.UserViewModel
import org.json.JSONObject


class MainActivity : BaseActivity(), NavItemAdapter.MyItemClickListener {
    companion object {
        const val TAG: String = "MainActivity"
    }

    private lateinit var navHostFragment: Fragment
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        PushNotificationUtils(this).createNotificationChannel()

        val sessionManager = SessionManager.getInstance(applicationContext)
        binding.rvItem.layoutManager = LinearLayoutManager(this)
        binding.rvItem.adapter = NavItemAdapter(this)
        binding.drawerMenu.setOnClickListener {
            binding.drawerLayout.open()
        }
        val imgClose: ImageView = findViewById(R.id.imgClose)
        imgClose.setOnClickListener {
            binding.drawerLayout.close()
        }
        if (sessionManager.loginKeep && sessionManager.apiKey != null && sessionManager.apiKey?.length!! > 0) {
            val user = sessionManager.userData?.let { Barber(it) }
            if (user != null) {
                app.currentUser = user
            }
            if (user?.id!! < 1) {
                return
            }
        }
        binding.llLogout.setOnClickListener {
            showConfirmLogoutDialog()
        }

        binding.navHeader.txtVersion.text = "Styler v" + BuildConfig.VERSION_NAME
        binding.navHeader.txtFullName.text =
            app.currentUser.firstName + " " + app.currentUser.lastName
        binding.navHeader.txtEmail.text = app.currentUser.email

        loadCircleImage(
            binding.navHeader.styler, Constants.downloadUrl(app.currentUser.image), null, true
        )
        toolTips()
        darkTheme()
        updateDeviceToken()

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        navController = (navHostFragment as NavHostFragment).navController
        navController.currentDestination
    }

    private fun toolTips() {
        if (sessionManager.isFirstTimeHint) {
            var count = 0
            binding.imgHint.setOnClickListener {
                sessionManager.isFirstTimeHint = false
                count++
                when (count) {
                    0 -> {
                        binding.imgHint.setImageResource(R.drawable.hamburger_image)
                    }
                    1 -> {
                        binding.imgHint.setImageResource(R.drawable.calendar_image)
                    }
                    2 -> {
                        binding.imgHint.setImageResource(R.drawable.funds_image)
                    }
                    3 -> {
                        binding.imgHint.setImageResource(R.drawable.book_appointment_image)
                    }
                    4 -> {
                        binding.imgHint.setImageResource(R.drawable.messages_image)
                    }
                    5 -> {
                        binding.imgHint.setImageResource(R.drawable.profile_image)
                    }
                    6 -> {
                        binding.imgHint.visibility = View.GONE
                    }
                    else -> {
                        binding.imgHint.visibility = View.GONE
                    }
                }
            }
        } else {
            binding.imgHint.visibility = View.GONE
        }
    }

    private fun darkTheme() {
        binding.switchDarkTheme.setOnCheckedChangeListener { _, b ->
            sessionManager.isDarkMode = b
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            if (b) {
                binding.switchDarkTheme.isChecked = true
                binding.switchDarkTheme.text = getString(R.string.dark_theme)
                val handler = Handler(Looper.myLooper()!!)
                handler.postDelayed({
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }, 1000)
            } else {
                binding.switchDarkTheme.isChecked = false
                binding.switchDarkTheme.text = getString(R.string.light_theme)
                val handler = Handler(Looper.myLooper()!!)
                handler.postDelayed({
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }, 1000)
            }
        }
    }

    private fun logout() {
        App.instance.currentPage = 0
        val sessionManager = SessionManager.getInstance(applicationContext)
        sessionManager.userData = ""
        sessionManager.apiKey = ""
        sessionManager.isSubscribed = false
        sessionManager.subscription_enddate = ""
        sessionManager.Sp_publishableKey = ""
        sessionManager.Sp_privateKey = ""
        sessionManager.isFirstTime = true
        val intent = Intent(this@MainActivity, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun updateDeviceToken() {

        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (it != null && it.result != null && it.isComplete) {
                    SessionManager.getInstance(applicationContext).deviceToken =
                        it.result.toString()
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("device_token", it.result.toString())
                    jsonObject.addProperty("type", ANDROID)
                    userViewModel.updateProfile(
                        this, progressHUD, Constants.API.user_device, jsonObject
                    )
                }

            }
        } catch (e: Exception) {
            println(e.localizedMessage)
        }

    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            when (navController.currentDestination?.id) {
                R.id.barberMainFragment -> {
                    if (doubleBackToExitPressedOnce) {
                        finish()
                    }
                    this.doubleBackToExitPressedOnce = true
                    App.instance.snackBarExit(this)

                    Handler(mainLooper).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
                }
                R.id.stylerProfileFragment -> {
                    navController.popBackStack()
                    return
                }
                else -> {
                    super.onBackPressed()
                }
            }
        }

    }

    override fun clicked(id: Int) {
        binding.drawerLayout.closeDrawers()
        when (id) {
            0 -> {
                binding.txtTitleBar.text = "Styler Profile"
                navController.navigate(R.id.stylerProfileFragment)
            }
            1 -> {
                binding.txtTitleBar.text = "Products"
                navController.navigate(R.id.productsFragment)

            }
            2 -> {
                val intent = Intent(this@MainActivity, OrderActivity::class.java)
                startActivity(intent)

            }
            3 -> {
                val intent = Intent(this@MainActivity, BarberCartActivity::class.java)
                startActivity(intent)
            }
            4 -> {
                binding.txtTitleBar.text = "About Us"
                navController.navigate(R.id.aboutUsFragment)
            }
            5 -> {
                binding.txtTitleBar.text = "Contact Us"
                navController.navigate(R.id.contactUsFragment)
            }
        }
    }

    private fun showConfirmLogoutDialog() {
        val builder = AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
        builder.setTitle("Confirm Log out")
        builder.setMessage("Are you sure you want to Log out?")
        builder.setPositiveButton("LOG OUT") { _, _ ->
            logoutCall()
        }
        builder.setNegativeButton("CANCEL", null)
        builder.show()
    }

    private fun logoutCall() {
        val params = HashMap<String, String>()
        progressHUD.show()
        APIHandler(this,
            Request.Method.GET,
            Constants.API.logout,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    logout()
                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        this@MainActivity, error, Toast.LENGTH_LONG
                    ).show()
                }

            })

    }


    override fun onResume() {
        super.onResume()
        val appUpdater = AppUpdater(this)
        appUpdater.setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
        appUpdater.start()
    }


}