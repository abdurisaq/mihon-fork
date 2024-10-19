package eu.kanade.presentation.more.settings.screen.reader.keybind.interactor

import eu.kanade.presentation.more.settings.screen.reader.keybind.model.KeybindAction
import eu.kanade.tachiyomi.ui.reader.setting.ReaderPreferences
import logcat.LogPriority
import tachiyomi.core.common.util.lang.withNonCancellableContext
import tachiyomi.core.common.util.system.logcat
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class CreateKeybind {



    suspend fun await(keyCode:Int, keyEvent: KeybindAction): Result = withNonCancellableContext {
        val mutableKeybind = Injekt.get<ReaderPreferences>().keybindings().get().toMutableMap()
        try {
            mutableKeybind[keyCode] = keyEvent
            Injekt.get<ReaderPreferences>().keybindings().set(mutableKeybind)
            Result.Success
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            Result.InternalError(e)
        }
    }

    sealed interface Result {
        data object Success : Result
        data class InternalError(val error: Throwable) : Result
    }
}
