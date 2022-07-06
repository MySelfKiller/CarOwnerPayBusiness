package com.kayu.business_car_owner.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.daimajia.numberprogressbar.NumberProgressBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hjq.toast.ToastUtils
import com.kayu.business_car_owner.*
import com.kayu.business_car_owner.R
import com.kayu.business_car_owner.http.*
import com.kayu.business_car_owner.model.SystemParam
import com.kayu.business_car_owner.ui.HomeFragmentNew
import com.kayu.business_car_owner.ui.PersonalFragment
import com.kayu.business_car_owner.ui.ShopFragment
import com.kayu.business_car_owner.ui.adapter.BottomNavigationViewHelper
import com.kayu.business_car_owner.update.UpdateCallBack
import com.kayu.business_car_owner.update.UpdateInfo
import com.kayu.business_car_owner.update.UpdateInfoParse
import com.kayu.utils.*
import com.kayu.utils.callback.Callback
import com.kayu.utils.callback.ImageCallback
import com.kayu.utils.location.LocationManagerUtil
import com.kayu.utils.permission.EasyPermissions
import com.kayu.utils.permission.EasyPermissions.DialogCallback
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kongzue.dialog.util.BaseDialog
import com.kongzue.dialog.v3.CustomDialog
import com.kongzue.dialog.v3.MessageDialog
import com.kongzue.dialog.v3.TipGifDialog
import com.maning.updatelibrary.InstallUtils
import com.maning.updatelibrary.InstallUtils.*
import java.io.File

