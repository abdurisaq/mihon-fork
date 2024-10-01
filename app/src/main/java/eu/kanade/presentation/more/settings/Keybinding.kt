package eu.kanade.presentation.more.settings
import android.view.KeyEvent

data class KeybindAction(
    val shortClickFunctionName: String = "N/A",
    val shortClickParameter: Float = 0.75f,
    val longClickFunctionName: String = "N/A",
    val longClickParameter: Float = 0f,
    val longReleaseFunctionName: String = "N/A",
    val longReleaseParameter: Float = 0f,
) {
    fun serialize(): String {
        return "${shortClickFunctionName},${shortClickParameter}," +
            "$longClickFunctionName,$longClickParameter," +
            "$longReleaseFunctionName,$longReleaseParameter"
    }

    companion object {
        fun deserialize(serialized: String): KeybindAction {
            val parts = serialized.split(",")
            return KeybindAction(
                shortClickFunctionName = parts.getOrNull(0) ?: "N/A",
                shortClickParameter = parts.getOrNull(1)?.toFloatOrNull() ?: 0f,
                longClickFunctionName = parts.getOrNull(2) ?: "N/A",
                longClickParameter = parts.getOrNull(3)?.toFloatOrNull() ?: 0f,
                longReleaseFunctionName = parts.getOrNull(4) ?: "N/A",
                longReleaseParameter = parts.getOrNull(5)?.toFloatOrNull() ?: 0f
            )
        }

        // Method to provide default keybindings
        fun defaultKeybindings(): Map<Int, KeybindAction> {
            return mapOf(
                KeyEvent.KEYCODE_W to KeybindAction("scrollUp", 1.0f, "startContinuousScroll", -2.0f, "stopContinuousScroll" ),
                KeyEvent.KEYCODE_S to KeybindAction("scrollDown", 1.0f, "startContinuousScroll", 0.2f, "stopContinuousScroll" ),
                KeyEvent.KEYCODE_D to KeybindAction("scrollDown", 1.0f, "startContinuousScroll", 0.8f, "stopContinuousScroll"),
                KeyEvent.KEYCODE_A to KeybindAction("scrollUp", 1.0f,"startContinuousScroll", -1.0f, "stopContinuousScroll")
            )
        }
    }
}

sealed interface KeybindActionSerializer {
    companion object {
        fun serializeKeybindActionMap(map: Map<Int, KeybindAction>): String {
            return map.entries.joinToString(";") { "${it.key}:${it.value.serialize()}" }
        }

        fun deserializeKeybindActionMap(serialized: String): Map<Int, KeybindAction> {
            return serialized.split(";").associate { entry ->
                val (key, value) = entry.split(":")
                key.toInt() to KeybindAction.deserialize(value)
            }
        }
    }
}



