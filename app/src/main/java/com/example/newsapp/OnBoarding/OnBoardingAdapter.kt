package com.example.newsapp.OnBoarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.databinding.ContainerOnboardingBinding

class OnBoardingAdapter(val list: ArrayList<OnBoardingModels>) :
    RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder>() {
    inner class OnBoardingViewHolder(val binding: ContainerOnboardingBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        return OnBoardingViewHolder(
            ContainerOnboardingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        holder.binding.apply {
            imageOnboarding.setImageResource(list[position].image)
            txtTitleOnboarding.text = list[position].title
            txtDescription.text = list[position].description
        }
    }
}