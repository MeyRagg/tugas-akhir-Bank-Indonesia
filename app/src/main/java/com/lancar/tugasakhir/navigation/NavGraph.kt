package com.lancar.tugasakhir.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.hilt.navigation.compose.hiltViewModel
import com.lancar.tugasakhir.screens.*
import com.lancar.tugasakhir.viewmodel.AuthViewModel

private const val AUTH_GRAPH_ROUTE = "auth_graph"

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    val screensWithBottomBar = listOf(
        Screen.Home.route,
        Screen.Riwayat.route,
        Screen.Scan.route,
        Screen.Koleksi.route,
        Screen.Profile.route
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val shouldShowBottomBar = currentRoute in screensWithBottomBar

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    val items = listOf(
                        Screen.Home,
                        Screen.Riwayat,
                        Screen.Scan,
                        Screen.Koleksi,
                        Screen.Profile
                    )
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                            label = { Text(screen.title!!) },
                            selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinish = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(route = Screen.Login.route) {
                LoginScreen(navController = navController)
            }

            // ========= AUTH SUBGRAPH (Register & CreatePassword share satu AuthViewModel) =========
            navigation(
                route = AUTH_GRAPH_ROUTE,
                startDestination = Screen.Register.route
            ) {
                composable(route = Screen.Register.route) { backStackEntry ->
                    val parent = remember(backStackEntry) {
                        navController.getBackStackEntry(AUTH_GRAPH_ROUTE)
                    }
                    val authVM: AuthViewModel = hiltViewModel(parent)
                    RegisterScreen(navController = navController, authViewModel = authVM)
                }

                composable(route = Screen.CreatePassword.route) { backStackEntry ->
                    val parent = remember(backStackEntry) {
                        navController.getBackStackEntry(AUTH_GRAPH_ROUTE)
                    }
                    val authVM: AuthViewModel = hiltViewModel(parent)
                    CreatePasswordScreen(navController = navController, authViewModel = authVM)
                }
            }
            // ======================================================================================

            composable(route = Screen.Home.route) { HomeScreen(navController = navController) }

            composable(
                route = Screen.Riwayat.route,
                deepLinks = listOf(navDeepLink { uriPattern = "satuperpustakaanku://riwayat/{tab}" })
            ) { backStackEntry ->
                val tab = backStackEntry.arguments?.getString("tab")
                RiwayatScreen(navController = navController, initialTab = tab)
            }

            composable(route = Screen.Scan.route) { ScannerScreen(navController = navController) }
            composable(route = Screen.Koleksi.route) { KoleksiScreen(navController = navController) }
            composable(route = Screen.Profile.route) { ProfileScreen(navController = navController) }

            composable(route = Screen.EditProfile.route) { EditProfileScreen(navController = navController) }
            composable(route = Screen.NotificationHistory.route) { NotificationScreen(navController = navController) }
            composable(route = Screen.CategoryList.route) {
                CategoryListScreen(navController = navController)
            }

            composable(
                route = Screen.BookList.route,
                arguments = listOf(
                    navArgument(Screen.BookList.LIST_TYPE_ARG) { type = NavType.StringType },
                    navArgument(Screen.BookList.LIST_TITLE_ARG) { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val listType = backStackEntry.arguments?.getString(Screen.BookList.LIST_TYPE_ARG).orEmpty()
                val rawTitle = backStackEntry.arguments?.getString(Screen.BookList.LIST_TITLE_ARG).orEmpty()
                val listTitle = Uri.decode(rawTitle)

                BookListScreen(
                    navController = navController,
                    listType = listType,
                    listTitle = listTitle
                )
            }

            composable(route = Screen.Search.route) {
                SearchScreen(navController = navController)
            }

            composable(
                route = Screen.BarcodeResult.route,
                arguments = listOf(navArgument("barcode") { type = NavType.StringType })
            ) { backStackEntry ->
                val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
                BarcodeResultScreen(
                    barcode = barcode,
                    onResolved = { bookId ->
                        if (bookId != null) {
                            navController.popBackStack()
                            navController.navigate(
                                com.lancar.tugasakhir.navigation.Screen.BookDetail.createRoute(
                                    bookId
                                )
                            )
                        }
                    }
                )
            }

            composable(
                route = Screen.BookDetail.route,
                arguments = listOf(navArgument("bookId") { type = NavType.StringType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                BookDetailScreen(navController = navController, bookId = bookId)
            }
        }
    }
}
