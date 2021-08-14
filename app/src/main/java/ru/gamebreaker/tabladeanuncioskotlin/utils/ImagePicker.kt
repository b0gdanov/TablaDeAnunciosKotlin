package ru.gamebreaker.tabladeanuncioskotlin.utils

import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.fragment.app.Fragment
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.addPixToActivity
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.gamebreaker.tabladeanuncioskotlin.R
import ru.gamebreaker.tabladeanuncioskotlin.act.EditAdsAct

object ImagePicker {

    const val MAX_IMAGE_COUNT = 3
    const val REQUEST_CODE_GET_IMAGES = 999
    const val REQUEST_CODE_GET_SINGLE_IMAGE = 998

    private fun getOptions(imageCounter: Int): Options {
        val options = Options().apply {
            count = imageCounter
            isFrontFacing = false
            mode = Mode.Picture
            path = "/pix/images"
        }
        return options
    }

    fun getMultiImages(edAct: EditAdsAct, imageCounter: Int) {
        edAct.addPixToActivity(R.id.place_holder, getOptions(imageCounter)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    getMultiSelectImages(edAct, result.data)
                    closePixFrag(edAct)
                }
            }
        }
    }

    fun getSingleImage(edAct: EditAdsAct) {
        val f = edAct.chooseImageFragment
        edAct.addPixToActivity(R.id.place_holder, getOptions(1)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    edAct.chooseImageFragment = f
                    openChooseImageFrag(edAct, f!!)
                    singleImage(edAct, result.data[0])
                }
            }
        }
    }

    private fun openChooseImageFrag(edAct: EditAdsAct, f: Fragment){
        edAct.supportFragmentManager.beginTransaction().replace(R.id.place_holder, f).commit()

    }

    private fun closePixFrag(edAct: EditAdsAct) {
        val fList = edAct.supportFragmentManager.fragments
        fList.forEach {
            if (it.isVisible) edAct.supportFragmentManager.beginTransaction().remove(it).commit()
        }
    }

    fun getMultiSelectImages(edAct: EditAdsAct, uris: List<Uri>) {
        if (uris.size > 1 && edAct.chooseImageFragment == null) {
            edAct.openChooseItemFragment(uris as ArrayList<Uri>)
        } else if (edAct.chooseImageFragment != null) {
            edAct.chooseImageFragment?.updateAdapter(uris as ArrayList<Uri>)
        } else if (uris.size == 1 && edAct.chooseImageFragment == null) {
            CoroutineScope(Dispatchers.Main).launch {
                edAct.rootElement.pBarLoading.visibility = View.VISIBLE
                val bitmapArray =
                    ImageManager.imageResize(uris as ArrayList<Uri>, edAct) as ArrayList<Bitmap>
                edAct.rootElement.pBarLoading.visibility = View.GONE
                edAct.imageAdapter.update(bitmapArray)
            }
        }
    }


    private fun singleImage(edAct: EditAdsAct, uri: Uri) {
        edAct.chooseImageFragment?.setSingleImage(uri, edAct.editImagePos)
    }
}