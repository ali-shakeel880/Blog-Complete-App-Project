package com.example.blogproject.Model

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi

data class BlogItemModel(val heading:String?=null, val userName:String?=null, val date:String?=null, val post:String?=null,val userId:String?=null,
                         var likeCount:Int=0, val profileImage:String?=null, var isSaved:Boolean=false,var postId:String?=null, val likedBy:MutableList<String>?=null):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:null,
        parcel.readString()?:null,
        parcel.readString()?:null,
        parcel.readString()?:null,
        parcel.readString(),
        parcel.readInt(),
        parcel.readString()?:null,
        parcel.readByte()!=0.toByte(),
        parcel.readString()?:null,
        parcel.createStringArrayList(),



    ) {
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(heading)
        parcel.writeString(userName)
        parcel.writeString(date)
        parcel.writeString(post)
        parcel.writeString(userId)
        parcel.writeInt(likeCount)
        parcel.writeString(profileImage)
        parcel.writeBoolean(isSaved)
        parcel.writeString(postId)
        parcel.writeStringList(likedBy)


    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BlogItemModel> {
        override fun createFromParcel(parcel: Parcel): BlogItemModel {
            return BlogItemModel(parcel)
        }

        override fun newArray(size: Int): Array<BlogItemModel?> {
            return arrayOfNulls(size)
        }
    }
}
