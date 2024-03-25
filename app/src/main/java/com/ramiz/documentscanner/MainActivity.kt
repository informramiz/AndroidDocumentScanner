package com.ramiz.documentscanner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.ramiz.documentscanner.ui.theme.DocumentScannerTheme
import java.io.File

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
        title = { Text(text = stringResource(id = R.string.app_name)) },
        colors = topAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

@Composable
private fun ScreenUI(modifier: Modifier = Modifier) {
    val activity = LocalContext.current as ComponentActivity
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val scannerClient = documentScanner()
    val scannerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
        if (activityResult.resultCode == ComponentActivity.RESULT_OK) {
            val result = GmsDocumentScanningResult.fromActivityResultIntent(activityResult.data)
            if (result == null) {
                Toast.makeText(
                    activity,
                    activity.getString(R.string.error_msg_document_scanning_failed),
                    Toast.LENGTH_SHORT
                ).show()
                return@rememberLauncherForActivityResult
            }
            imageUri = extractImageUri(result)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .weight(1f))
        AsyncImage(
            modifier = Modifier
                .size(300.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp)),
            model = imageUri,
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .weight(1f))

        Row(
            modifier = Modifier.padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
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
                Text(text = stringResource(R.string.button_label_scan_document))
            }

            if (imageUri != null) {
                Button(
                    onClick = {
                        shareDocument(activity, imageUri)
                    }
                ) {
                    Text(text = stringResource(R.string.button_label_share_document))
                }
            }
        }
    }
}

private fun shareDocument(activity: ComponentActivity, imageUri: Uri?) {
    val externalUri = FileProvider.getUriForFile(
        activity,
        activity.packageName + ".provider",
        File(imageUri?.path.orEmpty())
    )
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_STREAM, externalUri)
        type = "image/*"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    activity.startActivity(Intent.createChooser(shareIntent, "Share Document"))
}

private fun extractImageUri(scannerResult: GmsDocumentScanningResult): Uri? {
    return scannerResult.pages?.firstOrNull()?.imageUri
}
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
        ScreenUI()
    }
}
