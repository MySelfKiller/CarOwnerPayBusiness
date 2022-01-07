package com.kayu.business_car_owner.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.kayu.business_car_owner.*
import com.kayu.business_car_owner.R
import com.kayu.business_car_owner.activity.login.LoginAutoActivity
import com.kayu.utils.*

class OrderDetailsActivity constructor() : AppCompatActivity() {
    private var shipment_number: TextView? = null
    private var card_num: TextView? = null
    private var activation_code: TextView? = null
    private var ask_btn: AppCompatButton? = null
    private var waybillNo: String? = null
    private var cardNo: String? = null
    private var cardCode: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)
        waybillNo = getIntent().getStringExtra("waybillNo")
        cardNo = getIntent().getStringExtra("cardNo")
        cardCode = getIntent().getStringExtra("cardCode")

        //标题栏
//        LinearLayout title_lay = findViewById(R.id.title_lay);
//        title_lay.setBackgroundColor(getResources().getColor(R.color.background_gray));
        val title_name: TextView = findViewById(R.id.title_name_tv)
        title_name.setText("查询订单")
        findViewById<View>(R.id.title_back_btu).setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }

            override fun OnMoreErrorClick() {}
        })
        //        TextView back_tv = view.findViewById(R.id.title_back_tv);
//        back_tv.setText("我的");
        shipment_number = findViewById(R.id.details_shipment_number)
        card_num = findViewById(R.id.details_card_num)
        activation_code = findViewById(R.id.details_activation_code)
        ask_btn = findViewById(R.id.details_ask_btn)
        ask_btn?.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val intent: Intent =
                    Intent(this@OrderDetailsActivity, LoginAutoActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

            override fun OnMoreErrorClick() {}
        })
        if (StringUtil.isEmpty(waybillNo)) {
            shipment_number?.text = "未发货"
        } else {
            shipment_number?.text = waybillNo
        }
        card_num?.text = cardNo
        activation_code?.text = cardCode
    }
}