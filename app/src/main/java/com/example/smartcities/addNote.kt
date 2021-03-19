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
import com.example.smartcities.viewModel.NoteViewModel

class addNote : AppCompatActivity() {

    private lateinit var desc: EditText
    private lateinit var title: EditText
    private lateinit var notaViewModel: NoteViewModel

    private lateinit var noteText: EditText
    private lateinit var description: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        noteText = findViewById(R.id.note)
        description = findViewById(R.id.description)

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(noteText.text) || TextUtils.isEmpty(description.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)

                if(TextUtils.isEmpty(noteText.text)){
                    noteText.error = getString(R.string.miss_title)
                }
                if(TextUtils.isEmpty(description.text)){
                    description.error = getString(R.string.miss_description) // R.string.miss_description.toString()
                }

                Toast.makeText(this, R.string.erro_add_nota, Toast.LENGTH_SHORT).show()
            } else {
                replyIntent.putExtra(EXTRA_REPLY_NOTE, noteText.text.toString())
                replyIntent.putExtra(EXTRA_REPLY_DESCRIPTION, description.text.toString())
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }

        }
    }

    companion object {
        const val EXTRA_REPLY_NOTE = "com.example.android.note"
        const val EXTRA_REPLY_DESCRIPTION = "com.example.android.description"
    }

    fun ConsultNotas_3(view: View) {
        val intent = Intent(this, recyclerLista::class.java).apply {
        }
        startActivity(intent)
    }

    fun login_3(view: View) {
        val intent = Intent(this, MainActivity::class.java).apply {
        }
        startActivity(intent)
    }
}