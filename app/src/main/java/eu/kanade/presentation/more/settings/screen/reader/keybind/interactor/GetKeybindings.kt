package eu.kanade.presentation.more.settings.screen.reader.keybind.interactor

import eu.kanade.presentation.more.settings.screen.reader.keybind.model.KeybindAction
import eu.kanade.tachiyomi.ui.reader.setting.ReaderPreferences
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class GetKeybindings {
    private val readerPreferences: ReaderPreferences
        get() = Injekt.get<ReaderPreferences>()

    // Return a Flow of keybindings (similar to subscribe())
    fun subscribe(): Map<Int, KeybindAction> {
        return readerPreferences.keybindings().get()
    }

}
