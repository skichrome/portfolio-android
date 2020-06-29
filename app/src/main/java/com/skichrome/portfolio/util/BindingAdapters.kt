package com.skichrome.portfolio.util

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.skichrome.portfolio.model.remote.util.Category
import com.skichrome.portfolio.model.remote.util.ParagraphContent
import com.skichrome.portfolio.model.remote.util.Project
import com.skichrome.portfolio.model.remote.util.Theme
import com.skichrome.portfolio.view.ui.CategoriesAdapter
import com.skichrome.portfolio.view.ui.ProjectParagraphContentAdapter
import com.skichrome.portfolio.view.ui.ProjectsAdapter
import com.skichrome.portfolio.view.ui.ThemesAdapter
import java.text.SimpleDateFormat

@BindingAdapter(value = ["items_themes"])
fun setThemes(listView: RecyclerView, themes: List<Theme>?) = themes?.let {
    (listView.adapter as ThemesAdapter).submitList(themes)
}

@BindingAdapter(value = ["items_categories"])
fun setCategories(listView: RecyclerView, categories: List<Category>?) = categories?.let {
    (listView.adapter as CategoriesAdapter).submitList(categories)
}

@BindingAdapter(value = ["items_projects"])
fun setProjects(listView: RecyclerView, categories: List<Project>?) = categories?.let {
    (listView.adapter as ProjectsAdapter).submitList(categories)
}

@BindingAdapter(value = ["items_projects_paragraphs"])
fun setProjectsParagraphsContent(listView: RecyclerView, paragraphs: List<ParagraphContent>?) = paragraphs?.let {
    (listView.adapter as ProjectParagraphContentAdapter).submitList(paragraphs)
}

@BindingAdapter(value = ["bind_date"])
fun setDateFormatted(textView: TextInputEditText, dateMillis: Long) = textView.apply {
    setText(getDateFormatted(dateMillis))
}

fun getDateFormatted(dateMillis: Long): String = SimpleDateFormat.getDateTimeInstance().format(dateMillis)
