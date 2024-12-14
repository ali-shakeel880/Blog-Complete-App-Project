package com.example.blogproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.blogproject.databinding.ActivityMainBinding
import com.example.blogproject.databinding.ActivityProfileBinding
import com.example.blogproject.register.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Profile : AppCompatActivity() {

  private val binding:ActivityProfileBinding by lazy {

      ActivityProfileBinding.inflate(layoutInflater)
  }
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


         auth=FirebaseAuth.getInstance()

        val userId=auth.currentUser?.uid

        if (userId!=null) {
            loadUserProfileImage(userId)

            val userReference =
                FirebaseDatabase.getInstance("https://blog-project-d1b2f-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child(
                    "users"
                ).child(userId)

            userReference.child("name").addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name=snapshot.getValue(String::class.java)
                    binding.profileName.text=name
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })


        }
        binding.logout.setOnClickListener {
            if (userId != null) {

                auth.signOut()
                val intent = Intent(this, WelcomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()


            }
        }
        binding.logoutText.setOnClickListener {
            if (userId != null) {

                auth.signOut()
                startActivity(Intent(this,WelcomeActivity::class.java))
                finish()

            }
        }
        binding.addNewArticleIcon.setOnClickListener {

            startActivity(Intent(this,AddArticleActivity::class.java))
        }
binding.addNewArticleText.setOnClickListener {

    startActivity(Intent(this,AddArticleActivity::class.java))
}

        ///to be continue
binding.yourArticleIcon.setOnClickListener {
    startActivity(Intent(this,YourArticlesActivity::class.java))

}
        binding.yourArticleText.setOnClickListener {
            startActivity(Intent(this,YourArticlesActivity::class.java))

        }

    }
    private fun loadUserProfileImage(userId:String) {
        val userReference= FirebaseDatabase.getInstance("https://blog-project-d1b2f-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("users").child(userId)


        userReference.child("profileImage").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl=snapshot.getValue(String::class.java)

                Glide.with(this@Profile)
                    .load(profileImageUrl)
                    .into(binding.imageViewItem)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Profile,"Error Loading Profile Image", Toast.LENGTH_SHORT).show()
            }


        })

    }
}