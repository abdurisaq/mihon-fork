package eu.kanade.presentation.more.settings.screen.reader.components

import android.view.KeyEvent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import eu.kanade.presentation.more.settings.KeybindAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.SelectItem
import tachiyomi.presentation.core.components.SliderItem
import tachiyomi.presentation.core.components.material.Button
import tachiyomi.presentation.core.i18n.stringResource
import kotlin.time.Duration.Companion.seconds

@Composable
fun KeybindCreateDialog(
    onDismissRequest: () -> Unit,
    onCreate: (Int,KeybindAction) -> Unit,

) {


    var shortClickFunctionName by remember { mutableStateOf("N/A")}
    var longClickFunctionName by remember { mutableStateOf("N/A")}
    var longReleaseFunctionName by remember { mutableStateOf("stopContinuousScroll")}

    var shortClickFunctionParameter by remember { mutableFloatStateOf(0f) }
    var longClickFunctionParameter by remember { mutableFloatStateOf(0f) }


    var buttonText by remember { mutableStateOf("Press to Start Listening") }
    var isListening by remember { mutableStateOf(false) }
    var capturedKey by remember { mutableIntStateOf(-1) }

    val choices = arrayOf("N/A","moveBackward","moveForward","toggleMenu","smoothScrollBackward","smoothScrollForward")

    val focusRequester = remember { FocusRequester() }
    val nameAlreadyExists = false

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                enabled = shortClickFunctionName.isNotEmpty() && !nameAlreadyExists,
                onClick = {
                    val newKeybind = KeybindAction(shortClickFunctionName,shortClickFunctionParameter,
                        longClickFunctionName,longClickFunctionParameter,
                        longReleaseFunctionName)

                    if(capturedKey != -1){
                        onCreate(capturedKey,newKeybind )
                    }

                    onDismissRequest()
                },
            ) {
                Text(text = stringResource(MR.strings.action_add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(MR.strings.action_cancel))
            }
        },
        title = {
            Text(text = "Add Keybind" )//stringResource(MR.strings.action_add_category)
        },
        text = {
            Column(
                modifier = Modifier.focusRequester(focusRequester)

                    .onKeyEvent {
                        if(isListening&&it.type == KeyEventType.KeyDown){
                            capturedKey = it.nativeKeyEvent.keyCode
                            buttonText = "Key captured: ${getKeyCodeName(it.nativeKeyEvent.keyCode)}"
                            isListening = false
                            true // Indicate the event was handled
                        }else{
                            false
                        }
                    }
                    .focusable(true)
            ) {
                // Button that changes its text when clicked
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        buttonText = "Listening..."
                        isListening = true
                        focusRequester.requestFocus()
                    }
                ) {
                    Text(text = buttonText)
                }

                SelectItem(
                    label ="short press",
                    options = choices,
                    onSelect = {
                        shortClickFunctionName = choices[it]
                    },
                    selectedIndex = if(choices.indexOf(shortClickFunctionName) !=-1) choices.indexOf(shortClickFunctionName) else 0
                )

                SliderItem(
                    label ="Strength",
                    min = 0,
                    max = 20,
                    value = (shortClickFunctionParameter*10).toInt(),
                    valueText = "${(shortClickFunctionParameter*50).toInt()}%",
                    onChange = {
                        shortClickFunctionParameter = it/10f
                    }

                )
                SelectItem(
                    label ="long press",
                    options = choices,
                    onSelect = {
                        longClickFunctionName = choices[it]
                    },
                    selectedIndex =  if(choices.indexOf(longClickFunctionName) !=-1) choices.indexOf(longClickFunctionName) else 0
                )

                SliderItem(
                    label ="Strength",
                    min = 0,
                    max = 20,
                    value = (longClickFunctionParameter*10).toInt(),
                    valueText = "${(longClickFunctionParameter*50).toInt()}%",
                    onChange = {
                        longClickFunctionParameter = it/10f
                    }

                )

            }
        },
    )

