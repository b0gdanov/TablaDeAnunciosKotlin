package ru.gamebreaker.tabladeanuncioskotlin.utils

import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.gamebreaker.tabladeanuncioskotlin.act.EditAdsAct

object ImagePicker {

    const val MAX_IMAGE_COUNT = 3
    const val REQUEST_CODE_GET_IMAGES = 999
    const val REQUEST_CODE_GET_SINGLE_IMAGE = 998

    private fun getOptions(imageCounter : Int): Options{
        val options = Options.init()
            .setCount(imageCounter)                             //Number of images to restict selection count
            .setFrontfacing(false)                              //Front Facing camera on start
            .setSpanCount(4)                                    //Span count for gallery min 1 & max 5
            .setMode(Options.Mode.Picture)                      //Option to select only pictures or videos or both (default .Mode.All)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT) //Orientaion
            .setPath("/pix/images")                             //Custom Path For media Storage
        return options
    }

    fun launcher(edAct: EditAdsAct, launcher: ActivityResultLauncher<Intent>?, imageCounter: Int){
        PermUtil.checkForCamaraWritePermissions(edAct){
            val intent = Intent(edAct, Pix::class.java).apply {
                putExtra("options", getOptions(imageCounter))
            }
            launcher?.launch(intent)
        }
    }

    fun getLauncherForMultiSelectImages(edAct: EditAdsAct): ActivityResultLauncher<Intent>{
        return edAct.registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                if (result.data != null) {
                    val returnValues = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                    if (returnValues?.size!! > 1 && edAct.chooseImageFragment == null) {
                        edAct.openChooseItemFragment(returnValues)
                    } else if (edAct.chooseImageFragment != null) {
                        edAct.chooseImageFragment?.updateAdapter(returnValues)
                    } else if (returnValues.size == 1 && edAct.chooseImageFragment == null){
                        CoroutineScope(Dispatchers.Main).launch{
                            edAct.rootElement.pBarLoading.visibility = View.VISIBLE
                            val bitmapArray = ImageManager.imageResize(returnValues) as ArrayList<Bitmap>
                            edAct.rootElement.pBarLoading.visibility = View.GONE
                            edAct.imageAdapter.update(bitmapArray)
                        }
                    }
                }
            }
        }
    }

    fun getLauncherForSingleImages(edAct : EditAdsAct): ActivityResultLauncher<Intent> {
        return edAct.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                if (result.data != null) {
                    val uris = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                    edAct.chooseImageFragment?.setSingleImage(uris?.get(0)!!, edAct.editImagePos)
                }
            }
        }
    }
}