package creations.maa.devraj.ezdocscanner.ui.screens.home.components

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import creations.maa.devraj.ezdocscanner.R
import creations.maa.devraj.ezdocscanner.data.models.PdfEntity
import creations.maa.devraj.ezdocscanner.ui.viewModel.DocViewModel
import creations.maa.devraj.ezdocscanner.util.getFileUri
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PdfLayout(pdfEntity: PdfEntity, docViewModel: DocViewModel) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp),
        onClick = {
            val getFileUri = getFileUri(context, pdfEntity.name)
            val browserIntent = Intent(Intent.ACTION_VIEW, getFileUri)
            browserIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            activity.startActivity(browserIntent)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(40.dp),
                painter = painterResource(id = R.drawable.ic_pdf),
                contentDescription = "pdf icon",
                tint = Color(0xFFCE1717)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Title:${pdfEntity.name}",
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "size: ${pdfEntity.size}",
                    style = MaterialTheme.typography.bodyMedium
                    )
                Text(
                    text = "Date: ${SimpleDateFormat("dd-MMM-yyyy HH:mm a", Locale.getDefault()).format(pdfEntity.lastModifiedTime)}",
                    style = MaterialTheme.typography.bodyMedium
                    )

            }
            IconButton(onClick = {
                docViewModel.currentPdfEntity = pdfEntity
                docViewModel.showRenameDialog = true
            }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "more"
                )
            }

        }

    }
}