package com.example.newsapp.News.UI.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.News.CheckNetworkConnection
import com.example.newsapp.News.Adapter.NewsAdapter
import com.example.newsapp.News.Database.ArticleDatabase
import com.example.newsapp.News.Repository.NewsRepository
import com.example.newsapp.News.UI.NewsActivity
import com.example.newsapp.News.UI.NewsViewModelProviderFactory
import com.example.newsapp.News.Utils.Constant.Companion.QUERY_PAGE_SIZE
import com.example.newsapp.News.Utils.Constant.Companion.QUERY_SEARCH_PAGE_SIZE
import com.example.newsapp.News.Utils.Constant.Companion.SEARCH_TIME_DELAY
import com.example.newsapp.News.Utils.Resource
import com.example.newsapp.databinding.FragmentBreakingNewsBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BreakingNewsFragment : Fragment() {
    private lateinit var _binding: FragmentBreakingNewsBinding
    private val binding get() = _binding
    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var searchAdapter: NewsAdapter
    private var job: Job ?= null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)

        setUpRecyclerView()

        // Create ViewModel
        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        val viewModelProviderFactory = NewsViewModelProviderFactory(requireActivity().application, newsRepository)
        newsViewModel = ViewModelProvider(this, viewModelProviderFactory)[NewsViewModel::class.java]
        getBreakingNews()
        searchViewLogic()
        newsViewModel.networkConnection.observe(viewLifecycleOwner, Observer { isConnected ->
            if (isConnected) {
                binding.noInternet.visibility = View.GONE
                newsViewModel.getBreakingNewsWhenHaveInternet("us")
                Snackbar.make(binding.snackBarContent, "Internet connected", Snackbar.LENGTH_LONG).show()
            }else {
                Snackbar.make(binding.snackBarContent, "Lost Internet connection", Snackbar.LENGTH_LONG).show()
            }
        })
        return binding.root
    }
    private fun searchViewLogic() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                job?.cancel()
                job = MainScope().launch {
                    delay(SEARCH_TIME_DELAY)
                    if (newText != null) {
                        if (newText.isNotEmpty()) {
                            binding.rvSearchNews.visibility = View.VISIBLE
                            binding.rvBreakingNews.visibility = View.GONE
                            newsViewModel.searchingNews.observe(viewLifecycleOwner, Observer { respone ->
                                when (respone) {
                                    is Resource.Success -> {
                                        hideProgressBar()
                                        respone.data?.let {newsResponse ->
                                            searchAdapter.differ.submitList(newsResponse.articles)
                                            val totalPages = newsResponse.totalResults / QUERY_SEARCH_PAGE_SIZE + 2
                                            isLastPageSearch = newsViewModel.searchNewsPage == totalPages
                                            if (isLastPageSearch)
                                                binding.rvSearchNews.setPadding(0,0,0,0)
                                        }
                                    }
                                    is Resource.Loading -> {
                                        showProgressBar()
                                    }
                                    else -> {
                                        hideProgressBar()
                                        respone.message?.let { message ->
                                            Log.e("Search News Error", message)
                                        }
                                    }
                                }
                            })
                            newsViewModel.getSearchNews(newText)
                        }else {
                            binding.rvSearchNews.visibility = View.GONE
                            binding.rvBreakingNews.visibility = View.VISIBLE
                        }
                    }
                }
                return true
            }
        })

    }

    private fun getBreakingNews() {
        newsViewModel.breakingNews.observe(viewLifecycleOwner, Observer { respone ->
            when (respone) {
                is Resource.Success -> {
                    hideProgressBar()
                    respone.data?.let {newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPageBreaking = newsViewModel.breakingNewsPage == totalPages
                        if (isLastPageBreaking)
                            binding.rvBreakingNews.setPadding(0,0,0,0)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
                else -> {
                    hideProgressBar()
                    respone.message?.let { message ->
                        binding.noInternet.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter(findNavController())
        searchAdapter = NewsAdapter(findNavController())
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addOnScrollListener(this@BreakingNewsFragment.breakingScrollListener)
        }
        binding.rvSearchNews.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addOnScrollListener(this@BreakingNewsFragment.searchScrollListener)
        }
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.GONE
        isLoadingBreaking = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoadingBreaking = true
    }

    // Paging Breaking News
    var isLoadingBreaking = false
    var isLastPageBreaking = false
    var isScrollingBreaking = false
    val breakingScrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                isScrollingBreaking = true

        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoadingBreaking && !isLastPageBreaking
            val isAtLastItemBreaking = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItemBreaking && isNotAtBeginning && isTotalMoreThanVisible && isScrollingBreaking
            if (shouldPaginate) {
                newsViewModel.getBreakingNews("us")
                isScrollingBreaking = false
            }
        }
    }

    // Paging Search News
    // Paging Breaking News
    var isLoadingSearch = false
    var isLastPageSearch = false
    var isScrollingSearch = false
    val searchScrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                isScrollingSearch = true

        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoadingSearch && !isLastPageSearch
            val isAtLastItemSearch = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_SEARCH_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItemSearch && isNotAtBeginning && isTotalMoreThanVisible && isScrollingSearch
            if (shouldPaginate) {
                newsViewModel.getSearchNews(binding.searchView.query.toString())
                isScrollingSearch = false
            }
        }
    }

}