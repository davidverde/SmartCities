package com.example.smartcities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.smartcities.api.Anomalia
import com.example.smartcities.api.EndPoints
import com.example.smartcities.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream


class add_anom : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var blackscreen: RelativeLayout

    var utl_atual = ""
    var lat = ""
    var long = ""
    var img_64 = ""

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_anom)

        utl_atual = intent.getStringExtra("UTL_ATUAL").toString()
        lat = intent.getStringExtra("LAT").toString()
        long = intent.getStringExtra("LONG").toString()
        //Toast.makeText(this, "utl: " + utl_atual + " LAT: " + lat + " LONG: " + long, Toast.LENGTH_SHORT).show()

        val title = this.findViewById<EditText>(R.id.titulo_info_add)
        val description = this.findViewById<EditText>(R.id.descricao_add)
        var spinner = this.findViewById<Spinner>(R.id.spinner_add)
        val imagem = this.findViewById<ImageView>(R.id.img_add)

        val options = arrayOf("Obras", "Acidente", "Mau estado", "Indefinido")

        spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options)

        blackscreen = findViewById(R.id.blackscreen)

       setUpSensorStuff() // função do sensor

    }


    fun addAnom_add(view: View) {

        val title = this.findViewById<EditText>(R.id.titulo_info_add)
        val description = this.findViewById<EditText>(R.id.descricao_add)
        var spinner = this.findViewById<Spinner>(R.id.spinner_add)
        val imagem = this.findViewById<ImageView>(R.id.img_add)

        val tipo = spinner.selectedItem.toString()

        var intent = Intent(this, MapsActivity::class.java)

        // POST report anomalia

        if (title.text.isNullOrEmpty() || description.text.isNullOrEmpty()) {

            if (title.text.isNullOrEmpty()) {
                title.error = getString(R.string.missTitle)
            }
            if (description.text.isNullOrEmpty()) {
                description.error = getString(R.string.miss_description)
            }

        } else if(imagem.drawable == null) {
            Toast.makeText(this, R.string.miss_img, Toast.LENGTH_SHORT).show()
        }else {


            val request = ServiceBuilder.buildService(EndPoints::class.java)
            val call = request.addAnom(utl_atual.toInt(), title.text.toString(), description.text.toString(), tipo, img_64, lat.toFloat(), long.toFloat())

            call.enqueue(object : Callback<Anomalia> {

                override fun onResponse(call: Call<Anomalia>, response: Response<Anomalia>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@add_anom, R.string.reportSuccess, Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<Anomalia>, t: Throwable) {
                    Log.d("TAG_", "err: " + t.message)
                }

            })
        }
    }


    fun voltarMapa_add(view: View) {
        var intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    fun escolherImg(view: View) {       // Escolher imagem da galeria
        openGalleryForImage()
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var bitmap : Bitmap
        super.onActivityResult(requestCode, resultCode, data)       // ir buscar a imagem da galeria
        if (resultCode == Activity.RESULT_OK && requestCode == 1){
            var imageView = this.findViewById<ImageView>(R.id.img_add)
            imageView.setImageURI(data?.data) // listar a imagem escolhida
            bitmap = (imageView.drawable as BitmapDrawable).bitmap  // passar para bitmap

            /* ----- PASSAR PARA BASE64 ------ */
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object
            val b: ByteArray = baos.toByteArray()
            val encodedImage: String = Base64.encodeToString(b, Base64.DEFAULT)
            img_64 = encodedImage       // guardar a base64 num var para guardar na BD

            /* // DESCODIFICAR BASE64
            val decodedString: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            imageView.setImageBitmap(decodedByte)*/

        }
    }



     override fun onSensorChanged(event: SensorEvent?) {

         if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
             val sides = event.values[0]
             val upDown = event.values[1]

             Log.d("TAG_", "Sides: " + sides + " UpDown: " + upDown)
             if(upDown > -11 && upDown < -7){
                 blackscreen.visibility = (View.VISIBLE)
             }else {
                 blackscreen.visibility = (View.INVISIBLE)
             }
         }
     }

     override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
         return
     }


     @RequiresApi(Build.VERSION_CODES.KITKAT)
     private fun setUpSensorStuff(){
         // Create the sensor manager
         sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

         // Specify the sensor you want to listen to
         sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
             sensorManager.registerListener(
                     this,
                     accelerometer,
                     SensorManager.SENSOR_DELAY_FASTEST,
                     SensorManager.SENSOR_DELAY_FASTEST
             )
         }
     }

     override fun onPause() {
         super.onPause()
         sensorManager.unregisterListener(this)
     }


}