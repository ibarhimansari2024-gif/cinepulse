package pk.cinepulse.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage

/* ---------------- Catalog: public-domain classics, streamable full films ---------------- */

data class Ep(val n: Int, val t: String, val url: String, val mins: Int = 0)

data class Film(
    val id: String, val title: String, val year: Int, val genre: String,
    val mins: Int, val desc: String, val hue: Float, val url: String,
    val sec: String = "Hollywood", val eps: List<Ep> = emptyList()
)

val FILMS = listOf(
    Film("his_girl_friday", "His Girl Friday", 1940, "Comedy", 92,
        "A newspaper editor uses every trick he knows to keep his ace-reporter ex-wife from remarrying — and from walking into a hanging story.", 200f,
        "https://archive.org/download/his_girl_friday/his_girl_friday.mp4", "Hollywood"),
    Film("meet_john_doe", "Meet John Doe", 1941, "Drama", 122,
        "A reporter invents a \"John Doe\" to save her job — and accidentally ignites a nationwide movement she can no longer control.", 45f,
        "https://archive.org/download/meet_john_doe/meet_john_doe.mp4", "Hollywood"),
    Film("TheGeneral", "The General", 1926, "Action", 79,
        "A railroad engineer single-handedly chases his stolen locomotive — and his heart — straight through enemy lines.", 30f,
        "https://archive.org/download/TheGeneral/The_General_512kb.mp4", "Hollywood"),
    Film("TheLittleShopOfHorrors", "The Little Shop of Horrors", 1960, "Horror", 72,
        "A flower-shop clerk breeds a strange plant in one night — and it develops a very unusual appetite.", 120f,
        "https://archive.org/download/TheLittleShopOfHorrors/The%20Little%20Shop%20of%20Horrors.mp4", "Hollywood"),
    Film("Suddenly_1954", "Suddenly", 1954, "Thriller", 75,
        "Assassins seize an ordinary family home in a small town — because the President's train makes an unscheduled stop there today.", 350f,
        "https://archive.org/download/Suddenly_1954/Suddenly_1954_512kb.mp4", "Hollywood"),
    Film("A.Shriek.in.the.Night", "A Shriek in the Night", 1933, "Mystery", 77,
        "A reporter and a police rival race a strangled-lady killer through a Manhattan apartment house.", 260f,
        "https://archive.org/download/A.Shriek.in.the.Night/A_Shriek_in_the_Night.mp4", "Hollywood"),
    Film("TheAmazingMr.X1948Avi", "The Amazing Mr. X", 1948, "Thriller", 79,
        "A charming spiritualist works a grieving widow — until a voice from the shore starts working him.", 285f,
        "https://archive.org/download/TheAmazingMr.X1948Avi/The_Amazing_Mr_X.mp4", "Hollywood"),
    Film("Boss.of.Big.Town", "The Boss of Big Town", 1942, "Crime", 67,
        "A market owner stands up to the syndicate shaking down every stall in the city.", 15f,
        "https://archive.org/download/Boss.of.Big.Town/Boss.of.Big.Town.mp4", "Hollywood"),
    Film("woody_woodpecker_pantry_panic", "Woody Woodpecker: Pantry Panic", 1941, "Cartoon", 7,
        "A winter cold snap leaves a very hungry Woody defending every last kernel of his corn stash.", 95f,
        "https://archive.org/download/woody_woodpecker_pantry_panic/woody_woodpecker_pantry_panic_512kb.mp4", "Cartoons"),
    Film("Betty_Boop_Poor_Cinderella_1934", "Betty Boop: Poor Cinderella", 1934, "Cartoon", 10,
        "Fleischer's first color film: a scrub-girl, a fairy godmother, and one glass slipper.", 320f,
        "https://archive.org/download/Betty_Boop_Poor_Cinderella_1934/Betty_Boop_Poor_Cinderella_1934_512kb.mp4", "Cartoons"),
    Film("bb_rise_to_fame", "Betty Boop's Rise to Fame", 1934, "Cartoon", 9,
        "Max Fleischer draws Betty live — three of her classic turns in a single reel.", 340f,
        "https://archive.org/download/bb_rise_to_fame/bb_rise_to_fame_512kb.mp4", "Cartoons"),
    Film("little_audry_goofy_goofy_gander", "Little Audrey: Goofy Goofy Gander", 1950, "Cartoon", 7,
        "Audrey ducks into Mother Goose land and can't stop rewriting the rhymes.", 205f,
        "https://archive.org/download/little_audry_goofy_goofy_gander/little_audry_goofy_goofy_gander_512kb.mp4", "Cartoons"),
    Film("failedsamurai1917krazytoonsprint", "Namakura Gatana", 1917, "Anime", 4,
        "The oldest surviving Japanese animation: a fool buys a dull sword and learns its point the hard way.", 50f,
        "https://archive.org/download/failedsamurai1917krazytoonsprint/failedsamurai1917krazytoonsprint.mp4", "Anime"),
    Film("paramountnorakuroshorts1933-1", "Norakuro", 1933, "Anime", 10,
        "Early Japanese cartoon: a scruffy black-dog recruit stumbles through army life.", 215f,
        "https://archive.org/download/paramountnorakuroshorts1933-1/paramountnorakuroshorts1933-1.mp4", "Anime"),
    Film("anonymoose-benkei-tai-ushiwaka-v-2-dvd-10bit-a-28905-ae", "Benkei tai Ushiwaka", 1939, "Anime", 15,
        "The legendary duel on Gojo Bridge, told in pre-war Japanese animation.", 240f,
        "https://archive.org/download/anonymoose-benkei-tai-ushiwaka-v-2-dvd-10bit-a-28905-ae/%5BAnonymoose%5D%20Benkei%20tai%20Ushiwaka%20v2%20%28DVD%2C%2010bit%29%20%5BA28905AE%5D.mp4", "Anime"),
    Film("pyaasa-1957-guru-dutt-classic-hindi-film", "Pyaasa", 1957, "Drama", 136,
        "A poet starves for recognition in a city that only buys him once he's presumed dead.", 35f,
        "https://archive.org/download/pyaasa-1957-guru-dutt-classic-hindi-film/VTS_01_1.mp4", "Hindi"),
    Film("mother-india1957-mehboob-khan-classic-hindi-film-nargis-dutt", "Mother India", 1957, "Drama", 172,
        "A village mother's fight to feed her sons and keep her honor becomes the nation's own epic.", 25f,
        "https://archive.org/download/mother-india1957-mehboob-khan-classic-hindi-film-nargis-dutt/VTS_02_1.mp4", "Hindi"),
    Film("kaagaz-ke-phool-1959-classic-hindi-film-guru-dutt-waheeda-rehman-geeta-dutt-moserbaer-dvd", "Kaagaz Ke Phool", 1959, "Drama", 148,
        "A famous director discovers a girl in a studio doorway — and directs her into stardom and himself into silence.", 48f,
        "https://archive.org/download/kaagaz-ke-phool-1959-classic-hindi-film-guru-dutt-waheeda-rehman-geeta-dutt-moserbaer-dvd/VTS_03_1.mp4", "Hindi"),
    Film("shree-420-1955-classic-hindi-movie-raj-kapoor-nargis", "Shree 420", 1955, "Drama", 167,
        "A country boy comes to Bombay with honest hands — the city teaches him every trick except how to stop loving.", 300f,
        "https://archive.org/download/shree-420-1955-classic-hindi-movie-raj-kapoor-nargis/VTS_02_1.mp4", "Hindi"),
    Film("awara-1951-raj-kapoor-nargis-prithviraj-kapoor-classic-hindi-movie", "Awara", 1951, "Drama", 168,
        "A judge's son raised by the streets stands accused before the very father who abandoned him.", 330f,
        "https://archive.org/download/awara-1951-raj-kapoor-nargis-prithviraj-kapoor-classic-hindi-movie/VTS_04_1.mp4", "Hindi"),
    Film("madhumati-1958-bimal-roy-classic-hindi-film", "Madhumati", 1958, "Drama", 158,
        "A hill station, a reincarnated love, and a haunted haveli that remembers everything.", 275f,
        "https://archive.org/download/madhumati-1958-bimal-roy-classic-hindi-film/VTS_02_1.mp4", "Hindi"),
    Film("superman_mechanical_monsters", "Superman: The Mechanical Monsters", 1941, "Cartoon", 10,
        "Fleischer's art-deco Superman battles a fleet of flying robots stealing gold from the sky.", 210f,
        "https://archive.org/download/superman_the_mechanical_monsters/superman_the_mechanical_monsters_512kb.mp4", "Cartoons"),
    Film("superman_electric_earthquake", "Superman: Electric Earthquake", 1942, "Cartoon", 9,
        "A vengeful scientist's electric earthquake machine threatens Manhattan — Superman dives in.", 195f,
        "https://archive.org/download/superman_electric_earthquake/superman_electric_earthquake_512kb.mp4", "Cartoons"),
    Film("superman_eleventh_hour", "Superman: Eleventh Hour", 1942, "Cartoon", 9,
        "Clark and Lois are held captive — every eleventh hour, a mysterious sabotage hits the docks.", 225f,
        "https://archive.org/download/superman_eleventh_hour/superman_eleventh_hour_512kb.mp4", "Cartoons"),
    Film("flash-gordon-conquers-1940", "Flash Gordon Conquers the Universe", 1940, "Serial", 20,
        "The 1940 chapter serial: Flash, Dale and Zarkov defend Earth from Ming's purple-death plagues and rocket armadas.", 205f,
        "https://archive.org/download/flash_gordon1/flash_gordon_ep01_512kb.mp4", "TV & Serials",
        listOf(
            Ep(1, "Chapter 1", "https://archive.org/download/flash_gordon1/flash_gordon_ep01_512kb.mp4", 20),
            Ep(2, "Chapter 2", "https://archive.org/download/flash_gordon2/chapter2_512kb.mp4", 20),
            Ep(3, "Chapter 3", "https://archive.org/download/flash_gordon3/chapter3_512kb.mp4", 20),
            Ep(4, "Chapter 4", "https://archive.org/download/flash_gordon4/flash_gordon_ep04_512kb.mp4", 20),
            Ep(5, "Chapter 5", "https://archive.org/download/flash_gordon5/chapter5_512kb.mp4", 20))),
    Film("flash-gordon-1936", "Flash Gordon (1936)", 1936, "Serial", 22,
        "The original 1936 space serial that started them all — Flash, Dale and Zarkov crash-land on Mongo.", 45f,
        "https://archive.org/download/flash-gordon-1936_202408/S1Ep1%20-%20The%20Planet%20of%20Peril.mp4", "TV & Serials",
        listOf(Ep(1, "The Planet of Peril", "https://archive.org/download/flash-gordon-1936_202408/S1Ep1%20-%20The%20Planet%20of%20Peril.mp4", 22))),
    Film("thebeverlyhillbilliesgettingsettledhq", "The Beverly Hillbillies", 1962, "Sitcom", 26,
        "The Clampetts strike oil and move to Beverly Hills — the classic 1960s sitcom, these episodes are in the public domain.", 70f,
        "https://archive.org/download/thebeverlyhillbilliesgettingsettledhq/The%20Beverly%20Hillbillies%20-%20Getting%20Settled%20HQ.mp4", "TV & Serials",
        listOf(
            Ep(1, "Getting Settled", "https://archive.org/download/thebeverlyhillbilliesgettingsettledhq/The%20Beverly%20Hillbillies%20-%20Getting%20Settled%20HQ.mp4", 26),
            Ep(2, "Meanwhile, Back at the Cabin", "https://archive.org/download/thebeverlyhillbilliesmeanwhilebackatthecabinhq/The%20Beverly%20Hillbillies%20-%20Meanwhile%2C%20Back%20at%20the%20Cabin%20HQ.mp4", 26),
            Ep(3, "Granny's Garden", "https://archive.org/download/The_Beverly_Hillbillies/GRANNYS_GARDEN.mp4", 26))),
    Film("TheLucyShow5x0191266LucyAndGeorgeBurns", "The Lucy Show", 1966, "Sitcom", 26,
        "Lucille Ball's classic sitcom — a public-domain episode starring Lucy and George Burns.", 160f,
        "https://archive.org/download/TheLucyShow5x0191266LucyAndGeorgeBurns/TheLucyShow_5x01_9-12-66_LucyandGeorgeBurns.mp4", "TV & Serials",
        listOf(Ep(1, "Lucy and George Burns", "https://archive.org/download/TheLucyShow5x0191266LucyAndGeorgeBurns/TheLucyShow_5x01_9-12-66_LucyandGeorgeBurns.mp4", 26)))
)

