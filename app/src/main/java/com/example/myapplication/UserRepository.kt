package com.example.myapplication

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.log

/** Простое локальное хранилище пользователей для учебного проекта (без базы данных). */
data class UserRecord(
    val login: String,
    val fullName: String,
    val birthDate: String,
    val gender: String,
    val avatarIndex: Int
)

object UserRepository {
    private const val PREFS = "registered_users"
    private const val KEY = "users"

    fun getAll(context: Context): List<UserRecord> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val saved = prefs.getString(KEY, null)

        // Пользователь, зарегистрированный ещё во 2-й лабораторной, не теряется.
        if (saved == null) {
            val old = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val oldLogin = old.getString("login", "").orEmpty()
            val initial = if (oldLogin.isBlank()) emptyList() else listOf(
                UserRecord(
                    oldLogin,
                    old.getString("full_name", "").orEmpty().ifBlank { oldLogin },
                    old.getString("birth_date", "").orEmpty(),
                    old.getString("gender", "").orEmpty(),
                    old.getInt("avatar_index", 0)
                )
            )
            saveAll(context, initial)
            return initial
        }

        return try {
            val array = JSONArray(saved)
            buildList {
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    add(
                        UserRecord(
                            item.optString("login"),
                            item.optString("full_name"),
                            item.optString("birth_date"),
                            item.optString("gender"),
                            item.optInt("avatar_index", 0)
                        )
                    )
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Ошибка чтения пользователей", e)
            emptyList()
        }
    }

    /** Возвращает false, если указанный логин уже зарегистрирован. */
    fun add(context: Context, record: UserRecord): Boolean {
        val users = getAll(context).toMutableList()
        if (users.any { it.login.equals(record.login, ignoreCase = true) }) return false

        users.add(record)
        saveAll(context, users)

        // Сохраняем совместимость с ProfileActivity из лабораторной №2.
        context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
            .edit()
            .putString("login", record.login)
            .putString("full_name", record.fullName)
            .putString("birth_date", record.birthDate)
            .putString("gender", record.gender)
            .putInt("avatar_index", record.avatarIndex)
            .apply()
        return true
    }

    fun remove(context: Context, login: String): Boolean {
        val users = getAll(context).toMutableList()

        val removed = users.removeAll{
            it.login.equals(login, ignoreCase = true)
        }

        if (!removed) return false

        saveAll(context, users)

        val prefs = context.getSharedPreferences(
            "user_data", Context.MODE_PRIVATE
        )

        if (prefs.getString("login", "") == login) {
            prefs.edit().clear().apply()
        }

        return true
    }

    private fun saveAll(context: Context, users: List<UserRecord>) {
        val array = JSONArray()
        for (user in users) {
            array.put(
                JSONObject().apply {
                    put("login", user.login)
                    put("full_name", user.fullName)
                    put("birth_date", user.birthDate)
                    put("gender", user.gender)
                    put("avatar_index", user.avatarIndex)
                }
            )
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, array.toString())
            .apply()
    }
}
