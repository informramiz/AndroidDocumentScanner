package com.ramiz.documentscanner

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.ramiz.documentscanner.ui.theme.DocumentScannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DocumentScannerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        ScreenTopBar()
                    }
                ) { innerPadding ->
                    ScreenUI(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenTopBar() {
    TopAppBar(
        title = { Text(text = "Document Scanner") },
        colors = topAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

@Composable
private fun ScreenUI(modifier: Modifier = Modifier) {
    val scannerClient = documentScanner()
    val scannerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->

    }
    val activity = LocalContext.current as ComponentActivity
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                scannerClient.getStartScanIntent(activity)
                    .addOnSuccessListener { intentSender ->
                        scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(activity, exception.message, Toast.LENGTH_SHORT).show()
                    }
            }
        ) {
            Text(text = "Scan Document")
        }
    }
}

@Composable
private fun documentScanner(): GmsDocumentScanner {
    val documentScannerOptions = GmsDocumentScannerOptions.Builder()
        .setPageLimit(1)
        .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
        .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_JPEG)
        .setGalleryImportAllowed(false)
        .build()

    val scannerClient = GmsDocumentScanning.getClient(documentScannerOptions)
    return scannerClient
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DocumentScannerTheme {

    }
}
