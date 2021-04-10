package ru.gamebreaker.tabladeanuncioskotlin.utils

import androidx.appcompat.app.AppCompatActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix

object ImagePicker {
    const val MAX_IMAGE_COUNT = 3
    const val REQUEST_CODE_GET_IMAGES = 999
    fun getImages(context:AppCompatActivity, imageCounter : Int){
        val options: Options = Options.init()
            .setRequestCode(REQUEST_CODE_GET_IMAGES)            //Request code for activity results
            .setCount(imageCounter)                             //Number of images to restict selection count
            .setFrontfacing(false)                              //Front Facing camera on start
            .setSpanCount(4)                                    //Span count for gallery min 1 & max 5
            .setMode(Options.Mode.Picture)                      //Option to select only pictures or videos or both (default .Mode.All)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT) //Orientaion
            .setPath("/pix/images")                             //Custom Path For media Storage


        Pix.start(context, options)
    }
}