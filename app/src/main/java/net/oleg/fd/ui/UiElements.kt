package net.oleg.fd.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.paging.PagingConfig
import net.oleg.fd.room.FoodItem

val pagingConfig =
    PagingConfig(
        pageSize = 20,
        enablePlaceholders = true,
        maxSize = 200
    )

fun lazyColumnProgressItem(lazyListScope: LazyListScope, isInLoadingState: Boolean) =
    lazyListScope.apply {
        val modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
        item {
            if (isInLoadingState) {
                LinearProgressIndicator(
                    modifier = modifier
                )
            } else {
                LinearProgressIndicator(
                    modifier = modifier,
                    trackColor = Color.Transparent,
                    progress = 0f
                )
            }
        }
    }

val foodItemDummy = FoodItem(
    id = 0,
    name = "",
    barcode = null,
    date = null,
    energy = 0f,
    carbs = 0f,
    fat = 0f,
    protein = 0f,
    itemIsDeleted = false
)
