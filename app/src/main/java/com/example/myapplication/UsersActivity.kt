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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class UsersActivity : AppCompatActivity() {

    private var kab_Count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "UsersActivity: onCreate")

        kab_Count = (savedInstanceState?.getInt("kab_count")
            ?: intent.getIntExtra("kab_count", -1)) + 1

        setContent {
            KabTheme {
                var users by remember {
                    mutableStateOf(UserRepository.getAll(this@UsersActivity))
                }

                var selectedLogin by rememberSaveable {
                    mutableStateOf<String?>(null)
                }

                var confirmDelete by remember {
                    mutableStateOf(false)
                }

                BackHandler {
                    returnToMenu()
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.users_title),
                        fontSize = 28.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(users, key = { it.login }) { user ->
                            val selected = selectedLogin == user.login

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedLogin = user.login
                                        Log.d("UsersActivity", "Выбран: ${user.login}")
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selected)
                                        MaterialTheme.colorScheme.secondaryContainer
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(user.fullName)
                                    Text("Логин: ${user.login}")
                                }
                            }
                        }
                    }

                    Text(
                        text = selectedLogin?.let {
                            getString(R.string.selected_user, it)
                        } ?: stringResource(R.string.select_user_to_delete)
                    )

                    Button(
                        onClick = {
                            Log.d("UsersActivity", "Открыта регистрация")

                            startActivity(
                                Intent(
                                    this@UsersActivity,
                                    RegistrationActivity::class.java
                                ).apply {
                                    putExtra("from_users", true)
                                    putExtra("kab_count", kab_Count)
                                }
                            )
                            finish()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.add_user))
                    }

                    Button(
                        onClick = { confirmDelete = true },
                        enabled = selectedLogin != null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.delete_user))
                    }

                    OutlinedButton(
                        onClick = { returnToMenu() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.go_back))
                    }

                    Text(
                        text = stringResource(R.string.kab_counter, kab_Count)
                    )
                }

                if (confirmDelete) {
                    AlertDialog(
                        onDismissRequest = { confirmDelete = false },
                        title = {
                            Text(stringResource(R.string.delete_user))
                        },
                        text = {
                            Text(
                                selectedLogin?.let {
                                    "Удалить пользователя $it?"
                                } ?: ""
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val login = selectedLogin

                                    if (login != null &&
                                        UserRepository.remove(this@UsersActivity, login)
                                    ) {
                                        users = UserRepository.getAll(this@UsersActivity)
                                        selectedLogin = null

                                        Log.d("UsersActivity", "Удалён: $login")

                                        Toast.makeText(
                                            this@UsersActivity,
                                            R.string.user_deleted,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    confirmDelete = false
                                }
                            ) {
                                Text(stringResource(R.string.delete_user))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { confirmDelete = false }) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    )
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
        Log.d("Lifecycle", "UsersActivity: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "UsersActivity: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "UsersActivity: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle", "UsersActivity: onStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("Lifecycle", "UsersActivity: onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "UsersActivity: onDestroy")
    }
}
