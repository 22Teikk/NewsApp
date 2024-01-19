package com.example.newsapp.News.UI.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.newsapp.News.Database.ArticleDatabase
import com.example.newsapp.News.Repository.NewsRepository
import com.example.newsapp.News.UI.NewsActivity
import com.example.newsapp.News.UI.NewsViewModelProviderFactory
import com.example.newsapp.databinding.FragmentArticleBinding
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : Fragment() {
    private lateinit var _binding: FragmentArticleBinding
    private val binding get() = _binding
    private val args: ArticleFragmentArgs by navArgs()
    private lateinit var viewModel: NewsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        val article = args.article
        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[NewsViewModel::class.java]
        binding.apply {
            webView.apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        saveFavorites.visibility = View.VISIBLE
                    }
                }

                article.url?.let { loadUrl(it) }
            }

            saveFavorites.setOnClickListener {
                viewModel.saveArticle(article)
                Snackbar.make( binding.root, "Save Success", Snackbar.LENGTH_LONG).show()
            }

            back.setOnClickListener {
                findNavController().navigateUp()
            }
        }
        return binding.root
    }

}