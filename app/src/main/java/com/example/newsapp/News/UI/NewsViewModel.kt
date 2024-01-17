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

    var breakingNewsPage = 1

    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        return when (response.isSuccessful) {
            true -> Resource.Success(response.body()!!)
            false -> Resource.Error(response.message())
        }
    }
}