package com.example.blogproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogproject.Model.BlogItemModel
import com.example.blogproject.databinding.ActivityMainBinding
import com.example.blogproject.databinding.ActivityReadMoreBinding

class ReadMoreActivity : AppCompatActivity() {

private val binding: ActivityReadMoreBinding  by lazy {

    ActivityReadMoreBinding.inflate(layoutInflater)

}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.arrowButton.setOnClickListener {

            finish()
        }

        val blogs=intent.getParcelableExtra<BlogItemModel>("blogItem")

        if(blogs!=null){

            Glide.with(this)
                .load(blogs?.profileImage)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.imageViewItem)

            binding.readMoreTitle.text=blogs.heading
            binding.readMoreDescription.text=blogs.post
            binding.readMoreDate.text=blogs.date
            binding.readMoreAurthorName.text=blogs.userName

        }



    }
}