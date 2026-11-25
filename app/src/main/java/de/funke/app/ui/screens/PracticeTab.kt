package de.funke.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.funke.app.R
import de.funke.app.progress.Streaks
import de.funke.app.ui.AppState
import de.funke.app.ui.LocalHaptics
import de.funke.app.ui.LocalSounds
import de.funke.app.ui.Sfx
import de.funke.app.ui.challenge.Practice
import de.funke.app.ui.challenge.PracticeLevel
import de.funke.app.ui.challenge.PracticeSubjects
import de.funke.app.ui.challenge.Subject
import de.funke.app.ui.challenge.Task
import de.funke.app.ui.challenge.TaskHistory
import de.funke.app.ui.challenge.TaskPrefs
import de.funke.app.ui.challenge.TaskUse
import de.funke.app.ui.challenge.Tasks
import de.funke.app.ui.challenge.art
import de.funke.app.ui.challenge.topics
import de.funke.app.ui.components.AnimatedNumber
import de.funke.app.ui.components.BackButton
import de.funke.app.ui.components.BottomSheet
import de.funke.app.ui.components.CloseButton
import de.funke.app.ui.components.LipButton
import de.funke.app.ui.components.LipPill
import de.funke.app.ui.components.LipTone
import de.funke.app.ui.components.Lumi2D
import de.funke.app.ui.components.LumiCue
import de.funke.app.ui.components.LumiPose
import de.funke.app.ui.components.PrimaryButton
import de.funke.app.ui.components.SectionLabel
import de.funke.app.ui.components.SegmentedToggle
import de.funke.app.ui.components.TextAction
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.components.glass
import de.funke.app.ui.components.liquidRim
import de.funke.app.ui.components.hardShadow
import de.funke.app.ui.components.reveal
import de.funke.app.ui.components.surface
import de.funke.app.ui.theme.Elevation
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Sizes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.theme.reducedMotion
import de.funke.app.ui.tr
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*
 * Reiter "Aufgaben" (seit 12.0 wieder mit Lektionen, aber ohne Kurs):
 *  - Übersicht: oben "Weiter" (die letzte Lektion), "Gemischt" und die Fächer.
 *  - Fach: Stufe wählen. Die Stufen stehen als Bücher im Regal, vom dünnen Heft (Leicht) bis zum dicken
 *    Wälzer (Meister); das gewählte Buch zieht sich ein Stück heraus. Darunter, was einen erwartet.
 *  - Lektion: 3 bis 5 Aufgaben. Oben das Kreuz und die Abschnitte der Lektion (fertige antippen = noch
 *    einmal ansehen), unten Lumi mit Tipps und "Prüfen". Danach eine kurze Zusammenfassung.
 * Es gibt keinen Fortschritt über die Lektionen hinweg; jede Lektion zieht frische Aufgaben.
 */

/** "mix" = gemischt aus allen eingeschalteten Fächern, sonst die Fach-ID. */
private const val MIX = "mix"

