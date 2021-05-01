package com.example.smartcities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.smartcities.api.*
import com.google.gson.stream.JsonReader
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class edit_anom : AppCompatActivity() {

    var id_anomalia = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_anom)

        val titulo = intent.getStringExtra("MARKER")
        val array = intent.getStringExtra("STRS") //0-descricao 1-imagem 2-utl_report 3-utl_logado 4-nome_utl_logado 5-tipo

        val strs = array?.split("+")?.toTypedArray()

        val title = this.findViewById<EditText>(R.id.titulo_info_edit)
        val description = this.findViewById<EditText>(R.id.descricao_anom_edit)
        val  utl = this.findViewById<TextView>(R.id.utilizador_detalhe_edit)
        var spinner = this.findViewById<Spinner>(R.id.spinner_edit)
        val imagem = this.findViewById<ImageView>(R.id.imagem_edit)

        var tipo = ""
        val report = getString(R.string.reported)

        id_anomalia = strs?.get(6)!!.toInt()
        title.setText(titulo)
        description.setText(strs?.get(0))
        utl.text = report + strs?.get(4)




        Picasso.get().load(strs?.get(1)).into(imagem); // definir a imagem com o url

        imagem.getLayoutParams().height = 650 // ajustar tamanho da iamgem
        imagem.getLayoutParams().width = 800
        imagem.requestLayout()


        val options = arrayOf("Obras", "Acidente", "Mau estado", "Indefinido")

        spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //tipo = spinner.get(position).toString()
                //Log.d("****", "tipo: " + tipo)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //tipo = ""
            }
        }
        if(strs[5].equals("Acidente")){
            spinner.setSelection(1)
        }else if(strs[5].equals("Mau estado")){
            spinner.setSelection(2)
        }else if(strs[5].equals("Indefinido")){
            spinner.setSelection(3)
        }
    }

    fun editarAnom(view: View) {    // butão editar anomalia

        val title = this.findViewById<EditText>(R.id.titulo_info_edit)
        val description = this.findViewById<EditText>(R.id.descricao_anom_edit)
        var spinner = this.findViewById<Spinner>(R.id.spinner_edit)

        val tipo = spinner.selectedItem.toString()


        // invocar pedido POST para editar anomalia

        var intent = Intent(this, MapsActivity::class.java)


        if (title.text.isNullOrEmpty() || description.text.isNullOrEmpty()) {

            if (title.text.isNullOrEmpty()) {
                title.error = getString(R.string.missTitle)
            }
            if (description.text.isNullOrEmpty()) {
                description.error = getString(R.string.miss_description)
            }

        } else {

            val request = ServiceBuilder.buildService(EndPoints::class.java)
            val call = request.editarAnom(id_anomalia, title.text.toString(), description.text.toString(), tipo)

            call.enqueue(object : Callback<List<EditarAnom>> {

                override fun onResponse(call: Call<List<EditarAnom>>, response: Response<List<EditarAnom>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@edit_anom, R.string.editSucess, Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<List<EditarAnom>>, t: Throwable) {
                    Log.d("TAG_", "err: " + t.message)
                }

            })
           // Toast.makeText(this@edit_anom, R.string.editSucess, Toast.LENGTH_SHORT).show()
            //startActivity(intent)
        }
    }


    fun deleteAnom(view: View) {    // botão eliminar anomalia

        var intent = Intent(this, MapsActivity::class.java)

            val request = ServiceBuilder.buildService(EndPoints::class.java)
            val call = request.eliminarAnom(id_anomalia)

            call.enqueue(object : Callback<List<DeleteAnom>> {

                override fun onResponse(call: Call<List<DeleteAnom>>, response: Response<List<DeleteAnom>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@edit_anom, R.string.deleteSuccess, Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<List<DeleteAnom>>, t: Throwable) {
                    Log.d("TAG_", "err: " + t.message)
                }

            })

    }


    fun voltarMapa_edit(view: View) { // botão voltar para o mapa
        super.onBackPressed()   // volta para a atividade anterior e apaga a que estamos
    }

    fun addAnom_edit(view: View) {  // botão adicionar anomalia

    }

}