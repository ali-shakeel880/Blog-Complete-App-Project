package com.example.blogproject.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogproject.EditBlogActivity
import com.example.blogproject.Model.BlogItemModel
import com.example.blogproject.ReadMoreActivity
import com.example.blogproject.databinding.BlogYourItemBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.tasks.await


class ArticlesAdapter(private val items:MutableList<BlogItemModel>): RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder>() {
    inner class ArticleViewHolder(private val binding: BlogYourItemBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(blogItemModel: BlogItemModel){

            val context=binding.root.context
            binding.itemHeading.text=blogItemModel.heading
            Glide.with(binding.imageViewItem.context)
                .load(blogItemModel.profileImage)
                .into(binding.imageViewItem)
            binding.itemBloggerAurthorName.text=blogItemModel.userName
            binding.dateItem.text=blogItemModel.date
            binding.blogTextItem.text=blogItemModel.post

            binding.readMoreButtonItem.setOnClickListener {

                val context=binding.root.context

                val intent= Intent(context,ReadMoreActivity::class.java)
                intent.putExtra("blogItem",blogItemModel)

                context.startActivity(intent)

            }
            binding.deleteButtonItem.setOnClickListener {
                val blogsReference = FirebaseDatabase.getInstance("https://blog-project-d1b2f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("blogs")
blogsReference.addValueEventListener(object :ValueEventListener{
    override fun onDataChange(snapshot: DataSnapshot) {
       for (postSnapshot in snapshot.children){

           val blogData=postSnapshot.getValue(BlogItemModel::class.java)
           if(postSnapshot!=null && blogData?.postId==blogItemModel.postId){
               blogsReference.child("postId").removeValue()

           }

       }
    }

    override fun onCancelled(error: DatabaseError) {
        TODO("Not yet implemented")
    }


})

            }

            binding.deleteButtonItem.setOnClickListener {
                val blogsReference = FirebaseDatabase.getInstance("https://blog-project-d1b2f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("blogs")
                blogsReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (postSnapshot in snapshot.children) {
                            val blogData = postSnapshot.getValue(BlogItemModel::class.java)
                            if (blogData?.postId == blogItemModel.postId) {
                                Log.d("dtaaa","${blogItemModel.postId}")
                                // Use postSnapshot.key to ensure you're referencing the correct node to remove
                                postSnapshot.key?.let {
                                    blogsReference.child(it).removeValue().addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            // Successfully deleted the post
                                          Toast.makeText(context,"Successfully deleted blog",Toast.LENGTH_SHORT).show()
                                        } else {
                                            // Handle failure
                                            Toast.makeText(context,"Failed to delete blog",Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle possible errors
                        println("Database error: ${databaseError.message}")
                    }
                })
            }

            binding.editButtonItem.setOnClickListener {

           val intent=Intent(context,EditBlogActivity::class.java)

                intent.putExtra("data",blogItemModel)

                context.startActivity(intent)



            }





        }





        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
     val view= LayoutInflater.from(parent.context)
        val binding=BlogYourItemBinding.inflate(view,parent,false)
        return ArticleViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(items[position])
    }

}