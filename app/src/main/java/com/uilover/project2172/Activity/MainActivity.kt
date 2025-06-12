package com.uilover.project2172.Activity
import android.app.AlertDialog
import android.content.Intent
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
import com.uilover.project2172.Model.AppDatabase
import com.uilover.project2172.Model.CityModel
import com.uilover.project2172.Model.HourlyModel
import com.uilover.project2172.Model.WeatherCacheEntity
import com.uilover.project2172.R
import com.uilover.project2172.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject

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
                initRecyclerviewHourly(city)
            } else {
                Toast.makeText(this, "请输入城市名", Toast.LENGTH_SHORT).show()
            }
        }
        /*binding.chipNavigator.setOnItemSelectedListener { id ->
            when (id) {
                R.id.explorer -> {
                    giveClothingSuggestion()
                }
            }
        }*/
        //ai穿衣建议
        binding.chipNavigator.setOnItemSelectedListener { id ->
            when (id) {
                R.id.explorer -> {
                    val tempStr = binding.tempText.text.toString().replace("°C", "").trim()
                    val desc = binding.descText.text.toString().trim()
                    val temp = tempStr.toFloatOrNull() ?: return@setOnItemSelectedListener

                    lifecycleScope.launch {
                        val advice = getClothingAdviceFromGPT(temp, desc, "sk-proj-fWYdZ93rmabgccTUNJi6X5mK5vkHLw7erWDvUHBa1QP94WXFYyeaH3_iYHwG27W2YGeHlytNxFT3BlbkFJ5-" +
                                "7ytVkgsp1nioQAyErB-CCUPew_prasjr4WFgPqxZLaIN8aaZnn_GtT7a429BmM5jAveE9CIA")
                        showAdviceDialog(advice)
                    }
                }
                R.id.bookmark -> {
                    val intent = Intent(this, MemoActivity::class.java)
                    startActivity(intent)
                }
            }

        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        initRecyclerviewHourly("Tokyo")
        initRecyclerOtherCity()

        // 获取默认城市天气
        fetchWeather("Tokyo")

    }

    private fun initRecyclerviewHourly(city: String) {
        val items = ArrayList<HourlyModel>()
        val adapter = HourlyAdapter(items)
        binding.view1.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.view1.adapter = adapter

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.weatherApi.getHourlyForecastByCity(city, apiKey)
                }

                val firstHours = response.list.take(6) // 取前6个3小时预测项
                for (item in firstHours) {
                    val hour = item.dt_txt.substring(11, 16) // 格式 "15:00"
                    val temp = item.main.temp.toInt()
                    val icon = item.weather[0].icon

                    val picPath = when {
                        icon.contains("d") && icon.startsWith("01") -> "sunny"
                        icon.contains("n") && icon.startsWith("01") -> "night"
                        icon.startsWith("02") || icon.startsWith("03") -> "cloudy"
                        icon.startsWith("09") || icon.startsWith("10") -> "rainy"
                        icon.startsWith("13") -> "snowy"
                        else -> "cloudy"
                    }

                    items.add(HourlyModel(hour, temp, picPath))
                    adapter.notifyItemInserted(items.size - 1)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("WeatherError", "错误: ${e.message}")
                Toast.makeText(this@MainActivity, "小时天气加载失败", Toast.LENGTH_SHORT).show()
            }
        }
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
        val db = AppDatabase.getInstance(this)
        val dao = db.weatherCacheDao()

        lifecycleScope.launch {
            // 尝试从本地数据库获取缓存
            val local = withContext(Dispatchers.IO) {
                dao.getWeatherByCity(city)
            }

            if (local != null && System.currentTimeMillis() - local.timestamp < 3 * 60 * 1000) {
                //本地缓存有效（3分钟）
                displayWeather(
                    city = local.cityName,
                    temp = local.temp,
                    humidity = local.humidity,
                    desc = local.description,
                    icon = local.icon
                )
            } else {
                // 无缓存 → 请求网络
                try {
                    val response = withContext(Dispatchers.IO) {
                        RetrofitClient.weatherApi.getWeatherByCity(city, apiKey)
                    }

                    val icon = response.weather[0].icon
                    val entity = WeatherCacheEntity(
                        cityName = response.name,
                        temp = response.main.temp,
                        humidity = response.main.humidity,
                        description = response.weather[0].description,
                        icon = icon,
                        timestamp = System.currentTimeMillis()
                    )

                    withContext(Dispatchers.IO) {
                        dao.insertOrUpdateWeather(entity)
                    }

                    displayWeather(
                        city = entity.cityName,
                        temp = entity.temp,
                        humidity = entity.humidity,
                        desc = entity.description,
                        icon = entity.icon
                    )

                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "请求失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayWeather(city: String, temp: Float, humidity: Int, desc: String, icon: String) {
        binding.cityText.text = city
        binding.tempText.text = "$temp°C"
        binding.humidityText.text = "湿度：$humidity%"
        binding.descText.text = desc
        binding.iconImage.setImageResource(mapIconToDrawable(icon))
    }

    private fun giveClothingSuggestion() {
        val tempStr = binding.tempText.text.toString().replace("°C", "").trim()
        val desc = binding.descText.text.toString().lowercase()
        val humidityStr = binding.humidityText.text.toString().replace("湿度：", "").replace("%", "").trim()

        val temp = tempStr.toFloatOrNull() ?: return
        val humidity = humidityStr.toIntOrNull() ?: 50

        val suggestion = when {
            temp >= 30 -> "天气炎热，建议穿短袖短裤，注意防晒，多喝水 ☀️"
            temp in 20.0..29.9 -> "气温适中，T恤 + 长裤 + 轻薄外套正合适 🌤"
            temp in 10.0..19.9 -> "天气稍凉，建议穿卫衣、外套或风衣 🧥"
            temp in 0.0..9.9 -> "比较冷，建议穿羽绒服、围巾，注意保暖 🧣🧤"
            else -> "严寒天气，穿厚羽绒服、帽子手套全套上 ❄️🧊"

        } + if (desc.contains("雨") || desc.contains("snow")) "，记得带伞或防水外套 ☔️" else ""

        Toast.makeText(this, suggestion, Toast.LENGTH_LONG).show()
    }



    suspend fun getClothingAdviceFromGPT(temp: Float, desc: String, apiKey: String): String {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()

            val prompt = "今天天气${temp}度，天气情况是“${desc}”。请用简洁中文给我提供一段穿衣建议。"

            val requestBody = JSONObject().apply {
                put("model", "gpt-3.5-turbo")
                put("messages", JSONArray().put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                }))
                put("temperature", 0.7)
            }

            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer $apiKey")
                .header("Content-Type", "application/json")
                .post(RequestBody.create("application/json".toMediaType(), requestBody.toString()))
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext "请求失败：${response.code}"

                    val body = response.body?.string() ?: return@withContext "空响应"
                    val json = JSONObject(body)
                    val message = json.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                    message
                }
            } catch (e: Exception) {
                "请求出错：${e.localizedMessage}"
            }
        }
    }

    private fun showAdviceDialog(advice: String) {
        AlertDialog.Builder(this)
            .setTitle("穿衣建议")
            .setMessage(advice)
            .setCancelable(false) // 用户必须点击按钮关闭
            .setPositiveButton("知道了") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}
private fun mapIconToDrawable(iconCode: String): Int {
    return when (iconCode) {
        "01d" -> R.drawable.sunny
        "02d", "02n" -> R.drawable.cloudy
        "03d", "03n" -> R.drawable.cloudy_2
        "04d", "04n" -> R.drawable.cloudy_2
        "09d", "09n" -> R.drawable.rainy
        "10d", "10n" -> R.drawable.rainy
        "11d", "11n" -> R.drawable.storm
        "13d", "13n" -> R.drawable.snowy
        else -> R.drawable.cloudy
    }
}


