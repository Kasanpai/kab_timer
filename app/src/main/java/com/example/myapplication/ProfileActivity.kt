package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private var kab_Count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        kab_Count = (savedInstanceState?.getInt("kab_count")
            ?: intent.getIntExtra("kab_count", -1)) + 1

        findViewById<TextView>(R.id.zucchiniCounter).text =
            getString(R.string.kab_counter, kab_Count)

        val login = getSharedPreferences("user_data", MODE_PRIVATE)
            .getString("login", "")

        findViewById<TextView>(R.id.profileLogin).text =
            getString(R.string.profile_login, login)

        onBackPressedDispatcher.addCallback(this) {
            val nextIntent = Intent(this@ProfileActivity, MenuActivity::class.java)
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