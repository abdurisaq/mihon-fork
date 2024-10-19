package eu.kanade.presentation.more.settings.screen.reader.keybind.model
import android.view.KeyEvent

data class KeybindAction(
    val shortClickFunctionName: String = "N/A",
    val shortClickParameter: Float = 0f,
    val longClickFunctionName: String = "N/A",
    val longClickParameter: Float = 0f,
    val longReleaseFunctionName: String = "stopContinuousScroll",
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

        fun emptyKeybindings(): Map<Int, KeybindAction> {
            return mapOf()
        }
        fun bindableFunctions(): Array<String> {
            return arrayOf("N/A","moveBackward","moveForward","toggleMenu","smoothScrollBackward","smoothScrollForward")
        }
        // Method to provide default keybindings
        fun defaultKeybindings(): Map<Int, KeybindAction> {
            val defaultClickAmount = 0.75f
            val defaultScrollAmount = 1f

            return mapOf(
                KeyEvent.KEYCODE_W to KeybindAction("moveBackward", defaultClickAmount, "smoothScrollBackward", defaultScrollAmount, "stopContinuousScroll" ),
                KeyEvent.KEYCODE_A to KeybindAction("moveBackward", defaultClickAmount,"smoothScrollBackward", defaultScrollAmount, "stopContinuousScroll"),

                KeyEvent.KEYCODE_DPAD_UP to KeybindAction("moveBackward", defaultClickAmount, "smoothScrollBackward", defaultScrollAmount, "stopContinuousScroll" ),
                KeyEvent.KEYCODE_DPAD_LEFT to KeybindAction("moveBackward", defaultClickAmount, "smoothScrollBackward", defaultScrollAmount, "stopContinuousScroll" ),

                KeyEvent.KEYCODE_S to KeybindAction("moveForward", defaultClickAmount, "smoothScrollForward", defaultScrollAmount, "stopContinuousScroll" ),
                KeyEvent.KEYCODE_D to KeybindAction("moveForward", defaultClickAmount, "smoothScrollForward", defaultScrollAmount, "stopContinuousScroll"),

                KeyEvent.KEYCODE_DPAD_DOWN to KeybindAction("moveForward", defaultClickAmount, "smoothScrollForward", defaultScrollAmount, "stopContinuousScroll" ),
                KeyEvent.KEYCODE_DPAD_RIGHT to KeybindAction("moveForward", defaultClickAmount, "smoothScrollForward", defaultScrollAmount, "stopContinuousScroll"),

                KeyEvent.KEYCODE_VOLUME_DOWN to KeybindAction("moveForward", defaultClickAmount),

                KeyEvent.KEYCODE_MENU to KeybindAction("toggleMenu"),


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

//un-optimal to loop through everything everytime i want a keycode name, maybe make a map once every time
//and just keep it in a preference that can be queried
fun getKeyCodeName(keyCode: Int): String {
    val event = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
    val displayLabel = event.displayLabel
    if (displayLabel != ' ' && displayLabel != 0.toChar()) {
        return displayLabel.toString() // Return the display label if valid
    }

    val fields = KeyEvent::class.java.fields
    for (field in fields) {
        if (field.type == Int::class.javaPrimitiveType && field.getInt(null) == keyCode) {
            return field.name
        }
    }
    return "Unknown keycode"

}

