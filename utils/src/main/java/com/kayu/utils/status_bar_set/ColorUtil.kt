package com.kayu.utils.status_bar_set

import java.util.*

object ColorUtil {
    /**
     * 获取十六进制的颜色代码.例如  "#5A6677"
     * 分别取R、G、B的随机值，然后加起来即可
     *
     * @return String
     */
    val randColor: String
        get() {
            var R: String
            var G: String
            var B: String
            val random = Random()
            R = Integer.toHexString(random.nextInt(256)).toUpperCase()
            G = Integer.toHexString(random.nextInt(256)).toUpperCase()
            B = Integer.toHexString(random.nextInt(256)).toUpperCase()
            R = if (R.length == 1) "0$R" else R
            G = if (G.length == 1) "0$G" else G
            B = if (B.length == 1) "0$B" else B
            return "#80$R$G$B"
        }
}