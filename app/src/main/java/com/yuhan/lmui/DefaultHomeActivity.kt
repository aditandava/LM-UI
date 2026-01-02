package com.yuhan.lmui

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.yuhan.lmui.databinding.ActivityDefaultHomeBinding

class DefaultHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDefaultHomeBinding

    companion object {
        // This static flag survives until the app process is killed
        var isDismissedForSession = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            statusBarColor = android.graphics.Color.TRANSPARENT
        }

        binding = ActivityDefaultHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBlur()
        setupListeners()
        
        Handler(Looper.getMainLooper()).postDelayed({
            startGlowAnimation()
        }, 2000)
    }

    private fun setupBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.backgroundWallpaper.setRenderEffect(
                RenderEffect.createBlurEffect(30f, 30f, Shader.TileMode.CLAMP)
            )
        }
    }

    private fun setupListeners() {
        binding.btnCancel.setOnClickListener {
            // Set flag so it doesn't pop up again while app is open
            isDismissedForSession = true
            finish()
        }

        binding.btnSet.setOnClickListener {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent(Settings.ACTION_HOME_SETTINGS)
            } else {
                Intent(Settings.ACTION_SETTINGS)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun startGlowAnimation() {
        val glow = ObjectAnimator.ofPropertyValuesHolder(
            binding.appNameText,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.2f),
            PropertyValuesHolder.ofFloat(View.ALPHA, 0.5f)
        ).apply {
            duration = 500
            repeatCount = 3 
            repeatMode = ObjectAnimator.REVERSE
        }
        glow.start()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, android.R.anim.fade_out)
    }
}