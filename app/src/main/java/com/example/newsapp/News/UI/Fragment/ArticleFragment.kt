package com.example.newsapp.News.UI.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.newsapp.News.UI.NewsActivity
import com.example.newsapp.databinding.FragmentArticleBinding

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
        binding.apply {
            webView.apply {
                webViewClient = WebViewClient()
                loadUrl(article.url)
            }
        }
        return binding.root
    }

}