fun thumb(id: String) = "https://archive.org/services/img/$id"
fun poster(hue: Float) = Brush.linearGradient(
    listOf(Color.hsl(hue, 0.45f, 0.24f), Color.hsl(hue, 0.40f, 0.10f))
)

/* ---------------- App ---------------- */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CinePulseApp() }
    }
}

@Composable
fun CinePulseApp() {
    var detail by remember { mutableStateOf<Film?>(null) }
    var playing by remember { mutableStateOf<Film?>(null) }
    var playUrl by remember { mutableStateOf<String?>(null) }
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF19C6A6), background = Color(0xFF0B0D10),
            surface = Color(0xFF14181D), surfaceVariant = Color(0xFF1B2027)
        )
    ) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            when {
                playing != null -> PlayerScreen(playing!!, playUrl, onClose = { playing = null })
                detail != null -> DetailScreen(detail!!, onBack = { detail = null },
                    onPlay = { playing = detail; playUrl = null },
                    onPlayEp = { playing = detail; playUrl = it })
                else -> HomeScreen(onOpen = { detail = it }, onPlay = { playing = it })
            }
        }
    }
}

/* ---------------- Home ---------------- */

@Composable
fun HomeScreen(onOpen: (Film) -> Unit, onPlay: (Film) -> Unit) {
    var query by remember { mutableStateOf("") }
    var sec by remember { mutableStateOf("All") }
    val q = query.trim()
    val hits = FILMS.filter {
        (sec == "All" || it.sec == sec) &&
            (q.isEmpty() || it.title.contains(q, true) || it.genre.contains(q, true))
    }
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            Row(
                Modifier.fillMaxWidth().padding(16.dp, 14.dp, 16.dp, 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("CINE", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                Text("PULSE", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.weight(1f))
                Box(Modifier.size(32.dp).background(Color(0xFF19C6A6), CircleShape),
                    contentAlignment = Alignment.Center) {
                    Text("SI", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF04211B))
                }
            }
        }
        item {
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                placeholder = { Text("Search titles or genres") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color(0x22FFFFFF)
                ),
                modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp)
            )
        }
        item {
            LazyRow(contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)) {
                items(listOf("All", "Hollywood", "TV & Serials", "Cartoons", "Anime", "Hindi")) { s ->
                    val on = s == sec
                    Text(
                        s, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                        color = if (on) Color(0xFF04211B) else Color(0xFF9AA3AE),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(
                                if (on) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { sec = s }
                            .padding(14.dp, 8.dp)
                    )
                }
            }
        }
        if (hits.isEmpty()) {
            item {
                Column(Modifier.padding(16.dp, 40.dp)) {
                    Text("No results for \"$q\"", fontWeight = FontWeight.Bold)
                    Text("Try another title or genre.", color = Color(0xFF8B94A0), fontSize = 13.sp)
                }
            }
        } else {
            item { HeroCard(hits.first(), onPlay = onPlay, onOpen = onOpen) }
            item {
                Text("  NOW SHOWING", fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B94A0), letterSpacing = 2.sp,
                    modifier = Modifier.padding(0.dp, 10.dp))
            }
            item {
                LazyRow(contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)) {
                    items(hits) { f -> PosterCard(f, onOpen) }
                }
            }
            listOf("Comedy", "Drama", "Thriller", "Action", "Horror").forEach { g ->
                val row = hits.filter { it.genre.equals(g, true) }
                if (row.isNotEmpty()) {
                    item {
                        Text("  ${g.uppercase()} HOUR", fontSize = 12.sp, fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B94A0), letterSpacing = 2.sp,
                            modifier = Modifier.padding(0.dp, 10.dp))
                    }
                    item {
                        LazyRow(contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)) {
                            items(row) { f -> PosterCard(f, onOpen) }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(28.dp)) }
        }
    }
}

