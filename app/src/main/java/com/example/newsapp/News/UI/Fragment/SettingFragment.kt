package com.example.newsapp.News.UI.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentSettingBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingFragment : Fragment() {
    private lateinit var _binding: FragmentSettingBinding
    private val binding get() = _binding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("MODE", Context.MODE_PRIVATE)
        binding.apply {
            if (sharedPreferences.getBoolean("MODE", false)) {
                binding.darkLight.isChecked = true
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            darkLight.setOnClickListener {
                val editor = sharedPreferences.edit()
                if (sharedPreferences.getBoolean("MODE", false)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    editor.putBoolean("MODE", false)
                    nameMode.text = "Light Mode"
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    editor.putBoolean("MODE", true)
                    nameMode.text = "Dark Mode"
                }
                editor.apply()
                if (findNavController().currentDestination?.id == R.id.settingFragment) {
                    // Không làm gì cả, giữ nguyên ở SettingFragment
                } else {
                    // Nếu không ở SettingFragment, thực hiện điều hướng
                    findNavController().navigate(R.id.settingFragment)
                }
            }

            layoutHelp.setOnClickListener {
                val emailIntent = Intent(Intent.ACTION_SEND)
                emailIntent.type = "text/html"
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("nguyenkiet228@gmail.com"))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Help your application")
                emailIntent.putExtra(Intent.EXTRA_TEXT, "I'm email body.")
                val packageManager = requireActivity().packageManager
                if (emailIntent.resolveActivity(packageManager) != null) {
                    startActivity(Intent.createChooser(emailIntent, "Send Email"))
                } else {
                    Toast.makeText(requireContext(), "No email app installed.", Toast.LENGTH_SHORT).show()
                }

            }
            layoutAbout.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/22Teikk")))
            }
            layoutResource.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/22Teikk/NewsApp")))
            }
        }

        return binding.root
    }

}