@Composable
fun PracticeTab(state: AppState, modifier: Modifier = Modifier) {
    // Wege: Übersicht → Fach (Regal) → Lektion. Als Text gespeichert, damit es eine Drehung übersteht.
    var subjectId by rememberSaveable { mutableStateOf<String?>(null) }
    var lessonLevel by rememberSaveable { mutableStateOf<String?>(null) }
    var lessonNo by rememberSaveable { mutableIntStateOf(0) }
    val still = reducedMotion
    val route = when {
        lessonLevel != null -> 2
        subjectId != null -> 1
        else -> 0
    }
    // Im Fach und in der Lektion: Leiste unten weg, kein Wischen zwischen Reitern
    LaunchedEffect(route) { state.immersive = route != 0 }
    DisposableEffect(Unit) { onDispose { state.immersive = false } }
    fun start(id: String, level: PracticeLevel) {
        subjectId = id
        lessonLevel = level.id
        lessonNo++
        state.choosePractice(Subject.entries.firstOrNull { it.id == id }, level)
    }
    AnimatedContent(
        targetState = route,
        transitionSpec = {
            val forward = targetState > initialState
            if (still) fadeIn(tween(Motion.Fast)) togetherWith fadeOut(tween(Motion.Fast))
            else (fadeIn(tween(Motion.Base, easing = Motion.EaseOut)) + slideInHorizontally(tween(Motion.Slow, easing = Motion.EaseOut)) { if (forward) it / 6 else -it / 10 }) togetherWith
                (fadeOut(tween(Motion.Fast)) + slideOutHorizontally(tween(Motion.Base, easing = Motion.EaseOut)) { if (forward) -it / 10 else it / 6 })
        },
        label = "practice",
        modifier = modifier,
    ) { r ->
        when (r) {
            0 -> PracticeHub(
                state = state,
                onOpen = { id -> subjectId = id },
                onContinue = { id -> start(id, state.practiceLevelFor(Subject.entries.firstOrNull { it.id == id })) },
            )
            1 -> {
                val id = subjectId ?: MIX
                SubjectScreen(
                    state = state,
                    id = id,
                    onBack = { subjectId = null },
                    onStart = { level -> start(id, level) },
                )
            }
            else -> {
                val id = subjectId ?: MIX
                val level = PracticeLevel.byId(lessonLevel) ?: PracticeLevel.Medium
                key(lessonNo) {
                    LessonScreen(
                        state = state,
                        id = id,
                        level = level,
                        onExit = { lessonLevel = null },
                        onAgain = { lessonNo++ },
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Übersicht
// ---------------------------------------------------------------------------------------------

@Composable
private fun PracticeHub(state: AppState, onOpen: (String) -> Unit, onContinue: (String) -> Unit) {
    val p = Funke.colors
    @Suppress("UNUSED_VARIABLE") val v = state.taskPrefsVersion
    val subjects = PracticeSubjects.filter { TaskPrefs.subjectOn(it) }
    val mix = TaskPrefs.mixSubjects()
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(start = Space.page, end = Space.page, top = Space.s6, bottom = NavBarSpace + Space.s6),
    ) {
        Row(Modifier.fillMaxWidth().reveal(0), verticalAlignment = Alignment.CenterVertically) {
            Text(tr("Aufgaben", "Tasks"), style = MaterialTheme.typography.headlineLarge, color = p.text, modifier = Modifier.weight(1f), maxLines = 1)
            RewardsChip(state)
        }

        // Weiter: die zuletzt gestartete Lektion, mit einem Tipp sofort
        val last = state.lastLesson
        val lastSubject = Subject.entries.firstOrNull { it.id == last }
        if (last != null && (last == MIX || (lastSubject != null && TaskPrefs.subjectOn(lastSubject)))) {
            Spacer(Modifier.height(Space.s6))
            ContinueCard(
                subject = lastSubject,
                level = state.practiceLevelFor(lastSubject),
                mixSubjects = mix,
                onClick = { onContinue(last) },
                modifier = Modifier.reveal(40),
            )
        }

        Spacer(Modifier.height(Space.s6))
        MixCard(mix, all = mix.size >= subjects.size, onClick = { onOpen(MIX) }, modifier = Modifier.reveal(60))

        // Die Fächer als Kacheln: ohne Überschrift, die Bilder sagen genug
        Spacer(Modifier.height(Space.s6))
        subjects.chunked(2).forEachIndexed { row, pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(Space.s3), modifier = Modifier.fillMaxWidth().reveal(100 + 30 * row)) {
                pair.forEach { s -> SubjectTile(s, state.practiceLevelFor(s), Modifier.weight(1f)) { onOpen(s.id) } }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(Space.s3))
        }
    }
}

/** Fach-Bild auf einem zart getönten Kreis. */
@Composable
private fun SubjectArt(subject: Subject, size: Dp, modifier: Modifier = Modifier) {
    val p = Funke.colors
    Box(
        modifier
            .size(size)
            .clip(CircleShape)
            .background(p.tame(subject.tint).copy(alpha = if (p.isDark) 0.16f else 0.12f)),
        contentAlignment = Alignment.Center,
    ) {
        Image(painterResource(subject.art), contentDescription = null, modifier = Modifier.size(size * 0.72f))
    }
}

/** Drei Fach-Bilder, leicht aufgefächert (für "Gemischt"). */
@Composable
private fun MixArt(subjects: List<Subject>, modifier: Modifier = Modifier, size: Dp = 56.dp) {
    Box(modifier.size(width = size + size * 0.86f, height = size + size * 0.22f)) {
        subjects.take(3).forEachIndexed { i, s ->
            SubjectArt(
                s, size,
                Modifier
                    .offset(x = size * 0.43f * i, y = if (i == 1) 0.dp else size * 0.22f)
                    .graphicsLayer { rotationZ = (i - 1) * 8f },
            )
        }
    }
}

/** Weiter: Fach-Bild, Name, Stufe und ein runder Start-Knopf. Der schnellste Weg in eine Lektion. */
@Composable
private fun ContinueCard(subject: Subject?, level: PracticeLevel, mixSubjects: List<Subject>, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val p = Funke.colors
    Row(
        modifier
            .fillMaxWidth()
            .bouncyClickable(shape = Shapes.card, pressedScale = 0.98f, onClick = onClick)
            .surface(Shapes.card, Elevation.Level2)
            .padding(Space.s5)
            .semantics(mergeDescendants = true) { contentDescription = tr("Weiter", "Continue") },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (subject != null) SubjectArt(subject, 56.dp) else MixArt(mixSubjects, size = 40.dp)
        Spacer(Modifier.width(Space.s4))
        Column(Modifier.weight(1f)) {
            Text(tr("Weiter", "Continue"), style = MaterialTheme.typography.labelMedium, color = p.accent)
            Text(subject?.label ?: tr("Gemischt", "Mixed"), style = MaterialTheme.typography.titleLarge, color = p.text, maxLines = 1)
            Text(
                tr("${level.label}, ${level.lessonSize} Aufgaben", "${level.label}, ${level.lessonSize} tasks"),
                style = MaterialTheme.typography.bodySmall.copy(fontFeatureSettings = "tnum, ss03"),
                color = p.textTertiary,
            )
        }
        Spacer(Modifier.width(Space.s3))
        PlayDisc()
    }
}

/** Runder Start-Knopf in der Akzentfarbe. */
@Composable
private fun PlayDisc() {
    val p = Funke.colors
    Box(Modifier.size(48.dp).clip(CircleShape).background(p.accent), contentAlignment = Alignment.Center) {
        Icon(painterResource(R.drawable.ic_play), contentDescription = null, tint = p.onAccent, modifier = Modifier.size(20.dp).offset(x = 1.dp))
    }
}

/**
 * "Gemischt": aus den gewählten Fächern, jedes Mal etwas anderes. Drei Fach-Bilder fächern sich auf,
 * darunter steht, was im Mix ist (seit 15.2 frei wählbar).
 */
@Composable
private fun MixCard(subjects: List<Subject>, all: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val p = Funke.colors
    Row(
        modifier
            .fillMaxWidth()
            .bouncyClickable(shape = Shapes.card, pressedScale = 0.98f, onClick = onClick)
            .surface(Shapes.card, Elevation.Level1)
            .padding(Space.s5),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MixArt(subjects)
        Spacer(Modifier.width(Space.s3))
        Column(Modifier.weight(1f)) {
            Text(tr("Gemischt", "Mixed"), style = MaterialTheme.typography.titleLarge, color = p.text, maxLines = 1)
            Text(mixSummary(subjects, all), style = MaterialTheme.typography.bodySmall, color = p.textTertiary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.width(Space.s2))
        de.funke.app.ui.components.ChevronRight()
    }
}

/** "Alle Fächer", "Physik und Mathe", "Physik, Mathe und Chemie" oder "Physik, Mathe und 4 weitere". */
private fun mixSummary(subjects: List<Subject>, all: Boolean): String {
    val names = subjects.map { it.label }
    return when {
        all && subjects.size > 3 -> tr("Alle Fächer", "All subjects")
        names.size == 1 -> names[0]
        names.size == 2 -> tr("${names[0]} und ${names[1]}", "${names[0]} and ${names[1]}")
        names.size == 3 -> tr("${names[0]}, ${names[1]} und ${names[2]}", "${names[0]}, ${names[1]} and ${names[2]}")
        else -> tr("${names[0]}, ${names[1]} und ${names.size - 2} weitere", "${names[0]}, ${names[1]} and ${names.size - 2} more")
    }
}

/**
 * Kachel eines Fachs: Bild, Name und die Stufe, auf der man in diesem Fach gerade ist. Die Stufe steht als kleine
 * Punkte (wie auf den Büchern im Regal) mit ihrem Namen – so ist klar, was das Wort bedeutet. Seit 15.0 ändert sie
 * sich sofort, wenn man im Regal eine andere wählt.
 */
@Composable
private fun SubjectTile(subject: Subject, level: PracticeLevel, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val p = Funke.colors
    Column(
        modifier
            .bouncyClickable(shape = Shapes.card, pressedScale = 0.97f, onClick = onClick)
            .surface(Shapes.card, Elevation.Level1)
            .padding(start = Space.s4, end = Space.s4, top = Space.s5, bottom = Space.s4)
            .semantics(mergeDescendants = true) { contentDescription = tr("${subject.label}, Stufe ${level.label}", "${subject.label}, level ${level.label}") },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SubjectArt(subject, 64.dp)
        Spacer(Modifier.height(Space.s3))
        Text(subject.label, style = MaterialTheme.typography.titleSmall, color = p.text, maxLines = 1)
        Spacer(Modifier.height(Space.s1))
        Row(verticalAlignment = Alignment.CenterVertically) {
            LevelDots(level, p.tame(subject.tint))
            Spacer(Modifier.width(6.dp))
            Text(level.label, style = MaterialTheme.typography.labelSmall, color = p.textTertiary, maxLines = 1)
        }
    }
}

/** Stufe als fünf kleine Punkte (Leicht = einer … Meister = fünf). */
@Composable
private fun LevelDots(level: PracticeLevel, tint: Color) {
    val p = Funke.colors
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
        PracticeLevel.entries.forEach { lv ->
            Box(
                Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(if (lv.ordinal <= level.ordinal) tint else p.textTertiary.copy(alpha = 0.25f)),
            )
        }
    }
}

/**
 * Oben rechts, im selben Stil wie die Streak-Flamme im Fokus-Tab: eine ruhige Pille mit Streak-Eis
 * (Schneeflocke im Ring, der Ring zeigt den Weg zum nächsten Eis) und Funken. Nur die Symbole sind farbig,
 * die Zahlen stehen in Textfarbe.
 */
@Composable
private fun RewardsChip(state: AppState) {
    val p = Funke.colors
    val ice = de.funke.app.ui.theme.RewardColors.Ice
    val have = state.streakFreezes
    val sparks = state.sparks
    val full = have >= Streaks.MAX_FREEZES
    val f by animateFloatAsState(
        if (full) 1f else state.taskFreezeProgress / state.tasksPerFreeze.toFloat(),
        Motion.spec(Motion.Slow, Motion.EaseOut),
        label = "ice",
    )
    val numberStyle = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum, ss03")
    Row(
        Modifier
            .glass(Shapes.pill)
            .liquidRim(Shapes.pill, strength = 0.6f)
            .padding(start = Space.s3, end = Space.s4, top = Space.s2, bottom = Space.s2)
            .semantics(mergeDescendants = true) {
                contentDescription = tr("$have Streak-Eis, $sparks Funken", "$have streak freezes, $sparks sparks")
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(22.dp)) {
                val sw = 2.5.dp.toPx()
                val tl = Offset(sw / 2, sw / 2)
                val sz = Size(size.width - sw, size.height - sw)
                drawArc(p.textTertiary.copy(alpha = 0.25f), 0f, 360f, false, tl, sz, style = Stroke(sw))
                if (f > 0.001f) drawArc(ice, -90f, 360f * f, false, tl, sz, style = Stroke(sw, cap = StrokeCap.Round))
            }
            Icon(painterResource(R.drawable.ic_snowflake), contentDescription = null, tint = ice, modifier = Modifier.size(12.dp))
        }
        Spacer(Modifier.width(Space.s2))
        AnimatedNumber("$have", style = numberStyle, color = if (have > 0) p.text else p.textTertiary)
        Box(
            Modifier
                .padding(horizontal = Space.s3)
                .size(width = 1.dp, height = 18.dp)
                .background(p.textTertiary.copy(alpha = 0.3f)),
        )
        Icon(painterResource(R.drawable.ic_sparkles), contentDescription = null, tint = SparkGold, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(Space.s2))
        AnimatedNumber("$sparks", style = numberStyle, color = if (sparks > 0) p.text else p.textTertiary)
    }
}

// ---------------------------------------------------------------------------------------------
// Fach: Stufe im Regal wählen
// ---------------------------------------------------------------------------------------------

/** Ungefähre Minuten pro Aufgabe je Stufe (für "ca. 4 Min."). */
private fun PracticeLevel.minutesPerTask(): Float = when (this) {
    PracticeLevel.Easy -> 0.6f
    PracticeLevel.Medium -> 1f
    PracticeLevel.Hard -> 1.5f
    PracticeLevel.Expert -> 2.5f
    PracticeLevel.Master -> 4f
}

@Composable
private fun SubjectScreen(state: AppState, id: String, onBack: () -> Unit, onStart: (PracticeLevel) -> Unit) {
    val p = Funke.colors
    val subject = Subject.entries.firstOrNull { it.id == id }
    val version = state.taskPrefsVersion
    val mix = remember(id, version) { TaskPrefs.mixSubjects() }
    val levels = remember(id, mix) {
        if (subject != null) Practice.levels(subject)
        else PracticeLevel.entries.filter { lv -> mix.any { lv in Practice.levels(it) } }.ifEmpty { listOf(PracticeLevel.Medium) }
    }
    var picking by remember { mutableStateOf(false) }
    var selected by rememberSaveable(id) { mutableStateOf(state.practiceLevelFor(subject).let { if (it in levels) it.id else levels.first().id }) }
    // Passt die gewählte Stufe nach dem Ändern des Mixes nicht mehr, nimmt das Regal die nächstliegende
    val level = (PracticeLevel.byId(selected)?.takeIf { it in levels })
        ?: levels.minByOrNull { kotlin.math.abs(it.ordinal - (PracticeLevel.byId(selected)?.ordinal ?: 1)) } ?: levels.first()
    val tint = subject?.tint ?: p.accent
    BackHandler(enabled = !picking, onBack = onBack)

    Column(Modifier.fillMaxSize().background(p.bg).statusBarsPadding().navigationBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().padding(start = Space.s3, end = Space.s3, top = Space.s2),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BackButton(onBack)
            Spacer(Modifier.width(Space.s2))
            if (subject != null) SubjectArt(subject, 36.dp) else MixArt(mix, size = 28.dp)
            Spacer(Modifier.width(Space.s3))
            Text(subject?.label ?: tr("Gemischt", "Mixed"), style = MaterialTheme.typography.titleLarge, color = p.text, modifier = Modifier.weight(1f), maxLines = 1)
        }
        // Gemischt: welche Fächer drin sind, mit "Ändern" (seit 15.2)
        if (subject == null) {
            MixRow(
                mix,
                all = mix.size >= PracticeSubjects.count { TaskPrefs.subjectOn(it) },
                onEdit = { picking = true },
                modifier = Modifier.padding(start = Space.page, end = Space.page, top = Space.s4).reveal(20),
            )
        }

        Spacer(Modifier.weight(1f))
        LevelShelf(
            levels = levels,
            selected = level,
            tint = tint,
            onSelect = {
                selected = it.id
                // gleich merken: die Übersicht zeigt unter dem Fach dann sofort die neue Stufe
                state.rememberPracticeLevel(subject, it)
            },
            modifier = Modifier.padding(horizontal = Space.page).reveal(40),
        )
        Spacer(Modifier.height(Space.s7))
        AnimatedContent(
            targetState = level,
            transitionSpec = {
                val right = targetState.ordinal > initialState.ordinal
                (fadeIn(tween(Motion.Base, easing = Motion.EaseOut)) + slideInHorizontally(tween(Motion.Medium, easing = Motion.EaseOut)) { if (right) it / 12 else -it / 12 }) togetherWith
                    fadeOut(tween(Motion.Fast))
            },
            label = "levelInfo",
            modifier = Modifier.fillMaxWidth().padding(horizontal = Space.page),
        ) { lv ->
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(lv.label, style = MaterialTheme.typography.headlineMedium, color = p.text)
                Spacer(Modifier.height(Space.s1))
                val minutes = kotlin.math.ceil(lv.lessonSize * lv.minutesPerTask()).toInt().coerceAtLeast(2)
                Text(
                    tr("${lv.lessonSize} Aufgaben, ca. $minutes Min.", "${lv.lessonSize} tasks, about $minutes min"),
                    style = MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum, ss03"),
                    color = p.textTertiary,
                )
            }
        }
        Spacer(Modifier.weight(1.2f))
        PrimaryButton(
            tr("Lektion starten", "Start lesson"),
            onClick = { onStart(level) },
            modifier = Modifier.padding(horizontal = Space.page).padding(bottom = Space.s4),
        )
    }
    if (subject == null) MixSheet(state, visible = picking, onDismiss = { picking = false })
}

/**
 * Was im Mix ist: die Fach-Bilder leicht überlappend, daneben die Namen und rechts "Ändern".
 * Die ganze Zeile öffnet die Auswahl.
 */
@Composable
private fun MixRow(subjects: List<Subject>, all: Boolean, onEdit: () -> Unit, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val shown = subjects.take(5)
    val art = 28.dp
    val step = 18.dp
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .bouncyClickable(shape = Shapes.md, pressedScale = 0.98f, onClick = onEdit)
            .semantics(mergeDescendants = true) { contentDescription = tr("Fächer im Mix: ", "Subjects in the mix: ") + mixSummary(subjects, all) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(width = art + step * (shown.size - 1).coerceAtLeast(0), height = art)) {
            shown.forEachIndexed { i, s ->
                Box(
                    Modifier
                        .offset(x = step * i)
                        .size(art)
                        .clip(CircleShape)
                        // Ring in Grundfarbe trennt die überlappenden Bilder sauber
                        .background(p.bg)
                        .padding(2.dp),
                ) { SubjectArt(s, art - 4.dp) }
            }
        }
        Spacer(Modifier.width(Space.s3))
        Text(
            mixSummary(subjects, all),
            style = MaterialTheme.typography.bodyMedium,
            color = p.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(Space.s2))
        Text(tr("Ändern", "Change"), style = MaterialTheme.typography.labelLarge, color = p.accent)
    }
}

