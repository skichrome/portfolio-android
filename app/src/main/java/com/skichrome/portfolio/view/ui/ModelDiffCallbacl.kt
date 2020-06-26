package com.skichrome.portfolio.view.ui

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.skichrome.portfolio.model.remote.util.Model

class ModelDiffCallback<T : Model> : DiffUtil.ItemCallback<T>()
{
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem.id == newItem.id

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
}