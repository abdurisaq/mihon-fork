package eu.kanade.presentation.more.settings.screen.reader

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.icerock.moko.resources.StringResource
import eu.kanade.presentation.more.settings.screen.reader.keybind.model.KeybindAction
import eu.kanade.presentation.more.settings.screen.reader.keybind.interactor.CreateKeybind
import eu.kanade.presentation.more.settings.screen.reader.keybind.interactor.DeleteKeybind
import eu.kanade.presentation.more.settings.screen.reader.keybind.interactor.GetKeybindings
import eu.kanade.presentation.more.settings.screen.reader.keybind.interactor.RebindKeybind
import eu.kanade.tachiyomi.ui.reader.setting.ReaderPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tachiyomi.i18n.MR
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class KeyboardBindingScreenModel(
    private val getKeybindingsFromKeycode: GetKeybindings = Injekt.get(),
    private val createKeybindWithKeycode: CreateKeybind = Injekt.get(),
    private val deleteKeybindWithKeycode: DeleteKeybind = Injekt.get(),
    private val rebindKeybind: RebindKeybind = Injekt.get(),
) : StateScreenModel<KeyboardBindingScreenState>(KeyboardBindingScreenState.Loading) {

    private val _events: Channel<KeybindEvent> = Channel()
    val events = _events.receiveAsFlow()

    init {
        screenModelScope.launch {
            mutableState.update {
                KeyboardBindingScreenState.Success(
                    currentKeybindings = getKeybindingsFromKeycode.subscribe().toMutableMap()
                )
            }
        }
    }

    fun createKeybind(keyCode:Int, keyAction: KeybindAction){
        screenModelScope.launch {
            when(createKeybindWithKeycode.await(keyCode,keyAction)){
                is CreateKeybind.Result.InternalError -> _events.send(KeybindEvent.InternalError)
                else -> {
                    val newKeybindings = getKeybindingsFromKeycode.subscribe().toMutableMap()
                    mutableState.update {
                        when (it) {
                            is KeyboardBindingScreenState.Success -> it.copy(currentKeybindings = newKeybindings)
                            else -> it
                        }
                    }
                }
            }
        }
    }
    fun deleteKeybind(keyEvent: Int) {
        screenModelScope.launch {
            when (deleteKeybindWithKeycode.await(keyCode = keyEvent)) {
                is DeleteKeybind.Result.InternalError -> _events.send(KeybindEvent.InternalError)
                else -> {
                    val newKeybindings = getKeybindingsFromKeycode.subscribe().toMutableMap()
                    mutableState.update {
                        when (it) {
                            is KeyboardBindingScreenState.Success -> it.copy(currentKeybindings = newKeybindings)
                            else -> it
                        }

                    }
                }
            }
        }
    }

    fun rebindKeybind(keyCode:Int, keyAction: KeybindAction) {
        screenModelScope.launch {
            when (rebindKeybind.await(keyCode, keyAction)) {
                is RebindKeybind.Result.InternalError -> _events.send(KeybindEvent.InternalError)
                else -> {
                    val newKeybindings = getKeybindingsFromKeycode.subscribe().toMutableMap()
                    mutableState.update {
                        when (it) {
                            is KeyboardBindingScreenState.Success -> it.copy(currentKeybindings = newKeybindings)
                            else -> it
                        }
                    }
                }
            }
        }
    }

    fun showDialog(dialog: KeybindDialog) {
        mutableState.update {
            when (it) {
                KeyboardBindingScreenState.Loading -> it
                is KeyboardBindingScreenState.Success -> it.copy(dialog = dialog)
            }
        }
    }

    fun dismissDialog() {
        mutableState.update {
            when (it) {
                KeyboardBindingScreenState.Loading -> it
                is KeyboardBindingScreenState.Success -> it.copy(dialog = null)
            }
        }
    }
}

sealed interface KeybindDialog {
    data object Create : KeybindDialog
    data class Rename(val keyCode:Int, val keybind: KeybindAction) : KeybindDialog
    data class Delete(val keyCode:Int) : KeybindDialog
}

sealed interface KeybindEvent {
    sealed class LocalizedMessage(val stringRes: StringResource) : KeybindEvent
    data object InternalError : LocalizedMessage(MR.strings.internal_error)
}

sealed interface KeyboardBindingScreenState {

    @Immutable
    data object Loading : KeyboardBindingScreenState

    @Immutable
    data class Success(
        val currentKeybindings: MutableMap<Int, KeybindAction>,
        val dialog: KeybindDialog? = null,
    ) : KeyboardBindingScreenState {

        val isEmpty: Boolean
            get() = currentKeybindings.isEmpty()
    }
}
