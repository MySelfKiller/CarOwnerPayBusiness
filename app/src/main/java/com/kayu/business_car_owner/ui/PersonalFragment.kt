package com.kayu.business_car_owner.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.multidex.MultiDexApplication
import androidx.recyclerview.widget.RecyclerView
import com.gcssloop.widget.PagerGridLayoutManager
import com.hjq.toast.ToastUtils
import com.kayu.business_car_owner.KWApplication
import com.kayu.business_car_owner.R
import com.kayu.business_car_owner.activity.*
import com.kayu.business_car_owner.model.SysOrderBean
import com.kayu.business_car_owner.ui.adapter.OrderCategoryAdapter
import com.kayu.utils.*
import com.kayu.utils.callback.Callback
import com.kayu.utils.view.RoundImageView
import com.kongzue.dialog.interfaces.OnDismissListener
import com.kongzue.dialog.interfaces.OnMenuItemClickListener
import com.kongzue.dialog.v3.BottomMenu
import com.kongzue.dialog.v3.MessageDialog
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class PersonalFragment : Fragment() {
    private var refreshLayout: SmartRefreshLayout? = null
    var isLoadmore = false
    var isRefresh = false
    private var mainViewModel: MainViewModel? = null
    private var user_head_img: RoundImageView? = null
    private var user_name: TextView? = null
    private var user_balance: TextView? = null
    private var web_info_tv: TextView? = null
    private var card_num: TextView? = null
    private var user_tip: TextView? = null
    private var card_valid: TextView? = null
    private var explain_content: TextView? = null
    private var sp: SharedPreferences? = null
    //    private ConstraintLayout all_order_lay;
    //    private LinearLayout more_lay;
    //    private ImageView user_card_bg;
    private var income_lay: LinearLayout? = null
    private var user_expAmt: TextView? = null
    private var user_reacharge: TextView? = null
    private var user_rewad: TextView? = null
    private var category_rv: RecyclerView? = null
    private var isOnline = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        mainViewModel = ViewModelProviders.of(requireActivity())
            .get(MainViewModel::class.java)
        sp = requireContext().getSharedPreferences(
            Constants.SharedPreferences_name,
            Context.MODE_PRIVATE
        )
        return inflater.inflate(R.layout.fragment_personal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshLayout = view.findViewById(R.id.refreshLayout)
        //用户头像
        user_head_img = view.findViewById(R.id.personal_user_head_img)
        user_head_img?.setOnClickListener(object:NoMoreClickListener(){
            override fun OnMoreClick(view: View) {
                openImageChooserActivity()
            }

            override fun OnMoreErrorClick() {
            }

        })
        //用户名称
        user_name = view.findViewById(R.id.personal_user_name)
        //累计节省
        user_expAmt = view.findViewById(R.id.personal_user_expAmt)
        user_reacharge = view.findViewById(R.id.personal_recharge)
        explain_content = view.findViewById(R.id.personal_explain_content)

        //收益
        user_rewad = view.findViewById(R.id.personal_user_rewad)
        //可体现
        user_balance = view.findViewById(R.id.personal_user_balance)
//        user_balance = view.findViewById(R.id.personal_user_balance)
        //        user_card_bg = view.findViewById(R.id.personal_user_card_bg);
//        KWApplication.getInstance().loadImg(R.mipmap.ic_personal_bg,user_card_bg,new GlideRoundTransform(getContext()));
        card_num = view.findViewById(R.id.personal_card_num)
        //账户提示语
        user_tip = view.findViewById(R.id.personal_user_tip)
        card_valid = view.findViewById(R.id.personal_card_valid)
        web_info_tv = view.findViewById(R.id.personal_web_info)
        income_lay = view.findViewById(R.id.personal_income_lay)
        refreshLayout?.setEnableAutoLoadMore(false)
        refreshLayout?.setEnableLoadMore(false)
        refreshLayout?.setEnableLoadMoreWhenContentNotFull(true) //是否在列表不满一页时候开启上拉加载功能
        refreshLayout?.setEnableOverScrollBounce(true) //是否启用越界回弹
        refreshLayout?.setEnableOverScrollDrag(true)
        refreshLayout?.setOnRefreshListener(OnRefreshListener {
            if (isRefresh || isLoadmore) return@OnRefreshListener
            isRefresh = true
            initView()
        })
//        val detailed_list = view.findViewById<TextView>(R.id.personal_detailed_list)
//        detailed_list.setOnClickListener(object : NoMoreClickListener() {
//            override fun OnMoreClick(view: View) {
//                val fg = requireActivity().supportFragmentManager
//                val fragmentTransaction = fg.beginTransaction()
//                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                fragmentTransaction.add(R.id.main_root_lay, BalanceFragment())
//                fragmentTransaction.addToBackStack("ddd")
//                fragmentTransaction.commit()
//            }
//
//            override fun OnMoreErrorClick() {}
//        })
        val customer_services_lay: ConstraintLayout =
            view.findViewById(R.id.personal_customer_services_lay)
        customer_services_lay.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                startActivity(Intent(context, CustomerActivity::class.java))
            }

            override fun OnMoreErrorClick() {}
        })
        //        ConstraintLayout course_lay = view.findViewById(R.id.personal_course_lay);
