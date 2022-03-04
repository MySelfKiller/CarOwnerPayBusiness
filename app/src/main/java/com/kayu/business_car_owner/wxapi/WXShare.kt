package com.kayu.business_car_owner.wxapi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcel
import android.os.Parcelable
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hjq.toast.ToastUtils
import com.kayu.business_car_owner.R
import com.kayu.utils.*
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.modelmsg.*
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import java.io.ByteArrayOutputStream
import java.io.File


class WXShare(context: Context) {
    private val context: Context
    val api: IWXAPI
    private var listener: OnResponseListener? = null
    private var receiver: ResponseReceiver? = null
    fun register(): WXShare {
        // 微信分享
        api.registerApp(Constants.WX_APP_ID)
        receiver = ResponseReceiver()
        val filter = IntentFilter(ACTION_SHARE_RESPONSE)
        filter.addAction(TYPE_LOGIN)
        filter.addAction(TYPE_PAY)
        filter.addAction(TYPE_SHARE)
        filter.addAction(TYPE_OPEN_MINIPROGRAM)
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver!!, filter)
        //        context.registerReceiver(receiver, filter);
        return this
    }

    fun unregister() {
        try {
            api.unregisterApp()
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var callback: ItemCallback? = null

    /**
     * 发起微信支付请求
     *
     * @param wxPayBean 支付请求实体类
     * @param callback  支付结果回调
     */
    fun getWXPay(wxPayBean: WxPayBean, callback: ItemCallback?) {
        if (!api.isWXAppInstalled) {
            ToastUtils.show("您的设备未安装微信客户端")
        } else {
            val payRequest = PayReq()
            payRequest.appId = Constants.WX_APP_ID
            payRequest.partnerId = wxPayBean.body!!.partnerid
            payRequest.prepayId = wxPayBean.body!!.perpayid
            payRequest.packageValue = wxPayBean.body!!.packageX //固定值
            payRequest.nonceStr = wxPayBean.body!!.noncestr
            payRequest.timeStamp = wxPayBean.body!!.timestamp.toString()
            payRequest.sign = wxPayBean.body!!.sign
            api.sendReq(payRequest)
            this.callback = callback
        }
    }

    /**
     * 获取微信登录授权code
     *
     * @return
     */
    fun getAuth(callback: ItemCallback?): WXShare {
        //发起登录请求
        if (!api.isWXAppInstalled) {
            ToastUtils.show("您的设备未安装微信客户端")
        } else {
            val req = SendAuth.Req()
            req.scope = "snsapi_userinfo"
            req.state = "com.kayu.business_car_owner"
            api.sendReq(req)
            this.callback = callback
        }
        return this
    }

    /**
     * 打开微信小程序
     */
    fun openMiniProgram(progranID:String, path:String) {
        val api = WXAPIFactory.createWXAPI(context, Constants.WX_APP_ID)
        val req = WXLaunchMiniProgram.Req()
        req.userName = progranID // 小程序原始id
        req.path = path ////拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE // 可选打开 开发版，体验版和正式版
        api.sendReq(req)

    }

    fun shareText(type: Int, text: String?): WXShare {
        val textObj = WXTextObject()
        textObj.text = text
        val msg = WXMediaMessage()
        msg.mediaObject = textObj
        //        msg.title = "Will be ignored";
        msg.description = text
        val req = SendMessageToWX.Req()
        req.transaction = buildTransaction("text")
        req.message = msg
        req.scene =
            if (type == 0) SendMessageToWX.Req.WXSceneSession else SendMessageToWX.Req.WXSceneTimeline
        val result = api.sendReq(req)
        LogUtil.e("hm", "text shared: $result")
        return this
    }

    fun shareImg(type: Int, filePath: String): WXShare {
        val file = File(filePath)
        if (!file.exists()) {
            val tip = "文件不存在"
            ToastUtils.show("$tip path = $filePath")
            return this
        }
        val imgObj = WXImageObject()
        imgObj.setImagePath(filePath)
        val msg = WXMediaMessage()
        msg.mediaObject = imgObj
        val bmp = BitmapFactory.decodeFile(filePath)
        val thumbBmp = Bitmap.createScaledBitmap(bmp, 100, 150, true)
        msg.thumbData = FileUtil.bmpToByteArray(thumbBmp, true)
        val req = SendMessageToWX.Req()
        req.transaction = buildTransaction("img")
        req.message = msg
        req.scene =
            if (type == 0) SendMessageToWX.Req.WXSceneSession else SendMessageToWX.Req.WXSceneTimeline
        api.sendReq(req)
        return this
    }

    fun shareImg(type: Int, bitmap: Bitmap, title: String?, descroption: String?): WXShare {
        val imgObj = WXImageObject()
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val datas = baos.toByteArray()
        imgObj.imageData = datas
        val msg = WXMediaMessage()
        msg.mediaObject = imgObj
        msg.title = title
        msg.description = descroption
        val thumbBmp = Bitmap.createScaledBitmap(bitmap, 100, 150, true)
        msg.thumbData = FileUtil.bmpToByteArray(thumbBmp, true)
        val req = SendMessageToWX.Req()
        req.transaction = buildTransaction("img")
        req.message = msg
        req.scene =
            if (type == 0) SendMessageToWX.Req.WXSceneSession else SendMessageToWX.Req.WXSceneTimeline
        api.sendReq(req)
        return this
    }

    fun shareMusic(type: Int, url: String?, title: String?, descroption: String?): WXShare {
//初始化一个WXMusicObject，填写url
        val music = WXMusicObject()
        music.musicUrl = url

//用 WXMusicObject 对象初始化一个 WXMediaMessage 对象
        val msg = WXMediaMessage()
        msg.mediaObject = music
        msg.title = title
        msg.description = descroption
        val thumbBmp = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
        //设置音乐缩略图
        msg.thumbData = FileUtil.bmpToByteArray(thumbBmp, true)
        //构造一个Req
        val req = SendMessageToWX.Req()
        req.transaction = buildTransaction("music")
        req.message = msg
        req.scene =
            if (type == 0) SendMessageToWX.Req.WXSceneSession else SendMessageToWX.Req.WXSceneTimeline
        //        req.userOpenId = getOpenId();
//调用api接口，发送数据到微信
        api.sendReq(req)
        return this
    }

    fun shareVideo(type: Int, url: String?, title: String?, descroption: String?): WXShare {
//初始化一个WXVideoObject，填写url
        val video = WXVideoObject()
        video.videoUrl = url

//用 WXVideoObject 对象初始化一个 WXMediaMessage 对象
        val msg = WXMediaMessage(video)
        msg.title = title
        msg.description = descroption
        val thumbBmp = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
        msg.thumbData = FileUtil.bmpToByteArray(thumbBmp, true)
        //        msg.setThumbImage(null);
//构造一个Req
        val req = SendMessageToWX.Req()
        req.transaction = buildTransaction("video")
        req.message = msg
        req.scene =
            if (type == 0) SendMessageToWX.Req.WXSceneSession else SendMessageToWX.Req.WXSceneTimeline
        //        req.userOpenId = getOpenId();

//调用api接口，发送数据到微信
        api.sendReq(req)
        return this
    }

    fun shareUrl(
        type: Int,
        url: String?,
        title: String?,
        descroption: String?,
        filePath: String?
    ): WXShare { //初始化一个WXWebpageObject填写url          
        val webpageObject = WXWebpageObject()
        webpageObject.webpageUrl = url
        //用WXWebpageObject对象初始化一个WXMediaMessage，天下标题，描述
        val msg = WXMediaMessage(webpageObject)
        msg.title = title
        msg.description = descroption
        //这块需要注意，图片的像素千万不要太大，不然的话会调不起来微信分享，
        if (!StringUtil.isEmpty(filePath)) {
            val bmp = BitmapFactory.decodeFile(filePath)
            val thumbBmp = Bitmap.createScaledBitmap(bmp, 50, 50, true)
            msg.thumbData = FileUtil.bmpToByteArray(thumbBmp, true)
        } else {
            val thumbBmp = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
            msg.thumbData = FileUtil.bmpToByteArray(thumbBmp, true)
        }
        val req = SendMessageToWX.Req()
        req.transaction = System.currentTimeMillis().toString()
        req.message = msg
        req.scene =
            if (type == 0) SendMessageToWX.Req.WXSceneSession else SendMessageToWX.Req.WXSceneTimeline
        api.sendReq(req)
        return this
    }

    fun setListener(listener: OnResponseListener?) {
        this.listener = listener
    }

    private fun buildTransaction(type: String?): String {
        return if (type == null) System.currentTimeMillis()
            .toString() else type + System.currentTimeMillis()
    }

    private inner class ResponseReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            LogUtil.e("hm", "接收到广播")
            if (intent.action == TYPE_SHARE) {
                val response: Response? = intent.getParcelableExtra(EXTRA_RESULT)
                LogUtil.e("hm", "type: " + response?.type)
                LogUtil.e("hm", "errCode: " + response?.errCode)
                val result: String
                if (listener != null) {
                    if (response?.errCode == BaseResp.ErrCode.ERR_OK) {
                        listener!!.onSuccess()
                    } else if (response?.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
                        listener!!.onCancel()
                    } else {
                        result = when (response?.errCode) {
                            BaseResp.ErrCode.ERR_AUTH_DENIED -> "发送被拒绝"
                            BaseResp.ErrCode.ERR_UNSUPPORT -> "不支持错误"
                            else -> "发送返回"
                        }
                        listener!!.onFail(result)
                    }
                }
            } else if (intent.action == TYPE_LOGIN) {
                val code = intent.getStringExtra(EXTRA_RESULT)
                if (null != callback) {
                    callback!!.onItemCallback(0, code)
                }
            } else if (intent.action == TYPE_PAY) {
                val response: Response? = intent.getParcelableExtra(EXTRA_RESULT)
                val result: String
                if (listener != null) {
                    if (response?.errCode == BaseResp.ErrCode.ERR_OK) {
                        listener!!.onSuccess()
                    } else if (response?.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
                        listener!!.onCancel()
                    } else {
                        result = "支付错误" + response?.errCode
                        listener!!.onFail(result)
                    }
                }
                if (null != callback) {
                    callback!!.onItemCallback(0, null)
                }
            }else if (intent.action == TYPE_OPEN_MINIPROGRAM) {
                val response: Response? = intent.getParcelableExtra(EXTRA_RESULT)
                if (null != listener) {
                    if (response?.errCode == BaseResp.ErrCode.ERR_OK) {
                        listener!!.onSuccess()
                    } else {
                        listener!!.onFail(response!!.errStr)
                    }
                }
            }
        }
    }

    class Response : BaseResp, Parcelable {
//        var errCode: Int = 0
//        var errStr: String = ""
//        var transaction: String = ""
//        var openId: String = ""
        private var type: Int
        private var checkResult: Boolean

        constructor(baseResp: BaseResp) {
            this.errCode = baseResp.errCode
            errStr = baseResp.errStr
            transaction = baseResp.transaction
            openId = baseResp.openId
            type = baseResp.type
            checkResult = baseResp.checkArgs()
        }


        override fun getType(): Int {
            return type
        }

        override fun checkArgs(): Boolean {
            return checkResult
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeInt(this.errCode)
            dest.writeString(this.errStr)
            dest.writeString(this.transaction)
            dest.writeString(this.openId)
            dest.writeInt(type)
            dest.writeByte(if (checkResult) 1.toByte() else 0.toByte())
        }

        constructor(`in`: Parcel) {
            this.errCode = `in`.readInt()
            this.errStr = `in`.readString()
            this.transaction = `in`.readString()
            this.openId = `in`.readString()
            type = `in`.readInt()
            checkResult = `in`.readByte().toInt() != 0
        }

        companion object CREATOR : Parcelable.Creator<Response> {
            override fun createFromParcel(parcel: Parcel): Response {
                return Response(parcel)
            }

            override fun newArray(size: Int): Array<Response?> {
                return arrayOfNulls(size)
            }
        }
    }

    companion object {
        const val ACTION_SHARE_RESPONSE = "action_wx_share_response"
        const val EXTRA_RESULT = "result"
        const val EXTRA_TYPE = "type"
        const val TYPE_LOGIN = "login"
        const val TYPE_SHARE = "share"
        const val TYPE_PAY = "pay"
        const val TYPE_OPEN_MINIPROGRAM = "openwxproject"
    }

    //    private int mTargetScene = SendMessageToWX.Req.WXSceneSession;
    init {
        api = WXAPIFactory.createWXAPI(context, Constants.WX_APP_ID)
        this.context = context
    }
}