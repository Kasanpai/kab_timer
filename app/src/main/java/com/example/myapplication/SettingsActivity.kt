package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Switch
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    private var kab_Count = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        kab_Count = (savedInstanceState?.getInt("kab_count")
            ?: intent.getIntExtra("kab_count", -1)) + 1

        findViewById<TextView>(R.id.zucchiniCounter).text =
            getString(R.string.kab_counter, kab_Count)

        val themeSwitch = findViewById<Switch>(R.id.themeSwitch)

        val themePrefs = getSharedPreferences("settings", MODE_PRIVATE)
        themeSwitch.isChecked = themePrefs.getBoolean("dark_mode", false)

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            themePrefs.edit().putBoolean("dark_mode", isChecked).apply()
            Log.d("SettingsActivity", "Тёмная тема: $isChecked")
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }

        onBackPressedDispatcher.addCallback(this) {
            val nextIntent = Intent(this@SettingsActivity, MenuActivity::class.java)
            nextIntent.putExtra("kab_count", kab_Count)
            startActivity(nextIntent)
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("kab_count", kab_Count)
        super.onSaveInstanceState(outState)
    }
}