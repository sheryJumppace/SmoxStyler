package com.ibcemobile.smoxstyler.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.android.volley.Request
import com.google.gson.JsonObject
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.activities.*
import com.ibcemobile.smoxstyler.adapter.loadCircleImage
import com.ibcemobile.smoxstyler.databinding.StylerProfileFragmentBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.Review
import com.ibcemobile.smoxstyler.viewmodels.UserViewModel
import org.json.JSONObject
import java.math.RoundingMode
import java.util.*

class StylerProfileFragment : BaseFragment() {
    private var binding: StylerProfileFragmentBinding? = null
    private var totalCount: Int = 0
    private lateinit var userViewModel:UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = StylerProfileFragmentBinding.inflate(inflater, container, false)
        userViewModel= ViewModelProvider(this).get(UserViewModel::class.java)
        loadCircleImage(
            binding!!.styler,
            Constants.downloadUrl(app.currentUser.logo),
            null,
            true
        )

        getBarberReview()

        checkStripeConfig()

        binding!!.rlStripeConfig.setOnClickListener {
            openOnBoarding()
        }

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding!!.cardStylerProfile.setOnClickListener {
            NavHostFragment.findNavController(requireParentFragment())
                .navigate(R.id.action_stylerProfileFragment_to_profileFragment)
        }
        binding!!.rlRating.setOnClickListener {
            val intent = Intent(requireActivity(), ReviewActivity::class.java)
            startActivity(intent)
        }

        binding!!.rlBookingPref.setOnClickListener {
            val intent = Intent(requireActivity(), BookingPreferenceActivity::class.java)
            startActivity(intent)
        }
        binding!!.rlHourOperation.setOnClickListener {
            val intent = Intent(requireActivity(), HourOperationActivity::class.java)
            startActivity(intent)
        }
        binding!!.rlSocialMedia.setOnClickListener {
            val intent = Intent(requireActivity(), SocialMediaActivity::class.java)
            startActivity(intent)
        }
        binding!!.rlSubscribed.setOnClickListener {
            val intent = Intent(requireActivity(), SubscriptionActivity::class.java)
            startActivity(intent)
        }

        binding!!.txtName.text = String.format(app.currentUser.firstName + " " + app.currentUser.lastName)
        binding!!.txtEmail.text = app.currentUser.email




        if (sessionManager.subscription_enddate == "") {
            binding!!.txtSubHeading5.text = getString(R.string.no_subscription)
            binding!!.txtSubHeading5.setTextColor(resources.getColor(R.color.light_yellow))

        } else {

            binding!!.txtSubHeading5.text =
                getString(R.string.subscription_add) + Constants.mFormatsTo(sessionManager.subscription_enddate!!)
            binding!!.txtSubHeading5.setTextColor(resources.getColor(R.color.light_yellow))
        }

    }



    private fun openOnBoarding() {
        val params = HashMap<String, String>()
        params["barber_id"] = app.currentUser.id.toString()
        progressHUD.show()

        APIHandler(
            requireContext(),
            Request.Method.POST,
            Constants.API.onboarding_link,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    val message: String = result.getString("message")
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    progressHUD.dismiss()
                    val json = result.getJSONObject("result")
                    val link = json.getString("url")
                    val bundle = Bundle()
                    bundle.putString("url", link)
                    NavHostFragment.findNavController(requireParentFragment())
                        .navigate(R.id.action_stylerProfileFragment_to_webViewFragment, bundle)

                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        context,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            })

    }

    private fun checkStripeConfig() {
        val params = HashMap<String, String>()
        params["barber_id"] = app.currentUser.id.toString()
        progressHUD.show()
        APIHandler(
            requireContext(),
            Request.Method.POST,
            Constants.API.onboarding_status,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    val json = result.getJSONObject("result").getJSONObject("capabilities")
                    if (json.getString("transfers") == "inactive" && json.getString("card_payments") == "inactive") {
                        binding!!.txtSubHeading6.text = getString(R.string.stripe_pending)
                        binding!!.txtSubHeading6.setTextColor(resources.getColor(R.color.light_yellow))
                        binding!!.rlStripeConfig.setOnClickListener {
                            openOnBoarding()
                        }
                    } else {
                        binding!!.txtSubHeading6.text = getString(R.string.stripe_complete)
                        binding!!.txtSubHeading6.setTextColor(resources.getColor(R.color.green))
                        binding!!.rlStripeConfig.isEnabled=false
                    }

                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        context,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            })

    }

    private fun getBarberReview() {
        val params = HashMap<String, String>()
        params["barber_id"] = app.currentUser.id.toString()
        for ((key, value) in params) {
            println("$key = $value")
        }
        progressHUD.show()

        APIHandler(
            requireActivity(),
            Request.Method.GET,
            Constants.API.review,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    val data = result.getJSONObject("result")
                    if (data.has("total")) {
                        totalCount = data.getInt("total")
                    }
                    var ratavg = 0.0
                    val jsonArray = data.getJSONArray("reviews")
                    if (jsonArray.length() > 0) {
                        val items: ArrayList<Review> = ArrayList()
                        for (i in 0 until jsonArray.length()) {
                            val json = jsonArray.getJSONObject(i)
                            val review = Review(json)
                            items.add(review)
                            ratavg += review.rating

                        }
                        ratavg /= totalCount
                        binding!!.rattingBar.rating =
                            ratavg.toBigDecimal().setScale(1, RoundingMode.UP).toFloat()
                        binding!!.ratingAvg.text =
                            ratavg.toBigDecimal().setScale(1, RoundingMode.UP).toDouble().toString()

                    }

                    binding!!.totalReview.text = " $totalCount Users Rating"

                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    /*Toast.makeText(
                        requireActivity(),
                        error, Toast.LENGTH_LONG
                    ).show()*/

                }
            })
    }


    override fun onBackPressed() {
        onBackPressed()
    }

}