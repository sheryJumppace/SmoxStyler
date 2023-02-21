package com.ibcemobile.smoxstyler.utils


import android.app.Activity
import android.util.Log
import com.amazonaws.event.ProgressEvent
import com.amazonaws.services.s3.model.PutObjectRequest
import com.ibcemobile.smoxstyler.manager.Constants

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class ImageUploadUtils {

    fun onUpload(activity: Activity, uploadUrl: String, uploadImages: UploadImages) {

        GlobalScope.launch {
            val uuid = UUID.randomUUID().toString().uppercase(Locale.ROOT)
            val imageKey = "media-photo-$uuid"
            val imageFile = File(uploadUrl)

            if (imageFile.exists()) {
                try {
                    val awsImageRequest =
                        PutObjectRequest(Constants.API.BUCKET_NAME, imageKey, imageFile)
                    awsImageRequest.setGeneralProgressListener { progressEvent ->
                        when (progressEvent.eventCode) {
                            ProgressEvent.COMPLETED_EVENT_CODE -> {
                                val imageUrl = (Constants.API.AWS_URL + imageKey)
                                activity.runOnUiThread(Runnable {
                                    uploadImages.upload(imageUrl)
                                })
                            }
                            ProgressEvent.FAILED_EVENT_CODE -> {
                                Log.e("TAG", "onUpload: image upload error")
                                uploadImages.onError()
                            }
                        }
                    }
                    AmazonUtil.getS3Client(activity).putObject(awsImageRequest)
                } catch (e: Exception) {

                }
            }
        }
    }
}