package com.example.erick.recycler

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var personas:ArrayList<Persona>? = null
    var adapter:PersonaAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //recyclerPersonas.layoutManager = (GridLayoutManager(applicationContext,1) as RecyclerView.LayoutManager?)!!

        recyclerPersonas.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        personas = ArrayList<Persona>()
        adapter = PersonaAdapter(personas!!, this)
        recyclerPersonas.adapter = adapter

        val cache = DiskBasedCache(cacheDir, 1024*1024)
        val network = BasicNetwork(HurlStack())

        val requestQueue = RequestQueue(cache, network).apply{
            start()
        }
        val url = "https://randomuser.me/api/?results=10"

        val jsonObjectPersonas = JsonObjectRequest(
            Request.Method.GET, url,null,





            Response.Listener { response ->
                //Log.d("respuesta:", response.toString())
                var resultadosJSON = response.getJSONArray("results")

                for (indice in 0..resultadosJSON.length()-1){
                    val personaJSON = resultadosJSON.getJSONObject((indice))
                    val genero = personaJSON.getString("gender")
                    val nombreJson = personaJSON.getJSONObject("name")
                    val nombrePersona = "${nombreJson.getString("title")} ${nombreJson.getString("first")} ${nombreJson.getString("last")}"
                    val fotoJSON = personaJSON.getJSONObject("picture")
                    val foto = fotoJSON.getString("large")
                    val locationJSON = personaJSON.getJSONObject("location")
                    val coordJSON = locationJSON.getJSONObject("coordinates")
                    val latitud = coordJSON.getString("latitude").toDouble()
                    val longitud = coordJSON.getString("longitude").toDouble()

                    personas!!.add(Persona(nombrePersona, foto, longitud, latitud, genero))

                }
                adapter!!.notifyDataSetChanged()

            },
            Response.ErrorListener { error ->
                Log.wtf("error volley", error.localizedMessage)
            })
        requestQueue.add(jsonObjectPersonas)
    }
}