//    LaunchedEffect(focusRequester) {
//        // TODO: https://issuetracker.google.com/issues/204502668
//        delay(0.1.seconds)
//        focusRequester.requestFocus()
//    }
    LaunchedEffect(focusRequester) {
        withContext(Dispatchers.Main) {
            delay(0.1.seconds)
            focusRequester.requestFocus()
        }
    }
}

@Composable
fun KeybindRebindDialog(
    onDismissRequest: () -> Unit,
    onRename: (Int,KeybindAction) -> Unit,
    keycode: Int,
    keybind: KeybindAction
) {

    println("short function : ${keybind.shortClickFunctionName}")

    println("long function : ${keybind.longClickFunctionName}")

    var shortClickFunctionName by remember { mutableStateOf(keybind.shortClickFunctionName)}
    var longClickFunctionName by remember { mutableStateOf(keybind.longClickFunctionName)}
    var longReleaseFunctionName by remember { mutableStateOf("stopContinuousScroll")}

    var shortClickFunctionParameter by remember { mutableFloatStateOf(keybind.shortClickParameter) }
    var longClickFunctionParameter by remember { mutableFloatStateOf(keybind.longClickParameter) }
    var valueHasChanged by remember { mutableStateOf(true) }


    val choices = arrayOf("N/A","moveBackward","moveForward","toggleMenu","smoothScrollBackward","smoothScrollForward")
    val focusRequester = remember { FocusRequester() }
    val nameAlreadyExists  by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.focusRequester(focusRequester),
        confirmButton = {
            TextButton(
                enabled = valueHasChanged && !nameAlreadyExists,
                onClick = {
                    val newKeybind = KeybindAction(shortClickFunctionName,shortClickFunctionParameter,
                        longClickFunctionName,longClickFunctionParameter,
                        longReleaseFunctionName)
                    onRename(keycode,newKeybind)
                    onDismissRequest()
                },
            ) {
                Text(text = stringResource(MR.strings.action_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(MR.strings.action_cancel))
            }
        },
        title = {
            Text(text = "Edit Keybind")//stringResource(MR.strings.action_rename_category)
        },
        text = {
            Column{

                SelectItem(
                    label ="short press",
                    options = choices,
                    onSelect = {
                        shortClickFunctionName = choices[it]
                    },
                    selectedIndex = if(choices.indexOf(shortClickFunctionName) !=-1) choices.indexOf(shortClickFunctionName) else 0
                )

                SliderItem(
                    label ="Strength",
                    min = 0,
                    max = 20,
                    value = (shortClickFunctionParameter*10).toInt(),
                    valueText = "${(shortClickFunctionParameter*50).toInt()}%",
                    onChange = {
                        shortClickFunctionParameter = it/10f
                    }

                )
                SelectItem(
                    label ="long press",
                    options = choices,
                    onSelect = {
                        longClickFunctionName = choices[it]
                    },
                    selectedIndex = if(choices.indexOf(longClickFunctionName) !=-1) choices.indexOf(longClickFunctionName) else 0
                )

                SliderItem(
                    label ="Strength",
                    min = 0,
                    max = 20,
                    value = (longClickFunctionParameter*10).toInt(),
                    valueText = "${(longClickFunctionParameter*50).toInt()}%",
                    onChange = {
                        longClickFunctionParameter = it/10f
                    }

                )
        }

        },
    )

//    LaunchedEffect(focusRequester) {
//        // TODO: https://issuetracker.google.com/issues/204502668
//        delay(0.1.seconds)
//        focusRequester.requestFocus()
//    }
    LaunchedEffect(focusRequester) {
        withContext(Dispatchers.Main) {
            delay(0.1.seconds)
            focusRequester.requestFocus()
        }
    }
}

@Composable
fun KeybindDeleteDialog(
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit,
    keybind: Int,
) {
    val keybindToString = getKeyCodeName(keybind)
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onDelete()
                onDismissRequest()
            }) {
                Text(text = stringResource(MR.strings.action_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(MR.strings.action_cancel))
            }
        },
        title = {
            Text(text = "Delete Keybind")
        },
        text = {
            Text(text = "Do you wish to delete the keybind ${keybindToString}?")
        },
    )
}
