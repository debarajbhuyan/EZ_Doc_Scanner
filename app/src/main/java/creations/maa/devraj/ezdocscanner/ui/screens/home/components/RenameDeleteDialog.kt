package creations.maa.devraj.ezdocscanner.ui.screens.home.components

import android.content.ClipData
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import creations.maa.devraj.ezdocscanner.R
import creations.maa.devraj.ezdocscanner.ui.viewModel.DocViewModel
import creations.maa.devraj.ezdocscanner.util.deleteFile
import creations.maa.devraj.ezdocscanner.util.getFileUri
import creations.maa.devraj.ezdocscanner.util.renameFile
import creations.maa.devraj.ezdocscanner.util.showToast
import java.util.Date

@Composable
fun RenameDeleteDialog(docViewModel: DocViewModel) {
    var newNameText by remember(docViewModel.currentPdfEntity) {
        mutableStateOf(docViewModel.currentPdfEntity?.name?:"")
    }

    val context = LocalContext.current

    if (docViewModel.showRenameDialog) {
        Dialog(
            onDismissRequest = {
                docViewModel.showRenameDialog = false
            }
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(id = R.string.rename_pdf)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newNameText,
                        onValueChange = { newText -> newNameText = newText },
                        label = { Text(stringResource(id = R.string.pdf_name)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        IconButton(onClick = {
                            docViewModel.currentPdfEntity?.let {
                                docViewModel.showRenameDialog = false
                                val getFileUri = getFileUri(context, it.name)
                                val shareIntent = Intent(Intent.ACTION_SEND)
                                shareIntent.type = "application/pdf"
                                shareIntent.clipData = ClipData.newRawUri("",getFileUri)
                                shareIntent.putExtra(Intent.EXTRA_STREAM, getFileUri)
                                shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                context.startActivity(Intent.createChooser(shareIntent, "Share"))
                            }
                        }) {
                            Icon(
                                painterResource(id = R.drawable.ic_share),
                                contentDescription = "delete",
                                tint = Color.Gray
                            )
                        }
                        IconButton(onClick = {
                            docViewModel.currentPdfEntity?.let {
                                docViewModel.showRenameDialog = false
                                if(deleteFile(context,it.name)) {
                                    docViewModel.deletePdf(it)
                                } else {
                                    context.showToast("Something went wrong")
                                }
                            }
                        }) {
                            Icon(
                                painterResource(id = R.drawable.ic_delete),
                                contentDescription = "delete",
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            docViewModel.showRenameDialog = false
                        }) { Text(stringResource(id = R.string.cancel)) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            docViewModel.currentPdfEntity?.let {pdf->
                                if (!pdf.name.equals(newNameText,true)) {
                                    docViewModel.showRenameDialog = false
                                    renameFile(context,pdf.name,newNameText)
                                    val updatePdf = pdf.copy(
                                        name = newNameText, lastModifiedTime = Date()
                                    )
                                    docViewModel.updatePdf(updatePdf)
                                }else{
                                    docViewModel.showRenameDialog = false
                                }
                            }
                        }) { Text(stringResource(id = R.string.update)) }
                    }
                }
            }
        }
    }
}