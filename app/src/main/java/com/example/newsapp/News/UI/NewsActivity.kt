package com.example.newsapp.News.UI

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newsapp.News.Database.ArticleDatabase
import com.example.newsapp.News.Repository.NewsRepository
import com.example.newsapp.News.UI.Fragment.NewsViewModel
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivityNewsBinding

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    private lateinit var navController: NavController
    lateinit var newsViewModel: NewsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.NewsApp_Theme)
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpNavigation()




    }

    private fun setUpNavigation() {
        navController = findNavController(R.id.newNavHosFragment)
        binding.bottomNavigationNews.setupWithNavController(navController)
    }
}