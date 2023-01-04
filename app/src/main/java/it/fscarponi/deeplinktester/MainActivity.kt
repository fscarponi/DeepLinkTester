package it.fscarponi.deeplinktester

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private val currentUrl = mutableStateOf("")
    private val status = mutableStateOf(Status.WAIT)

    enum class Status {
        SENT, SENDING, FAIL, WAIT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Deeplink generator")
                        Image(painter = painterResource(id = R.drawable.link), contentDescription = "")
                        UrlContainer(currentUrl, Modifier.fillMaxSize(), status)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlContainer(text: MutableState<String>, modifier: Modifier = Modifier, status: MutableState<MainActivity.Status>) {
    val scope = rememberCoroutineScope()
    val context= LocalContext.current
    Column(modifier = modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = text.value, onValueChange = { it: String ->
                text.value = it
            }
        )
        Button(
            onClick = {
                scope.launch {
                    runCatching {
                        println("sending deeplink")
                        println(text.value)
                        status.value = MainActivity.Status.SENDING
                        val intent = Intent(Intent.ACTION_DEFAULT)
                        intent.data = Uri.parse(text.value)
                        context.startActivity(intent)
                    }.onSuccess {
                        status.value = MainActivity.Status.SENT
                        println("deeplink sent")
                    }.onFailure {
                        status.value = MainActivity.Status.FAIL
                        println("deeplink fail")
                        Toast.makeText(context, it.message,Toast.LENGTH_LONG ).show()
                    }
                }
            },
            enabled = status.value != MainActivity.Status.SENDING
        ) {
            Text(text = "Send Deeplink")
        }
        Text(text = "state: ${status.value.name}")
    }

}
