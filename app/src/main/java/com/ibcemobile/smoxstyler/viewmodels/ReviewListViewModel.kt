package com.ibcemobile.smoxstyler.viewmodels

import android.content.Context
import androidx.lifecycle.*
import com.ibcemobile.smoxstyler.data.ReviewRepository
import com.ibcemobile.smoxstyler.model.Review


class ReviewListViewModel internal constructor(private val repository: ReviewRepository) : ViewModel() {
    private val isUpdated = repository.isUpdated

    var reviews: LiveData<List<Review>> = Transformations.switchMap(isUpdated) {
        repository.getReviews()
    }
    fun setStartPageIndex(page:Int){
        if(page == 0){
            repository.reviews.clear()
        }
        repository.page = page
    }
    fun getReviewsCount():Int{
        return repository.reviews.count()
    }
    fun addReview(context: Context, comment: String, rating: Int, barberId: Int){
        repository.addReview(context, comment, rating, barberId)
    }
    fun fetchList(context: Context, barberId:Int){
        repository.fetchList(context = context, barberId = barberId)
    }
}

class ReviewListViewModelFactory(
    private val repository: ReviewRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = ReviewListViewModel(repository) as T
}