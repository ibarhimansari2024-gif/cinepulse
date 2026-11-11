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
import androidx.compose.foundation.layout.matchParentSize
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

data class Film(
    val id: String, val title: String, val year: Int, val genre: String,
    val mins: Int, val desc: String, val hue: Float, val url: String
)

val FILMS = listOf(
    Film("his_girl_friday", "His Girl Friday", 1940, "Comedy", 92,
        "A newspaper editor uses every trick he knows to keep his ace-reporter ex-wife from remarrying — and from walking into a hanging story.", 200f,
        "https://archive.org/download/his_girl_friday/his_girl_friday.mp4"),
    Film("meet_john_doe", "Meet John Doe", 1941, "Drama", 122,
        "A reporter invents a \"John Doe\" to save her job — and accidentally ignites a nationwide movement she can no longer control.", 45f,
        "https://archive.org/download/meet_john_doe/meet_john_doe.mp4"),
    Film("TheGeneral", "The General", 1926, "Action", 79,
        "A railroad engineer single-handedly chases his stolen locomotive — and his heart — straight through enemy lines.", 30f,
        "https://archive.org/download/TheGeneral/The_General_512kb.mp4"),
    Film("TheLittleShopOfHorrors", "The Little Shop of Horrors", 1960, "Horror", 72,
        "A flower-shop clerk breeds a strange plant in one night — and it develops a very unusual appetite.", 120f,
        "https://archive.org/download/TheLittleShopOfHorrors/The%20Little%20Shop%20of%20Horrors.mp4"),
    Film("Suddenly_1954", "Suddenly", 1954, "Thriller", 75,
        "Assassins seize an ordinary family home in a small town — because the President's train makes an unscheduled stop there today.", 350f,
        "https://archive.org/download/Suddenly_1954/Suddenly_1954_512kb.mp4")
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
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF19C6A6), background = Color(0xFF0B0D10),
            surface = Color(0xFF14181D), surfaceVariant = Color(0xFF1B2027)
        )
    ) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            when {
                playing != null -> PlayerScreen(playing!!, onClose = { playing = null })
                detail != null -> DetailScreen(detail!!, onBack = { detail = null },
                    onPlay = { playing = detail })
                else -> HomeScreen(onOpen = { detail = it }, onPlay = { playing = it })
            }
        }
    }
}

/* ---------------- Home ---------------- */

@Composable
fun HomeScreen(onOpen: (Film) -> Unit, onPlay: (Film) -> Unit) {
    var query by remember { mutableStateOf("") }
    val q = query.trim()
    val hits = if (q.isEmpty()) FILMS else FILMS.filter {
        it.title.contains(q, true) || it.genre.contains(q, true)
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
                Text("  CLASSICS CINEMA", fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B94A0), letterSpacing = 2.sp,
                    modifier = Modifier.padding(0.dp, 10.dp))
            }
            item {
                LazyRow(contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)) {
                    items(hits) { f -> PosterCard(f) { detail = f } }
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
                            items(row) { f -> PosterCard(f) { detail = f } }
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
fun DetailScreen(f: Film, onBack: () -> Unit, onPlay: () -> Unit) {
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
            Text("Streamed from the Internet Archive's public-domain collection. " +
                    "The film is in the public domain; playback quality depends on the archive copy.",
                fontSize = 11.sp, color = Color(0xFF6B7480), lineHeight = 15.sp)
        }
    }
}

/* ---------------- Player ---------------- */

@Composable
fun PlayerScreen(f: Film, onClose: () -> Unit) {
    val ctx = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(ctx).build().apply {
            setMediaItem(MediaItem.fromUri(f.url))
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
