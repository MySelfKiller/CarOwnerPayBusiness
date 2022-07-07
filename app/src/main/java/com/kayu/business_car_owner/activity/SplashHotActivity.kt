package com.kayu.business_car_owner.activity

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.kayu.business_car_owner.*
import com.kayu.business_car_owner.R
import com.kayu.utils.*

class SplashHotActivity : AppCompatActivity() {
    private var mSplashContainer: FrameLayout? = null

    private var splash_img: LinearLayout? = null

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
        Handler(Looper.getMainLooper()).postDelayed({ goToMainActivity() },1500*1)
    }

    /**
     * 跳转到主页面
     */
    private fun goToMainActivity() {
        finish()
    }
}