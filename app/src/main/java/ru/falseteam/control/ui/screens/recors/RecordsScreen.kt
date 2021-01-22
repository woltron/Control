package ru.falseteam.control.ui.screens.recors

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.util.Pools
import androidx.navigation.NavController
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import ru.falseteam.control.R
import ru.falseteam.control.di.kodeinViewModel
import ru.falseteam.control.ui.PrimaryButton
import ru.falseteam.control.ui.red900

@Composable
fun RecordsScreen(navController: NavController, viewModel: RecordsViewModel = kodeinViewModel()) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(scaffoldState, viewModel) },
        drawerContent = { FilterContent(viewModel) },
    ) { Content(viewModel) }
}

@Composable
private fun Content(viewModel: RecordsViewModel) {
    when (val state = viewModel.state.collectAsState().value) {
        RecordsState.Loading -> LoadingScreen()
        is RecordsState.Error -> ErrorScreen(state, viewModel)
        is RecordsState.ShowResult -> ShowResultScreen(state, viewModel)
    }
}

@Composable
private fun TopBar(scaffoldState: ScaffoldState, viewModel: RecordsViewModel) {
    TopAppBar {
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = { scaffoldState.drawerState.open() }
        ) {
            Icon(vectorResource(id = R.drawable.ic_filter))
        }
        IconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = { viewModel.forceUpdate() }
        ) {
            Icon(vectorResource(id = R.drawable.ic_refresh))
        }
    }
}

@Composable
private fun FilterContent(viewModel: RecordsViewModel) {
    val state = viewModel.filterState.collectAsState().value
    Column(modifier = Modifier.padding(16.dp)) {
        Row {
            Text(text = "Только сохраненные", modifier = Modifier.weight(1f))
            Checkbox(
                checked = state.isOnlySaved,
                onCheckedChange = { viewModel.updateFilterModel(state.copy(isOnlySaved = !state.isOnlySaved)) }
            )
        }

        Row {
            Text(text = "Только c именем", modifier = Modifier.weight(1f))
            Checkbox(
                checked = state.isOnlyNamed,
                onCheckedChange = { viewModel.updateFilterModel(state.copy(isOnlyNamed = !state.isOnlyNamed)) }
            )

        }

    }
}

@Composable
private fun LoadingScreen() {
    //TODO make global function like this
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
private fun ErrorScreen(state: RecordsState.Error, viewModel: RecordsViewModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                text = "Не удалось загрузить записи с сервера",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Ошибка: ${state.error}",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            PrimaryButton(
                text = "Retry",
                onClick = { viewModel.forceUpdate() },
                modifier = Modifier
                    .padding(0.dp, 16.dp, 0.dp, 0.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun ShowResultScreen(state: RecordsState.ShowResult, viewModel: RecordsViewModel) {
    RecordsList(state.records, viewModel)
}

@Composable
private fun RecordsList(records: List<RecordUiModel>, viewModel: RecordsViewModel) {
    val context = AmbientContext.current
    val playerCache = remember {
        PlayerCache(context)
    }

    LazyColumn {
        items(records) { record ->
            RecordCard(record = record, playerCache, viewModel)
        }
    }
}

@Composable
private fun RecordCard(
    record: RecordUiModel,
    playerCache: PlayerCache,
    viewModel: RecordsViewModel
) {
    Card(
        shape = RectangleShape,
        elevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 6.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row {
                Text(
                    text = "Без названия...",
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(16.dp, 0.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(vectorResource(id = R.drawable.ic_edit))
                }
            }

            VideoRecord(record.uri, playerCache)

            Row(
                modifier = Modifier.padding(16.dp, 8.dp)
            ) {
                Text(
                    text = record.cameraName ?: "Unknown camera",
                    modifier = Modifier.weight(1f)
                )
                Text(text = record.date)
            }

            Divider(modifier = Modifier.padding(8.dp, 0.dp))
            Row {
                IconButton(onClick = { viewModel.setKeepForever(record) }) {
                    if (record.keepForever) Icon(
                        vectorResource(id = R.drawable.ic_star_filled),
                        tint = Color.Unspecified
                    )
                    else Icon(vectorResource(id = R.drawable.ic_star))
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { viewModel.deleteRecord(record.id) }) {
                    Icon(vectorResource(id = R.drawable.ic_delete), tint = red900)
                }
            }
        }
    }
}


@Composable
private fun VideoRecord(uri: Uri, playerCache: PlayerCache) {
    Surface {
        val player = remember {
            playerCache.acquire()
        }
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f),
            viewBlock = {
                player
            }) {
            it.player?.apply {
                this as SimpleExoPlayer
                setMediaSource(playerCache.createMedia(uri))
                prepare()
            }
        }
    }
}

private class PlayerCache(
    private val context: Context
) {
    private val pool = Pools.SimplePool<MyPlayer>(10)
    private val mediaFactory by lazy {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, context.packageName)
        )

        ProgressiveMediaSource.Factory(dataSourceFactory)
    }

    fun createMedia(uri: Uri): ProgressiveMediaSource {
        val mediaItem = MediaItem.Builder().setUri(uri).build()
        return mediaFactory.createMediaSource(mediaItem)
    }

    fun acquire(): MyPlayer = pool.acquire() ?: MyPlayer(context).apply {
        player = SimpleExoPlayer.Builder(context).build()
    }

    fun release(player: MyPlayer) = pool.release(player)
}

private class MyPlayer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PlayerView(context, attrs, defStyleAttr) {
    var cache: PlayerCache? = null

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        player?.stop()
        cache?.release(this)
    }
}