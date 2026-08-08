package com.picoxr.destinationvideo.destination

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.runtime.mutableStateListOf
import com.picoxr.destinationvideo.R

/**
 * A collection used to group related videos.
 */
enum class VideoCategory(
    val title: String,
    val description: String,
    @DrawableRes val imageRes: Int,
) {
    CINEMATIC(
        title = "电影感镜头",
        description = "欣赏精心捕捉的视频与动画，感受影像构图、光影和运动带来的电影质感。",
        imageRes = R.drawable.cinematic_poster,
    ),
    FOREST(
        title = "森林生活",
        description = "暂时离开城市节奏，沉浸在森林、微风与自然声响之中。",
        imageRes = R.drawable.forest_poster,
    ),
    SEA(
        title = "海边时光",
        description = "迎着海风观看海浪、沙滩与远方天光，感受平静而开阔的海岸风景。",
        imageRes = R.drawable.sea_poster,
    ),
    ANIMALS(
        title = "可爱动物",
        description = "在这组自然影像中遇见羽毛、甲壳与林间小伙伴。",
        imageRes = R.drawable.animals_poster,
    ),
    AMAZING(
        title = "奇妙动画",
        description = "进入机器人植物学家的动画世界，跟随它探索未知星球。",
        imageRes = R.drawable.amazing_poster,
    ),
    EXTRAORDINARY(
        title = "非凡旅程",
        description = "跟随机器人植物学家踏上任务，寻找并记录全新的植物生命。",
        imageRes = R.drawable.extraordinary_poster,
    ),
}

/**
 * Identifies either an Android raw resource or a remote stream.
 */
sealed interface VideoSource {
    data class Raw(@RawRes val resourceId: Int) : VideoSource
    data class Remote(val url: String) : VideoSource
}

/**
 * Metadata used by the catalog, detail view, and playback layer.
 */
data class DestinationVideo(
    val id: Int,
    val name: String,
    val synopsis: String,
    val source: VideoSource,
    @DrawableRes val landscapeImageRes: Int,
    @DrawableRes val portraitImageRes: Int,
    val year: Int,
    val durationSeconds: Int,
    val startSeconds: Int = 0,
    val contentRating: String = "未评级",
    val categories: Set<VideoCategory>,
    val genres: Set<String>,
    val actors: List<String>,
    val directors: List<String>,
    val writers: List<String>,
    val isHero: Boolean = false,
    val isFeatured: Boolean = false,
) {
    /**
     * Returns the compact duration used by cards and detail metadata.
     */
    fun formattedDuration(): String {
        val minutes = durationSeconds / 60
        val seconds = durationSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }
}

/**
 * Static catalog ported from the Apple Destination Video sample.
 */
object DestinationCatalog {
    val collectionCategories = listOf(
        VideoCategory.CINEMATIC,
        VideoCategory.FOREST,
        VideoCategory.SEA,
        VideoCategory.ANIMALS,
    )

    val animationCategories = listOf(
        VideoCategory.AMAZING,
        VideoCategory.EXTRAORDINARY,
    )

