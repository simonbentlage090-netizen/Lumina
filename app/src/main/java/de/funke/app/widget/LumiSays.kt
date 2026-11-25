package de.funke.app.widget

import de.funke.app.R
import de.funke.app.progress.Streaks
import de.funke.app.ui.daysText
import de.funke.app.ui.daysWord
import de.funke.app.ui.tr
import java.time.LocalDateTime

/**
 * Was Lumi gerade sagt – für das Lumi-Widget.
 *
 * Seit 9.3 ist das Widget bewusst textarm: eine kurze Überschrift (höchstens ein paar Wörter),
 * manchmal eine große Zahl (Streak-Tage oder der Countdown bis Mitternacht) und ein Knopf.
 * Jeder Satz bringt seinen eigenen Knopf mit, damit Knopf und Satz immer zusammenpassen
 * (kein "Streak retten", wenn der Streak schon gerettet ist).
 *
 * [LumiLine.text] ist der längere Satz dazu – fürs Vorlesen (Bedienungshilfe) und falls man ihn
 * später in einer Benachrichtigung braucht. Im Widget steht er nicht mehr.
 *
 * Damit es nicht langweilig wird, gibt es pro Lage mehrere Sätze. Welcher kommt, wechselt alle
 * 30 Minuten, bleibt aber innerhalb dieser 30 Minuten gleich.
 */
class LumiLine(
    val image: Int,
    /** Kurze Überschrift, steht groß im Widget. */
    val title: String,
    /** Längerer Satz (nicht im Widget). */
    val text: String,
    /** Beschriftung des Knopfs, null = kein Knopf. */
    val cta: String?,
    /** Reiter, der beim Antippen aufgeht (0 = Lesen, 3 = Aufgaben, 4 = Fortschritt). */
    val tab: Int = 0,
    /** Große Zahl über der Überschrift, z. B. "12" (Streak) oder "3" (Stunden bis Mitternacht). */
    val big: String? = null,
    /** Kleine Einheit hinter der großen Zahl ("Tage", "Std."). */
    val bigUnit: String? = null,
    /** Letzte Stunde vor Mitternacht: statt der Zahl läuft ein Countdown (mm:ss). */
    val liveCountdown: Boolean = false,
    /** Countdown-Stimmung (Streak reißt bald). */
    val urgent: Boolean = false,
    /** Fortschritt zum Tagesziel (gelesene Minuten, Ziel) – als Balken unter der Überschrift. */
    val goal: Pair<Int, Int>? = null,
)

object LumiSays {

    private class V(
        val image: Int,
        val title: String,
        val text: String,
        val cta: String?,
        val tab: Int = 0,
        val big: String? = null,
        val bigUnit: String? = null,
        val live: Boolean = false,
        val urgent: Boolean = false,
        val goal: Pair<Int, Int>? = null,
    )

    /** Minuten bis Mitternacht. */
    fun minutesToMidnight(at: LocalDateTime): Int = 24 * 60 - (at.hour * 60 + at.minute)

