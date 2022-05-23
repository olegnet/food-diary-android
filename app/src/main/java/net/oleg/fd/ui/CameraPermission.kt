@file:OptIn(ExperimentalPermissionsApi::class)

package net.oleg.fd.ui

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
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
            text = stringResource(message),
            modifier = Modifier
                .padding(bottom = 16.dp, start = 8.dp, end = 8.dp, top = 8.dp)
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp),
            onClick = {
                cameraPermissionState.launchPermissionRequest()
            }
        ) {
            Text(stringResource(R.string.button_request_permission))
        }
    }
}

@Preview(showBackground = true)
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

@Preview(showBackground = true)
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