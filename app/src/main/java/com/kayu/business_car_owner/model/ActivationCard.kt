package com.kayu.business_car_owner.model

import com.google.gson.annotations.SerializedName

class ActivationCard {
    /**
     * no : 000000001
     * code : 218T84
     */
    @SerializedName("no")
    var no: String? = null

    @SerializedName("code")
    var code: String? = null
}