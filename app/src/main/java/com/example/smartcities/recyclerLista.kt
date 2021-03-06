package com.example.smartcities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartcities.adapter.LineAdapter
import com.example.smartcities.entities.Note
import com.example.smartcities.viewModel.NoteViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_recycler_lista.*


class recyclerLista : AppCompatActivity(), LineAdapter.CallbackInterface {

    private lateinit var noteViewModel: NoteViewModel
    private val newWordActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_lista)

        // recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = LineAdapter(this, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // view model
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        noteViewModel.allNotes.observe(this, Observer { notes ->            // fazer a monotorização para alterar os dados acrescentados ou tirados OBSERVER
            // Update the cached copy of the words in the adapter.
            notes?.let { adapter.setNotes(it) }
        })

    }

    override fun passResultCallback(id: Int?) {
        noteViewModel.delete(id)
    }

    fun ConsultNotas_4(view: View) {
        val intent = Intent(this, addNote::class.java).apply {
        }
        startActivity(intent)
    }

    fun login_4(view: View) {
        val intent = Intent(this, MainActivity::class.java).apply {
        }
        startActivity(intent)
    }

}