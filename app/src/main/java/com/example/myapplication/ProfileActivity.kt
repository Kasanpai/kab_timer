package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView
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

        val prefs = getSharedPreferences("user_data", MODE_PRIVATE)
        val login = prefs.getString("login", "").orEmpty()
        val fullName = prefs.getString("full_name", "").orEmpty()
        val birthDate = prefs.getString("birth_date", "").orEmpty()
        val gender = prefs.getString("gender", "").orEmpty()
        val avatarIndex = prefs.getInt("avatar_index", 0).coerceIn(0, 2)
        val avatars = intArrayOf(R.drawable.kab, R.drawable.sun, R.drawable.nut)

        findViewById<TextView>(R.id.profileLogin).text =
            getString(R.string.profile_login, login)
        findViewById<TextView>(R.id.profileFullName).text =
            getString(R.string.profile_name_value, fullName)
        findViewById<TextView>(R.id.profileBirthDate).text =
            getString(R.string.profile_birth_value, birthDate)
        findViewById<TextView>(R.id.profileGender).text =
            getString(R.string.profile_gender_value, gender)
        findViewById<ImageView>(R.id.profileAvatar).setImageResource(avatars[avatarIndex])

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