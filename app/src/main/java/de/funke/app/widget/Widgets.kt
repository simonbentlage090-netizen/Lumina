package de.funke.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.SizeF
import android.view.View
import android.widget.RemoteViews
import de.funke.app.MainActivity
import de.funke.app.R
import de.funke.app.progress.Streaks
import de.funke.app.ui.DeepLink
import de.funke.app.ui.Lang
import de.funke.app.ui.daysText
import de.funke.app.ui.daysWord
import de.funke.app.ui.tr
import java.time.YearMonth
import java.time.format.TextStyle

/*
 * Die vier Widgets für den Startbildschirm.
 *
 * Android-Widgets sind keine kleinen Apps, sondern "Fernansichten" (RemoteViews): Lumina beschreibt,
 * was angezeigt werden soll (Bild, Texte, Fortschritt), und der Startbildschirm zeichnet es.
 * Aktualisiert wird:
 *  - alle 30 bzw. 60 Minuten vom System (siehe res/xml/widget_*_info.xml),
 *  - jedes Mal, wenn Lumina geöffnet wird oder eine Session endet,
 *  - abends, wenn die Erinnerung läuft.
 * Ein Tipp aufs Widget öffnet Lumina (Fortschritts-Widgets direkt im Reiter "Fortschritt").
 */

/** Gemeinsame Basis: Update und Größenänderung landen beide in [Widgets.update]. */
abstract class BaseWidget(private val kind: Widgets.Kind) : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        Widgets.update(context, manager, ids, kind)
    }

    override fun onAppWidgetOptionsChanged(context: Context, manager: AppWidgetManager, id: Int, newOptions: Bundle?) {
        Widgets.update(context, manager, intArrayOf(id), kind)
    }
}

class LumiWidget : BaseWidget(Widgets.Kind.Lumi)
class StreakWidget : BaseWidget(Widgets.Kind.Streak)
class WeekWidget : BaseWidget(Widgets.Kind.Week)
class MonthWidget : BaseWidget(Widgets.Kind.Month)

object Widgets {

    enum class Kind { Lumi, Streak, Week, Month }

    private fun providerOf(kind: Kind): Class<*> = when (kind) {
        Kind.Lumi -> LumiWidget::class.java
        Kind.Streak -> StreakWidget::class.java
        Kind.Week -> WeekWidget::class.java
        Kind.Month -> MonthWidget::class.java
    }

    /** Alle Widgets neu zeichnen, die auf dem Startbildschirm liegen. Darf nie die App stören. */
    fun refresh(context: Context?) {
        val ctx = context?.applicationContext ?: return
        runCatching {
            val manager = AppWidgetManager.getInstance(ctx) ?: return
            for (kind in Kind.entries) {
                val ids = manager.getAppWidgetIds(ComponentName(ctx, providerOf(kind)))
                if (ids.isNotEmpty()) update(ctx, manager, ids, kind)
            }
        }
    }

    fun update(context: Context, manager: AppWidgetManager, ids: IntArray, kind: Kind) {
        val prefs = context.getSharedPreferences(Streaks.PREFS, Context.MODE_PRIVATE)
        Lang.load(prefs)
        val snap = Streaks.snapshot(prefs)
        for (id in ids) {
            runCatching {
                val options = manager.getAppWidgetOptions(id)
                val views = when (kind) {
                    Kind.Lumi -> lumi(context, snap, id, options)
                    Kind.Streak -> streak(context, snap)
                    Kind.Week -> week(context, snap, options)
                    Kind.Month -> month(context, snap, options)
                }
                manager.updateAppWidget(id, views)
            }
        }
    }

