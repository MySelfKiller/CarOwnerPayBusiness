package com.kayu.utils.callback

import android.graphics.Bitmap

interface ImageCallback {
    fun onSuccess(resource: Bitmap)
    fun onError()
    class EmptyCallback : ImageCallback {
        override fun onSuccess(resource: Bitmap) {}
        override fun onError() {}
    }
}