//        course_lay.setOnClickListener(new NoMoreClickListener() {
//            @Override
//            protected void OnMoreClick(View view) {
//                mainViewModel.getParameter(getContext(),11).observe(requireActivity(), new Observer<SystemParam>() {
//                    @Override
//                    public void onChanged(SystemParam systemParam) {
//                        String target = systemParam.url;
//                        if (!StringUtil.isEmpty(target)){
//                            Intent intent = new Intent(getContext(), WebViewActivity.class);
//                            intent.putExtra("url",target);
//                            intent.putExtra("from","新手教程");
//                            requireActivity().startActivity(intent);
//                        }
//                    }
//                });
//
//
//            }
//
//            @Override
//            protected void OnMoreErrorClick() {
//
//            }
//        });
        val setting_lay: ConstraintLayout = view.findViewById(R.id.personal_setting_lay)
        setting_lay.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                startActivity(Intent(context, SettingsActivity::class.java))
            }

            override fun OnMoreErrorClick() {}
        })
        //        all_order_lay = view.findViewById(R.id.id_all_order_lay);
//        more_lay = view.findViewById(R.id.personal_more_lay);
        category_rv = view.findViewById(R.id.personal_category_rv)
        //        oil_order_lay = view.findViewById(R.id.personal_oil_order_lay);
