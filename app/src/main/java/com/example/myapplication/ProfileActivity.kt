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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ProfileActivity : AppCompatActivity() {
    private var kab_Count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "ProfileActivity: onCreate")
        kab_Count = (savedInstanceState?.getInt("kab_count")
            ?: intent.getIntExtra("kab_count", -1)) + 1

        val viewer = UserRepository.current(this)
        if (viewer == null) {
            startActivity(Intent(this, HelloActivity::class.java))
            finish()
            return
        }

        val fromAdmin = intent.getBooleanExtra("from_admin", false) && viewer.isAdmin
        val targetLogin = if (fromAdmin) {
            intent.getStringExtra("profile_login") ?: viewer.login
        } else viewer.login

        setContent {
            KabTheme {
                var person by remember {
                    mutableStateOf(UserRepository.getByLogin(this@ProfileActivity, targetLogin))
                }
                BackHandler { returnBack(fromAdmin) }

                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Профиль", fontSize = 28.sp)
                    val user = person
                    if (user == null) {
                        Text("Пользователь не найден")
                    } else {
                        val avatars = intArrayOf(
                            R.drawable.kab, R.drawable.sun, R.drawable.nut
                        )
                        Image(
                            painter = painterResource(
                                avatars[user.avatarIndex.coerceIn(0, 2)]
                            ),
                            contentDescription = "Аватар пользователя",
                            modifier = Modifier.size(130.dp)
                        )
                        Text("Логин: ${user.login}")
                        Text("ФИО: ${user.fullName}")
                        Text("Дата рождения: ${user.birthDate}")
                        Text("Пол: ${user.gender}")
                        Text(if (user.isAdmin) "Администратор" else "Обычный пользователь")

                        if (fromAdmin && !user.isAdmin) {
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    if (UserRepository.makeAdmin(
                                            this@ProfileActivity, user.login
                                        )) {
                                        person = UserRepository.getByLogin(
                                            this@ProfileActivity, user.login
                                        )
                                        Log.d("ProfileActivity", "Назначен администратор: ${user.login}")
                                    }
                                }, modifier = Modifier.fillMaxWidth()
                            ) { Text("Сделать администратором") }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { returnBack(fromAdmin) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Назад") }
                    Text("Выращено кабачков: $kab_Count")
                }
            }
        }
    }

    private fun returnBack(fromAdmin: Boolean) {
        val destination = if (fromAdmin) UsersActivity::class.java else MenuActivity::class.java
        startActivity(Intent(this, destination).apply {
            putExtra("kab_count", kab_Count)
        })
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("kab_count", kab_Count)
        super.onSaveInstanceState(outState)
    }
}
