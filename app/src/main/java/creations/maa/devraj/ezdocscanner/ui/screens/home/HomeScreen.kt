package creations.maa.devraj.ezdocscanner.ui.screens.home

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import creations.maa.devraj.ezdocscanner.R
import creations.maa.devraj.ezdocscanner.data.models.PdfEntity
import creations.maa.devraj.ezdocscanner.ui.screens.common.ErrorScreen
import creations.maa.devraj.ezdocscanner.ui.screens.common.LoadingDialog
import creations.maa.devraj.ezdocscanner.ui.screens.home.components.PdfLayout
import creations.maa.devraj.ezdocscanner.ui.screens.home.components.RenameDeleteDialog
import creations.maa.devraj.ezdocscanner.ui.viewModel.DocViewModel
import creations.maa.devraj.ezdocscanner.util.Resource
import creations.maa.devraj.ezdocscanner.util.copyPdfFileToAppDirectory
import creations.maa.devraj.ezdocscanner.util.getFileSize
import creations.maa.devraj.ezdocscanner.util.showToast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(docViewModel: DocViewModel) {
    LoadingDialog(docViewModel = docViewModel)
    RenameDeleteDialog(docViewModel = docViewModel)

    val activity = LocalContext.current as Activity
    val context = LocalContext.current

    val pdfState by docViewModel.pdfStateFlow.collectAsState()
    val message = docViewModel.message
    LaunchedEffect(Unit) {
        message.collect{
            when(it){
                is Resource.Success -> {
                    context.showToast(it.data)
                }
                is Resource.Error -> {
                    context.showToast(it.message)
                }
                Resource.Idle -> {}
                Resource.Loading -> {}
            }
        }
    }

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result->
        if (result.resultCode == Activity.RESULT_OK){
            val scanningResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            scanningResult?.pdf?.let {pdf->
                Log.d("pdfName", pdf.uri.lastPathSegment.toString())

                val date = Date()
                val fileName = SimpleDateFormat(
                    "dd-MMM-yyyy HH:mm:ss",
                    Locale.getDefault()
                ).format(date) + ".pdf"

                copyPdfFileToAppDirectory(context, pdf.uri, fileName)

                val pdfEntity = PdfEntity(UUID.randomUUID().toString(),fileName,getFileSize(context, fileName),date)
                docViewModel.insertPdf(pdfEntity)
            }
        }
    }

    val scanner =  remember {
        GmsDocumentScanning.getClient(
            GmsDocumentScannerOptions
                .Builder()
                .setGalleryImportAllowed(true)
                .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_PDF)
                .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
                .build()  //we can also add .setPageLimit() for numbers of page to be scanned
        )
    }

    Scaffold(
        topBar = {TopAppBar(title = {
            Text(text = stringResource(id = R.string.app_name))
        }, actions = {
            Switch(checked = docViewModel.isDarkMode, onCheckedChange = {
                docViewModel.isDarkMode = it
            })
        })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    scanner
                        .getStartScanIntent(activity)
                        .addOnSuccessListener {
                            scannerLauncher.launch(
                                IntentSenderRequest.Builder(it).build()
                            )
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                            context.showToast(it.message.toString())
                        }
                },
                text = {
                    Text(text = stringResource(R.string.scan))
                },
                icon = {
                    Icon(painter = painterResource(id = R.drawable.ic_camera), contentDescription = "Scan")
                })
        }
    ) {paddingValue->

        pdfState.DisplayResult(
            onLoading = {
//                Box(
//                    modifier = Modifier.size(100.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator()
//                }
            }, onSuccess = { pdfList ->
                if (pdfList.isEmpty()){
                    ErrorScreen(message = "No pdf")
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValue)
                    ) {
                        items(
                            items = pdfList,
                            key = { pdfEntity -> pdfEntity.id } // or you can simply use "key = {it.id}"
                        ) { pdfEntity ->
                            PdfLayout(pdfEntity = pdfEntity, docViewModel = docViewModel)
                            // same as before use can use (pdfEntity = it) instead of pdfEntity -> pdfEntity
                        }
                    }
                }
            },
            onError = {
                ErrorScreen(message = it)
            })

    }
}