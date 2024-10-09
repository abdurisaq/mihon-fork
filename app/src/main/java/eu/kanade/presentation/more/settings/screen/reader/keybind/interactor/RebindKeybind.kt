package eu.kanade.presentation.more.settings.screen.reader.keybind.interactor

import eu.kanade.presentation.more.settings.KeybindAction
import eu.kanade.tachiyomi.ui.reader.setting.ReaderPreferences
import logcat.LogPriority
import tachiyomi.core.common.util.lang.withNonCancellableContext
import tachiyomi.core.common.util.system.logcat
import tachiyomi.domain.category.repository.CategoryRepository
import tachiyomi.domain.library.service.LibraryPreferences
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class RebindKeybind {

    private var mutableKeybind = Injekt.get<ReaderPreferences>().keybinds().get().toMutableMap()

    suspend fun await(keyCode:Int, keyEvent: KeybindAction): Result = withNonCancellableContext {


        try {
            mutableKeybind[keyCode] = keyEvent
            Injekt.get<ReaderPreferences>().keybinds().set(mutableKeybind)
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
