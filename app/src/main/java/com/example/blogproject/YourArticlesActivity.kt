package com.example.blogproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogproject.Adapter.ArticlesAdapter
import com.example.blogproject.Adapter.BlogAdapter
import com.example.blogproject.Model.BlogItemModel
import com.example.blogproject.databinding.ActivityYourArticlesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class YourArticlesActivity : AppCompatActivity() {

    private val binding:ActivityYourArticlesBinding by lazy{

        ActivityYourArticlesBinding.inflate(layoutInflater)
    }

    private val myItems= mutableListOf<BlogItemModel>()
    private  var databaseReference: DatabaseReference = FirebaseDatabase.getInstance("https://blog-project-d1b2f-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    private lateinit var articlesAdapter: ArticlesAdapter
    private var auth= FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.arrowButton.setOnClickListener {

            finish()
        }

        val recyclerView=binding.recyclerViewSavePosts
       articlesAdapter= ArticlesAdapter(myItems)
        recyclerView.adapter=articlesAdapter
        recyclerView.layoutManager= LinearLayoutManager(this)

        val postReference=databaseReference.child("blogs")

        val currentUserId=auth.currentUser?.uid

        Log.d("allArticlesss","outside courruintine")
        postReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedSaveItems = mutableListOf<BlogItemModel>()
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d("allArticlesss","inside courruintine")
                for(postSnapshot in snapshot.children){
                    val allArticles=postSnapshot.getValue(BlogItemModel::class.java)
                    Log.d("allArticlesss","1===$allArticles")
                    Log.d("allArticlesss","Current Id=$currentUserId==${allArticles?.userId}")
                    if (allArticles!=null && currentUserId==allArticles?.userId){
                        Log.d("allArticlesss","2===$allArticles")
                            updatedSaveItems.add(allArticles)

                    }
                }
                    launch(Dispatchers.Main) {
                        myItems.clear()
                        myItems.addAll(updatedSaveItems)
                        Log.d("allArticlesss","3===$myItems")
                        articlesAdapter.notifyDataSetChanged()
                    }
            }}

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }
}