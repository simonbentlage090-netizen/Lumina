package de.funke.app.ui.screens

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import de.funke.app.R
import de.funke.app.lock.SessionLock
import de.funke.app.notify.Days
import de.funke.app.notify.Delivery
import de.funke.app.notify.Reminder
import de.funke.app.notify.ReminderKind
import de.funke.app.notify.ReminderStore
import de.funke.app.notify.Reminders
import de.funke.app.notify.UsageLimit
import de.funke.app.ui.AppState
import de.funke.app.ui.LocalHaptics
import de.funke.app.ui.components.BottomSheet
import de.funke.app.ui.components.PillButton
import de.funke.app.ui.components.PrimaryButton
import de.funke.app.ui.components.SectionLabel
import de.funke.app.ui.components.TextAction
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.components.glass
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Sizes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.theme.reducedMotion
import de.funke.app.ui.tr
import de.funke.app.usage.GroupConfig
import de.funke.app.usage.ScreenTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/*
 * Denkpause → Erinnerungen (seit 14.0 neu gestaltet).
 *
 * Wie eine Weckerliste: links groß die Uhrzeit, daneben der Name und wann/wie ("Täglich als Pop-up"),
 * rechts der Schalter. Die Namen sagen schon, wann eine eingebaute Erinnerung kommt ("Tagesziel" nur,
 * wenn es offen ist, "Streak retten" nur, wenn er sonst reißt) – keine Erklärsätze mehr auf der Seite.
 * Fehlt ein Recht, steht ganz oben eine Karte mit genau den Zeilen, die fehlen.
 *
 * Editor: große Uhrzeit in 15-Minuten-Schritten (gedrückt halten = schnell), Wochentage, Pop-up oder
 * Vollbild, "Jetzt testen" und "In 1 Min." (echter Weg über den Wecker) und eine Zeile, was zuletzt passiert ist.
 */

/** Welches Blatt gerade offen ist (liegt in GateTab ganz oben, damit es über allem ist). */
@Stable
internal class ReminderSheets {
    var editing by mutableStateOf<Reminder?>(null)
    var isNew by mutableStateOf(false)
    var usage by mutableStateOf(false)
    var pickingApps by mutableStateOf(false)
    val open: Boolean get() = editing != null || usage
    fun close() { editing = null; usage = false; isNew = false }
}

@Composable
internal fun rememberReminderSheets(): ReminderSheets = remember { ReminderSheets() }

/** Was im System erlaubt ist – bei jeder Rückkehr in die App neu nachgesehen. */
private class Rights(
    val notify: Boolean,
    val popup: Boolean,
    val exact: Boolean,
    val battery: Boolean,
    val service: Boolean,
    val usage: Boolean,
)

@Composable
private fun rememberRights(): Rights {
    val context = LocalContext.current
    // Jede Abfrage einzeln abgesichert: ein Gerät, das eine davon nicht kennt, darf die Seite nicht abstürzen lassen
    fun read(): Rights {
        runCatching { Reminders.ensureChannel(context) }
        return Rights(
            notify = runCatching { Reminders.allowed(context) }.getOrDefault(true),
            popup = runCatching { Reminders.popupAllowed(context) }.getOrDefault(true),
            exact = runCatching { Reminders.exactOk(context) }.getOrDefault(true),
            battery = runCatching { Reminders.batteryOk(context) }.getOrDefault(true),
            service = runCatching { SessionLock.isServiceEnabled(context) }.getOrDefault(false),
            usage = runCatching { ScreenTime.hasAccess(context) }.getOrDefault(false),
        )
    }
    var rights by remember { mutableStateOf(read()) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val obs = LifecycleEventObserver { _, e -> if (e == Lifecycle.Event.ON_RESUME) rights = read() }
        lifecycle.addObserver(obs)
        onDispose { lifecycle.removeObserver(obs) }
    }
    return rights
}

