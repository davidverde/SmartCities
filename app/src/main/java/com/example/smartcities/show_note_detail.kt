package com.example.smartcities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.example.smartcities.adapter.DESCRICAO
import com.example.smartcities.adapter.TITULO

class show_note_detail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_note_detail)

        val editTitl = intent.getStringExtra(TITULO)
        val editDesc = intent.getStringExtra(DESCRICAO)


        findViewById<TextView>(R.id.detail_title).setText(editTitl)
        findViewById<TextView>(R.id.detail_description).setText(editDesc)

    }

    fun ConsultNotas_2(view: View) {
        val intent = Intent(this, recyclerLista::class.java).apply {
        }
        startActivity(intent)
    }

    fun login_2(view: View) {
        val intent = Intent(this, MainActivity::class.java).apply {
        }
        startActivity(intent)
    }
}