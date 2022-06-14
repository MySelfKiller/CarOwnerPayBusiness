package com.kayu.business_car_owner.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.hjq.toast.ToastUtils
import com.kongzue.dialog.v3.TipGifDialog
import com.kayu.business_car_owner.model.SystemParam
import com.kayu.business_car_owner.KWApplication
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kongzue.dialog.util.BaseDialog
import com.kayu.business_car_owner.model.UserBean
import com.kayu.utils.location.LocationManagerUtil
import com.kongzue.dialog.v3.MessageDialog
import com.kongzue.dialog.v3.CustomDialog
import com.kayu.utils.callback.ImageCallback
import com.kayu.business_car_owner.activity.login.LogOffActivity
import androidx.lifecycle.Observer
import com.kayu.business_car_owner.R
import com.kayu.business_car_owner.activity.login.LoginAutoActivity
import com.kayu.business_car_owner.activity.login.SetPasswordActivity
import com.kayu.utils.*

class SettingsActivity : BaseActivity() {
    private var app_version: TextView? = null
    private var app_new_version: TextView? = null
    private var sign_out: Button? = null
    private var useData: UserBean? = null
    private var user_name: TextView? = null
    private var mParamet: SystemParam? = null
    private var mainViewModel: MainViewModel? = null
    private var dialog: CustomDialog? = null
    private val itemCallback: ItemCallback = object : ItemCallback {
        override fun onItemCallback(position: Int, obj: Any?) {
            val systemParam1: SystemParam? = obj as SystemParam?
            if (null != systemParam1) {
                showPop()
            }
        }

        override fun onDetailCallBack(position: Int, obj: Any?) {}
    }
    private var user_agreement: TextView? = null
    private var user_privacy: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        mainViewModel = ViewModelProviders.of(this@SettingsActivity).get(
            MainViewModel::class.java
        )
        val sp: SharedPreferences =
            getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE)
        val jsonUser: String? = sp.getString(Constants.userInfo, "")
        useData = GsonHelper.fromJson(jsonUser, UserBean::class.java)

        //标题栏
//        LinearLayout title_lay = findViewById(R.id.title_lay);
//        title_lay.setBackgroundColor(getResources().getColor(R.color.background_gray));
        val title_name: TextView = findViewById(R.id.title_name_tv)
        title_name.setText("设置")
        findViewById<View>(R.id.title_back_btu).setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }

            override fun OnMoreErrorClick() {}
        })
        //        TextView back_tv = view.findViewById(R.id.title_back_tv);
//        back_tv.setText("我的");
        val version: String = AppUtil.getVersionName(this@SettingsActivity)
        user_name = findViewById(R.id.setting_user_name_tv)
        findViewById<View>(R.id.setting_log_off_tv).setOnClickListener(object :
            NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val intent = Intent(this@SettingsActivity, LogOffActivity::class.java)
                startActivity(intent)
            }

            override fun OnMoreErrorClick() {}
        })
        findViewById<View>(R.id.setting_password_setting_tv).setOnClickListener(object :
            NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val intent = Intent(this@SettingsActivity, SetPasswordActivity::class.java)
                startActivity(intent)
            }

            override fun OnMoreErrorClick() {}
        })
        app_version = findViewById(R.id.setting_app_version_tv)
        app_new_version = findViewById(R.id.setting_app_new_version_tv)
        app_new_version?.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
