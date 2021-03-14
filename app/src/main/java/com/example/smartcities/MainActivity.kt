package com.example.smartcities

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
import com.example.smartcities.entities.Note
import com.example.smartcities.viewModel.NoteViewModel
import kotlinx.android.synthetic.main.activity_recycler_lista.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

    }
    private lateinit var noteViewModel: NoteViewModel
    private val newWordActivityRequestCode = 1

    fun listaNotas(view: View) {
        val intent = Intent(this, recyclerLista::class.java).apply {
        }
        startActivity(intent)
    }

    fun addNotas(view: View) {
        val intent = Intent(this, addNote::class.java).apply {
        }
        startActivityForResult(intent, newWordActivityRequestCode)
    }

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
}