package com.yuhan.lmui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuhan.lmui.adapter.AppsAdapter
import com.yuhan.lmui.databinding.ActivitySearchPanelBinding
import com.yuhan.lmui.util.PrefsManager
import kotlinx.coroutines.launch

class SearchPanelActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchPanelBinding
    private lateinit var prefsManager: PrefsManager
    private var allApps = listOf<AppInfo>()
    
    private val viewModel: MainViewModel by viewModels()
    
    private val searchAdapter = AppsAdapter { appInfo, isLongClick, view ->
        if (isLongClick) showAppOptions(appInfo, view) else launchApp(appInfo, view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivitySearchPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = PrefsManager(this)

        setupInsets()
        setupUI()
        setupRecyclerView()
        setupObservers()
        
        // Remove the blur from the entire root since it blurs the text and icons too much
        // Instead, we'll let the semi-transparent background handle the visual overlay
        // If we want a blur behind the panel, we should blur only the background view

        // Show keyboard immediately
        binding.searchEditText.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.apps.collect { apps ->
                    allApps = apps
                    filterApps(binding.searchEditText.text.toString())
                }
            }
        }
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            val bottomPadding = if (imeInsets.bottom > 0) imeInsets.bottom else systemInsets.bottom
            
            binding.root.setPadding(0, systemInsets.top, 0, bottomPadding)
            insets
        }
    }

    private fun setupUI() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                filterApps(query)
                binding.clearSearch.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    val filtered = allApps.filter { it.label.contains(query, ignoreCase = true) }
                    if (filtered.isNotEmpty()) {
                        val intent = packageManager.getLaunchIntentForPackage(filtered[0].packageName)
                        if (intent != null) {
                            startActivity(intent)
                            finish()
                        }
                        return@setOnEditorActionListener true
                    }
                }
                finish()
                true
            } else {
                false
            }
        }

        binding.clearSearch.setOnClickListener {
            binding.searchEditText.setText("")
        }

        binding.root.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        val isVertical = prefsManager.isVerticalListModeEnabled()
        searchAdapter.setVerticalMode(isVertical)
        binding.searchResultsRecycler.layoutManager = if (isVertical) {
            LinearLayoutManager(this)
        } else {
            GridLayoutManager(this, 4)
        }
        binding.searchResultsRecycler.adapter = searchAdapter
    }

    private fun filterApps(query: String) {
        val filtered = if (query.isEmpty()) {
            emptyList()
        } else {
            allApps.filter { it.label.contains(query, ignoreCase = true) }
        }
        searchAdapter.submitAppList(filtered)
    }

    private fun showAppOptions(app: AppInfo, anchorView: View) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_app_options, null)
        val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<ImageView>(R.id.dialogAppIcon).setImageDrawable(app.icon)
        dialogView.findViewById<TextView>(R.id.dialogAppName).text = app.label

        val favs = prefsManager.getFavorites().toMutableSet()
        val isFav = favs.contains(app.packageName)
        val txtFav = dialogView.findViewById<TextView>(R.id.txtFavorite)
        
        txtFav.text = if (isFav) "Remove from Favorites" else "Add to Favorites"

        dialogView.findViewById<View>(R.id.optionFavorite).setOnClickListener {
            toggleFavorite(app, anchorView)
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.optionAppInfo).setOnClickListener {
            dialog.dismiss()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", app.packageName, null)
            }
            startActivity(intent)
        }

        dialogView.findViewById<View>(R.id.optionHide).setOnClickListener {
            dialog.dismiss()
            anchorView.animate()
                .alpha(0f)
                .scaleX(0f)
                .scaleY(0f)
                .setDuration(300)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction {
                    val hidden = prefsManager.getHiddenApps().toMutableSet()
                    hidden.add(app.packageName)
                    prefsManager.saveHiddenApps(hidden)
                    viewModel.loadApps()
                }.start()
        }

        dialogView.findViewById<View>(R.id.optionUninstall).setOnClickListener {
            dialog.dismiss()
            anchorView.animate()
                .alpha(0f)
                .scaleX(1.5f)
                .scaleY(1.5f)
                .setDuration(400)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction {
                    val intent = Intent(Intent.ACTION_DELETE).apply {
                        data = Uri.fromParts("package", app.packageName, null)
                    }
                    startActivity(intent)
                }.start()
        }

        dialog.show()
    }

    private fun toggleFavorite(app: AppInfo, view: View) {
        val favs = prefsManager.getFavorites().toMutableSet()
        val isAdded = !favs.contains(app.packageName)
        
        if (isAdded) favs.add(app.packageName) else favs.remove(app.packageName)
        prefsManager.saveFavorites(favs)
        
        // Pulse animation for visual feedback
        view.animate()
            .scaleX(if (isAdded) 1.15f else 0.85f)
            .scaleY(if (isAdded) 1.15f else 0.85f)
            .setDuration(120)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(120)
                    .start()
            }.start()

        Toast.makeText(this, if (isAdded) "Added to favorites" else "Removed from favorites", Toast.LENGTH_SHORT).show()
        viewModel.loadApps()
    }

    private fun launchApp(app: AppInfo, view: View) {
        val intent = packageManager.getLaunchIntentForPackage(app.packageName)
        if (intent != null) {
            val options = ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0, view.width, view.height)
            startActivity(intent, options.toBundle())
            finish()
        } else {
            Toast.makeText(this, "Cannot open app", Toast.LENGTH_SHORT).show()
        }
    }

    override fun finish() {
        super.finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }
}
