package com.kayu.business_car_owner.model

import com.google.gson.annotations.SerializedName

/**
 * Author by killer, Email xx@xx.com, Date on 2020/10/14.
 * PS: Not easy to write code, please indicate.
 */
class LoginInfo {
    /**
     * phone : 137******31
     * token : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.SiOhfhSwA3w-pDMt_2I9dIWOkR5ddBiei-wgySe9CGIPtDy_ZYDmJ3p3aEwEacpRj_GNCylVgvPTBwaAqA4aLLhfMXGfN6rveMg7WOxCGhMK8DPxkWu1cnsfcH2KlOubFDqW5lLBevp6ktxG2WNwOgke8wv0EMjU.YjW7JI9mYIUn1nn0JRFUDDyhprPkibN_0cW0UA5IWxU
     * loatLoginTime : 2020-10-13 16:27:16
     */
    @SerializedName("phone")
    var phone: String? = null

    @SerializedName("token")
    var token: String? = null

    @SerializedName("lastLoginTime")
    var lastLoginTime: String? = null
}