/**
 * Fächer für "Gemischt" wählen: alle Fächer als Raster aus Bild und Name. Gewählte stehen in Farbe mit Haken,
 * die anderen blass. Es bleibt immer mindestens eins gewählt. Änderungen gelten sofort.
 */
@Composable
private fun MixSheet(state: AppState, visible: Boolean, onDismiss: () -> Unit) {
    val p = Funke.colors
    val haptics = LocalHaptics.current
    @Suppress("UNUSED_VARIABLE") val v = state.taskPrefsVersion
    val available = PracticeSubjects.filter { TaskPrefs.subjectOn(it) }
    val chosen = TaskPrefs.mixSubjects().toSet()
    BottomSheet(visible = visible, onDismiss = onDismiss) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(tr("Fächer im Mix", "Subjects in the mix"), style = MaterialTheme.typography.titleLarge, color = p.text, modifier = Modifier.weight(1f))
            if (chosen.size < available.size) {
                TextAction(tr("Alle", "All"), onClick = { state.mixAll(); haptics?.tick() })
            }
        }
        Spacer(Modifier.height(Space.s4))
        available.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.s2)) {
                row.forEach { s ->
                    val on = s in chosen
                    MixChoice(s, on, Modifier.weight(1f)) {
                        if (state.setInMix(s, !on)) haptics?.tick()
                    }
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(Space.s2))
        }
        Spacer(Modifier.height(Space.s3))
        PrimaryButton(tr("Fertig", "Done"), onClick = onDismiss)
    }
}

