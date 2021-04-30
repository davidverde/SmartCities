package com.example.smartcities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Picasso

class infoWindowAdapter(context: Context) : GoogleMap.InfoWindowAdapter {

    var mContext = context
    var mWindow = (context as Activity).layoutInflater.inflate(R.layout.info_window, null)


    private fun rendowWindowText(marker: Marker, view: View){

        val tvTitle = view.findViewById<TextView>(R.id.titulo_info)
        val descricao = view.findViewById<TextView>(R.id.descricao)
        val imagem = view.findViewById<ImageView>(R.id.imagem)
        val utl = view.findViewById<TextView>(R.id.utilizador)
        val tipo = view.findViewById<TextView>(R.id.tipo_anom)
        //val botBar = view.findViewById<LinearLayout>(R.id.bottomBar)

        val strs = marker.snippet.split("+").toTypedArray() // dividir a string que contem a descricao, o url e o id do utl
                                                                        // 0-descricao 1-imagem 2-utl_report 3-utl_logado 4-nome_utl_logado 5-tipo
        tvTitle.text = marker.title         // aplicar o TITULO

        descricao.text = strs[0]            // aplicar a descricao

        Picasso.get().load(strs[1]).into(imagem); // definir a imagem com o url

        imagem.getLayoutParams().height = 450; // ajudtar tamanho da iamgem
        imagem.getLayoutParams().width = 600;
        imagem.requestLayout();

        tipo.text = "Type: " + strs[5]

        if(strs[2].toInt() == strs[3].toInt()){     // se o utilizador que reportou a anomalia for o mesmo que tem login iniciado
            utl.text = "Reported by: " + strs[4]
            utl.visibility = (View.VISIBLE)
        }else{                                      // caso nao seja
            utl.visibility = (View.GONE)
        }

    }

    override fun getInfoWindow(marker: Marker): View {
        rendowWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoContents(marker: Marker): View? {
        rendowWindowText(marker, mWindow)
        return mWindow
    }

}