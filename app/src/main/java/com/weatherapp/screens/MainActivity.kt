package com.weatherapp.screens

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.weatherapp.R
import com.weatherapp.client.APIClient.client
import com.weatherapp.databinding.ActivityMainBinding
import com.weatherapp.model.WeatherData
import com.weatherapp.service.APIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var apiService: APIService? = null
    private val app_id = "45d8254fe6022ad20da387655871fb51"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.setOnClickListener {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }

        apiService = client!!.create(APIService::class.java)
        getWeatherData(apiService, "indore")
        searchCityWeather()
    }

    private fun searchCityWeather() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                getWeatherData(apiService, query!!)
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                getWeatherData(apiService, query!!)
                return true
            }

        })
    }

    private fun getWeatherData(apiService: APIService?, city: String) {
        val call: Call<WeatherData> = apiService!!.getWeatherData(city, app_id, "metric")
        call.enqueue(object : Callback<WeatherData?> {

            override fun onResponse(call: Call<WeatherData?>?, response: Response<WeatherData?>?) {
                Log.d("response", "Success : ${response!!.body().toString()}")
                if (response.body() != null) {
                    binding.tvCity.text = city.trim()
                    showData(response.body()!!)
                } else {
                    Log.d("response", "Failed : ${response.message()}")
                }

            }

            override fun onFailure(call: Call<WeatherData?>?, t: Throwable?) {
                Log.d("response", "Failed : ${t!!.message}, ${call!!.cancel()}")
            }
        })
    }

    private fun showData(weatherData: WeatherData) {
        val main = weatherData.main
        val wind = weatherData.wind
        val sys = weatherData.sys
        val weather  = weatherData.weather
        val condition  = "${weather.firstOrNull()!!.main?:"unknown"}"
        binding.apply {

          //  tvDay.text = dayName()
            tvTemperature.text = "${main.temp}°C"
            tvTemperatureMin.text = "Min:${main.temp_min}°C"
            tvTemperatureMax.text = "Max:${main.temp_max}°C"
            tvConditionC.text = condition

            tvHumidity.text = "${main.humidity}%"
            tvWindSpeed.text = "${wind.speed}m/s"
            tvCondition.text = condition
            tvSunrise.text = "${time(sys.sunrise.toLong())}"
            tvSunset.text = "${time(sys.sunset.toLong())}"
            tvSeaLevel.text = "${main.pressure}hPa"

            // get data in system
            tvDayName.text = dayName()
            tvDate.text = date()

            changeTheme(condition)
        }
    }

    private fun changeTheme(condition: String) {
        when(condition){
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.animationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds","Clouds","Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.animationView.setAnimation(R.raw.cloud)
            }
            "Light Rain","Rain","Drizzle","Moderate Rain","Showers","Heavy Rain","Thunderstorm"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.animationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.animationView.setAnimation(R.raw.snow)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.animationView.setAnimation(R.raw.sun)
            }
        }
        binding.animationView.playAnimation()
    }

    private fun time(time:Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(time*1000))
    }

    private fun dayName(): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
}