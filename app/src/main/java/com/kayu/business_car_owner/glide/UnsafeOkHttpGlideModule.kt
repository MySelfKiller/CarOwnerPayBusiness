package com.kayu.business_car_owner.glide

import android.content.Context
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import java.io.InputStream

@GlideModule
class UnsafeOkHttpGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
//        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, "glide_cache", 1025*1024*50));
        //指定内存缓存大小
//        builder.setMemoryCache(new LruResourceCache(1024*3));
//        //全部的内存缓存用来作为图片缓存
//        builder.setBitmapPool(new LruBitmapPool(1024*6));
        // Apply options to the builder here.
//        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
//        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory())
    }
}