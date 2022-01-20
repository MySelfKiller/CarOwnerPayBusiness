package com.kayu.business_car_owner.model

data class ProductSortBean(
    val id: Int,
    val name: String,
    val products: MutableList<Product>,
    val tag: String,
    val tips: String,
    val sort: Int
)