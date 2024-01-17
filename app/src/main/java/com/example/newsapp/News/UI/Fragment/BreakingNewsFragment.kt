package com.example.newsapp.News.UI.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.newsapp.News.Utils.Resource
import com.example.newsapp.databinding.FragmentBreakingNewsBinding

class BreakingNewsFragment : Fragment() {
    private lateinit var _binding: FragmentBreakingNewsBinding
    private val binding get() = _binding
    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        newsAdapter = NewsAdapter()
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Create ViewModel
        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)
        newsViewModel = ViewModelProvider(this, viewModelProviderFactory)[NewsViewModel::class.java]

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

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

}