    /** Öffnet Lumina, auf Wunsch gleich in einem bestimmten Reiter. */
    fun openApp(context: Context, tab: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
            .setAction(Intent.ACTION_MAIN)
            .putExtra(DeepLink.EXTRA_TAB, tab)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return PendingIntent.getActivity(
            context, 100 + tab, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    // ------------------------------------------------------------------------------------------
    // Lumi
    // ------------------------------------------------------------------------------------------

    /** Stimmung des Lumi-Widgets: färbt Lichtschein, Knopf und große Zahl passend zur Lage. */
    private enum class Mood(val bgWide: Int, val bgTall: Int, val button: Int, val buttonText: Int, val bigColor: Int) {
        Warm(R.drawable.widget_bg_warm_h, R.drawable.widget_bg_warm_v, R.drawable.widget_btn_accent, R.color.w_on_accent, R.color.w_accent_text),
        Cool(R.drawable.widget_bg_cool_h, R.drawable.widget_bg_cool_v, R.drawable.widget_btn_ice, R.color.w_on_ice, R.color.w_ice_text),
        Alert(R.drawable.widget_bg_alert_h, R.drawable.widget_bg_alert_v, R.drawable.widget_btn_alert, R.color.w_on_alert, R.color.w_alert),
        Night(R.drawable.widget_bg_night_h, R.drawable.widget_bg_night_v, R.drawable.widget_btn_accent, R.color.w_on_accent, R.color.w_accent_text),
    }

    private fun moodOf(line: LumiLine): Mood = when {
        line.image == R.drawable.lumi_w_frozen && !line.urgent -> Mood.Cool
        line.urgent -> Mood.Alert
        line.image in setOf(R.drawable.lumi_w_ice, R.drawable.lumi_w_thaw, R.drawable.lumi_w_icebreak) -> Mood.Cool
        line.image in setOf(R.drawable.lumi_w_scared, R.drawable.lumi_w_cry) -> Mood.Alert
        line.image in setOf(R.drawable.lumi_w_sleep, R.drawable.lumi_w_miss, R.drawable.lumi_w_sad) -> Mood.Night
        else -> Mood.Warm
    }

    /** Fassungen des Lumi-Widgets: welches Layout und ob es hoch (Lumi oben) oder breit (Lumi links) ist. */
    private enum class LumiSize(val layout: Int, val tall: Boolean) {
        /** klein (2x2): Lumi und eine Zeile, das ganze Widget ist der Knopf. */
        Small(R.layout.widget_lumi_small, true),
        /** breit, aber flach (4x1): Überschrift und Knopf. */
        WideShort(R.layout.widget_lumi, false),
        /** breit (3x2, 4x2 …): Lumi links groß, rechts Zahl oder Überschrift, Fortschritt und Knopf. */
        Wide(R.layout.widget_lumi, false),
        /** schmal und hoch (2x3). */
        TallNarrow(R.layout.widget_lumi_tall, true),
        /** groß und eher quadratisch oder hoch (4x3, 4x4). */
        Tall(R.layout.widget_lumi_tall, true),
    }

    /**
     * Welche Fassung zu einer Größe (dp) passt: nach Seitenverhältnis. Breit (ab 1,25 : 1) = Lumi links,
     * sonst Lumi oben. So bekommt ein breites 4x2-Widget nie die hohe Fassung mit winzigem Lumi.
     */
    private fun lumiSizeFor(w: Float, h: Float): LumiSize = when {
        h < 140f -> if (w >= 200f) LumiSize.WideShort else LumiSize.Small
        w < 160f -> if (h >= 200f) LumiSize.TallNarrow else LumiSize.Small
        w >= 1.25f * h -> if (w >= 200f) LumiSize.Wide else LumiSize.Small
        w >= 220f && h >= 240f -> LumiSize.Tall
        h >= 200f -> LumiSize.TallNarrow
        else -> LumiSize.Small
    }

    /**
     * Ideale Größen (dp) für Android 12+. Der Startbildschirm nimmt von allen Einträgen, die ins Widget passen,
     * den mit dem kleinsten Abstand zur echten Größe. Diese 16 Einträge (das Maximum) sind per Simulation über alle
     * Größen von 110 bis 450 dp so gewählt, dass die Wahl fast überall [lumiSizeFor] entspricht.
     */
    private val LumiIdeal: List<Triple<Float, Float, LumiSize>> = listOf(
        Triple(110f, 110f, LumiSize.Small),
        Triple(190f, 160f, LumiSize.Small),
        Triple(200f, 110f, LumiSize.WideShort),
        Triple(220f, 110f, LumiSize.WideShort),
        Triple(250f, 110f, LumiSize.WideShort),
        Triple(200f, 140f, LumiSize.Wide),
        Triple(200f, 150f, LumiSize.Wide),
        Triple(220f, 140f, LumiSize.Wide),
        Triple(250f, 140f, LumiSize.Wide),
        Triple(270f, 170f, LumiSize.Wide),
        Triple(300f, 150f, LumiSize.Wide),
        Triple(110f, 200f, LumiSize.TallNarrow),
        Triple(180f, 200f, LumiSize.TallNarrow),
        Triple(190f, 210f, LumiSize.TallNarrow),
        Triple(220f, 230f, LumiSize.TallNarrow),
        Triple(220f, 240f, LumiSize.Tall),
    )

    /** "12 Tage": Zahl groß, Einheit klein. */
    private fun bigText(big: String, unit: String?): CharSequence {
        if (unit.isNullOrEmpty()) return big
        val sp = android.text.SpannableString("$big\u2009$unit")
        sp.setSpan(android.text.style.RelativeSizeSpan(0.42f), big.length, sp.length, android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sp
    }

    private fun lumi(context: Context, s: Streaks.Snapshot, id: Int, options: Bundle?): RemoteViews {
        val now = java.time.LocalDateTime.now()
        val line = LumiSays.now(s, now, salt = id)
        val mood = moodOf(line)
        val cta = line.cta?.let { "$it \u2192" }
        val click = openApp(context, line.tab)
        // Countdown bis Mitternacht (läuft im Widget selbst, ohne Updates)
        val toMidnightMs = java.time.Duration.between(now, now.toLocalDate().plusDays(1).atStartOfDay()).toMillis()
        val hasBig = line.big != null && !line.liveCountdown
        // Manche Überschriften setzen die Zahl fort ("4 Std." + "bis dein Streak erlischt")
        val continues = line.title.firstOrNull()?.isLowerCase() == true

        /**
         * Baut eine Fassung für mindestens [wDp] × [hDp] dp. Was hineinkommt, richtet sich nach der Höhe, damit Lumi
         * groß bleibt: Die große Zahl und der Balken zum Tagesziel erscheinen nur, wenn genug Platz ist.
         */
        fun build(size: LumiSize, wDp: Float, hDp: Float): RemoteViews =
            RemoteViews(context.packageName, size.layout).apply {
                val root = android.R.id.background
                setInt(root, "setBackgroundResource", if (size.tall) mood.bgTall else mood.bgWide)
                setImageViewResource(R.id.w_lumi, line.image)
                setContentDescription(R.id.w_lumi, "Lumi")
                setContentDescription(root, "Lumi: ${line.title}. ${line.text}")

                // Platzbudget aus der echten Größe: zuerst der Knopf (groß genug zum Tippen), dann zwei Zeilen
                // Überschrift; die große Zahl und der Balken kommen nur dazu, wenn danach noch Platz bleibt.
                // Im hohen Widget behält Lumi mindestens die Hälfte der Höhe.
                val inner = hDp - 32f
                val buttonDp = when (size) {
                    LumiSize.Small -> 0f
                    LumiSize.WideShort -> (inner * 0.45f).coerceIn(40f, 48f)
                    LumiSize.Wide -> (inner * 0.30f).coerceIn(44f, 56f)
                    LumiSize.TallNarrow, LumiSize.Tall -> (inner * 0.16f).coerceIn(44f, 56f)
                }
                val textRoom = when (size) {
                    LumiSize.Small -> 0f
                    LumiSize.WideShort -> 0f
                    LumiSize.Wide -> inner - buttonDp - 8f - 56f
                    LumiSize.TallNarrow, LumiSize.Tall -> inner * 0.5f - buttonDp - 8f - 44f
                }
                val showBig = hasBig && when (size) {
                    LumiSize.Small -> true
                    LumiSize.WideShort -> false
                    else -> textRoom >= 44f
                }
                val showChrono = line.liveCountdown && when (size) {
                    LumiSize.Small -> true
                    LumiSize.WideShort -> false
                    else -> textRoom >= 44f
                }
                if (Build.VERSION.SDK_INT >= 31 && buttonDp > 0f) {
                    setViewLayoutHeight(R.id.w_cta, buttonDp, android.util.TypedValue.COMPLEX_UNIT_DIP)
                }
                setViewVisibility(R.id.w_big, if (showBig) View.VISIBLE else View.GONE)
                if (showBig) {
                    setTextViewText(R.id.w_big, bigText(line.big!!, line.bigUnit))
                    setTextColor(R.id.w_big, context.getColor(mood.bigColor))
                }
                setViewVisibility(R.id.w_chrono, if (showChrono) View.VISIBLE else View.GONE)
                if (showChrono) {
                    setChronometerCountDown(R.id.w_chrono, true)
                    setChronometer(R.id.w_chrono, android.os.SystemClock.elapsedRealtime() + toMidnightMs, null, true)
                }

                // Ohne große Zahl darüber braucht eine Fortsetzung ("bis dein Streak erlischt") die Zahl im Satz
                val title = if (hasBig && !showBig && continues) "${line.big} ${line.bigUnit.orEmpty()} ${line.title}".replace("  ", " ") else line.title
                setTextViewText(R.id.w_title, title)

                if (size == LumiSize.Small) {
                    // klein: entweder Zahl/Countdown oder Überschrift
                    setViewVisibility(R.id.w_title, if (showBig || showChrono) View.GONE else View.VISIBLE)
                } else {
                    // Balken zum Tagesziel ("12/30 Min"); im breiten Widget nur ohne große Zahl darüber
                    val goal = line.goal
                    // Nur ohne große Zahl und nur mit genug Höhe, damit Lumi nicht zu klein wird
                    val roomForGoal = !(showBig || showChrono) && size != LumiSize.WideShort && textRoom >= 24f
                    val showGoal = goal != null && roomForGoal
                    setViewVisibility(R.id.w_goal, if (showGoal) View.VISIBLE else View.GONE)
                    if (showGoal && goal != null) {
                        val (m, g) = goal
                        setProgressBar(R.id.w_goal_bar, 100, (m * 100 / g.coerceAtLeast(1)).coerceIn(4, 100), false)
                        setTextViewText(R.id.w_goal_text, tr("$m/$g Min", "$m/$g min"))
                        setContentDescription(R.id.w_goal, tr("$m von $g Minuten Tagesziel", "$m of $g minutes daily goal"))
                    }
                    // Mit Zahl darüber rückt die Überschrift direkt darunter
                    if (!size.tall) {
                        setInt(
                            R.id.w_title, "setGravity",
                            if (showBig || showChrono) android.view.Gravity.TOP or android.view.Gravity.START
                            else android.view.Gravity.CENTER_VERTICAL or android.view.Gravity.START,
                        )
                    }
                    if (cta == null) {
                        setViewVisibility(R.id.w_cta, View.GONE)
                    } else {
                        setViewVisibility(R.id.w_cta, View.VISIBLE)
                        setTextViewText(R.id.w_cta, cta)
                        setInt(R.id.w_cta, "setBackgroundResource", mood.button)
                        setTextColor(R.id.w_cta, context.getColor(mood.buttonText))
                        setOnClickPendingIntent(R.id.w_cta, click)
                    }
                }
                setOnClickPendingIntent(root, click)
            }

        // Wenn der Streak heute in Gefahr ist: zur nächsten vollen Stunde neu zeichnen (Countdown 4, 3, 2, 1 …)
        if (s.atRisk && now.hour >= 15) scheduleTick(context, now)

        return if (Build.VERSION.SDK_INT >= 31) {
            // Android 12+: Der Startbildschirm meldet die echten Größen (Hoch- und Querformat). Für genau diese wird
            // gebaut, damit Knopf, Schrift und Lumi zur tatsächlichen Fläche passen. Ohne Angabe: [LumiIdeal].
            @Suppress("DEPRECATION")
            val actual = options?.getParcelableArrayList<SizeF>(AppWidgetManager.OPTION_APPWIDGET_SIZES)
                ?.filter { it.width > 0f && it.height > 0f }
                ?.distinctBy { Math.round(it.width) to Math.round(it.height) }
                ?.take(16)
                .orEmpty()
            if (actual.isNotEmpty()) {
                RemoteViews(actual.associate { SizeF(Math.round(it.width).toFloat(), Math.round(it.height).toFloat()) to build(lumiSizeFor(it.width, it.height), it.width, it.height) })
            } else {
                RemoteViews(LumiIdeal.associate { (w, h, size) -> SizeF(w, h) to build(size, w, h) })
            }
        } else {
            // Davor: im Hochformat ist die Breite minWidth und die Höhe maxHeight
            val w = (options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0)?.takeIf { it > 0 } ?: 250).toFloat()
            val h = (options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 0)?.takeIf { it > 0 } ?: 110).toFloat()
            build(lumiSizeFor(w, h), w, h)
        }
    }

    /**
     * Lumi-Widget zur nächsten vollen Stunde neu zeichnen lassen. Nicht sekundengenau (kein Wecker-Recht nötig),
     * höchstens ein, zwei Minuten später. Die letzte Stunde zählt das Widget ohnehin selbst herunter.
     */
    private fun scheduleTick(context: Context, now: java.time.LocalDateTime) {
        runCatching {
            val am = context.getSystemService(android.app.AlarmManager::class.java) ?: return
            val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, LumiWidget::class.java))
            val intent = Intent(context, LumiWidget::class.java)
                .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            val pi = PendingIntent.getBroadcast(
                context, 4711, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
            val next = now.withMinute(0).withSecond(5).withNano(0).plusHours(1)
            val at = next.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            am.setWindow(android.app.AlarmManager.RTC, at, 90_000L, pi)
        }
    }

