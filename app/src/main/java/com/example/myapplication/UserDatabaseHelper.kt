package com.example.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "users.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_LOGIN = "login"
        const val COLUMN_PASSWORD_HASH = "password_hash"
        const val COLUMN_PASSWORD_SALT = "password_salt"
        const val COLUMN_FULL_NAME = "full_name"
        const val COLUMN_BIRTH_DATE = "birth_date"
        const val COLUMN_GENDER = "gender"
        const val COLUMN_AVATAR = "avatar_index"
        const val COLUMN_ADMIN = "is_admin"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_LOGIN TEXT NOT NULL UNIQUE COLLATE NOCASE,
                $COLUMN_PASSWORD_HASH TEXT NOT NULL,
                $COLUMN_PASSWORD_SALT TEXT NOT NULL,
                $COLUMN_FULL_NAME TEXT NOT NULL,
                $COLUMN_BIRTH_DATE TEXT NOT NULL,
                $COLUMN_GENDER TEXT NOT NULL,
                $COLUMN_AVATAR INTEGER NOT NULL DEFAULT 0,
                $COLUMN_ADMIN INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}
