package net.oleg.fd.ui

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.oleg.fd.R
import net.oleg.fd.room.FoodItem
import net.oleg.fd.ui.theme.invertedButtonColors
import net.oleg.fd.viewmodel.FloatFieldState
import net.oleg.fd.viewmodel.FoodViewModelMock

@Composable
fun NutritionInfo(
    energy: Float,
    carbs: Float,
    fat: Float,
    protein: Float,
) {
    val borderWidth = 1.dp
    val borderShape = MaterialTheme.shapes.extraSmall
    val borderColor = MaterialTheme.colorScheme.outline

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Column(
            modifier = Modifier
                .width(0.dp)
                .weight(1f)
                .border(width = borderWidth, shape = borderShape, color = borderColor)
                .padding(8.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier
                    .wrapContentSize(align = Alignment.Center),
                text = stringResource(id = R.string.label_calories),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
            Text(
                modifier = Modifier
                    .wrapContentSize(align = Alignment.Center),
                text = energy.printToForm(),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )
        }

        Column(
            modifier = Modifier
                .width(0.dp)
                .weight(1f)
                .border(width = borderWidth, shape = borderShape, color = borderColor)
                .padding(8.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier
                    .wrapContentSize(align = Alignment.Center),
                text = stringResource(id = R.string.label_carbs),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
            Text(
                modifier = Modifier
                    .wrapContentSize(align = Alignment.Center),
                text = carbs.printToForm(),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )
        }

        Column(
            modifier = Modifier
                .width(0.dp)
                .weight(1f)
                .border(width = borderWidth, shape = borderShape, color = borderColor)
                .padding(8.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier
                    .wrapContentSize(align = Alignment.Center),
                text = stringResource(id = R.string.label_fat),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
            Text(
                modifier = Modifier
                    .wrapContentSize(align = Alignment.Center),
                text = fat.printToForm(),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )
        }

        Column(
            modifier = Modifier
                .width(0.dp)
                .weight(1f)
                .border(width = borderWidth, shape = borderShape, color = borderColor)
                .padding(8.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier
                    .wrapContentSize(align = Alignment.Center),
                text = stringResource(id = R.string.label_protein),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
            Text(
                modifier = Modifier
                    .wrapContentSize(align = Alignment.Center),
                text = protein.printToForm(),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )
        }
    }
}

@Composable
fun EditFoodWeightRow(
    foodItem: FoodItem,
    weight: String? = null,
    secondaryActionButton: (@Composable () -> Unit)? = null,
    onClick: (weight: Float) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val (foodWeight, setFoodWeight) = rememberSaveable { mutableStateOf(FloatFieldState(weight ?: "")) }

    NutritionInfo(foodItem.energy, foodItem.carbs!!, foodItem.fat!!, foodItem.protein!!)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier
                .width(0.dp)
                .weight(1f),
            value = foodWeight.value,
            isError = foodWeight.isError,
            onValueChange = { setFoodWeight(FloatFieldState(it)) },
            label = { Text(text = stringResource(id = R.string.label_weight)) },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.clearFocus() }),
        )

        Button(
            colors = invertedButtonColors(),
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 4.dp),
            onClick = {
                if (!foodWeight.isError && foodWeight.value.isNotEmpty()) {
                    onClick(foodWeight.value.toFloat())
                } // else FIXME show error
            }
        ) {
            Text(text = stringResource(id = if (weight != null) R.string.button_save else R.string.button_add))
        }

        secondaryActionButton?.invoke()
    }
}

@Composable
fun NoFoodColumn(
    @StringRes text: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier
                .wrapContentSize()
                .padding(bottom = 16.dp),
            text = stringResource(id = text)
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 64.dp),
            onClick = onClick
        ) {
            Text(text = stringResource(id = R.string.button_add))
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun EditFoodWeightRowPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        EditFoodWeightRow(
            foodItem = FoodViewModelMock.selectedFoodItem.value!!,
            secondaryActionButton = { EditIconButton {} },
            onClick = {}
        )
    }
}