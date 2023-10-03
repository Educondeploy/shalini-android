package com.tt.skolarrs.model

data class Menu(
    val image: Int?,
    val tittle: String,
    var isSelected: Boolean,
    var id: Int,
    val menuList: ArrayList<SubMenu>
)

