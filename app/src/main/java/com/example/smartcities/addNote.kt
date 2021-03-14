package com.example.smartcities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText

class addNote : AppCompatActivity() {

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
            if (TextUtils.isEmpty(noteText.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                replyIntent.putExtra(EXTRA_REPLY_NOTE, noteText.text.toString())
                replyIntent.putExtra(EXTRA_REPLY_DESCRIPTION, description.text.toString())
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_REPLY_NOTE = "com.example.android.note"
        const val EXTRA_REPLY_DESCRIPTION = "com.example.android.description"
    }
}