package com.picoxr.destinationvideo.destination

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.Icon
import com.pico.spatial.ui.design.LocalContentColor
import com.pico.spatial.ui.design.PicoTheme
import com.pico.spatial.ui.design.Text
import com.pico.spatial.ui.design.windows.TabBar
import com.pico.spatial.ui.foundation.material.backgroundMaterial
import com.pico.spatial.ui.platform.Material
import com.pico.spatial.ui.platform.ViewPoint
import com.pico.spatial.ui.platform.containers.closeWindowContainer
import com.pico.spatial.ui.platform.containers.openWindowContainer
import com.picoxr.destinationvideo.R

private val AppBackground = Color.Transparent
private val CardBackground = Color(0x1F000000)
private val SelectedBackground = Color(0x33000000)
private val SecondaryText = Color(0xB3000000)
private val GlassPrimaryText = Color(0xE6000000)
private val GlassSecondaryText = Color(0xB3000000)
private val Accent = Color(0xFFB0EAFF)
private val DarkSurface = Color(0xFF1F1F1F)
private val DarkSurfaceVariant = Color(0xCC2A2A2A)
private val OnDarkSurface = Color(0xFFE8E8E8)
private val BorderColor = Color(0x80000000)
private val GlassBorder = Color(0x8A000000)

@Composable
private fun Modifier.spatialHoverEffect(scaleOnHover: Boolean = true): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val scale by animateFloatAsState(
        targetValue = if (scaleOnHover && isHovered) 1.04f else 1f,
        label = "destinationHoverScale",
    )
    return hoverable(interactionSource)
        .scale(scale)
}

private enum class DestinationTab(
    val title: String,
    @DrawableRes val iconRes: Int,
) {
    WATCH_NOW("立即观看", R.drawable.ic_tab_watch_now),
    LIBRARY("资料库", R.drawable.ic_tab_library),
    NEW("最新", R.drawable.ic_tab_new),
    FAVORITES("收藏", R.drawable.ic_tab_favorites),
    SEARCH("搜索", R.drawable.ic_tab_search),
}

private sealed interface DestinationRoute {
    data object Root : DestinationRoute
    data class Detail(val video: DestinationVideo) : DestinationRoute
    data class Category(val category: VideoCategory) : DestinationRoute
}

/**
 * Hosts the full Destination Video catalog, navigation, and player presentation.
 */
@Composable
fun DestinationApp() {
    val context = LocalContext.current
    val store = remember { DestinationStore(context.applicationContext) }
    var selectedTab by remember { mutableStateOf(DestinationTab.WATCH_NOW) }
    var route by remember {
        mutableStateOf<DestinationRoute>(
            DestinationReturnRoute.consumeDetail(context.applicationContext)
                ?.let(DestinationRoute::Detail)
                ?: DestinationRoute.Root,
        )
    }

    BackHandler(enabled = route != DestinationRoute.Root) {
        route = DestinationRoute.Root
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .onGloballyPositioned { coordinates ->
                // #region debug-point E:catalog-root-layout
                kotlin.concurrent.thread { runCatching { val connection = java.net.URL("http://127.0.0.1:7777/event").openConnection() as java.net.HttpURLConnection; connection.requestMethod = "POST"; connection.doOutput = true; connection.setRequestProperty("Content-Type", "application/json"); connection.outputStream.use { it.write(org.json.JSONObject().put("sessionId", "studio-scene-mismatch").put("runId", "post-fix").put("hypothesisId", "E").put("location", "DestinationApp:rootLayout").put("msg", "[DEBUG] Catalog root measured").put("data", org.json.JSONObject().put("widthPx", coordinates.size.width).put("heightPx", coordinates.size.height).put("density", context.resources.displayMetrics.density).put("background", "#1C1C1C")).toString().toByteArray()) }; connection.responseCode } }
                // #endregion
            },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (val currentRoute = route) {
                DestinationRoute.Root -> when (selectedTab) {
                    DestinationTab.WATCH_NOW -> WatchNowView(
                        upNextVideos = store.upNextVideos(),
                        onVideoSelected = { route = DestinationRoute.Detail(it) },
                        onCategorySelected = { route = DestinationRoute.Category(it) },
                    )
                    DestinationTab.LIBRARY -> LibraryView(
                        onVideoSelected = { route = DestinationRoute.Detail(it) },
                    )
                    DestinationTab.NEW -> EmptyTabView("最新内容会显示在这里。")
                    DestinationTab.FAVORITES -> EmptyTabView("收藏的影片会显示在这里。")
                    DestinationTab.SEARCH -> SearchView(
                        onVideoSelected = { route = DestinationRoute.Detail(it) },
                    )
                }
                is DestinationRoute.Detail -> DetailView(
                    video = currentRoute.video,
                    isUpNext = store.isUpNext(currentRoute.video.id),
                    onBack = { route = DestinationRoute.Root },
                    onPlay = {
                        context.openWindowContainer(
                            DESTINATION_PLAYER_WINDOW_ID,
                            bundle = Bundle().apply {
                                putInt(DESTINATION_PLAYER_VIDEO_ID, currentRoute.video.id)
                            },
                        )
                        context.closeWindowContainer(DESTINATION_HOME_WINDOW_ID)
                    },
                    onToggleUpNext = { store.toggleUpNext(currentRoute.video.id) },
                )
                is DestinationRoute.Category -> CategoryView(
                    category = currentRoute.category,
                    onBack = { route = DestinationRoute.Root },
                    onVideoSelected = { route = DestinationRoute.Detail(it) },
                )
            }
        }
        DestinationSidebar(
            selectedTab = selectedTab,
            onSelect = { tab ->
                selectedTab = tab
                route = DestinationRoute.Root
            },
        )
    }
}