    val videos = listOf(
        DestinationVideo(
            id = 0,
            name = "机器人植物学家的冒险",
            synopsis = "一位可爱的机器人踏上旅程，试图拯救濒临消失的奇异植物。它将在外星世界中发现神秘的生命形态，也让我们跟随它一起见证新植物的诞生。",
            source = VideoSource.Raw(R.raw.bot_anist_video),
            landscapeImageRes = R.drawable.bot_anist_landscape,
            portraitImageRes = R.drawable.bot_anist_portrait,
            year = 2026,
            durationSeconds = 66,
            categories = setOf(VideoCategory.CINEMATIC, VideoCategory.AMAZING, VideoCategory.EXTRAORDINARY),
            genres = setOf("动画", "科幻"),
            actors = listOf("阿叶", "小芦"),
            directors = listOf("白空"),
            writers = listOf("温泉", "林岸"),
            isHero = true,
        ),
        DestinationVideo(
            id = 1,
            name = "降落",
            synopsis = "经过漫长的星际航行，机器人植物学家和它可靠的飞船抵达沃尔夫一〇六九号星，准备探索星球表面的秘密。",
            source = VideoSource.Raw(R.raw.bot_anist_video),
            landscapeImageRes = R.drawable.landing_landscape,
            portraitImageRes = R.drawable.landing_portrait,
            year = 2026,
            durationSeconds = 66,
            categories = setOf(VideoCategory.AMAZING, VideoCategory.EXTRAORDINARY),
            genres = setOf("动画", "冒险"),
            actors = listOf("阿叶", "小芦"),
            directors = listOf("白空"),
            writers = listOf("温泉", "林岸"),
            isFeatured = true,
        ),
        DestinationVideo(
            id = 2,
            name = "种子采样",
            synopsis = "在一颗被茂密丛林覆盖的星球上，机器人植物学家寻找并记录独特植物，却在途中发现了更多意想不到的线索。",
            source = VideoSource.Raw(R.raw.bot_anist_video),
            landscapeImageRes = R.drawable.samples_landscape,
            portraitImageRes = R.drawable.samples_portrait,
            year = 2026,
            durationSeconds = 66,
            startSeconds = 21,
            categories = setOf(VideoCategory.FOREST, VideoCategory.AMAZING, VideoCategory.EXTRAORDINARY),
            genres = setOf("动画", "科幻"),
            actors = listOf("阿叶", "小芦"),
            directors = listOf("白空"),
            writers = listOf("温泉", "林岸"),
            isFeatured = true,
        ),
        DestinationVideo(
            id = 3,
            name = "实验室",
            synopsis = "机器人植物学家带着满满一背包植物样本回到实验室，开始研究这些神秘的外星植被。",
            source = VideoSource.Raw(R.raw.bot_anist_video),
            landscapeImageRes = R.drawable.lab_landscape,
            portraitImageRes = R.drawable.lab_portrait,
            year = 2026,
            durationSeconds = 66,
            startSeconds = 35,
            categories = setOf(VideoCategory.AMAZING, VideoCategory.EXTRAORDINARY),
            genres = setOf("动画", "科幻"),
            actors = listOf("阿叶", "小芦"),
            directors = listOf("白空"),
            writers = listOf("温泉", "林岸"),
            isFeatured = true,
        ),
        DestinationVideo(
            id = 4,
            name = "发现",
            synopsis = "坚定的植物学家深入地下洞穴，寻找从未被记录的物种，并穿越复杂地形继续前行。",
            source = VideoSource.Raw(R.raw.bot_anist_video),
            landscapeImageRes = R.drawable.discovery_landscape,
            portraitImageRes = R.drawable.discovery_portrait,
            year = 2026,
            durationSeconds = 66,
            startSeconds = 51,
            categories = setOf(VideoCategory.AMAZING, VideoCategory.EXTRAORDINARY),
            genres = setOf("动画", "悬疑"),
            actors = listOf("阿叶", "小芦"),
            directors = listOf("白空"),
            writers = listOf("温泉", "林岸"),
            isFeatured = true,
        ),
        DestinationVideo(
            id = 5,
            name = "舞蹈",
            synopsis = "每个人都需要从工作中休息一下，机器人植物学家也不例外。跟随一场即兴舞会放松片刻。",
            source = VideoSource.Raw(R.raw.dance_video),
            landscapeImageRes = R.drawable.dance_landscape,
            portraitImageRes = R.drawable.dance_portrait,
            year = 2026,
            durationSeconds = 16,
            categories = setOf(VideoCategory.AMAZING),
            genres = setOf("喜剧", "动画"),
            actors = listOf("宁沙", "潘天", "江阳", "林风"),
            directors = listOf("穆鸥"),
            writers = listOf("若石", "顾海璃"),
            isFeatured = true,
        ),
        DestinationVideo(
            id = 6,
            name = "海滩",
            synopsis = "海浪拍打着风景优美的海岸，铺展开一段静谧的海边影像。放松下来，聆听海洋温柔的声音。",
            source = VideoSource.Remote("https://embed-ssl.wistia.com/deliveries/cc8402e8c16cc8f36d3f63bd29eb82f99f4b5f88/accudvh5jy.mp4"),
            landscapeImageRes = R.drawable.beach_landscape,
            portraitImageRes = R.drawable.beach_portrait,
            year = 2026,
            durationSeconds = 61,
            categories = setOf(VideoCategory.SEA),
            genres = setOf("剧情", "浪漫"),
            actors = listOf("宁沙", "潘天", "江阳", "林风"),
            directors = listOf("穆鸥"),
            writers = listOf("若石", "顾海璃"),
            isFeatured = true,
        ),
        DestinationVideo(
            id = 7,
            name = "湖边",
            synopsis = "一群小龟为了占据阳光最好的位置，继续在木桩上展开有趣的争夺。",
            source = VideoSource.Remote("https://embed-ssl.wistia.com/deliveries/cc8402e8c16cc8f36d3f63bd29eb82f99f4b5f88/accudvh5jy.mp4"),
            landscapeImageRes = R.drawable.lake_landscape,
            portraitImageRes = R.drawable.lake_portrait,
            year = 2026,
            durationSeconds = 19,
            categories = setOf(VideoCategory.SEA, VideoCategory.ANIMALS),
            genres = setOf("喜剧", "动作"),
            actors = listOf("麦龟", "柯青"),
            directors = listOf("丁莲"),
            writers = listOf("左浪"),
        ),
        DestinationVideo(
            id = 8,
            name = "森林露营",
            synopsis = "一起寻找理想的营地，聆听野外声音，看花朵在微风中轻轻摇曳。",
            source = VideoSource.Remote("https://embed-ssl.wistia.com/deliveries/cc8402e8c16cc8f36d3f63bd29eb82f99f4b5f88/accudvh5jy.mp4"),
            landscapeImageRes = R.drawable.camping_landscape,
            portraitImageRes = R.drawable.camping_portrait,
            year = 2026,
            durationSeconds = 28,
            categories = setOf(VideoCategory.FOREST),
            genres = setOf("动作", "冒险"),
            actors = listOf("石枝", "方木", "安篷"),
            directors = listOf("安云"),
            writers = listOf("陶雏菊"),
        ),
        DestinationVideo(
            id = 9,
            name = "公园小鸟",
            synopsis = "在梦幻般的春日午后，友好的小鸟们穿梭飞舞，热闹地度过一段时光。",
            source = VideoSource.Remote("https://embed-ssl.wistia.com/deliveries/cc8402e8c16cc8f36d3f63bd29eb82f99f4b5f88/accudvh5jy.mp4"),
            landscapeImageRes = R.drawable.park_landscape,
            portraitImageRes = R.drawable.park_portrait,
            year = 2026,
            durationSeconds = 23,
            categories = setOf(VideoCategory.ANIMALS),
            genres = setOf("纪录片"),
            actors = listOf("白雾", "任山"),
            directors = listOf("苏晴", "温阳"),
            writers = listOf("顾草", "安云"),
        ),
        DestinationVideo(
            id = 10,
            name = "溪边谜影",
            synopsis = "几个朋友来到雨中的营地，却发现溪边似乎生活着某种意想不到的存在。",
            source = VideoSource.Remote("https://embed-ssl.wistia.com/deliveries/cc8402e8c16cc8f36d3f63bd29eb82f99f4b5f88/accudvh5jy.mp4"),
            landscapeImageRes = R.drawable.creek_landscape,
            portraitImageRes = R.drawable.creek_portrait,
            year = 2026,
            durationSeconds = 28,
            categories = setOf(VideoCategory.FOREST, VideoCategory.SEA),
            genres = setOf("悬疑"),
            actors = listOf("欧惊", "尤敢"),
            directors = listOf("宁隐", "柯暗"),
            writers = listOf("顾奔"),
        ),
        DestinationVideo(
            id = 11,
            name = "起伏山丘",
            synopsis = "越过公路，发现山丘深处的美景。薄雾在坡地上缓缓流动，呈现安静而细腻的景象。",
            source = VideoSource.Remote("https://embed-ssl.wistia.com/deliveries/cc8402e8c16cc8f36d3f63bd29eb82f99f4b5f88/accudvh5jy.mp4"),
            landscapeImageRes = R.drawable.hillside_landscape,
            portraitImageRes = R.drawable.hillside_portrait,
            year = 2026,
            durationSeconds = 80,
            categories = setOf(VideoCategory.CINEMATIC),
            genres = setOf("浪漫", "剧情"),
            actors = listOf("林雾", "任山"),
            directors = listOf("尤笔"),
            writers = listOf("安云"),
            isFeatured = true,
        ),
        DestinationVideo(
            id = 12,
            name = "海风",
            synopsis = "一只黑色甲虫沿着巨石缓慢爬行，准备起飞。跟随它感受海岸边微小生命的旅程。",
            source = VideoSource.Remote("https://embed-ssl.wistia.com/deliveries/cc8402e8c16cc8f36d3f63bd29eb82f99f4b5f88/accudvh5jy.mp4"),
            landscapeImageRes = R.drawable.ocean_landscape,
            portraitImageRes = R.drawable.ocean_portrait,
            year = 2026,
            durationSeconds = 14,
            categories = setOf(VideoCategory.CINEMATIC, VideoCategory.SEA),
            genres = setOf("喜剧", "动作"),
            actors = listOf("阿叶", "小芦"),
            directors = listOf("白空"),
            writers = listOf("温泉", "林岸"),
        ),
    )

