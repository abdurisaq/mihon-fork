package eu.kanade.presentation.more.settings.screen.reader.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import eu.kanade.presentation.more.settings.screen.reader.keybind.model.KeybindAction
import eu.kanade.presentation.more.settings.screen.reader.keybind.model.getKeyCodeName
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.SelectItem
import tachiyomi.presentation.core.components.SliderItem
import tachiyomi.presentation.core.components.material.Button
import tachiyomi.presentation.core.i18n.stringResource
import java.text.NumberFormat
import kotlin.time.Duration.Companion.seconds

@Composable
fun KeybindCreateDialog(
    onDismissRequest: () -> Unit,
    onCreate: (Int, KeybindAction) -> Unit,
    keybindings: ImmutableMap<Int, KeybindAction>
    ) {


    var shortClickFunctionName by remember { mutableStateOf(KeybindAction.bindableFunctions()[0])}
    var longClickFunctionName by remember { mutableStateOf(KeybindAction.bindableFunctions()[0])}

    var shortClickFunctionParameter by remember { mutableFloatStateOf(0f) }
    var longClickFunctionParameter by remember { mutableFloatStateOf(0f) }



    var buttonText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var capturedKey by remember { mutableIntStateOf(-1) }

    val choices = KeybindAction.bindableFunctions()
    val focusRequester = remember { FocusRequester() }
    var errorText by remember { mutableStateOf("") }
    val nameAlreadyExists = remember(capturedKey) { keybindings.contains(capturedKey) }
    val valueHasChanged by remember { mutableStateOf(capturedKey != -1) }

    val numberFormat = remember { NumberFormat.getPercentInstance() }


    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                enabled = !nameAlreadyExists && valueHasChanged,
                onClick = {
                    val newKeybind = KeybindAction(shortClickFunctionName,shortClickFunctionParameter,
                        longClickFunctionName,longClickFunctionParameter)

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
            Text(text = stringResource(MR.strings.action_add_keybind))
        },
        text = {
            Column(
                modifier = Modifier.focusRequester(focusRequester)

                    .onKeyEvent {
                        if(isListening&&it.type == KeyEventType.KeyDown){

                            capturedKey = it.nativeKeyEvent.keyCode
                            buttonText = getKeyCodeName(capturedKey)
                            isListening = false
                            true
                        }else{
                            false
                        }
                    }
                    .focusable(true)
            ) {

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        buttonText = " "
                        isListening = true
                        focusRequester.requestFocus()
                    }
                ) {
                    when(buttonText){
                        "" -> Text(text =stringResource(MR.strings.action_start_listening))
                        " " -> Text(text =stringResource(MR.strings.action_listening))
                        else -> Text(text =stringResource(MR.strings.add_keybind_confirmation, buttonText))
                    }

                }

                SelectItem(
                    label =stringResource(MR.strings.short_press_indicator),
                    options = choices,
                    onSelect = {
                        shortClickFunctionName = choices[it]
                    },
                    selectedIndex = if(choices.indexOf(shortClickFunctionName) !=-1) choices.indexOf(shortClickFunctionName) else 0
                )

                SliderItem(
                    label =stringResource(MR.strings.pref_webtoon_scroll_amount),
                    min = 0,
                    max = 20,
                    value = (shortClickFunctionParameter*10).toInt(),
                    valueText =  numberFormat.format(shortClickFunctionParameter/2) ,//"${(shortClickFunctionParameter*50).toInt()}%",
                    onChange = {
                        shortClickFunctionParameter = it/10f
                    }

                )
                SelectItem(
                    label =stringResource(MR.strings.long_press_indicator),
                    options = choices,
                    onSelect = {
                        longClickFunctionName = choices[it]
                    },
                    selectedIndex =  if(choices.indexOf(longClickFunctionName) !=-1) choices.indexOf(longClickFunctionName) else 0
                )

                SliderItem(
                    label =stringResource(MR.strings.pref_webtoon_scroll_amount),//"Amount",
                    min = 0,
                    max = 20,
                    value = (longClickFunctionParameter*10).toInt(),
                    valueText = numberFormat.format(longClickFunctionParameter/2),
                    onChange = {
                        longClickFunctionParameter = it/10f
                    }

                )
                if (buttonText.isEmpty() || nameAlreadyExists) {
                    errorText = if (nameAlreadyExists) {
                        stringResource(MR.strings.error_keybinding_exists)
                    } else {
                        stringResource(MR.strings.information_required_plain)
                    }

                    Text(text = errorText, color =if (nameAlreadyExists) Color.Red else Color.Gray )
                }

            }
        },
    )

    LaunchedEffect(focusRequester) {
        //TODO: https://issuetracker.google.com/issues/204502668
        withContext(Dispatchers.Main) {
            delay(0.1.seconds)
            focusRequester.requestFocus()
        }
    }
}

