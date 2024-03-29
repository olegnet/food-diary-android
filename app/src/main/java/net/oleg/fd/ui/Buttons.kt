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

package net.oleg.fd.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import net.oleg.fd.R

@Composable
fun DeleteIconButton(
    onClick: () -> Unit,
) = IconButton(
    colors = IconButtonDefaults.iconButtonColors(
        containerColor = Color.Transparent,
        contentColor = colorScheme.error
    ),
    onClick = onClick
) {
    Icon(imageVector = Icons.Filled.Delete, contentDescription = stringResource(id = R.string.button_delete))
}

@Composable
fun EditIconButton(
    onClick: () -> Unit,
) = IconButton(
    colors = IconButtonDefaults.iconButtonColors(
        containerColor = Color.Transparent,
        contentColor = colorScheme.primary
    ),
    onClick = onClick
) {
    Icon(imageVector = Icons.Filled.Edit, contentDescription = stringResource(id = R.string.button_edit))
}