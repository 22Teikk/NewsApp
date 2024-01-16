package com.example.newsapp.News.UI.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.newsapp.databinding.FragmentSaveNewsBinding

class SaveNewsFragment : Fragment() {
    private lateinit var _binding: FragmentSaveNewsBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSaveNewsBinding.inflate(inflater, container, false)

        return binding.root
    }

}