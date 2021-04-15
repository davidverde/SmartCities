package com.example.smartcities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast

class mapa_menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_menu)
    }

    // Aplicar o top menu a este ecrã
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_menu, menu)
        return true;
    }

    // Opções do top menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.mapa_menu_add_nota -> {

                // mudar para o ecrã adicionar nota
                val intent = Intent(this, addNote::class.java).apply {
                }
                startActivity(intent)
                true
            }

            R.id.mapa_menu_consultar_notas -> {

                // mudar para o ecrã consultar notas
                val intent = Intent(this, recyclerLista::class.java).apply {
                }
                startActivity(intent)

                true
            }

            R.id.mapa_menu_logout -> {

                //Eliminar utilizador no Shared Preferences
                val shared: SharedPreferences = getSharedPreferences(
                        getString(R.string.login_pref), Context.MODE_PRIVATE
                )
                with(shared.edit()){
                    putBoolean(getString(R.string.user_logged), false)
                    putString(getString(R.string.email_login_user), "")
                    putString(getString(R.string.id_login_user), "")
                    putString(getString(R.string.nome_login_user), "")
                    commit()
                }

                // mudar para o ecrã login
                val intent = Intent(this, MainActivity::class.java).apply {
                }
                startActivity(intent)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}