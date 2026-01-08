package br.com.webstorage.falaserio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import br.com.webstorage.falaserio.presentation.navigation.FalaSerioNavGraph
import br.com.webstorage.falaserio.presentation.ui.theme.FalaSerioTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity principal do FalaSério.
 *
 * @AndroidEntryPoint é OBRIGATÓRIO para injetar dependências via Hilt.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FalaSerioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FalaSerioNavGraph()
                }
            }
        }
    }
}