/**
 * Displays the persistent PICO Spatial UI tab bar.
 */
@Composable
private fun DestinationSidebar(
    selectedTab: DestinationTab,
    onSelect: (DestinationTab) -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current
    val isRTL = remember(layoutDirection) { layoutDirection == LayoutDirection.Rtl }

    TabBar(
        followViewpoints = setOf(
            ViewPoint.Front,
            if (isRTL) ViewPoint.Right else ViewPoint.Left,
        ),
    ) {
        DestinationTab.entries.forEach { tab ->
            item(
                selected = tab == selectedTab,
                onClick = { onSelect(tab) },
                mainContent = {
                    Icon(
                        painter = painterResource(tab.iconRes),
                        contentDescription = tab.title,
                        tint = LocalContentColor.current,
                    )
                },
                supportContent = { Text(text = tab.title) },
            )
        }
    }
}

/**
 * Displays the hero, featured videos, categories, and Up Next queue.
 */
@Composable
private fun WatchNowView(
    upNextVideos: List<DestinationVideo>,
    onVideoSelected: (DestinationVideo) -> Unit,
    onCategorySelected: (VideoCategory) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 50.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            HeroView(
                video = DestinationCatalog.heroVideo,
                onDetails = { onVideoSelected(DestinationCatalog.heroVideo) },
            )
        }
        item {
            VideoSection(
                title = "精选",
                videos = DestinationCatalog.featuredVideos,
                showDetails = true,
                onVideoSelected = onVideoSelected,
            )
        }
        item {
            CategorySection(
                title = "合集",
                categories = DestinationCatalog.collectionCategories,
                onCategorySelected = onCategorySelected,
            )
        }
        item {
            CategorySection(
                title = "动画",
                categories = DestinationCatalog.animationCategories,
                onCategorySelected = onCategorySelected,
            )
        }
        if (upNextVideos.isNotEmpty()) {
            item {
                VideoSection(
                    title = "接着观看",
                    videos = upNextVideos,
                    showDetails = false,
                    onVideoSelected = onVideoSelected,
                )
            }
        }
    }
}

/**
 * Displays the featured catalog item as a wide cinematic banner.
 */
@Composable
private fun HeroView(video: DestinationVideo, onDetails: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(550.dp),
    ) {
        Image(
            painter = painterResource(video.landscapeImageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        ProfileButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(50.dp),
        )
        GlassPanel(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 50.dp),
        ) {
            Column(
                modifier = Modifier.width(500.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                Text(
                    text = video.name,
                    style = PicoTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = GlassPrimaryText,
                )
                Text(
                    text = video.synopsis,
                    style = PicoTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = GlassSecondaryText,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )
                CatalogButton(text = "查看详情", onClick = onDetails)
            }
        }
    }
}

@Composable
private fun GlassPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .spatialHoverEffect(scaleOnHover = false)
            .backgroundMaterial(style = Material.Thin)
            .border(1.dp, GlassBorder, RoundedCornerShape(28.dp))
            .padding(24.dp),
    ) {
        content()
    }
}

/**
 * Displays a horizontal row of video cards.
 */
@Composable
private fun VideoSection(
    title: String,
    videos: List<DestinationVideo>,
    showDetails: Boolean,
    onVideoSelected: (DestinationVideo) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(title)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 50.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            items(videos, key = DestinationVideo::id) { video ->
                VideoCard(
                    video = video,
                    showDetails = showDetails,
                    modifier = Modifier.width(if (showDetails) 300.dp else 200.dp),
                    onClick = { onVideoSelected(video) },
                )
            }
        }
    }
}

/**
 * Displays a horizontal row of category posters.
 */
