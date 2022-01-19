package ru.gamebreaker.tabladeanuncioskotlin.adapters

import androidx.recyclerview.widget.DiffUtil
import ru.gamebreaker.tabladeanuncioskotlin.model.Clan

class DiffUtilHelperClan(val oldList: List<Clan>, val newList: List<Clan>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].key == newList[newItemPosition].key
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}