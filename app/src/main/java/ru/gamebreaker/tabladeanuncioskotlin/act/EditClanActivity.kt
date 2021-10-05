package ru.gamebreaker.tabladeanuncioskotlin.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import ru.gamebreaker.tabladeanuncioskotlin.databinding.ActivityEditClanBinding
import ru.gamebreaker.tabladeanuncioskotlin.model.Clan
import ru.gamebreaker.tabladeanuncioskotlin.model.DbManager

class EditClanActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditClanBinding
    private val dbManager = DbManager()
    private var clan: Clan? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditClanBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
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

    private fun fillClan(): Clan {
        val clan: Clan
        binding.apply{
            clan = Clan(
                etName.text.toString(),
                etLevel.text.toString(),
                etTournament.text.toString(),
                etDescription.text.toString(),
                System.currentTimeMillis().toString(),
                dbManager.db.push().key,
                dbManager.auth.uid
            )
        }
        return clan
    }
}