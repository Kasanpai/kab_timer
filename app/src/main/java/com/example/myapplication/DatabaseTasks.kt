package com.example.myapplication

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log

object DatabaseTasks {
    fun delayMs(context: Context): Long {
        return context.getSharedPreferences(
            "lab6_database",
            Context.MODE_PRIVATE
        ).getLong("delay_ms", 0L)
    }

    fun setDelay(context: Context, value: Long) {
        context.getSharedPreferences(
            "lab6_database",
            Context.MODE_PRIVATE
        ).edit()
            .putLong("delay_ms", value)
            .apply()
    }

    private val handler = Handler(Looper.getMainLooper())

    fun <T> execute(
        context: Context,
        operation: () -> T,
        onFinished: (Result<T>) -> Unit
    ) {
        Log.d("Lab6", "Запуск из потока: ${Thread.currentThread().name}")

        Thread {
            Log.d("Lab6", "Работа с БД в потоке: ${Thread.currentThread().name}")

            val result = runCatching {
                val delay = context.getSharedPreferences(
                    "lab6_database",
                    Context.MODE_PRIVATE
                ).getLong("delay_ms", 0L)

                Thread.sleep(delay)
                operation()
            }

            handler.post {
                Log.d("Lab6", "Результат получен в потоке: ${Thread.currentThread().name}")
                onFinished(result)
            }
        }.start()
    }
}
