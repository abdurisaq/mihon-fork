package eu.kanade.presentation.more.settings.screen.reader.keybind.interactor

import eu.kanade.presentation.more.settings.KeybindAction
import eu.kanade.tachiyomi.ui.reader.setting.ReaderPreferences
import kotlinx.coroutines.flow.Flow
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class GetKeybinds {
    private val readerPreferences: ReaderPreferences
        get() = Injekt.get<ReaderPreferences>()

    // Return a Flow of keybindings (similar to subscribe())
    fun subscribe(): Map<Int, KeybindAction> {
        return readerPreferences.keybinds().get()
    }

    // Synchronously return the keybindings (similar to await())
    suspend fun await(): Map<Int, KeybindAction> {
        return readerPreferences.keybinds().get()
    }
}
