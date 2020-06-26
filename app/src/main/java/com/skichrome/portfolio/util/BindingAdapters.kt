package com.skichrome.portfolio.util

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.portfolio.model.remote.util.Category
import com.skichrome.portfolio.model.remote.util.Theme
import com.skichrome.portfolio.view.ui.CategoriesAdapter
import com.skichrome.portfolio.view.ui.ThemesAdapter

@BindingAdapter(value = ["items_themes"])
fun setThemes(listView: RecyclerView, themes: List<Theme>?) = themes?.let {
    (listView.adapter as ThemesAdapter).submitList(themes)
}

@BindingAdapter(value = ["items_categories"])
fun setCategories(listView: RecyclerView, categories: List<Category>?) = categories?.let {
    (listView.adapter as CategoriesAdapter).submitList(categories)
}