package com.kayu.utils

import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton

class SMSCountDownTimer : CountDownTimer {
    private var mView: View

    /**
     * @param millisInFuture    The number of millis in the future from the call
     * to [.start] until the countdown is done and [.onFinish]
     * is called.
     * @param countDownInterval The interval along the way to receive
     * [.onTick] callbacks.
     */
    constructor(view: TextView, millisInFuture: Long, countDownInterval: Long) : super(
        millisInFuture,
        countDownInterval
    ) {
        mView = view
    }

    constructor(view: AppCompatButton, millisInFuture: Long, countDownInterval: Long) : super(
        millisInFuture,
        countDownInterval
    ) {
        mView = view
    }

    override fun onTick(millisUntilFinished: Long) {
//防止计时过程中重复点击
        mView.isClickable = false
        if (mView is TextView) {
            (mView as TextView).setText((millisUntilFinished / 1000).toString() + "秒")
        } else if (mView is AppCompatButton) {
            (mView as AppCompatButton).setText((millisUntilFinished / 1000).toString() + "秒")
        }
    }

    override fun onFinish() {
        //重新给Button设置文字
        if (mView is TextView) {
            (mView as TextView).text = "重新获取"
        } else if (mView is AppCompatButton) {
            (mView as AppCompatButton).text = "重新获取"
        }
        //设置可点击
        mView.isClickable = true
    }

    fun clear() {
        cancel()
        //重新给Button设置文字
        if (mView is TextView) {
            (mView as TextView).text = "重新获取"
        } else if (mView is AppCompatButton) {
            (mView as AppCompatButton).text = "重新获取"
        }
        //设置可点击
        mView.isClickable = true
    }
}