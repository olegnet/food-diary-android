/*
 * Copyright 2022 Oleg Okhotnikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.oleg.fd.ui

import android.Manifest
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import net.oleg.fd.R
import net.oleg.fd.ui.theme.FoodDiaryTheme
import net.oleg.fd.viewmodel.FoodViewModel
import net.oleg.fd.viewmodel.FoodViewModelMock

sealed class Screen(val route: String, val icon: ImageVector, @StringRes val text: Int) {
    object DailyList : Screen("daily-list", Icons.Filled.List, R.string.nav_bar_daily_list)
    object AddToDailyList : Screen("add-to-daily-list", Icons.Filled.AddComment, R.string.nav_bar_add_to_daily_list)
    object EditFood : Screen("add-or-edit-food", Icons.Filled.Edit, R.string.nav_bar_edit_food)
    object Camera : Screen("camera", Icons.Filled.Camera, R.string.nav_bar_camera_short)

    // not a real screen. used in viewmodel.cameraReturnPath
    object Barcode: Screen("barcode", Icons.Filled.Camera, R.string.nav_bar_camera_short)
}

fun NavHostController.navigate(screen: Screen) {
    navigate(screen.route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivityScreen(
    viewModel: FoodViewModel
) {
    val navBarItems = listOf(Screen.DailyList, Screen.AddToDailyList, Screen.EditFood, Screen.Camera)
    val navController = rememberNavController()

    FoodDiaryTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    navBarItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = stringResource(screen.text)) },
                            label = { Text(
                                text = stringResource(id = screen.text),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            ) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                if (screen == Screen.Camera) {
                                    // if current screen is the camera screen, set return path to Barcode
                                    // as a flag to process it later when barcode is ready
                                    viewModel.setCameraReturnPath(Screen.Barcode)
                                }
                                // looks a bit better and fixes bug with the camera screen
                                if (currentDestination?.route != screen.route) {
                                    navController.navigate(screen)
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(navController, startDestination = Screen.DailyList.route, Modifier.padding(innerPadding)) {
                composable(Screen.DailyList.route) { DailyListScreen(navController, viewModel) }
                composable(Screen.AddToDailyList.route) { AddToDailyListScreen(navController, viewModel) }
                composable(Screen.EditFood.route) { EditFoodForm(navController, viewModel) }
                composable(Screen.Camera.route) { CameraScreen(navController, viewModel) }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(navController: NavHostController, viewModel: FoodViewModel) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    when (cameraPermissionState.status) {
        PermissionStatus.Granted -> {
            CameraPreview(navController, viewModel)
        }
        is PermissionStatus.Denied -> {
            CameraAskForPermission(cameraPermissionState)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityScreenPreview() {
    MainActivityScreen(viewModel = FoodViewModelMock)
}