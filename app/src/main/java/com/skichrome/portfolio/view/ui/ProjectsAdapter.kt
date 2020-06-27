package com.skichrome.portfolio.view.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.portfolio.databinding.RvItemFragmentProjectsBinding
import com.skichrome.portfolio.model.remote.util.Project
import com.skichrome.portfolio.viewmodel.ProjectsViewModel

class ProjectsAdapter(private val viewModel: ProjectsViewModel) :
    ListAdapter<Project, ProjectsAdapter.ProjectsViewHolder>(ModelDiffCallback())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectsViewHolder
    {
        val binding = RvItemFragmentProjectsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProjectsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectsViewHolder, position: Int) = holder.bind(viewModel, getItem(position))

    class ProjectsViewHolder(private val binding: RvItemFragmentProjectsBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(viewModel: ProjectsViewModel, project: Project)
        {
            binding.root.setOnLongClickListener {
                viewModel.onLongClick(categoryId = project.id)
                return@setOnLongClickListener true
            }
            binding.viewModel = viewModel
            binding.project = project

            //Todo màj photo du Projet avec glide
        }
    }
}