package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity

class RegistrationActivity : AppCompatActivity() {

    private var kab_Count = 0

    private lateinit var loginEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var repeatPasswordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "RegistrationActivity: onCreate")

        setContentView(R.layout.activity_registration)

        kab_Count = (savedInstanceState?.getInt("kab_count")
            ?: intent.getIntExtra("kab_count", -1)) + 1

        findViewById<TextView>(R.id.zucchiniCounter).text =
            getString(R.string.kab_counter, kab_Count)

        loginEditText = findViewById(R.id.regLoginEditText)
        passwordEditText = findViewById(R.id.regPasswordEditText)
        repeatPasswordEditText = findViewById(R.id.regRepeatPasswordEditText)

        if (savedInstanceState != null) {
            loginEditText.setText(savedInstanceState.getString("login"))
            passwordEditText.setText(savedInstanceState.getString("password"))
            repeatPasswordEditText.setText(savedInstanceState.getString("repeat_password"))
        }

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            val login = loginEditText.text.toString()
            val password = passwordEditText.text.toString()
            val repeatPassword = repeatPasswordEditText.text.toString()

            when {
                login.isBlank() || password.isBlank() || repeatPassword.isBlank() -> {
                    Toast.makeText(this, R.string.fill_fields, Toast.LENGTH_SHORT).show()
                }

                password != repeatPassword -> {
                    Toast.makeText(this, R.string.password_mismatch, Toast.LENGTH_SHORT).show()
                }

                else -> {
                    getSharedPreferences("user_data", MODE_PRIVATE)
                        .edit()
                        .putString("login", login)
                        .apply()

                    Toast.makeText(
                        this,
                        R.string.registration_success,
                        Toast.LENGTH_SHORT
                    ).show()

                    returnToHello()
                }
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            returnToHello()
        }
    }

    private fun returnToHello() {
        val nextIntent = Intent(this, HelloActivity::class.java)
        nextIntent.putExtra("kab_count", kab_Count)
        startActivity(nextIntent)
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("kab_count", kab_Count)
        outState.putString("login", loginEditText.text.toString())
        outState.putString("password", passwordEditText.text.toString())
        outState.putString("repeat_password", repeatPasswordEditText.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "RegistrationActivity: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "RegistrationActivity: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "RegistrationActivity: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle", "RegistrationActivity: onStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("Lifecycle", "RegistrationActivity: onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "RegistrationActivity: onDestroy")
    }
}