@Composable
private fun CategorySection(
    title: String,
    categories: List<VideoCategory>,
    onCategorySelected: (VideoCategory) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(title)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 50.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            items(categories, key = VideoCategory::name) { category ->
                PosterCard(
                    imageRes = category.imageRes,
                    title = category.title,
                    modifier = Modifier.width(300.dp),
                    onClick = { onCategorySelected(category) },
                )
            }
        }
    }
}

/**
 * Displays a section heading with catalog-aligned spacing.
 */
@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = PicoTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = GlassPrimaryText,
        modifier = Modifier.padding(horizontal = 50.dp),
    )
}

@Composable
private fun ProfileButton(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .spatialHoverEffect()
            .background(Color(0x2B000000)),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(32.dp)) {
            val color = Color.Black.copy(alpha = 0.92f)
            val stroke = 1.8.dp.toPx()
            drawCircle(color, radius = size.minDimension * 0.18f, center = center.copy(y = size.height * 0.34f), style = Stroke(stroke))
            val shoulders = Path().apply {
                moveTo(size.width * 0.22f, size.height * 0.78f)
                cubicTo(size.width * 0.26f, size.height * 0.57f, size.width * 0.74f, size.height * 0.57f, size.width * 0.78f, size.height * 0.78f)
            }
            drawPath(shoulders, color, style = Stroke(stroke))
        }
    }
}

/**
 * Displays a video card in detailed or compact style.
 */
@Composable
private fun VideoCard(
    video: DestinationVideo,
    showDetails: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    if (!showDetails) {
        PosterCard(
            imageRes = video.landscapeImageRes,
            title = video.name,
            modifier = modifier,
            onClick = onClick,
        )
        return
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .spatialHoverEffect()
            .background(CardBackground)
            .clickable(onClick = onClick),
    ) {
        Image(
            painter = painterResource(video.landscapeImageRes),
            contentDescription = video.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "${video.year}年  |  ${video.contentRating}  |  ${video.formattedDuration()}",
                style = PicoTheme.typography.bodyMedium,
                color = SecondaryText,
                maxLines = 1,
            )
            Text(
                text = video.name,
                style = PicoTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = video.synopsis,
                style = PicoTheme.typography.bodyMedium,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                video.genres.take(3).forEach { genre ->
                    Text(
                        text = genre,
                        style = PicoTheme.typography.bodyMedium,
                        modifier = Modifier
                            .border(1.dp, BorderColor, RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }
        }
    }
}

/**
 * Displays a poster image with a title below it.
 */
@Composable
private fun PosterCard(
    imageRes: Int,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .spatialHoverEffect()
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop,
        )
        Text(
            text = title,
            style = PicoTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Displays all videos with an optional genre filter.
 */
@Composable
private fun LibraryView(onVideoSelected: (DestinationVideo) -> Unit) {
    var selectedGenre by remember { mutableStateOf<String?>(null) }
    val videos = selectedGenre?.let { genre ->
        DestinationCatalog.videos.filter { genre in it.genres }
    } ?: DestinationCatalog.videos

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 50.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = "资料库",
            style = PicoTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 50.dp),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 50.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                FilterChip(
                    title = "全部",
                    selected = selectedGenre == null,
                    onClick = { selectedGenre = null },
                )
            }
            items(DestinationCatalog.genres) { genre ->
                FilterChip(
                    title = genre,
                    selected = selectedGenre == genre,
                    onClick = { selectedGenre = genre },
                )
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 50.dp, end = 50.dp, bottom = 50.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            items(videos, key = DestinationVideo::id) { video ->
                VideoCard(
                    video = video,
                    showDetails = false,
                    onClick = { onVideoSelected(video) },
                )
            }
        }
    }
}

/**
 * Displays a selectable genre filter.
 */
@Composable
private fun FilterChip(title: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .spatialHoverEffect()
            .background(if (selected) DarkSurface else SelectedBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp),
    ) {
        Text(
            text = title,
            style = PicoTheme.typography.bodyMedium,
            color = if (selected) OnDarkSurface else GlassPrimaryText,
            fontWeight = FontWeight.Medium,
        )
    }
}

/**
 * Displays one category and the videos assigned to it.
 */
@Composable
private fun CategoryView(
    category: VideoCategory,
    onBack: () -> Unit,
    onVideoSelected: (DestinationVideo) -> Unit,
) {
    val videos = DestinationCatalog.videos.filter { category in it.categories }
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        CatalogButton(
            text = "返回",
            modifier = Modifier.padding(horizontal = 50.dp),
            onClick = onBack,
        )
        Text(
            text = category.title,
            style = PicoTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 50.dp),
        )
        Text(
            text = category.description,
            style = PicoTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 50.dp).width(700.dp),
        )
        CatalogButton(
            text = "播放",
            primary = true,
            modifier = Modifier.padding(horizontal = 50.dp),
            onClick = { videos.firstOrNull()?.let(onVideoSelected) },
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 50.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            items(videos, key = DestinationVideo::id) { video ->
                StackVideoCard(
                    video = video,
                    onClick = { onVideoSelected(video) },
                )
            }
        }
    }
}

