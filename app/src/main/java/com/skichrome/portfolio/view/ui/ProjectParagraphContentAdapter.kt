package com.skichrome.portfolio.view.ui

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.portfolio.databinding.RvItemFragmentParagraphContentBinding
import com.skichrome.portfolio.model.remote.util.ParagraphContent
import com.skichrome.portfolio.viewmodel.AddEditProjectViewModel

class ProjectParagraphContentAdapter(private val viewModel: AddEditProjectViewModel) :
    ListAdapter<ParagraphContent, ProjectParagraphContentAdapter.ProjectsViewHolder>(ModelDiffCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectsViewHolder
    {
        val binding = RvItemFragmentParagraphContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val longClickListener = OnParagraphLongClickListener()
        val titleListener = TitleEditTextChangedListener()
        val contentListener = ContentEditTextChangedListener()
        return ProjectsViewHolder(viewModel, binding, longClickListener, titleListener, contentListener)
    }

    override fun onBindViewHolder(holder: ProjectsViewHolder, position: Int) = holder.bind(getItem(position), position)

    class ProjectsViewHolder(
        viewModel: AddEditProjectViewModel,
        private val binding: RvItemFragmentParagraphContentBinding,
        private val onLongClickListener: OnParagraphLongClickListener,
        private val titleTextListener: TitleEditTextChangedListener,
        private val contentTextListener: ContentEditTextChangedListener
    ) :
        RecyclerView.ViewHolder(binding.root)
    {
        init
        {
            binding.root.setOnLongClickListener(onLongClickListener)
            binding.rvItemFragmentParagraphContentTitleEditText.addTextChangedListener(titleTextListener)
            binding.rvItemFragmentParagraphContentContentEditText.addTextChangedListener(contentTextListener)
            binding.viewModel = viewModel
        }

        fun bind(content: ParagraphContent, index: Int)
        {
            binding.index = index
            binding.paragraphContent = content

            onLongClickListener.position = index
            titleTextListener.position = index
            contentTextListener.position = index

            binding.executePendingBindings()

            //Todo màj photo du Projet avec glide
        }
    }

    inner class TitleEditTextChangedListener(var position: Int = -1) : TextWatcher
    {
        override fun afterTextChanged(e: Editable?) = Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = s?.let {
            getItem(position).postTitle = s.toString()
        } ?: Unit
    }

    inner class ContentEditTextChangedListener(var position: Int = -1) : TextWatcher
    {
        override fun afterTextChanged(e: Editable?) = Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = s?.let {
            getItem(position).postContentText = s.toString()
        } ?: Unit
    }

    inner class OnParagraphLongClickListener(var position: Int = -1) : View.OnLongClickListener
    {
        override fun onLongClick(p0: View?): Boolean
        {
            viewModel.onLongClickParagraphItem(position)
            return true
        }
    }
}