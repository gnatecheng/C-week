package com.py2c.week

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.py2c.week.ui.navigation.Py2CRoot
import com.py2c.week.ui.theme.Py2CTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as Py2CApplication).container
        setContent {
            Py2CTheme {
                Py2CRoot(container)
            }
        }
    }
}
