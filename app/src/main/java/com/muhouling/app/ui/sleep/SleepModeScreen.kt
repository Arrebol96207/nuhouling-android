package com.muhouling.app.ui.sleep

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.muhouling.app.data.repository.SleepRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SleepModeScreen(
    sleepRepository: SleepRepository,
    onExit: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    var showConfirmDialog by remember { mutableStateOf(false) }
    var isExiting by remember { mutableStateOf(false) }
    var pressProgress by remember { mutableFloatStateOf(0f) }
    var isPressing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Enter immersive mode
    LaunchedEffect(Unit) {
        activity?.let { act ->
            val window = act.window
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
            insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    // Handle long press progress
    LaunchedEffect(isPressing) {
        if (isPressing) {
            while (pressProgress < 1f && isPressing) {
                delay(50)
                pressProgress += 0.025f
            }
            if (pressProgress >= 1f && isPressing) {
                showConfirmDialog = true
            }
        } else {
            pressProgress = 0f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Bedtime,
                contentDescription = null,
                modifier = Modifier.size(128.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "睡眠模式",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "长按退出按钮 2 秒退出",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(64.dp))

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { pressProgress },
                    modifier = Modifier.size(96.dp),
                    strokeWidth = 8.dp
                )

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = { isPressing = true }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.error
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "退出",
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.onError
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { isPressing = false }
            ) {
                Text("取消")
            }
        }

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    if (showConfirmDialog) {
        Dialog(onDismissRequest = { showConfirmDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "确认退出？",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "退出将消耗一枚金牌或通知紧急联系人",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showConfirmDialog = false }) {
                            Text("取消")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                isExiting = true
                                scope.launch {
                                    sleepRepository.earlyExit()
                                        .onSuccess {
                                            activity?.let { act ->
                                                val window = act.window
                                                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                                                insetsController.show(WindowInsetsCompat.Type.systemBars())
                                            }
                                            onExit()
                                        }
                                        .onFailure { errorMessage = it.message }
                                    isExiting = false
                                    showConfirmDialog = false
                                }
                            },
                            enabled = !isExiting
                        ) {
                            if (isExiting) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                Text("确认退出")
                            }
                        }
                    }
                }
            }
        }
    }
}
