package com.skichrome.portfolio.util

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.skichrome.portfolio.R
import java.io.IOException

fun Fragment.openImagePicker(rootView: View, requestCode: Int)
{
    Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "image/*"
        resolveActivity(requireActivity().packageManager)?.let {
            startActivityForResult(this, requestCode)
        } ?: rootView.snackBar(getString(R.string.profile_fragment_no_app_take_picture_intent))
    }
}

fun Fragment.launchCamera(requestCode: Int, parentPictureFolderName: String): Uri?
{
    var localImageRef: Uri? = null
    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
        takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
            val photoFile = try
            {
                if (canWriteExternalStorage())
                {
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        ?.createOrGetJpegFile(parentPictureFolderName, "category")
                }
                else null
            }
            catch (e: IOException)
            {
                errorLog("Error when getting photo file : ${e.message}", e)
                null
            }

            photoFile?.also { file ->
                localImageRef = file.toUri()
                val uri = FileProvider.getUriForFile(
                    requireActivity().applicationContext,
                    requireActivity().getString(R.string.file_provider_authority),
                    file
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                startActivityForResult(takePictureIntent, requestCode)
            }
        }
    }
    return localImageRef
}