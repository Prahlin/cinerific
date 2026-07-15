package com.prahlin.cinerific

import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.prahlin.cinerific.ui.CinerificApp
import com.prahlin.cinerific.ui.theme.CinerificTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val bootStartMillis = SystemClock.uptimeMillis()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        setContent {
            CinerificTheme {
                CinerificApp(bootStartMillis = bootStartMillis)
            }
        }
    }
}
