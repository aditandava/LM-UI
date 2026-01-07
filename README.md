<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=0D1117,1A1F2E,2C3E50,1A1F2E,0D1117&height=300&section=header&text=LM%20UI%20Launcher&fontSize=80&fontColor=6D8B74&fontAlignY=35&desc=Minimalist%20Android%20Launcher%20Experience&descSize=20&descAlignY=55&animation=fadeIn" width="100%">

<br/>

# 🌲 LM UI Launcher 💧
### ✨ *Where Minimalism Meets Fluid Design* ✨

<p align="center">
  <img src="https://readme-typing-svg.demolab.com?font=Fira+Code&weight=500&size=22&duration=3000&pause=1000&color=6D8B74&center=true&vCenter=true&multiline=true&repeat=true&width=900&height=120&lines=Serene+%F0%9F%8C%BF+Minimal+%F0%9F%8C%91+Fluid;Like+Dewdrops+on+Your+Screen+%F0%9F%92%A7;Advanced+Android+Launcher+%E2%9C%A8;Real-time+Weather+%26+Battery+Monitoring" alt="Typing SVG" />
</p>

<p align="center"><em>"A launcher as calm as morning mist, as fluid as flowing water"</em> 💧🌲</p>

<br />

[![GitHub Stars](https://img.shields.io/github/stars/aditandava/LM-UI?style=for-the-badge&color=2D4A2B&logo=github&logoColor=6D8B74&labelColor=0D1117)](https://github.com/aditandava/LM-UI/stargazers)
[![Forks](https://img.shields.io/github/forks/aditandava/LM-UI?style=for-the-badge&color=3D5A40&logo=github&logoColor=6D8B74&labelColor=0D1117)](https://github.com/aditandava/LM-UI/network/members)
[![License](https://img.shields.io/badge/License-MIT-8B9DA3?style=for-the-badge&logo=open-source-initiative&logoColor=white&labelColor=0D1117)](LICENSE)
[![Android](https://img.shields.io/badge/Platform-Android-6D8B74?style=for-the-badge&logo=android&logoColor=white&labelColor=0D1117)](https://www.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-5F7161?style=for-the-badge&logo=kotlin&logoColor=white&labelColor=0D1117)](https://kotlinlang.org)
[![Issues](https://img.shields.io/github/issues/aditandava/LM-UI?style=for-the-badge&color=556B2F&logo=github&logoColor=6D8B74&labelColor=0D1117)](https://github.com/aditandava/LM-UI/issues)

<br/>

```ascii
╔════════════════════════════════════════════════════════════════╗
║  🌿  Minimalist Design  •  ⚡ Fluid Animations  •  💧 Serene  ║
╚════════════════════════════════════════════════════════════════╝
```

<p align="center">
  <img src="https://skillicons.dev/icons?i=androidstudio,kotlin,gradle&theme=dark" alt="Tech Stack" />
</p>

**LM UI Launcher** is a sophisticated, modern Android launcher application that replaces your default home screen with a **minimalist**, **nature-inspired** interface. Like dewdrops glistening on evergreen branches, LM UI brings tranquility and elegance to your Android experience.

<p align="center">
  <a href="#-features">🎯 Features</a> •
  <a href="#-quick-start">🚀 Quick Start</a> •
  <a href="#-architecture">🏗️ Architecture</a> •
  <a href="#-contributing">🤝 Contributing</a>
</p>

<br/>

---

</div>

## 🌿 Philosophy

> *"A launcher should be like nature - effortless, balanced, and deeply calming. Every interaction should feel like a gentle breeze through pine trees."*

LM UI Launcher embraces **minimalism** and **fluid design**, removing clutter and distraction from your home screen. Inspired by the serenity of misty forests and moonlit nights, every animation, gesture, and transition is crafted to provide a **calm**, **responsive** experience.

<br/>

---

## ✨ Features

<table>
  <tr>
    <td align="center" width="33%">
      <br/>
      <h3>🌑 Minimal Design</h3>
      <p>Clean, distraction-free interface with muted tones and gentle shadows. Material Design 3 principles ensure a modern, cohesive look.</p>
    </td>
    <td align="center" width="33%">
      <br/>
      <h3>💧 Fluid Animations</h3>
      <p>Smooth transitions and organic animations. Real-time blur effects when opening the app drawer, creating a sense of depth.</p>
    </td>
    <td align="center" width="33%">
      <br/>
      <h3>🌤️ Real-time Weather</h3>
      <p>Integrated weather system with Open-Meteo API. Displays temperature, humidity, wind, UV, and AQI with inspirational quotes.</p>
    </td>
  </tr>
  <tr>
    <td align="center" width="33%">
      <br/>
      <h3>🔋 Battery Monitoring</h3>
      <p>Live battery status with charging animations. Dynamic scaling effects when plugged in (AC/USB detection).</p>
    </td>
    <td align="center" width="33%">
      <br/>
      <h3>🎨 Customizable</h3>
      <p>Full-screen mode, grid/list layouts, adjustable opacity and blur. Hide apps, manage favorites, and personalize gestures.</p>
    </td>
    <td align="center" width="33%">
      <br/>
      <h3>⚡ Performance</h3>
      <p>Built with Kotlin Coroutines and MVVM architecture. Hardware-accelerated rendering for smooth 60fps+ experience.</p>
    </td>
  </tr>
</table>

<br/>

---

## 📦 Core Components

<details open>
<summary><h3>🏠 Activities & UI</h3></summary>

<br/>

| Component | Description |
|-----------|-------------|
| **MainActivity.kt** | Heart of the launcher. Manages home screen, time/date display, battery monitoring, weather UI, and app drawer with blur effects. |
| **SettingsActivity.kt** | Configuration hub for full-screen mode, layout options, opacity, blur magnitude, and gesture controls. |
| **SearchPanelActivity.kt** | Dedicated search interface with auto-keyboard for quick app access. |
| **DefaultHomeActivity.kt** | Helper to prompt users to set LM UI as default launcher. |

</details>

<details>
<summary><h3>🧠 Architecture & Data</h3></summary>

<br/>

**MVVM Pattern** with Jetpack components:

```
┌─────────────────────────────────────────────────┐
│              MainActivity                       │
│  ┌──────────────────────────────────────────┐  │
│  │  UI Layer (Views, RecyclerViews)         │  │
│  └──────────────────────────────────────────┘  │
│                      ▲                          │
│                      │ StateFlow                │
│                      │                          │
│  ┌──────────────────────────────────────────┐  │
│  │  MainViewModel                           │  │
│  │  • App Loading (PackageManager)          │  │
│  │  • StateFlow (apps, favorites, weather)  │  │
│  │  • Coroutines (Dispatchers.IO)           │  │
│  └──────────────────────────────────────────┘  │
│                      ▲                          │
│                      │                          │
│  ┌──────────────────────────────────────────┐  │
│  │  Data Layer                              │  │
│  │  • PrefsManager (SharedPreferences)      │  │
│  │  • WeatherManager (Retrofit + Gson)      │  │
│  │  • AppInfo Models                        │  │
│  └──────────────────────────────────────────┘  │
└─────────────────────────────────────────────────┘
```

**Key Classes:**
- **MainViewModel.kt**: Manages state, queries PackageManager, exposes StateFlows
- **PrefsManager.kt**: Persistent storage for favorites, hidden apps, UI settings
- **Weather.kt**: Integrates Open-Meteo API with Retrofit, caches data, provides magazine quotes

</details>

<details>
<summary><h3>🎭 Adapters & Animations</h3></summary>

<br/>

- **AppsAdapter.kt** & **FavoritesAdapter.kt**: Efficient RecyclerView rendering with grid/list support
- **ExplodeItemAnimator.kt**: Custom ItemAnimator with visually striking explode/implode effects
- **Gesture Detection**: Swipe up (drawer), swipe down (notifications), long-press (settings)
- **Blur Effects**: Android S+ RenderEffect for real-time wallpaper blur

</details>

<br/>

---

## 🚀 Quick Start

### 📥 Installation

#### Option 1: Download APK
```bash
# Download the latest release
https://github.com/aditandava/LM-UI/releases/latest

# Install on your Android device (Android 8.0+)
adb install lm-ui.apk
```

#### Option 2: Build from Source
```bash
# Clone the repository
git clone https://github.com/aditandava/LM-UI.git
cd LM-UI

# Open in Android Studio
# File > Open > Select LM-UI folder

# Build and run
./gradlew assembleDebug
```

<br/>

### ⚙️ Setup

1. **Install LM UI Launcher** on your Android device
2. **Press Home button** → Select "LM UI" → Tap "Always"
3. **Grant permissions**: Location (weather), Wallpaper access
4. **Customize**: Long-press home screen → Settings → Configure to your liking

<br/>

---

## 🎨 Customization

<table>
  <tr>
    <th>Feature</th>
    <th>Options</th>
  </tr>
  <tr>
    <td><strong>🖼️ Display</strong></td>
    <td>Full-screen mode, System bar visibility</td>
  </tr>
  <tr>
    <td><strong>🔍 Search</strong></td>
    <td>Toggle search bar on home screen</td>
  </tr>
  <tr>
    <td><strong>📱 App Drawer</strong></td>
    <td>Grid layout or Vertical list</td>
  </tr>
  <tr>
    <td><strong>💫 Effects</strong></td>
    <td>Drawer opacity (0-100%), Blur intensity, Icon animations</td>
  </tr>
  <tr>
    <td><strong>🎯 Gestures</strong></td>
    <td>Swipe down for notifications, Long-press for settings</td>
  </tr>
  <tr>
    <td><strong>📌 Apps</strong></td>
    <td>Add to favorites, Hide apps, App info, Uninstall</td>
  </tr>
</table>

<br/>

---

## 🛠️ Technical Stack

<div align="center">

| Technology | Purpose |
|------------|---------|
| <img src="https://img.shields.io/badge/Kotlin-5F7161?style=for-the-badge&logo=kotlin&logoColor=white&labelColor=0D1117"/> | Primary language for app logic |
| <img src="https://img.shields.io/badge/Jetpack-6D8B74?style=for-the-badge&logo=android&logoColor=white&labelColor=0D1117"/> | ViewModel, LiveData, Coroutines |
| <img src="https://img.shields.io/badge/Material_Design_3-556B2F?style=for-the-badge&logo=material-design&logoColor=white&labelColor=0D1117"/> | UI components and theming |
| <img src="https://img.shields.io/badge/Retrofit-8B9DA3?style=for-the-badge&logo=square&logoColor=white&labelColor=0D1117"/> | Weather API integration |
| <img src="https://img.shields.io/badge/Coroutines-3D5A40?style=for-the-badge&logo=kotlin&logoColor=white&labelColor=0D1117"/> | Async/background operations |
| <img src="https://img.shields.io/badge/RenderEffect-2D4A2B?style=for-the-badge&logo=android&logoColor=white&labelColor=0D1117"/> | Real-time blur effects (API 31+) |

</div>

<br/>

---

## 📋 Permissions

The launcher requires the following permissions:

```xml
<!-- Required for listing all installed apps -->
<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />

<!-- Wallpaper management -->
<uses-permission android:name="android.permission.SET_WALLPAPER" />

<!-- Weather integration -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />

<!-- Notification panel gesture -->
<uses-permission android:name="android.permission.EXPAND_STATUS_BAR" />
```

<br/>

---

## 🏗️ Architecture

### Technical Logic Flow

```
1. Startup
   └─> MainActivity initializes PrefsManager, WeatherManager, MainViewModel

2. Insets Handling
   └─> WindowInsetsControllerCompat manages edge-to-edge display

3. Data Fetching
   ├─> MainViewModel loads apps (background thread)
   └─> WeatherManager fetches weather (if permissions granted)

4. UI Binding
   ├─> Data bound to RecyclerViews via adapters
   └─> RenderEffect prepared for wallpaper blur

5. Interaction
   ├─> Gestures trigger BottomSheet state changes
   ├─> Taps launch apps
   └─> Long-presses open management dialogs
```

### Performance Optimizations

- ✅ **Large Heap**: Enabled for handling many app icons
- ✅ **Hardware Acceleration**: Full utilization for smooth animations
- ✅ **Kotlin Coroutines**: Prevents ANRs with efficient thread management
- ✅ **Lazy Loading**: Asynchronous app icon and weather data handling
- ✅ **State Caching**: Weather data persisted for offline availability

<br/>

---

## 🤝 Contributing

We welcome contributions! LM UI Launcher thrives on collaborative development 🌱

### How to Contribute

```bash
# Fork the repository
# Clone your fork
git clone https://github.com/YOUR_USERNAME/LM-UI.git

# Create a feature branch
git checkout -b feature/awesome-enhancement

# Make your changes
# Test thoroughly on multiple devices

# Commit with clear messages
git commit -m "feat: add swipe gesture customization"

# Push to your fork
git push origin feature/awesome-enhancement

# Open a Pull Request
```

### Development Guidelines

- 🎯 **Keep it minimal**: Follow the serene design philosophy
- ✅ **Test on real devices**: Ensure smooth performance
- 📝 **Document your code**: Help others understand your changes
- 🙏 **Be respectful**: Foster a welcoming community

### Report Issues

<div align="center">

<a href="https://github.com/aditandava/LM-UI/issues/new?labels=bug&template=bug_report.md&title=%5BBUG%5D+">
  <img src="https://img.shields.io/badge/🐛_Report_Bug-Critical_Issue-ff4444?style=for-the-badge&labelColor=0D1117" alt="Report Bug"/>
</a>
<a href="https://github.com/aditandava/LM-UI/issues/new?labels=enhancement&template=feature_request.md&title=%5BFEATURE%5D+">
  <img src="https://img.shields.io/badge/💡_Request_Feature-New_Enhancement-44ff44?style=for-the-badge&labelColor=0D1117" alt="Request Feature"/>
</a>
<a href="https://github.com/aditandava/LM-UI/issues">
  <img src="https://img.shields.io/badge/📋_View_All_Issues-Browse_Issues-4444ff?style=for-the-badge&labelColor=0D1117" alt="View Issues"/>
</a>

</div>

<br/>

---

## 📄 License

<div align="center">

[![License: MIT](https://img.shields.io/badge/License-MIT-8B9DA3?style=for-the-badge&logo=open-source-initiative&logoColor=white&labelColor=0D1117)](LICENSE)

**MIT © [aditandava](https://github.com/aditandava)**

</div>

<br/>

---

<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=0D1117,1A1F2E,2C3E50,1A1F2E,0D1117&height=150&section=footer" width="100%">

<br/>

### 🌙 Made with Tranquility by [@aditandava](https://github.com/aditandava)

<br/>

<p><em>"Let your launcher breathe like dewdrops on evergreen branches"</em> 💧🌲</p>

⭐ **Star us on GitHub** — Your support brings calm to our journey

<br/>

<a href="https://github.com/aditandava">
  <img src="https://img.shields.io/badge/GitHub-aditandava-5F7161?style=for-the-badge&logo=github&logoColor=6D8B74&labelColor=0D1117" alt="GitHub"/>
</a>

<br/><br/>

### 🌿 Connect & Explore

<p align="center">
  <a href="https://github.com/aditandava/LM-UI">📦 Repository</a> •
  <a href="https://github.com/aditandava/LM-UI/issues">🐛 Issues</a> •
  <a href="https://github.com/aditandava/LM-UI/releases">📥 Releases</a>
</p>

<br/>

```ascii
╔═══════════════════════════════════════════════════════════════════╗
║  Package: com.yuhan.lmui  •  Min SDK: 26  •  Target SDK: 34     ║
║  Architecture: MVVM  •  Language: Kotlin  •  License: MIT        ║
╚═══════════════════════════════════════════════════════════════════╝
```

<br/>

<p align="center">
  <img src="https://komarev.com/ghpvc/?username=aditandava&label=Profile%20Views&color=6D8B74&style=for-the-badge&labelColor=0D1117" alt="Profile Views"/>
  <img src="https://img.shields.io/github/stars/aditandava/LM-UI?style=for-the-badge&logo=github&logoColor=6D8B74&labelColor=0D1117&color=2D4A2B" alt="Stars"/>
</p>

</div>
