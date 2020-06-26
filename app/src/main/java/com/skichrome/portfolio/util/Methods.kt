package com.skichrome.portfolio.util

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.skichrome.portfolio.R
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// --- Toasts and logs --- //

fun Activity.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
fun Fragment.toast(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

fun Activity.debugLog(msg: String, e: Exception? = null) = Log.d(javaClass.simpleName, msg, e)
fun Activity.errorLog(msg: String, e: Exception? = null) = Log.e(javaClass.simpleName, msg, e)
fun Fragment.errorLog(msg: String, e: Exception? = null) = Log.e(javaClass.simpleName, msg, e)

// --- Snackbar messages --- //

fun View.snackBar(msg: String)
{
    Snackbar.make(this, msg, Snackbar.LENGTH_SHORT).run { show() }
}

// --- RecyclerView Methods --- //

fun RecyclerView.addItemDecorationAndLinearLayoutManager()
{
    val mLayoutManager = LinearLayoutManager(context)
    layoutManager = mLayoutManager
    addItemDecoration(DividerItemDecoration(context, mLayoutManager.orientation))
}

fun RecyclerView.addGridLayoutManager(isTablet: Boolean = false, spanCount: Int = if (isTablet) 2 else 1)
{
    val mLayoutManager = GridLayoutManager(context, spanCount)
    layoutManager = mLayoutManager
}

// --- ImageView Methods --- //

fun ImageView.loadPhotoWithGlide(photoReference: String)
{
    val cornerRadius = resources.getDimensionPixelSize(R.dimen.glide_img_corner_radius)
    val requestOptions = RequestOptions.bitmapTransform(RoundedCorners(cornerRadius))
    Glide.with(context)
        .load(photoReference)
        .centerCrop()
        .apply(requestOptions)
        .error(ContextCompat.getDrawable(context, R.mipmap.ic_launcher))
        .into(this)
}

fun ImageView.loadPhotoWithGlide(photoReference: Bitmap)
{
    val cornerRadius = resources.getDimensionPixelSize(R.dimen.glide_img_corner_radius)
    val requestOptions = RequestOptions.bitmapTransform(RoundedCorners(cornerRadius))
    Glide.with(context)
        .load(photoReference)
        .centerCrop()
        .apply(requestOptions)
        .error(ContextCompat.getDrawable(context, R.mipmap.ic_launcher))
        .into(this)
}

fun ImageView.loadPhotoWithGlide(photoReference: Uri)
{
    val cornerRadius = resources.getDimensionPixelSize(R.dimen.glide_img_corner_radius)
    val requestOptions = RequestOptions.bitmapTransform(RoundedCorners(cornerRadius))
    Glide.with(context)
        .load(photoReference)
        .centerCrop()
        .apply(requestOptions)
        .into(this)
}

// --- View methods --- //

fun View.getColorCompat(@ColorRes color: Int): Int = ContextCompat.getColor(context, color)
fun View.getDrawableCompat(@DrawableRes drawable: Int): Drawable? = ContextCompat.getDrawable(context, drawable)

// --- Use Task with coroutines --- //

@Suppress("UNCHECKED_CAST")
suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        when
        {
            task.result is QuerySnapshot && (task.result as QuerySnapshot).metadata.isFromCache ->
                continuation.resumeWithException(Exception("without internet"))
            task.result is DocumentSnapshot && (task.result as DocumentSnapshot).metadata.isFromCache ->
                continuation.resumeWithException(Exception("without internet"))
            task.result is Uri && (task.result as Uri).path == null ->
                continuation.resumeWithException(Exception("path is null !!"))
            task.isCanceled ->
                continuation.cancel()
            task.isSuccessful ->
                continuation.resume(task.result as T)
        }
    }
    addOnFailureListener { continuation.resumeWithException(it) }
}