@Composable
internal fun RemindersPage(state: AppState, sheets: ReminderSheets) {
    val context = LocalContext.current
    val rights = rememberRights()
    val version = state.remindersVersion
    val list = remember(version) { runCatching { state.reminders() }.getOrDefault(emptyList()) }
    val usage = remember(version) { runCatching { state.usageLimit() }.getOrNull() }
    var notifyAsked by remember { mutableStateOf(false) }
    var guide by remember { mutableStateOf(false) }
    if (guide) AccessibilityGuide(onDismiss = { guide = false })
    val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { notifyAsked = true }

    // ----- Fehlende Rechte (nur die, die für die eingeschalteten Erinnerungen nötig sind) -----
    val active = list.filter { it.on }
    val usageOn = usage?.on == true
    val anyOn = active.isNotEmpty() || usageOn
    val deliveries = active.map { it.delivery } + listOfNotNull(usage?.takeIf { it.on }?.delivery)
    val needsNotify = anyOn && !rights.notify
    val needsPopup = !needsNotify && deliveries.any { it == Delivery.Notify } && !rights.popup
    val needsExact = active.isNotEmpty() && !rights.exact
    val needsService = deliveries.any { it == Delivery.Screen } && !rights.service
    val needsUsage = usageOn && !rights.usage
    val samsung = android.os.Build.MANUFACTURER.equals("samsung", ignoreCase = true)
    val needsBattery = anyOn && !rights.battery
    if (needsNotify || needsPopup || needsExact || needsService || needsUsage || needsBattery) {
        ListCard {
            var first = true
            fun sep(): Boolean { val f = first; first = false; return !f }
            if (needsNotify) {
                if (sep()) CardDivider()
                FixRow(tr("Benachrichtigungen erlauben", "Allow notifications"), tr("Sonst kommt nichts an", "Otherwise nothing gets through")) {
                    if (android.os.Build.VERSION.SDK_INT >= 33 && !notifyAsked &&
                        androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) !=
                        android.content.pm.PackageManager.PERMISSION_GRANTED
                    ) notifLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    else Reminders.openSystemSettings(context)
                }
            }
            if (needsPopup) {
                if (sep()) CardDivider()
                FixRow(tr("Pop-up einschalten", "Turn on pop-ups"), tr("Im System steht Lumina auf leise", "Lumina is set to silent in the system")) {
                    Reminders.openChannelSettings(context)
                }
            }
            if (needsExact) {
                if (sep()) CardDivider()
                FixRow(tr("Pünktliche Erinnerungen erlauben", "Allow on-time reminders"), tr("Sonst kommen sie oft zu spät", "Otherwise they often come late")) { Reminders.openExactSettings(context) }
            }
            if (needsService) {
                if (sep()) CardDivider()
                FixRow(tr("Bedienungshilfe einschalten", "Turn on accessibility service"), tr("Für Vollbild: ", "For full screen: ") + SessionLock.accessibilityPath(context)) { guide = true }
            }
            if (needsUsage) {
                if (sep()) CardDivider()
                FixRow(tr("Nutzungsdaten erlauben", "Allow usage access"), tr("Damit Lumi mitzählen kann", "So Lumi can count along")) { ScreenTime.openSettings(context) }
            }
            if (needsBattery) {
                if (sep()) CardDivider()
                FixRow(
                    tr("Vom Energiesparen ausnehmen", "Exclude from battery saving"),
                    if (samsung) tr("Samsung legt Apps sonst schlafen", "Samsung otherwise puts apps to sleep") else tr("Damit nichts verschluckt wird", "So nothing gets swallowed"),
                    soft = true,
                ) { Reminders.requestBatteryExemption(context) }
            }
        }
        Spacer(Modifier.height(Space.s4))
    }

    // ----- Liste wie beim Wecker -----
    Column(verticalArrangement = Arrangement.spacedBy(Space.s3)) {
        list.forEach { r ->
            ReminderCard(
                big = timeText(r.hour, r.minute),
                title = titleFor(r),
                line = scheduleLine(r.days, r.delivery),
                on = r.on,
                onToggle = { state.saveReminder(r.copy(on = !r.on)) },
                onOpen = { sheets.isNew = false; sheets.editing = r },
            )
            // Das Nutzungslimit steht nach den eingebauten Uhrzeit-Erinnerungen, vor den eigenen
            if (r.kind == ReminderKind.Bed && usage != null) UsageCard(state, usage) { sheets.usage = true }
        }
        if (list.none { it.kind == ReminderKind.Bed } && usage != null) UsageCard(state, usage) { sheets.usage = true }
        AddReminderRow {
            sheets.isNew = true
            sheets.editing = Reminder(
                id = ReminderStore.newCustomId(), kind = ReminderKind.Custom, on = true,
                minuteOfDay = nextQuarterHour(), days = Days.ALL, title = "", text = "", delivery = Delivery.Notify,
            )
        }
    }
}

/** Nächste volle Viertelstunde ab jetzt (+1 h), als Vorschlag für eine neue Erinnerung. */
private fun nextQuarterHour(): Int {
    val now = java.time.LocalTime.now()
    val m = now.hour * 60 + now.minute + 60
    return ((m + 14) / 15 * 15) % 1440
}