@Composable
private fun MixChoice(subject: Subject, on: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val p = Funke.colors
    val fade by animateFloatAsState(if (on) 1f else 0.38f, Motion.fade(Motion.Fast), label = "mixChoice")
    val check by animateFloatAsState(if (on) 1f else 0f, Motion.spec(Motion.Fast, Motion.EaseOut), label = "mixCheck")
    Column(
        modifier
            .bouncyClickable(shape = Shapes.md, pressedScale = 0.95f, role = Role.Checkbox, onClick = onClick)
            .semantics { selected = on }
            .padding(vertical = Space.s2),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            SubjectArt(subject, 52.dp, Modifier.graphicsLayer { alpha = fade })
            // Haken unten rechts am Bild
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size(20.dp)
                    .graphicsLayer { alpha = check; scaleX = 0.6f + 0.4f * check; scaleY = 0.6f + 0.4f * check }
                    .clip(CircleShape)
                    .background(p.bg)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(p.accent),
                contentAlignment = Alignment.Center,
            ) {
                Icon(painterResource(R.drawable.ic_check), contentDescription = null, tint = p.onAccent, modifier = Modifier.size(11.dp))
            }
        }
        Spacer(Modifier.height(Space.s2))
        Text(
            subject.label,
            style = MaterialTheme.typography.labelMedium,
            color = if (on) p.text else p.textTertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Die Stufen als Bücher im Regal: von links das dünne Heft bis rechts der dicke Wälzer, jedes etwas höher.
 * Das gewählte Buch hebt sich aus dem Regal (seit 14.0 ruhiger): eine Feder mit ganz leichtem Nachschwingen,
 * wie ein echtes Buch, das man ein Stück herauszieht – ohne Kippen und ohne Lichtstreifen. Dahinter glimmt
 * es leise in seiner Farbe, unten wird sein Schatten aufs Brett weicher. Die anderen sinken ohne Schwingen zurück.
 * Bei "Weniger Bewegung": nur ein kurzes Gleiten.
 */
@Composable
private fun LevelShelf(levels: List<PracticeLevel>, selected: PracticeLevel, tint: Color, onSelect: (PracticeLevel) -> Unit, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val haptics = LocalHaptics.current
    BoxWithConstraints(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        val n = levels.size
        val gap = 10.dp
        val unit = ((maxWidth - gap * (n - 1) - Space.s6) / n).coerceIn(48.dp, 60.dp)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                Modifier.height(236.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(gap),
            ) {
                levels.forEachIndexed { i, lv ->
                    val f = if (n > 1) i / (n - 1f) else 0f
                    Book(
                        level = lv,
                        index = i,
                        chosen = lv == selected,
                        color = bookColor(tint, f, p.isDark),
                        width = unit * (0.86f + 0.14f * f),
                        height = 132.dp + 64.dp * f,
                        onClick = {
                            if (lv != selected) haptics?.tick()
                            onSelect(lv)
                        },
                    )
                }
            }
            // Regalbrett
            Box(
                Modifier
                    .width(unit * n + gap * (n - 1) + Space.s6)
                    .height(12.dp)
                    .surface(Shapes.pill, Elevation.Level2),
            )
        }
    }
}