@Composable
private fun StackVideoCard(video: DestinationVideo, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .spatialHoverEffect()
            .background(CardBackground)
            .clickable(onClick = onClick)
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(video.landscapeImageRes),
            contentDescription = video.name,
            modifier = Modifier
                .width(250.dp)
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "${video.year}年  |  ${video.contentRating}  |  ${video.formattedDuration()}",
                style = PicoTheme.typography.bodyMedium,
                color = SecondaryText,
            )
            Text(video.name, style = PicoTheme.typography.titleLarge)
            Text(
                text = video.synopsis,
                style = PicoTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * Displays metadata, playback actions, extras, and cast for one video.
 */
@Composable
private fun DetailView(
    video: DestinationVideo,
    isUpNext: Boolean,
    onBack: () -> Unit,
    onPlay: () -> Unit,
    onToggleUpNext: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 40.dp),
    ) {
        item {
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth().height(620.dp),
            ) {
                val imageRes = if (maxHeight > maxWidth) video.portraitImageRes else video.landscapeImageRes
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                GlassPanel(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(50.dp),
                ) {
                    Column(
                        modifier = Modifier.width(680.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        CatalogButton(text = "返回", onClick = onBack)
                        Text(
                            text = video.name,
                            style = PicoTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = GlassPrimaryText,
                        )
                        Text(
                            text = "${video.year}年  |  ${video.contentRating}  |  ${video.formattedDuration()}",
                            style = PicoTheme.typography.titleLarge,
                            color = GlassPrimaryText,
                        )
                        Text(
                            text = video.genres.joinToString("  "),
                            style = PicoTheme.typography.bodyMedium,
                            color = GlassSecondaryText,
                        )
                        Text(
                            text = video.synopsis,
                            style = PicoTheme.typography.bodyMedium,
                            color = GlassSecondaryText,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CatalogButton(text = "播放影片", primary = true, onClick = onPlay)
                            CatalogButton(
                                text = if (isUpNext) "已加入接着观看" else "加入接着观看",
                                onClick = onToggleUpNext,
                            )
                        }
                    }
                }
            }
        }
        item {
            Column(
                modifier = Modifier.padding(50.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                Text("花絮", style = PicoTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                PosterCard(
                    imageRes = video.landscapeImageRes,
                    title = "播放预告片",
                    modifier = Modifier.width(360.dp),
                    onClick = onPlay,
                )
                Spacer(Modifier.height(12.dp))
                Text("演职员", style = PicoTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                CreditRow("主演", video.actors)
                CreditRow("导演", video.directors)
                CreditRow("编剧", video.writers)
            }
        }
    }
}

/**
 * Displays one role and its associated people.
 */
@Composable
private fun CreditRow(role: String, names: List<String>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            text = role,
            style = PicoTheme.typography.bodyMedium,
            color = SecondaryText,
            modifier = Modifier.width(100.dp),
        )
        Text(
            text = names.joinToString(", "),
            style = PicoTheme.typography.bodyMedium,
        )
    }
}

/**
 * Displays a simple interactive search over names and genres.
 */
@Composable
private fun SearchView(onVideoSelected: (DestinationVideo) -> Unit) {
    var query by remember { mutableStateOf("") }
    val results = if (query.isBlank()) {
        DestinationCatalog.videos
    } else {
        DestinationCatalog.videos.filter { video ->
            video.name.contains(query, ignoreCase = true) ||
                video.genres.any { it.contains(query, ignoreCase = true) }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(50.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Text("搜索", style = PicoTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("全部", "动画", "剧情", "冒险").forEach { suggestion ->
                FilterChip(
                    title = suggestion,
                    selected = query == suggestion || (suggestion == "全部" && query.isBlank()),
                    onClick = { query = if (suggestion == "全部") "" else suggestion },
                )
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            items(results, key = DestinationVideo::id) { video ->
                VideoCard(
                    video = video,
                    showDetails = false,
                    onClick = { onVideoSelected(video) },
                )
            }
        }
    }
}

/**
 * Displays the intentionally empty sample tabs.
 */
@Composable
private fun EmptyTabView(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, style = PicoTheme.typography.titleLarge, color = SecondaryText)
    }
}

/**
 * Displays a reusable catalog action with primary and secondary styling.
 */
@Composable
private fun CatalogButton(
    text: String,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .spatialHoverEffect()
            .background(if (primary) DarkSurface else DarkSurfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 11.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = PicoTheme.typography.bodyMedium,
            color = OnDarkSurface,
            fontWeight = FontWeight.Medium,
        )
    }
}
