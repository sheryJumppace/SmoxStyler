package com.ibcemobile.smoxstyler.data

import android.content.Context
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.ibcemobile.smoxstyler.App
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.Review
import com.kaopiz.kprogresshud.KProgressHUD

import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

/**
 * Repository module for handling data operations.
 */
class ReviewRepository: BaseObservable() {
    private var isFetchInProgress = false

    var totalCount:Int? = null
    var page:Int = 0

    var isUpdated:MutableLiveData<Boolean> = MutableLiveData()
    var reviews:ArrayList<Review> = ArrayList<Review>()

    fun getReview(id:Int): Review? {
        return reviews.find { it.id == id }
    }

    fun getReviews(): MutableLiveData<List<Review>> {
        return MutableLiveData(reviews)
    }

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: ReviewRepository? = null

        fun getInstance() =
                instance ?: synchronized(this) {
                    instance ?: ReviewRepository().also { instance = it }
                }
    }

    fun fetchList(context: Context, barberId:Int) {
        if(isFetchInProgress) return
        if(totalCount != null && totalCount!! <= reviews.size){
            return
        }
        isFetchInProgress = true

        val params = HashMap<String, String>()
        params["barber_id"] = barberId.toString()
        params["page"] = page.toString()

        val progressHUD = KProgressHUD(context)
        progressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
        progressHUD.show()

        APIHandler(
            context,
            Request.Method.GET,
            Constants.API.review,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    page += 1
                    isFetchInProgress = false

                    val data = result.getJSONObject("result")
                    if(data.has("total")){
                        totalCount = data.getInt("total")
                    }
                    val jsonArray = data.getJSONArray("reviews")
                    val items:ArrayList<Review> = ArrayList()
                    for (i in 0 until jsonArray.length()) {
                        val json = jsonArray.getJSONObject(i)
                        val review = Review(json)
                        items.add(review)
                    }
                    reviews.addAll(items)
                    isUpdated.postValue(true)

                }
                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    isFetchInProgress = false
                    Toast.makeText(
                        context.applicationContext,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    fun addReview(context: Context, comment: String, rating: Int, barberId: Int){
        val user = App.instance.currentUser
        val message = String.format("you got %d stars from %s", rating, user.firstName)

        val params = HashMap<String, String>()
        params["comment"] = comment
        params["rating"] = rating.toString()
        params["message"] = message
        params["barber_id"] = barberId.toString()

        for ((key, value) in params) {
            println("$key = $value")
        }

        val progressHUD = KProgressHUD(context)
        progressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
        progressHUD.show()
        progressHUD.show()

        APIHandler(
            context,
            Request.Method.POST,
            Constants.API.review,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()

                    val review = Review()
                    review.id = result.getInt("result")
                    review.barberId = barberId
                    review.userId = user.id
                    review.comment = comment
                    review.rating = rating.toDouble()
                    review.date = Date()
                    review.name = user.name
                    review.isMore.set(comment.length <= 200)

                    reviews.add(0, review)
                    isUpdated.postValue(true)

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
}