/** Buchfarben: von hell (Leicht) zu tief (Meister) in der Farbe des Fachs, an Hell/Dunkel angepasst. */
@Composable
private fun bookColor(tint: Color, f: Float, dark: Boolean): Color {
    val p = Funke.colors
    val base = p.tame(tint)
    val light = lerp(base, if (dark) Color(0xFFDCD9FF) else Color.White, if (dark) 0.10f else 0.30f)
    val deep = lerp(base, Color.Black, if (dark) 0.42f else 0.38f)
    return lerp(light, deep, f)
}

/** So weit hebt sich das gewählte Buch. */
private val BookLift = 16.dp

@Composable
private fun Book(level: PracticeLevel, index: Int, chosen: Boolean, color: Color, width: Dp, height: Dp, onClick: () -> Unit) {
    val p = Funke.colors
    val still = reducedMotion
    val cartoon = Funke.cartoon
    // Feder: beim Herausheben ein Hauch Nachschwingen, beim Zurücksinken keins
    val lift by animateFloatAsState(
        if (chosen) 1f else 0f,
        if (still) tween(Motion.Fast, easing = Motion.EaseOut)
        else androidx.compose.animation.core.spring(dampingRatio = if (chosen) 0.62f else 1f, stiffness = if (chosen) 380f else 520f),
        label = "bookLift",
    )
    // Leuchten von hinten: reine Deckkraft, darf auch bei "Weniger Bewegung" weich kommen
    val glow by animateFloatAsState(if (chosen) 1f else 0f, Motion.fade(if (chosen) Motion.Slow else Motion.Fast), label = "bookGlow")
    // Der Moment des Auswählens (seit 15.0): das Buch "ploppt" kurz heraus und ein kleiner Funkenregen steigt auf.
    // Nicht beim ersten Anzeigen, nur wenn man wirklich ein anderes Buch wählt. Bei "Weniger Bewegung" nichts davon.
    val burst = remember { Animatable(1f) }
    val burstScope = androidx.compose.runtime.rememberCoroutineScope()
    val firstRun = remember { booleanArrayOf(true) }
    LaunchedEffect(chosen) {
        if (firstRun[0]) {
            firstRun[0] = false
            return@LaunchedEffect
        }
        if (chosen && !still) {
            // Eigener Ablauf, unabhängig von der Auswahl (seit 15.2): Wählt man schnell ein anderes Buch, läuft der
            // Funkenregen trotzdem bis zum Ende und verglimmt. Früher brach er mitten im Flug ab und ein paar Funken
            // blieben stehen.
            burstScope.launch {
                burst.snapTo(0f)
                burst.animateTo(1f, tween(900, easing = androidx.compose.animation.core.LinearEasing))
            }
        }
    }
    val ink = if (color.luminance() > 0.5f) Color(0xFF1E1D2B) else Color.White
    val shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
    Box(Modifier.size(width, height)) {
        // Leuchten hinter dem Buch, in seiner eigenen Farbe (auf dunklem Grund kräftiger)
        Canvas(Modifier.matchParentSize()) {
            if (glow < 0.01f) return@Canvas
            val c = Offset(size.width / 2f, size.height * 0.45f - BookLift.toPx() * lift)
            val r = size.height * 0.62f
            drawCircle(
                Brush.radialGradient(listOf(color.copy(alpha = (if (p.isDark) 0.34f else 0.22f) * glow), color.copy(alpha = 0f)), center = c, radius = r),
                radius = r,
                center = c,
            )
        }
        // Schatten auf dem Brett unter dem herausgezogenen Buch (im Cartoon-Stil reicht die Lücke)
        if (!cartoon) {
            Canvas(Modifier.matchParentSize()) {
                if (lift < 0.01f) return@Canvas
                val a = (if (p.isDark) 0.40f else 0.18f) * lift
                val w = size.width * 1.1f
                val h = 10.dp.toPx()
                val c = Offset(size.width / 2f, size.height - 1.dp.toPx())
                withTransform({ scale(1f, h / w, c) }) {
                    drawCircle(
                        Brush.radialGradient(listOf(Color.Black.copy(alpha = a), Color.Transparent), center = c, radius = w / 2f),
                        radius = w / 2f,
                        center = c,
                    )
                }
            }
        }
        Box(
            Modifier
                .matchParentSize()
                .graphicsLayer {
                    translationY = -BookLift.toPx() * lift
                    // kurzes Herausploppen: in 200 ms auf 106 %, dann weich zurück
                    val b = burst.value
                    val pop = if (b < 1f) 0.06f * kotlin.math.sin(Math.PI.toFloat() * (b / 0.5f).coerceIn(0f, 1f)) else 0f
                    scaleX = 1f + pop
                    scaleY = 1f + pop
                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 1f)
                }
                .bouncyClickable(shape = shape, pressedScale = 0.97f, role = Role.RadioButton, onClick = onClick)
                .then(if (cartoon) Modifier.hardShadow(shape, p.ink, 3.dp) else Modifier)
                .clip(shape)
                .background(color)
                .then(if (cartoon) Modifier.border(2.dp, p.ink, shape) else Modifier)
                .semantics {
                    selected = chosen
                    contentDescription = level.label
                },
            contentAlignment = Alignment.Center,
        ) {
            // Rundung des Buchrückens: links Licht, rechts Schatten
            Canvas(Modifier.matchParentSize()) {
                drawRect(
                    Brush.horizontalGradient(
                        0f to Color.White.copy(alpha = 0.22f),
                        0.35f to Color.Transparent,
                        0.85f to Color.Transparent,
                        1f to Color.Black.copy(alpha = 0.18f),
                    )
                )
                // Bänder oben und unten
                val band = 3.dp.toPx()
                val y1 = size.height * 0.12f
                val y2 = size.height * 0.84f
                drawRect(ink.copy(alpha = 0.28f), topLeft = Offset(0f, y1), size = Size(size.width, band))
                drawRect(ink.copy(alpha = 0.28f), topLeft = Offset(0f, y1 + band * 2f), size = Size(size.width, band * 0.6f))
                drawRect(ink.copy(alpha = 0.28f), topLeft = Offset(0f, y2), size = Size(size.width, band))
                // Stufe als Punkte unten (Meister: ein Stern)
                val cy = (y2 + size.height) / 2f + band / 2f
                if (level.master) {
                    drawStar(Offset(size.width / 2f, cy), 6.dp.toPx(), ink.copy(alpha = 0.85f))
                } else {
                    val dots = index + 1
                    val r = 2.2.dp.toPx()
                    val step = 6.dp.toPx()
                    val x0 = size.width / 2f - step * (dots - 1) / 2f
                    for (k in 0 until dots) drawCircle(ink.copy(alpha = 0.75f), r, Offset(x0 + step * k, cy))
                }
            }
            // Titel auf dem Rücken, von unten nach oben gelesen
            Text(
                level.label,
                style = MaterialTheme.typography.labelLarge,
                color = ink,
                softWrap = false,
                maxLines = 1,
                modifier = Modifier
                    .wrapContentSize(unbounded = true)
                    .graphicsLayer { rotationZ = -90f },
            )
        }
        // Licht aus dem Buch (seit 15.1): Das gewählte Buch leuchtet oben leise weiter – als käme das Wissen heraus.
        // Beim Wählen steigt ein warmer Lichtschein auf, darin die Funken.
        Canvas(Modifier.matchParentSize()) {
            val origin = Offset(size.width / 2f, -BookLift.toPx() * lift + 2.dp.toPx())
            val warm = lerp(SparkGold, Color.White, 0.35f)
            if (glow > 0.01f) {
                val r = size.width * 0.95f
                drawCircle(
                    Brush.radialGradient(listOf(warm.copy(alpha = (if (p.isDark) 0.30f else 0.38f) * glow), warm.copy(alpha = 0f)), center = origin, radius = r),
                    radius = r,
                    center = origin,
                )
            }
            val b = burst.value
            if (b >= 1f || b <= 0f) return@Canvas
            drawKnowledgeLight(origin, b, size.width, warm, p.isDark)
            // Die Funken sind nach drei Vierteln der Zeit verglommen und werden danach gar nicht mehr gezeichnet.
            val t = b / 0.75f
            if (t < 1f) drawSparkBurst(origin, t, sparkColor = SparkGold, bookColor = color)
        }
    }
}

