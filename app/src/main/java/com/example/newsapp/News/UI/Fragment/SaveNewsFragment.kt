package com.example.newsapp.News.UI.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.News.Adapter.NewsAdapter
import com.example.newsapp.News.Database.ArticleDatabase
import com.example.newsapp.News.Repository.NewsRepository
import com.example.newsapp.News.UI.NewsActivity
import com.example.newsapp.News.UI.NewsViewModelProviderFactory
import com.example.newsapp.databinding.FragmentSaveNewsBinding
import com.google.android.material.snackbar.Snackbar

class SaveNewsFragment : Fragment() {
    private lateinit var _binding: FragmentSaveNewsBinding
    private val binding get() = _binding
    private lateinit var viewModel: NewsViewModel
    private lateinit var saveAdapter: NewsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSaveNewsBinding.inflate(inflater, container, false)
        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        val viewModelProviderFactory = NewsViewModelProviderFactory(requireActivity().application ,newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[NewsViewModel::class.java]
        setUpRecyclerView()
        getSaveNews()
        deleteNews()
        return binding.root
    }

    private fun deleteNews() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = saveAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(binding.snackBarContent, "Delete Success", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSaveNews)
        }
    }

    private fun getSaveNews() {
        viewModel.getSaveArticle().observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                binding.rvSaveNews.visibility = View.GONE
                binding.noNews.visibility = View.VISIBLE
            }else {
                binding.rvSaveNews.visibility = View.VISIBLE
                binding.noNews.visibility = View.GONE
                saveAdapter.differ.submitList(it)
            }
        })
    }

    private fun setUpRecyclerView() {
        saveAdapter = NewsAdapter(findNavController(), "Save")
        binding.rvSaveNews.apply {
            adapter = saveAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

}