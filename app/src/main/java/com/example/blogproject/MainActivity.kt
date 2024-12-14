package com.example.blogproject


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.blogproject.Adapter.BlogAdapter
import com.example.blogproject.Model.BlogItemModel
import com.example.blogproject.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.EventListener


class MainActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference

    private val blogItems= mutableListOf<BlogItemModel>()
    private lateinit  var blogAdapter:BlogAdapter

    private lateinit var auth: FirebaseAuth
private val binding:ActivityMainBinding by lazy {
    ActivityMainBinding.inflate(layoutInflater)

}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth= FirebaseAuth.getInstance()

        databaseReference=FirebaseDatabase.getInstance("https://blog-project-d1b2f-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("blogs")

        val userId=auth.currentUser?.uid

        if (userId!=null){
            loadUserProfileImage(userId)

        }


        //to see the save blogs

        binding.saveButtonTopOfHomeScreen.setOnClickListener {


            startActivity(Intent(this,SaveBlogsActivity::class.java))


        }

        binding.profileImageView.setOnClickListener {
            startActivity(Intent(this,Profile::class.java))

        }



        // Set blog post

        //initialize recyclerView and setBlog Post


        val recyclerView=binding.recyclerView
         blogAdapter=BlogAdapter(blogItems)
        recyclerView.adapter=blogAdapter
        recyclerView.layoutManager=LinearLayoutManager(this)

        // fetch Data From the firebase

        databaseReference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                blogItems.clear()
               for (snapshot in snapshot.children){

                   val blogItem=snapshot.getValue(BlogItemModel::class.java)
                   Log.d("dataaa","$blogItem")
                   if(blogItem!=null){
                       blogItems.add(blogItem)

                   }
               }
                Log.d("dataaa","$blogItems")
                // ta ke blogs uper show jo be add hoo 
                blogItems.reverse()


                blogAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
             Toast.makeText(this@MainActivity,"Blogs loading failed",Toast.LENGTH_SHORT).show()
            }


        })


binding.floatingPlusButton.setOnClickListener{

    startActivity(Intent(this,AddArticleActivity::class.java))

}

    }

    override fun onRestart() {
        super.onRestart()
        blogAdapter.notifyDataSetChanged()
    }

    private fun loadUserProfileImage(userId:String) {
       val userReference=FirebaseDatabase.getInstance("https://blog-project-d1b2f-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("users").child(userId)


        userReference.child("profileImage").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl=snapshot.getValue(String::class.java)

                Glide.with(this@MainActivity)
                    .load(profileImageUrl)
                    .into(binding.profileImageView)
            }

            override fun onCancelled(error: DatabaseError) {
              Toast.makeText(this@MainActivity,"Error Loading Profile Image",Toast.LENGTH_SHORT).show()
            }


        })

    }


}