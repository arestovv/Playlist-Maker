package com.arestov.playlistmaker.ui.player

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.domain.search.model.Track
import com.arestov.playlistmaker.ui.compose.components.PlaylistRowItem
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun PlayerScreen(
    onBack: () -> Unit,
    onCreatePlaylistClick: () -> Unit,
    viewModel: PlayerViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val track = remember { viewModel.getTrack() }

    BindPlayerService(context = context, track = track, viewModel = viewModel)
    HandleNotificationPermission(context = context)
    HandleLifecycle(context = context, viewModel = viewModel)

    val playerState by viewModel.playerStateProgressLiveData.observeAsState()
    val screenState by viewModel.stateScreenLiveData.observeAsState()
    val isPlaying = playerState is PlayerStateProgress.Playing
    val timer = playerState?.progress ?: PlayerViewModel.DEFAULT_TIMER

    val isFavorite = (screenState as? PlayerScreenState.Favorite)?.isFavorite ?: track.isFavorite
    val playlists = (screenState as? PlayerScreenState.Content)?.playlists ?: emptyList()

    var showAddSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(screenState) {
        val s = screenState
        if (s is PlayerScreenState.TrackAddedToPlaylist) {
            val msg = if (s.isAdded)
                context.getString(R.string.added_to_playlist, s.playlist.name)
            else
                context.getString(R.string.track_already_added_to_playlist, s.playlist.name)
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            if (s.isAdded) {
                scope.launch { sheetState.hide() }
                showAddSheet = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp)
        ) {
            GlideImage(
                model = track.artworkUrl512,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 26.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp)),
                loading = placeholder(R.drawable.im_album_placeholder),
                failure = placeholder(R.drawable.im_album_placeholder),
            )

            Text(
                text = track.trackName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                textAlign = TextAlign.Center,
            )
            Text(
                text = track.artistName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                IconButton(onClick = { showAddSheet = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_button_add_to_playlist),
                        contentDescription = null,
                        tint = androidx.compose.ui.graphics.Color.Unspecified,
                        modifier = Modifier.size(48.dp)
                    )
                }
                IconButton(
                    onClick = { viewModel.onPlayButtonClicked() },
                    modifier = Modifier.size(100.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            if (isPlaying) R.drawable.ic_button_pause
                            else R.drawable.ic_button_play
                        ),
                        contentDescription = null,
                        tint = androidx.compose.ui.graphics.Color.Unspecified,
                        modifier = Modifier.size(100.dp)
                    )
                }
                IconButton(onClick = { viewModel.onFavoriteClicked(track) }) {
                    Icon(
                        painter = painterResource(
                            if (isFavorite) R.drawable.ic_button_added_to_favorite
                            else R.drawable.ic_button_add_to_favorite
                        ),
                        contentDescription = null,
                        tint = androidx.compose.ui.graphics.Color.Unspecified,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Text(
                text = timer,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(17.dp))

            InfoRow(stringResource(R.string.duration), track.trackTimeSeconds)
            if (track.collectionName.isNotEmpty()) {
                InfoRow(stringResource(R.string.album), track.collectionName)
            }
            if (track.releaseYear.isNotEmpty()) {
                InfoRow(stringResource(R.string.year), track.releaseYear)
            }
            InfoRow(stringResource(R.string.genre), track.primaryGenreName)
            InfoRow(stringResource(R.string.country), track.country)
        }
    }

    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            Column(modifier = Modifier.padding(bottom = 24.dp)) {
                Text(
                    text = stringResource(R.string.add_to_playlist),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 18.dp),
                    textAlign = TextAlign.Center,
                )
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Button(onClick = onCreatePlaylistClick) {
                        Text(stringResource(R.string.new_playlist))
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    items(items = playlists, key = { it.id }) { playlist ->
                        PlaylistRowItem(
                            playlist = playlist,
                            onClick = { viewModel.addTrackToPlaylist(playlist, track) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun BindPlayerService(
    context: Context,
    track: Track,
    viewModel: PlayerViewModel,
) {
    val serviceConnection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as PlayerService.PlayerBinder
                viewModel.setPlayerControl(binder.getService())
            }

            override fun onServiceDisconnected(name: ComponentName?) {}
        }
    }

    DisposableEffect(Unit) {
        val intent = Intent(context, PlayerService::class.java).apply {
            putExtra(PlayerService.EXTRA_URL, track.previewUrl)
            putExtra(PlayerService.EXTRA_TRACK_NAME, track.trackName)
            putExtra(PlayerService.EXTRA_ARTIST_NAME, track.artistName)
        }
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        onDispose { context.unbindService(serviceConnection) }
    }
}

@Composable
private fun HandleNotificationPermission(context: Context) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@Composable
private fun HandleLifecycle(context: Context, viewModel: PlayerViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (!hasNotificationPermission(context)) return@LifecycleEventObserver
            when (event) {
                Lifecycle.Event.ON_START -> viewModel.onUiForeground()
                Lifecycle.Event.ON_STOP -> viewModel.onUiBackground()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

private fun hasNotificationPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED
}
