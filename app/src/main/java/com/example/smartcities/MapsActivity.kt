package com.example.smartcities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartcities.api.Anomalia
import com.example.smartcities.api.EndPoints
import com.example.smartcities.api.OutputPost
import com.example.smartcities.api.ServiceBuilder
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // guardar o id do utilizador atual
        var id_utl : Any? = 0

        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.login_pref), Context.MODE_PRIVATE
        )
        if (sharedPref != null){
            id_utl = sharedPref.all[getString(R.string.id_login_user)]
        }


        // invocar pedido GET anomalias
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getAnomalias()
        var coordenadas : LatLng

        call.enqueue(object : Callback<List<Anomalia>> {

            override fun onResponse(call: Call<List<Anomalia>>, response: Response<List<Anomalia>>) {

                if (response.isSuccessful){
                    for(Anomalia in response.body()!!){   // listar todas as anomalias recebidas no mapa

                        coordenadas = LatLng(Anomalia.latitude, Anomalia.longitude)

                        if(id_utl.toString().toInt() == Anomalia.utilizador_id){   // se a anomalia for do utilizador logado aparece o marcador azul
                            mMap.addMarker(MarkerOptions()
                                    .position(coordenadas).title(Anomalia.utilizador_id.toString() + " - " + Anomalia.titulo)
                                    .snippet(Anomalia.descricao + "," + Anomalia.imagem)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                        } else {
                            mMap.addMarker(MarkerOptions()
                                    .position(coordenadas).title(Anomalia.utilizador_id.toString() + " - " + Anomalia.titulo)   // titulo
                                    .snippet(Anomalia.descricao + "," + Anomalia.imagem)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))              //cor
                        }
                    }
                }

            }

            override fun onFailure(call: Call<List<Anomalia>>, t: Throwable) {
                /* Toast.makeText(this@MainActivity, "${t.message}", Toast.LENGTH_SHORT).show()*/
                Log.d("TAG_", "err: " + t.message)
            }
        })

    }




    /* --------- TOP MENU --------------- */

    // Aplicar o top menu a este ecrã
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_menu, menu)
        return true;
    }

    // Opções do top menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.mapa_menu_add_nota -> {

                // mudar para o ecrã adicionar nota
                val intent = Intent(this, addNote::class.java).apply {
                }
                startActivity(intent)
                true
            }

            R.id.mapa_menu_consultar_notas -> {

                // mudar para o ecrã consultar notas
                val intent = Intent(this, recyclerLista::class.java).apply {
                }
                startActivity(intent)

                true
            }

            R.id.mapa_menu_logout -> {

                //Eliminar utilizador no Shared Preferences
                val shared: SharedPreferences = getSharedPreferences(
                    getString(R.string.login_pref), Context.MODE_PRIVATE
                )
                with(shared.edit()) {
                    putBoolean(getString(R.string.user_logged), false)
                    putString(getString(R.string.email_login_user), "")
                    putString(getString(R.string.id_login_user), "")
                    putString(getString(R.string.nome_login_user), "")
                    commit()
                }

                // mudar para o ecrã login
                val intent = Intent(this, MainActivity::class.java).apply {
                }
                startActivity(intent)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /* ---------------------------------- */

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val viana = LatLng(41.6946, -8.83016)
        //mMap.addMarker(MarkerOptions().position(viana).title("Centro de Viana"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(viana, 15.0f)) // centra o mapa nas cordenadas do ponto e com o zoom já aplicado
        mMap.setInfoWindowAdapter(infoWindowAdapter(this))
    }


    // caso o utilizador utilize o botão de voltar para tras do telefone
    override fun onBackPressed() {
        Toast.makeText(this, R.string.erro_voltar_atras, Toast.LENGTH_SHORT).show()
    }




}