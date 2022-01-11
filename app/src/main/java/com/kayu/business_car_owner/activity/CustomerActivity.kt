package com.kayu.business_car_owner.activity

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.hjq.toast.ToastUtils
import com.kayu.business_car_owner.model.SystemParam
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kongzue.dialog.util.BaseDialog
import com.kayu.utils.permission.EasyPermissions.DialogCallback
import com.kongzue.dialog.v3.MessageDialog
import com.kayu.utils.permission.EasyPermissions
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
                                permissionsCheck(
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
                            permissionsCheck(
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

    fun permissionsCheck(perms: Array<String>, resId: Int, callback: Callback) {
//        String[] perms = {Manifest.permission.CAMERA};
        performCodeWithPermission(
            1,
            Constants.RC_PERMISSION_PERMISSION_FRAGMENT,
            perms,
            object : PermissionCallback {
                override fun hasPermission(allPerms: List<Array<String>>) {
                    callback.onSuccess()
                }

                override fun noPermission(
                    deniedPerms: List<String>?,
                    grantedPerms: List<String?>?,
                    hasPermanentlyDenied: Boolean?
                ) {
                    EasyPermissions.goSettingsPermissions(
                        this@CustomerActivity,
                        1,
                        Constants.RC_PERMISSION_PERMISSION_FRAGMENT,
                        Constants.RC_PERMISSION_BASE
                    )
                }

                public override fun showDialog(dialogType: Int, callback: DialogCallback) {
                    val dialog: MessageDialog =
                        MessageDialog.build((this@CustomerActivity as AppCompatActivity?)!!)
                    dialog.setTitle("需要获取以下权限")
                    dialog.setMessage(getString(resId))
                    dialog.setOkButton("下一步", object : OnDialogButtonClickListener {
                        public override fun onClick(baseDialog: BaseDialog, v: View): Boolean {
                            callback.onGranted()
                            return false
                        }
                    })
                    dialog.setCancelable(false)
                    dialog.show()
                }
            })
    }
}