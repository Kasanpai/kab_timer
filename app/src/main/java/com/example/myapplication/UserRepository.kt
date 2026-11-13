package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Base64
import android.util.Log
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


data class UserRecord(
    val login: String,
    val fullName: String,
    val birthDate: String,
    val gender: String,
    val avatarIndex: Int,
    val isAdmin: Boolean = false
)

object UserRepository {
    private const val SESSION_PREFS = "active_session"
    private const val SESSION_LOGIN = "login"
    private const val HASH_ITERATIONS = 120_000

    private fun fromCursor(c: Cursor): UserRecord = UserRecord(
        login = c.getString(c.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_LOGIN)),
        fullName = c.getString(c.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_FULL_NAME)),
        birthDate = c.getString(c.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_BIRTH_DATE)),
        gender = c.getString(c.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_GENDER)),
        avatarIndex = c.getInt(c.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_AVATAR)),
        isAdmin = c.getInt(c.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_ADMIN)) == 1
    )

    fun getAll(context: Context): List<UserRecord> {
        val helper = UserDatabaseHelper(context.applicationContext)
        return try {
            helper.readableDatabase.query(
                UserDatabaseHelper.TABLE_USERS, null, null, null, null, null,
                "${UserDatabaseHelper.COLUMN_ID} ASC"
            ).use { cursor ->
                buildList {
                    while (cursor.moveToNext()) add(fromCursor(cursor))
                }
            }
        } finally {
            helper.close()
        }
    }

    fun getByLogin(context: Context, login: String): UserRecord? {
        val helper = UserDatabaseHelper(context.applicationContext)
        return try {
            helper.readableDatabase.query(
                UserDatabaseHelper.TABLE_USERS,
                null,
                "${UserDatabaseHelper.COLUMN_LOGIN} = ? COLLATE NOCASE",
                arrayOf(login), null, null, null
            ).use { cursor ->
                if (cursor.moveToFirst()) fromCursor(cursor) else null
            }
        } finally {
            helper.close()
        }
    }

    private fun passwordHash(password: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, HASH_ITERATIONS, 256)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                .generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    fun register(context: Context, record: UserRecord, password: String): Boolean {
        if (record.login.isBlank() || password.isBlank()) return false
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val encodedSalt = Base64.encodeToString(salt, Base64.NO_WRAP)
        val encodedHash = Base64.encodeToString(passwordHash(password, salt), Base64.NO_WRAP)

        val helper = UserDatabaseHelper(context.applicationContext)
        return try {
            val db = helper.writableDatabase
            db.beginTransaction()
            try {
                val first = db.rawQuery(
                    "SELECT COUNT(*) FROM ${UserDatabaseHelper.TABLE_USERS}", null
                ).use { cursor -> cursor.moveToFirst(); cursor.getInt(0) == 0 }

                val values = ContentValues().apply {
                    put(UserDatabaseHelper.COLUMN_LOGIN, record.login.trim())
                    put(UserDatabaseHelper.COLUMN_PASSWORD_HASH, encodedHash)
                    put(UserDatabaseHelper.COLUMN_PASSWORD_SALT, encodedSalt)
                    put(UserDatabaseHelper.COLUMN_FULL_NAME, record.fullName)
                    put(UserDatabaseHelper.COLUMN_BIRTH_DATE, record.birthDate)
                    put(UserDatabaseHelper.COLUMN_GENDER, record.gender)
                    put(UserDatabaseHelper.COLUMN_AVATAR, record.avatarIndex)
                    put(UserDatabaseHelper.COLUMN_ADMIN, if (first) 1 else 0)
                }
                val id = db.insertWithOnConflict(
                    UserDatabaseHelper.TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_IGNORE
                )
                if (id == -1L) false else {
                    db.setTransactionSuccessful()
                    true
                }
            } finally {
                db.endTransaction()
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Не удалось зарегистрировать пользователя", e)
            false
        } finally {
            helper.close()
        }
    }

    fun add(context: Context, record: UserRecord): Boolean = false

    fun authenticate(context: Context, login: String, password: String): UserRecord? {
        if (login.isBlank() || password.isBlank()) return null
        val helper = UserDatabaseHelper(context.applicationContext)
        val saved = try {
            helper.readableDatabase.query(
                UserDatabaseHelper.TABLE_USERS,
                arrayOf(
                    UserDatabaseHelper.COLUMN_PASSWORD_HASH,
                    UserDatabaseHelper.COLUMN_PASSWORD_SALT
                ),
                "${UserDatabaseHelper.COLUMN_LOGIN} = ? COLLATE NOCASE",
                arrayOf(login.trim()), null, null, null
            ).use { c ->
                if (c.moveToFirst()) Pair(c.getString(0), c.getString(1)) else null
            }
        } finally {
            helper.close()
        } ?: return null

        val salt = Base64.decode(saved.second, Base64.NO_WRAP)
        val expectedHash = Base64.decode(saved.first, Base64.NO_WRAP)
        val actualHash = passwordHash(password, salt)
        if (!MessageDigest.isEqual(expectedHash, actualHash)) return null

        val user = getByLogin(context, login.trim()) ?: return null
        context.getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE)
            .edit().putString(SESSION_LOGIN, user.login).apply()
        return user
    }

    fun current(context: Context): UserRecord? {
        val login = context.getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE)
            .getString(SESSION_LOGIN, null) ?: return null
        return getByLogin(context, login)
    }

    fun signOut(context: Context) {
        context.getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE)
            .edit().remove(SESSION_LOGIN).apply()
    }

    fun makeAdmin(context: Context, login: String): Boolean {
        if (current(context)?.isAdmin != true) return false
        val helper = UserDatabaseHelper(context.applicationContext)
        return try {
            val values = ContentValues().apply {
                put(UserDatabaseHelper.COLUMN_ADMIN, 1)
            }
            helper.writableDatabase.update(
                UserDatabaseHelper.TABLE_USERS, values,
                "${UserDatabaseHelper.COLUMN_LOGIN} = ? COLLATE NOCASE",
                arrayOf(login)
            ) > 0
        } finally {
            helper.close()
        }
    }

    fun remove(context: Context, login: String): Boolean {
        val currentUser = current(context) ?: return false
        if (!currentUser.isAdmin || currentUser.login.equals(login, true)) return false
        val target = getByLogin(context, login) ?: return false
        if (target.isAdmin && getAll(context).count { it.isAdmin } <= 1) return false

        val helper = UserDatabaseHelper(context.applicationContext)
        return try {
            helper.writableDatabase.delete(
                UserDatabaseHelper.TABLE_USERS,
                "${UserDatabaseHelper.COLUMN_LOGIN} = ? COLLATE NOCASE",
                arrayOf(login)
            ) > 0
        } finally {
            helper.close()
        }
    }
}
