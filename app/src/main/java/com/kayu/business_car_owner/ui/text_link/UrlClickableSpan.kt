package com.kayu.business_car_owner.ui.text_link

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import com.kayu.business_car_owner.activity.WebViewActivity

class UrlClickableSpan(private val context: Context, private val url: String) : ClickableSpan() {
    override fun onClick(widget: View) {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val content_url = Uri.parse(url)
        intent.data = content_url
        context.startActivity(intent)
    }

    override fun updateDrawState(ds: TextPaint) {
//        super.updateDrawState(ds);
        ds.isUnderlineText = false
    }
}