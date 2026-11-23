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

        setContent {
            KabTheme {
                Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
        DatabaseTasks.execute(applicationContext, {
            val viewer = UserRepository.current(applicationContext)
            val list = if (viewer?.isAdmin == true)
                UserRepository.getAll(applicationContext) else emptyList()
            Pair(viewer, list)
        }) { result ->
            if (!isFinishing && !isDestroyed) {
                val loaded = result.getOrNull()
                if (loaded?.first?.isAdmin != true) returnToMenu()
                else showUsers(loaded.first!!, loaded.second)
            }
        }
    }

    private fun showUsers(viewer: UserRecord, initialUsers: List<UserRecord>) {
        setContent {
            KabTheme {
                var users by remember { mutableStateOf(initialUsers) }
                var deleteLogin by remember { mutableStateOf<String?>(null) }
                var isDeleting by remember { mutableStateOf(false) }
                var delayMs by remember { mutableLongStateOf(DatabaseTasks.delayMs(applicationContext)) }
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
                                            viewer.login,
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
                    Text("Имитация задержки запросов к БД:")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(0L, 2000L, 5000L).forEach { value ->
                            FilterChip(
                                selected = delayMs == value,
                                onClick = {
                                    delayMs = value
                                    DatabaseTasks.setDelay(applicationContext, value)
                                },
                                label = { Text("${value / 1000} с") }
                            )
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
                        text = {
                            Column {
                                Text("Удалить пользователя ${deleteLogin.orEmpty()}?")
                                if (isDeleting) LinearProgressIndicator()
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                val login = deleteLogin
                                if (login != null && !isDeleting) {
                                    isDeleting = true
                                    DatabaseTasks.execute(applicationContext, {
                                        val removed = UserRepository.remove(applicationContext, login)
                                        val fresh = if (removed) UserRepository.getAll(applicationContext)
                                            else emptyList()
                                        Pair(removed, fresh)
                                    }) { result ->
                                        if (!isFinishing && !isDestroyed) {
                                            isDeleting = false
                                            val value = result.getOrNull()
                                            if (value?.first == true) {
                                                users = value.second
                                                Log.d("UsersActivity", "Удалён: $login")
                                            } else {
                                                Toast.makeText(this@UsersActivity,
                                                    "Удаление недоступно", Toast.LENGTH_SHORT).show()
                                            }
                                            deleteLogin = null
                                        }
                                    }
                                }
                            }, enabled = !isDeleting) { Text("Удалить") }
                        },
                        dismissButton = {
                            TextButton(onClick = { deleteLogin = null }, enabled = !isDeleting) {
                                Text("Отмена")
                            }
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