//        oil_order_lay.setOnClickListener(new NoMoreClickListener() {
//            @Override
//            protected void OnMoreClick(View view) {
//                startActivity(new Intent(getContext(),OilOrderListActivity.class));
//            }
//
//            @Override
//            protected void OnMoreErrorClick() {
//
//            }
//        });
//        wash_order_lay = view.findViewById(R.id.personal_shop_order_lay);
//        wash_order_lay.setOnClickListener(new NoMoreClickListener() {
//            @Override
//            protected void OnMoreClick(View view) {
//                startActivity(new Intent(getContext(),WashOrderListActivity.class));
//            }
//
//            @Override
//            protected void OnMoreErrorClick() {
//
//            }
//        });
//        if (getUserVisibleHint()){
//            refreshLayout.autoRefresh();
//            mHasLoadedOnce = true;
//        }
        isCreated = true
    }
    private var isCreated = false
    private var mHasLoadedOnce = false // 页面已经加载过
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        LogUtil.e("PersonalFragment----", "----setUserVisibleHint---")
        if (isVisibleToUser && isCreated && !mHasLoadedOnce) {
            LogUtil.e("PersonalFragment----", "----setUserVisibleHint---isCreated")
//            TipGifDialog.show(
//                requireContext() as AppCompatActivity,
//                "加载中...",
//                TipGifDialog.TYPE.OTHER,
//                R.drawable.loading_gif
//            )
//            isRefresh = true
//            mHasLoadedOnce = true
//            initView()
            refreshLayout?.autoRefresh()
        }
    }

    override fun onStart() {
        super.onStart()
        LogUtil.e("PersonalFragment----", "----onStart---")
        if (!userVisibleHint || mHasLoadedOnce) return
        LogUtil.e("PersonalFragment----", "----onStart---isVisibleToUser")
//        TipGifDialog.show(
//            requireContext() as AppCompatActivity,
//            "加载中...",
//            TipGifDialog.TYPE.OTHER,
//            R.drawable.loading_gif
//        )
//        isRefresh = true
//        mHasLoadedOnce = true
//        initView()
        refreshLayout?.autoRefresh()
    }

    private fun initView() {
//        if (null != LocationManagerUtil.getSelf().getLoccation()){
//            mainViewModel.getReminder(getContext(), LocationManagerUtil.getSelf().getLoccation().getCity()).observe(requireActivity(), new Observer<String>() {
//                @Override
//                public void onChanged(String parameter) {
//                    explain_content.setText(parameter);
//                }
//            });
//        }
//        mainViewModel!!.sendOilPayInfo(context)
        mainViewModel!!.getUserInfo(requireContext()).observe(requireActivity(), Observer { userBean ->
            if (null == userBean) return@Observer
            mainViewModel!!.getUserTips(requireContext())
                .observe(requireActivity(), Observer { systemParam ->
                    if (null == systemParam) return@Observer
                    try {
                        val jsonObject = JSONObject(systemParam.content)
                        var tipStr = ""
                        var btnStr = ""
                        if (userBean.type == 1) {
                            val json1 = jsonObject.optJSONObject("1")
                            tipStr = json1.getString("tip")
                            btnStr = json1.getString("btn")
                        } else if (userBean.type == 2) {
                            val json2 = jsonObject.optJSONObject("2")
                            tipStr = json2.getString("tip")
                            btnStr = json2.getString("btn")
                        } else if (userBean.type == 3) {
                            val json3 = jsonObject.optJSONObject("3")
                            tipStr = json3.getString("tip")
                            btnStr = json3.getString("btn")
                        }
                        mainViewModel!!.getSysParameter(requireContext(), 10)
                            .observe(requireActivity(), Observer { systemParam ->
                                if (null == systemParam) return@Observer
                                isOnline = systemParam.blank1
                                //todo isOnline 是判断正在上线审核标志
                                val ion = StringUtil.equals(isOnline, "isOnline1")
                                val uer = KWApplication.instance.userRole == -2
                                if (!ion && !uer) {
                                    if (!StringUtil.isEmpty(jsonObject.getString("name"))) {
                                        user_reacharge?.text = jsonObject.getString("name")
                                        user_reacharge?.setOnClickListener(object :
                                            NoMoreClickListener() {
                                            override fun OnMoreClick(view: View) {
                                                val target = jsonObject.getString("url")
                                                if (StringUtil.isEmpty(target)) {
                                                    ToastUtils.show("链接不存在！")
                                                    return
                                                }
                                                val jumpUrl = StringBuilder().append(target)
                                                if (target.contains("?")) {
                                                    jumpUrl.append("&token=")
                                                } else {
                                                    jumpUrl.append("?token=")
                                                }
                                                val randomNum = System.currentTimeMillis()
                                                jumpUrl.append(KWApplication.instance.token)
                                                    .append("&").append(randomNum)
                                                val intent =
                                                    Intent(context, WebViewActivity::class.java)
                                                intent.putExtra("url", jumpUrl.toString())
                                                requireActivity().startActivity(intent)
                                            }

                                            override fun OnMoreErrorClick() {
                                            }

                                        })
                                        user_reacharge?.visibility = View.VISIBLE
                                    } else {
                                        user_reacharge?.visibility = View.GONE
                                    }
                                }else {
                                    user_reacharge?.visibility = View.GONE
                                }
                            })

                        user_tip!!.text = tipStr
                        web_info_tv!!.text = btnStr
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                })
            KWApplication.instance.loadImg(userBean.headPic, user_head_img!!)
            val photo = sp?.getString("photoPath","")
            if (!photo.isNullOrEmpty()) {
                if (hasFile(photo)) {
                    val bitmap = BitmapFactory.decodeFile(photo)
                    user_head_img?.setImageBitmap(bitmap)
                }
            }
//            var useType = ""
//            when (userBean.type) {
//                -2 -> useType = "游客"
//                1 -> useType = "VIP"
//                2 -> useType = "团长"
//                3 -> useType = "运营商"
//            }
            user_name!!.text = userBean.phone
            card_valid!!.text = userBean.idenName
            KWApplication.instance.userRole = userBean.type
            if (userBean.balance != 0.0)
                user_balance!!.text = userBean.balance.toString()
            val sss = userBean.busTitle.split("#".toRegex()).toTypedArray()
            if (sss.size == 2) {
                explain_content!!.text = sss[0]
                user_expAmt!!.text = sss[1]
            }
            if (userBean.rewardAmt != 0.0)
                user_rewad!!.text = userBean.rewardAmt.toString()

            if (!StringUtil.isEmpty(userBean.inviteNo)) {
                card_num!!.text = "账号：" + userBean.inviteNo
                card_num!!.visibility = View.VISIBLE
            } else {
                card_num!!.visibility = View.INVISIBLE
            }
            if (userBean.type < 1) {
                income_lay!!.visibility = View.GONE
            } else {
                income_lay!!.visibility = View.VISIBLE
            }
            web_info_tv!!.setOnClickListener(object : NoMoreClickListener() {
                override fun OnMoreClick(view: View) {
                    val target = userBean.equityUrl
                    val jumpUrl = StringBuilder().append(target)
                    if (target.contains("?")) {
                        jumpUrl.append("&token=")
                    } else {
                        jumpUrl.append("?token=")
                    }
                    val randomNum = System.currentTimeMillis()
                    jumpUrl.append(KWApplication.instance.token).append("&").append(randomNum)
                    val intent = Intent(context, WebViewActivity::class.java)
                    intent.putExtra("url", jumpUrl.toString())
                    requireActivity().startActivity(intent)
                }

                override fun OnMoreErrorClick() {}
            })
        })
        mainViewModel!!.getSysOrderList(requireContext()).observe(
            requireActivity(),
            Observer<MutableList<MutableList<SysOrderBean>>?> { categoryBeans ->
                if (null == categoryBeans) return@Observer
                var categoryListNew: MutableList<MutableList<SysOrderBean>> = ArrayList()
                val categoryBeans1: MutableList<SysOrderBean> = ArrayList()
                for (list in categoryBeans) {
                    for (categoryBean in list) {
                        if (StringUtil.equals(categoryBean.title, "加油订单")
                            || StringUtil.equals(categoryBean.title, "洗车订单")
//                            || equals(categoryBean.title, "电影订票")
                        ) {
                            categoryBeans1.add(categoryBean)
                        }
                        if (StringUtil.equals(categoryBean.type, "KY_GAS")) {
                            KWApplication.instance.isGasPublic = categoryBean.isPublic
                        }
                        if (StringUtil.equals(categoryBean.type, "KY_WASH")) {
                            KWApplication.instance.isWashPublic = categoryBean.isPublic
                        }
                    }
                }
                categoryListNew.add(categoryBeans1)
                if (StringUtil.equals(isOnline, "isOnline1") || KWApplication.instance.userRole != -2
                ) {
                    categoryListNew = categoryBeans
                }
                val mColumns = 1
                val mRows = categoryListNew.size
                //                if (categoryBeans.size() <= 4) {
//                    mColumns = 4;
//                    mRows = 1;
//
//                } else {
//                    mRows = categoryBeans.size() % 4 == 0 ? categoryBeans.size() / 4 : categoryBeans.size() / 4 + 1;
//                    mColumns = 4;
//                }
                val layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(
                        R.dimen.dp_84
                    ) * mRows
                )
                layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.dp_14)
                category_rv!!.layoutParams = layoutParams
                val mLayoutManager =
                    PagerGridLayoutManager(mRows, mColumns, PagerGridLayoutManager.HORIZONTAL)
                // 系统带的 RecyclerView，无需自定义


                // 水平分页布局管理器
                mLayoutManager.setPageListener(object : PagerGridLayoutManager.PageListener {
                    override fun onPageSizeChanged(pageSize: Int) {}
                    override fun onPageSelect(pageIndex: Int) {}
                }) // 设置页面变化监听器
                category_rv!!.layoutManager = mLayoutManager
                val categoryAdapter = OrderCategoryAdapter(categoryListNew, object : ItemCallback {
                    override fun onItemCallback(position: Int, obj: Any?) {
                        val categoryBean = obj as SysOrderBean
//                        val userRole = KWApplication.instance.userRole
//                        val isPublic = categoryBean.isPublic
//                        if (userRole == -2 && isPublic == 0) {
//                            KWApplication.instance.showRegDialog(requireContext())
//                            return
//                        }
                        val target = categoryBean.href
                        if (StringUtil.equals(categoryBean.type, "KY_GAS")) {
                            startActivity(Intent(context, OilOrderListActivity::class.java))
                        } else if (StringUtil.equals(categoryBean.type, "KY_WASH")) {
                            startActivity(Intent(context, WashOrderListActivity::class.java))
                        } else {
                            if (!StringUtil.isEmpty(target)) {
                                val intent = Intent(
                                    context, WebViewActivity::class.java
                                )
                                val sb = StringBuilder()
                                sb.append(target)
                                //                                sb.append("https://www.ky808.cn/carfriend/static/cyt/text/index.html#/advertising"); 测试视屏广告链接
                                if (StringUtil.equals(categoryBean.type, "KY_H5")) {
                                    if (target!!.contains("?")) {
                                        sb.append("&token=")
                                    } else {
                                        sb.append("?token=")
                                    }
                                    sb.append(KWApplication.instance.token)
                                }
                                intent.putExtra("url", sb.toString())
                                intent.putExtra("from", "首页")
                                startActivity(intent)
                            } else {
                                MessageDialog.show(
                                    (requireContext() as AppCompatActivity),
                                    "温馨提示",
                                    "功能未开启，敬请期待"
                                )
                            }
                        }
                    }

                    override fun onDetailCallBack(position: Int, obj: Any?) {}
                })
                category_rv!!.adapter = categoryAdapter
            })

        if (isRefresh) {
            refreshLayout!!.finishRefresh()
            isRefresh = false
        }
        if (isLoadmore) {
            refreshLayout!!.finishLoadMore()
            isLoadmore = false
        }
    }

    //选择拍照还是相册
    fun openImageChooserActivity() {
        showCustomDialog()
    }




    //拍照
    private fun takeCamera() {
        KWApplication.instance.permissionsCheck(
            this@PersonalFragment.requireActivity() as BaseActivity,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            R.string.permiss_take_phone,
            object : Callback {
                override fun onSuccess() {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    cameraFilePath = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        .toString() + "//" + System.currentTimeMillis() + ".png"
                    val outputImage = File(cameraFilePath)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //7.0及以上
                        val photoUri: Uri = FileProvider.getUriForFile(
                            this@PersonalFragment.requireContext(), Constants.authority,
                            outputImage
                        )
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        intent.putExtra("return-data", true)
                    } else {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputImage))
                    }
                    startActivityForResult(intent, FILE_CAMERA_RESULT_CODE)
                }

                override fun onError() {

                }
            })
    }
    private var cameraFilePath: String? = null
    private val FILE_CHOOSER_RESULT_CODE: Int = 1
    private val FILE_CAMERA_RESULT_CODE: Int = 0
    //选择图片
    private fun takePhoto() {
        KWApplication.instance.permissionsCheck(
            this@PersonalFragment.requireActivity() as BaseActivity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            R.string.permiss_write_stor2,
            object : Callback {
                override fun onSuccess() {

                    // FIXME: 2018/12/10 从相册选择
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    intent.addFlags(
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE)
                }

                override fun onError() {

                }
            })

    }
    var isClickBottomMenu = false
    private fun showCustomDialog() {
        BottomMenu.show(
            this@PersonalFragment.requireActivity() as BaseActivity,
            arrayOf("拍照", "从相册选择"),
            object : OnMenuItemClickListener {
                public override fun onClick(text: String, index: Int) {
                    if (index == 0) {
                        // 2018/12/10 拍照
//                    requestCode = FILE_CAMERA_RESULT_CODE;
                        takeCamera()
                        isClickBottomMenu = true
                    } else if (index == 1) {
//                    requestCode = FILE_CHOOSER_RESULT_CODE;
                        // 2018/12/10 从相册选择
                        takePhoto()
                        isClickBottomMenu = true
                    } else {
//                    mUploadCallbackAboveL = null;
//                    mUploadMessage = null;
                        isClickBottomMenu = false
                    }
                }
            }).setOnDismissListener(object : OnDismissListener {
            public override fun onDismiss() {
                if (isClickBottomMenu)return

            }
        })
    }

    //    private int requestCode = -2;
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        isClickBottomMenu = false
        var resultUri: Uri? = null
        if (requestCode == FILE_CAMERA_RESULT_CODE) {
            if (data?.data != null) {
                resultUri = data.getData()
            }
            if (resultUri == null && hasFile(cameraFilePath)) {
//                resultUri = Uri.fromFile(File(cameraFilePath))
                val pictureFile = File(cameraFilePath)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    resultUri = FileProvider.getUriForFile(
                        this@PersonalFragment.requireContext(),
                        Constants.authority, pictureFile
                    )
                } else {
                    resultUri = Uri.fromFile(pictureFile)
                }
            }


            Log.e("hm", "photoUri=" + resultUri.toString())
            if (resultUri != null) {
                startPhotoZoom(resultUri)
            }

        } else if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (data != null) {
                resultUri = data.getData()
            }
            if (resultUri != null) {
                startPhotoZoom(resultUri)
            }

        }else if (requestCode == 3) {
            val bitmap = BitmapFactory.decodeFile(photoPath + cropPhoto)
            user_head_img?.setImageBitmap(bitmap)
            val editor: SharedPreferences.Editor = sp!!.edit()
            editor.putString("photoPath", photoPath + cropPhoto)
            editor.apply()
            editor.commit()

        }
    }

    /**
     * 判断文件是否存在
     */
    fun hasFile(path: String?): Boolean {
        try {
            val f: File = File(path)
            if (!f.exists()) {
                return false
            }
        } catch (e: Exception) {
            Log.i("error", e.toString())
            return false
        }
        return true
    }
    var photoPath: String? = null
    var cropPhoto: String? = null

    private fun startPhotoZoom(uri: Uri) {
        //保存裁剪后的图片
        cropPhoto = "photo.jpg"
        photoPath = KWApplication.instance.GetDataPath()+ Constants.PATH_PHOTO
        val file = File(photoPath)
        if (!file.exists()) {
            file.mkdirs()
        }
        val cropFile = File(photoPath, cropPhoto)
        try {
            if (cropFile.exists()) {
                cropFile.delete()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        val cropUri: Uri
        cropUri = Uri.fromFile(cropFile)
        val intent = Intent("com.android.camera.action.CROP")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        intent.setDataAndType(uri, "image/*")
        intent.putExtra("crop", "true")
        intent.putExtra("aspectX", 1) // 裁剪框比例
        intent.putExtra("aspectY", 1)
        intent.putExtra("outputX", 300) // 输出图片大小
        intent.putExtra("outputY", 300)
        intent.putExtra("scale", true)
        intent.putExtra("return-data", true)
        Log.e("hm", "cropUri = $cropUri")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString())
        intent.putExtra("noFaceDetection", true) // no face detection
        startActivityForResult(intent, 3)
    }
}