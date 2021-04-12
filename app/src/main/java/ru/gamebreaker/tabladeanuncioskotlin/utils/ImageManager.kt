package ru.gamebreaker.tabladeanuncioskotlin.utils

import android.graphics.BitmapFactory
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

object ImageManager {

    const val MAX_IMAGE_SIZE = 1000
    const val WIDTH = 0
    const val HEIGHT = 1

    fun getImageSize(uri : String) : List<Int>{
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(uri, options)
        return if (imageRotation(uri) == 90)
            listOf(options.outHeight, options.outWidth)
        else listOf(options.outWidth, options.outHeight)
    }

    private fun imageRotation(uri : String) : Int{
        val rotation : Int
        val imageFile = File(uri)
        val exif = ExifInterface(imageFile.absolutePath)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        rotation = if (orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270){
            90
        } else {
            0
        }
        return rotation
    }

    suspend fun imageResize(uris : List<String>) : String = withContext(Dispatchers.IO){
        val tempList = ArrayList<List<Int>>()
        for (n in uris.indices){
            val size = getImageSize(uris[n])
            Log.d("MyLog", "Width : ${size[WIDTH]} Height : ${size[HEIGHT]}")
            val imageRatio = size[WIDTH].toFloat() / size[HEIGHT].toFloat()
            if (imageRatio >1){
                if (size[WIDTH] > MAX_IMAGE_SIZE){
                    tempList.add(listOf(MAX_IMAGE_SIZE, (MAX_IMAGE_SIZE / imageRatio).toInt()))
                } else {
                    tempList.add(listOf(size[WIDTH], size[HEIGHT]))
                }
            } else {
                if (size[HEIGHT] > MAX_IMAGE_SIZE){
                    tempList.add(listOf((MAX_IMAGE_SIZE * imageRatio).toInt(), MAX_IMAGE_SIZE))
                } else {
                    tempList.add(listOf(size[WIDTH], size[HEIGHT]))
                }
            }
            Log.d("MyLog", "Width : ${tempList[n][WIDTH]} Height : ${tempList[n][HEIGHT]}")
        }
        delay(3000)
        return@withContext "Done"
    }
}