    // ------------------------------------------------------------------------------------------
    // Streak
    // ------------------------------------------------------------------------------------------

    private fun streak(context: Context, s: Streaks.Snapshot): RemoteViews =
        RemoteViews(context.packageName, R.layout.widget_streak).apply {
            val n = s.streak
            setImageViewResource(R.id.w_flame, if (s.doneToday) R.drawable.ic_w_flame else R.drawable.ic_w_flame_off)
            setTextViewText(R.id.w_number, n.toString())
            val g = s.streakGoal
            setTextViewText(
                R.id.w_label,
                if (g > n) tr("von $g Tagen", "of $g days") else tr("${daysWord(n)} Streak", "day streak"),
            )
            setViewVisibility(R.id.w_status_done, if (s.doneToday) View.VISIBLE else View.GONE)
            setViewVisibility(R.id.w_status_open, if (s.doneToday) View.GONE else View.VISIBLE)
            setTextViewText(R.id.w_status_done, tr("Heute geschafft", "Today: done"))
            setTextViewText(
                R.id.w_status_open,
                when {
                    s.atRisk -> tr("Heute noch Fokus", "Focus today")
                    n == 0 -> tr("Heute neu starten", "Start again today")
                    else -> tr("Heute noch offen", "Today: still open")
                },
            )
            // Das Streak-Eis steht hier bewusst nicht: Wer sieht "noch 2 Eis", lässt eher einen Tag aus.
            setContentDescription(android.R.id.background, tr("Streak: ${daysText(n)}", "Streak: ${daysText(n)}"))
            setOnClickPendingIntent(android.R.id.background, openApp(context, 0))
        }

