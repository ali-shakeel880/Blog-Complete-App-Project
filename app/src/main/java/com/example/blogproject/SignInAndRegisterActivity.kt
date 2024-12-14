package com.example.blogproject

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogproject.Model.UserData
import com.example.blogproject.databinding.ActivitySignInAndRegisterBinding
import com.example.blogproject.register.WelcomeActivity
import com.google.android.play.core.integrity.e
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class SignInAndRegisterActivity : AppCompatActivity() {
    private val binding:ActivitySignInAndRegisterBinding by lazy{
        ActivitySignInAndRegisterBinding.inflate(layoutInflater)
    }
private lateinit var auth:FirebaseAuth
private lateinit var database:FirebaseDatabase
private lateinit var storage:FirebaseStorage
private val PICK_IMAGE_REQUEST=1
    private var imageUri: Uri? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val action= intent.getStringExtra("action")
// initializing fire base things:
        auth= FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance("https://blog-project-d1b2f-default-rtdb.asia-southeast1.firebasedatabase.app/")
        storage=FirebaseStorage.getInstance()

//        setting Visibilty
if(action=="login"){
    binding.loginButton.visibility= View.VISIBLE
    binding.emailEditTextLogin.visibility=View.VISIBLE
    binding.passwordEditTextLogin.visibility=View.VISIBLE

    binding.cardViewForImage.visibility=View.GONE
    binding.nameEditTextRegister.visibility=View.GONE
    binding.emailEditTextRegister.visibility=View.GONE
    binding.passwordEditTextRegister.visibility=View.GONE

    binding.newHereTextView.alpha=0.5f
    binding.newHereTextView.isEnabled=false

    binding.registerButton.alpha=0.5f
    binding.registerButton.isEnabled=false

    binding.loginButton.setOnClickListener{

        val loginEmail=binding.emailEditTextLogin.text.toString()
        val loginPassword=binding.passwordEditTextLogin.text.toString()

        if(loginEmail.isEmpty() || loginPassword.isEmpty()){

            Toast.makeText(this,"Please Fill All The Details",Toast.LENGTH_SHORT).show()
        }else{

auth.signInWithEmailAndPassword(loginEmail,loginPassword)
    .addOnCompleteListener{task->
       if(task.isSuccessful){

           Toast.makeText(this,"Login Successfully",Toast.LENGTH_SHORT).show()
           startActivity(Intent(this,MainActivity::class.java))
           finish()
       }else{

           Toast.makeText(this,"Please Enter correct Details",Toast.LENGTH_SHORT).show()
       }

    }

        }


    }



}else if(action=="register"){

    binding.loginButton.alpha=0.5f
    binding.loginButton.isEnabled=false

    binding.registerButton.setOnClickListener{

     val registerName=binding.nameEditTextRegister.text.toString()
     val registerEmail=binding.emailEditTextRegister.text.toString()
     val registerPassword=binding.passwordEditTextRegister.text.toString()


        if(registerName.isEmpty() || registerEmail.isEmpty() || registerPassword.isEmpty())
        {
            Toast.makeText(this,"Please Fill All The Credentials", Toast.LENGTH_SHORT).show()

        }else{
            auth.createUserWithEmailAndPassword(registerEmail,registerPassword)
                .addOnCompleteListener { task ->
                    Log.d("Checkk","0000000")
                    if (task.isSuccessful) {
// user Registration or adding user in the realtime data base firebase
                        Log.d("Checkk","10000000")
                        val user = auth.currentUser
                        auth.signOut()
                        user?.let {
                            Log.d("checkk","user data=${user}")
                            val userReference = database.getReference("users")
                            val userId = user.uid
                            Log.d("checkk","user data=${userId}")
                            val userData = UserData(registerName, registerEmail)
Log.d("checkk","user data=${userData}")
                            // Set data to the user's node in the "users" node
                            userReference.child(userId).setValue(userData)
                                .addOnSuccessListener {
                                    // Successfully written to the database
                                    Log.d("Checkk", "Successfully sent data")
                                }
                                .addOnFailureListener { e ->
                                    // Failed to write to the database
                                    Log.d("Checkk", "Failed to send data: ${e.message}")
                                }


                        Log.d("Checkk","30000000")


                               //upload image to firebase database
                               if (imageUri != null) {
                                   val storageReference = storage.reference.child("profile_image/${userId}.jpg")
                                   storageReference.putFile(imageUri!!).addOnCompleteListener{task->

                                       storageReference.downloadUrl.addOnCompleteListener {imageUri->
                                           val imageUrl=imageUri.result.toString()


                                           userReference.child(userId).child("profileImage").setValue(imageUrl)
                                       }
                                   }


                               }
                               Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this,WelcomeActivity::class.java))
                            finish()

                        }


                    } else {
                        Toast.makeText(this, "Registered Failed", Toast.LENGTH_SHORT).show()

                    }

                }

        }
    }
}

binding.cardViewForImage.setOnClickListener{

    val intent=Intent()
    intent.action=Intent.ACTION_GET_CONTENT
    intent.type="image/*"
    startActivityForResult(Intent.createChooser(intent,"Select Image"),PICK_IMAGE_REQUEST)

}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==PICK_IMAGE_REQUEST && resultCode== RESULT_OK && data!=null && data.data!==null){

            imageUri=data.data

            Glide.with(this)
                .load(imageUri)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.registerUserImageView)



        }
    }
}