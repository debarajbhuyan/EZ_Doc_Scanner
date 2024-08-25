package creations.maa.devraj.ezdocscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.viewModelFactory
import creations.maa.devraj.ezdocscanner.ui.screens.home.HomeScreen
import creations.maa.devraj.ezdocscanner.ui.theme.EZDocScannerTheme
import creations.maa.devraj.ezdocscanner.ui.viewModel.DocViewModel

class MainActivity : ComponentActivity() {
    private  val docViewModel by viewModels<DocViewModel>{
        viewModelFactory {
            addInitializer(DocViewModel::class){
                DocViewModel(application)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen() //splash screen
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            splashScreen.setKeepOnScreenCondition{docViewModel.isSplashScreen} //splash screen
            EZDocScannerTheme(docViewModel.isDarkMode,false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(docViewModel)
                }
            }
        }
    }
}