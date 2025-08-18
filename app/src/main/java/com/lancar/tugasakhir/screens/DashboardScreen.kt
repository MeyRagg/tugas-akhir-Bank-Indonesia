//package com.lancar.tugasakhir.screens
//
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.NavigationBar
//import androidx.compose.material3.NavigationBarItem
//import androidx.compose.material3.NavigationBarItemDefaults
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.navigation.NavController
//import androidx.navigation.NavDestination.Companion.hierarchy
//import androidx.navigation.NavGraph.Companion.findStartDestination
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.currentBackStackEntryAsState
//import androidx.navigation.compose.rememberNavController
//import com.lancar.tugasakhir.navigation.Screen
//
//@Composable
//fun DashboardScreen(mainNavController: NavController, initialTabRoute: String? = null) {
//    // NavController khusus untuk Bottom Bar
//    val nestedNavController = rememberNavController()
//
//    // --- PERBAIKAN UTAMA DI SINI ---
//    // LaunchedEffect memastikan navigasi hanya terjadi setelah NavController siap.
//    LaunchedEffect(initialTabRoute) {
//        if (initialTabRoute != null) {
//            nestedNavController.navigate(initialTabRoute) {
//                // Pop up to the start destination of the graph to avoid building up a large back stack.
//                popUpTo(nestedNavController.graph.startDestinationId) {
//                    saveState = true
//                }
//                // Avoid multiple copies of the same destination when re-selecting the same item
//                launchSingleTop = true
//                // Restore state when re-selecting a previously selected item
//                restoreState = true
//            }
//        }
//    }
//    // ------------------------------------
//
//    Scaffold(
//        bottomBar = {
//            NavigationBar(
//                containerColor = MaterialTheme.colorScheme.surface
//            ) {
//                val items = listOf(
//                    Screen.Home,
//                    Screen.Riwayat,
//                    Screen.Scan,
//                    Screen.Koleksi,
//                    Screen.Profile
//                )
//                val navBackStackEntry by nestedNavController.currentBackStackEntryAsState()
//                val currentDestination = navBackStackEntry?.destination
//
//                items.forEach { screen ->
//                    NavigationBarItem(
//                        icon = { Icon(screen.icon!!, contentDescription = screen.title) },
//                        label = { Text(screen.title!!) },
//                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
//                        onClick = {
//                            nestedNavController.navigate(screen.route) {
//                                popUpTo(nestedNavController.graph.findStartDestination().id) {
//                                    saveState = true
//                                }
//                                launchSingleTop = true
//                                restoreState = true
//                            }
//                        },
//                        colors = NavigationBarItemDefaults.colors(
//                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
//                            selectedTextColor = MaterialTheme.colorScheme.primary,
//                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
//                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
//                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                    )
//                }
//            }
//        }
//    ) { innerPadding ->
//        // NavHost ini hanya mengatur layar-layar yang ada di navigasi bawah
//        NavHost(
//            navController = nestedNavController,
//            startDestination = Screen.Home.route,
//            modifier = Modifier.padding(innerPadding)
//        ) {
//            composable(Screen.Home.route) { HomeScreen(navController = mainNavController) }
//            composable(Screen.Riwayat.route) { RiwayatScreen(navController = mainNavController) }
//            composable(Screen.Scan.route) { ScannerScreen(navController = mainNavController) }
//            composable(Screen.Koleksi.route) { KoleksiScreen(navController = mainNavController) }
//            composable(Screen.Profile.route) { ProfileScreen(navController = mainNavController) }
//        }
//    }
//}