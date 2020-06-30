package com.skichrome.portfolio.view.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skichrome.portfolio.R
import com.skichrome.portfolio.databinding.RvItemFragmentThemesBinding
import com.skichrome.portfolio.model.remote.util.Theme
import com.skichrome.portfolio.util.loadPhotoWithGlide
import com.skichrome.portfolio.viewmodel.ThemesViewModel

class ThemesAdapter(private val viewModel: ThemesViewModel) : ListAdapter<Theme, ThemesAdapter.ThemesViewHolder>(ModelDiffCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemesViewHolder
    {
        val binding = RvItemFragmentThemesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val listener = LongClickListener()
        return ThemesViewHolder(viewModel, binding, listener)
    }

    override fun onBindViewHolder(holder: ThemesViewHolder, position: Int) = holder.bind(getItem(position))

    class ThemesViewHolder(
        viewModel: ThemesViewModel,
        private val binding: RvItemFragmentThemesBinding,
        private val listener: LongClickListener
    ) : RecyclerView.ViewHolder(binding.root)
    {
        init
        {
            binding.viewModel = viewModel
            binding.root.setOnLongClickListener(listener)
        }

        fun bind(theme: Theme)
        {
            listener.themeId = theme.id
            binding.theme = theme

            theme.imgReference?.let {
                binding.rvItemFragmentThemesImg.loadPhotoWithGlide(it)
            }
                ?: Glide.with(binding.root).load(R.drawable.ic_baseline_image_24).into(binding.rvItemFragmentThemesImg)

            binding.executePendingBindings()
        }
    }

    inner class LongClickListener(var themeId: String = "") : View.OnLongClickListener
    {
        override fun onLongClick(p0: View?): Boolean
        {
            viewModel.onLongClick(themeId = themeId)
            return true
        }
    }
}