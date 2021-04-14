package com.example.smartcities

import android.util.Log
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.smartcities.api.EndPoints
import com.example.smartcities.api.OutputPost
import com.example.smartcities.api.ServiceBuilder
import com.example.smartcities.api.User
import com.example.smartcities.entities.Note
import com.example.smartcities.viewModel.NoteViewModel
import kotlinx.android.synthetic.main.activity_recycler_lista.*

import kotlinx.android.synthetic.main.activity_main.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        // Retrofit - invocação do metodo GET que devolve informação de todos os utilizadores
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getUsers()

        call.enqueue(object : Callback<List<User>>{
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>){

                if(response.isSuccessful){
                    for(User in response.body()!!){
                        Log.d("TAG_", User.id_utl.toString() + User.nome.toString())
                    }
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.d("TAG_", "Dei erro")
                //Toast.makeText(this@MainActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private lateinit var noteViewModel: NoteViewModel
    private val newWordActivityRequestCode = 1

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            val pNote = data?.getStringExtra(addNote.EXTRA_REPLY_NOTE)
            val pDescricao = data?.getStringExtra(addNote.EXTRA_REPLY_DESCRIPTION)

            if (pNote != null && pDescricao != null) {
                val note = Note(note = pNote, descricao = pDescricao)
                noteViewModel.insert(note)
            }

        } else {
            Toast.makeText(
                applicationContext,
                R.string.error,
                Toast.LENGTH_LONG).show()
        }
    }

    fun listaNotas(view: View) {    // botao listar notas
        val intent = Intent(this, recyclerLista::class.java).apply {
        }
        startActivity(intent)
    }

    fun addNotas(view: View) {      // botao adicionar notas
        val intent = Intent(this, addNote::class.java).apply {
        }
        startActivityForResult(intent, newWordActivityRequestCode)
    }

    fun verificarLogin(view: View) {        // botao login - verificação

        val emailVal = findViewById<EditText>(R.id.email)
        val passVal = findViewById<EditText>(R.id.pass)
        val erro = findViewById<TextView>(R.id.erro_log)

        val intent = Intent(this, mapa_menu::class.java).apply {
        }

        erro.visibility = (View.INVISIBLE)

        // Verificações de campos vazios

            if(emailVal.text.isNullOrEmpty()){
                emailVal.error = getString(R.string.erro_email)
            }
            if(passVal.text.isNullOrEmpty()){
                passVal.error = getString(R.string.erro_pass)
            }


        // invocar pedido post com os paramotros do login
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.postUtl(emailVal.text.toString(), passVal.text.toString())

        call.enqueue(object : Callback<List<OutputPost>>{

            override fun onResponse(call: Call<List<OutputPost>>, response: Response<List<OutputPost>>) {

                if (response.isSuccessful){
                    for(OutputPost in response.body()!!){   // verificação se os dados do login correspondem a um utilizador

                        if(emailVal.text.toString().equals(OutputPost.email) && passVal.text.toString().equals(OutputPost.pass)){
                            startActivity(intent)
                        } else if(!(emailVal.text.isNullOrEmpty()) || !(passVal.text.isNullOrEmpty())){
                            erro.visibility = (View.VISIBLE)
                        }

                    }
                }
            }

            override fun onFailure(call: Call<List<OutputPost>>, t: Throwable) {
                /* Toast.makeText(this@MainActivity, "${t.message}", Toast.LENGTH_SHORT).show()*/
                Log.d("TAG_", "err: " + t.message)
                erro.visibility = (View.VISIBLE)
            }

        })
    }
}