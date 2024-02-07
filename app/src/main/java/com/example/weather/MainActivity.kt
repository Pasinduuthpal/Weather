package com.example.weather

import android.Manifest
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log


class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val CITY: String = "Denver,United States"
    val API: String = "c9e5f9c9bbeb12fcfcee082daff2a77c"
    var LAT:String =""
     var LANG:String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getLocation()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        weatherTask().execute()
    }
    private fun getLocation(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        val location =    fusedLocationProviderClient. lastLocation
        location.addOnSuccessListener{
            if (it != null) {
                    LAT = it.latitude.toString()
                    LANG = it.longitude.toString()
//                Toast.makeText(this@MainActivity,"error:$LAT : $LANG",Toast.LENGTH_LONG).show()
                    Log.e("WeatherApp", "WeatherTask: Exception - $LAT")
                }
        }
    }
    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorTest).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response =
                    URL("https://api.openweathermap.org/data/2.5/weather?lat=$LAT&lon=$LANG&units=metric&appid=c9e5f9c9bbeb12fcfcee082daff2a77c")
                        .readText(Charsets.UTF_8)
            } catch (e: Exception) {
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jasonOgj = JSONObject(result)
                val main = jasonOgj.getJSONObject("main")
                val sys = jasonOgj.getJSONObject("sys")
                val wind = jasonOgj.getJSONObject("wind")
                val updatedAt: Long = jasonOgj.getLong("dt")
                val updatedAtText =
                    "Updated at: " + SimpleDateFormat("dd/mm/yyyy hh:mm a", Locale.ENGLISH).format(
                        Date(updatedAt * 1000)
                    )
                val weather = jasonOgj.getJSONArray("weather").getJSONObject(0)
                val temp = main.getString("temp") + "˚C"
                val tempMin = "Min Temp: " + main.getString("temp_min") + "˚C"
                val tempMax = "Max Temp: " + main.getString("temp_max") + "˚C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")
                val sunrise:Long =sys.getLong("sunrise")
                val sunset:Long =sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")
                val address = jasonOgj.getString("name")+", "+ sys.getString("country")

                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updatedAt).text = updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a",Locale.ENGLISH).format(Date(sunrise*1000))
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a",Locale.ENGLISH).format(Date(sunset*1000))
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity

                findViewById<ProgressBar>(R.id.loader).visibility=View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility=View.VISIBLE
            }
            catch (e: Exception){
                Toast.makeText(this@MainActivity,"error:$e",Toast.LENGTH_LONG).show()
                Log.d("WeatherApp", "Error"+e.message.toString())
                findViewById<ProgressBar>(R.id.loader).visibility=View.GONE
                findViewById<TextView>(R.id.errorTest).visibility=View.VISIBLE
            }
        }
    }
}