/**
 * Ein warmer Lichtkegel steigt aus dem oberen Buchrand auf (unten so breit wie das Buch, oben weiter und offen),
 * leuchtet in 200 ms auf und verglimmt dann langsam. [t] läuft von 0 bis 1.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawKnowledgeLight(origin: Offset, t: Float, bookWidth: Float, warm: Color, dark: Boolean) {
    val rise = 1f - (1f - t) * (1f - t)
    val alpha = (if (t < 0.22f) t / 0.22f else 1f - (t - 0.22f) / 0.78f).coerceIn(0f, 1f) * (if (dark) 0.55f else 0.7f)
    if (alpha <= 0.001f) return
    val h = (56.dp.toPx() + 28.dp.toPx() * rise)
    val bottomHalf = bookWidth * 0.42f
    val topHalf = bookWidth * 1.05f
    val beam = androidx.compose.ui.graphics.Path().apply {
        moveTo(origin.x - bottomHalf, origin.y)
        lineTo(origin.x - topHalf, origin.y - h)
        lineTo(origin.x + topHalf, origin.y - h)
        lineTo(origin.x + bottomHalf, origin.y)
        close()
    }
    drawPath(
        beam,
        Brush.verticalGradient(listOf(warm.copy(alpha = 0f), warm.copy(alpha = alpha * 0.55f), warm.copy(alpha = alpha)), startY = origin.y - h, endY = origin.y),
    )
    // heller Kern direkt über dem Buch
    val core = bookWidth * 0.7f
    drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = alpha), Color.White.copy(alpha = 0f)), center = origin, radius = core), core, origin)
}

/**
 * Acht kleine Funken fächern sich vom oberen Buchrand nach oben auf, drehen sich, werden kleiner und verblassen.
 * [t] läuft von 0 bis 1. Feste Winkel und Weiten (kein Zufall), damit es jedes Mal gleich sauber aussieht.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSparkBurst(origin: Offset, t: Float, sparkColor: Color, bookColor: Color) {
    val ease = 1f - (1f - t) * (1f - t) * (1f - t) // Ease-out (kubisch)
    val fade = if (t < 0.45f) 1f else (1f - (t - 0.45f) / 0.55f).coerceIn(0f, 1f)
    for (i in 0 until 8) {
        val f = i / 7f
        val angle = Math.toRadians(-158.0 + 136.0 * f).toFloat()
        val reach = (if (i % 2 == 0) 40.dp else 28.dp).toPx()
        val d = reach * ease
        val c = Offset(origin.x + kotlin.math.cos(angle) * d, origin.y + kotlin.math.sin(angle) * d - 6.dp.toPx() * ease)
        val grow = (t / 0.25f).coerceIn(0f, 1f)
        val r = (if (i % 3 == 0) 5.dp else 3.6.dp).toPx() * grow * (1f - 0.45f * ease)
        val col = if (i % 2 == 0) sparkColor else lerp(bookColor, Color.White, 0.35f)
        val spin = 140f * ease * (if (i % 2 == 0) 1f else -1f)
        rotate(spin, c) {
            val path = androidx.compose.ui.graphics.Path().apply {
                // vierzackiger Glitzerstern
                moveTo(c.x, c.y - r)
                quadraticBezierTo(c.x, c.y, c.x + r, c.y)
                quadraticBezierTo(c.x, c.y, c.x, c.y + r)
                quadraticBezierTo(c.x, c.y, c.x - r, c.y)
                quadraticBezierTo(c.x, c.y, c.x, c.y - r)
                close()
            }
            drawPath(path, col, alpha = fade)
        }
    }
}

/** Fünfzackiger Stern (für den Meister-Band). */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStar(c: Offset, r: Float, color: Color) {
    val path = androidx.compose.ui.graphics.Path()
    for (i in 0 until 10) {
        val rr = if (i % 2 == 0) r else r * 0.45f
        val a = Math.PI / 2 + i * Math.PI / 5
        val x = c.x + (rr * kotlin.math.cos(a)).toFloat()
        val y = c.y - (rr * kotlin.math.sin(a)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
}

// ---------------------------------------------------------------------------------------------
// Lektion
// ---------------------------------------------------------------------------------------------

/** Zuletzt gezogene Aufgaben über Lektionen hinweg (solange die App läuft), damit nichts gleich wiederkommt. */
private val recentIds = ArrayDeque<String>()

@Composable
private fun LessonScreen(state: AppState, id: String, level: PracticeLevel, onExit: () -> Unit, onAgain: () -> Unit) {
    val p = Funke.colors
    val fixed = Subject.entries.firstOrNull { it.id == id }
    val pool = remember(id) {
        (if (fixed == null) TaskPrefs.mixSubjects() else listOf(fixed)).ifEmpty { PracticeSubjects }
    }
    val total = level.lessonSize
    val used = remember { mutableSetOf<String>() }
    var lastSubject by remember { mutableStateOf<Subject?>(null) }

    fun draw(avoid: String?, slot: Int): TaskRun {
        // Gemischt: zuerst das Fach würfeln (nie zweimal hintereinander dasselbe), dann die Aufgabe
        val subject = if (pool.size == 1) pool.first() else pool.filter { it != lastSubject }.random(Random)
        lastSubject = subject
        val lv = Practice.fit(subject, level)
        val task = Tasks.next(
            subject.topics, lv.difficulty, TaskUse.Practice, master = lv.master, avoid = avoid,
            exclude = used + recentIds, slot = slot, of = total, stretch = subject.recall,
        )
        used += task.id
        recentIds.addLast(task.id)
        while (recentIds.size > 30) recentIds.removeFirst()
        return TaskRun(task)
    }

    val runs = remember { mutableStateListOf(draw(null, 0)) }
    val index = runs.lastIndex
    val run = runs.last()
    /** null = aktuelle Aufgabe, sonst eine frühere zum Ansehen */
    var viewing by remember { mutableStateOf<Int?>(null) }
    var counted by remember { mutableStateOf<TaskRun?>(null) }
    var correct by remember { mutableIntStateOf(0) }
    var clean by remember { mutableIntStateOf(0) }
    var sparks by remember { mutableIntStateOf(0) }
    var freezes by remember { mutableIntStateOf(0) }
    var complete by remember { mutableStateOf(false) }
    var showExplain by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }

    BackHandler {
        when {
            showExplain -> showExplain = false
            showHint -> showHint = false
            viewing != null -> viewing = null
            else -> onExit()
        }
    }

    // Ergebnis einer Aufgabe einmal verbuchen
    LaunchedEffect(run, run.phase) {
        showHint = false
        if (counted === run) return@LaunchedEffect
        val subject = run.task.subject
        val lv = Practice.fit(subject, level)
        when (run.phase) {
            TaskPhase.Correct -> {
                counted = run
                TaskHistory.record(subject, lv.difficulty, lv.master, solved = true, clean = run.clean)
                correct++
                if (run.clean) clean++
                sparks += run.sparks
                if (state.recordTaskSolved(run.task.id, run.sparks, run.clean)) freezes++
            }
            TaskPhase.Revealed -> {
                counted = run
                TaskHistory.record(subject, lv.difficulty, lv.master, solved = false, clean = false)
                state.breakPracticeStreak()
                showExplain = true
            }
            else -> Unit
        }
    }

    fun next() {
        showExplain = false
        showHint = false
        viewing = null
        if (index + 1 >= total) complete = true
        else runs.add(draw(run.task.id, index + 1))
    }

    val shown = viewing?.let { runs.getOrNull(it) } ?: run
    val looking = viewing != null

    Box(Modifier.fillMaxSize().background(p.bg)) {
        AnimatedContent(
            targetState = complete,
            transitionSpec = { fadeIn(tween(Motion.Medium, easing = Motion.EaseOut)) togetherWith fadeOut(tween(Motion.Fast)) },
            label = "lessonDone",
        ) { done ->
            if (done) {
                LessonComplete(
                    total = total,
                    correct = correct,
                    perfect = clean == total,
                    sparks = sparks,
                    freezes = freezes,
                    onAgain = onAgain,
                    onDone = onExit,
                )
            } else {
                Column(Modifier.fillMaxSize()) {
                    LessonTopBar(
                        total = total,
                        index = index,
                        currentDone = run.finished,
                        viewing = viewing,
                        onSegment = { i -> viewing = if (i >= index) null else i },
                        onClose = onExit,
                    )
                    Box(Modifier.weight(1f)) {
                        val still = reducedMotion
                        AnimatedContent(
                            targetState = shown,
                            transitionSpec = {
                                if (still) fadeIn(tween(Motion.Fast)) togetherWith fadeOut(tween(Motion.Fast))
                                else (fadeIn(tween(Motion.Medium, 60, Motion.EaseOut)) + slideInHorizontally(tween(Motion.Slow, easing = Motion.EaseOut)) { it / 10 }) togetherWith
                                    (fadeOut(tween(Motion.Fast)) + slideOutHorizontally(tween(Motion.Base, easing = Motion.EaseOut)) { -it / 14 })
                            },
                            label = "lessonTask",
                        ) { r ->
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(start = Space.page, end = Space.page, top = Space.s4, bottom = Space.s6),
                            ) {
                                if (r !== run) {
                                    Text(
                                        tr("Aufgabe ${runs.indexOf(r) + 1} zum Ansehen", "Task ${runs.indexOf(r) + 1} for review"),
                                        style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum, ss03"),
                                        color = p.textTertiary,
                                        modifier = Modifier.padding(bottom = Space.s3),
                                    )
                                }
                                TaskContent(r, TaskMode.Practice)
                            }
                        }
                        // Tipp-Blase: Antippen daneben schließt sie
                        if (showHint && !looking) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { showHint = false }
                            )
                        }
                        if (!looking) HintBubble(run, showHint, onDismiss = { showHint = false }, modifier = Modifier.align(Alignment.BottomStart).padding(bottom = Space.s1))
                    }
                    AnimatedContent(
                        targetState = looking,
                        transitionSpec = { fadeIn(tween(Motion.Base)) togetherWith fadeOut(tween(Motion.Fast)) },
                        label = "lessonDock",
                    ) { l ->
                        if (l) {
                            LookBackDock(onExplain = { showExplain = true }, onCurrent = { viewing = null })
                        } else {
                            TaskDock(
                                run = run,
                                mode = TaskMode.Practice,
                                onContinue = ::next,
                                onExplain = { showExplain = true },
                                onHelper = { showHint = !showHint },
                                continueLabel = if (index + 1 >= total) tr("Abschließen", "Finish") else tr("Weiter", "Continue"),
                            )
                        }
                    }
                }
            }
        }
        if (!complete && !looking) TaskEffects(run)
        BottomSheet(visible = showExplain && !complete, onDismiss = { showExplain = false }) {
            Explanation(shown.task)
            Spacer(Modifier.height(Space.s6))
            if (!looking && run.finished) {
                PrimaryButton(if (index + 1 >= total) tr("Abschließen", "Finish") else tr("Weiter", "Continue"), onClick = ::next)
            }
        }
    }
}

