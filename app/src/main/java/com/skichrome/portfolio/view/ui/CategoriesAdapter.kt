package com.skichrome.portfolio.view.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skichrome.portfolio.R
import com.skichrome.portfolio.databinding.RvItemFragmentCategoriesBinding
import com.skichrome.portfolio.model.remote.util.Category
import com.skichrome.portfolio.util.loadPhotoWithGlide
import com.skichrome.portfolio.viewmodel.CategoriesViewModel

class CategoriesAdapter(private val viewModel: CategoriesViewModel) :
    ListAdapter<Category, CategoriesAdapter.CategoriesViewHolder>(ModelDiffCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder
    {
        val binding = RvItemFragmentCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val listener = LongClickListener()
        return CategoriesViewHolder(viewModel, binding, listener)
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) = holder.bind(getItem(position))

    class CategoriesViewHolder(
        viewModel: CategoriesViewModel,
        private val binding: RvItemFragmentCategoriesBinding,
        private val longClickListener: LongClickListener
    ) :
        RecyclerView.ViewHolder(binding.root)
    {
        init
        {
            binding.root.setOnLongClickListener(longClickListener)
            binding.viewModel = viewModel
        }

        fun bind(category: Category)
        {
            longClickListener.categoryId = category.id
            binding.category = category

            category.imgReference?.let {
                binding.rvItemFragmentCategoriesImg.loadPhotoWithGlide(it)
            } ?: Glide.with(binding.root).load(R.drawable.ic_baseline_image_24).into(binding.rvItemFragmentCategoriesImg)

            binding.executePendingBindings()
        }
    }

    inner class LongClickListener(var categoryId: String = "") : View.OnLongClickListener
    {
        override fun onLongClick(p0: View?): Boolean
        {
            viewModel.onLongClick(categoryId = categoryId)
            return true
        }
    }
}