package com.example.newsapp.News.Repository

import com.example.newsapp.News.API.RetrofitInstance
import com.example.newsapp.News.Database.ArticleDatabase

class NewsRepository (
    val database: ArticleDatabase
) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) = RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)
}