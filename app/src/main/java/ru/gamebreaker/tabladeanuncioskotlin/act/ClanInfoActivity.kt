package ru.gamebreaker.tabladeanuncioskotlin.act

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import ru.gamebreaker.tabladeanuncioskotlin.R
import ru.gamebreaker.tabladeanuncioskotlin.adapters.ImageAdapter
import ru.gamebreaker.tabladeanuncioskotlin.databinding.ActivityClanInfoBinding
import ru.gamebreaker.tabladeanuncioskotlin.model.Ad
import ru.gamebreaker.tabladeanuncioskotlin.model.Clan
import ru.gamebreaker.tabladeanuncioskotlin.utils.ImageManager

class ClanInfoActivity : AppCompatActivity() {
    lateinit var binding: ActivityClanInfoBinding
    private var clan: Clan? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClanInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getIntentFromMainAct()
        binding.fbTel.setOnClickListener { call() }
        binding.fbEmail.setOnClickListener { sendEmail() }
    }

    private fun getIntentFromMainAct(){
        clan = intent.getSerializableExtra("CLAN") as Clan
        if (clan != null) fillTextViews(clan!!)
    }

    private fun fillTextViews(clan: Clan) = with(binding){
        tvTitle.text = clan.name
        tvClanNameValue.text = clan.name
        tvClanLevelValue.text = clan.level
        tvClanTournamentValue.text = clan.tournament
        tvClanDescriptionValue.text = clan.description
    }

    private fun call(){
        val callUri = "tel:${clan?.tel}"
        val iCall = Intent(Intent.ACTION_DIAL)
        iCall.data = callUri.toUri()
        startActivity(iCall)
    }

    private fun sendEmail(){
        val iSendEmail = Intent(Intent.ACTION_SEND)
        iSendEmail.type = "message/rfc822"
        iSendEmail.apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(clan?.email))
            putExtra(Intent.EXTRA_SUBJECT, "Объявление")
            putExtra(Intent.EXTRA_TEXT, "Меня интересует Ваше объявление!")
        }
        try {
            startActivity(Intent.createChooser(iSendEmail, "Открыть с помощью"))
        }catch (e: ActivityNotFoundException){

        }
    }
}