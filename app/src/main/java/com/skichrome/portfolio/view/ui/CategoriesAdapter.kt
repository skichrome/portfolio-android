package com.skichrome.portfolio.view.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.portfolio.databinding.RvItemFragmentCategoriesBinding
import com.skichrome.portfolio.model.remote.util.Category
import com.skichrome.portfolio.viewmodel.CategoriesViewModel

class CategoriesAdapter(private val viewModel: CategoriesViewModel) :
    ListAdapter<Category, CategoriesAdapter.CategoriesViewHolder>(ModelDiffCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder
    {
        val binding = RvItemFragmentCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) = holder.bind(viewModel, getItem(position))

    class CategoriesViewHolder(private val binding: RvItemFragmentCategoriesBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(viewModel: CategoriesViewModel, category: Category)
        {
            binding.root.setOnLongClickListener {
                viewModel.onLongClick(categoryId = category.id)
                return@setOnLongClickListener true
            }
            binding.viewModel = viewModel
            binding.category = category

            //Todo màj photo de la catégorie avec glide
        }
    }
}