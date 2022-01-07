/*
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kayu.utils.permission

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlin.jvm.JvmOverloads
import androidx.viewpager.widget.ViewPager
import com.kayu.utils.callback.ImageCallback
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.kayu.utils.location.LocationCallback
import com.amap.api.location.AMapLocationListener
import com.amap.api.location.AMapLocationQualityReport
import com.kayu.utils.location.LocationManagerUtil
import kotlin.jvm.Synchronized
import com.kayu.utils.location.CoordinateTransformUtil
import com.kayu.utils.permission.EasyPermissions
import com.kayu.utils.permission.EasyPermissions.PermissionWithDialogCallbacks
import com.kayu.utils.permission.EasyPermissions.DialogCallback
import com.kayu.utils.permission.EasyPermissions.PermissionCallbacks
import com.kayu.utils.permission.AfterPermissionGranted
import kotlin.Throws
import com.kayu.utils.status_bar_set.OSUtils
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.kayu.utils.status_bar_set.SystemBarTintManager
import com.kayu.utils.status_bar_set.StatusBarUtil.ViewType
import com.kayu.utils.status_bar_set.SystemBarTintManager.SystemBarConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.GsonBuilder
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.DecodeHintType
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.BinaryBitmap
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.common.GlobalHistogramBinarizer
import com.kayu.utils.*
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import java.lang.reflect.InvocationTargetException
import java.util.ArrayList

/**
 * @author  ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
object EasyPermissions {
    private const val TAG = "EasyPermissions"

    /**
     * Check if the calling context has a set of permissions.
     *
     * @param context the calling context.
     * @param perms   one ore more permissions, such as `android.Manifest.permission.CAMERA`.
     * @return true if all permissions are already granted, false if at least one permission
     * is not yet granted.
     */
    fun hasPermissions(context: Context, vararg perms: String): Boolean {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            LogUtil.w(TAG, "hasPermissions: API version < M, returning true by default")
            return true
        }
        for (perm in perms) {
            val hasPerm = ContextCompat.checkSelfPermission(
                context!!, perm!!
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPerm) {
                return false
            }
        }
        return true
    }

    fun requestPermissions(obj: Any, requestCode: Int, vararg perms: String) {
        requestPermissions(obj, 0, requestCode, false, *perms)
    }

    fun requestPermissions(
        obj: Any,
        dialogType: Int,
        requestCode: Int,
        vararg perms: String
    ) {
        requestPermissions(obj, dialogType, requestCode, true, *perms)
    }

    fun requestPermissions(
        obj: Any, dialogType: Int,
        requestCode: Int, isAppDialog: Boolean, vararg perms: String
    ) {
        checkCallingObjectSuitability(obj)
        var shouldShowRationale = false
        for (perm in perms) {
            shouldShowRationale =
                shouldShowRationale || shouldShowRequestPermissionRationale(obj, perm)
        }
        if (shouldShowRationale) {
            val activity = getActivity(obj) ?: return
            //调用app自己的dialog显示
            if (isAppDialog == true) {
                if (obj is PermissionWithDialogCallbacks) {
                    obj.onDialog(requestCode, dialogType, object : DialogCallback {
                        override fun onGranted() {
                            executePermissionsRequest(obj, perms as Array<String>, requestCode)
                        }
                    })
                }
            } else {
                // 调用easypermission自带的dialog
            }
        } else {
            if (isAppDialog == true) {
                if (obj is PermissionWithDialogCallbacks) {
                    obj.onDialog(requestCode, dialogType, object : DialogCallback {
                        override fun onGranted() {
                            executePermissionsRequest(obj, perms as Array<String>, requestCode)
                        }
                    })
                }
            }
            //            executePermissionsRequest(object, perms, requestCode);
        }
    }

    fun somePermissionPermanentlyDenied(obj: Any?, deniedPermissions: List<String>): Boolean {
        for (deniedPermission in deniedPermissions) {
            if (permissionPermanentlyDenied(obj, deniedPermission)) {
                return true
            }
        }
        return false
    }

    fun permissionPermanentlyDenied(obj: Any?, deniedPermission: String): Boolean {
        return !shouldShowRequestPermissionRationale(obj, deniedPermission)
    }

    fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray, vararg receivers: Any
    ) {

        // Make a collection of granted and denied permissions from the request.
        val granted = ArrayList<String>()
        val denied = ArrayList<String>()
        for (i in permissions.indices) {
            val perm = permissions[i]
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm)
            } else {
                denied.add(perm)
            }
        }

        // iterate through all receivers
        for (obj in receivers) {
            // Report granted permissions, if any.
            if (!granted.isEmpty()) {
                if (obj is PermissionCallbacks) {
                    obj.onPermissionsGranted(requestCode, granted)
                }
            }

            // Report denied permissions, if any.
            if (!denied.isEmpty()) {
                if (obj is PermissionCallbacks) {
                    obj.onPermissionsDenied(requestCode, denied)
                }
            }

            // If 100% successful, call annotated methods
            if (!granted.isEmpty() && denied.isEmpty()) {
                runAnnotatedMethods(obj, requestCode)
            }
        }
    }

    fun goSettingsPermissions(obj: Any?, requestCode: Int, requestCodeForResult: Int) {
        goSettingsPermissions(obj, 0, requestCode, requestCodeForResult, false)
    }

    @JvmOverloads
    fun goSettingsPermissions(
        obj: Any?,
        dialogType: Int,
        requestCode: Int,
        requestCodeForResult: Int,
        isAppDialog: Boolean = true
    ) {
        checkCallingObjectSuitability(obj)
        val activity = getActivity(obj) ?: return

        //调用app自己的dialog显示
        if (isAppDialog == true) {
            if (obj is PermissionWithDialogCallbacks) {
                obj.onDialog(requestCode, dialogType, object : DialogCallback {
                    override fun onGranted() {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", activity.packageName, null)
                        intent.data = uri
                        startForResult(obj, intent, requestCodeForResult)
                    }
                })
            }
        } else {
            // 调用easypermission自带的dialog
        }
    }

    @TargetApi(23)
    private fun shouldShowRequestPermissionRationale(obj: Any?, perm: String): Boolean {
        return if (obj is Activity) {
            ActivityCompat.shouldShowRequestPermissionRationale(
                (obj as Activity?)!!,
                perm
            )
        } else if (obj is Fragment) {
            obj.shouldShowRequestPermissionRationale(perm)
        } else if (obj is android.app.Fragment) {
            obj.shouldShowRequestPermissionRationale(perm)
        } else {
            false
        }
    }

    @TargetApi(23)
    private fun executePermissionsRequest(obj: Any, perms: Array<String>, requestCode: Int) {
        checkCallingObjectSuitability(obj)
        if (obj is Activity) {
            ActivityCompat.requestPermissions(obj, perms, requestCode)
        } else if (obj is Fragment) {
            obj.requestPermissions(perms, requestCode)
        } else if (obj is android.app.Fragment) {
            obj.requestPermissions(perms, requestCode)
        }
    }

    @TargetApi(11)
    private fun getActivity(obj: Any?): Activity? {
        return if (obj is Activity) {
            obj
        } else if (obj is Fragment) {
            obj.activity
        } else if (obj is android.app.Fragment) {
            obj.activity
        } else {
            null
        }
    }

    @TargetApi(11)
    private fun startForResult(obj: Any, intent: Intent, requestCode: Int) {
        if (obj is Activity) {
            obj.startActivityForResult(intent, requestCode)
        } else if (obj is Fragment) {
            obj.startActivityForResult(intent, requestCode)
        } else if (obj is android.app.Fragment) {
            obj.startActivityForResult(intent, requestCode)
        }
    }

    private fun checkCallingObjectSuitability(obj: Any?) {
        // Make sure Object is an Activity or Fragment
        val isActivity = obj is Activity
        val isSupportFragment = obj is Fragment
        val isAppFragment = obj is android.app.Fragment
        val isMinSdkM = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        if (!(isSupportFragment || isActivity || isAppFragment && isMinSdkM)) {
            require(!isAppFragment) { "Target SDK needs to be greater than 23 if caller is android.app.Fragment" }
            throw IllegalArgumentException("Caller must be an Activity or a Fragment.")
        }
    }

    /**
     * Find all methods annotated with [] on a given object with the
     * correc requestCode argument.
     * @param object the object with annotated methods.
     * @param requestCode the requestCode passed to the annotation.
     */
    private fun runAnnotatedMethods(obj: Any, requestCode: Int) {
        var clazz: Class<*>? = obj.javaClass
        if (isUsingAndroidAnnotations(obj)) {
            clazz = clazz!!.superclass
        }
        while (clazz != null) {
            for (method in clazz.declaredMethods) {
                if (method.isAnnotationPresent(AfterPermissionGranted::class.java)) {
                    // Check for annotated methods with matching request code.
                    val ann = method.getAnnotation(AfterPermissionGranted::class.java)
                    if (ann.value == requestCode) {
                        // Method must be void so that we can invoke it
                        if (method.parameterTypes.size > 0) {
                            throw RuntimeException(
                                "Cannot execute method " + method.name + " because it is non-void method and/or has input parameters."
                            )
                        }
                        try {
                            // Make method accessible if private
                            if (!method.isAccessible) {
                                method.isAccessible = true
                            }
                            method.invoke(obj)
                        } catch (e: IllegalAccessException) {
                            LogUtil.e(TAG, "runDefaultMethod:IllegalAccessException", e)
                        } catch (e: InvocationTargetException) {
                            LogUtil.e(TAG, "runDefaultMethod:InvocationTargetException", e)
                        }
                    }
                }
            }
            clazz = clazz.superclass
        }
    }

    /**
     * Determine if the project is using the AndroidAnnoations library.
     */
    private fun isUsingAndroidAnnotations(obj: Any): Boolean {
        return if (!obj.javaClass.simpleName.endsWith("_")) {
            false
        } else try {
            val clazz = Class.forName("org.androidannotations.api.view.HasViews")
            clazz.isInstance(obj)
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    /**
     * 自带对话框的回调
     */
    interface PermissionCallbacks : ActivityCompat.OnRequestPermissionsResultCallback {
        fun onPermissionsGranted(requestCode: Int, perms: List<String>?)
        fun onPermissionsDenied(requestCode: Int, perms: List<String>?)
    }

    /**
     * 自定义对话框的回调
     */
    interface PermissionWithDialogCallbacks : PermissionCallbacks {
        fun onDialog(requestCode: Int, dialogType: Int, callback: DialogCallback?)
    }

    /**
     * 允许授权
     */
    interface DialogCallback {
        fun onGranted()
    }
}