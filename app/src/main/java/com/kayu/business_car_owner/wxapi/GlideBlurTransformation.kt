/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.kayu.business_car_owner.wxapi

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.kayu.business_car_owner.KWApplication
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import java.security.MessageDigest

class GlideBlurTransformation(context: Context?) : BitmapTransformation() {
    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val targetWidth: Int = KWApplication.instance.displayWidth
        //    LogUtil.e("hm","displayWidth="+ targetWidth);
//    LogUtil.e("hm","source.getHeight()="+source.getHeight()+",source.getWidth()="+source.getWidth()+",targetWidth="+targetWidth);
        if (toTransform.width == 0) {
            return toTransform
        }

        //如果图片小于设置的宽度，则返回原图
        val aspectRatio = outHeight.toDouble() / outWidth
        val targetHeight = (targetWidth * aspectRatio).toInt()
        return if (targetHeight != 0 && targetWidth != 0) {
            val result =
                Bitmap.createScaledBitmap(toTransform, targetWidth, targetHeight, true)
            if (result != toTransform) {
                // Same bitmap is returned if sizes are the same
                toTransform.recycle()
            }
            result
        } else {
            toTransform
        }
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {}
}