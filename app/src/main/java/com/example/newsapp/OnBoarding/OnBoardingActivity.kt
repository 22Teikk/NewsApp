package com.example.newsapp.OnBoarding

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.viewpager2.widget.ViewPager2
import com.example.newsapp.NewsActivity
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivityOnboardingBinding

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var adapter: OnBoardingAdapter
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var indicatorsLayout: LinearLayout
    private lateinit var sharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Base_Theme_NewsApp)
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check view on boarding before
        sharedPref = this.getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        if (sharedPref.getBoolean("Finished", false)) {
            startActivity(Intent(this, NewsActivity::class.java))
        }
        // Load Splash screen
        installSplashScreen()

        // Set up On boarding View
        setUpOnBoarding()
        binding.viewPager.adapter = adapter

        //Set up default indicator in first screen
        setUpIndicator()
        setUpCurrentIndicator(0)

        //Set Event Change Page ViewPager for change indicator
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    binding.txtBack.visibility = View.GONE
                }else binding.txtBack.visibility = View.VISIBLE
                if (position == 2) {
                    binding.btnOnboarding.text = "Finish"
                } else binding.btnOnboarding.text = "Next"
                setUpCurrentIndicator(position)
            }
        })

        binding.apply {
            txtBack.setOnClickListener{
                if (viewPager.currentItem > 0)
                    viewPager.currentItem -= 1
            }

            btnOnboarding.setOnClickListener {
                if (viewPager.currentItem + 1 < adapter.itemCount)
                    viewPager.currentItem += 1
                else {
                    //Start activity new
                    startActivity(Intent(this@OnBoardingActivity, NewsActivity::class.java))
                    onBoardingFinished()
                    finish()
                }
            }
        }
    }

    private fun setUpOnBoarding() {
        val listOnboarding = ArrayList<OnBoardingModels>()
        listOnboarding.add(
            OnBoardingModels(
                R.drawable.ob1,
                getString(R.string.onboarding_1_title),
                getString(R.string.onboarding_1_description)
            )
        )
        listOnboarding.add(
            OnBoardingModels(
                R.drawable.ob2,
                getString(R.string.onboarding_2_title),
                getString(R.string.onboarding_2_description)
            )
        )
        listOnboarding.add(
            OnBoardingModels(
                R.drawable.ob3,
                getString(R.string.onboarding_3_title),
                getString(R.string.onboarding_3_description)
            )
        )
        adapter = OnBoardingAdapter(listOnboarding)
    }

    private fun setUpIndicator() {
        indicatorsLayout = LinearLayout(this)
        indicatorsLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(10, 0, 10, 0)
        }

        val indicators: Array<ImageView?> = Array(adapter.itemCount) { null }
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.onboarding_indicator_inactive
                )
            )
            indicators[i]?.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(10, 0, 10, 0)
            }
            indicatorsLayout.addView(indicators[i])
        }

        // Lấy ra LayoutParams của LinearLayout từ ConstraintLayout
        val params = binding.indicatorOnboarding.layoutParams as? ConstraintLayout.LayoutParams

        // Thêm indicatorsLayout vào đúng vị trí của LinearLayout trong ConstraintLayout
        params?.let {
            binding.root.addView(indicatorsLayout, params)
        }
    }

    private fun setUpCurrentIndicator(index: Int) {
        for (i in 0 until indicatorsLayout.childCount) {
            val imageView = indicatorsLayout.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.onboarding_indicator_active
                ))
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.onboarding_indicator_inactive
                ))
            }
        }
    }

    private fun onBoardingFinished() {
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }
}