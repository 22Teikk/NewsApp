package com.example.newsapp.News.UI.Fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.News.Models.Article
import com.example.newsapp.News.Models.NewsResponse
import com.example.newsapp.News.Repository.NewsRepository
import com.example.newsapp.News.Utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel (
    private val newsRepository: NewsRepository
) : ViewModel() {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    val searchingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    fun getSearchNews(searchQuery: String) = viewModelScope.launch {
        searchingNews.postValue(Resource.Loading())
        val response = newsRepository.searchNews(searchQuery, searchNewsPage)
        searchingNews.postValue(handleSearchNewsResponse(response))
    }


    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        return when (response.isSuccessful) {
            true -> {
                breakingNewsPage++
                if (breakingNewsResponse == null)
                    breakingNewsResponse = response.body()
                else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = response.body()?.articles
                    if (oldArticles != null) {
                        oldArticles.addAll(newArticles!!)
                    }
                }
                Resource.Success(breakingNewsResponse ?: response.body()!!)
            }
            false -> Resource.Error(response.message())
        }
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        return when (response.isSuccessful) {
            true -> {
                searchNewsPage++
                if (searchNewsResponse == null)
                    searchNewsResponse = response.body()
                else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = response.body()?.articles
                    if (oldArticles!= null) {
                        oldArticles.addAll(newArticles!!)
                    }
                }
                Resource.Success(searchNewsResponse ?: response.body()!!)
            }
            false -> Resource.Error(response.message())
        }
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.insertArticle(article)
    }

    fun getSaveArticle() = newsRepository.getArticle()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }
}