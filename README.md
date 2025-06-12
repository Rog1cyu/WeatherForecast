# 🌤️ 天气预报 Android 应用

一个基于 OkHttp + Room + GPT 的 Android 天气查询 App，支持城市天气搜索、小时预报展示、历史缓存、以及 AI 穿衣建议推荐。

---

## 🧭 项目功能

1. **城市天气查询**：支持用户输入任意城市名称，实时获取天气数据。
2. **OkHttp 网络模块**：使用 OkHttp 请求 OpenWeatherMap API，并解析 JSON 数据。
3. **天气信息展示**：展示当前温度、湿度、天气描述与图标信息。
4. **小时天气预报**：接入 `/forecast` 接口，展示未来数小时天气趋势。
5. **历史记录缓存**：使用 Room 数据库缓存查询数据，若城市已查询则本地读取。
6. **底部导航栏切换**：支持在 Home / Explore / Profile 等模块切换。
7. **AI 穿衣建议（GPT 模型）**：根据天气自动调用 OpenAI 接口生成穿衣建议，以弹窗形式展示。
8. **图标本地映射**：将天气 API 返回的 icon code 映射为本地 drawable 资源，提升加载速度。
9. **错误处理与提示**：网络失败、无效城市等情况都有提示信息保障用户体验。

---

## 🛠️ 技术栈与设计思路

- `Activity + ViewBinding` 实现主页面交互与界面渲染；
- 使用 `OkHttp` 实现天气接口请求；
- 使用 `Room (KSP)` 实现本地历史缓存；
- 使用 `RecyclerView` 实现小时天气滑动卡片与热门城市卡片展示；
- 使用 `AlertDialog` 弹出 GPT 穿衣建议；
- 使用 `Kotlin Coroutine` 实现异步操作；
- 使用 `ChipNavigationBar` 实现底部菜单切换。

---

## 📦 项目结构说明

```
.
├── app/
│   ├── java/com/uilover/project2172/
│   │   ├── Activity/           # MainActivity.kt
│   │   ├── Adapter/            # HourlyAdapter.kt, OtherCityAdapter.kt
│   │   ├── Api/                # RetrofitClient.kt, WeatherApi.kt
│   │   ├── Model/              # Room实体 + 响应数据类
│   ├── res/
│   │   ├── layout/             # activity_main.xml
│   │   ├── menu/               # bottom_menu.xml
│   │   ├── drawable/           # 图标资源 sunny.png 等
│   └── AndroidManifest.xml
```

---

## 🚀 如何运行

1. 克隆项目到本地：
   ```bash
   git clone https://github.com/yourusername/weather-app.git
   ```

2. 使用 Android Studio 打开项目根目录；

3. 在 `AndroidManifest.xml` 中确保已配置网络权限：

   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   ```

4. 在 `MainActivity.kt` 中配置你的 API Key：
   ```kotlin
   private val apiKey = "your_openweathermap_key"
   ```

5. （可选）如需启用 GPT 穿衣建议，请替换为你的 OpenAI API Key：
   ```kotlin
   getClothingAdviceFromGPT(..., apiKey = "sk-xxxxxxxxxx")
   ```

6. 构建并运行 App 即可使用。

---

## 📌 课程拓展亮点（自由发挥）

- 使用 GPT 实现根据天气智能推荐穿衣建议；
- 使用本地 AlertDialog 弹窗提升用户交互体验；
- 使用本地图标映射，提升离线可用性和界面加载速度。

---

## 🧠 TODO

- [ ] 添加城市收藏与分类管理
- [ ] 天气预报图表可视化（温度变化曲线）
- [ ] 支持定位获取当前城市天气
- [ ] 多语言适配与夜间模式切换

---