@Composable
private fun UsageCard(state: AppState, u: UsageLimit, onOpen: () -> Unit) {
    val context = LocalContext.current
    val target = remember(u) { runCatching { Reminders.usageTargetLabel(context, u) }.getOrDefault("") }
    ReminderCard(
        big = durationText(u.limitMin),
        title = tr("Nutzungslimit", "Usage limit"),
        line = deliveryPhrase(target, u.delivery),
        on = u.on,
        onToggle = { state.saveUsageLimit(u.copy(on = !u.on)) },
        onOpen = onOpen,
    )
}

/** Namen, die schon sagen, wann die eingebauten kommen. */
private fun titleFor(r: Reminder): String = when (r.kind) {
    ReminderKind.Read -> tr("Tagesziel", "Daily goal")
    ReminderKind.Streak -> tr("Streak retten", "Save your streak")
    ReminderKind.Bed -> tr("Schlafenszeit", "Bedtime")
    ReminderKind.Usage -> tr("Nutzungslimit", "Usage limit")
    ReminderKind.Custom -> r.title.ifBlank { tr("Erinnerung", "Reminder") }
}

/** Eine kurze Zeile im Editor unter dem Namen – nur, wo die Bedingung nicht offensichtlich ist. */
private fun conditionFor(r: Reminder): String? = when (r.kind) {
    ReminderKind.Read -> tr("Nur, wenn es heute noch offen ist.", "Only if it's still open today.")
    ReminderKind.Streak -> tr("Nur, wenn dein Streak sonst reißt.", "Only if your streak would break.")
    else -> null
}

/** "Täglich als Pop-up", "Mo–Fr im Vollbild" */
private fun scheduleLine(days: Int, d: Delivery): String =
    deliveryPhrase(Days.label(days).replaceFirstChar { it.uppercase() }, d)

private fun deliveryPhrase(what: String, d: Delivery): String = when (d) {
    Delivery.Notify -> tr("$what als Pop-up", "$what, pop-up")
    Delivery.Screen -> tr("$what im Vollbild", "$what, full screen")
}

/** "45 min", "2 h", "2 h 15 min" */
internal fun durationText(min: Int): String {
    val h = min / 60
    val m = min % 60
    return when {
        h == 0 -> "$m min"
        m == 0 -> "$h h"
        else -> "$h h $m min"
    }
}

/**
 * Eine Zeile der Liste: links groß die Uhrzeit (oder das Limit), daneben Name und "Täglich als Pop-up".
 * Die ganze Karte öffnet den Editor, der Schalter rechts hat seine eigene Tippfläche.
 * Aus: alles tritt leise zurück (Farben gleiten in 200 ms).
 */
@Composable
private fun ReminderCard(big: String, title: String, line: String, on: Boolean, onToggle: () -> Unit, onOpen: () -> Unit) {
    val p = Funke.colors
    val bigColor by animateColorAsState(if (on) p.text else p.textTertiary, Motion.fade(Motion.Base), label = "big")
    val titleColor by animateColorAsState(if (on) p.text else p.textSecondary, Motion.fade(Motion.Base), label = "title")
    Row(
        Modifier
            .fillMaxWidth()
            .bouncyClickable(shape = Shapes.card, pressedScale = 0.985f, onClick = onOpen)
            .glass(Shapes.card)
            .padding(start = Space.s5, top = Space.s4, bottom = Space.s4, end = Space.s2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            big,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Medium, fontFeatureSettings = "tnum, ss03"),
            color = bigColor,
            maxLines = 1,
            modifier = Modifier.widthIn(min = 84.dp),
        )
        Spacer(Modifier.width(Space.s3))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = titleColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(line, style = MaterialTheme.typography.bodySmall, color = p.textTertiary, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        // Eigene Tippfläche für den Schalter (48 dp), damit Schalten nicht den Editor öffnet
        Box(
            Modifier
                .padding(start = Space.s2)
                .heightIn(min = Sizes.touch)
                .bouncyClickable(shape = Shapes.pill, pressedScale = 0.96f, role = Role.Switch, onClick = onToggle)
                .padding(horizontal = Space.s2)
                .semantics {
                    contentDescription = title
                    stateDescription = if (on) tr("An", "On") else tr("Aus", "Off")
                },
            contentAlignment = Alignment.Center,
        ) { LuminaSwitch(on) }
    }
}

/** "+ Eigene Erinnerung" – das Plus steht dort, wo sonst die Uhrzeiten stehen. */
@Composable
private fun AddReminderRow(onClick: () -> Unit) {
    val p = Funke.colors
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .bouncyClickable(shape = Shapes.card, pressedScale = 0.985f, onClick = onClick)
            // seit 15.1 ohne Umrandung: Plus und Wort in Akzentfarbe reichen als Einladung
            .padding(start = Space.s5, end = Space.s4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.widthIn(min = 84.dp)) {
            Icon(painterResource(R.drawable.ic_plus), contentDescription = null, tint = p.accent, modifier = Modifier.size(Sizes.iconLg))
        }
        Spacer(Modifier.width(Space.s3))
        Text(tr("Eigene Erinnerung", "Custom reminder"), style = MaterialTheme.typography.titleSmall, color = p.accent)
    }
}

