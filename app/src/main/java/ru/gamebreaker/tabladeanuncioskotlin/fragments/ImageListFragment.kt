package ru.gamebreaker.tabladeanuncioskotlin.fragments

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.get
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.gamebreaker.tabladeanuncioskotlin.R
import ru.gamebreaker.tabladeanuncioskotlin.act.EditAdsAct
import ru.gamebreaker.tabladeanuncioskotlin.databinding.ListImageFragBinding
import ru.gamebreaker.tabladeanuncioskotlin.dialoghelper.ProgressDialog
import ru.gamebreaker.tabladeanuncioskotlin.utils.AdapterCallBack
import ru.gamebreaker.tabladeanuncioskotlin.utils.ImageManager
import ru.gamebreaker.tabladeanuncioskotlin.utils.ImagePicker
import ru.gamebreaker.tabladeanuncioskotlin.utils.ItemTouchMoveCallBack

class ImageListFragment(private val fragmentCloseInterface: FragmentCloseInterface, private val newList : ArrayList<String>?) : BaseAdsFrag(), AdapterCallBack {

    val adapter = SelectImageRvAdapter(this)
    val dragCallback = ItemTouchMoveCallBack(adapter)
    val touchHelper = ItemTouchHelper(dragCallback)
    private var job : Job? = null
    private var addImageItem : MenuItem? = null
    lateinit var binding : ListImageFragBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = ListImageFragBinding.inflate(layoutInflater)
        adView = binding.adView
        return binding.root

    }

    override fun onItemDelete() {
        addImageItem?.isVisible = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        binding.apply {
            touchHelper.attachToRecyclerView(rcViewSelectImage)
            rcViewSelectImage.layoutManager = LinearLayoutManager(activity)
            rcViewSelectImage.adapter = adapter
            if (newList != null) resizeSelectedImages(newList, true)
        }

    }
    fun updateAdapterFromEdit(bitmapList : List<Bitmap>){
        adapter.updateAdapter(bitmapList, true)
    }

    override fun onDetach() {
        super.onDetach()
        fragmentCloseInterface.onFragmentClose(adapter.mainArray)
        job?.cancel()
    }

    override fun onClose() {
        super.onClose()
        activity?.supportFragmentManager?.beginTransaction()?.remove(this@ImageListFragment)?.commit()
    }

    private fun resizeSelectedImages(newList : ArrayList<String>, needClear : Boolean){
        job = CoroutineScope(Dispatchers.Main).launch {
            val dialog = ProgressDialog.createProgressDialog(activity as Activity)
            val bitmapList = ImageManager.imageResize(newList)
            dialog.dismiss()
            adapter.updateAdapter(bitmapList, needClear)
            if(adapter.mainArray.size > 2) addImageItem?.isVisible = false
        }
    }

    private fun setUpToolbar(){

        binding.apply {
            tb.inflateMenu(R.menu.menu_choose_image)
            val deleteItem = tb.menu.findItem(R.id.id_delete_image)
            addImageItem = tb.menu.findItem(R.id.id_add_image)

            tb.setNavigationOnClickListener {

                showInterstitialAd()

            }

            deleteItem.setOnMenuItemClickListener {
                adapter.updateAdapter(ArrayList(), true)
                addImageItem?.isVisible = true
                true
            }

            addImageItem?.setOnMenuItemClickListener {
                val imageCount = ImagePicker.MAX_IMAGE_COUNT - adapter.mainArray.size
                ImagePicker.launcher(activity as EditAdsAct, (activity as EditAdsAct).launcherMultiSelectImage, imageCount)
                true
            }
        }
    }

    fun updateAdapter(newList: ArrayList<String>){
        resizeSelectedImages(newList, false)
    }

    fun setSingleImage(uri : String, pos : Int){
        val pBar = binding.rcViewSelectImage[pos].findViewById<ProgressBar>(R.id.pBar)
        job = CoroutineScope(Dispatchers.Main).launch {
            pBar.visibility = View.VISIBLE
            val bitmapList = ImageManager.imageResize(listOf(uri))
            pBar.visibility = View.GONE
            adapter.mainArray[pos] = bitmapList[0]
            adapter.notifyItemChanged(pos)
        }
    }
}