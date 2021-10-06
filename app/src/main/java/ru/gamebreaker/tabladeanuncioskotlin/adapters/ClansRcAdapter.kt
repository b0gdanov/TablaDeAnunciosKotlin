package ru.gamebreaker.tabladeanuncioskotlin.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.gamebreaker.tabladeanuncioskotlin.MainActivity
import ru.gamebreaker.tabladeanuncioskotlin.R
import ru.gamebreaker.tabladeanuncioskotlin.act.EditClanActivity
import ru.gamebreaker.tabladeanuncioskotlin.databinding.ClanListItemBinding
import ru.gamebreaker.tabladeanuncioskotlin.model.Clan

class ClansRcAdapter(val act: MainActivity) : RecyclerView.Adapter<ClansRcAdapter.ClansHolder>() {

    val clanArray = ArrayList<Clan>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClansHolder {
        val binding =
            ClanListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClansHolder(binding, act)
    }

    override fun onBindViewHolder(holder: ClansHolder, position: Int) {
        holder.setData(clanArray[position])
    }

    override fun getItemCount(): Int {
        return clanArray.size
    }

    fun updateAdapter(newList: List<Clan>) {
        val tempArray = ArrayList<Clan>()
        tempArray.addAll(clanArray)
        tempArray.addAll(newList)

        val diffResult = DiffUtil.calculateDiff(DiffUtilHelperClan(clanArray, tempArray))
        diffResult.dispatchUpdatesTo(this)
        clanArray.clear()
        clanArray.addAll(tempArray)
    }

    fun updateAdapterWithClear(newList: List<Clan>) {

        val diffResult = DiffUtil.calculateDiff(DiffUtilHelperClan(clanArray, newList))
        diffResult.dispatchUpdatesTo(this)
        clanArray.clear()
        clanArray.addAll(newList)
    }

    class ClansHolder(val binding: ClanListItemBinding, val act: MainActivity) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(clan: Clan) = with(binding) {
            tvLevel.text = clan.level
            tvTitle.text = clan.name
            tvTournament.text = clan.tournament
            tvDescription.text = clan.description

            isFav(clan)
            showEditPanel(isOwner(clan))
            mainOnClick(clan)
        }

        private fun mainOnClick(clan: Clan) = with(binding) {
            ibFav.setOnClickListener {
                if (act.mAuth.currentUser?.isAnonymous == false) act.onFavClickedClan(clan)
            }
            itemView.setOnClickListener {
                act.onClanViewed(clan)
            }
            ibEditAd.setOnClickListener(onClickEdit(clan))
            ibDelAd.setOnClickListener {
                act.onDeleteItemClan(clan)
            }
        }

        private fun isFav(clan: Clan) {
            if (clan.isFav) {
                binding.ibFav.setImageResource(R.drawable.ic_favorite_on)
            } else {
                binding.ibFav.setImageResource(R.drawable.ic_favorite_off)
            }
        }

        private fun onClickEdit(clan: Clan): View.OnClickListener {
            return View.OnClickListener {
                val editIntent = Intent(act, EditClanActivity::class.java).apply {
                    putExtra(MainActivity.EDIT_STATE, true)
                    putExtra(MainActivity.CLAN_DATA, clan)
                }
                act.startActivity(editIntent)
            }
        }

        private fun isOwner(clan: Clan): Boolean {
            return clan.uid == act.mAuth.uid
        }

        private fun showEditPanel(isOwner: Boolean) {
            if (isOwner) {
                binding.editPanel.visibility = View.VISIBLE
            } else {
                binding.editPanel.visibility = View.GONE
            }
        }
    }

    interface Listener {
        fun onDeleteItemClan(clan: Clan)
        fun onClanViewed(clan: Clan)
        fun onFavClickedClan(clan: Clan)
    }
}