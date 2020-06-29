package com.skichrome.portfolio.view.ui

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.portfolio.databinding.RvItemFragmentParagraphContentBinding
import com.skichrome.portfolio.model.remote.util.ParagraphContent
import com.skichrome.portfolio.viewmodel.AddEditProjectViewModel

class ProjectParagraphContentAdapter(private val viewModel: AddEditProjectViewModel) :
    ListAdapter<ParagraphContent, ProjectParagraphContentAdapter.ProjectsViewHolder>(ParagraphContentDiffCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectsViewHolder
    {
        val binding = RvItemFragmentParagraphContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProjectsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectsViewHolder, position: Int) = holder.bind(viewModel, getItem(position), position)

    class ProjectsViewHolder(private val binding: RvItemFragmentParagraphContentBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(viewModel: AddEditProjectViewModel, content: ParagraphContent, index: Int)
        {
            binding.root.setOnLongClickListener {
                viewModel.onLongClickParagraphItem(index)
                return@setOnLongClickListener true
            }
            binding.viewModel = viewModel
            binding.index = index
            binding.paragraphContent = content

            binding.rvItemFragmentParagraphContentTitleEditText.addTextChangedListener(object : TextWatcher
            {
                override fun afterTextChanged(e: Editable?) = Unit
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = s?.let {
                    content.postTitle = s.toString()
                } ?: Unit
            })

            binding.rvItemFragmentParagraphContentContentEditText.addTextChangedListener(object : TextWatcher
            {
                override fun afterTextChanged(e: Editable?) = Unit
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = s?.let {
                    content.postContentText = s.toString()
                } ?: Unit
            })

            //Todo màj photo du Projet avec glide
        }
    }

    class ParagraphContentDiffCallback : DiffUtil.ItemCallback<ParagraphContent>()
    {
        override fun areItemsTheSame(oldItem: ParagraphContent, newItem: ParagraphContent): Boolean = oldItem.postTitle == newItem.postTitle

        override fun areContentsTheSame(oldItem: ParagraphContent, newItem: ParagraphContent): Boolean = oldItem == newItem
    }
}