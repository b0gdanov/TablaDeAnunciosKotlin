package ru.gamebreaker.tabladeanuncioskotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_content.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init(){

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        val length = Toast.LENGTH_SHORT
        val textAddToast = getString(R.string.textAddToast)

        when(item.itemId){

            R.id.id_my_ads ->{

                val text = textAddToast + getString(R.string.ad_my_ads)
                Toast.makeText(this, text, length).show()

            }
            R.id.id_car ->{

                val text = textAddToast + getString(R.string.ad_car)
                Toast.makeText(this, text, length).show()

            }
            R.id.id_pc ->{

                val text = textAddToast + getString(R.string.ad_pc)
                Toast.makeText(this, text, length).show()

            }
            R.id.id_smart ->{

                val text = textAddToast + getString(R.string.ad_smartphone)
                Toast.makeText(this, text, length).show()

            }
            R.id.id_dm ->{

                val text = textAddToast + getString(R.string.ad_dm)
                Toast.makeText(this, text, length).show()

            }
            R.id.id_sign_up ->{

                val text = textAddToast + getString(R.string.ac_sign_up)
                Toast.makeText(this, text, length).show()

            }
            R.id.id_sign_in ->{

                val text = textAddToast + getString(R.string.ac_sign_in)
                Toast.makeText(this, text, length).show()

            }
            R.id.id_sign_out ->{

                val text = textAddToast + getString(R.string.ac_sign_out)
                Toast.makeText(this, text, length).show()

            }

        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true

    }

}