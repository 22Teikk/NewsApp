package com.example.newsapp.News.Adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.News.Models.Article
import com.example.newsapp.News.UI.Fragment.BreakingNewsFragmentDirections
import com.example.newsapp.News.UI.Fragment.SaveNewsFragmentDirections
import com.example.newsapp.News.Utils.Converter
import com.example.newsapp.R
import com.example.newsapp.databinding.ItemArticleBinding

class NewsAdapter(private val navController: NavController, private val nameFragment: String = "Breaking") :
    RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {
    inner class ArticleViewHolder(val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            // Because Article From API don't have ID
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.binding.apply {
            if (article.urlToImage == "") {
                ivArticleImage.setImageResource(R.drawable.baseline_news_24)
            } else
                Glide.with(this.root).load(article.urlToImage).into(ivArticleImage)
            tvSource.text = article.source?.name
            tvTitle.text = article.title
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tvPublishedAt.text = article.publishedAt?.let { Converter.FormatFullDate(it) }
            } else
                tvPublishedAt.text = article.publishedAt
            articleItem.setOnClickListener {
                //onItemClickListener?.let { it(article) }
                if (article != null) {
                    if (nameFragment == "Breaking") {
                        val action =
                            BreakingNewsFragmentDirections.actionBreakingNewsFragmentToArticleFragment(
                                article
                            )
                        navController.navigate(action)
                    }
                    else {
                        val action = SaveNewsFragmentDirections.actionSaveNewsFragmentToArticleFragment(article)
                        navController.navigate(action)
                    }
                }
            }
        }
    }

    private var onItemClickListener: ((Article) -> Unit)? = null
    fun setOnItemClickListener(onItemClickListener: ((Article) -> Unit)?) {
        this.onItemClickListener = onItemClickListener
    }
}