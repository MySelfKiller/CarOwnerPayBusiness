package com.kayu.business_car_owner.activity

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.hjq.toast.ToastUtils
import com.kayu.business_car_owner.model.SystemParam
import com.kayu.utils.callback.ImageCallback
import androidx.lifecycle.Observer
import com.kayu.business_car_owner.*
import com.kayu.business_car_owner.R
import com.kayu.utils.*
import com.kayu.utils.callback.Callback

class CustomerActivity : BaseActivity() {
    private var save_btn: TextView? = null
    private var mainViewModel: MainViewModel? = null
    private var qrcode_iv: ImageView? = null
    private var call_btn: Button? = null
    private var compay_tv2: TextView? = null
    private var compay_tv1: TextView? = null
    var qrcodeBitmap: Bitmap? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)
        mainViewModel = ViewModelProviders.of(this@CustomerActivity).get(
            MainViewModel::class.java
        )
        //标题栏
//        LinearLayout title_lay = findViewById(R.id.title_lay);
//        title_lay.setBackgroundColor(getResources().getColor(R.color.background_gray));
        val title_name: TextView = findViewById(R.id.title_name_tv)
        title_name.setText("客服")
        findViewById<View>(R.id.title_back_btu).setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }

            override fun OnMoreErrorClick() {}
        })
        val back_tv: TextView = findViewById(R.id.title_back_tv)
        back_tv.setText("我的")
        save_btn = findViewById(R.id.title_arrow_tv)
        qrcode_iv = findViewById(R.id.customer_qrcode_iv)
        compay_tv1 = findViewById(R.id.customer_compay_tv1)
        compay_tv2 = findViewById(R.id.customer_compay_tv2)
        call_btn = findViewById(R.id.customer_call_btn)
        mainViewModel!!.getCustomer(this@CustomerActivity)
            .observe(this@CustomerActivity, object : Observer<SystemParam?> {
                public override fun onChanged(systemParam: SystemParam?) {
                    if (null == systemParam) return
                    compay_tv1?.setText(if (StringUtil.isEmpty(systemParam.blank9)) "扫码添加客服微信" else systemParam.blank9)
                    if (StringUtil.isEmpty(systemParam.content)) {
                        compay_tv2?.setVisibility(View.GONE)
                        call_btn?.setVisibility(View.GONE)
                    } else {
                        call_btn?.setVisibility(View.VISIBLE)
                        call_btn?.setOnClickListener(object : NoMoreClickListener() {
                            override fun OnMoreClick(view: View) {
                                KWApplication.instance.permissionsCheck(
                                    this@CustomerActivity,
                                    arrayOf(Manifest.permission.CALL_PHONE),
                                    R.string.permiss_call_phone,
                                    object : Callback {
                                        public override fun onSuccess() {
                                            systemParam.content?.let {
                                                KWApplication.instance.callPhone(
                                                    this@CustomerActivity,
                                                    it
                                                )
                                            }
                                        }

                                        public override fun onError() {}
                                    })
                            }

                            override fun OnMoreErrorClick() {}
                        })
                        compay_tv2?.setVisibility(View.VISIBLE)
                        compay_tv2?.setText("客服电话：" + systemParam.content)
                    }
                    KWApplication.instance.loadImg(systemParam.url, qrcode_iv, object : ImageCallback {
                            override fun onSuccess(resource: Bitmap) {
                                qrcodeBitmap = resource
                            }

                            override fun onError() {}
                        })
                    save_btn?.visibility = View.VISIBLE
                    save_btn?.setOnClickListener(object : NoMoreClickListener() {
                        override fun OnMoreClick(view: View) {
                            KWApplication.instance.permissionsCheck(this@CustomerActivity,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                R.string.permiss_write_store,
                                object : Callback {
                                    public override fun onSuccess() {
                                        if (null == qrcodeBitmap) {
                                            ToastUtils.show("保存图片不存在")
                                            return
                                        }
                                        val fileName: String =
                                            "qr_" + System.currentTimeMillis() + ".jpg"
                                        val isSaveSuccess: Boolean = ImageUtil.saveImageToGallery(
                                            this@CustomerActivity,
                                            qrcodeBitmap!!,
                                            fileName
                                        )
                                        if (isSaveSuccess) {
                                            ToastUtils.show("保存成功")
                                        } else {
                                            ToastUtils.show("保存失败")
                                        }
                                    }

                                    public override fun onError() {}
                                })
                        }

                        override fun OnMoreErrorClick() {}
                    })
                }
            })
    }

}