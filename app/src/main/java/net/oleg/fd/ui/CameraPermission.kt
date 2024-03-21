/*
 * Copyright 2022-2024 Oleg Okhotnikov
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

@file:OptIn(ExperimentalPermissionsApi::class)

package net.oleg.fd.ui

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.shouldShowRationale
import net.oleg.fd.R

@Composable
fun CameraAskForPermission(
    cameraPermissionState: PermissionState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp, top = 8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val message = if (cameraPermissionState.status.shouldShowRationale) {
            R.string.camera_permission_rationale
        } else {
            SideEffect {
                cameraPermissionState.launchPermissionRequest()
            }
            R.string.ask_for_camera_permission
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 8.dp, end = 8.dp, top = 8.dp),
            text = stringResource(id = message),
            textAlign = TextAlign.Center
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp),
            onClick = {
                cameraPermissionState.launchPermissionRequest()
            }
        ) {
            Text(text = stringResource(id = R.string.button_request_permission))
        }
    }
}

@LocalesPreview
@Composable
fun CameraPermissionDeniedPreview() {
    CameraAskForPermission(object : PermissionState {
        override val permission: String
            get() = Manifest.permission.CAMERA
        override val status: PermissionStatus.Denied
            get() = PermissionStatus.Denied(shouldShowRationale = false)

        override fun launchPermissionRequest() {}
    })
}

@LocalesPreview
@Composable
fun CameraPermissionShouldShowRationalePreview() {
    CameraAskForPermission(object : PermissionState {
        override val permission: String
            get() = Manifest.permission.CAMERA
        override val status: PermissionStatus.Denied
            get() = PermissionStatus.Denied(shouldShowRationale = true)

        override fun launchPermissionRequest() {}
    })
}