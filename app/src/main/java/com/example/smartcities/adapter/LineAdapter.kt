package com.example.smartcities.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartcities.R
import com.example.smartcities.addNote
import com.example.smartcities.editNote
import com.example.smartcities.entities.Note
import kotlinx.android.synthetic.main.recyclerline.view.*

const val TITULO="TITULO"
const val DESCRICAO="DESCRICAO"
const val ID="ID"

class LineAdapter internal constructor(
    context: Context, private val callbackInterface:CallbackInterface
) : RecyclerView.Adapter<LineAdapter.NoteViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var notes = emptyList<Note>() // Cached copy of cities

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteItemView: TextView = itemView.findViewById(R.id.title)
        val edit: ImageButton = itemView.findViewById(R.id.editarNota)
        val delete: ImageButton = itemView.findViewById(R.id.eliminarNota)
    }

    interface CallbackInterface {
        fun passResultCallback(id: Int?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerline, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val current = notes[position]
        holder.noteItemView.text = current.note

        val id: Int? = current.id
        val descricao: String? = current.descricao

        holder.delete.setOnClickListener {
            callbackInterface.passResultCallback(current.id)
        }

        holder.edit.setOnClickListener {
            val context = holder.noteItemView.context
            val titl = holder.noteItemView.text.toString()

            val intent = Intent(context, editNote::class.java).apply {
                putExtra(TITULO, titl)
                putExtra(DESCRICAO, descricao )
                putExtra( ID,id)
            }
            context.startActivity(intent)
        }

    }

    internal fun setNotes(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    override fun getItemCount() = notes.size

}
