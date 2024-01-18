package com.example.newsapp.News.Repository

import com.example.newsapp.News.API.RetrofitInstance
import com.example.newsapp.News.Database.ArticleDatabase
import com.example.newsapp.News.Models.Article

class NewsRepository (
    val database: ArticleDatabase
) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) = RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) = RetrofitInstance.api.searchForNews(searchQuery, pageNumber = pageNumber)

    suspend fun insertArticle(article: Article) = database.articleDao().insertArticle(article)

    suspend fun deleteArticle(article: Article) = database.articleDao().deleteArticle(article)

    fun getArticle() = database.articleDao().getAllArticles()

}