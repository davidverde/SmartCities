package com.example.smartcities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.view.get
import org.w3c.dom.Text

class edit_anom : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_anom)

        val titulo = intent.getStringExtra("MARKER")
        val array = intent.getStringExtra("STRS") //0-descricao 1-imagem 2-utl_report 3-utl_logado 4-nome_utl_logado 5-tipo

        val strs = array?.split("+")?.toTypedArray()

        val title = this.findViewById<TextView>(R.id.titulo_info_edit)
        val description = this.findViewById<TextView>(R.id.descricao_anom_edit)
        val  utl = this.findViewById<TextView>(R.id.utilizador_detalhe_edit)
        var spinner = this.findViewById<Spinner>(R.id.spinner_edit)

        var tipo = ""
        val report = getString(R.string.reported)

        title.text = titulo
        description.text = strs?.get(0)
        utl.text = report + strs?.get(4)

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
    }
}