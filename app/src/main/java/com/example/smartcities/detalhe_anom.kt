package com.example.smartcities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.util.Base64
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso

class detalhe_anom : AppCompatActivity() {

    var utl_atual = ""
    var lat = 0.0f
    var long = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhe_anom)

        // guardar a base_3
        var base_3 : Any? = ""

        val sharedPref: SharedPreferences = getSharedPreferences(
                getString(R.string.login_pref), Context.MODE_PRIVATE
        )
        if (sharedPref != null){
            base_3 = sharedPref.all[getString(R.string.base_3)]
        }

        val titulo = intent.getStringExtra("MARKER")
        val descricao_0 = intent.getStringExtra("DESCR")
        val tipo_5 = intent.getStringExtra("TIPO")

        val base_1 = intent.getStringExtra("BASE1")
        val base_2 = intent.getStringExtra("BASE2")

        val utl_at = intent.getStringExtra("UTL_ATUAL")
        val lat_ = intent.getStringExtra("LAT")
        val long_ = intent.getStringExtra("LONG")

        utl_atual = utl_at!!
        lat = lat_!!.toFloat()
        long = long_!!.toFloat()

        val base_64 = base_1+base_2+base_3

        val title = this.findViewById<TextView>(R.id.titulo_info_detalhe)
        val type = this.findViewById<TextView>(R.id.tipo_anom_detalhe)
        val description = this.findViewById<TextView>(R.id.descricao_detalhe)
        val imagem = this.findViewById<ImageView>(R.id.imagem_detalhe)


        val tip = getString(R.string.tipo)

        title.text = titulo
        type.text = tip + tipo_5
        description.text = descricao_0

        // DESCODIFICAR BASE64
        val decodedString: ByteArray = Base64.decode(base_64, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        imagem.setImageBitmap(decodedByte)

        imagem.getLayoutParams().height = 650 // ajustar tamanho da iamgem
        imagem.getLayoutParams().width = 800
        imagem.requestLayout()

    }

    fun voltarMapa(view: View) {    // voltar para o mapa
        super.onBackPressed()   // volta para a atividade anterior e apaga a que estamos
    }

    fun addAnom(view: View) {
        val intent = Intent(this, add_anom::class.java)
        intent.putExtra("UTL_ATUAL", utl_atual)
        intent.putExtra("LAT", lat.toString())
        intent.putExtra("LONG", long.toString())
        startActivity(intent)
    }
}