    val heroVideo: DestinationVideo = videos.first(DestinationVideo::isHero)
    val featuredVideos: List<DestinationVideo> = videos.filter(DestinationVideo::isFeatured)
    val genres: List<String> = videos.flatMap { it.genres }.distinct().sorted()

    /**
     * Finds a catalog item by its stable sample ID.
     */
    fun findVideo(id: Int): DestinationVideo? = videos.firstOrNull { it.id == id }
}

/**
 * Persists the Up Next queue as stable catalog IDs.
 */
class DestinationStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    val upNextIds = mutableStateListOf<Int>()

    init {
        val restoredIds = preferences.getStringSet(KEY_UP_NEXT, emptySet())
            .orEmpty()
            .mapNotNull(String::toIntOrNull)
            .sorted()
        upNextIds.addAll(restoredIds)
    }

    /**
     * Reports whether a video is currently queued.
     */
    fun isUpNext(videoId: Int): Boolean = videoId in upNextIds

    /**
     * Adds or removes a video and immediately persists the queue.
     */
    fun toggleUpNext(videoId: Int) {
        if (!upNextIds.remove(videoId)) {
            upNextIds.add(videoId)
        }
        preferences.edit()
            .putStringSet(KEY_UP_NEXT, upNextIds.map(Int::toString).toSet())
            .apply()
    }

    /**
     * Resolves the current queue against the immutable catalog.
     */
    fun upNextVideos(): List<DestinationVideo> = upNextIds.mapNotNull(DestinationCatalog::findVideo)

    private companion object {
        const val PREFERENCES_NAME = "destination_video"
        const val KEY_UP_NEXT = "up_next"
    }
}
