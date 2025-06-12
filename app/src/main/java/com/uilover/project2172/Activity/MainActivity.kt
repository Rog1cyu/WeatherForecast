package com.uilover.project2172.Activity
import android.util.Log
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.uilover.project2172.Adapter.HourlyAdapter
import com.uilover.project2172.Adapter.OtherCityAdapter
import com.uilover.project2172.Api.RetrofitClient
import com.uilover.project2172.Model.CityModel
import com.uilover.project2172.Model.HourlyModel
import com.uilover.project2172.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val apiKey = "1a93f36bbcbf88e1bb546c50edb79f74"
    private val cityList = listOf("Paris", "Berlin", "Rome", "London", "New York")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chipNavigator.setItemSelected(com.uilover.project2172.R.id.home, true)
        binding.button.setOnClickListener {
            val city = binding.editTextText.text.toString().trim()
            if (city.isNotEmpty()) {
                fetchWeather(city)
            } else {
                Toast.makeText(this, "请输入城市名", Toast.LENGTH_SHORT).show()
            }
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        initRecyclerviewHourly()
        initRecyclerOtherCity()

        // 获取默认城市天气
        fetchWeather("Beijing")
    }

    private fun initRecyclerviewHourly() {
        val items = arrayListOf(
            HourlyModel("9 pm", 28, "cloudy"),
            HourlyModel("10 pm", 29, "sunny"),
            HourlyModel("11 pm", 30, "windy"),
            HourlyModel("12 pm", 31, "cloudy_2"),
            HourlyModel("1 am", 10, "snowy")
        )

        binding.view1.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.view1.adapter = HourlyAdapter(items)
    }

    private fun initRecyclerOtherCity() {
        val cityModels = ArrayList<CityModel>()
        val adapter = OtherCityAdapter(cityModels)
        binding.view2.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.view2.adapter = adapter

        lifecycleScope.launch {
            for (city in cityList) {
                try {
                    val response = withContext(Dispatchers.IO) {
                        RetrofitClient.weatherApi.getWeatherByCity(city, apiKey)
                    }

                    val temp = response.main.temp.toInt()
                    val humidity = response.main.humidity
                    val wind = (5..25).random() // OpenWeatherMap 免费接口不提供 wind speed，模拟数据
                    val rain = (0..50).random() // 同理，模拟降水
                    val icon = response.weather[0].icon

                    // 你可以映射 icon 为你的本地图标资源名
                    val picPath = when {
                        icon.contains("d") && icon.startsWith("01") -> "sunny"
                        icon.contains("n") && icon.startsWith("01") -> "night"
                        icon.startsWith("02") || icon.startsWith("03") -> "cloudy"
                        icon.startsWith("09") || icon.startsWith("10") -> "rainy"
                        icon.startsWith("13") -> "snowy"
                        else -> "cloudy"
                    }

                    val model = CityModel(city, temp, picPath, wind, humidity, rain)
                    cityModels.add(model)
                    adapter.notifyItemInserted(cityModels.size - 1)

                } catch (e: Exception) {
                    Log.e("CityLoadError", "加载城市 $city 失败：${e.message}")
                }
            }
        }
    }
    private fun fetchWeather(city: String) {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.weatherApi.getWeatherByCity(city, apiKey)
                }
                Log.d("WeatherDebug", "Weather in ${response.name}, temp: ${response.main.temp}")
                val weather = response.weather[0]
                val temp = response.main.temp
                val humidity = response.main.humidity
                val icon = weather.icon
                // 显示到布局中（你可自行适配现有 View）
                binding.cityText.text = response.name
                binding.tempText.text = "$temp°C"
                binding.humidityText.text = "湿度：$humidity%"
                binding.descText.text = weather.description

                val iconUrl = "https://openweathermap.org/img/wn/${icon}@2x.png"
                Glide.with(this@MainActivity)
                    .load(iconUrl)
                    .into(binding.iconImage)

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("WeatherError", "错误: ${e.message}")
                Toast.makeText(this@MainActivity, "天气数据获取失败", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
