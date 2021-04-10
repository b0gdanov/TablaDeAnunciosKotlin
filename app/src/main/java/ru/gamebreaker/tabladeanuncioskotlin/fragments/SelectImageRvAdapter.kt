package ru.gamebreaker.tabladeanuncioskotlin.fragments

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.gamebreaker.tabladeanuncioskotlin.R
import ru.gamebreaker.tabladeanuncioskotlin.act.EditAdsAct
import ru.gamebreaker.tabladeanuncioskotlin.utils.ImagePicker
import ru.gamebreaker.tabladeanuncioskotlin.utils.ItemTouchMoveCallBack

class SelectImageRvAdapter : RecyclerView.Adapter<SelectImageRvAdapter.ImageHolder>(), ItemTouchMoveCallBack.ItemTouchAdapter {
    val mainArray = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_image_fragment_item, parent, false)
        return ImageHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.setData(mainArray[position])
    }

    override fun getItemCount(): Int {
        return mainArray.size
    }

    override fun onMove(startPos: Int, targetPos: Int) {
        val targetItem = mainArray[targetPos]
        mainArray[targetPos] = mainArray[startPos]
        //val titleStart = mainArray[targetPos].title
        //mainArray[targetPos].title = targetItem.title
        mainArray[startPos] = targetItem
        //mainArray[startPos].title = titleStart
        notifyItemMoved(startPos, targetPos)
    }

    override fun onClear() {
        notifyDataSetChanged()
    }

    class ImageHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {

        lateinit var tvTitle: TextView
        lateinit var image: ImageView
        lateinit var imEditImage: ImageButton

        fun setData(item: String) {

            tvTitle = itemView.findViewById(R.id.tvTitle)
            image = itemView.findViewById(R.id.imageContent)
            imEditImage = itemView.findViewById(R.id.imEditImage)
            imEditImage.setOnClickListener {

                ImagePicker.getImages(

                    context as EditAdsAct,
                    1,
                    ImagePicker.REQUEST_CODE_GET_SINGLE_IMAGE

                )

                context.editImagePos = adapterPosition

            }

            tvTitle.text =
                context.resources.getStringArray(R.array.title_image_array)[adapterPosition]
            image.setImageURI(Uri.parse(item))

        }
    }

    fun updateAdapter(newList:List<String>, needClear : Boolean){
        if (needClear) mainArray.clear()
        mainArray.addAll(newList)
        notifyDataSetChanged()
    }

}