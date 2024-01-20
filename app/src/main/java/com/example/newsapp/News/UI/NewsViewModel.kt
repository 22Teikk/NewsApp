package com.example.newsapp.News.UI.Fragment

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.News.Models.Article
import com.example.newsapp.News.Models.NewsResponse
import com.example.newsapp.News.NewsApplication
import com.example.newsapp.News.Repository.NewsRepository
import com.example.newsapp.News.Utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel (
    private val newsRepository: NewsRepository,
    application: Application
) : AndroidViewModel(application) {

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
        safeBreakingNewsCallAPI(countryCode)
    }

    fun getBreakingNewsWhenHaveInternet(countryCode: String) = viewModelScope.launch {
        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    fun getSearchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCallAPI(searchQuery)
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

    private suspend fun safeBreakingNewsCallAPI(countryCode: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            if (checkForInternet()) {
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            }else {
                breakingNews.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeSearchNewsCallAPI(querySearch: String) {
        searchingNews.postValue(Resource.Loading())
        try {
            if (checkForInternet()) {
                val response = newsRepository.searchNews(querySearch, searchNewsPage)
                searchingNews.postValue(handleBreakingNewsResponse(response))
            }else {
                searchingNews.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchingNews.postValue(Resource.Error("Network Failure"))
                else -> searchingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }


    private fun checkForInternet(): Boolean {
        val context = getApplication<NewsApplication>()
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

}