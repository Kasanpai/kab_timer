package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Экран «Администрирование» на основе UsersActivity из ЛР №3–4.
class UsersActivity : AppCompatActivity() {
    private var kab_Count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "UsersActivity: onCreate")
        kab_Count = (savedInstanceState?.getInt("kab_count")
            ?: intent.getIntExtra("kab_count", -1)) + 1

        if (UserRepository.current(this)?.isAdmin != true) {
            returnToMenu()
            return
        }

        setContent {
            KabTheme {
                var users by remember {
                    mutableStateOf(UserRepository.getAll(this@UsersActivity))
                }
                var deleteLogin by remember { mutableStateOf<String?>(null) }
                BackHandler { returnToMenu() }

                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text("Администрирование", fontSize = 28.sp)
                    Text("Зарегистрированные пользователи: ${users.size}")
                    Spacer(Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(users, key = { it.login }) { user ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f)
                                            .clickable { openProfile(user.login) }
                                    ) {
                                        Text(user.fullName)
                                        Text("Логин: ${user.login}")
                                        Text(
                                            if (user.isAdmin) "Администратор"
                                            else "Пользователь"
                                        )
                                        Text("Нажмите для просмотра профиля")
                                    }
                                    if (!user.login.equals(
                                            UserRepository.current(this@UsersActivity)?.login,
                                            ignoreCase = true
                                        )) {
                                        TextButton(onClick = { deleteLogin = user.login }) {
                                            Text("Удалить")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Button(
                        onClick = {
                            startActivity(Intent(
                                this@UsersActivity, RegistrationActivity::class.java
                            ).apply {
                                putExtra("from_users", true)
                                putExtra("kab_count", kab_Count)
                            })
                            finish()
                        }, modifier = Modifier.fillMaxWidth()
                    ) { Text("Добавить пользователя") }
                    OutlinedButton(
                        onClick = { returnToMenu() },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Назад") }
                    Text("Выращено кабачков: $kab_Count")
                }

                if (deleteLogin != null) {
                    AlertDialog(
                        onDismissRequest = { deleteLogin = null },
                        title = { Text("Удаление пользователя") },
                        text = { Text("Удалить пользователя ${deleteLogin.orEmpty()}?") },
                        confirmButton = {
                            TextButton(onClick = {
                                val login = deleteLogin
                                if (login != null) {
                                    val removed = UserRepository.remove(
                                        this@UsersActivity, login
                                    )
                                    if (removed) {
                                        users = UserRepository.getAll(this@UsersActivity)
                                        Log.d("UsersActivity", "Удалён: $login")
                                    } else {
                                        Toast.makeText(
                                            this@UsersActivity,
                                            "Удаление недоступно",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                deleteLogin = null
                            }) { Text("Удалить") }
                        },
                        dismissButton = {
                            TextButton(onClick = { deleteLogin = null }) { Text("Отмена") }
                        }
                    )
                }
            }
        }
    }

    private fun openProfile(login: String) {
        startActivity(Intent(this, ProfileActivity::class.java).apply {
            putExtra("profile_login", login)
            putExtra("from_admin", true)
            putExtra("kab_count", kab_Count)
        })
        finish()
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
}
