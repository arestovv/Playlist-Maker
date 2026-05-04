package com.arestov.playlistmaker.ui.media

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.ui.compose.components.PlaylistGridItem
import com.arestov.playlistmaker.ui.compose.components.TrackItem
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(
    ExperimentalMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class,
)
@Composable
fun MediaScreen(
    onTrackClick: () -> Unit,
    onCreatePlaylistClick: () -> Unit,
    onPlaylistClick: (Int) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.media),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
            )
        )

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.background,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = MaterialTheme.colorScheme.onBackground,
                )
            },
        ) {
            Tab(
                selected = pagerState.currentPage == 0,
                selectedContentColor = MaterialTheme.colorScheme.onBackground,
                unselectedContentColor = MaterialTheme.colorScheme.outline,
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(0) }
                },
                text = { Text(stringResource(R.string.favorite_tracks)) }
            )
            Tab(
                selected = pagerState.currentPage == 1,
                selectedContentColor = MaterialTheme.colorScheme.onBackground,
                unselectedContentColor = MaterialTheme.colorScheme.outline,
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(1) }
                },
                text = { Text(stringResource(R.string.playlists)) }
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            when (page) {
                0 -> FavoriteTab(onTrackClick = onTrackClick)
                1 -> PlaylistsTab(
                    onCreatePlaylistClick = onCreatePlaylistClick,
                    onPlaylistClick = onPlaylistClick,
                )
            }
        }
    }
}

@Composable
private fun FavoriteTab(
    onTrackClick: () -> Unit,
    viewModel: MediaViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) { viewModel.start() }

    val state by viewModel.screenStateLiveData
        .observeAsState(initial = FavoriteScreenState.Empty)

    when (val s = state) {
        is FavoriteScreenState.Content -> {
            LaunchedEffect(s.tracks) { viewModel.setFavorite(s.tracks) }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items = s.tracks, key = { it.trackId }) { track ->
                    TrackItem(
                        track = track,
                        onClick = {
                            viewModel.addHistoryTrack(track)
                            onTrackClick()
                        }
                    )
                }
            }
        }

        is FavoriteScreenState.Empty -> EmptyMediaPlaceholder(
            text = stringResource(R.string.your_media_is_empty)
        )
    }
}

@Composable
private fun PlaylistsTab(
    onCreatePlaylistClick: () -> Unit,
    onPlaylistClick: (Int) -> Unit,
    viewModel: PlaylistViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) { viewModel.start() }

    val state by viewModel.screenStateLiveData
        .observeAsState(initial = PlaylistScreenState.Empty)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onCreatePlaylistClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.background,
            ),
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text(stringResource(R.string.new_playlist))
        }

        when (val s = state) {
            is PlaylistScreenState.Content -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                items(items = s.playlists, key = { it.id }) { playlist ->
                    PlaylistGridItem(
                        playlist = playlist,
                        onClick = { onPlaylistClick(playlist.id) }
                    )
                }
            }

            is PlaylistScreenState.Empty -> EmptyMediaPlaceholder(
                text = stringResource(R.string.you_not_created_playlist)
            )
        }
    }
}

@Composable
private fun EmptyMediaPlaceholder(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 106.dp, start = 24.dp, end = 24.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_nothing_found),
                contentDescription = null,
                tint = androidx.compose.ui.graphics.Color.Unspecified,
                modifier = Modifier.size(120.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }
}
