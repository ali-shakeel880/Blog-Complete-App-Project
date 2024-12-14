package com.example.blogproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogproject.Adapter.BlogAdapter
import com.example.blogproject.Model.BlogItemModel
import com.example.blogproject.databinding.ActivitySaveBlogsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SaveBlogsActivity : AppCompatActivity() {


    private val saveItems= mutableListOf<BlogItemModel>()
    private  var databaseReference: DatabaseReference= FirebaseDatabase.getInstance("https://blog-project-d1b2f-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
private lateinit var blogAdapter:BlogAdapter
    private var auth= FirebaseAuth.getInstance()
    private val binding: ActivitySaveBlogsBinding by lazy {
        ActivitySaveBlogsBinding.inflate(layoutInflater)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if(saveItems.isNotEmpty()){
            binding.noSaveBlogsTextView.visibility=View.INVISIBLE}else{ binding.noSaveBlogsTextView.visibility=View.VISIBLE}

        binding.arrowButton.setOnClickListener {

            finish()
        }

blogAdapter= BlogAdapter(saveItems.filter { it.isSaved }.toMutableList())


        val recyclerView=binding.recyclerViewSavePosts


        recyclerView.adapter=blogAdapter
        recyclerView.layoutManager= LinearLayoutManager(this)

        // fetch Data From the firebase
val userId=auth.currentUser?.uid
        if(userId!=null){
        val blogsReference = databaseReference.child("blogs") // Reference to the blogs collection
        val userReference = FirebaseDatabase.getInstance("https://blog-project-d1b2f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users").child(userId).child("saveBlogPosts")


            userReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val updatedSaveItems = mutableListOf<BlogItemModel>()

                    CoroutineScope(Dispatchers.IO).launch {

                        for (postSnapshot in snapshot.children) {
                            val postId = postSnapshot.key
                            val isSaved = postSnapshot.value as Boolean

                            if (postId != null && isSaved) {
                                val blogItem = fetchBlogItem(postId)
                                if (blogItem != null) {
                                    updatedSaveItems.add(blogItem)
                                }
                            }
                        }


                        launch(Dispatchers.Main) {
                            saveItems.clear()
                            saveItems.addAll(updatedSaveItems)
                            if(saveItems.isNotEmpty()){
                                binding.noSaveBlogsTextView.visibility=View.INVISIBLE}else{ binding.noSaveBlogsTextView.visibility=View.VISIBLE}
                            blogAdapter.updateData(saveItems)
                        }
                    }
                }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SaveBlogsActivity,"Blogs loading failed",Toast.LENGTH_SHORT).show()
            }


        })

    }



    }

    private suspend fun fetchBlogItem(postId: String): BlogItemModel? {
        val blogsReference = FirebaseDatabase.getInstance("https://blog-project-d1b2f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("blogs")

        return try {
            val dataSnapshot=blogsReference.child(postId).get().await()

            val blogData=dataSnapshot.getValue(BlogItemModel::class.java)
           blogData
        }catch (e:Exception){
            null
        }


    }


}