package com.ramiz.documentscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramiz.documentscanner.ui.theme.DocumentScannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DocumentScannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ScreenUI(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ScreenUI(modifier: Modifier = Modifier) {
    Text(text = "Hello World", modifier = modifier.padding(16.dp))
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DocumentScannerTheme {

    }
}