// ---------------------------------------------------------------------------------------------
// Blätter: Editor und Nutzungslimit
// ---------------------------------------------------------------------------------------------

@Composable
internal fun ReminderSheetsHost(state: AppState, sheets: ReminderSheets) {
    BottomSheet(visible = sheets.open, onDismiss = { sheets.close() }) {
        val r = sheets.editing
        when {
            r != null -> ReminderEditor(
                state = state,
                initial = r,
                isNew = sheets.isNew,
                onAdded = { sheets.close() },
                onDeleted = { sheets.close() },
            )
            sheets.usage -> state.usageLimit()?.let { UsageEditor(state, it, onPickApps = { sheets.pickingApps = true }) }
        }
    }
    AnimatedVisibility(visible = sheets.pickingApps, enter = Motion.sheetEnter(false), exit = Motion.sheetExit(false)) {
        val u = state.usageLimit()
        if (u != null) {
            GateAppPicker(u.apps, title = tr("Welche Apps zählen?", "Which apps count?")) { chosen ->
                state.saveUsageLimit(u.copy(apps = chosen, on = true))
                sheets.pickingApps = false
            }
        }
    }
}

@Composable
private fun ColumnScope.ReminderEditor(state: AppState, initial: Reminder, isNew: Boolean, onAdded: () -> Unit, onDeleted: () -> Unit) {
    val p = Funke.colors
    val context = LocalContext.current
    val rights = rememberRights()
    var r by remember(initial.id) { mutableStateOf(initial) }
    var testInfo by remember(initial.id) { mutableStateOf<String?>(null) }
    var testTick by remember(initial.id) { mutableIntStateOf(0) }
    var guide by remember { mutableStateOf(false) }
    if (guide) AccessibilityGuide(onDismiss = { guide = false })
    // Bestehende Erinnerungen speichern gleich mit (kurz gesammelt, damit schnelles Weiterzählen nicht bei
    // jedem Schritt alle Wecker neu stellt); jede Änderung schaltet sie auch ein
    var dirty by remember(initial.id) { mutableStateOf(false) }
    fun update(next: Reminder) {
        r = next.copy(on = true)
        testInfo = null
        if (!isNew) dirty = true
    }
    LaunchedEffect(r, dirty) {
        if (dirty) {
            delay(350)
            state.saveReminder(r)
            dirty = false
        }
    }
    DisposableEffect(initial.id) { onDispose { if (dirty) state.saveReminder(r) } }

    Text(if (isNew) tr("Neue Erinnerung", "New reminder") else titleFor(r), style = MaterialTheme.typography.titleLarge, color = p.text)
    conditionFor(r)?.let {
        Text(it, style = MaterialTheme.typography.bodyMedium, color = p.textSecondary)
    }
    Spacer(Modifier.height(Space.s6))

    BigValueStepper(
        text = timeText(r.hour, r.minute),
        minusDescription = tr("15 Minuten früher", "15 minutes earlier"),
        plusDescription = tr("15 Minuten später", "15 minutes later"),
        onMinus = { update(r.copy(minuteOfDay = (r.minuteOfDay - 15 + 1440) % 1440)) },
        onPlus = { update(r.copy(minuteOfDay = (r.minuteOfDay + 15) % 1440)) },
    )
    Spacer(Modifier.height(Space.s6))

    if (r.isCustom) {
        QuietField(value = r.title, placeholder = tr("Titel", "Title"), maxLength = 40, singleLine = true) { update(r.copy(title = it)) }
        Spacer(Modifier.height(Space.s2))
        QuietField(value = r.text, placeholder = tr("Nachricht (optional)", "Message (optional)"), maxLength = 140, singleLine = false) { update(r.copy(text = it)) }
        Spacer(Modifier.height(Space.s6))
    }

    DaysPicker(r.days) { update(r.copy(days = it)) }
    Spacer(Modifier.height(Space.s6))

    DeliveryPicker(r.delivery, rights, onGuide = { guide = true }) { update(r.copy(delivery = it)) }
    Spacer(Modifier.height(Space.s6))

    TestButtons(
        onNow = {
            Reminders.testReminder(context, r)
            testInfo = nowMessage(r.delivery, rights)
            testTick++
        },
        onLater = {
            val at = Reminders.testReminderLater(context, r)
            testInfo = laterMessage(at, r.delivery, rights)
            testTick++
        },
    )
    val last = remember(r.id, testTick, state.remindersVersion) { lastLine(context, r.id) }
    (testInfo ?: last)?.let {
        Spacer(Modifier.height(Space.s3))
        Text(it, style = MaterialTheme.typography.bodySmall, color = p.textTertiary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
    if (isNew) {
        Spacer(Modifier.height(Space.s4))
        PrimaryButton(
            tr("Hinzufügen", "Add"),
            onClick = {
                state.saveReminder(r.copy(on = true))
                onAdded()
            },
            modifier = Modifier.fillMaxWidth(),
        )
    } else if (r.isCustom) {
        Spacer(Modifier.height(Space.s3))
        TextAction(
            tr("Löschen", "Delete"),
            onClick = {
                state.deleteReminder(r.id)
                onDeleted()
            },
            color = p.danger,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

private fun nowMessage(d: Delivery, rights: Rights): String? = when {
    !rights.notify && (d == Delivery.Notify || !rights.service) -> tr("Benachrichtigungen sind aus.", "Notifications are off.")
    d == Delivery.Notify && !rights.popup -> tr("Im System auf leise gestellt – sie landet nur in der Leiste.", "Set to silent in the system – it only lands in the shade.")
    else -> null
}

private fun laterMessage(at: Long, d: Delivery, rights: Rights): String {
    if (at == 0L) return tr("Ging gerade nicht. Versuch es gleich noch mal.", "Didn't work just now. Try again in a moment.")
    if (!rights.notify && (d == Delivery.Notify || !rights.service)) return tr("Benachrichtigungen sind aus.", "Notifications are off.")
    val t = Instant.ofEpochMilli(at).atZone(ZoneId.systemDefault())
    val time = timeText(t.hour, t.minute)
    return when {
        d == Delivery.Screen && !rights.service -> tr("Kommt um $time – als Pop-up, solange die Bedienungshilfe aus ist.", "Arrives at $time – as a pop-up while the accessibility service is off.")
        else -> tr("Kommt um $time. Du kannst das Handy so lange weglegen.", "Arrives at $time. You can put your phone away until then.")
    }
}

/** Was zuletzt mit dieser Erinnerung passiert ist – eine leise Zeile, nur wenn es etwas zu sagen gibt. */
private fun lastLine(context: Context, id: String): String? {
    val (at, outcome) = Reminders.lastOutcome(context, id) ?: return null
    val t = Instant.ofEpochMilli(at).atZone(ZoneId.systemDefault())
    val day = when (ChronoUnit.DAYS.between(t.toLocalDate(), LocalDate.now())) {
        0L -> tr("heute", "today")
        1L -> tr("gestern", "yesterday")
        else -> "%02d.%02d.".format(t.dayOfMonth, t.monthValue)
    }
    val time = timeText(t.hour, t.minute)
    val text = when (outcome) {
        Reminders.Outcome.Popup -> tr("Zuletzt $day, $time als Pop-up", "Last $day, $time as a pop-up")
        Reminders.Outcome.Screen -> tr("Zuletzt $day, $time im Vollbild", "Last $day, $time full screen")
        Reminders.Outcome.Waiting ->
            if (System.currentTimeMillis() - at < 6 * 60 * 60_000L) tr("Wartet seit $time aufs Entsperren", "Waiting since $time for you to unlock")
            else tr("$day, $time verpasst – das Handy blieb gesperrt", "Missed $day, $time – the phone stayed locked")
        Reminders.Outcome.Done -> tr("$day, $time nicht nötig – schon geschafft", "Not needed $day, $time – already done")
        Reminders.Outcome.Busy -> tr("$day, $time ausgelassen – du warst im Fokus", "Skipped $day, $time – you were focusing")
        Reminders.Outcome.Blocked -> tr("$day, $time blockiert – Benachrichtigungen sind aus", "Blocked $day, $time – notifications are off")
    }
    return text.replaceFirstChar { it.uppercase() }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColumnScope.UsageEditor(state: AppState, initial: UsageLimit, onPickApps: () -> Unit) {
    val p = Funke.colors
    val context = LocalContext.current
    val rights = rememberRights()
    var u by remember { mutableStateOf(initial) }
    var testInfo by remember { mutableStateOf<String?>(null) }
    var testTick by remember { mutableIntStateOf(0) }
    var guide by remember { mutableStateOf(false) }
    if (guide) AccessibilityGuide(onDismiss = { guide = false })
    var dirty by remember { mutableStateOf(false) }
    fun update(next: UsageLimit) {
        u = next.copy(on = true)
        testInfo = null
        dirty = true
    }
    LaunchedEffect(u, dirty) {
        if (dirty) {
            delay(350)
            state.saveUsageLimit(u)
            dirty = false
        }
    }
    DisposableEffect(Unit) { onDispose { if (dirty) state.saveUsageLimit(u) } }
    // Wenn die App-Auswahl zurückkommt, steht der neue Stand im Speicher
    LaunchedEffect(state.remindersVersion) {
        state.usageLimit()?.let { saved -> if (saved.apps != u.apps) u = u.copy(apps = saved.apps) }
    }

    Text(tr("Nutzungslimit", "Usage limit"), style = MaterialTheme.typography.titleLarge, color = p.text)
    if (!rights.usage) {
        Spacer(Modifier.height(Space.s3))
        ListCard { FixRow(tr("Nutzungsdaten erlauben", "Allow usage access"), tr("Damit Lumi mitzählen kann", "So Lumi can count along")) { ScreenTime.openSettings(context) } }
    }
    Spacer(Modifier.height(Space.s6))

    BigValueStepper(
        text = durationText(u.limitMin),
        minusDescription = tr("15 Minuten weniger", "15 minutes less"),
        plusDescription = tr("15 Minuten mehr", "15 minutes more"),
        onMinus = { update(u.copy(limitMin = (u.limitMin - 15).coerceIn(15, 12 * 60))) },
        onPlus = { update(u.copy(limitMin = (u.limitMin + 15).coerceIn(15, 12 * 60))) },
        caption = tr("am Tag", "per day"),
        compact = true,
    )
    Spacer(Modifier.height(Space.s6))

    SectionLabel(tr("Was zählt", "What counts"), Modifier.padding(start = Space.s1))
    Spacer(Modifier.height(Space.s2))
    val config = remember {
        runCatching {
            GroupConfig.fromJson(context.getSharedPreferences("funke", android.content.Context.MODE_PRIVATE).getString("app_groups", null))
        }.getOrElse { GroupConfig.fromJson(null) }
    }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(Space.s2), verticalArrangement = Arrangement.spacedBy(Space.s2)) {
        config.groups.filter { it.id != de.funke.app.usage.AppGroups.OTHER }.forEach { g ->
            val chosen = g.id in u.groups
            SelectChip(g.name, chosen) { update(u.copy(groups = if (chosen) u.groups - g.id else u.groups + g.id)) }
        }
    }
    Spacer(Modifier.height(Space.s3))
    ListCard {
        ListRow(R.drawable.ic_nav_screen, tr("Einzelne Apps", "Single apps"), onClick = onPickApps) {
            ValueText(
                when (u.apps.size) {
                    0 -> tr("Keine", "None")
                    1 -> tr("1 App", "1 app")
                    else -> tr("${u.apps.size} Apps", "${u.apps.size} apps")
                },
                accent = u.apps.isEmpty(),
            )
        }
    }
    if (!u.hasTargets) {
        Spacer(Modifier.height(Space.s2))
        Text(tr("Wähle mindestens eine Gruppe oder App.", "Pick at least one group or app."), style = MaterialTheme.typography.bodySmall, color = p.danger, modifier = Modifier.padding(horizontal = Space.s1))
    }
    Spacer(Modifier.height(Space.s3))
    ToggleRow(tr("Alle 30 Min. wiederholen", "Repeat every 30 min"), u.repeat) { update(u.copy(repeat = it)) }
    Spacer(Modifier.height(Space.s6))

    DeliveryPicker(u.delivery, rights, onGuide = { guide = true }) { update(u.copy(delivery = it)) }
    Spacer(Modifier.height(Space.s6))
    TestButtons(
        enabled = u.hasTargets,
        onNow = {
            Reminders.testUsage(context, u)
            testInfo = nowMessage(u.delivery, rights)
            testTick++
        },
        onLater = {
            val at = Reminders.testUsageLater(context, u)
            testInfo = laterMessage(at, u.delivery, rights)
            testTick++
        },
    )
    val last = remember(testTick, state.remindersVersion) { lastLine(context, Reminders.USAGE_ID) }
    (testInfo ?: last)?.let {
        Spacer(Modifier.height(Space.s3))
        Text(it, style = MaterialTheme.typography.bodySmall, color = p.textTertiary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}

// ---------------------------------------------------------------------------------------------
// Kleine Bausteine
// ---------------------------------------------------------------------------------------------

/**
 * Große Zahl in der Mitte, rund − und + daneben (wie ein Wecker). Gedrückt halten zählt schnell weiter.
 * Die Zahl gleitet in die Richtung, in die man drückt (200 ms ease-out), bei weniger Bewegung nur Überblenden.
 */
@Composable
private fun BigValueStepper(
    text: String,
    minusDescription: String,
    plusDescription: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    caption: String? = null,
    compact: Boolean = false,
) {
    val p = Funke.colors
    val still = reducedMotion
    var dir by remember { mutableIntStateOf(1) }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        RepeatStep("−", minusDescription) { dir = -1; onMinus() }
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedContent(
                targetState = text,
                transitionSpec = {
                    if (still) fadeIn(tween(Motion.Fast)) togetherWith fadeOut(tween(Motion.Fast))
                    else (slideInVertically(tween(Motion.Base, easing = Motion.EaseOut)) { h -> h * dir / 3 } + fadeIn(tween(Motion.Base, easing = Motion.EaseOut))) togetherWith
                        (slideOutVertically(tween(Motion.Fast, easing = Motion.EaseOut)) { h -> -h * dir / 3 } + fadeOut(tween(Motion.Fast)))
                },
                label = "bigValue",
            ) { t ->
                Text(
                    t,
                    style = (if (compact) MaterialTheme.typography.displaySmall else MaterialTheme.typography.displayLarge)
                        .copy(fontFeatureSettings = "tnum, ss03"),
                    color = p.text,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                )
            }
            if (caption != null) Text(caption, style = MaterialTheme.typography.bodySmall, color = p.textTertiary)
        }
        RepeatStep("+", plusDescription) { dir = 1; onPlus() }
    }
}

/** Runder Knopf: Tippen = ein Schritt, Halten = nach kurzer Pause schnell weiter. */
@Composable
private fun RepeatStep(label: String, description: String, onStep: () -> Unit) {
    val p = Funke.colors
    val haptics = LocalHaptics.current
    val scope = rememberCoroutineScope()
    val step by rememberUpdatedState(onStep)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.92f else 1f, Motion.spec(Motion.Press), label = "stepScale")
    val bg by animateColorAsState(if (pressed) p.accentSoft else p.track, Motion.fade(Motion.Press), label = "stepBg")
    Box(
        Modifier
            .size(Sizes.button)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(CircleShape)
            .background(bg)
            .pointerInput(Unit) {
                detectTapGestures(onPress = { offset ->
                    val press = PressInteraction.Press(offset)
                    interaction.emit(press)
                    step()
                    haptics?.tick()
                    val repeat = scope.launch {
                        delay(450)
                        while (true) {
                            step()
                            haptics?.tick()
                            delay(90)
                        }
                    }
                    val released = tryAwaitRelease()
                    repeat.cancel()
                    interaction.emit(if (released) PressInteraction.Release(press) else PressInteraction.Cancel(press))
                })
            }
            .semantics {
                contentDescription = description
                role = Role.Button
                onClick { step(); true }
            },
        contentAlignment = Alignment.Center,
    ) { Text(label, style = MaterialTheme.typography.titleLarge, color = p.text) }
}

/** Zwei Wege nebeneinander: Pop-up oder Vollbild, jeweils mit einem Bild, wie es aussieht. */
@Composable
private fun DeliveryPicker(selected: Delivery, rights: Rights, onGuide: () -> Unit, onSelect: (Delivery) -> Unit) {
    val p = Funke.colors
    val haptics = LocalHaptics.current
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
        Delivery.entries.forEach { d ->
            val on = d == selected
            val bg by animateColorAsState(if (on) p.accentSoft else p.track, Motion.fade(Motion.Fast), label = "dlBg")
            val ring by animateColorAsState(if (on) p.accent.copy(alpha = 0.55f) else Color.Transparent, Motion.fade(Motion.Fast), label = "dlRing")
            val tint by animateColorAsState(if (on) p.accent else p.textSecondary, Motion.fade(Motion.Fast), label = "dlTint")
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .bouncyClickable(shape = Shapes.lg, pressedScale = 0.97f, role = Role.RadioButton) {
                        haptics?.tick()
                        onSelect(d)
                    }
                    .clip(Shapes.lg)
                    .background(bg)
                    .border(1.5.dp, ring, Shapes.lg)
                    .padding(Space.s4)
                    .semantics { stateDescription = if (on) tr("Ausgewählt", "Selected") else "" },
            ) {
                Icon(
                    painterResource(if (d == Delivery.Notify) R.drawable.ic_popup else R.drawable.ic_fullscreen),
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(Sizes.iconLg),
                )
                Spacer(Modifier.height(Space.s3))
                Text(d.label, style = MaterialTheme.typography.titleSmall, color = p.text)
                Text(
                    when (d) {
                        Delivery.Notify -> tr("Kurz oben am Rand", "Briefly at the top")
                        Delivery.Screen -> tr("Sofort oder beim Entsperren", "Now or when you unlock")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = p.textTertiary,
                )
            }
        }
    }
    if (selected == Delivery.Screen && !rights.service) {
        Spacer(Modifier.height(Space.s2))
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(min = Sizes.touch)
                .bouncyClickable(shape = Shapes.md, pressedScale = 0.99f, onClick = onGuide)
                .padding(horizontal = Space.s1),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(p.warning))
            Spacer(Modifier.width(Space.s2))
            Text(
                tr("Braucht die Bedienungshilfe – bis dahin als Pop-up", "Needs the accessibility service – a pop-up until then"),
                style = MaterialTheme.typography.bodySmall,
                color = p.textSecondary,
                modifier = Modifier.weight(1f),
            )
            Text(tr("Einschalten", "Turn on"), style = MaterialTheme.typography.labelLarge, color = p.accent)
        }
    }
}

@Composable
private fun TestButtons(onNow: () -> Unit, onLater: () -> Unit, enabled: Boolean = true) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
        PillButton(tr("Jetzt testen", "Test now"), onClick = onNow, enabled = enabled, modifier = Modifier.weight(1f))
        PillButton(tr("In 1 Min.", "In 1 min"), onClick = onLater, enabled = enabled, modifier = Modifier.weight(1f))
    }
}

