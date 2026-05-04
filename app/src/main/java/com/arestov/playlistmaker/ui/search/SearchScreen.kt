package com.arestov.playlistmaker.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.ui.compose.theme.YpBlack
import com.arestov.playlistmaker.domain.search.model.Track
import com.arestov.playlistmaker.ui.compose.components.TrackItem
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onTrackClick: () -> Unit,
    viewModel: SearchViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) { viewModel.start() }

    val state by viewModel.screenStateLiveData
        .observeAsState(initial = SearchScreenState.EmptyHistory)

    var query by remember { mutableStateOf(viewModel.currentQuery) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboard?.show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.search),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
            )
        )

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.onSearchTextChanged(it)
            },
            singleLine = true,
            placeholder = {
                Text(stringResource(R.string.search))
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search_input),
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    Icon(
                        painter = painterResource(R.drawable.ic_clear_input),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            query = ""
                            viewModel.onSearchTextChanged("")
                            keyboard?.hide()
                        }
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = YpBlack,
                unfocusedTextColor = YpBlack,
                focusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .focusRequester(focusRequester)
        )

        when (val s = state) {
            is SearchScreenState.Loading -> LoadingBlock()
            is SearchScreenState.Content -> {
                LaunchedEffect(s.tracks) { viewModel.setFavorite(s.tracks) }
                TrackList(
                    tracks = s.tracks,
                    onTrackClick = { track ->
                        keyboard?.hide()
                        viewModel.addHistoryTrack(track)
                        onTrackClick()
                    }
                )
            }

            is SearchScreenState.EmptyResult -> InfoBlock(
                imageRes = R.drawable.ic_nothing_found,
                text = stringResource(R.string.nothing_found)
            )

            is SearchScreenState.NetworkError -> InfoBlock(
                imageRes = R.drawable.ic_network_problem,
                text = stringResource(R.string.network_problem),
                buttonText = stringResource(R.string.update),
                onButtonClick = { viewModel.retrySearch() }
            )

            is SearchScreenState.HistoryContent -> {
                LaunchedEffect(s.historyTracks) {
                    viewModel.setFavorite(s.historyTracks)
                }
                HistoryBlock(
                    tracks = s.historyTracks,
                    onTrackClick = { track ->
                        keyboard?.hide()
                        viewModel.addHistoryTrack(track)
                        onTrackClick()
                    },
                    onClearHistory = { viewModel.clearHistoryTrack() }
                )
            }

            is SearchScreenState.EmptyHistory -> Unit
        }
    }
}

@Composable
private fun TrackList(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 24.dp)
    ) {
        items(items = tracks, key = { it.trackId }) { track ->
            TrackItem(track = track, onClick = { onTrackClick(track) })
        }
    }
}

@Composable
private fun HistoryBlock(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onClearHistory: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.your_history),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 44.dp, bottom = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 24.dp)
        ) {
            items(items = tracks, key = { it.trackId }) { track ->
                TrackItem(track = track, onClick = { onTrackClick(track) })
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onClearHistory,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background,
                )
            ) {
                Text(stringResource(R.string.clear_history))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun LoadingBlock() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 148.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(44.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun InfoBlock(
    imageRes: Int,
    text: String,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(110.dp))
        Icon(
            painter = painterResource(imageRes),
            contentDescription = null,
            tint = androidx.compose.ui.graphics.Color.Unspecified,
            modifier = Modifier.size(120.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        if (buttonText != null && onButtonClick != null) {
            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background,
                )
            ) {
                Text(buttonText)
            }
        }
    }
}
