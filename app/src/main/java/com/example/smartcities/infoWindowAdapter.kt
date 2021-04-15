package com.example.smartcities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
        val linear = view.findViewById<LinearLayout>(R.id.linear)



        tvTitle.text = marker.title         // aplicar o titulo do marcar a view titulo
        descricao.text = marker.snippet     // aplicar a descricao

        val strs = descricao.text.split(",").toTypedArray() // dividir a string que contem a descricao e o url

        descricao.text = strs[0]

        Picasso.get().load(strs[1]).into(imagem); // definir a imagem com o url

        imagem.getLayoutParams().height = 450; // ajudtar tamanho da iamgem
        imagem.getLayoutParams().width = 450;
        imagem.requestLayout();

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