/** Sieben runde Tage, Montag zuerst. Mindestens einer bleibt an. */
@Composable
private fun DaysPicker(mask: Int, onChange: (Int) -> Unit) {
    val p = Funke.colors
    val haptics = LocalHaptics.current
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.s2)) {
        DayOfWeek.entries.forEach { d ->
            val on = Days.has(mask, d)
            val bg by animateColorAsState(if (on) p.accent else p.track, Motion.fade(Motion.Fast), label = "day$d")
            Box(
                Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .heightIn(min = 40.dp)
                    .bouncyClickable(shape = CircleShape, pressedScale = 0.92f, role = Role.Checkbox) {
                        val next = Days.toggle(mask, d)
                        if (next and Days.ALL != 0) {
                            haptics?.tick()
                            onChange(next)
                        }
                    }
                    .clip(CircleShape)
                    .background(bg)
                    .semantics { stateDescription = if (on) tr("An", "On") else tr("Aus", "Off") },
                contentAlignment = Alignment.Center,
            ) {
                Text(Days.short(d), style = MaterialTheme.typography.labelLarge, color = if (on) p.onAccent else p.textSecondary)
            }
        }
    }
}

@Composable
private fun SelectChip(text: String, on: Boolean, onClick: () -> Unit) {
    val p = Funke.colors
    val bg by animateColorAsState(if (on) p.accent else p.track, Motion.fade(Motion.Fast), label = "chip")
    Box(
        Modifier
            .heightIn(min = 40.dp)
            .bouncyClickable(shape = Shapes.pill, pressedScale = 0.95f, role = Role.Checkbox, onClick = onClick)
            .clip(Shapes.pill)
            .background(bg)
            .padding(horizontal = Space.s4, vertical = Space.s2)
            .semantics { stateDescription = if (on) tr("Ausgewählt", "Selected") else "" },
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge, color = if (on) p.onAccent else p.textSecondary, maxLines = 1)
    }
}

