package eu.kanade.presentation.more.settings.screen.reader.keybind.interactor

import eu.kanade.presentation.more.settings.KeybindAction
import eu.kanade.presentation.more.settings.screen.reader.keybind.interactor.CreateKeybind.Result
import eu.kanade.tachiyomi.ui.reader.setting.ReaderPreferences
import logcat.LogPriority
import tachiyomi.core.common.util.lang.withNonCancellableContext
import tachiyomi.core.common.util.system.logcat
import tachiyomi.domain.category.model.CategoryUpdate
import tachiyomi.domain.category.repository.CategoryRepository
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get


class DeleteKeybind {
    private var mutableKeybind = Injekt.get<ReaderPreferences>().keybinds().get().toMutableMap()

    suspend fun await(keyCode:Int) = withNonCancellableContext {


        try {
            mutableKeybind.remove(keyCode)
            Injekt.get<ReaderPreferences>().keybinds().set(mutableKeybind)
            println("count of keybinds left ${mutableKeybind.size}")
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
