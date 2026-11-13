package com.example.myapplication

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

class RegistrationActivity : AppCompatActivity() {
    private var kab_Count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "RegistrationActivity: onCreate")
        kab_Count = (savedInstanceState?.getInt("kab_count")
            ?: intent.getIntExtra("kab_count", -1)) + 1

        setContent {
            KabTheme {
                var login by rememberSaveable { mutableStateOf("") }
                var password by rememberSaveable { mutableStateOf("") }
                var repeatPassword by rememberSaveable { mutableStateOf("") }
                var fullName by rememberSaveable { mutableStateOf("") }
                var birthDate by rememberSaveable { mutableStateOf("") }
                var gender by rememberSaveable { mutableStateOf("") }
                var avatarIndex by rememberSaveable { mutableIntStateOf(0) }
                var error by remember { mutableStateOf("") }
                val avatars = intArrayOf(R.drawable.kab, R.drawable.sun, R.drawable.nut)

                BackHandler { returnBack() }
                Column(
                    modifier = Modifier.fillMaxSize()
                        .verticalScroll(rememberScrollState()).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.registration), fontSize = 28.sp)
                    Spacer(Modifier.height(16.dp))
                    Image(
                        painter = painterResource(avatars[avatarIndex]),
                        contentDescription = stringResource(R.string.avatar),
                        modifier = Modifier.size(110.dp).clickable {
                            avatarIndex = (avatarIndex + 1) % avatars.size
                            Log.d("Registration", "Аватар: $avatarIndex")
                        }
                    )
                    Text(stringResource(R.string.avatar_tap_hint))
                    OutlinedTextField(
                        value = login, onValueChange = { login = it },
                        label = { Text(stringResource(R.string.login)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = password, onValueChange = { password = it },
                        label = { Text(stringResource(R.string.password)) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = repeatPassword, onValueChange = { repeatPassword = it },
                        label = { Text(stringResource(R.string.repeat_password)) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = fullName, onValueChange = { fullName = it },
                        label = { Text(stringResource(R.string.full_name)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            val calendar = Calendar.getInstance()
                            DatePickerDialog(
                                this@RegistrationActivity,
                                { _, year, month, day ->
                                    birthDate = "%02d.%02d.%04d".format(day, month + 1, year)
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).apply { datePicker.maxDate = System.currentTimeMillis() }.show()
                        }, modifier = Modifier.fillMaxWidth()
                    ) { Text(birthDate.ifBlank { getString(R.string.birth_date) }) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = gender == "М", onClick = { gender = "М" })
                        Text(stringResource(R.string.male), Modifier.clickable { gender = "М" })
                        Spacer(Modifier.width(24.dp))
                        RadioButton(selected = gender == "Ж", onClick = { gender = "Ж" })
                        Text(stringResource(R.string.female), Modifier.clickable { gender = "Ж" })
                    }
                    if (error.isNotBlank()) {
                        Text(error, color = MaterialTheme.colorScheme.error)
                    }
                    Button(
                        onClick = {
                            when {
                                listOf(login, password, repeatPassword, fullName,
                                    birthDate, gender).any { it.isBlank() } -> {
                                    error = getString(R.string.fill_fields)
                                }
                                password != repeatPassword -> {
                                    error = getString(R.string.password_mismatch)
                                }
                                else -> {
                                    val record = UserRecord(
                                        login.trim(), fullName.trim(), birthDate,
                                        gender, avatarIndex
                                    )
                                    if (UserRepository.register(
                                            this@RegistrationActivity, record, password
                                        )) {
                                        Log.d("Registration", "Создан пользователь ${record.login}")
                                        Toast.makeText(
                                            this@RegistrationActivity,
                                            R.string.registration_success,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        returnBack()
                                    } else {
                                        error = "Этот логин уже используется или БД недоступна"
                                    }
                                }
                            }
                        }, modifier = Modifier.fillMaxWidth()
                    ) { Text(stringResource(R.string.register)) }
                    OutlinedButton(
                        onClick = { returnBack() }, modifier = Modifier.fillMaxWidth()
                    ) { Text(stringResource(R.string.go_back)) }
                    Text(stringResource(R.string.kab_counter, kab_Count))
                }
            }
        }
    }

    private fun returnBack() {
        val fromAdmin = intent.getBooleanExtra("from_users", false)
        val target = if (fromAdmin && UserRepository.current(this)?.isAdmin == true)
            UsersActivity::class.java else HelloActivity::class.java
        startActivity(Intent(this, target).apply { putExtra("kab_count", kab_Count) })
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("kab_count", kab_Count)
        super.onSaveInstanceState(outState)
    }
    override fun onStart() { super.onStart(); Log.d("Lifecycle", "RegistrationActivity: onStart") }
    override fun onResume() { super.onResume(); Log.d("Lifecycle", "RegistrationActivity: onResume") }
    override fun onPause() { super.onPause(); Log.d("Lifecycle", "RegistrationActivity: onPause") }
    override fun onStop() { super.onStop(); Log.d("Lifecycle", "RegistrationActivity: onStop") }
    override fun onRestart() { super.onRestart(); Log.d("Lifecycle", "RegistrationActivity: onRestart") }
    override fun onDestroy() { super.onDestroy(); Log.d("Lifecycle", "RegistrationActivity: onDestroy") }
}
