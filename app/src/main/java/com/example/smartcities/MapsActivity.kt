package com.example.smartcities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import com.example.smartcities.api.Anomalia
import com.example.smartcities.api.EndPoints
import com.example.smartcities.api.ServiceBuilder
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, SensorEventListener {

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallBack: LocationCallback

    private lateinit var sensorManager: SensorManager
    private  var brightness : Sensor? = null

    lateinit var loc : LatLng
    var utl_atual : Int = 0

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

        utl_atual = id_utl.toString().toInt()

        // invocar pedido GET anomalias
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getAnomalias()
        var coordenadas : LatLng

        call.enqueue(object : Callback<List<Anomalia>> {

            override fun onResponse(call: Call<List<Anomalia>>, response: Response<List<Anomalia>>) {

                if (response.isSuccessful) {
                    for (Anomalia in response.body()!!) {   // listar todas as anomalias recebidas no mapa

                        coordenadas = LatLng(Anomalia.latitude, Anomalia.longitude)
                        if (id_utl.toString().toInt() == Anomalia.utilizador_id) {   // se a anomalia for do utilizador logado aparece o marcador azul
                            mMap.addMarker(MarkerOptions()
                                    .position(coordenadas).title(Anomalia.titulo)
                                    .snippet(Anomalia.descricao + ";" + Anomalia.imagem + ";" + Anomalia.utilizador_id + ";" + id_utl.toString() + ";" + nome.toString() + ';' + Anomalia.tipo + ';' + Anomalia.id_amon)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                        } else {
                            mMap.addMarker(MarkerOptions()
                                    .position(coordenadas).title(Anomalia.titulo)   // titulo
                                    .snippet(Anomalia.descricao + ";" + Anomalia.imagem + ";" + Anomalia.utilizador_id + ";" + id_utl.toString() + ";" + nome.toString() + ';' + Anomalia.tipo + ';' + Anomalia.id_amon)
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
                loc = LatLng(lastLocation.latitude, lastLocation.longitude)
                //mMap.addMarker(MarkerOptions().position(loc).title("UTL").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pessoa)))
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f))
                Log.d("DAVIDLOC", "latitude: " + loc.latitude + " - longitude: " + loc.longitude)
            }
        }

        createLocationRequest()

        setUpSensorStuff()  // função sensor
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

        if(ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }else{
            mMap.isMyLocationEnabled = true
        }

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
        sensorManager.unregisterListener(this)
    }

    public override fun onResume() {
        super.onResume()
        startLocationUpdates()
        sensorManager.registerListener(this, brightness, SensorManager.SENSOR_DELAY_NORMAL)
    }

    // caso o utilizador utilize o botão de voltar para tras do telefone
    override fun onBackPressed() {
        Toast.makeText(this, R.string.erro_voltar_atras, Toast.LENGTH_SHORT).show()
    }


    override fun onInfoWindowClick(marker: Marker) {

        val strs = marker.snippet.split(";").toTypedArray() //0-descricao 1-imagem 2-utl_report 3-utl_logado 4-nome_utl_logado 5-tipo 6-id_amon

        var array = strs[1].chunked(strs[1].length / 3)

        //Guardar base_3 no Shared Preferences
        val shared: SharedPreferences = getSharedPreferences(
                getString(R.string.login_pref), Context.MODE_PRIVATE
        )
        with(shared.edit()){

            putString(getString(R.string.base_3), "${array[2]}")
            commit()
        }

        if(strs[2].toInt() == strs[3].toInt()){     // se o utilizador que reportou a anomalia for o mesmo que tem login iniciado

            val intent = Intent(this, edit_anom::class.java)
            intent.putExtra("MARKER", marker.title);
            //intent.putExtra("STRS", marker.snippet);
            //intent.putExtra("BASE", strs[1])
            intent.putExtra("DESCR", strs[0])
            intent.putExtra("NOME", strs[4])
            intent.putExtra("TIPO", strs[5])
            intent.putExtra("ID_ANOM", strs[6])
            intent.putExtra("BASE1", array[0])
            intent.putExtra("BASE2", array[1])

            intent.putExtra("UTL_ATUAL", utl_atual.toString())
            intent.putExtra("LAT", loc.latitude.toString())
            intent.putExtra("LONG", loc.longitude.toString())
            //intent.putExtra("BASE3", array[2])
            startActivity(intent)

        }else{                                      // caso nao seja
            val intent = Intent(this, detalhe_anom::class.java)
            intent.putExtra("MARKER", marker.title);
            //intent.putExtra("STRS", marker.snippet);
            //intent.putExtra("BASE", strs[1])
            intent.putExtra("DESCR", strs[0])
            intent.putExtra("TIPO", strs[5])
            intent.putExtra("BASE1", array[0])
            intent.putExtra("BASE2", array[1])

            intent.putExtra("UTL_ATUAL", utl_atual.toString())
            intent.putExtra("LAT", loc.latitude.toString())
            intent.putExtra("LONG", loc.longitude.toString())
            //intent.putExtra("BASE3", array[2])
            startActivity(intent)
        }

    }

    fun add_anom_btn_plus(view: View) {     // mudar para o ecra de reportar anomalia e passar o utl atual e a localização
        val intent = Intent(this, add_anom::class.java)
        intent.putExtra("UTL_ATUAL", utl_atual.toString())
        intent.putExtra("LAT", loc.latitude.toString())
        intent.putExtra("LONG", loc.longitude.toString())
        startActivity(intent)
    }


    fun search_by_distance(view: View) {}



    fun tipo_todos(view: View) {
        mMap.clear()    // limpa os marcadores do mapa

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

        utl_atual = id_utl.toString().toInt()

        // invocar pedido GET anomalias
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getAnomalias()
        var coordenadas : LatLng

        call.enqueue(object : Callback<List<Anomalia>> {

            override fun onResponse(call: Call<List<Anomalia>>, response: Response<List<Anomalia>>) {

                if (response.isSuccessful) {
                    for (Anomalia in response.body()!!) {   // listar todas as anomalias recebidas no mapa

                        coordenadas = LatLng(Anomalia.latitude, Anomalia.longitude)
                        if (id_utl.toString().toInt() == Anomalia.utilizador_id) {   // se a anomalia for do utilizador logado aparece o marcador azul
                            mMap.addMarker(MarkerOptions()
                                    .position(coordenadas).title(Anomalia.titulo)
                                    .snippet(Anomalia.descricao + ";" + Anomalia.imagem + ";" + Anomalia.utilizador_id + ";" + id_utl.toString() + ";" + nome.toString() + ';' + Anomalia.tipo + ';' + Anomalia.id_amon)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                        } else {
                            mMap.addMarker(MarkerOptions()
                                    .position(coordenadas).title(Anomalia.titulo)   // titulo
                                    .snippet(Anomalia.descricao + ";" + Anomalia.imagem + ";" + Anomalia.utilizador_id + ";" + id_utl.toString() + ";" + nome.toString() + ';' + Anomalia.tipo + ';' + Anomalia.id_amon)
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


    fun tipo_obras(view: View) {
        mMap.clear()    // limpa os marcadores do mapa

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

        utl_atual = id_utl.toString().toInt()

        // invocar pedido GET anomalias
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getAnomalias()
        var coordenadas : LatLng

        call.enqueue(object : Callback<List<Anomalia>> {

            override fun onResponse(call: Call<List<Anomalia>>, response: Response<List<Anomalia>>) {

                if (response.isSuccessful) {
                    for (Anomalia in response.body()!!) {   // listar todas as anomalias recebidas no mapa

                        if (Anomalia.tipo.equals("Obras")) {
                            coordenadas = LatLng(Anomalia.latitude, Anomalia.longitude)
                            if (id_utl.toString().toInt() == Anomalia.utilizador_id) {   // se a anomalia for do utilizador logado aparece o marcador azul
                                mMap.addMarker(MarkerOptions()
                                        .position(coordenadas).title(Anomalia.titulo)
                                        .snippet(Anomalia.descricao + ";" + Anomalia.imagem + ";" + Anomalia.utilizador_id + ";" + id_utl.toString() + ";" + nome.toString() + ';' + Anomalia.tipo + ';' + Anomalia.id_amon)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                            } else {
                                mMap.addMarker(MarkerOptions()
                                        .position(coordenadas).title(Anomalia.titulo)   // titulo
                                        .snippet(Anomalia.descricao + ";" + Anomalia.imagem + ";" + Anomalia.utilizador_id + ";" + id_utl.toString() + ";" + nome.toString() + ';' + Anomalia.tipo + ';' + Anomalia.id_amon)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))              //cor
                            }
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


    fun tipo_acidente(view: View) {
        mMap.clear()    // limpa os marcadores do mapa

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

        utl_atual = id_utl.toString().toInt()

        // invocar pedido GET anomalias
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getAnomalias()
        var coordenadas : LatLng

        call.enqueue(object : Callback<List<Anomalia>> {

            override fun onResponse(call: Call<List<Anomalia>>, response: Response<List<Anomalia>>) {

                if (response.isSuccessful) {
                    for (Anomalia in response.body()!!) {   // listar todas as anomalias recebidas no mapa

                        if (Anomalia.tipo.equals("Acidente")) {
                            coordenadas = LatLng(Anomalia.latitude, Anomalia.longitude)
                            if (id_utl.toString().toInt() == Anomalia.utilizador_id) {   // se a anomalia for do utilizador logado aparece o marcador azul
                                mMap.addMarker(MarkerOptions()
                                        .position(coordenadas).title(Anomalia.titulo)
                                        .snippet(Anomalia.descricao + ";" + Anomalia.imagem + ";" + Anomalia.utilizador_id + ";" + id_utl.toString() + ";" + nome.toString() + ';' + Anomalia.tipo + ';' + Anomalia.id_amon)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                            } else {
                                mMap.addMarker(MarkerOptions()
                                        .position(coordenadas).title(Anomalia.titulo)   // titulo
                                        .snippet(Anomalia.descricao + ";" + Anomalia.imagem + ";" + Anomalia.utilizador_id + ";" + id_utl.toString() + ";" + nome.toString() + ';' + Anomalia.tipo + ';' + Anomalia.id_amon)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))              //cor
                            }
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

    fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Float {      // função para calcular distancia
        val result = FloatArray(1)
        Location.distanceBetween(lat1, lng1, lat2, lng2, result)
        return result[0]
    }

    fun distancia_500(view: View) {
        mMap.clear()    // limpa os marcadores do mapa

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

        utl_atual = id_utl.toString().toInt()

        // invocar pedido GET anomalias
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getAnomalias()
        var coordenadas : LatLng

        call.enqueue(object : Callback<List<Anomalia>> {

            override fun onResponse(call: Call<List<Anomalia>>, response: Response<List<Anomalia>>) {

                if (response.isSuccessful) {
                    mMap.addCircle(CircleOptions()
                            .center(LatLng(loc.latitude, loc.longitude))
                            .radius(500.0)
                            .strokeColor(Color.BLUE))
                    for (Anomalia in response.body()!!) {   // listar todas as anomalias recebidas no mapa
                        var dist = calculateDistance(loc.latitude, loc.longitude, Anomalia.latitude, Anomalia.longitude)
                        if (dist <= 500) {
                            coordenadas = LatLng(Anomalia.latitude, Anomalia.longitude)
                            if (id_utl.toString().toInt() == Anomalia.utilizador_id) {   // se a anomalia for do utilizador logado aparece o marcador azul
                                mMap.addMarker(MarkerOptions()
                                        .position(coordenadas).title(Anomalia.titulo)
                                        .snippet(Anomalia.descricao + ";" + Anomalia.imagem + ";" + Anomalia.utilizador_id + ";" + id_utl.toString() + ";" + nome.toString() + ';' + Anomalia.tipo + ';' + Anomalia.id_amon)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                            } else {
                                mMap.addMarker(MarkerOptions()
                                        .position(coordenadas).title(Anomalia.titulo)   // titulo
                                        .snippet(Anomalia.descricao + ";" + Anomalia.imagem + ";" + Anomalia.utilizador_id + ";" + id_utl.toString() + ";" + nome.toString() + ';' + Anomalia.tipo + ';' + Anomalia.id_amon)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))              //cor
                            }
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


    fun distancia_1500(view: View) {
        mMap.clear()    // limpa os marcadores do mapa

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

        utl_atual = id_utl.toString().toInt()

        // invocar pedido GET anomalias
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getAnomalias()
        var coordenadas : LatLng

        call.enqueue(object : Callback<List<Anomalia>> {

            override fun onResponse(call: Call<List<Anomalia>>, response: Response<List<Anomalia>>) {

                if (response.isSuccessful) {
                    mMap.addCircle(CircleOptions()
                            .center(LatLng(loc.latitude, loc.longitude))
                            .radius(1500.0)
                            .strokeColor(Color.BLUE))
                    for (Anomalia in response.body()!!) {   // listar todas as anomalias recebidas no mapa
                        var dist = calculateDistance(loc.latitude, loc.longitude, Anomalia.latitude, Anomalia.longitude)
                        if (dist <= 1500) {
                            coordenadas = LatLng(Anomalia.latitude, Anomalia.longitude)
                            if (id_utl.toString().toInt() == Anomalia.utilizador_id) {   // se a anomalia for do utilizador logado aparece o marcador azul
                                mMap.addMarker(MarkerOptions()
                                        .position(coordenadas).title(Anomalia.titulo)
                                        .snippet(Anomalia.descricao + ";" + Anomalia.imagem + ";" + Anomalia.utilizador_id + ";" + id_utl.toString() + ";" + nome.toString() + ';' + Anomalia.tipo + ';' + Anomalia.id_amon)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                            } else {
                                mMap.addMarker(MarkerOptions()
                                        .position(coordenadas).title(Anomalia.titulo)   // titulo
                                        .snippet(Anomalia.descricao + ";" + Anomalia.imagem + ";" + Anomalia.utilizador_id + ";" + id_utl.toString() + ";" + nome.toString() + ';' + Anomalia.tipo + ';' + Anomalia.id_amon)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))              //cor
                            }
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


    fun distancia_3000(view: View) {
        mMap.clear()    // limpa os marcadores do mapa

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

        utl_atual = id_utl.toString().toInt()

        // invocar pedido GET anomalias
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getAnomalias()
        var coordenadas : LatLng

        call.enqueue(object : Callback<List<Anomalia>> {

            override fun onResponse(call: Call<List<Anomalia>>, response: Response<List<Anomalia>>) {

                if (response.isSuccessful) {
                    mMap.addCircle(CircleOptions()
                            .center(LatLng(loc.latitude, loc.longitude))
                            .radius(3000.0)
                            .strokeColor(Color.BLUE))
                    for (Anomalia in response.body()!!) {   // listar todas as anomalias recebidas no mapa
                        var dist = calculateDistance(loc.latitude, loc.longitude, Anomalia.latitude, Anomalia.longitude)
                        if (dist <= 3000) {
                            coordenadas = LatLng(Anomalia.latitude, Anomalia.longitude)
                            if (id_utl.toString().toInt() == Anomalia.utilizador_id) {   // se a anomalia for do utilizador logado aparece o marcador azul
                                mMap.addMarker(MarkerOptions()
                                        .position(coordenadas).title(Anomalia.titulo)
                                        .snippet(Anomalia.descricao + ";" + Anomalia.imagem + ";" + Anomalia.utilizador_id + ";" + id_utl.toString() + ";" + nome.toString() + ';' + Anomalia.tipo + ';' + Anomalia.id_amon)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                            } else {
                                mMap.addMarker(MarkerOptions()
                                        .position(coordenadas).title(Anomalia.titulo)   // titulo
                                        .snippet(Anomalia.descricao + ";" + Anomalia.imagem + ";" + Anomalia.utilizador_id + ";" + id_utl.toString() + ";" + nome.toString() + ';' + Anomalia.tipo + ';' + Anomalia.id_amon)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))              //cor
                            }
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



    /* ------ SENSORES ------- */
    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val light = event.values[0]
            Log.d("TAG_", light.toString())

            if(light < 500){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    private fun setUpSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        brightness = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }


}