@Composable
fun KeybindRebindDialog(
    onDismissRequest: () -> Unit,
    onRename: (Int, KeybindAction) -> Unit,
    keycode: Int,
    keybind: KeybindAction
) {


    var shortClickFunctionName by remember { mutableStateOf(keybind.shortClickFunctionName)}
    var longClickFunctionName by remember { mutableStateOf(keybind.longClickFunctionName)}
    val longReleaseFunctionName by remember { mutableStateOf(keybind.longReleaseFunctionName)}

    var shortClickFunctionParameter by remember { mutableFloatStateOf(keybind.shortClickParameter) }
    var longClickFunctionParameter by remember { mutableFloatStateOf(keybind.longClickParameter) }

    val valueHasChanged by remember {
        derivedStateOf {
            shortClickFunctionName != keybind.shortClickFunctionName ||
                longClickFunctionName != keybind.longClickFunctionName ||
                longReleaseFunctionName != keybind.longReleaseFunctionName ||
                shortClickFunctionParameter != keybind.shortClickParameter ||
                longClickFunctionParameter != keybind.longClickParameter
        }
    }
    val choices = KeybindAction.bindableFunctions()
    val focusRequester = remember { FocusRequester() }
    val numberFormat = remember { NumberFormat.getPercentInstance() }


    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.focusRequester(focusRequester),
        confirmButton = {
            TextButton(
                enabled = valueHasChanged,
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
            Text(text = stringResource(MR.strings.action_edit_keybind))
        },
        text = {
            Column{

                SelectItem(
                    label =stringResource(MR.strings.short_press_indicator),
                    options = choices,
                    onSelect = {
                        shortClickFunctionName = choices[it]
                    },
                    selectedIndex = if(choices.indexOf(shortClickFunctionName) !=-1) choices.indexOf(shortClickFunctionName) else 0
                )

                SliderItem(
                    label = stringResource(MR.strings.pref_webtoon_scroll_amount),
                    min = 0,
                    max = 20,
                    value = (shortClickFunctionParameter*10).toInt(),
                    valueText = numberFormat.format(shortClickFunctionParameter/2),
                    onChange = {
                        shortClickFunctionParameter = it/10f
                    }

                )
                SelectItem(
                    label =stringResource(MR.strings.long_press_indicator),
                    options = choices,
                    onSelect = {
                        longClickFunctionName = choices[it]
                    },
                    selectedIndex = if(choices.indexOf(longClickFunctionName) !=-1) choices.indexOf(longClickFunctionName) else 0
                )

                SliderItem(
                    label = stringResource(MR.strings.pref_webtoon_scroll_amount),
                    min = 0,
                    max = 20,
                    value = (longClickFunctionParameter*10).toInt(),
                    valueText = numberFormat.format(longClickFunctionParameter/2),
                    onChange = {
                        longClickFunctionParameter = it/10f
                    }

                )
        }

        },
    )


    LaunchedEffect(focusRequester) {
        // TODO: https://issuetracker.google.com/issues/204502668
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
            Text(text = stringResource(MR.strings.action_delete_keybind))
        },
        text = {
            Text(text =  stringResource(MR.strings.delete_keybind_confirmation, keybindToString))
        },
    )
}
