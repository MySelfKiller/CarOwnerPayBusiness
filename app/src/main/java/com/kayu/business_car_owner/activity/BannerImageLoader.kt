package com.kayu.business_car_owner.activity

import android.content.Context
import android.widget.ImageView
import com.kayu.business_car_owner.KWApplication
import com.youth.banner.loader.ImageLoader

class BannerImageLoader : ImageLoader() {
     override fun displayImage(context: Context, path: Any, imageView: ImageView) {
         (path as String?)?.let { KWApplication.instance.loadImg(it, imageView) }
    }
}