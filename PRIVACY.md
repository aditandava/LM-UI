<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=0D1117,1A1F2E,2C3E50,1A1F2E,0D1117&height=200&section=header&text=Privacy%20Policy&fontSize=70&fontColor=6D8B74&fontAlignY=35&animation=fadeIn" width="100%">

<br/>

# 🔒 Privacy Policy

### LM UI Launcher - Your Privacy, Our Priority

<br/>

![Privacy Grade](https://img.shields.io/badge/Privacy_Grade-A+-00C853?style=for-the-badge&logo=security&logoColor=white&labelColor=0D1117)
![Zero Tracking](https://img.shields.io/badge/Tracking-ZERO-00C853?style=for-the-badge&logo=ghost&logoColor=white&labelColor=0D1117)
![Data Collection](https://img.shields.io/badge/Data_Collection-NONE-00C853?style=for-the-badge&logo=shield&logoColor=white&labelColor=0D1117)
![Last Updated](https://img.shields.io/badge/Last_Updated-January_2025-6D8B74?style=for-the-badge&logo=calendar&logoColor=white&labelColor=0D1117)

<br/>

```ascii
╔═══════════════════════════════════════════════════════════════╗
║  🌿  100% Local  •  🔒  Zero Trackers  •  💚  Open Source   ║
╚═══════════════════════════════════════════════════════════════╝
```

</div>

<br/>

---

## 📜 Our Commitment

**LM UI Launcher** is built on a foundation of **trust, transparency, and user autonomy**. We believe your digital life belongs to you—not advertisers, not data brokers, not us.

> *"Privacy is not something you trade for convenience. It's a fundamental right."*

This document explains exactly what data LM UI collects (spoiler: **nothing**), what permissions we request, and why. No legal jargon, no hidden clauses—just the truth.

<br/>

---

## 🛡️ Privacy at a Glance

<div align="center">

### Security Audit Report

<br/>

| Category | Status | Details |
|----------|--------|---------|
| 📊 **Analytics & Tracking** | ![No](https://img.shields.io/badge/NO-FF0000?style=flat-square&logo=x&logoColor=white) | Zero analytics. No Google Analytics, Firebase, or any tracking SDK. |
| 🎯 **Targeted Advertising** | ![No](https://img.shields.io/badge/NO-FF0000?style=flat-square&logo=x&logoColor=white) | No ads. No ad networks. No marketing partners. Ever. |
| 📧 **Email Collection** | ![No](https://img.shields.io/badge/NO-FF0000?style=flat-square&logo=x&logoColor=white) | We don't ask for your email. We don't have a backend to store it. |
| 👤 **Personal Information** | ![No](https://img.shields.io/badge/NO-FF0000?style=flat-square&logo=x&logoColor=white) | No names, phone numbers, addresses, or identifiers collected. |
| 🌐 **Remote Servers** | ![No](https://img.shields.io/badge/NO-FF0000?style=flat-square&logo=x&logoColor=white) | No user data sent to external servers (except weather API). |
| 📱 **Device Identifiers** | ![No](https://img.shields.io/badge/NO-FF0000?style=flat-square&logo=x&logoColor=white) | No IMEI, Android ID, or advertising ID tracking. |
| 🍪 **Cookies & Session Tracking** | ![No](https://img.shields.io/badge/NO-FF0000?style=flat-square&logo=x&logoColor=white) | LM UI doesn't use cookies or track sessions. |
| 💾 **Local Storage** | ![Yes](https://img.shields.io/badge/YES-00C853?style=flat-square&logo=check&logoColor=white) | All settings stored locally on YOUR device. |
| 🔓 **Open Source Code** | ![Yes](https://img.shields.io/badge/YES-00C853?style=flat-square&logo=check&logoColor=white) | Fully auditable. Review every line on [GitHub](https://github.com/aditandava/LM-UI). |

</div>

<br/>

---

## 📡 What Data LM UI Accesses

LM UI is a launcher. To function, it needs access to certain system features. Here's what we use and **why**:

<br/>

<details>
<summary><h3>🖼️ Wallpaper Access</h3></summary>

<br/>

**Permission:** `android.permission.SET_WALLPAPER`

**Why we need it:**
- To display your current wallpaper as the launcher background
- To apply blur effects when you open the app drawer
- To create a seamless, cohesive home screen experience

**What we DON'T do:**
- ❌ We don't upload your wallpaper anywhere
- ❌ We don't analyze or process your wallpaper content
- ❌ We don't share it with third parties

**Where it's stored:** Your wallpaper stays on your device. LM UI simply reads it from the system to display it.

</details>

<details>
<summary><h3>📱 Installed Apps List</h3></summary>

<br/>

**Permission:** `android.permission.QUERY_ALL_PACKAGES`

**Why we need it:**
- To display all your installed apps in the app drawer
- To let you search, launch, and organize your apps
- To manage favorites and hidden apps

**What we DON'T do:**
- ❌ We don't send your app list to any server
- ❌ We don't track which apps you use or when
- ❌ We don't share this information with anyone

**Where it's stored:** Your app list is loaded from the system PackageManager and cached locally in SharedPreferences. It never leaves your device.

</details>

<details>
<summary><h3>🌤️ Location (Optional)</h3></summary>

<br/>

**Permission:** `android.permission.ACCESS_FINE_LOCATION` & `android.permission.ACCESS_COARSE_LOCATION`

**Why we need it:**
- To fetch weather data for your current location
- To display temperature, humidity, wind speed, UV index, and AQI

**What we DON'T do:**
- ❌ We don't store your location history
- ❌ We don't track your movements
- ❌ We don't share your location with anyone except Open-Meteo API (see below)

**How it works:**
1. You grant location permission (optional)
2. LM UI gets your approximate coordinates
3. Coordinates are sent ONLY to [Open-Meteo API](https://open-meteo.com/) (privacy-friendly, no API key, no tracking)
4. Weather data is returned and cached locally
5. Your location is NOT stored permanently

**Can I disable this?** Yes! Simply deny location permission. The weather widget will be hidden, but all other launcher features work perfectly.

</details>

<details>
<summary><h3>🔔 Notification Panel Access</h3></summary>

<br/>

**Permission:** `android.permission.EXPAND_STATUS_BAR`

**Why we need it:**
- To let you swipe down on the home screen to open the notification panel
- Standard launcher gesture for accessing notifications

**What we DON'T do:**
- ❌ We don't read your notifications
- ❌ We don't access notification content
- ❌ We only trigger the system notification panel to expand

**Where it's stored:** Nowhere. This is a system action with no data involved.

</details>

<details>
<summary><h3>🌐 Internet Access</h3></summary>

<br/>

**Permission:** `android.permission.INTERNET`

**Why we need it:**
- To fetch weather data from Open-Meteo API
- That's it. No other network requests are made.

**What we DON'T do:**
- ❌ No analytics pings
- ❌ No crash reporting to remote servers
- ❌ No version check-ins or telemetry

**Third-Party Services:**
- **Open-Meteo API** ([Privacy Policy](https://open-meteo.com/en/terms)): Free, open-source weather API. No API key required. No user tracking. No data collection.

**Can I audit network requests?** Yes! Use tools like [Charles Proxy](https://www.charlesproxy.com/) or [Wireshark](https://www.wireshark.org/) to monitor all network traffic. You'll see only weather API requests.

</details>

<br/>

---

## 💾 Local Data Storage

LM UI stores the following data **locally on your device** using Android's SharedPreferences:

<div align="center">

| Data Type | Purpose | Storage Location |
|-----------|---------|------------------|
| **Favorite Apps** | Apps you pin to the home screen | `/data/data/com.yuhan.lmui/shared_prefs/` |
| **Hidden Apps** | Apps you choose to hide from the drawer | `/data/data/com.yuhan.lmui/shared_prefs/` |
| **UI Settings** | Full-screen mode, blur intensity, layout preferences | `/data/data/com.yuhan.lmui/shared_prefs/` |
| **Weather Cache** | Last fetched weather data (for offline use) | `/data/data/com.yuhan.lmui/shared_prefs/` |

</div>

**Access:** Only LM UI can access this data. Android's sandboxing prevents other apps from reading it.

**Deletion:** Uninstalling LM UI automatically deletes all stored data. You can also manually clear it via Android Settings > Apps > LM UI > Storage > Clear Data.

<br/>

---

## 🔍 Third-Party Services

LM UI uses **one** external service:

### 🌤️ Open-Meteo Weather API

- **Purpose:** Fetch real-time weather data (temperature, humidity, wind, UV, AQI)
- **Data Sent:** Your approximate location (latitude, longitude)
- **Data Stored by Them:** None. Open-Meteo is privacy-first and doesn't log requests.
- **Privacy Policy:** [https://open-meteo.com/en/terms](https://open-meteo.com/en/terms)

**Why we chose Open-Meteo:**
- ✅ No API key required (no user tracking)
- ✅ Open-source
- ✅ GDPR compliant
- ✅ No ads, no data selling

<br/>

---

## 🛡️ Security Practices

We take security seriously:

- 🔒 **No Network Libraries Beyond Weather:** LM UI doesn't use analytics SDKs, ad networks, or crash reporting tools.
- 🔓 **Open Source:** Every line of code is public. Audit it on [GitHub](https://github.com/aditandava/LM-UI).
- 🔐 **No Backend:** LM UI doesn't have a server. There's nowhere to send your data even if we wanted to.
- 🧱 **Android Sandboxing:** All data is stored in app-private storage, inaccessible to other apps.
- 🔄 **Regular Updates:** Security patches and dependency updates are applied promptly.

<br/>

---

## 🧒 Children's Privacy

LM UI does not knowingly collect data from anyone, including children under 13. Since we collect **zero data**, LM UI is safe for all ages.

<br/>

---

## 🌍 International Users

LM UI is available worldwide. Since we don't collect data, GDPR, CCPA, and other privacy laws don't apply—there's nothing to regulate!

<br/>

---

## 📝 Changes to This Policy

If we ever change our privacy practices (unlikely, since we can't collect less than zero), we'll update this document and notify users via:

- GitHub release notes
- In-app notification (non-intrusive)

**Version History:**
- **v1.0** (January 2025): Initial policy. Zero data collection established.

<br/>

---

## 📧 Contact Us

Have questions about privacy? Want to verify our claims?

<div align="center">

[![GitHub Issues](https://img.shields.io/badge/GitHub-Issues-6D8B74?style=for-the-badge&logo=github&logoColor=white&labelColor=0D1117)](https://github.com/aditandava/LM-UI/issues)
[![GitHub Discussions](https://img.shields.io/badge/GitHub-Discussions-556B2F?style=for-the-badge&logo=github&logoColor=white&labelColor=0D1117)](https://github.com/aditandava/LM-UI/discussions)

<br/>

**Prefer email?** Reach out to the maintainer: [GitHub Profile](https://github.com/aditandava)

</div>

<br/>

---

## ✅ Our Promise

<div align="center">

<br/>

### We Will Never:

<br/>

<table>
  <tr>
    <td align="center" width="25%">
      <br/>
      <img src="https://img.shields.io/badge/❌-Sell_Your_Data-FF0000?style=for-the-badge&labelColor=0D1117" alt="No Sell"/>
      <br/><br/>
    </td>
    <td align="center" width="25%">
      <br/>
      <img src="https://img.shields.io/badge/❌-Track_You-FF0000?style=for-the-badge&labelColor=0D1117" alt="No Track"/>
      <br/><br/>
    </td>
    <td align="center" width="25%">
      <br/>
      <img src="https://img.shields.io/badge/❌-Show_Ads-FF0000?style=for-the-badge&labelColor=0D1117" alt="No Ads"/>
      <br/><br/>
    </td>
    <td align="center" width="25%">
      <br/>
      <img src="https://img.shields.io/badge/❌-Betray_Trust-FF0000?style=for-the-badge&labelColor=0D1117" alt="No Betray"/>
      <br/><br/>
    </td>
  </tr>
</table>

<br/>

### We Promise:

<br/>

<table>
  <tr>
    <td align="center" width="33%">
      <br/>
      <img src="https://img.shields.io/badge/✅-Transparency-00C853?style=for-the-badge&labelColor=0D1117" alt="Transparency"/>
      <br/><br/>
    </td>
    <td align="center" width="33%">
      <br/>
      <img src="https://img.shields.io/badge/✅-Respect-00C853?style=for-the-badge&labelColor=0D1117" alt="Respect"/>
      <br/><br/>
    </td>
    <td align="center" width="33%">
      <br/>
      <img src="https://img.shields.io/badge/✅-Open_Source-00C853?style=for-the-badge&labelColor=0D1117" alt="Open Source"/>
      <br/><br/>
    </td>
  </tr>
</table>

</div>

<br/>

---

<div align="center">

<br/>

```ascii
╔═══════════════════════════════════════════════════════════════╗
║                   Privacy is a Feature™                       ║
║                                                               ║
║  Your data belongs to you. Not us. Not advertisers.          ║
║  Not governments. YOU.                                        ║
╚═══════════════════════════════════════════════════════════════╝
```

<br/>

### 🔒 Built with ❤️ and 🔐 by the Community

<br/>

[![Made in India](https://img.shields.io/badge/Made_with_❤️_in-India-FF9933?style=for-the-badge&labelColor=0D1117&logo=data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMTIiIGN5PSIxMiIgcj0iMTAiIGZpbGw9IiMxMzgwODAiLz4KPGNpcmNsZSBjeD0iMTIiIGN5PSIxMiIgcj0iMyIgZmlsbD0id2hpdGUiLz4KPC9zdmc+)](https://github.com/aditandava)
[![Open Source](https://img.shields.io/badge/Open_Source-Forever-00C853?style=for-the-badge&logo=open-source-initiative&logoColor=white&labelColor=0D1117)](https://github.com/aditandava/LM-UI)
[![License MIT](https://img.shields.io/badge/License-MIT-8B9DA3?style=for-the-badge&logo=balance-scale&logoColor=white&labelColor=0D1117)](LICENSE)

<br/>

<a href="https://github.com/aditandava/LM-UI">
  <img src="https://img.shields.io/badge/🏠_Back_to_Repository-Click_Here-6D8B74?style=for-the-badge&logo=github&logoColor=white&labelColor=0D1117" alt="Back to Repo" height="50"/>
</a>

<br/><br/>

---

<br/>

<p><em>"Privacy isn't about having something to hide. It's about having something to protect."</em></p>

<br/>

**LM UI Launcher** • Zero Trackers • Zero Collection • Zero Compromise

<br/>

<img src="https://komarev.com/ghpvc/?username=aditandava-lmui-privacy&label=Privacy%20Policy%20Views&color=6D8B74&style=for-the-badge&labelColor=0D1117" alt="Views"/>

<br/><br/>

<img src="https://capsule-render.vercel.app/api?type=waving&color=0D1117,1A1F2E,2C3E50,1A1F2E,0D1117&height=120&section=footer" width="100%">

</div>
