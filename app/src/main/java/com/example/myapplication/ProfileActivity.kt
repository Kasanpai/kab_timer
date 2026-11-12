
package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ProfileActivity : AppCompatActivity() {

    private var kab_Count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "ProfileActivity: onCreate")

        kab_Count = (savedInstanceState?.getInt("kab_count")
            ?: intent.getIntExtra("kab_count", -1)) + 1

        val prefs = getSharedPreferences("user_data", MODE_PRIVATE)

        val login = prefs.getString("login", "").orEmpty()
        val fullName = prefs.getString("full_name", "").orEmpty()
        val birthDate = prefs.getString("birth_date", "").orEmpty()
        val gender = prefs.getString("gender", "").orEmpty()

        val avatars = intArrayOf(
            R.drawable.kab,
            R.drawable.sun,
            R.drawable.nut
        )

        val index = prefs.getInt("avatar_index", 0).coerceIn(0, 2)

        setContent {
            KabTheme {
                BackHandler { returnToMenu() }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.profile),
                        fontSize = 28.sp
                    )

                    Image(
                        painter = painterResource(avatars[index]),
                        contentDescription = stringResource(R.string.avatar),
                        modifier = Modifier.size(130.dp)
                    )

                    Text(stringResource(R.string.profile_login, login))
                    Text(stringResource(R.string.profile_name_value, fullName))
                    Text(stringResource(R.string.profile_birth_value, birthDate))
                    Text(stringResource(R.string.profile_gender_value, gender))

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
        Log.d("Lifecycle", "ProfileActivity: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "ProfileActivity: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "ProfileActivity: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle", "ProfileActivity: onStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("Lifecycle", "ProfileActivity: onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "ProfileActivity: onDestroy")
    }
}
