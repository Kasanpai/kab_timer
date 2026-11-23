package com.example.myapplication

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MenuActivity : AppCompatActivity() {
    private var kab_Count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "MenuActivity: onCreate")
        kab_Count = (savedInstanceState?.getInt("kab_count")
            ?: intent.getIntExtra("kab_count", -1)) + 1

        setContent {
            KabTheme {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
        DatabaseTasks.execute(applicationContext, {
            UserRepository.current(applicationContext)
        }) { result ->
            if (!isFinishing && !isDestroyed) {
                val user = result.getOrNull()
                if (user == null) openActivity(HelloActivity::class.java)
                else showMenu(user)
            }
        }
    }

    private fun showMenu(signedIn: UserRecord) {
        setContent {
            KabTheme {
                BackHandler { signOut() }
                Column(
                    modifier = Modifier.fillMaxSize()
                        .verticalScroll(rememberScrollState()).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(R.string.app_title), fontSize = 28.sp)
                    Text("Вы вошли как: ${signedIn.login}")
                    if (signedIn.isAdmin) Text("Администратор")
                    Spacer(Modifier.height(20.dp))
                    MenuButton(stringResource(R.string.start)) {
                        Log.d("MenuActivity", "Нажата кнопка Старт")
                        Toast.makeText(this@MenuActivity, "Старт", Toast.LENGTH_SHORT).show()
                    }
                    MenuButton(stringResource(R.string.profile)) {
                        openActivity(ProfileActivity::class.java)
                    }
                    if (signedIn.isAdmin) {
                        MenuButton("Администрирование") {
                            openActivity(UsersActivity::class.java)
                        }
                    }
                    MenuButton(stringResource(R.string.settings)) {
                        openActivity(SettingsActivity::class.java)
                    }
                    MenuButton(stringResource(R.string.report_problem)) {
                        try {
                            startActivity(Intent(
                                Intent.ACTION_DIAL, Uri.parse("tel:+70000000000")
                            ))
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(
                                this@MenuActivity, R.string.no_dialer,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    MenuButton(stringResource(R.string.exit)) { signOut() }
                    Spacer(Modifier.height(12.dp))
                    Text(stringResource(R.string.kab_counter, kab_Count))
                }
            }
        }
    }

    private fun signOut() {
        UserRepository.signOut(this)
        openActivity(HelloActivity::class.java)
    }

    private fun openActivity(target: Class<out Activity>) {
        startActivity(Intent(this, target).apply { putExtra("kab_count", kab_Count) })
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("kab_count", kab_Count)
        super.onSaveInstanceState(outState)
    }
    override fun onStart() { super.onStart(); Log.d("Lifecycle", "MenuActivity: onStart") }
    override fun onResume() { super.onResume(); Log.d("Lifecycle", "MenuActivity: onResume") }
    override fun onPause() { super.onPause(); Log.d("Lifecycle", "MenuActivity: onPause") }
    override fun onStop() { super.onStop(); Log.d("Lifecycle", "MenuActivity: onStop") }
    override fun onRestart() { super.onRestart(); Log.d("Lifecycle", "MenuActivity: onRestart") }
    override fun onDestroy() { super.onDestroy(); Log.d("Lifecycle", "MenuActivity: onDestroy") }
}

@Composable
private fun MenuButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(text) }
}
