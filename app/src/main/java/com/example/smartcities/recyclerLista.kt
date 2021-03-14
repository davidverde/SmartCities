package com.example.smartcities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartcities.adapter.LineAdapter
import com.example.smartcities.dataClasses.Place
import kotlinx.android.synthetic.main.activity_recycler_lista.*


class recyclerLista : AppCompatActivity() {

    private  lateinit var myList: ArrayList<Place>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_recycler_lista)

        myList = ArrayList<Place>()

        for(i in 0 until 100){
            myList.add(Place("Country $i", i*100, "Capital $i"))
        }
        recycler_view.adapter = LineAdapter(myList)
        recycler_view.layoutManager = LinearLayoutManager(this)

    }

    fun insert(view: View) {
        myList.add(0, Place("Country XXX", 999, "Capital XXX"))
        recycler_view.adapter?.notifyDataSetChanged()
    }
}