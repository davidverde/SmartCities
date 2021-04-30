package com.example.smartcities

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso

class detalhe_anom : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhe_anom)

        val titulo = intent.getStringExtra("MARKER")
        val array = intent.getStringExtra("STRS") //0-descricao 1-imagem 2-utl_report 3-utl_logado 4-nome_utl_logado 5-tipo

        val strs = array?.split("+")?.toTypedArray()

        val title = this.findViewById<TextView>(R.id.titulo_info_detalhe)
        val type = this.findViewById<TextView>(R.id.tipo_anom_detalhe)
        val description = this.findViewById<TextView>(R.id.descricao_detalhe)
        val imagem = this.findViewById<ImageView>(R.id.imagem_detalhe)


        val tip = getString(R.string.tipo)

        title.text = titulo
        type.text = tip + strs?.get(5)
        description.text = strs?.get(0)

        Picasso.get().load(strs?.get(1)).into(imagem); // definir a imagem com o url

        imagem.getLayoutParams().height = 650 // ajustar tamanho da iamgem
        imagem.getLayoutParams().width = 800
        imagem.requestLayout()

    }

    fun voltarMapa(view: View) {    // voltar para o mapa
        super.onBackPressed()   // volta para a atividade anterior e apaga a que estamos
    }

    fun addAnom(view: View) {}
}