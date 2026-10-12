package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HelloActivity : AppCompatActivity() {

    private var kab_Count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "HelloActivity: onCreate")

        setContentView(R.layout.activity_helloact)

        kab_Count = (savedInstanceState?.getInt("kab_count")
            ?: intent.getIntExtra("kab_count", -1)) + 1

        findViewById<TextView>(R.id.zucchiniCounter).text =
            getString(R.string.kab_counter, kab_Count)

        findViewById<Button>(R.id.loginButton).setOnClickListener {
            val nextIntent = Intent(this, MenuActivity::class.java)
            nextIntent.putExtra("kab_count", kab_Count)
            startActivity(nextIntent)
            finish()
        }

        findViewById<Button>(R.id.registrationButton).setOnClickListener {
            val nextIntent = Intent(this, RegistrationActivity::class.java)
            nextIntent.putExtra("kab_count", kab_Count)
            startActivity(nextIntent)
            finish()
        }

        findViewById<Button>(R.id.exitButton).setOnClickListener {
            finishAffinity()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("kab_count", kab_Count)
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "HelloActivity: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "HelloActivity: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "HelloActivity: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle", "HelloActivity: onStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("Lifecycle", "HelloActivity: onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "HelloActivity: onDestroy")
    }
}