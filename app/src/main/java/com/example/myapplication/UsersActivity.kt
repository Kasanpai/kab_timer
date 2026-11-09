package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog

class UsersActivity : AppCompatActivity() {

    private var kab_Count = 0
    private val userLabels = arrayListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private var displayedUsers: List<UserRecord> = emptyList()

    private var selectedLogin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "UsersActivity: onCreate")
        setContentView(R.layout.activity_users)

        kab_Count = (savedInstanceState?.getInt("kab_count")
            ?: intent.getIntExtra("kab_count", -1)) + 1
        findViewById<TextView>(R.id.zucchiniCounter).text =
            getString(R.string.kab_counter, kab_Count)

        val usersList = findViewById<ListView>(R.id.usersList)
        adapter = ArrayAdapter(this, R.layout.item, R.id.itemContent, userLabels)
        usersList.adapter = adapter
        usersList.emptyView = findViewById(R.id.emptyUsersText)

        findViewById<Button>(R.id.addUserButton).setOnClickListener {
            Log.d("UsersActivity", "Нажата кнопка Добавить: открыта регистрация")
            startActivity(Intent(this, RegistrationActivity::class.java).apply {
                putExtra("kab_count", kab_Count)
                putExtra("from_users", true)
            })
            finish()
        }

        findViewById<Button>(R.id.deleteUserButton).setOnClickListener {
            val login = selectedLogin

            if (login == null) {
                Toast.makeText(this, "Выберите пользователя", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            AlertDialog.Builder(this)
                .setTitle("Удаление пользователя")
                .setMessage("Удалить пользователя $login?")
                .setPositiveButton("Удалить") { _, _ ->

                    if (UserRepository.remove(this, login)) {
                        selectedLogin = null
                        refreshUsers()
                        Log.d("UsersActivity", "Удалён пользователь: $login")
                    }

                }
                .setNegativeButton("Отмена", null)
                .show()
        }

        usersList.setOnItemClickListener { _, _, position, _ ->
            val user = displayedUsers[position]
            selectedLogin = user.login
            Log.d("UsersActivity", "Выбран пользователь: ${user.login}")
            Toast.makeText(this, user.fullName, Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.usersBackButton).setOnClickListener { returnToMenu() }

        onBackPressedDispatcher.addCallback(this) { returnToMenu() }
    }

    override fun onResume() {
        super.onResume()
        refreshUsers()
    }

    private fun refreshUsers() {
        displayedUsers = UserRepository.getAll(this)

        userLabels.clear()

        userLabels.addAll(displayedUsers.map { user ->
            "${user.fullName}\nЛогин: ${user.login}"
        })

        adapter.notifyDataSetChanged()
    }

    private fun returnToMenu() {
        Log.d("UsersActivity", "Возврат в главное меню")
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
