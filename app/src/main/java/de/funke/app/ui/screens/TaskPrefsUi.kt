package de.funke.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.funke.app.ui.AppState
import de.funke.app.ui.LocalHaptics
import de.funke.app.ui.challenge.KindPref
import de.funke.app.ui.challenge.PracticeSubjects
import de.funke.app.ui.challenge.Subject
import de.funke.app.ui.challenge.TaskKind
import de.funke.app.ui.challenge.TaskPrefs
import de.funke.app.ui.challenge.Topic
import de.funke.app.ui.challenge.TopicPref
import de.funke.app.ui.challenge.topics
import de.funke.app.ui.components.SectionLabel
import de.funke.app.ui.components.TextAction
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.components.glass
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.tr

/*
 * Einstellungen → Aufgaben → "Deine Auswahl": pro Fach die Lieblings-Unterthemen (Aus · Normal · Mehr · Nur)
 * und welche Aufgabenarten man mag. Standard: alles normal, dann wählt Lumi allein.
 */

/** Gruppen der Physik-Themen, damit die lange Liste übersichtlich bleibt. */
private fun physicsGroups(): List<Pair<String, List<Topic>>> = listOf(
    tr("Mechanik", "Mechanics") to listOf(Topic.Motion, Topic.Forces, Topic.Energy, Topic.Momentum, Topic.Rotation, Topic.Gravity, Topic.Oscillations),
    tr("Stoffe, Wärme & Wellen", "Matter, heat & waves") to listOf(Topic.Matter, Topic.Heat, Topic.Waves, Topic.Optics),
    tr("Elektrizität", "Electricity") to listOf(Topic.Electricity, Topic.Fields, Topic.Magnetism),
    tr("Moderne Physik", "Modern physics") to listOf(Topic.Relativity, Topic.Quantum, Topic.Nuclear, Topic.Astro, Topic.Modern),
)

@Composable
internal fun TaskPrefsSection(state: AppState, subject: Subject, onSubject: (Subject) -> Unit) {
    val p = Funke.colors
    @Suppress("UNUSED_VARIABLE") val v = state.taskPrefsVersion // neu zeichnen bei Änderungen
    SectionLabel(tr("Deine Auswahl", "Your mix"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s1))
    Text(
        tr(
            "Normalerweise mischt Lumi alle Themen. Hier kannst du Lieblingsthemen öfter oder ausschließlich üben.",
            "Normally Lumi mixes all topics. Here you can practise favourite topics more often – or only those.",
        ),
        style = MaterialTheme.typography.bodySmall,
        color = p.textTertiary,
        modifier = Modifier.padding(horizontal = Space.s2),
    )
    Spacer(Modifier.height(Space.s3))
    // Fach wählen
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Space.s2),
    ) {
        PracticeSubjects.forEach { s ->
            de.funke.app.ui.components.Chip(
                s.label + if (TaskPrefs.customized(s) && s.topics.any { TaskPrefs.topic(it) != TopicPref.Normal }) " •" else "",
                selected = s == subject,
                onClick = { onSubject(s) },
            )
        }
    }
    Spacer(Modifier.height(Space.s4))

    val groups = if (subject == Subject.Physics) physicsGroups() else listOf("" to subject.topics.sortedBy { it.ordinal })
    groups.forEach { (title, list) ->
        if (title.isNotEmpty()) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = p.textTertiary, modifier = Modifier.padding(start = Space.s2, top = Space.s2, bottom = Space.s2))
        }
        Column(Modifier.fillMaxWidth().glass(Shapes.card)) {
            list.forEachIndexed { i, topic ->
                if (i > 0) Box(Modifier.padding(start = Space.s4).fillMaxWidth().height(1.dp).background(p.divider))
                TopicPrefRow(topic, TaskPrefs.topic(topic)) { state.setTopicPref(topic, it) }
            }
        }
        Spacer(Modifier.height(Space.s3))
    }

    Spacer(Modifier.height(Space.s4))
    SectionLabel(tr("Aufgabenarten", "Task types"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    Column(Modifier.fillMaxWidth().glass(Shapes.card)) {
        TaskKind.entries.forEachIndexed { i, kind ->
            if (i > 0) Box(Modifier.padding(start = Space.s4).fillMaxWidth().height(1.dp).background(p.divider))
            PrefRow(kind.label, kind.hint, KindPref.entries.map { it.label }, TaskPrefs.kind(kind).ordinal) {
                state.setKindPref(kind, KindPref.entries[it])
            }
        }
    }
    Spacer(Modifier.height(Space.s2))
    TextAction(tr("Alles zurück auf „Lumi wählt“", "Reset everything to “Lumi picks”"), onClick = { state.resetTaskPrefs(null) })
}

@Composable
private fun TopicPrefRow(topic: Topic, pref: TopicPref, onPick: (TopicPref) -> Unit) {
    val order = listOf(TopicPref.Off, TopicPref.Normal, TopicPref.More, TopicPref.Only)
    PrefRow(topic.label, topic.hint, order.map { it.label }, order.indexOf(pref)) { onPick(order[it]) }
}

/** Zeile: Titel und kurze Erklärung, darunter ein kleiner Segment-Schalter. */
@Composable
private fun PrefRow(title: String, hint: String, options: List<String>, selected: Int, onSelect: (Int) -> Unit) {
    val p = Funke.colors
    Column(Modifier.fillMaxWidth().padding(horizontal = Space.s4, vertical = Space.s3)) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = p.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(hint, style = MaterialTheme.typography.bodySmall, color = p.textTertiary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(Space.s2))
        MiniSegments(options, selected, onSelect)
    }
}

