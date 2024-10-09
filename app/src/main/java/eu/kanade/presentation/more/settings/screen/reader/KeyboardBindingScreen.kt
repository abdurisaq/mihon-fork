package eu.kanade.presentation.more.settings.screen.reader


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.more.settings.KeybindAction
import eu.kanade.presentation.more.settings.screen.reader.components.KeybindCreateDialog
import eu.kanade.presentation.more.settings.screen.reader.components.KeybindDeleteDialog
import eu.kanade.presentation.more.settings.screen.reader.components.KeybindRebindDialog
import eu.kanade.presentation.more.settings.screen.reader.components.KeyboardBindingScreen
import eu.kanade.presentation.util.Screen
import eu.kanade.tachiyomi.ui.reader.setting.ReaderPreferences
import eu.kanade.tachiyomi.util.system.toast
import kotlinx.coroutines.flow.collectLatest
import tachiyomi.presentation.core.screens.LoadingScreen
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class KeyboardBindingScreen(
) : Screen() {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        val screenModel = rememberScreenModel { KeyboardBindingScreenModel() }
        val state by screenModel.state.collectAsState()


        if (state is KeyboardBindingScreenState.Loading) {
            LoadingScreen()
            return
        }

        val successState = state as KeyboardBindingScreenState.Success


        KeyboardBindingScreen(
            state = successState,
            onClickCreate = { screenModel.showDialog(KeybindDialog.Create) },
            onClickRename = { id, name -> screenModel.showDialog(KeybindDialog.Rename(id, name)) },
            onClickDelete = { screenModel.showDialog(KeybindDialog.Delete(it)) },
            navigateUp = navigator::pop,
        )

        when (val dialog = successState.dialog) {
            null -> {}
            KeybindDialog.Create -> {
                KeybindCreateDialog(
                    onDismissRequest = screenModel::dismissDialog,
                    onCreate = screenModel::createKeybind
                )
            }
            is KeybindDialog.Rename -> {

                KeybindRebindDialog(
                    onDismissRequest = screenModel::dismissDialog,
                    onRename = screenModel::rebindKeybind,
                    keycode = dialog.keyCode,
                    keybind = dialog.keybind
                )
            }
            is KeybindDialog.Delete -> {
                KeybindDeleteDialog(
                    onDismissRequest = screenModel::dismissDialog,
                    onDelete = { screenModel.deleteKeybind(dialog.keyCode) },
                    keybind = dialog.keyCode,
                )
            }


        }

        LaunchedEffect(Unit) {
            screenModel.events.collectLatest { event ->
                if (event is KeybindEvent.LocalizedMessage) {
                    context.toast(event.stringRes)
                }
            }
        }
    }
}