    // ------------------------------------------------------------------------------------------
    // Letzte 7 Tage
    // ------------------------------------------------------------------------------------------

    private fun week(context: Context, s: Streaks.Snapshot, options: Bundle?): RemoteViews {
        val days = (6 downTo 0).map { s.today.minusDays(it.toLong()) }
        val total = days.sumOf { s.minutesOn(it) }
        val avg = (total + 3) / 7
        val hit = days.count { s.minutesOn(it) >= s.goal }
        val (w, h, dens) = chartSize(context, options, defW = 250, defH = 110, headerDp = 76)
        val data = WidgetCharts.WeekData(
            days = days,
            minutes = days.map { s.minutesOn(it) },
            done = days.map { it.toString() in s.done },
            frozen = days.map { it.toString() in s.frozen },
            goal = s.goal,
            locale = Lang.locale,
        )
        return RemoteViews(context.packageName, R.layout.widget_week).apply {
            setTextViewText(R.id.w_title, tr("Letzte 7 Tage", "Last 7 days"))
            setTextViewText(R.id.w_total, formatMinutes(total))
            setTextViewText(R.id.w_sub, tr("Ø $avg Min, Ziel an $hit/7 Tagen", "Avg $avg min, goal hit $hit/7 days"))
            val type = WidgetCharts.Type.of(context)
            setImageViewBitmap(R.id.w_chart_light, WidgetCharts.week(w, h, dens, WidgetCharts.LIGHT, data, type))
            setImageViewBitmap(R.id.w_chart_dark, WidgetCharts.week(w, h, dens, WidgetCharts.DARK, data, type))
            setOnClickPendingIntent(android.R.id.background, openApp(context, 4))
        }
    }

