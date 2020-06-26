package com.skichrome.portfolio.view.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.portfolio.databinding.RvItemFragmentThemesBinding
import com.skichrome.portfolio.model.remote.util.Theme
import com.skichrome.portfolio.viewmodel.ThemesViewModel

class ThemesAdapter(private val viewModel: ThemesViewModel) : ListAdapter<Theme, ThemesAdapter.ThemesViewHolder>(ThemesDiffCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemesViewHolder
    {
        val binding = RvItemFragmentThemesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ThemesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ThemesViewHolder, position: Int) = holder.bind(viewModel, getItem(position))

    class ThemesViewHolder(private val binding: RvItemFragmentThemesBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(viewModel: ThemesViewModel, theme: Theme)
        {
            binding.root.setOnLongClickListener {
                viewModel.onLongClick(themeId = theme.id)
                return@setOnLongClickListener true
            }
            binding.viewModel = viewModel
            binding.theme = theme

            //Todo màj photo du thème avec glide
        }
    }

    class ThemesDiffCallback : DiffUtil.ItemCallback<Theme>()
    {
        override fun areItemsTheSame(oldItem: Theme, newItem: Theme): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Theme, newItem: Theme): Boolean = oldItem == newItem
    }
}