/**
 * Oben in der Lektion: Kreuz, die Abschnitte der Lektion und "2 / 4". Fertige Abschnitte lassen sich antippen,
 * um die Aufgabe noch einmal anzusehen. Kein durchgehender Balken: einzelne weiche Stücke in der Akzentfarbe.
 */
@Composable
private fun LessonTopBar(total: Int, index: Int, currentDone: Boolean, viewing: Int?, onSegment: (Int) -> Unit, onClose: () -> Unit) {
    val p = Funke.colors
    Row(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = Space.s3, end = Space.s5, top = Space.s2, bottom = Space.s2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CloseButton(onClick = onClose, description = tr("Lektion beenden", "End lesson"))
        Spacer(Modifier.width(Space.s3))
        Row(Modifier.weight(1f).height(Sizes.touch), horizontalArrangement = Arrangement.spacedBy(Space.s2), verticalAlignment = Alignment.CenterVertically) {
            for (i in 0 until total) {
                val done = i < index || (i == index && currentDone)
                val fill by animateFloatAsState(if (done) 1f else 0f, Motion.spec(Motion.Medium, Motion.EaseOut), label = "seg$i")
                val focus by animateFloatAsState(if (viewing == i || (viewing == null && i == index)) 1f else 0f, Motion.spec(Motion.Base), label = "segFocus$i")
                val tappable = i < index
                Box(
                    Modifier
                        .weight(1f)
                        .height(Sizes.touch)
                        .then(if (tappable) Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onSegment(i) } else Modifier)
                        .semantics { contentDescription = tr("Aufgabe ${i + 1}", "Task ${i + 1}") },
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(8.dp + 4.dp * focus)
                            .clip(Shapes.pill)
                            .background(p.track),
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth(fill.coerceAtLeast(0.001f))
                                .height(8.dp + 4.dp * focus)
                                .clip(Shapes.pill)
                                .background(p.accent),
                        )
                    }
                }
            }
        }
        Spacer(Modifier.width(Space.s3))
        Text(
            "${(viewing ?: index) + 1} / $total",
            style = MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum, ss03"),
            color = p.textSecondary,
        )
    }
}