@Composable
fun HeroCard(f: Film, onPlay: (Film) -> Unit, onOpen: (Film) -> Unit) {
    Box(
        Modifier.fillMaxWidth().height(250.dp).clickable { onOpen(f) }
    ) {
        Box(Modifier.matchParentSize().background(poster(f.hue)))
        AsyncImage(
            model = thumb(f.id), contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Box(
            Modifier.matchParentSize().background(
                Brush.verticalGradient(listOf(Color.Transparent, Color(0xEE0B0D10)), startY = 60f)
            )
        )
        Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text("CINEPULSE CLASSIC", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary, letterSpacing = 2.sp)
            Text(f.title, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            Text("${f.year}  •  ${f.mins} min  •  ${f.genre}  •  PUBLIC DOMAIN",
                fontSize = 12.sp, color = Color(0xFF9AA3AE))
            Row(Modifier.padding(0.dp, 10.dp)) {
                Button(onClick = { onPlay(f) }, shape = RoundedCornerShape(8.dp)) { Text("▶  Play") }
                Spacer(Modifier.width(10.dp))
                OutlinedButton(onClick = { onOpen(f) }, shape = RoundedCornerShape(8.dp)) { Text("More info") }
            }
        }
    }
}

@Composable
fun PosterCard(f: Film, onOpen: (Film) -> Unit) {
    Box(
        Modifier
            .padding(end = 10.dp)
            .width(120.dp)
            .height(172.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
            .clickable { onOpen(f) }
    ) {
        Box(Modifier.matchParentSize().background(poster(f.hue), RoundedCornerShape(10.dp)))
        AsyncImage(
            model = thumb(f.id), contentDescription = f.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Box(
            Modifier.matchParentSize().background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, Color(0xF20B0D10)), startY = 180f
                ), RoundedCornerShape(10.dp)
            )
        )
        Column(Modifier.align(Alignment.BottomStart).padding(8.dp)) {
            Text(f.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 2)
            Text("${f.year} • ${f.genre}", fontSize = 10.sp, color = Color(0xFF8B94A0))
        }
    }
}

