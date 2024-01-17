package com.example.newsapp.News.UI.Fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.News.Models.NewsResponse
import com.example.newsapp.News.Repository.NewsRepository
import com.example.newsapp.News.Utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel (
    private val newsRepository: NewsRepository
) : ViewModel() {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var breakingNewsPage = 1

    val searchingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var searchNewsPage = 1

    init {
        getBreakingNews("us")
    }

    private fun getBreakingNews(countryCode: String) = viewModelScope.launch {
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
            true -> Resource.Success(response.body()!!)
            false -> Resource.Error(response.message())
        }
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        return when (response.isSuccessful) {
            true -> Resource.Success(response.body()!!)
            false -> Resource.Error(response.message())
        }
    }
}