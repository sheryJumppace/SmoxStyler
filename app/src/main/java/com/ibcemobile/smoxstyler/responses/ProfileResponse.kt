package com.ibcemobile.smoxstyler.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProfileResponse(
    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: SmoxProUser
) {
    class SmoxProUser(
        @SerializedName("id")
        @Expose
        var id: String? = null,
        @SerializedName("business_name")
        @Expose
        var businessName: String? = null,

        @SerializedName("email")
        @Expose
        var email: String? = null,

        @SerializedName("phone")
        @Expose
        var phone: String? = null,

        @SerializedName("first_name")
        @Expose
        var firstName: String? = null,

        @SerializedName("last_name")
        @Expose
        var lastName: String? = null,

        @SerializedName("image")
        @Expose
        var image: String? = null,

        @SerializedName("logo")
        @Expose
        var logo: String? = null,

        @SerializedName("password")
        @Expose
        var password: String? = null,

        @SerializedName("api_key")
        @Expose
        var apiKey: String? = null,

        @SerializedName("user_push_id")
        @Expose
        var userPushId: String? = null,

        @SerializedName("device_type")
        @Expose
        var deviceType: String? = null,

        @SerializedName("account_social_type")
        @Expose
        var accountSocialType: String? = null,

        @SerializedName("account_type")
        @Expose
        var accountType: String? = null,

        @SerializedName("location")
        @Expose
        var location: String? = null,

        @SerializedName("address_one")
        @Expose
        var address_one: String? = null,

        @SerializedName("address_two")
        @Expose
        var address_two: String? = null,

        @SerializedName("city")
        @Expose
        var city: String? = null,

        @SerializedName("state")
        @Expose
        var state: String? = null,

        @SerializedName("zip_code")
        @Expose
        var zip_code: String? = null,

        @SerializedName("timezone")
        @Expose
        var timezone: Any? = null,

        @SerializedName("location_lat")
        @Expose
        var locationLat: String? = null,

        @SerializedName("location_lng")
        @Expose
        var locationLng: String? = null,

        @SerializedName("facebook")
        @Expose
        var facebook: String? = null,

        @SerializedName("twitter")
        @Expose
        var twitter: String? = null,

        @SerializedName("instagram")
        @Expose
        var instagram: String? = null,

        @SerializedName("linkedin")
        @Expose
        var linkedin: String? = null,

        @SerializedName("youtube")
        @Expose
        var youtube: String? = null,

        @SerializedName("start_time")
        @Expose
        var startTime: String? = null,

        @SerializedName("end_time")
        @Expose
        var endTime: String? = null,

        @SerializedName("is_activated")
        @Expose
        var isActivated: String? = null,

        @SerializedName("last_active")
        @Expose
        var lastActive: String? = null,

        @SerializedName("google_id")
        @Expose
        var googleId: Any? = null,

        @SerializedName("facebook_id")
        @Expose
        var facebookId: String? = null,

        @SerializedName("apple_id")
        @Expose
        var appleId: Any? = null,

        @SerializedName("online")
        @Expose
        var online: String? = null,

        @SerializedName("bio")
        @Expose
        var bio: String? = null,

        @SerializedName("cancellation_fee")
        @Expose
        var cancellationFee: String? = null,

        @SerializedName("stripe_public_key")
        @Expose
        var stripePublicKey: Any? = null,

        @SerializedName("stripe_secret_key")
        @Expose
        var stripeSecretKey: Any? = null,

        @SerializedName("stripe_customer_id")
        @Expose
        var stripeCustomerId: String? = null,

        @SerializedName("stripe_business_account")
        @Expose
        var stripeBusinessAccount: Any? = null,

        @SerializedName("id_global_setting")
        @Expose
        var idGlobalSetting: Any? = null,

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null,

        @SerializedName("whatsapp")
        @Expose
        var whatsapp: String? = null,

        @SerializedName("contact_email")
        @Expose
        var contactEmail: String? = null,

        @SerializedName("open_hours")
        @Expose
        var openHours: OpenHours? = null
    ) {
        class OpenHours(
            @SerializedName("id")
            @Expose
            var id: String? = null,

            @SerializedName("barber_id")
            @Expose
            var barberId: String? = null,

            @SerializedName("mo")
            @Expose
            var mo: String? = null,

            @SerializedName("tu")
            @Expose
            var tu: String? = null,

            @SerializedName("we")
            @Expose
            var we: String? = null,

            @SerializedName("th")
            @Expose
            var th: String? = null,

            @SerializedName("fr")
            @Expose
            var fr: String? = null,

            @SerializedName("sa")
            @Expose
            var sa: String? = null,

            @SerializedName("su")
            @Expose
            var su: String? = null,

            @SerializedName("created_at")
            @Expose
            var createdAt: String? = null
        )


    }
}
