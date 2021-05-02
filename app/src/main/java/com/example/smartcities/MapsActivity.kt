package com.example.smartcities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.smartcities.api.Anomalia
import com.example.smartcities.api.EndPoints
import com.example.smartcities.api.OutputPost
import com.example.smartcities.api.ServiceBuilder
import com.google.android.gms.location.*
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

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallBack: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // guardar o id do utilizador atual
        var id_utl : Any? = 0
        var nome : Any? = ""

        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.login_pref), Context.MODE_PRIVATE
        )
        if (sharedPref != null){
            id_utl = sharedPref.all[getString(R.string.id_login_user)]
            nome = sharedPref.all[getString(R.string.nome_login_user)]
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
                                    .position(coordenadas).title(Anomalia.titulo)
                                    .snippet(Anomalia.descricao + "+" + Anomalia.imagem + "+" + Anomalia.utilizador_id + "+" + id_utl.toString() + "+" + nome.toString() + '+' + Anomalia.tipo + '+' + Anomalia.id_amon)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                        } else {
                            mMap.addMarker(MarkerOptions()
                                    .position(coordenadas).title(Anomalia.titulo)   // titulo
                                    .snippet(Anomalia.descricao + "+" + Anomalia.imagem + "+" + Anomalia.utilizador_id + "+" + id_utl.toString() + "+" + nome.toString() + '+' + Anomalia.tipo + '+' + Anomalia.id_amon)
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


        /* ------ LOCATION CALLBACK --------- */
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        locationCallBack = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
                var loc = LatLng(lastLocation.latitude, lastLocation.longitude)
                //mMap.addMarker(MarkerOptions().position(loc).title("UTL").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pessoa)))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f))
                Log.d("DAVIDLOC", "latitude: " + loc.latitude + " - longitude: " + loc.longitude)
            }
        }

        createLocationRequest()

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

        googleMap.setOnInfoWindowClickListener(this)

    }

    private fun startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            1)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallBack, null)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 5000     // 5 segundos
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallBack)
    }

    public override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    // caso o utilizador utilize o botão de voltar para tras do telefone
    override fun onBackPressed() {
        Toast.makeText(this, R.string.erro_voltar_atras, Toast.LENGTH_SHORT).show()
    }


    override fun onInfoWindowClick(marker: Marker) {

        val strs = marker.snippet.split("+").toTypedArray() //0-descricao 1-imagem 2-utl_report 3-utl_logado 4-nome_utl_logado 5-tipo 6-id_amon

        if(strs[2].toInt() == strs[3].toInt()){     // se o utilizador que reportou a anomalia for o mesmo que tem login iniciado
            val intent = Intent(this, edit_anom::class.java)
            intent.putExtra("MARKER", marker.title);
            intent.putExtra("STRS", marker.snippet);
            startActivity(intent)
        }else{                                      // caso nao seja
            val intent = Intent(this, detalhe_anom::class.java)
            intent.putExtra("MARKER", marker.title);
            intent.putExtra("STRS", marker.snippet);
            startActivity(intent)
        }

    }

    fun add_anom_btn_plus(view: View) {
        Toast.makeText(this, "OLAAAAAA", Toast.LENGTH_SHORT).show()
    }


}