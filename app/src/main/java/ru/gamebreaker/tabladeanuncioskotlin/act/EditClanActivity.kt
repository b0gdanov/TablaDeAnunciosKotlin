package ru.gamebreaker.tabladeanuncioskotlin.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import ru.gamebreaker.tabladeanuncioskotlin.MainActivity
import ru.gamebreaker.tabladeanuncioskotlin.databinding.ActivityEditClanBinding
import ru.gamebreaker.tabladeanuncioskotlin.model.Ad
import ru.gamebreaker.tabladeanuncioskotlin.model.Clan
import ru.gamebreaker.tabladeanuncioskotlin.model.DbManager

class EditClanActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditClanBinding
    private val dbManager = DbManager()
    private var clan: Clan? = null
    private var isEditState: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditClanBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        checkEditState()
    }

    private fun isEditState(): Boolean{
        return intent.getBooleanExtra(MainActivity.EDIT_STATE, false)
    }

    private fun checkEditState(){
        isEditState = isEditState()
        if (isEditState){
            clan = intent.getSerializableExtra(MainActivity.CLAN_DATA) as Clan
            if(clan != null) fillViews(clan!!)
        }
    }

    private fun fillViews(clan: Clan) = with(binding) {
        etName.setText(clan.name)
        etLevel.setText(clan.name)
        etTournament.setText(clan.name)
        etDescription.setText(clan.name)
        etTel.setText(clan.tel)
        etEmail.setText(clan.email)
    }

    private fun fillClan(): Clan {
        val clan: Clan
        binding.apply{
            clan = Clan(
                etName.text.toString(),
                etLevel.text.toString(),
                etTournament.text.toString(),
                etDescription.text.toString(),
                dbManager.db.push().key,
                dbManager.auth.uid,
                etTel.text.toString(),
                etEmail.text.toString(),
                System.currentTimeMillis().toString(),
                false,
                "0",
                "0",
                "0"
            )
        }
        return clan
    }

    fun onClickPublishClan(view: View){
        clan = fillClan()
        dbManager.publishClan(clan!!, onPublishFinish())
    }

    private fun  onPublishFinish(): DbManager.FinishWorkListener{
        return object : DbManager.FinishWorkListener{
            override fun onFinish() {
                finish()
            }
        }
    }
}