package com.ibcemobile.smoxstyler.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.ibcemobile.smoxstyler.SessionManager
import com.ibcemobile.smoxstyler.adapter.ReviewAdapter
import com.ibcemobile.smoxstyler.databinding.ActivityReviewBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.Review
import com.ibcemobile.smoxstyler.viewmodels.ReviewListViewModel
import org.json.JSONObject
import java.math.RoundingMode
import java.util.*

class ReviewActivity : BaseActivity() {
    private lateinit var binding: ActivityReviewBinding
    private lateinit var viewModel: ReviewListViewModel
    private var barberId: Int = 0
    private var totalCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        barberId = intent.getIntExtra("barber_id", 0)

        val adapter = ReviewAdapter()

        binding.rvRating.layoutManager as LinearLayoutManager
        fetchList(adapter)


    }


    fun fetchList(adapter: ReviewAdapter) {
        sessionManager = SessionManager.getInstance(this)
        val params = HashMap<String, String>()
        params["barber_id"] = app.currentUser.id.toString()

        for ((key, value) in params) {
            println("$key = $value")
        }
        progressHUD.show()

        APIHandler(
            this,
            Request.Method.GET,
            Constants.API.review,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()


                    Log.e("AppointmentRepository ", "params :- $params  result====  $result")
                    val data = result.getJSONObject("result")
                    if (data.has("total")) {
                        totalCount = data.getInt("total")
                    }

                    val clean = data.getDouble("clean_rating")
                    val work = data.getDouble("work_rating")
                    val behave = data.getDouble("behave_rating")
                    binding.clRat.text = clean.toString()
                    binding.workRat.text = work.toString()
                    binding.behRat.text = behave.toString()

                    binding.totalReview.text = " $totalCount Users Rating"
                    //binding.rattingBar.rating = avgRating.toBigDecimal().setScale(1, RoundingMode.UP).toFloat()
                    //binding.ratingAvg.text = avgRating.toBigDecimal().setScale(1, RoundingMode.UP).toDouble().toString()

                    var ratavg = 0.0
                    val jsonArray = data.getJSONArray("reviews")
                    val items: ArrayList<Review> = ArrayList()

                    if (jsonArray.length() > 0) {

                    for (i in 0 until jsonArray.length()) {
                        val json = jsonArray.getJSONObject(i)
                        val review = Review(json)
                        items.add(review)
                        ratavg += review.rating

                    }
                    ratavg /= totalCount
                    binding.rattingBar.rating =
                        ratavg.toBigDecimal().setScale(1, RoundingMode.UP).toFloat()
                    binding.ratingAvg.text =
                        ratavg.toBigDecimal().setScale(1, RoundingMode.UP).toDouble().toString()


                    }
                    if (items.isNotEmpty()) {
                        adapter.submitList(items)
                        adapter.notifyDataSetChanged()
                        binding.tvNotFound.visibility = View.GONE
                        binding.rvRating.adapter = adapter


                    } else {
                        binding.tvNotFound.visibility = View.VISIBLE
                    }

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


}