    /** Aktuelle Lage → Bild, Überschrift und Knopf. [salt] mischt z. B. die Widget-ID ein, damit zwei Widgets nicht gleich sind. */
    fun now(s: Streaks.Snapshot, at: LocalDateTime = LocalDateTime.now(), salt: Int = 0): LumiLine {
        val h = at.hour
        val n = s.streak
        val m = s.todayMinutes
        val g = s.goal
        val left = (g - m).coerceAtLeast(1)
        val f = s.freezes
        val d = s.daysSinceRead
        val nd = daysText(n)
        val ndD = de.funke.app.ui.daysTextDative(n)
        val minLeft = minutesToMidnight(at)
        val hoursLeft = (minLeft + 59) / 60

        val read = tr("Loslegen", "Start")
        val more = tr("Noch ein Kapitel", "One more chapter")
        val keep = tr("Weitermachen", "Keep going")
        val save = tr("Streak retten", "Save the streak")
        val task = tr("Zur Aufgabe", "Try a puzzle")

        val options: List<V> = when {
            // Noch nie gelesen
            d == -1 && s.best == 0 -> listOf(
                V(R.drawable.lumi_w_hello, tr("Hi, ich bin Lumi!", "Hi, I'm Lumi!"),
                    tr("Heute die ersten Minuten Fokus? Ich leuchte mit.", "Your first minutes of focus today? I'll glow along."),
                    if (h < 6) null else tr("Los geht's", "Let's go")),
                V(R.drawable.lumi_w_read, tr("Lass uns anfangen", "Let's get started"),
                    tr("Schnapp dir ein Buch. $g Minuten und dein erster Tag ist geschafft.", "Grab a book. $g minutes and your first day is done."),
                    if (h < 6) null else tr("Los geht's", "Let's go")),
            )

            // Mitten in der Nacht
            h < 6 -> listOf(
                V(R.drawable.lumi_w_sleep, tr("Psst …", "Shh …"), tr("Lumi schläft noch. Bis später!", "Lumi is still asleep. See you later!"), null),
                V(R.drawable.lumi_w_sleep, tr("Zzz …", "Zzz …"), tr("Ich träume von Büchern. Du solltest auch schlafen.", "I'm dreaming of books. You should sleep too."), null),
            )

            // Gestern lag der Streak auf Eis, heute gelesen: Lumi ist frei
            s.doneToday && s.frozeYesterday && h < 21 -> listOf(
                V(R.drawable.lumi_w_icebreak, tr("Aufgetaut!", "Thawed out!"),
                    tr("Gestern lag dein Streak auf Eis – du hast mich befreit!", "Your streak was on ice yesterday – you set me free!"),
                    if (s.goalMetToday) more else keep, goal = if (s.goalMetToday) null else m to g),
                V(R.drawable.lumi_w_icebreak, tr("Danke fürs Retten!", "Thanks for the rescue!"),
                    tr("$nd und ich leuchte wieder.", "$nd and I'm glowing again."),
                    if (s.goalMetToday) more else keep, big = "$n", bigUnit = daysWord(n)),
            )

            // Heute alles geschafft
            s.doneToday && s.goalMetToday -> when {
                s.streakGoal > 0 && n == s.streakGoal -> listOf(
                    V(R.drawable.lumi_w_wow, tr("Streak-Ziel geschafft!", "Streak goal reached!"),
                        tr("$nd am Stück – genau wie du es dir vorgenommen hast!", "$nd in a row – exactly what you set out to do!"),
                        tr("Neues Ziel", "New goal"), tab = 4, big = "$n", bigUnit = daysWord(n)),
                )
                n >= 3 && n >= s.best -> listOf(
                    V(R.drawable.lumi_w_wow, tr("Neuer Rekord!", "New record!"),
                        tr("$nd am Stück – so lange wie noch nie!", "$nd in a row – longer than ever!"),
                        if (h >= 21) null else more, big = "$n", bigUnit = daysWord(n)),
                    V(R.drawable.lumi_w_proud, tr("Rekord-Lumi!", "Record Lumi!"),
                        tr("$nd. Ich platze gleich vor Stolz.", "$nd. I'm bursting with pride."),
                        if (h >= 21) null else more, big = "$n", bigUnit = daysWord(n)),
                )
                h >= 21 -> listOf(
                    V(R.drawable.lumi_w_sleep, tr("Feierabend!", "All done!"),
                        tr("Alles geschafft. Ich mach jetzt das Licht aus.", "Everything done. I'm turning off the light now."), null),
                    V(R.drawable.lumi_w_sleep, tr("Gute Nacht!", "Good night!"),
                        tr("$m Minuten heute. Morgen geht es weiter.", "$m minutes today. More tomorrow."), null),
                )
                n >= 7 -> listOf(
                    V(R.drawable.lumi_w_zap, tr("Du hast einen Lauf!", "You're on a roll!"),
                        tr("$nd am Stück. Du stehst voll unter Strom!", "$nd in a row. You're fully charged!"),
                        more, big = "$n", bigUnit = daysWord(n)),
                    V(R.drawable.lumi_w_zap, tr("Volle Spannung!", "Full power!"),
                        tr("$nd hintereinander – so hell hab ich noch nie geleuchtet.", "$nd in a row – I've never shone this bright."),
                        more, big = "$n", bigUnit = daysWord(n)),
                    V(R.drawable.lumi_w_fire, tr("Es brennt!", "On fire!"),
                        tr("Dein Streak glüht: $nd.", "Your streak is glowing: $nd."),
                        more, big = "$n", bigUnit = daysWord(n)),
                    V(R.drawable.lumi_w_cheer, tr("Geschafft!", "Done!"),
                        tr("Tag $n ist im Kasten – $m Minuten heute.", "Day $n is in the bag – $m minutes today."), more),
                )
                else -> buildList {
                    add(V(R.drawable.lumi_w_cheer, tr("Geschafft!", "Done!"),
                        tr("Tagesziel erreicht – $m Minuten heute.", "Daily goal reached – $m minutes today."), more))
                    if (n >= 2) add(V(R.drawable.lumi_w_proud, tr("Stolz auf dich!", "Proud of you!"),
                        tr("$nd Streak und heute schon $m Minuten.", "A $n-day streak and $m minutes today."),
                        more, big = "$n", bigUnit = daysWord(n)))
                    add(V(R.drawable.lumi_w_giggle, tr("Lust auf mehr?", "Up for more?"),
                        tr("Probier doch mal eine Expert-Aufgabe.", "Why not try an expert puzzle?"), task, tab = 3))
                    if (s.streakGoal > n) {
                        val togo = s.streakGoal - n
                        add(V(R.drawable.lumi_w_proud, tr("bis zum Streak-Ziel", "to your streak goal"),
                            tr("Noch ${daysText(togo)} bis zu deinem Streak-Ziel.", "${daysText(togo)} to go until your streak goal."),
                            more, big = "$togo", bigUnit = daysWord(togo)))
                    }
                }
            }

            // Streak gesichert, Tagesziel noch nicht: Balken zeigt, wie weit es noch zum Ziel ist
            s.doneToday -> listOf(
                V(R.drawable.lumi_w_wink, tr("Streak gesichert!", "Streak saved!"),
                    tr("Noch $left Minuten bis zu deinem Tagesziel.", "$left more minutes to your daily goal."), keep, goal = m to g),
                V(R.drawable.lumi_w_go, tr("Weiter so!", "Keep going!"),
                    tr("$m von $g Minuten – du bist auf dem Weg.", "$m of $g minutes – you're on your way."), keep, goal = m to g),
            )

            // Minuten da, aber keine ganze Session (Streak zählt nur für ganze Sessions)
            m > 0 && n > 0 -> listOf(
                V(R.drawable.lumi_w_plead, tr("Fast!", "Almost!"),
                    tr("Für den Streak ($nd) fehlt noch eine ganze Fokuszeit.", "Your streak ($nd) still needs one full focus session."),
                    tr("Fokuszeit starten", "Start focusing")),
                V(R.drawable.lumi_w_go, tr("Noch eine Fokuszeit", "One more session"),
                    tr("Eine Fokuszeit bis zum Ende, dann wächst dein Streak.", "One session to the end and your streak grows."),
                    tr("Fokuszeit starten", "Start focusing")),
            )
            m > 0 -> listOf(
                V(R.drawable.lumi_w_wink, tr("Fast geschafft!", "Almost there!"),
                    tr("Noch $left Minuten bis zu deinem Tagesziel.", "$left more minutes to your daily goal."), keep, goal = m to g),
                V(R.drawable.lumi_w_go, tr("Gib nicht auf!", "Don't give up!"),
                    tr("$m von $g Minuten. Den Rest schaffst du locker.", "$m of $g minutes. You've got the rest."), keep, goal = m to g),
            )

            // Countdown: kein Eis mehr, um Mitternacht reißt der Streak
            s.atRisk && f == 0 && h >= 20 -> {
                val img = when {
                    s.frozeYesterday -> R.drawable.lumi_w_frozen
                    minLeft <= 60 -> R.drawable.lumi_w_cry
                    else -> R.drawable.lumi_w_scared
                }
                if (minLeft <= 60) listOf(
                    V(img, tr("Nein, nicht!", "No, don't!"),
                        tr("Nur noch $minLeft Minuten, dann ist dein Streak von $ndD weg!", "Only $minLeft minutes left, then your $n-day streak is gone!"),
                        save, live = true, urgent = true),
                ) else listOf(
                    V(img, tr("bis dein Streak erlischt", "until your streak ends"),
                        tr("Noch $hoursLeft Stunden, dann ist dein Streak von $ndD weg.", "$hoursLeft hours left, then your $n-day streak is gone."),
                        save, big = "$hoursLeft", bigUnit = tr("Std.", if (hoursLeft == 1) "hr" else "hrs"), urgent = true),
                )
            }

            // Spät, aber es gibt noch Eis
            s.atRisk && f > 0 && h >= 21 -> listOf(
                V(R.drawable.lumi_w_ice, tr("Gleich frier ich ein!", "I'm about to freeze!"),
                    tr("Liest du heute nicht, springt dein Eis ein. Schaffst du es selbst?", "If you don't read today, your freeze kicks in. Can you make it yourself?"), save),
            )

            // Gestern lag der Streak auf Eis – Lumi steckt fest, heute muss gelesen werden
            s.atRisk && s.frozeYesterday -> if (s.giftYesterday) listOf(
                V(R.drawable.lumi_w_frozen, tr("Geschenktes Eis!", "A gift of ice!"),
                    tr("Ich hab deinen Streak eingefroren, damit er nicht reißt. Taust du mich heute auf?", "I froze your streak so it wouldn't break. Will you thaw me out today?"),
                    tr("Lumi auftauen", "Thaw Lumi")),
            ) else listOf(
                V(R.drawable.lumi_w_frozen, tr("Hilf mir!", "Help me!"),
                    tr("Dein Streak liegt auf Eis und mein Licht geht gleich aus. Heute ein paar Minuten?", "Your streak is on ice and my light is fading. A few minutes today?"),
                    tr("Lumi retten", "Save Lumi")),
                V(R.drawable.lumi_w_frozen, tr("Hol mich hier raus!", "Get me out of here!"),
                    tr("Eine Fokuszeit heute und ich bin wieder frei.", "One focus session today and I'm free again."),
                    tr("Lumi auftauen", "Thaw Lumi")),
            )

            s.atRisk && h >= 17 -> listOf(
                V(R.drawable.lumi_w_scared, tr("Streak in Gefahr!", "Streak in danger!"),
                    tr("Dein Streak von $ndD steht auf dem Spiel. Eine Fokuszeit reicht.", "Your streak of $nd is at stake. One focus session is enough."), save),
                V(R.drawable.lumi_w_scared, tr("Oh nein, oh nein!", "Oh no, oh no!"),
                    tr("Heute fehlt noch deine Fokuszeit für den Streak.", "You still need today's focus session for your streak."), save),
                V(R.drawable.lumi_w_plead, tr("Schnell, ein Kapitel!", "Quick, a chapter!"),
                    tr("Sonst ist dein Streak von $ndD heute Nacht weg.", "Or your streak of $nd is gone tonight."), read),
            )
            s.atRisk -> listOf(
                V(R.drawable.lumi_w_read, tr("Heute weitermachen?", "Keep going today?"),
                    tr("Halt deinen Streak von $ndD am Leben.", "Keep your streak of $nd alive."), read),
                V(R.drawable.lumi_w_plead, tr("Tag ${n + 1} wartet", "Day ${n + 1} is waiting"),
                    tr("Eine Fokuszeit heute und dein Streak wächst weiter.", "One focus session today and your streak keeps growing."), read),
                V(R.drawable.lumi_w_fire, tr("Weiter brennen!", "Keep burning!"),
                    tr("$nd am Stück. Mach heute den nächsten Tag voll.", "$nd in a row. Make today the next one."),
                    read, big = "$n", bigUnit = daysWord(n)),
            )

            // Lange nicht gelesen
            d >= 3 -> listOf(
                V(R.drawable.lumi_w_miss, tr("Ich vermisse dich …", "I miss you …"),
                    tr("Seit $d Tagen keine Fokuszeit. Nur 10 Minuten?", "No focus for $d days. Just 10 minutes?"), tr("10 Min Fokus", "10 min focus")),
                V(R.drawable.lumi_w_miss, tr("Bist du noch da?", "Are you still there?"),
                    tr("Mein Licht wird ohne dich immer schwächer.", "My light keeps fading without you."), tr("10 Min Fokus", "10 min focus")),
                V(R.drawable.lumi_w_sad, tr("Komm zurück", "Come back"),
                    tr("Dein Platz ist noch frei. Ich hab ihn dir warmgehalten.", "Your spot is still free. I kept it warm for you."), tr("Weitermachen", "Keep going")),
            )
            // Streak gerade gerissen, aber vorher gut gelaufen
            n == 0 && s.best >= 3 -> listOf(
                V(R.drawable.lumi_w_go, tr("Gib nicht auf!", "Don't give up!"),
                    tr("Dein Rekord sind ${daysText(s.best)}. Heute beginnt der nächste Lauf.", "Your record is ${daysText(s.best)}. Your next run starts today."),
                    tr("Neu starten", "Start again")),
                V(R.drawable.lumi_w_go, tr("Neuer Anlauf", "Fresh start"),
                    tr("Jeder Streak beginnt mit Tag 1. Wie wär's mit heute?", "Every streak starts with day 1. How about today?"),
                    tr("Neu starten", "Start again")),
            )
            h >= 20 -> listOf(
                V(R.drawable.lumi_w_sad, tr("Schon $h Uhr …", "Already ${h % 12}${if (h >= 12) " pm" else " am"} …"),
                    tr("Ich warte noch auf dich. Ein paar Seiten?", "I'm still waiting for you. A few pages?"), read),
                V(R.drawable.lumi_w_sad, tr("Kein Fokus heute?", "No focus today?"),
                    tr("Lumi sitzt ganz allein mit dem Buch hier.", "Lumi is sitting here all alone with the book."), read),
                V(R.drawable.lumi_w_plead, tr("Nur ein bisschen?", "Just a little?"),
                    tr("Heute hatten wir noch keine Fokuszeit zusammen.", "We haven't had any focus time together today."), read),
            )
            h < 12 -> listOf(
                V(R.drawable.lumi_w_hello, tr("Guten Morgen!", "Good morning!"),
                    tr("Heute schon $g Minuten eingeplant?", "Got $g minutes planned for today?"), read),
                V(R.drawable.lumi_w_hello, tr("Neues Kapitel?", "New chapter?"),
                    tr("Neuer Tag, neues Kapitel. Ich bin bereit, wenn du es bist.", "New day, new chapter. I'm ready when you are."), read),
            )
            else -> listOf(
                V(R.drawable.lumi_w_read, tr("Zeit für ein Kapitel?", "Time for a chapter?"),
                    tr("$g Minuten und dein Tagesziel ist geschafft.", "$g minutes and your daily goal is done."), read),
                V(R.drawable.lumi_w_plead, tr("Liest du mit mir?", "Read with me?"),
                    tr("Bitte, bitte – nur eine kurze Fokuszeit.", "Pretty please – just a short focus session."), read),
                V(R.drawable.lumi_w_giggle, tr("Lust zu knobeln?", "Fancy a puzzle?"),
                    tr("Ich hab eine knifflige Aufgabe für dich.", "I've got a tricky puzzle for you."), task, tab = 3),
            )
        }
        // Alle 30 Minuten der nächste Satz
        val slot = at.toLocalDate().toEpochDay() * 48 + (h * 60 + at.minute) / 30 + salt
        val v = options[Math.floorMod(slot, options.size.toLong()).toInt()]
        return LumiLine(v.image, v.title, v.text, v.cta, v.tab, v.big, v.bigUnit, v.live, v.urgent, v.goal)
    }
}
