package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class SettingsActivity : AppCompatActivity() {

    private var kab_Count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "SettingsActivity: onCreate")

        kab_Count = (savedInstanceState?.getInt("kab_count")
            ?: intent.getIntExtra("kab_count", -1)) + 1

        val prefs = getSharedPreferences("settings", MODE_PRIVATE)

        setContent {
            var darkMode by remember {
                mutableStateOf(prefs.getBoolean("dark_mode", false))
            }

            KabTheme(darkMode = darkMode) {
                BackHandler { returnToMenu() }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.settings),
                        fontSize = 28.sp
                    )

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.day_night))

                        Switch(
                            checked = darkMode,
                            onCheckedChange = {
                                darkMode = it

                                prefs.edit()
                                    .putBoolean("dark_mode", it)
                                    .apply()

                                Log.d("SettingsActivity", "Тёмная тема: $it")
                            }
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = { returnToMenu() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.go_back))
                    }

                    Text(stringResource(R.string.kab_counter, kab_Count))
                }
            }
        }
    }

    private fun returnToMenu() {
        startActivity(Intent(this, MenuActivity::class.java).apply {
            putExtra("kab_count", kab_Count)
        })
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("kab_count", kab_Count)
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "SettingsActivity: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "SettingsActivity: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "SettingsActivity: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle", "SettingsActivity: onStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("Lifecycle", "SettingsActivity: onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "SettingsActivity: onDestroy")
    }
}
