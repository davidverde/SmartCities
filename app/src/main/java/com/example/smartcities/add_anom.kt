package com.example.smartcities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.smartcities.api.Anomalia
import com.example.smartcities.api.EditarAnom
import com.example.smartcities.api.EndPoints
import com.example.smartcities.api.ServiceBuilder
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class add_anom : AppCompatActivity() {

    var utl_atual = ""
    var lat = ""
    var long = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_anom)

        utl_atual = intent.getStringExtra("UTL_ATUAL").toString()
        lat = intent.getStringExtra("LAT").toString()
        long = intent.getStringExtra("LONG").toString()
        //Toast.makeText(this, "utl: " + utl_atual + " LAT: " + lat + " LONG: " + long, Toast.LENGTH_SHORT).show()

        val title = this.findViewById<EditText>(R.id.titulo_info_add)
        val description = this.findViewById<EditText>(R.id.descricao_add)
        var spinner = this.findViewById<Spinner>(R.id.spinner_add)
        val imagem = this.findViewById<ImageView>(R.id.img_add)

        val options = arrayOf("Obras", "Acidente", "Mau estado", "Indefinido")

        spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options)

    }


    fun addAnom_add(view: View) {

        val title = this.findViewById<EditText>(R.id.titulo_info_add)
        val description = this.findViewById<EditText>(R.id.descricao_add)
        var spinner = this.findViewById<Spinner>(R.id.spinner_add)
        val imagem = this.findViewById<ImageView>(R.id.img_add)

        val tipo = spinner.selectedItem.toString()

        var intent = Intent(this, MapsActivity::class.java)

        // POST report anomalia
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.addAnom(utl_atual.toInt(), title.text.toString(), description.text.toString(), tipo, "Image", lat.toFloat(), long.toFloat())

        call.enqueue(object : Callback<Anomalia> {

            override fun onResponse(call: Call<Anomalia>, response: Response<Anomalia>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@add_anom, R.string.reportSuccess, Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                }
            }

            override fun onFailure(call: Call<Anomalia>, t: Throwable) {
                Log.d("TAG_", "err: " + t.message)
            }

        })
    }


    fun voltarMapa_add(view: View) {
        var intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

}