
package com.example.myapplication

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class RegistrationActivity : AppCompatActivity() {

    private var kab_Count = 0
    private var avatarIndex = 0

    private val avatars = intArrayOf(
        R.drawable.kab,
        R.drawable.sun,
        R.drawable.nut
    )

    private lateinit var loginEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var repeatPasswordEditText: EditText
    private lateinit var fullNameEditText: EditText
    private lateinit var birthDateEditText: EditText
    private lateinit var genderGroup: RadioGroup

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
        fullNameEditText = findViewById(R.id.regFullName)
        birthDateEditText = findViewById(R.id.regBirthDate)
        genderGroup = findViewById(R.id.regGenderGroup)

        if (savedInstanceState != null) {
            loginEditText.setText(savedInstanceState.getString("login"))
            passwordEditText.setText(savedInstanceState.getString("password"))
            repeatPasswordEditText.setText(savedInstanceState.getString("repeat_password"))
            fullNameEditText.setText(savedInstanceState.getString("full_name"))
            birthDateEditText.setText(savedInstanceState.getString("birth_date"))

            avatarIndex = savedInstanceState.getInt("avatar_index", 0)
            findViewById<ImageView>(R.id.regAvatar)
                .setImageResource(avatars[avatarIndex])



            val genderId = savedInstanceState.getInt("gender_id", -1)
            if (genderId != -1) {
                genderGroup.check(genderId)
            }
        }

        birthDateEditText.setOnClickListener {
            Log.d("Registration", "Открыт календарь выбора даты рождения")

            val calendar = Calendar.getInstance()

            val datePicker = DatePickerDialog(
                this,
                { _, year, month, day ->
                    val date = "%02d.%02d.%04d".format(
                        day,
                        month + 1,
                        year
                    )

                    birthDateEditText.setText(date)
                    Log.d("Registration", "Выбрана дата рождения: $date")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePicker.datePicker.maxDate = System.currentTimeMillis()
            datePicker.show()
        }

        genderGroup.setOnCheckedChangeListener { _, checkedId ->
            val gender = when (checkedId) {
                R.id.regMale -> getString(R.string.male)
                R.id.regFemale -> getString(R.string.female)
                else -> ""
            }

            Log.d("Registration", "Выбран пол: $gender")
        }

        val avatarImage = findViewById<ImageView>(R.id.regAvatar)

        avatarImage.setOnClickListener {
            avatarIndex = (avatarIndex + 1) % avatars.size
            avatarImage.setImageResource(avatars[avatarIndex])

            Log.d("Registration", "Выбран аватар: $avatarIndex")
        }

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            Log.d("Registration", "Нажата кнопка регистрации")

            val login = loginEditText.text.toString()
            val password = passwordEditText.text.toString()
            val repeatPassword = repeatPasswordEditText.text.toString()
            val fullName = fullNameEditText.text.toString()
            val birthDate = birthDateEditText.text.toString()

            val gender = when (genderGroup.checkedRadioButtonId) {
                R.id.regMale -> getString(R.string.male)
                R.id.regFemale -> getString(R.string.female)
                else -> ""
            }

            when {
                login.isBlank() ||
                        password.isBlank() ||
                        repeatPassword.isBlank() ||
                        fullName.isBlank() ||
                        birthDate.isBlank() ||
                        gender.isBlank() -> {
                    Toast.makeText(
                        this,
                        R.string.fill_fields,
                        Toast.LENGTH_SHORT
                    ).show()

                    Log.d("Registration", "Регистрация: не все поля заполнены")
                }

                password != repeatPassword -> {
                    Toast.makeText(
                        this,
                        R.string.password_mismatch,
                        Toast.LENGTH_SHORT
                    ).show()

                    Log.d("Registration", "Регистрация: пароли не совпадают")
                }

                else -> {
                    val record = UserRecord(
                        login = login.trim(),
                        fullName = fullName.trim(),
                        birthDate = birthDate,
                        gender = gender,
                        avatarIndex = avatarIndex
                    )

                    if (UserRepository.add(this, record)) {
                        Log.d("Registration", "Пользователь зарегистрирован: ${record.login}")
                        Toast.makeText(this, R.string.registration_success, Toast.LENGTH_SHORT).show()
                        returnToPreviousScreen()
                    } else {
                        Log.d("Registration", "Логин уже существует: ${record.login}")
                        Toast.makeText(this, R.string.user_already_exists, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            returnToPreviousScreen()
        }
    }

    private fun returnToPreviousScreen() {
        // Регистрация из Hello возвращает в Hello; из списка — к списку.
        val fromUsers = intent.getBooleanExtra("from_users", false)
        val destination = if (fromUsers) UsersActivity::class.java else HelloActivity::class.java
        Log.d("Registration", "Возврат на экран ${destination.simpleName}")
        val nextIntent = Intent(this, destination)
        nextIntent.putExtra("kab_count", kab_Count)
        startActivity(nextIntent)
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("kab_count", kab_Count)
        outState.putString("login", loginEditText.text.toString())
        outState.putString("password", passwordEditText.text.toString())
        outState.putString("repeat_password", repeatPasswordEditText.text.toString())
        outState.putString("full_name", fullNameEditText.text.toString())
        outState.putString("birth_date", birthDateEditText.text.toString())
        outState.putInt("gender_id", genderGroup.checkedRadioButtonId)
        outState.putInt("avatar_index", avatarIndex)
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