//                reqUpdate();
                showPop()
            }

            override fun OnMoreErrorClick() {}
        })
        app_version?.setText(version)
        initViewData()
        reqAppDown(null)
        sign_out = findViewById(R.id.setting_sign_out_tv)
        sign_out?.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                MessageDialog.show(
                    this@SettingsActivity,
                    getResources().getString(R.string.app_name),
                    "确定需要退出登录？",
                    "退出登录",
                    "取消"
                )
                    .setOkButton(object : OnDialogButtonClickListener {
                        public override fun onClick(baseDialog: BaseDialog, v: View): Boolean {
//                                SharedPreferences sp = getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE);
                            val editor: SharedPreferences.Editor = sp.edit()
                            editor.putBoolean(Constants.isLogin, false)
                            editor.putString(Constants.userInfo, "")
                            editor.apply()
                            editor.commit()
                            AppManager.appManager?.finishAllActivity()
                            LocationManagerUtil.self?.stopLocation()
                            //                                LocationManagerUtil.getSelf().destroyLocation();
                            startActivity(
                                Intent(
                                    this@SettingsActivity,
                                    LoginAutoActivity::class.java
                                )
                            )
                            finish()
                            return false
                        }
                    })
            }

            override fun OnMoreErrorClick() {}
        })
        user_agreement = findViewById(R.id.setting_user_agreement_tv)
        user_privacy = findViewById(R.id.setting_user_privacy_tv)
        mainViewModel!!.getParameter(this@SettingsActivity, 3)
            .observe(this@SettingsActivity, object : Observer<SystemParam?> {
                public override fun onChanged(systemParam: SystemParam?) {
//                WaitDialog.dismiss();
                    if (null != systemParam && systemParam.type == 3) {
                        val titles: Array<String>? =
                            systemParam.title?.split("@@".toRegex())?.toTypedArray()
                        val urls: Array<String>? =
                            systemParam.url?.split("@@".toRegex())?.toTypedArray()
                        user_agreement?.text = titles?.get(0)
                        user_privacy?.text = titles?.get(1)
                        user_agreement?.setOnClickListener(object : NoMoreClickListener() {
                            override fun OnMoreClick(view: View) {
                                jumpWeb(titles?.get(0)!!, urls?.get(0)!!)
                            }

                            override fun OnMoreErrorClick() {}
                        })
                        user_privacy?.setOnClickListener(object : NoMoreClickListener() {
                            override fun OnMoreClick(view: View) {
                                jumpWeb(titles?.get(1)!!, urls?.get(1)!!)
                            }

                            override fun OnMoreErrorClick() {}
                        })
                    }
                }
            })
    }

    //    public View onCreateView(@NonNull LayoutInflater inflater,
    //                             ViewGroup container, Bundle savedInstanceState) {
    //        mainViewModel = ViewModelProviders.of(SettingsFragment.this).get(MainViewModel.class);
    //        View root = inflater.inflate(R.layout.fragment_setting, container, false);
    //        final TextView textView = root.findViewById(R.id.text_notifications);
    //        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
    //            @Override
    //            public void onChanged(@Nullable String s) {
    //                textView.setText(s);
    //            }
    //        });
    //        return root;
    //    }
    //    @Override
    //    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    //        super.onViewCreated(view, savedInstanceState);
    //
    //    }
    private fun jumpWeb(title: String, url: String) {
        val intent: Intent = Intent(this@SettingsActivity, WebViewActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("from", title)
        startActivity(intent)
    }

    var qrcodeBitmap: Bitmap? = null
    private fun showPop() {
        var systemParam: SystemParam? = null
        if (null == mParamet) {
            reqAppDown(itemCallback)
        } else {
            systemParam = mParamet
            if (StringUtil.isEmpty(systemParam!!.url)) {
                MessageDialog.show(this@SettingsActivity, "提示", "地址链接错误", "重新获取", "取消")
                    .setCancelable(false)
                    .setOkButton(object : OnDialogButtonClickListener {
                        public override fun onClick(baseDialog: BaseDialog, v: View): Boolean {
                            baseDialog.doDismiss()
                            reqAppDown(itemCallback)
                            return false
                        }
                    })
                return
            }
            initPopView()
            KWApplication.instance.loadImg(systemParam.url, qrcode_iv, object : ImageCallback {
                public override fun onSuccess(resource: Bitmap) {
                    qrcodeBitmap = resource
                    creatPopWindow(popview)
                    showWindo()
                }

                public override fun onError() {}
            })
            save_btn!!.setOnClickListener(object : NoMoreClickListener() {
                override fun OnMoreClick(view: View) {
                    if (null == qrcodeBitmap) {
                        ToastUtils.show("保存图片不存在")
                        return
                    }
                    val fileName: String = "qr_" + System.currentTimeMillis() + ".jpg"
                    val isSaveSuccess: Boolean =
                        ImageUtil.saveImageToGallery(this@SettingsActivity, qrcodeBitmap!!, fileName)
                    if (isSaveSuccess) {
                        ToastUtils.show("保存成功")
                    } else {
                        ToastUtils.show("保存失败")
                    }
                }

                override fun OnMoreErrorClick() {}
            })
            if (TextUtils.isEmpty(systemParam.title)) {
                compay_tv1!!.setVisibility(View.GONE)
            } else {
                compay_tv1!!.setText(systemParam.title)
                compay_tv1!!.setVisibility(View.VISIBLE)
            }
        }
    }

    private fun initViewData() {
        if (null != useData) {
            user_name!!.setText(useData!!.username)
        }
    }

    private var popview: View? = null
    private var qrcode_iv: ImageView? = null
    private var save_btn: Button? = null
    private var compay_tv1: TextView? = null
    private fun initPopView() {
        val nullParent: ViewGroup? = null
        popview = getLayoutInflater().inflate(R.layout.qrcode_lay, nullParent)
        qrcode_iv = popview?.findViewById(R.id.shared_qrcode_iv)
        save_btn = popview?.findViewById(R.id.shared_call_btn)
        compay_tv1 = popview?.findViewById(R.id.shared_compay_tv1)
    }

    private fun creatPopWindow(view: View?) {
        dialog = CustomDialog.build(this@SettingsActivity, view).setCancelable(true)
    }

    private fun showWindo() {
        if (null != dialog && !dialog!!.isShow) dialog!!.show()
    }

    @SuppressLint("HandlerLeak")
    private fun reqAppDown(itemCallback: ItemCallback?) {
        if (null != itemCallback) {
            TipGifDialog.show(
                this@SettingsActivity,
                "稍等...",
                TipGifDialog.TYPE.OTHER,
                R.drawable.loading_gif
            )
        }
        mainViewModel!!.getParameter(this@SettingsActivity, 1)
            .observe(this@SettingsActivity, object : Observer<SystemParam?> {
                public override fun onChanged(systemParam: SystemParam?) {
                    if (null != itemCallback) {
                        TipGifDialog.dismiss()
                    }
                    if (null != systemParam && systemParam.type == 1) {
                        mParamet = systemParam
                        if (null != itemCallback) {
                            itemCallback.onItemCallback(0, mParamet)
                        }
                    }
                }
            })
    }
}