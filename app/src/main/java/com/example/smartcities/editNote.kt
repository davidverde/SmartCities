package com.example.smartcities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.smartcities.adapter.DESCRICAO
import com.example.smartcities.adapter.ID
import com.example.smartcities.adapter.TITULO
import com.example.smartcities.entities.Note
import com.example.smartcities.viewModel.NoteViewModel

class editNote : AppCompatActivity() {

    private lateinit var desc: EditText
    private lateinit var title: EditText
    private lateinit var notaViewModel: NoteViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        val editTitl = intent.getStringExtra(TITULO)
        val editDesc = intent.getStringExtra(DESCRICAO)


        findViewById<EditText>(R.id.note_edit).setText(editTitl)
        findViewById<EditText>(R.id.description_edit).setText(editDesc)

        notaViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)


        val buttonCancel = findViewById<Button>(R.id.button_edit_cancel)
        buttonCancel.setOnClickListener {
            val intent = Intent(this, recyclerLista::class.java)
            startActivity(intent)
        }
    }


    fun Editar(view: View) {
        title = findViewById(R.id.note_edit)
        desc = findViewById(R.id.description_edit)

        var message3 = intent.getIntExtra(ID, 0)
        val replyIntent = Intent()
        if (TextUtils.isEmpty(title.text) || TextUtils.isEmpty(desc.text))  {
            setResult(Activity.RESULT_CANCELED, replyIntent)
            if(TextUtils.isEmpty(title.text)){
                title.setError(getString(R.string.miss_title))
            }
            if(TextUtils.isEmpty(desc.text)){
                desc.setError(getString(R.string.miss_description))
            }
            Toast.makeText(this, R.string.erro_edit_nota, Toast.LENGTH_SHORT).show()
        } else {
            val nota = Note(id = message3, note = title.text.toString(), descricao = desc.text.toString())
            notaViewModel.update(nota)

            finish()
        }

    }

    fun ConsultNotas_1(view: View) {
        val intent = Intent(this, recyclerLista::class.java).apply {
        }
        startActivity(intent)
    }

    fun login_1(view: View) {
        val intent = Intent(this, MainActivity::class.java).apply {
        }
        startActivity(intent)
    }


}