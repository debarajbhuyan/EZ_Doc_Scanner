package creations.maa.devraj.ezdocscanner.ui.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import creations.maa.devraj.ezdocscanner.ui.viewModel.DocViewModel

@Composable
fun LoadingDialog(docViewModel: DocViewModel) {
    if (docViewModel.loadingDialog) {
        Dialog(onDismissRequest = {
            docViewModel.loadingDialog = false
        }, properties = DialogProperties(false,false)) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}