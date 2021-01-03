package ru.falseteam.control

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.falseteam.control.ui.ControlTheme
import ru.falseteam.control.ui.screens.addcamera.AddCameraScreen
import ru.falseteam.control.ui.screens.cams.CamsScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ControlTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Destinations.CamsScreen) {
                    composable(Destinations.CamsScreen) { CamsScreen(navController) }
                    composable(Destinations.AddCameraScreen) { AddCameraScreen(navController) }
                }
            }
        }
    }
}