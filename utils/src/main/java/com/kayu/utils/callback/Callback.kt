package com.kayu.utils.callback

interface Callback {
    fun onSuccess()
    fun onError()
    class EmptyCallback : Callback {
        override fun onSuccess() {}
        override fun onError() {}
    }
}