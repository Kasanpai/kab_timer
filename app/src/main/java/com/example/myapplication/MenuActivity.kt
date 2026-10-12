package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    private var kab_Count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        kab_Count = (savedInstanceState?.getInt("kab_count")
            ?: intent.getIntExtra("kab_count", -1)) + 1

        findViewById<TextView>(R.id.zucchiniCounter).text =
            getString(R.string.kab_counter, kab_Count)

        findViewById<Button>(R.id.startButton).setOnClickListener {
            Toast.makeText(this, "Старт", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.profileButton).setOnClickListener {
            openActivity(ProfileActivity::class.java)
        }

        findViewById<Button>(R.id.settingsButton).setOnClickListener {
            openActivity(SettingsActivity::class.java)
        }

        findViewById<Button>(R.id.problemButton).setOnClickListener {
            val dialIntent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:+70000000000")
            )

            if (dialIntent.resolveActivity(packageManager) != null) {
                startActivity(dialIntent)
            } else {
                Toast.makeText(this, R.string.no_dialer, Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.exitButton).setOnClickListener {
            openActivity(HelloActivity::class.java)
        }

        onBackPressedDispatcher.addCallback(this) {
            openActivity(HelloActivity::class.java)
        }
    }

    private fun openActivity(target: Class<out android.app.Activity>) {
        val nextIntent = Intent(this, target)
        nextIntent.putExtra("kab_count", kab_Count)
        startActivity(nextIntent)
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("kab_count", kab_Count)
        super.onSaveInstanceState(outState)
    }
}