    // ------------------------------------------------------------------------------------------
    // Monat
    // ------------------------------------------------------------------------------------------

    private fun month(context: Context, s: Streaks.Snapshot, options: Bundle?): RemoteViews {
        val month = YearMonth.from(s.today)
        val days = (1..month.lengthOfMonth()).map { month.atDay(it) }
        val readDays = days.count { s.minutesOn(it) > 0 || it.toString() in s.done }
        val total = days.sumOf { s.minutesOn(it) }
        val hit = days.count { s.minutesOn(it) >= s.goal }
        val (w, h, dens) = chartSize(context, options, defW = 250, defH = 180, headerDp = 76)
        val data = WidgetCharts.MonthData(
            month = month,
            today = s.today,
            minutes = days.map { s.minutesOn(it) },
            done = days.map { it.toString() in s.done },
            frozen = days.map { it.toString() in s.frozen },
            goal = s.goal,
            locale = Lang.locale,
        )
        val name = month.month.getDisplayName(TextStyle.FULL_STANDALONE, Lang.locale)
            .replaceFirstChar { it.titlecase(Lang.locale) }
        return RemoteViews(context.packageName, R.layout.widget_month).apply {
            setTextViewText(R.id.w_title, name)
            setTextViewText(R.id.w_total, tr("$readDays ${if (readDays == 1) "Lesetag" else "Lesetage"}", "$readDays reading ${if (readDays == 1) "day" else "days"}"))
            setTextViewText(R.id.w_sub, tr("${formatMinutes(total)}, Ziel an ${daysText(hit)}", "${formatMinutes(total)}, goal hit on ${daysText(hit)}"))
            val type = WidgetCharts.Type.of(context)
            setImageViewBitmap(R.id.w_chart_light, WidgetCharts.month(w, h, dens, WidgetCharts.LIGHT, data, type))
            setImageViewBitmap(R.id.w_chart_dark, WidgetCharts.month(w, h, dens, WidgetCharts.DARK, data, type))
            setOnClickPendingIntent(android.R.id.background, openApp(context, 4))
        }
    }

    // ------------------------------------------------------------------------------------------

    private fun density(context: Context) = context.resources.displayMetrics.density

    /** Größe der Diagramm-Fläche in Pixeln (Breite im Hochformat = minWidth, Höhe = maxHeight) und die Dichte zum Zeichnen. */
    private fun chartSize(context: Context, options: Bundle?, defW: Int, defH: Int, headerDp: Int): Triple<Int, Int, Float> {
        val d = density(context)
        val wDp = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0)?.takeIf { it > 0 } ?: defW
        val hDp = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 0)?.takeIf { it > 0 } ?: defH
        var w = ((wDp - 32) * d).toInt().coerceAtLeast((120 * d).toInt())
        var h = ((hDp - headerDp) * d).toInt().coerceAtLeast((44 * d).toInt())
        // Speicher schonen: Widgets dürfen nicht beliebig große Bilder schicken.
        val scale = minOf(1f, 900f / w, 700f / h)
        w = (w * scale).toInt(); h = (h * scale).toInt()
        return Triple(w, h, d * scale)
    }

    fun formatMinutes(total: Int): String =
        if (total < 60) tr("$total Min", "$total min")
        else "${total / 60} h ${"%02d".format(total % 60)} min"
}
