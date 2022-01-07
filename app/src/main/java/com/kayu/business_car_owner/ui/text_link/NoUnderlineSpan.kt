package com.kayu.business_car_owner.ui.text_link

import android.text.TextPaint
import android.text.style.UnderlineSpan

class NoUnderlineSpan : UnderlineSpan() {
    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = false
    }
}