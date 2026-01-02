package com.yuhan.lmui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RenderEffect
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuhan.lmui.adapter.HiddenApp
import com.yuhan.lmui.adapter.HiddenAppsAdapter
import com.yuhan.lmui.databinding.ActivitySettingsBinding
import com.yuhan.lmui.util.PrefsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * SettingsActivity handles all launcher-specific configurations.
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefsManager: PrefsManager
    private lateinit var weatherManager: WeatherManager
    private lateinit var hiddenAppsAdapter: HiddenAppsAdapter

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            saveWallpaperLocally(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefsManager = PrefsManager(this)
        weatherManager = WeatherManager(this)

        // Nuclear Reset: Clear bad data from memory immediately
        val cached = weatherManager.getCachedData()
        if (cached?.locationName == "Join" || cached?.locationName == "Unknown Location") {
            getSharedPreferences("weather_prefs", Context.MODE_PRIVATE).edit().clear().apply()
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInsets()
        setupUI()
        setupHiddenAppsList()

        updateLocationUI(weatherManager.getCachedData()?.locationName)
        updateCurrentFontDisplay()

        applyBlurPreview(prefsManager.getBlurMagnitude())
        applyOpacityPreview(prefsManager.getDrawerOpacity())
        applyFullScreenState()

        refreshLocationData()
    }

    override fun onResume() {
        super.onResume()
        refreshLocationData()
    }

    private fun refreshLocationData() {
        lifecycleScope.launch {
            val lastLoc = weatherManager.getLastLocation()
            if (lastLoc != null) {
                updateLocationUI(String.format(Locale.US, "%.2f, %.2f", lastLoc.first, lastLoc.second))
            } else {
                binding.locationText.text = "Waiting for GPS..."
            }
        }
    }

    private fun updateLocationUI(coords: String?) {
        // Only show GPS coordinates in Settings. Blocks any lingering bad strings.
        if (!coords.isNullOrEmpty() && !coords.contains("Join", true)) {
            binding.locationText.text = "GPS: $coords"
        } else {
            val lastLoc = weatherManager.getLastLocation()
            if (lastLoc != null) {
                binding.locationText.text = String.format(Locale.US, "GPS: %.2f, %.2f", lastLoc.first, lastLoc.second)
            } else {
                binding.locationText.text = "Waiting for GPS..."
            }
        }
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }
    }

    private fun applyFullScreenState() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        if (prefsManager.isFullScreen()) {
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    private fun setupUI() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.connectButton.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:aditandava@gmail.com")
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " Feedback")
                }
                startActivity(Intent.createChooser(intent, "Send Email"))
            } catch (e: Exception) {
                // Ignore
            }
        }

        binding.wallpaperOption.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.systemWallpaperOption.setOnClickListener {
            useSystemWallpaper()
        }

        binding.fontOption.setOnClickListener {
            showFontDialog()
        }

        binding.blurSlider.value = prefsManager.getBlurMagnitude().toFloat()
        binding.blurSlider.addOnChangeListener { _, value, _ ->
            val magnitude = value.toInt()
            prefsManager.setBlurMagnitude(magnitude)
            applyBlurPreview(magnitude)
        }

        binding.opacitySlider.value = prefsManager.getDrawerOpacity().toFloat()
        binding.opacitySlider.addOnChangeListener { _, value, _ ->
            val opacity = value.toInt()
            prefsManager.setDrawerOpacity(opacity)
            applyOpacityPreview(opacity)
        }

        binding.swipeDownSwitch.isChecked = prefsManager.isSwipeDownNotificationsEnabled()
        binding.swipeDownSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefsManager.setSwipeDownNotificationsEnabled(isChecked)
        }

        binding.iconAnimationSwitch.isChecked = prefsManager.isIconAnimationEnabled()
        binding.iconAnimationSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefsManager.setIconAnimationEnabled(isChecked)
        }

        binding.fullScreenSwitch.isChecked = prefsManager.isFullScreen()
        binding.fullScreenSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefsManager.setFullScreen(isChecked)
            applyFullScreenState()
        }

        binding.appDrawerSwitch.isChecked = prefsManager.isAppDrawerEnabled()
        binding.appDrawerSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefsManager.setAppDrawerEnabled(isChecked)
        }

        binding.searchBarSwitch.isChecked = prefsManager.isSearchBarEnabled()
        binding.searchBarSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefsManager.setSearchBarEnabled(isChecked)
        }

        binding.verticalListSwitch.isChecked = prefsManager.isVerticalListModeEnabled()
        binding.verticalListSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefsManager.setVerticalListModeEnabled(isChecked)
        }
    }

    private fun showFontDialog() {
        val fonts = arrayOf(
            getString(R.string.font_inter_regular),
            getString(R.string.font_inter_medium),
            getString(R.string.font_inter_light),
            getString(R.string.font_outfit_light),
            getString(R.string.font_outfit_semibold),
            getString(R.string.font_playfair_display)
        )
        val fontValues = arrayOf(
            "inter_regular", "inter_medium", "inter_light",
            "outfit_light", "outfit_semibold", "playfair_display_italic"
        )

        val currentFont = prefsManager.getAppFont()
        val checkedItem = fontValues.indexOf(currentFont).coerceAtLeast(0)

        AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle(R.string.select_app_font)
            .setSingleChoiceItems(fonts, checkedItem) { dialog, which ->
                prefsManager.setAppFont(fontValues[which])
                updateCurrentFontDisplay()
                dialog.dismiss()
                Toast.makeText(this, R.string.font_updated, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun updateCurrentFontDisplay() {
        val fontResId = when(prefsManager.getAppFont()) {
            "inter_regular" -> R.string.font_inter_regular
            "inter_medium" -> R.string.font_inter_medium
            "inter_light" -> R.string.font_inter_light
            "outfit_light" -> R.string.font_outfit_light
            "outfit_semibold" -> R.string.font_outfit_semibold
            "playfair_display_italic" -> R.string.font_playfair_display
            else -> R.string.font_inter_regular
        }
        binding.currentFontText.text = getString(fontResId)
    }

    private fun applyBlurPreview(magnitude: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val radius = (magnitude.toFloat() * 0.5f).coerceAtLeast(0.1f)
            val blurEffect = RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.CLAMP)
            binding.blurPreviewImage.setRenderEffect(blurEffect)
        } else {
            val alpha = (magnitude.toFloat() / 100f * 0.8f).coerceIn(0f, 0.9f)
            binding.blurPreviewOverlay.alpha = alpha
        }
    }

    private fun applyOpacityPreview(opacity: Int) {
        binding.opacityPreviewOverlay.alpha = opacity.toFloat() / 255f
    }

    private fun setupHiddenAppsList() {
        hiddenAppsAdapter = HiddenAppsAdapter(emptyList()) { packageName ->
            prefsManager.unhideApp(packageName)
            loadHiddenApps()
        }
        binding.hiddenAppsRecycler.layoutManager = LinearLayoutManager(this)
        binding.hiddenAppsRecycler.adapter = hiddenAppsAdapter
        loadHiddenApps()
    }

    private fun loadHiddenApps() {
        val hiddenPackageNames = prefsManager.getHiddenApps()
        if (hiddenPackageNames.isEmpty()) {
            binding.noHiddenAppsText.visibility = View.VISIBLE
            binding.hiddenAppsRecycler.visibility = View.GONE
            hiddenAppsAdapter.updateData(emptyList())
            return
        }

        binding.noHiddenAppsText.visibility = View.GONE
        binding.hiddenAppsRecycler.visibility = View.VISIBLE

        val pm = packageManager
        val hiddenApps = hiddenPackageNames.mapNotNull { pkg ->
            try {
                val info = pm.getApplicationInfo(pkg, 0)
                HiddenApp(pkg, pm.getApplicationLabel(info).toString(), pm.getApplicationIcon(info))
            } catch (e: Exception) {
                null
            }
        }
        hiddenAppsAdapter.updateData(hiddenApps)
    }

    private fun saveWallpaperLocally(uri: Uri) {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE

                val success = withContext(Dispatchers.IO) {
                    val inputStream = contentResolver.openInputStream(uri)
                    val bitmap = inputStream?.use { stream -> BitmapFactory.decodeStream(stream) }
                        ?: return@withContext false

                    val oldWallpaperPath = prefsManager.getWallpaper()
                    if (!oldWallpaperPath.isNullOrEmpty()) {
                        val oldFile = File(oldWallpaperPath)
                        if (oldFile.exists()) oldFile.delete()
                    }

                    val fileName = "launcher_wallpaper_${System.currentTimeMillis()}.png"
                    val file = File(filesDir, fileName)

                    FileOutputStream(file).use { out: FileOutputStream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }

                    prefsManager.saveWallpaper(file.absolutePath)
                    true
                }

                binding.progressBar.visibility = View.GONE
                if (success) {
                    Toast.makeText(this@SettingsActivity, R.string.wallpaper_updated, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@SettingsActivity, R.string.failed_to_update_wallpaper, Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@SettingsActivity, R.string.failed_to_update_wallpaper, Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun useSystemWallpaper() {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE

                withContext(Dispatchers.IO) {
                    val oldWallpaperPath = prefsManager.getWallpaper()
                    if (!oldWallpaperPath.isNullOrEmpty()) {
                        val oldFile = File(oldWallpaperPath)
                        if (oldFile.exists()) oldFile.delete()
                    }
                    prefsManager.saveWallpaper("")
                }

                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@SettingsActivity, R.string.using_system_wallpaper, Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@SettingsActivity, R.string.failed_to_clear_wallpaper, Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }
}
