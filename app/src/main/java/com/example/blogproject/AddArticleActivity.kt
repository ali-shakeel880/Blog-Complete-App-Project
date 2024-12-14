package com.example.blogproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.blogproject.Model.BlogItemModel
import com.example.blogproject.Model.UserData
import com.example.blogproject.databinding.ActivityAddArticleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class AddArticleActivity : AppCompatActivity() {

    private val binding:ActivityAddArticleBinding by lazy{

        ActivityAddArticleBinding.inflate(layoutInflater)
    }

    private val dataBaseReference:DatabaseReference=FirebaseDatabase.getInstance("https://blog-project-d1b2f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("blogs")
    private val userReference:DatabaseReference=FirebaseDatabase.getInstance("https://blog-project-d1b2f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users")
    private val auth=FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.addBlogButton.setOnClickListener{

            val title=binding.addBlogTitle.text.toString()
            val description=binding.addBlogDescription.text.toString()

            if(title.isEmpty()  || description.isEmpty()){

                Toast.makeText(this,"Please Fill All The Fields", Toast.LENGTH_SHORT).show()
            }
val user:FirebaseUser?=auth.currentUser

            if(user!=null){

                val userId=user.uid

               val userName=user.displayName?:"Anonymous"

                val userImageUrl=user.photoUrl?:""

                // fetch user name and user profile form data base

                userReference.child(userId).addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userData=snapshot.getValue(UserData::class.java)

                        if(userData!=null) {
                            Log.d("mnbv","Log 1 $userData")
                       val userNameFromDB=userData.name
                            val userImageUrlFromDB=userData.profileImage
                            val currentDate= SimpleDateFormat("yyyy-MM-dd").format(Date())
                             val uswerId=auth.currentUser?.uid

                            val blogItem=BlogItemModel(
                                title,userNameFromDB,currentDate,description,uswerId,0,userImageUrlFromDB,
                            )

                            val key=dataBaseReference.push().key

                            if(key!=null){
                                blogItem.postId=key
                                Log.d("mnbv","Log 2 key check $key")
                                dataBaseReference.child(key).setValue(blogItem).addOnCompleteListener{
                                    if(it.isSuccessful){
                                        Log.d("mnbv","Log 3 successfully store")
                                        Toast.makeText(this@AddArticleActivity,"Add Article Successfully",Toast.LENGTH_SHORT).show()
                                        finish()
                                    }else{
Toast.makeText(this@AddArticleActivity,"Add Article Failed",Toast.LENGTH_SHORT).show()

                                    }
                                }
                            }
                        }
                        }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("mnbv", "DatabaseError: ${error.message}")
                    }





                })





            }




    }
}}