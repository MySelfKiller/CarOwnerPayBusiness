package com.kayu.utils

interface ItemCallback {
    fun onItemCallback(position: Int, obj: Any?)
    fun onDetailCallBack(position: Int, obj: Any?)
}