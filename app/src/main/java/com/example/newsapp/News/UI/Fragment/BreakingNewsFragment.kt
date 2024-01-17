package com.example.newsapp.News.UI.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.News.Adapter.NewsAdapter
import com.example.newsapp.News.Database.ArticleDatabase
import com.example.newsapp.News.Repository.NewsRepository
import com.example.newsapp.News.UI.NewsActivity
import com.example.newsapp.News.UI.NewsViewModelProviderFactory
import com.example.newsapp.News.Utils.Constant.Companion.SEARCH_TIME_DELAY
import com.example.newsapp.News.Utils.Resource
import com.example.newsapp.databinding.FragmentBreakingNewsBinding
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        newsAdapter = NewsAdapter()
        searchAdapter = NewsAdapter()
        setUpRecyclerView()


        // Create ViewModel
        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)
        newsViewModel = ViewModelProvider(this, viewModelProviderFactory)[NewsViewModel::class.java]

        var job: Job? = null
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
                                        }
                                    }
                                    is Resource.Loading -> {
                                        showProgressBar()
                                    }
                                    else -> {
                                        hideProgressBar()
                                        respone.message?.let { message ->
                                            Log.e("sdlkfjsdflk", message)
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

        newsViewModel.breakingNews.observe(viewLifecycleOwner, Observer { respone ->
            when (respone) {
                is Resource.Success -> {
                    hideProgressBar()
                    respone.data?.let {newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
                else -> {
                    hideProgressBar()
                    respone.message?.let { message ->
                        Log.e("sdlkfjsdflk", message)
                    }
                }
            }
        })
        return binding.root
    }

    private fun setUpRecyclerView() {
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        binding.rvSearchNews.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

}