package com.kayu.utils

import android.content.Context
import android.os.Environment
import android.view.ViewGroup
import android.widget.GridView
import java.io.File
import java.lang.Exception
import java.nio.charset.Charset
import java.util.*

object Utils {
    fun GetExternalStroragePath(context: Context?): String? {
        // 得到存储卡路径
        var sdDir: File? = null
        val sdCardExist = Environment.getExternalStorageState() ==
                Environment.MEDIA_MOUNTED // 判断sd卡
        // 或可存储空间是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory() // 获取sd卡或可存储空间的跟目录
            LogUtil.e("main", "得到的根目录路径:$sdDir")
            return sdDir.toString()
        }
        return null
    }

    private fun checkfile(path: String): Boolean {
        val file = File(path)
        //    	return createDirectory(file);
        return if (!file.isDirectory) {
            if (!file.mkdirs()) {
                false
            } else {
                try {
                    file.setExecutable(true, false)
                    file.setWritable(true, false)
                    file.setReadable(true, false)
                } catch (e: NoSuchMethodError) {
                    e.printStackTrace()
                }
                true
            }
        } else {
            try {
                file.setExecutable(true, false)
                file.setWritable(true, false)
                file.setReadable(true, false)
            } catch (e: NoSuchMethodError) {
                e.printStackTrace()
            }
            true
        }
    }

    fun getEnaviBaseStorage(context: Context): String {
        var map_base_path: String? = ""
        map_base_path = GetExternalStroragePath(context)
        if (map_base_path != null && map_base_path.length > 2) {
            map_base_path = (map_base_path
                    + File.separator
                    + Constants.PATH_ROOT + File.separator)
            if (checkfile(map_base_path)) {
                return map_base_path
            }
        }
        map_base_path = context.filesDir.toString()
        if (map_base_path != null && map_base_path.length > 2) {
            val file = File(map_base_path)
            if (file.isDirectory) {
                return map_base_path
            }
        }
        return map_base_path
    }

    fun ByteToM(bytes: Long): String {
        var M = bytes / 1024 / 1024
        val p = bytes / 1024 - bytes / 1024 / 1024 * 1024
        var pd: Long = 0
        if (p > 950) {
            M += 1
        } else if (p >= 100) {
            pd = p / 100
        } else if (M == 0L) {
            pd = 1
        }
        return if (pd == 0L) M.toString() + "M" else M.toString() + "." + pd + "M"
    }

    fun setListViewHeightBasedOnChildren(listView: GridView) {
        // 获取listview的adapter
        val listAdapter = listView.adapter ?: return
        // 固定列宽，有多少列
        val col = 4 // listView.getNumColumns();
        var totalHeight = 0
        // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加
        var i = 0
        while (i < listAdapter.count) {

            // 获取listview的每一个item
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(0, 0)
            // 获取item的高度和
            totalHeight += listItem.measuredHeight
            i += col
        }
        // 获取listview的布局参数
        val params = listView.layoutParams
        // 设置高度
        params.height = totalHeight
        // 设置margin
        (params as ViewGroup.MarginLayoutParams).setMargins(10, 10, 10, 10)
        // 设置参数
        listView.layoutParams = params
    }

    fun readAssert(context: Context, fileName: String?): String {
        val jsonString = ""
        var resultString = ""
        try {
            val inputStream = context.resources.assets.open(fileName!!)
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            resultString = String(buffer, Charset.defaultCharset())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return resultString
    }

    /**
     * 比较是否是同一天
     * @param currentTime
     * @param lastTime
     * @return
     */
    fun isSameData(currentTime: Date?, lastTime: Date?): Boolean {
        try {
            val nowCal = Calendar.getInstance()
            val dataCal = Calendar.getInstance()
            nowCal.time = currentTime
            dataCal.time = lastTime
            return isSameDay(nowCal, dataCal)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun isSameDay(cal1: Calendar?, cal2: Calendar?): Boolean {
        return if (cal1 != null && cal2 != null) {
            cal1[Calendar.ERA] == cal2[Calendar.ERA] && cal1[Calendar.YEAR] == cal2[Calendar.YEAR] && cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR]
        } else {
            false
        }
    }
}