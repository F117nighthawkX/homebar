package dev.nighthawklabs.homebar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.nighthawklabs.homebar.ui.navigation.HomeBarNavGraph
import dev.nighthawklabs.homebar.ui.theme.HomeBarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeBarTheme {
                HomeBarNavGraph()
            }
        }
    }
}

