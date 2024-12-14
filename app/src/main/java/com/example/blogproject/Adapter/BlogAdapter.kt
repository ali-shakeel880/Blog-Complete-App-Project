package com.example.blogproject.Adapter

import com.example.blogproject.Model.BlogItemModel
import com.example.blogproject.R


import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogproject.ReadMoreActivity
import com.example.blogproject.databinding.BlogItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class BlogAdapter(private val items:MutableList<BlogItemModel>) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    private  var databaseReference: DatabaseReference= FirebaseDatabase.getInstance("https://blog-project-d1b2f-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        private var currentUser=FirebaseAuth.getInstance().currentUser


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {

        val view = LayoutInflater.from(parent.context)
           val binding=BlogItemBinding.inflate(view,parent,false)

        return BlogViewHolder(binding)
    }


    override fun onBindViewHolder(viewHolder: BlogViewHolder, position: Int) {
        viewHolder.bind(items[position])

    }


    override fun getItemCount():Int {

        return  items.size

    }






    inner class BlogViewHolder(private val binding:BlogItemBinding):
        RecyclerView.ViewHolder(binding.root){
fun bind(blogItemModel: BlogItemModel){
    val postId: String = blogItemModel.postId.toString()
    val context=binding.root.context
    binding.itemHeading.text=blogItemModel.heading
    Glide.with(binding.imageViewItem.context)
        .load(blogItemModel.profileImage)
        .into(binding.imageViewItem)
     binding.itemBloggerAurthorName.text=blogItemModel.userName
    binding.dateItem.text=blogItemModel.date
    binding.blogTextItem.text=blogItemModel.post
    binding.likeCountItem.text=blogItemModel.likeCount.toString()


    binding.readMoreButtonItem.setOnClickListener {

     val context=binding.root.context

        val intent=Intent(context,ReadMoreActivity::class.java)
        intent.putExtra("blogItem",blogItemModel)

        context.startActivity(intent)

    }


// check if the user like the post or not and update the like button

val postLikeReference=databaseReference.child("blogs").child(postId).child("likes")
    val currentUserLiked=currentUser?.uid?.let { uid->

        postLikeReference.child(uid).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               if(snapshot.exists()){
                   Log.d("onDataChangeeee","LikeeeIconnsssss")
binding.heartButtonItem.setImageResource(R.drawable.heart_red_filled)

               }else{
                   binding.heartButtonItem.setImageResource(R.drawable.heart_not_filled)
                   
               }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }
    // handle the like button clicks

    binding.heartButtonItem.setOnClickListener {

        if (currentUser!=null)
        {
            handleLikeButton(postId,blogItemModel,binding)
        }else{
            Toast.makeText(context,"You have to login first",Toast.LENGTH_SHORT).show()
        }

    }

    val userReference=databaseReference.child("users").child(currentUser?.uid?:"")
    val postSaveReference=userReference.child("saveBlogPosts").child(postId)
    postSaveReference.addListenerForSingleValueEvent(object :ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            if(snapshot.exists()) {
                Log.d("onDataChangeeee","saveIconnsssss")
                binding.saveButtonItem.setImageResource(R.drawable.save_filled_img)
            }else{
                binding.saveButtonItem.setImageResource(R.drawable.save_unfilled_img)

                    }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("DatabaseError", "Error: ${error.message}")
        }


    })


    binding.saveButtonItem.setOnClickListener {
        if (currentUser!=null)
        {
            handleSaveButton(postId,blogItemModel,binding)
        }else{
            Toast.makeText(context,"You have to login first",Toast.LENGTH_SHORT).show()
        }
    }


}}



    private fun handleLikeButton(postId: String, blogItemModel: BlogItemModel, binding: BlogItemBinding) {

            val userReference=databaseReference.child("users").child(currentUser!!.uid)
            val postLikeReference = databaseReference.child("blogs").child(postId).child("likes")


            postLikeReference.child(currentUser!!.uid).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){

userReference.child("likes").child(postId).removeValue()
    .addOnSuccessListener {
        postLikeReference.child(currentUser!!.uid).removeValue()
          blogItemModel.likedBy?.remove(currentUser!!.uid)

        updateLikeButtonImage(binding,false)

        //decrement the like in the database
        val newLikeCount=blogItemModel.likeCount-1

        blogItemModel.likeCount=newLikeCount

        databaseReference.child("blogs").child(postId).child("likeCount").setValue(newLikeCount)

        notifyDataSetChanged()
        Log.d("LikedButtonClicked","onDataChange success to unlike the blog$")

    }
    .addOnFailureListener { e->
        Log.d("LikedButtonClicked","onDataChange Failed to unlike the blog$e")
    }
                    }else{
                        //user has not like the post so like it
                        postLikeReference.child(currentUser!!.uid).setValue(true)
                        userReference.child("likes").child(postId).setValue(true)
                            .addOnSuccessListener {
                                blogItemModel.likedBy?.add(currentUser!!.uid)
                                updateLikeButtonImage(binding, true)

                                //increment the like count in the data base
                                val newLikeCount = blogItemModel.likeCount + 1
                                blogItemModel.likeCount = newLikeCount
                                databaseReference.child("blogs").child(postId).child("likeCount")
                                    .setValue(newLikeCount)
                                notifyDataSetChanged()
                                Log.d("LikedButtonClicked","onDataChange success to like the blog")
                            }
                            .addOnFailureListener { e->
                                Log.d("LikedButtonClicked","onDataChange Failed to like the blog$e")
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("DatabaseError", "Error: ${error.message}")
                }


            })

        }

    private fun updateLikeButtonImage(binding: BlogItemBinding, liked: Boolean) {

        if(liked){
            binding.heartButtonItem.setImageResource(R.drawable.heart_red_filled)
        }else{
            binding.heartButtonItem.setImageResource(R.drawable.heart_not_filled)
        }

    }
    private fun handleSaveButton(postId: String, blogItemModel: BlogItemModel, binding: BlogItemBinding) {

val userReference=databaseReference.child("users").child(currentUser!!.uid)

        userReference.child("saveBlogPosts").child(postId).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    //the blog is currently saved so un save it

                    userReference.child("saveBlogPosts").child(postId).removeValue()
                        .addOnSuccessListener {
                            //update ui
                            val clickedBlogItem=items.find { it.postId==postId }
                            clickedBlogItem?.isSaved=false
                            val context=binding.root.context
                            Toast.makeText(context,"BLog unsaved !",Toast.LENGTH_SHORT).show()


                        }.addOnFailureListener {
                            val context=binding.root.context
                            Toast.makeText(context,"Failed to Unsaved Blog!",Toast.LENGTH_SHORT).show()

                        }

                    binding.saveButtonItem.setImageResource(R.drawable.save_unfilled_img)
                }else{

                    //the blog is not saved so saved it
                    userReference.child("saveBlogPosts").child(postId).setValue(true)
                        .addOnSuccessListener {
                            //update ui
                            val clickedBlogItem=items.find { it.postId==postId }
                            clickedBlogItem?.isSaved=true
                            notifyDataSetChanged()

                            val context=binding.root.context
                            Toast.makeText(context,"Blog! Saved",Toast.LENGTH_SHORT).show()


                        }.addOnFailureListener {
                            val context=binding.root.context
                            Toast.makeText(context,"Blog! failed to Save blog",Toast.LENGTH_SHORT).show()


                        }
//change the save button icon
                    binding.saveButtonItem.setImageResource(R.drawable.save_filled_img)

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    fun updateData(saveItems: List<BlogItemModel>) {
        items.clear()
        items.addAll(saveItems)
        notifyDataSetChanged()
    }

}








