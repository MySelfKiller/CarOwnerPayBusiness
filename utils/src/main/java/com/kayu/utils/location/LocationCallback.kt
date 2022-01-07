package com.kayu.utils.location

import com.amap.api.location.AMapLocation

interface LocationCallback {
    fun onLocationChanged(location: AMapLocation)
}