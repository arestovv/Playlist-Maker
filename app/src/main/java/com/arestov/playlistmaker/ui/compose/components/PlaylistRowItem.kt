package com.arestov.playlistmaker.ui.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.domain.search.model.Playlist
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import java.io.File

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PlaylistRowItem(
    playlist: Playlist,
    onClick: () -> Unit,
) {
    val model: Any =
        if (playlist.imageUri.isNotEmpty()) File(playlist.imageUri) else R.drawable.ic_album_placeholder

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        GlideImage(
            model = model,
            contentDescription = null,
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp)),
            loading = placeholder(R.drawable.ic_album_placeholder),
            failure = placeholder(R.drawable.ic_album_placeholder),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1
            )
            Text(
                text = pluralStringResource(
                    R.plurals.tracks_count,
                    playlist.trackCount,
                    playlist.trackCount
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
            )
        }
    }
}
