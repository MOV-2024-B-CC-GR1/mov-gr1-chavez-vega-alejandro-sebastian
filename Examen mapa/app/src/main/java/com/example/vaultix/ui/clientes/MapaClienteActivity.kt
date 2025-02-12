package com.example.vaultix.ui.clientes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import com.example.vaultix.R
import com.google.android.material.appbar.MaterialToolbar

class MapaClienteActivity : AppCompatActivity() {
    private lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_cliente)

        // Configurar la barra superior
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Configurar OSMdroid
        Configuration.getInstance().userAgentValue = applicationContext.packageName

        // Obtener coordenadas
        val latitud = intent.getDoubleExtra("latitud", 0.0)
        val longitud = intent.getDoubleExtra("longitud", 0.0)
        val nombreCliente = intent.getStringExtra("nombre") ?: ""

        // Configurar el mapa
        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        // Configurar el punto en el mapa
        val punto = GeoPoint(latitud, longitud)
        map.controller.setZoom(15.0)
        map.controller.setCenter(punto)

        // Agregar marcador
        val marker = Marker(map)
        marker.position = punto
        marker.title = nombreCliente
        marker.snippet = "Lat: $latitud, Long: $longitud"
        map.overlays.add(marker)
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}