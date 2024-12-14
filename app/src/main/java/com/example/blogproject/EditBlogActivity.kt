package com.example.blogproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.blogproject.Model.BlogItemModel
import com.example.blogproject.databinding.ActivityEditBlogBinding
import com.google.firebase.database.FirebaseDatabase

class EditBlogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBlogBinding // Assuming you're using ViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBlogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the BlogItemModel from the intent
        val blogItem = intent.getParcelableExtra<BlogItemModel>("data")

        // Display the blog details in the UI
        blogItem?.let {
            binding.addBlogTitle.setText(it.heading)
            binding.addBlogDescription.setText(it.post)
            // Set other fields similarly
        }

        // Setup the save changes button click listener
        binding.editBlogButton.setOnClickListener {
            saveChanges(blogItem)
        }
    }

    private fun saveChanges(blogItem: BlogItemModel?) {
        // Extract the updated details from your UI components
        val updatedHeading = binding.addBlogTitle.text.toString()
        val updatedPost = binding.addBlogDescription.text.toString()
        // Extract other fields similarly

        if (blogItem != null) {
            // Create a new BlogItemModel with the updated details (or directly modify the existing one)
            val updatedBlogItem = blogItem.copy(
                heading = updatedHeading,
                post = updatedPost
                // Update other fields similarly
            )

            // Update the blog post in Firebase
            updateBlogInFirebase(updatedBlogItem)
        }
    }

    private fun updateBlogInFirebase(blogItem: BlogItemModel) {
        // Reference to the 'blogs' node in Firebase
        val blogsReference = FirebaseDatabase.getInstance("https://blog-project-d1b2f-default-rtdb.asia-southeast1.firebasedatabase.app/").
        getReference("blogs")

        // Assuming you're storing blogs with postId as their key
        blogItem.postId?.let { postId ->
            blogsReference.child(postId).setValue(blogItem).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Blog updated successfully", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity
                } else {
                    Toast.makeText(this, "Failed to update blog", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
