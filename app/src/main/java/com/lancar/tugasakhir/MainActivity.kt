package com.lancar.tugasakhir

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lancar.tugasakhir.data.UserPreferencesRepository
import com.lancar.tugasakhir.navigation.NavGraph
import com.lancar.tugasakhir.navigation.Screen
import com.lancar.tugasakhir.ui.theme.PerpustakaanTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPrefsRepository: UserPreferencesRepository
    private lateinit var navController: NavHostController

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (::navController.isInitialized) {
            navController.handleDeepLink(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        setContent {
            PerpustakaanTheme {
                var startDestination by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(key1 = Unit) {
                    val isOnboardingCompleted = userPrefsRepository.isOnboardingCompleted.first()
                    if (!isOnboardingCompleted) {
                        startDestination = Screen.Onboarding.route
                    } else {
                        val authToken = userPrefsRepository.authToken.first()
                        startDestination = if (authToken != null) Screen.Home.route else Screen.Login.route
                    }
                }

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    if (startDestination != null) {
                        navController = rememberNavController()
                        HandleBackButton(navController)
                        NavGraph(
                            navController = navController,
                            startDestination = startDestination!!
                        )
                        if (intent?.data != null) {
                            LaunchedEffect(key1 = Unit) {
                                navController.handleDeepLink(intent)
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HandleBackButton(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var backPressedTime by remember { mutableStateOf(0L) }
    val context = LocalContext.current
    val activity = (LocalContext.current as? ComponentActivity)

    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (currentRoute != Screen.Home.route) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.startDestinationId)
                    }
                } else {
                    if (backPressedTime + 2000 > System.currentTimeMillis()) {
                        activity?.finish()
                    } else {
                        Toast.makeText(context, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show()
                    }
                    backPressedTime = System.currentTimeMillis()
                }
            }
        }
    }

    val dashboardScreens = listOf(
        Screen.Home.route, Screen.Riwayat.route, Screen.Scan.route,
        Screen.Koleksi.route, Screen.Profile.route
    )
    backCallback.isEnabled = dashboardScreens.contains(currentRoute)

    val dispatcher = activity?.onBackPressedDispatcher
    DisposableEffect(dispatcher) {
        dispatcher?.addCallback(backCallback)
        onDispose {
            backCallback.remove()
        }
    }
}