/**
 * Kleiner Segment-Schalter (36 dp): die Auswahl gleitet als getönte Fläche hinüber (200 ms, Ease-out).
 * "Normal" bleibt neutral, alles andere wird in Akzent markiert.
 */
@Composable
private fun MiniSegments(options: List<String>, selected: Int, onSelect: (Int) -> Unit) {
    val p = Funke.colors
    val haptics = LocalHaptics.current
    val still = de.funke.app.ui.theme.reducedMotion
    BoxWithConstraints(
        Modifier
            .fillMaxWidth()
            .height(36.dp)
            .clip(Shapes.pill)
            .background(p.track)
            .padding(2.dp),
    ) {
        val w = maxWidth / options.size
        val x by animateDpAsState(w * selected, if (still) tween(0) else tween(Motion.Base, easing = Motion.EaseOut), label = "miniSeg")
        val neutral = options.size == 4 && selected == 1 || options.size == 3 && selected == 1
        val fill by animateColorAsState(if (neutral) p.raised(de.funke.app.ui.theme.Elevation.Level2) else p.accent.copy(alpha = if (p.isDark) 0.26f else 0.18f), Motion.fade(Motion.Base), label = "miniFill")
        Box(
            Modifier
                .offset(x = x)
                .width(w)
                .fillMaxHeight()
                .clip(Shapes.pill)
                .background(fill)
                .then(if (neutral) Modifier else Modifier.border(1.dp, p.accent.copy(alpha = 0.4f), Shapes.pill)),
        )
        Row(Modifier.fillMaxWidth().fillMaxHeight()) {
            options.forEachIndexed { i, label ->
                val on = i == selected
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(Shapes.pill)
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, role = Role.RadioButton) {
                            if (!on) {
                                haptics?.tick()
                                onSelect(i)
                            }
                        }
                        .semantics { this.selected = on; contentDescription = label },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified),
                        color = when {
                            on && !neutral -> p.accent
                            on -> p.text
                            else -> p.textTertiary
                        },
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

/** Runder Knopf oben im Fach: öffnet die Auswahl. Ein Punkt zeigt, dass etwas eingestellt ist. */
@Composable
internal fun TuneButton(customized: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val p = Funke.colors
    Box(modifier.size(de.funke.app.ui.theme.Sizes.buttonSmall)) {
        Box(
            Modifier
                .size(de.funke.app.ui.theme.Sizes.buttonSmall)
                .bouncyClickable(shape = androidx.compose.foundation.shape.CircleShape, pressedScale = 0.92f, onClick = onClick)
                .glass(androidx.compose.foundation.shape.CircleShape)
                .semantics { contentDescription = tr("Themen und Aufgabenarten wählen", "Choose topics and task types") },
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.material3.Icon(
                androidx.compose.ui.res.painterResource(de.funke.app.R.drawable.ic_tune),
                contentDescription = null,
                tint = p.textSecondary,
                modifier = Modifier.size(de.funke.app.ui.theme.Sizes.icon),
            )
        }
        if (customized) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 2.dp, end = 2.dp)
                    .size(10.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(p.accent),
            )
        }
    }
}