/** Beim Ansehen einer früheren Aufgabe: Erklärung oder zurück zur aktuellen. */
@Composable
private fun LookBackDock(onExplain: () -> Unit, onCurrent: () -> Unit) {
    val p = Funke.colors
    Row(
        Modifier
            .fillMaxWidth()
            .background(p.bg)
            .navigationBarsPadding()
            .padding(start = Space.page, end = Space.page, top = Space.s3, bottom = Space.s4)
            .heightIn(min = 64.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LipButton(tr("Erklärung", "Explanation"), onClick = onExplain, tone = LipTone.Neutral, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(Space.s3))
        LipButton(tr("Zur aktuellen", "Back to current"), onClick = onCurrent, tone = LipTone.Accent, modifier = Modifier.weight(1f))
    }
}

/**
 * Lektion geschafft: Lumi auf einem leuchtenden Sockel, darunter die Zahlen (Funken, Richtig, Eis),
 * dazu ein kleiner Akkord und Haptik. Bei einer perfekten Lektion dreht Lumi sich einmal.
 */
@Composable
private fun LessonComplete(total: Int, correct: Int, perfect: Boolean, sparks: Int, freezes: Int, onAgain: () -> Unit, onDone: () -> Unit) {
    val p = Funke.colors
    val sounds = LocalSounds.current
    val haptics = LocalHaptics.current
    val still = reducedMotion
    val glow = remember { Animatable(if (still) 1f else 0f) }
    var cue by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        sounds?.play(Sfx.Complete)
        haptics?.celebrate()
        glow.animateTo(1f, tween(600, easing = Motion.EaseOut))
        if (perfect) {
            delay(150)
            cue++
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = Space.page),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        Box(Modifier.size(240.dp), contentAlignment = Alignment.BottomCenter) {
            Pedestal(glow.value, Modifier.fillMaxSize())
            if (de.funke.app.ui.LocalSidekick.current) {
                Lumi2D(
                    LumiPose.Happy,
                    Modifier.padding(bottom = 28.dp).size(168.dp),
                    tappable = false,
                    cue = cue,
                    cueReaction = LumiCue.Spin,
                )
            } else {
                de.funke.app.ui.components.AnimatedCheckBadge(Modifier.padding(bottom = 72.dp), size = 88.dp)
            }
        }
        Spacer(Modifier.height(Space.s6))
        Text(
            if (perfect) tr("Perfekt!", "Perfect!") else tr("Lektion geschafft!", "Lesson complete!"),
            style = MaterialTheme.typography.headlineMedium,
            color = p.text,
            modifier = Modifier.reveal(120),
        )
        Spacer(Modifier.height(Space.s6))
        Row(horizontalArrangement = Arrangement.spacedBy(Space.s3), modifier = Modifier.fillMaxWidth().reveal(200)) {
            StatCard(R.drawable.ic_sparkles, p.tame(SparkGold), "+$sparks", tr("Funken", "Sparks"), Modifier.weight(1f))
            StatCard(R.drawable.ic_check, p.success, "$correct/$total", tr("Richtig", "Correct"), Modifier.weight(1f))
            if (freezes > 0) StatCard(R.drawable.ic_snowflake, p.tame(de.funke.app.ui.theme.RewardColors.Ice), "+$freezes", tr("Eis", "Freeze"), Modifier.weight(1f))
        }
        Spacer(Modifier.weight(1f))
        PrimaryButton(tr("Noch eine Lektion", "Another lesson"), onClick = onAgain)
        Spacer(Modifier.height(Space.s2))
        TextAction(tr("Fertig", "Done"), onClick = onDone, color = p.textSecondary)
        Spacer(Modifier.height(Space.s3))
    }
}

/** Leuchtender Sockel mit einem weichen Lichtkegel nach oben. */
@Composable
private fun Pedestal(t: Float, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val accent = p.accent
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val base = Offset(w / 2f, h - 24.dp.toPx())
        val cone = androidx.compose.ui.graphics.Path().apply {
            moveTo(base.x - w * 0.30f, base.y)
            lineTo(base.x - w * 0.16f, 0f)
            lineTo(base.x + w * 0.16f, 0f)
            lineTo(base.x + w * 0.30f, base.y)
            close()
        }
        drawPath(
            cone,
            Brush.verticalGradient(
                listOf(Color.Transparent, accent.copy(alpha = (if (p.isDark) 0.22f else 0.16f) * t)),
                startY = 0f,
                endY = base.y,
            ),
        )
        val rx = w * 0.32f
        val ry = 14.dp.toPx()
        drawOval(lerp(accent, Color.Black, 0.35f).copy(alpha = t), topLeft = Offset(base.x - rx, base.y - ry + 8.dp.toPx()), size = Size(rx * 2, ry * 2))
        drawOval(accent.copy(alpha = t), topLeft = Offset(base.x - rx, base.y - ry), size = Size(rx * 2, ry * 2))
        drawOval(
            Brush.radialGradient(listOf(Color.White.copy(alpha = 0.35f * t), Color.Transparent), center = base, radius = rx),
            topLeft = Offset(base.x - rx, base.y - ry),
            size = Size(rx * 2, ry * 2),
        )
    }
}

@Composable
private fun StatCard(icon: Int, tint: Color, value: String, label: String, modifier: Modifier = Modifier) {
    val p = Funke.colors
    Column(
        modifier
            .surface(Shapes.lg, Elevation.Level1)
            .padding(vertical = Space.s4, horizontal = Space.s3),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(painterResource(icon), contentDescription = null, tint = tint, modifier = Modifier.size(Sizes.icon))
        Spacer(Modifier.height(Space.s2))
        Text(value, style = MaterialTheme.typography.titleLarge.copy(fontFeatureSettings = "tnum, ss03"), color = p.text)
        Text(label, style = MaterialTheme.typography.labelMedium, color = p.textTertiary)
    }
}
