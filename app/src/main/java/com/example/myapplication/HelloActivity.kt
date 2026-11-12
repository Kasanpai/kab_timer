
package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class HelloActivity : AppCompatActivity() {

    private var kab_Count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "HelloActivity: onCreate")

        kab_Count = (savedInstanceState?.getInt("kab_count")
            ?: intent.getIntExtra("kab_count", -1)) + 1

        setContent {
            WelcomeTheme {
                var login by rememberSaveable { mutableStateOf("") }
                var password by rememberSaveable { mutableStateOf("") }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.kab),
                        contentDescription = stringResource(R.string.logo),
                        modifier = Modifier.size(120.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.app_title),
                        fontSize = 28.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = stringResource(R.string.app_description),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(24.dp))

                    OutlinedTextField(
                        value = login,
                        onValueChange = { login = it },
                        label = {
                            Text(stringResource(R.string.login))
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = {
                            Text(stringResource(R.string.password))
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            openActivity(MenuActivity::class.java)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(stringResource(R.string.sign_in))
                    }

                    Button(
                        onClick = {
                            openActivity(RegistrationActivity::class.java)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Text(stringResource(R.string.registration))
                    }

                    Button(
                        onClick = {
                            finishAffinity()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        )
                    ) {
                        Text(stringResource(R.string.exit))
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.kab_counter, kab_Count),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

    private fun openActivity(activity: Class<out Activity>) {
        val nextIntent = Intent(this, activity)
        nextIntent.putExtra("kab_count", kab_Count)
        startActivity(nextIntent)
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("kab_count", kab_Count)
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "HelloActivity: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "HelloActivity: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "HelloActivity: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle", "HelloActivity: onStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("Lifecycle", "HelloActivity: onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "HelloActivity: onDestroy")
    }
}

@Preview(showBackground = true)
@Composable
fun HelloPreview() {
    WelcomeTheme {
        Text(
            text = "Таймер кабачков",
            fontSize = 28.sp
        )
    }
}
