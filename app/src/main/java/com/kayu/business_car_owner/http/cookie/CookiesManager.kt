package com.kayu.business_car_owner.http.cookie

import com.kayu.business_car_owner.KWApplication
import com.kayu.business_car_owner.http.NetUtil
import com.kayu.utils.LogUtil
import okhttp3.*

class CookiesManager     //    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    (
    private val cookieStore: PersistentCookieStore,
    private val setTokenCallBack: SetTokenCallBack?
) : CookieJar {
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
//        LogUtil.e("hm","------saveFromResponse-----");
        if (cookies.isNotEmpty()) {
            for (item in cookies) {
//                LogUtil.e("hm","cookieItem respones = name:"+item.name()+"----value:"+item.value());
                if (item.name() == "token") {
                    NetUtil.token = item.value()
                    setTokenCallBack?.setToken(item.value())

                    LogUtil.e("hm","------token_key-----"+KWApplication.instance.token)
                }
                LogUtil.e("hm","------saveFromResponse-----")
                LogUtil.e("hm","cookieItem request = name:"+item.name()+"----value:"+item.value())

                cookieStore.add(url, item)
            }
            //            cookieStore.put(url.host(), cookies);
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
//        SharedPreferences sp = KWApplication.getInstance().getApplicationContext().getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE);
////        boolean isLogin = sp.getBoolean(Constants.isLogin,false);
////        if (!isLogin){
//////            cookieStore.removeAll();
////            List<Cookie> cookies = cookieStore.getCookies();
////            for (Cookie item : cookies){
////                if (item.name().equals("token")){
////                    cookieStore.remove(url,item);
////
////                }
////            }
////        }
        val cookies = cookieStore[url]
        //        if ( null == cookies || cookies.size()==0){
//            cookies.add(new Cookie.Builder().name("token").value("fAM3WaRwKsJPigEIlBfHWXxk0z+UsCgq1tN4Yxas74upza4rpEEX1nUrV/a4RUSBUeQmmOrs2" +
//                    "lffXHX5kGgWWL1PmGqS6gXXvbilvwc7T39SR2oPtCYtlqz74gNOrzrjXgbXBSvG0Oh8ANnU9mLfQhP81frSXe6ID1JYr0CVEEqiUqkSPsI" +
//                    "7tKAy2LRgD8gGCQ/eq6kbt/Kh+B+a9x4g3YqGx7u5QumRV1SQXPEi1GOIj923Qhdy419z2d7aRGNI2c4HfhNbrCS0Bt080RS7rThrl+pZRe" +
//                    "r3Twaj/fRdKYFVAly4FmN7JjHqD80HdRs+lPbdRTF3ync41N55UdEPw7hNNGrLxZEcImo9IDSeREE=").domain("www.kakayuy.com").build());
//        }
//        List<Cookie> cookies = cookieStore.get(url.host());

        LogUtil.e("hm","------loadForRequest-----")
        for (item in cookies) {
                LogUtil.e("hm","cookieItem request = name:"+item.name()+"----value:"+item.value())
//            cookieStore.add(url, item);
            if (item.name() == "token") {
                NetUtil.token = item.value()
                setTokenCallBack?.setToken(item.value())
                                    LogUtil.e("hm","------token_key-----"+ KWApplication.instance.token)
            }
        }
        return cookies

//        return cookies;
    }
}