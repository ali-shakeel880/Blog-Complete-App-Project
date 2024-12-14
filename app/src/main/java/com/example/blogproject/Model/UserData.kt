package com.example.blogproject.Model

data class UserData(

    val name:String="",
    val email:String="",
    val profileImage:String=""
){
    constructor():this("","","")
}
