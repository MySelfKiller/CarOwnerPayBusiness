package com.kayu.business_car_owner.activity

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.kayu.business_car_owner.*
import com.kayu.business_car_owner.R
import com.kayu.business_car_owner.activity.login.LoginAutoActivity
import com.kayu.utils.*

class SplashActivity : AppCompatActivity() {
    private var mSplashContainer: FrameLayout? = null

    //是否强制跳转到主页面
    private var mForceGoMain: Boolean = false

    private var splash_img: LinearLayout? = null
    private var isLogin: Boolean = false
    private var mainViewModel: MainViewModel? = null
    public override fun onConfigurationChanged(newConfig: Configuration) {
        //非默认值
        if (newConfig.fontScale != 1f) {
            getResources()
        }
        super.onConfigurationChanged(newConfig)
    }

    public override fun getResources(): Resources { //还原字体大小
        val res: Resources = super.getResources()
        //非默认值
        if (res.getConfiguration().fontScale != 1f) {
            val newConfig = Configuration()
            newConfig.setToDefaults() //设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics())
        }
        return res
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProviders.of(this@SplashActivity).get(MainViewModel::class.java)
        KWApplication.instance.displayWidth = ScreenUtils.getDisplayWidth(this)
        KWApplication.instance.displayHeight = ScreenUtils.getDisplayHeight(this)
        StatusBarUtil.setRootViewFitsSystemWindows(this, true)
        StatusBarUtil.setTranslucentStatus(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
        setContentView(R.layout.activity_splash)
        mSplashContainer = findViewById<View>(R.id.splash_container) as FrameLayout?
        splash_img = findViewById<View>(R.id.splash_img_lay) as LinearLayout?

        val sp: SharedPreferences =
            getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE)
        isLogin = sp.getBoolean(Constants.isLogin, false)
        Handler(Looper.getMainLooper()).postDelayed({ goToMainActivity() },1500*1)
    }
    override fun onResume() {
        //判断是否该跳转到主页面
        if (mForceGoMain) {
            goToMainActivity()
        }
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        mForceGoMain = true
    }

    /**
     * 跳转到主页面
     */
    private fun goToMainActivity() {
        val intent: Intent
        intent = if (isLogin) {
            Intent(this@SplashActivity, MainActivity::class.java)
        } else {
            Intent(this@SplashActivity, LoginAutoActivity::class.java)
        }
        val data: Uri? = getIntent().getData()
        if (data != null) {
            intent.setData(data)
        }
        startActivity(intent)
        finish()
    }
}