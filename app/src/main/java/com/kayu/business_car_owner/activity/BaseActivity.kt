package com.kayu.business_car_owner.activity

import android.Manifest
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.kayu.utils.permission.EasyPermissions.DialogCallback
import com.kayu.utils.permission.EasyPermissions.PermissionWithDialogCallbacks
import com.kayu.utils.permission.EasyPermissions
import com.kayu.business_car_owner.*
import java.util.*

open class BaseActivity : AppCompatActivity(), PermissionWithDialogCallbacks {
    /**
     * activity堆栈管理
     */
    protected var appManager: AppManager? = AppManager.appManager
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
            val newConfig: Configuration = Configuration()
            newConfig.setToDefaults() //设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics())
        }
        return res
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        setStatusBar();
//        if(NavigationBarUtil.checkNavigationBarShow(this)){
//            NavigationBarUtil.initActivity(findViewById(android.R.id.content));
//        }
        appManager!!.addActivity(this)
        //沉浸式代码配置
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, true)
        //设置状态栏透明
//        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.white))

//        StatusBarUtil.setTranslucent(this, 0);
//        StatusBarUtil.setColor(this, getResources().getColor(R.color.white));
//        int displayHeight = CommonUtils.getDisplayHeight(this);
//        LogUtil.e("screen","displayWidth："+KWApplication.getInstance().displayWidth
//                +" displayHeight："+displayHeight);
//        setContentView(R.layout.activity_abs);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected fun setStatusBar() {
        StatusBarUtil.setLightMode(this)
        StatusBarUtil.setColor(this, getResources().getColor(R.color.dark_grey))
    }

    override fun onDestroy() {
        super.onDestroy()
        // 从栈中移除activity
        appManager!!.finishActivity(this)
    }
    /**
     * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
     * Android6.0权限控制
     * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
     */
    /**
     * 需要进行检测的权限数组
     */
    var needPermissions: Array<String> = arrayOf( //            Manifest.permission.CALL_PHONE,
        //            Manifest.permission.READ_PHONE_STATE,
        //            Manifest.permission.READ_CALL_LOG,
        //            Manifest.permission.WRITE_CALL_LOG,
        //            Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private var mPermissonCallbacks: MutableMap<Int, PermissionCallback>? = null
    private var mPermissions: MutableMap<Int, Array<String>>? = null

    open interface PermissionCallback {
        /**
         * has all permission
         *
         * @param allPerms all permissions
         */
        fun hasPermission(allPerms: List<Array<String>>)

        /**
         * denied some permission
         *
         * @param deniedPerms          denied permission
         * @param grantedPerms         granted permission
         * @param hasPermanentlyDenied has permission denied permanently
         */
        fun noPermission(
            deniedPerms: List<String>?,
            grantedPerms: List<String?>?,
            hasPermanentlyDenied: Boolean?
        )

        /**
         * @param dialogType dialogType
         * @param callback   callback from easypermissions
         */
        fun showDialog(dialogType: Int, callback: DialogCallback)
    }

    /**
     * request permission
     *
     * @param dialogType  dialogType
     * @param requestCode requestCode
     * @param perms       permissions
     * @param callback    callback
     */
    fun performCodeWithPermission(
        dialogType: Int,
        requestCode: Int, perms: Array<String>, callback: PermissionCallback
    ) {
        if (EasyPermissions.hasPermissions(this, *perms)) {
            callback.hasPermission(listOf(perms))
        } else {
            if (mPermissonCallbacks == null) {
                mPermissonCallbacks = HashMap()
            }
            mPermissonCallbacks!!.put(requestCode, callback)
            if (mPermissions == null) {
                mPermissions = HashMap()
            }
            mPermissions!!.put(requestCode, perms)
            EasyPermissions.requestPermissions(this, dialogType, requestCode, *perms)
        }
    }

    public override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>?) {
        if (mPermissonCallbacks == null || !mPermissonCallbacks!!.containsKey(requestCode)) {
            return
        }
        if (mPermissions == null || !mPermissions!!.containsKey(requestCode)) {
            return
        }

        // 100% granted permissions
        if (mPermissions!!.get(requestCode)?.size == perms!!.size) {
            mPermissonCallbacks!!.get(requestCode)!!
                .hasPermission(listOf(mPermissions!![requestCode]) as List<Array<String>>)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>?) {
        if (mPermissonCallbacks == null || !mPermissonCallbacks!!.containsKey(requestCode)) {
            return
        }
        if (mPermissions == null || !mPermissions!!.containsKey(requestCode)) {
            return
        }

        //granted permission
        val grantedPerms: MutableList<String?> = ArrayList()
        for (perm: String in mPermissions!![requestCode]!!) {
            if (!perms!!.contains(perm)) {
                grantedPerms.add(perm)
            }
        }

        //check has permission denied permanently
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms!!)) {
            mPermissonCallbacks!!.get(requestCode)!!.noPermission(perms, grantedPerms, true)
        } else {
            mPermissonCallbacks!!.get(requestCode)!!.noPermission(perms, grantedPerms, false)
        }
    }

     override fun onDialog(requestCode: Int, dialogType: Int, callback: DialogCallback?) {
        if (mPermissonCallbacks == null || !mPermissonCallbacks!!.containsKey(requestCode)) {
            return
        }
        mPermissonCallbacks!!.get(requestCode)!!.showDialog(dialogType, callback!!)
    }
}