/** Eingabefeld ohne Überschrift: Der Platzhalter ist der Name des Felds ("Titel"). */
@Composable
private fun QuietField(value: String, placeholder: String, maxLength: Int, singleLine: Boolean, onChange: (String) -> Unit) {
    val p = Funke.colors
    val focus = LocalFocusManager.current
    BasicTextField(
        value = value,
        onValueChange = { if (it.length <= maxLength) onChange(if (singleLine) it.replace("\n", " ") else it) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        maxLines = if (singleLine) 1 else 4,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = p.text),
        cursorBrush = SolidColor(p.accent),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = if (singleLine) ImeAction.Next else ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
        decorationBox = { inner ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .glass(Shapes.lg)
                    .padding(horizontal = Space.s4, vertical = Space.s3),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty()) Text(placeholder, style = MaterialTheme.typography.bodyLarge, color = p.textTertiary)
                inner()
            }
        },
    )
}

/** Feld mit kleiner Überschrift (z. B. Buch umbenennen). */
@Composable
internal fun LabeledField(label: String, value: String, placeholder: String, maxLength: Int, singleLine: Boolean, onChange: (String) -> Unit) {
    SectionLabel(label, Modifier.padding(start = Space.s1))
    Spacer(Modifier.height(Space.s2))
    QuietField(value, placeholder, maxLength, singleLine, onChange)
}
