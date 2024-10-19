package eu.kanade.presentation.more.settings.screen.reader.components


import android.view.KeyEvent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import eu.kanade.presentation.more.settings.screen.reader.keybind.model.KeybindAction
import eu.kanade.presentation.more.settings.screen.reader.keybind.model.getKeyCodeName
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun KeyboardBindingContent(
    lazyListState: LazyListState,
    paddingValues: PaddingValues,
    keybindings: MutableMap<Int, KeybindAction>,
    onClickDelete: (Int) -> Unit,
    onClickRename: (Int, KeybindAction) -> Unit,
    modifier: Modifier = Modifier,
) {


    LazyColumn(
        state = lazyListState,
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
        modifier = modifier,
    ) {
        for(key in keybindings.keys){
            item{
                keybindings[key]?.let {
                    KeyboardBindingListItem(
                        keyCode = key,
                        keybind =it,
                        onDelete = onClickDelete,
                        onRename = onClickRename
                    )
                }

            }
        }
    }
}




@Composable
private fun KeyboardBindingListItem(
    keyCode: Int,
    keybind: KeybindAction,
    onRename: (Int, KeybindAction) -> Unit,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {

    val keyEventName = getKeyCodeName(keyCode)

    ElevatedCard(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRename(keyCode, keybind) }
                .padding(
                    start = MaterialTheme.padding.medium,
                    top = MaterialTheme.padding.medium,
                    end = MaterialTheme.padding.medium,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = Icons.AutoMirrored.Outlined.Label, contentDescription = null)
            Text(
                text = keyEventName,
                modifier = Modifier.padding(start = MaterialTheme.padding.medium),
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {


            IconButton(
                onClick = { onRename(keyCode, keybind) },
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = stringResource(MR.strings.action_copy_to_clipboard),
                )
            }

            IconButton(onClick = { onDelete(keyCode) }) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(MR.strings.action_delete),
                )
            }
        }
    }
}