/* ---------------- Detail ---------------- */

@Composable
fun DetailScreen(f: Film, onBack: () -> Unit, onPlay: () -> Unit, onPlayEp: (String) -> Unit) {
    BackHandler(onBack = onBack)
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth().height(260.dp)) {
            Box(Modifier.matchParentSize().background(poster(f.hue)))
            AsyncImage(model = thumb(f.id), contentDescription = null,
                contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize())
            Box(Modifier.matchParentSize().background(
                Brush.verticalGradient(listOf(Color.Transparent, Color(0xF50B0D10)), startY = 40f)))
            TextButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.TopStart).padding(4.dp).statusBarsPadding()
            ) { Text("←  Back", color = Color.White) }
            Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                Text(f.title, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Text("${f.year}  •  ${f.mins} min  •  ${f.genre}  •  Public Domain",
                    fontSize = 12.sp, color = Color(0xFF9AA3AE))
            }
        }
        Column(Modifier.padding(16.dp)) {
            Button(onClick = onPlay, shape = RoundedCornerShape(8.dp)) { Text("▶  Play full film") }
            Spacer(Modifier.height(14.dp))
            Text(f.desc, fontSize = 14.sp, lineHeight = 20.sp, color = Color(0xFFD3D9E0))
            Spacer(Modifier.height(10.dp))
            if (f.eps.isNotEmpty()) {
                Text("  EPISODES", fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B94A0), letterSpacing = 2.sp)
                f.eps.forEach { ep ->
                    Row(
                        Modifier.fillMaxWidth().clickable { onPlayEp(ep.url) }.padding(0.dp, 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("EP ${ep.n}", color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(44.dp))
                        Column(Modifier.weight(1f)) {
                            Text(ep.t, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            if (ep.mins > 0) Text("${ep.mins} min", fontSize = 11.sp, color = Color(0xFF8B94A0))
                        }
                        Text("▶", color = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
            Text("Streamed from the Internet Archive's public-domain collection. " +
                    "The film is in the public domain; playback quality depends on the archive copy.",
                fontSize = 11.sp, color = Color(0xFF6B7480), lineHeight = 15.sp)
        }
    }
}

/* ---------------- Player ---------------- */

@Composable
fun PlayerScreen(f: Film, url: String?, onClose: () -> Unit) {
    val ctx = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(ctx).build().apply {
            setMediaItem(MediaItem.fromUri(url ?: f.url))
            prepare()
            playWhenReady = true
        }
    }
    DisposableEffect(Unit) { onDispose { player.release() } }
    BackHandler(onBack = onClose)
    Column(Modifier.fillMaxSize().background(Color.Black)) {
        Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onClose) { Text("✕  Close", color = Color.White) }
            Spacer(Modifier.weight(1f))
            Text(f.title, color = Color(0xFF9AA3AE), fontSize = 13.sp, modifier = Modifier.padding(end = 12.dp))
        }
        AndroidView(
            factory = { pctx -> PlayerView(pctx).apply { this.player = player } },
            modifier = Modifier.fillMaxSize()
        )
    }
}