class MainActivity: BaseActivity(), OnPageChangeListener {
    private var view_pager: ViewPager? = null
    private var mViewModel: MainViewModel? = null
    private var apkDownloadPath: String? = null
    private var downloadCallBack: DownloadCallBack? = null
    private var progressDialog: MessageDialog? = null
    private var progressbar: NumberProgressBar? = null
    private var lastSelectItemid: Int = 0
    private val mOnNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
        object : BottomNavigationView.OnNavigationItemSelectedListener {
            public override fun onNavigationItemSelected(item: MenuItem): Boolean {
//            LogUtil.e("hm","NavigationItemSelected position="+item.getItemId());
                if (lastSelectItemid == item.getItemId()) {
                    return true
                }
                lastSelectItemid = item.getItemId()
                when (item.getItemId()) {
                    R.id.navigation_home -> view_pager!!.currentItem = 0
                    R.id.navigation_shop -> view_pager!!.currentItem = 1
                    R.id.navigation_personal -> view_pager!!.currentItem = 2
                }
                return true
            }
        }
    private var navigation: BottomNavigationView? = null
    private var customDialog: CustomDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //沉浸式代码配置
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, true)
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this)
        setContentView(R.layout.activity_main)
        navigation = findViewById(R.id.nav_view)
        view_pager = findViewById<View>(R.id.view_pager) as ViewPager?
        mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        permissionsCheck()

        //预加载首页数据
        mViewModel!!.getUserRole(this@MainActivity)
            .observe(this@MainActivity, object : Observer<Int?> {
                public override fun onChanged(integer: Int?) {
                    KWApplication.instance.userRole = (integer)!!
                }
            })
        mViewModel!!.getParamSelect(this@MainActivity)
        mViewModel!!.getParamWash(this@MainActivity)
    }

    private val fragments: List<Fragment>
        get() {
            val list: MutableList<Fragment> = ArrayList()
            list.add(HomeFragmentNew())
            list.add(ShopFragment())
            list.add(PersonalFragment())
            return list
        }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RC_PERMISSION_BASE) {
            permissionsCheck()
        }
        val fragmentManager: FragmentManager = getSupportFragmentManager()
        for (fragment: Fragment? in fragmentManager.getFragments()) {
            processAllFragment(fragment, requestCode, resultCode, data)
        }
    }

    // 遍历所有fragment
    private fun processAllFragment(
        fragment: Fragment?,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (fragment == null) {
            return
        }
        for (childFragment: Fragment in fragment.getChildFragmentManager().getFragments()) {
            processAllFragment(childFragment, requestCode, resultCode, data)
        }
        if (!fragment.isAdded() || fragment.isDetached()) {
            return
        }
        fragment.onActivityResult(requestCode, resultCode, data)
    }

    //记录用户首次点击返回键的时间
    private var firstTime: Long = 0
    public override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount <= 0) { //这里是取出我们返回栈存在Fragment的个数
            val secondTime: Long = System.currentTimeMillis()
            if (secondTime - firstTime > 2000) {
                ToastUtils.show("再按一次退出应用")
                firstTime = secondTime
                return
            } else {
                appManager!!.finishAllActivity()
                LocationManagerUtil.self?.stopLocation()
                LocationManagerUtil.self?.destroyLocation()
                finish()
                System.exit(0)
            }
        } else { //取出我们返回栈保存的Fragment,这里会从栈顶开始弹栈
            getSupportFragmentManager().popBackStack()
        }
    }

    private fun showPermissTipsDialog() {
        MessageDialog.show(
            this@MainActivity,
            "需要开启定位服务",
            getString(R.string.permiss_location),
            "下一步",
            ""
        ).setCancelable(false)
            .setOnOkButtonClickListener(object : OnDialogButtonClickListener {
                public override fun onClick(baseDialog: BaseDialog, v: View): Boolean {
                    baseDialog.doDismiss()
                    //                        permissionsCheck();
                    return true
                }
            })
    }

    fun permissionsCheck() {
//        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        val perms: Array<String> = needPermissions
        performCodeWithPermission(
            1,
            Constants.RC_PERMISSION_PERMISSION_FRAGMENT,
            perms,
            object : PermissionCallback {
                override fun hasPermission(allPerms: List<Array<String>>) {
                    reqUpdate()
                    if (!mHasShowOnce1) reqActivityData(38)
                    val navigationAdapter =
                        NavigationAdapter(supportFragmentManager, fragments)
                    view_pager!!.addOnPageChangeListener(this@MainActivity)
                    view_pager!!.offscreenPageLimit = 3
                    view_pager!!.setAdapter(navigationAdapter)
                    navigation?.let { BottomNavigationViewHelper.disableShiftMode(it) }
                    navigation!!.itemIconTintList = null //设置item图标颜色为null，当menu里icon设置selector的时候，
                    navigation!!.setOnNavigationItemSelectedListener(
                        mOnNavigationItemSelectedListener
                    )
                    LocationManagerUtil.self?.reStartLocation()
                }

                override fun noPermission(
                    deniedPerms: List<String>?,
                    grantedPerms: List<String?>?,
                    hasPermanentlyDenied: Boolean?
                ) {
                    reqUpdate()
                    if (!mHasShowOnce1) reqActivityData(38)
                    val navigationAdapter =
                        NavigationAdapter(supportFragmentManager, fragments)
                    view_pager!!.addOnPageChangeListener(this@MainActivity)
                    view_pager!!.offscreenPageLimit = 3
                    view_pager!!.adapter = navigationAdapter
                    navigation?.let { BottomNavigationViewHelper.disableShiftMode(it) }
                    navigation!!.itemIconTintList = null //设置item图标颜色为null，当menu里icon设置selector的时候，
                    navigation!!.setOnNavigationItemSelectedListener(
                        mOnNavigationItemSelectedListener
                    )
                }

                public override fun showDialog(dialogType: Int, callback: DialogCallback) {
                    val dialog: MessageDialog =
                        MessageDialog.build((this@MainActivity as AppCompatActivity?)!!)
                    dialog.setTitle("需要获取以下权限")
                    dialog.setMessage(getString(R.string.permiss_location))
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

    private var updateInfo: UpdateInfo? = null
    @SuppressLint("HandlerLeak")
    private fun reqUpdate() {
        val reqInfo: RequestInfo = RequestInfo()
        reqInfo.context = this@MainActivity
        reqInfo.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_CHECK_UPDAGE
        reqInfo.parser = UpdateInfoParse()
        val file: File = File(
            KWApplication.instance.dataPath + File.separator + "apk" + File.separator
        )
        if (!file.exists()) file.mkdirs()
        reqInfo.reqDataMap = HashMap()
        reqInfo.reqDataMap!!["version"] = AppUtil.getVersionName(this)
        reqInfo.handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                val resInfo: ResponseInfo = msg.obj as ResponseInfo
                if (resInfo.status == 1) {
                    updateInfo = resInfo.responseData as UpdateInfo?
                    val ss: Array<String> = updateInfo!!.url.split("/".toRegex()).toTypedArray()
                    val apkNme: String = ss.get(ss.size - 1)
                    var mustUpdata = false
                    var hasUpdata = false
                    if (updateInfo!!.force == 1) {
                        hasUpdata = true
                    } else if (updateInfo!!.force == 2) {
                        mustUpdata = true
                    }
                    if (hasUpdata || mustUpdata) {
                        updateDialog(mustUpdata, apkNme)
                    }
                }
            }
        }
        ReqUtil.setReqInfo(reqInfo)
        ReqUtil.requestPostJSON(UpdateCallBack(reqInfo))
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
                        this@MainActivity,
                        1,
                        Constants.RC_PERMISSION_PERMISSION_FRAGMENT,
                        Constants.RC_PERMISSION_BASE
                    )
                }

                public override fun showDialog(dialogType: Int, callback: DialogCallback) {
                    val dialog: MessageDialog =
                        MessageDialog.build((this@MainActivity as AppCompatActivity?)!!)
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

    fun updateDialog(isMustUpdate: Boolean, apkName: String) {
        val file: File = File(
            KWApplication.instance.dataPath + File.separator + "apk" + File.separator + apkName
        )
        val messageDialog: MessageDialog = MessageDialog.build(this@MainActivity)
        val filMd5: String = Md5Util.getFileMD5(file)
        val md5Eq: Boolean = updateInfo!!.pathMd5.let { StringUtil.equals(filMd5, it) }
        val fileLength: Long = file.length()
        val lengthEq: Boolean = fileLength == updateInfo!!.pathLength
        if (file.exists() && lengthEq && md5Eq) {
            messageDialog.setTitle("更新APP")
            messageDialog.setMessage("新版本已下载,请安装！")
            messageDialog.setOkButton("安装")
            if (!isMustUpdate) {
                messageDialog.setCancelButton("取消")
                messageDialog.setCancelButton { baseDialog: BaseDialog?, v: View? ->
                    messageDialog.doDismiss()
                    false
                }
            }
            messageDialog.setCancelable(!isMustUpdate)
            messageDialog.setOkButton { baseDialog: BaseDialog?, v: View? ->
                messageDialog.doDismiss()
                permissionsCheck(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    R.string.permiss_write_store1,
                    object : Callback {
                        public override fun onSuccess() {
                            installApk(file.getAbsolutePath())
                        }
                        public override fun onError() {}
                    })
                false
            }
        } else {
            if (hasSave() == true && file.exists()) file.delete()
            val md5: String = updateInfo!!.pathMd5
            val url: String = updateInfo!!.url
            messageDialog.setTitle("检测到新版")
            messageDialog.setMessage(updateInfo!!.content)
            messageDialog.setOkButton("立即更新")
            if (!isMustUpdate) {
                messageDialog.setCancelButton("下次再说")
                messageDialog.setCancelButton { baseDialog: BaseDialog?, v: View? ->
                    messageDialog.doDismiss()
                    false
                }
            }
            messageDialog.setCancelable(false)
            messageDialog.setOkButton { baseDialog, v ->
                messageDialog.doDismiss()
                permissionsCheck(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    R.string.permiss_write_store1,
                    object : Callback {
                        public override fun onSuccess() {
                            val userSettings: SharedPreferences =
                                getSharedPreferences(Constants.SharedPreferences_name, 0)
                            val editor: SharedPreferences.Editor = userSettings.edit()
                            editor.putString("update_md5", md5)
                            editor.apply()
                            editor.commit()
                            initCallBack()
                            showProgressDialog(isMustUpdate)
                            InstallUtils.with(this@MainActivity) //必须-下载地址
                                .setApkUrl(updateInfo!!.url) //非必须-下载保存的文件的完整路径+name.apk
                                .setApkPath(
                                    KWApplication.instance.dataPath + File.separator + "apk" + File.separator + apkName
                                ) //非必须-下载回调
                                .setCallBack(downloadCallBack) //开始下载
                                .startDownload()
                        }
                        public override fun onError() {}
                    })

                false
            }
        }
        messageDialog.show()
    }

    private fun hasSave(): Boolean? {
        val userSettings: SharedPreferences =
            getSharedPreferences(Constants.SharedPreferences_name, 0)
        val md5: String? = userSettings.getString("update_md5", null)
        return md5?.let { StringUtil.equals(it, updateInfo!!.pathMd5) }
    }

    private fun showProgressDialog(isMustUpdate: Boolean) {
        progressDialog = MessageDialog.build(this@MainActivity)
        progressDialog?.title = "下载中..."
        progressDialog?.setOkButton(null as String?)
        progressDialog?.setCustomView(R.layout.progress_lay, object : MessageDialog.OnBindView {
            override fun onBind(dialog: MessageDialog, v: View) {
                progressbar = v.findViewById(R.id.progressbar)
            }
        })
        progressDialog?.cancelable = false
        progressDialog?.show()
    }

    private fun initCallBack() {
        downloadCallBack = object : DownloadCallBack {
            override fun onStart() {
                progressbar!!.progress = 0
            }

            override fun onComplete(path: String) {
                apkDownloadPath = path
                progressbar!!.progress = 100
                progressDialog!!.doDismiss()

                //先判断有没有安装权限
                InstallUtils.checkInstallPermission(
                    this@MainActivity,
                    object : InstallPermissionCallBack {
                        override fun onGranted() {
                            //去安装APK
                            installApk(apkDownloadPath)
                        }

                        override fun onDenied() {
                            //弹出弹框提醒用户
                            MessageDialog.show(
                                this@MainActivity,
                                "安装授权提示",
                                "必须授权才能安装APK，请设置允许安装",
                                "设置"
                            )
                                .setCancelable(false)
                                .setOkButton(object : OnDialogButtonClickListener {
                                    public override fun onClick(
                                        baseDialog: BaseDialog,
                                        v: View
                                    ): Boolean {
                                        baseDialog.doDismiss()
                                        //打开设置页面
                                        InstallUtils.openInstallPermissionSetting(
                                            this@MainActivity,
                                            object : InstallPermissionCallBack {
                                                public override fun onGranted() {
                                                    //去安装APK
                                                    installApk(apkDownloadPath)
                                                }

                                                public override fun onDenied() {
                                                    //还是不允许咋搞？
                                                    appManager!!.finishAllActivity()
                                                    finish()
                                                }
                                            })
                                        return false
                                    }
                                })
                        }
                    })
            }

            override fun onLoading(total: Long, current: Long) {
                //内部做了处理，onLoading 进度转回progress必须是+1，防止频率过快
                val progress: Int = (current * 100 / total).toInt()
                progressbar!!.progress = progress
            }

            override fun onFail(e: Exception) {
                progressDialog!!.doDismiss()
                LogUtil.e("hm", "下载失败" + e.toString())
                TipGifDialog.show(this@MainActivity, "下载失败", TipGifDialog.TYPE.ERROR)
            }

            override fun cancle() {
                progressDialog!!.doDismiss()
                TipGifDialog.show(this@MainActivity, "下载已取消", TipGifDialog.TYPE.ERROR)
            }
        }
    }

    private fun installApk(path: String?) {
        InstallUtils.installAPK(this@MainActivity, path, object : InstallCallBack {
            override fun onSuccess() {
                //onSuccess：表示系统的安装界面被打开
                //防止用户取消安装，在这里可以关闭当前应用，以免出现安装被取消
                appManager!!.finishAllActivity()
                finish()
            }

            override fun onFail(e: Exception) {
                TipGifDialog.show(this@MainActivity, "安装失败", TipGifDialog.TYPE.ERROR)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        //设置监听,防止其他页面设置回调后当前页面回调失效
//        if (InstallUtils.isDownloading()) {
//            InstallUtils.setDownloadCallBack(downloadCallBack);
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModel!!.onCleared()
    }

    public override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) {
    }

    public override fun onPageSelected(position: Int) {
        var selectedItemId = 0
        when (position) {
            0 -> {
                selectedItemId = R.id.navigation_home
                if (!mHasShowOnce1) reqActivityData(38)
            }
            1 -> {
                selectedItemId = R.id.navigation_shop
            }
            2 -> {
                selectedItemId = R.id.navigation_personal
                if (!mHasShowOnce2) reqActivityData(39)
            }
        }
        //        LogUtil.e("hm","viewPage getSelectedItemId="+navigation.getSelectedItemId());
//        LogUtil.e("hm","viewPage selectedItemId="+selectedItemId);
        if (navigation!!.getSelectedItemId() != selectedItemId) {
            navigation!!.setSelectedItemId(selectedItemId)
        }
    }

    public override fun onPageScrollStateChanged(state: Int) {}
    @SuppressLint("HandlerLeak")
    private fun reqActivityData(type: Int) {
        if (type == 38) {
            mViewModel!!.getHomeActivity(this@MainActivity)
                .observe(this@MainActivity, object : Observer<SystemParam?> {
                    public override fun onChanged(systemParam: SystemParam?) {
                        if (null == systemParam) return
                        initActivityView(systemParam, type)
                    }
                })
        } else if (type == 39) {
            mViewModel!!.getSettingActivity(this@MainActivity)
                .observe(this@MainActivity, object : Observer<SystemParam?> {
                    public override fun onChanged(systemParam: SystemParam?) {
                        if (null == systemParam) return
                        initActivityView(systemParam, type)
                    }
                })
        }
    }

    //    private CustomPopupWindow popWindow;
    private var mHasShowOnce1: Boolean = false // 活动弹框已经显示过一次了 首页展示
    private var mHasShowOnce2: Boolean = false // 活动弹框已经显示过一次了 个人中心展示
    private fun initActivityView(systemParam: SystemParam?, type: Int) {
        if (null == systemParam) {
            return
        }
        if (type == 38 && !mHasShowOnce1) {
            systemParam.title?.let { showPopvView(type, it, systemParam.url!!, systemParam.content!!) }
        } else if (type == 39 && !mHasShowOnce2) {
            showPopvView(type, systemParam.title!!, systemParam.url!!, systemParam.content!!)
        }
    }

    private fun showPopvView(type: Int, jumpTitle: String, jumpUrl: String, imgUrl: String) {
        if (StringUtil.isEmpty(imgUrl)) return
        //        final View customView = getLayoutInflater().inflate(R.layout.activity_activity_layout,null);
        KWApplication.instance.loadImg(imgUrl, null, object : ImageCallback {
            public override fun onSuccess(resource: Bitmap) {
                //对于已实例化的布局：
                customDialog = CustomDialog.show(
                    this@MainActivity,
                    R.layout.activity_activity_layout,
                    object : CustomDialog.OnBindView {
                        override fun onBind(dialog: CustomDialog, v: View) {
                            val showAcy: ImageView = v.findViewById(R.id.act_show_img)
                            showAcy.setImageBitmap(resource)
                            //                ConstraintLayout.LayoutParams params1 = new ConstraintLayout.LayoutParams(resource.getWidth(),resource.getHeight());
//                showAcy.setLayoutParams(params1);
                            showAcy.setOnClickListener(object : NoMoreClickListener() {
                                override fun OnMoreClick(view: View) {
                                    if (!StringUtil.isEmpty(jumpUrl)) {
                                        val intent: Intent =
                                            Intent(this@MainActivity, WebViewActivity::class.java)
                                        intent.putExtra("url", jumpUrl)
                                        intent.putExtra("from", jumpTitle)
                                        startActivity(intent)
                                    }
                                    customDialog!!.doDismiss()
                                }

                                override fun OnMoreErrorClick() {}
                            })
                            val closeAct: ImageView = v.findViewById(R.id.act_close_img)
                            closeAct.setOnClickListener(object : NoMoreClickListener() {
                                override fun OnMoreClick(view: View) {
                                    customDialog!!.doDismiss()
                                }

                                override fun OnMoreErrorClick() {}
                            })
                        }
                    }).setCancelable(false).setFullScreen(false).setCustomLayoutParams(
                    RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )
                if (type == 38) {
                    mHasShowOnce1 = true
                } else if (type == 39) {
                    mHasShowOnce2 = true
                }
            }

            public override fun onError() {
                if (type == 38) {
                    mHasShowOnce1 = true
                } else if (type == 39) {
                    mHasShowOnce2 = true
                }
            }
        })
    }
}