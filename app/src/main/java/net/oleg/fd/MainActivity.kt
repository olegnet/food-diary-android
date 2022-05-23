package net.oleg.fd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import net.oleg.fd.ui.MainActivityScreen
import net.oleg.fd.viewmodel.FoodViewModel
import net.oleg.fd.viewmodel.FoodViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: FoodViewModel by viewModels {
        FoodViewModelFactory((application as FoodApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainActivityScreen(viewModel)
        }
    }
}