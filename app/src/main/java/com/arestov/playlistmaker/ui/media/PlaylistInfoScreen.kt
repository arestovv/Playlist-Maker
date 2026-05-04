package com.arestov.playlistmaker.ui.media

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.ui.compose.theme.YpBlack
import com.arestov.playlistmaker.ui.compose.theme.YpLightGray
import com.arestov.playlistmaker.domain.search.model.Playlist
import com.arestov.playlistmaker.domain.search.model.Track
import com.arestov.playlistmaker.ui.compose.components.TrackItem
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun PlaylistInfoScreen(
    playlistId: Int,
    onBack: () -> Unit,
    onTrackClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    viewModel: PlaylistInfoViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(playlistId) { viewModel.loadPlaylist(playlistId) }

    val state by viewModel.screenStateLiveData.observeAsState()

    val playlist: Playlist? = (state as? PlaylistInfoScreenState.Content)?.playlist
    val tracks: List<Track> = (state as? PlaylistInfoScreenState.Content)?.tracks ?: emptyList()

    LaunchedEffect(state) {
        if (state is PlaylistInfoScreenState.Toast) {
            Toast.makeText(
                context,
                context.getString(R.string.playlist_no_shareable_tracks),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    var showMenu by remember { mutableStateOf(false) }
    var trackToDelete by remember { mutableStateOf<Track?>(null) }
    var showDeletePlaylist by remember { mutableStateOf(false) }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 240.dp,
        sheetContainerColor = MaterialTheme.colorScheme.background,
        containerColor = MaterialTheme.colorScheme.background,
        sheetContent = {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(items = tracks, key = { it.trackId }) { track ->
                    TrackItem(
                        track = track,
                        onClick = {
                            viewModel.saveTrack(track)
                            onTrackClick()
                        },
                        onLongClick = { trackToDelete = track }
                    )
                }
            }
        }
    ) { padding ->
        PlaylistHeader(
            playlist = playlist,
            tracks = tracks,
            paddingValues = padding,
            onBack = onBack,
            onShare = { viewModel.sharePlaylist(quantityTracks(context, tracks.size)) },
            onMenu = { showMenu = true }
        )
    }

    if (showMenu) {
        val menuSheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showMenu = false },
            sheetState = menuSheetState,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            MenuSheetContent(
                playlist = playlist,
                trackCount = tracks.size,
                onShare = {
                    showMenu = false
                    viewModel.sharePlaylist(quantityTracks(context, tracks.size))
                },
                onEdit = {
                    showMenu = false
                    onEditClick(playlistId)
                },
                onDelete = {
                    showMenu = false
                    showDeletePlaylist = true
                },
            )
        }
    }

    val track = trackToDelete
    if (track != null) {
        AlertDialog(
            onDismissRequest = { trackToDelete = null },
            title = { Text(stringResource(R.string.delete_track_title)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTrack(track)
                    trackToDelete = null
                }) {
                    Text(stringResource(R.string.yes_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { trackToDelete = null }) {
                    Text(stringResource(R.string.no_button))
                }
            }
        )
    }

    if (showDeletePlaylist && playlist != null) {
        AlertDialog(
            onDismissRequest = { showDeletePlaylist = false },
            title = {
                Text(stringResource(R.string.delete_playlist_title, playlist.name))
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeletePlaylist = false
                    scope.launch {
                        viewModel.deletePlaylist()
                        onBack()
                    }
                }) {
                    Text(stringResource(R.string.yes_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePlaylist = false }) {
                    Text(stringResource(R.string.no_button))
                }
            }
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun PlaylistHeader(
    playlist: Playlist?,
    tracks: List<Track>,
    paddingValues: PaddingValues,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onMenu: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            if (playlist != null && playlist.imageUri.isNotEmpty()) {
                GlideImage(
                    model = File(playlist.imageUri),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = placeholder(R.drawable.im_album_placeholder),
                    failure = placeholder(R.drawable.im_album_placeholder),
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.im_album_placeholder),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.fillMaxSize()
                )
            }

            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Text(
            text = playlist?.name.orEmpty(),
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp)
        )

        if (!playlist?.description.isNullOrEmpty()) {
            Text(
                text = playlist!!.description,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )
        }

        Text(
            text = playlistInfoLine(LocalContext.current, tracks),
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 8.dp, top = 16.dp)
        ) {
            IconButton(
                onClick = onShare,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_share_playlist),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            IconButton(onClick = onMenu,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_menu),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun MenuSheetContent(
    playlist: Playlist?,
    trackCount: Int,
    onShare: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            if (playlist != null && playlist.imageUri.isNotEmpty()) {
                GlideImage(
                    model = File(playlist.imageUri),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(45.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    loading = placeholder(R.drawable.ic_album_placeholder),
                    failure = placeholder(R.drawable.ic_album_placeholder),
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_album_placeholder),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(45.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = playlist?.name.orEmpty(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = pluralStringResource(R.plurals.tracks_count, trackCount, trackCount),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
        }

        MenuItem(
            text = stringResource(R.string.share),
            onClick = onShare,
            top = 30.dp,
            bottom = 22.dp
        )

        MenuItem(
            text = stringResource(R.string.edit_info),
            onClick = onEdit,
            top = 22.dp,
            bottom = 22.dp
        )

        MenuItem(
            text = stringResource(R.string.delete_playlist),
            onClick = onDelete,
            top = 22.dp,
            bottom = 80.dp
        )
    }
}

@Composable
private fun MenuItem(
    text: String,
    onClick: () -> Unit,
    top: Dp,
    bottom: Dp
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 12.dp, top = top, bottom = bottom)
    )
}

private fun quantityTracks(context: android.content.Context, count: Int): String {
    return context.resources.getQuantityString(R.plurals.tracks_count, count, count)
}

private fun playlistInfoLine(context: android.content.Context, tracks: List<Track>): String {
    val totalSeconds = tracks.sumOf { track ->
        val parts = track.trackTimeSeconds.split(":")
        val minutes = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val seconds = parts.getOrNull(1)?.toIntOrNull() ?: 0
        minutes * 60 + seconds
    }
    val totalMinutes = totalSeconds / 60
    val minutesText = context.resources
        .getQuantityString(R.plurals.minutes_count, totalMinutes, totalMinutes)
    val tracksText = quantityTracks(context, tracks.size)
    return "$minutesText • $tracksText"
}
