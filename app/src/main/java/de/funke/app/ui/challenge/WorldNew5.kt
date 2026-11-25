package de.funke.app.ui.challenge

/* Ausbau (seit 13.2): weitere Logikrätsel mit eigenem Aufbau. Texte selbst geschrieben. */

private val E = Difficulty.Easy
private val M = Difficulty.Medium

object WorldNew5 {
    val all: List<TaskDef> get() = logic

    private val logic = listOf(
        lab("logik.pferdehandel", Topic.Logic, E, 0.35f) {
            val (a, b, c, d) = pick(listOf(60, 70, 80, 90), listOf(100, 120, 130, 150), listOf(40, 50, 60, 75))
            val profit = -a + b - c + d
            task(
                question = l("Eine Händlerin kauft ein Pferd für ${a} €, verkauft es für ${b} €, kauft es für ${c} € zurück und verkauft es schließlich für ${d} €. Wie viel Gewinn hat sie insgesamt gemacht?", "A trader buys a horse for €${a}, sells it for €${b}, buys it back for €${c} and finally sells it for €${d}. How much profit has she made altogether?"),
                steps = listOf(num(l("Gewinn in €", "Profit in €"), profit.toDouble(), "€", abs = 0.0, done = "$profit €",
                    hints = listOf(l("Kernidee: Lass dich nicht vom Pferd verwirren. Verfolge nur das Geld: Was geht raus, was kommt rein?", "Key idea: don't let the horse confuse you. Follow only the money: what goes out, what comes in?"), """§-${a} + ${b} - ${c} + ${d}§"""))),
                solution = listOf("""§-${a} + ${b} - ${c} + ${d} = ${profit}§""", l("Oder als zwei getrennte Geschäfte: ${b - a} € plus ${d - c} €.", "Or as two separate deals: €${b - a} plus €${d - c}.")),
                background = l("Viele antworten ${(b - a) - (c - b) + (d - c)} € oder sogar 0, weil sie meinen, das Zurückkaufen für ${c} € koste einen Teil des ersten Gewinns. Aber nach dem ersten Verkauf ist das Geschäft abgeschlossen. Wer die Geldflüsse einzeln aufschreibt, kann sich nicht verrechnen. Genau so arbeitet doppelte Buchführung: Jede Zahlung wird erfasst, egal, was die Geschichte dazu erzählt.", "Many people answer €${(b - a) - (c - b) + (d - c)} or even 0, because they think buying back for €${c} costs part of the first profit. But after the first sale that deal is finished. If you write down the cash flows one by one, you can't go wrong. That's exactly how double-entry bookkeeping works: every payment is recorded, whatever story goes with it."),
            )
        },
        mc("logik.lampenschalter", Topic.Logic, M,
            t("Im Keller sind drei Schalter, nur einer davon schaltet eine alte Glühbirne im verschlossenen Dachboden. Von unten siehst du die Lampe nicht. Du darfst die Schalter beliebig bedienen, aber nur ein einziges Mal nach oben gehen. Wie findest du den richtigen Schalter?", "In the cellar there are three switches, and only one of them operates an old incandescent bulb in the locked attic. From downstairs you can't see the bulb. You may use the switches as much as you like, but you may go upstairs only once. How do you find the right switch?"),
            t("Methode", "Method"),
            t("Schalter 1 zehn Minuten einschalten, dann aus. Schalter 2 ein. Oben: Brennt die Lampe, ist es 2. Ist sie aus, aber warm, ist es 1. Ist sie aus und kalt, ist es 3.", "Switch 1 on for ten minutes, then off. Switch 2 on. Upstairs: if the bulb is lit, it's 2. If it's off but warm, it's 1. If it's off and cold, it's 3."),
            listOf(t("Alle drei nacheinander kurz einschalten und hoffen, dass man es hört.", "Switch all three on briefly one after another and hope to hear it."), t("Schalter 1 und 2 gleichzeitig einschalten und oben schauen.", "Switch 1 and 2 on together and look upstairs."), t("Das ist unmöglich, man braucht mindestens zwei Wege.", "It's impossible; you need at least two trips.")),
            t("Kernidee: An oder aus ergibt nur zwei Zustände, du musst aber drei Schalter unterscheiden. Hat eine Glühbirne noch eine andere Eigenschaft, die du prüfen kannst?", "Key idea: on or off only gives two states, but you have to tell three switches apart. Does an incandescent bulb have another property you can check?"),
            t("Eine Glühbirne wird beim Leuchten heiß und bleibt danach eine Weile warm.", "An incandescent bulb gets hot when lit and stays warm for a while afterwards."),
            t("Die Wärme ist die dritte Information: an, aus-aber-warm, aus-und-kalt. Damit lassen sich mit einem einzigen Blick drei Möglichkeiten unterscheiden.", "The heat is the third piece of information: on, off-but-warm, off-and-cold. That way a single look can distinguish three possibilities."),
            t("Gute Lösungen nutzen oft eine Eigenschaft, an die man zuerst nicht denkt. Mit einer modernen LED-Lampe funktioniert der Trick kaum, weil sie fast nicht warm wird. Dahinter steckt eine Frage der Informationstheorie: Wie viele verschiedene Ergebnisse kann eine einzige Beobachtung liefern? Für drei Möglichkeiten braucht man mindestens drei unterscheidbare Ausgänge.", "Good solutions often use a property you don't think of at first. With a modern LED lamp the trick barely works, because it hardly gets warm. Behind it lies a question from information theory: how many different results can a single observation give? For three possibilities you need at least three distinguishable outcomes."),
        ),
        lab("logik.pizzaschnitte", Topic.Logic, M, 0.5f) {
            val cuts = pick(3, 4, 5); val pieces = 1 + cuts * (cuts + 1) / 2
            task(
                question = l("Du schneidest eine runde Pizza mit ${cuts} geraden Schnitten quer durch, ohne die Stücke zu verschieben oder zu stapeln. Die Stücke müssen nicht gleich groß sein. Wie viele Stücke kannst du höchstens bekommen?", "You cut a round pizza with ${cuts} straight cuts right across, without moving or stacking the pieces. The pieces don't have to be the same size. What is the largest number of pieces you can get?"),
                steps = listOf(num(l("Höchstzahl Stücke", "Maximum pieces"), pieces.toDouble(), "", abs = 0.0, done = "$pieces",
                    hints = listOf(l("Kernidee: Ein neuer Schnitt soll alle bisherigen Schnitte kreuzen, und zwar an verschiedenen Stellen. Jedes Mal, wenn er eine Linie kreuzt, teilt er ein weiteres Stück.", "Key idea: each new cut should cross all the previous cuts, at different points. Every time it crosses a line, it splits another piece."), l("Der k-te Schnitt erzeugt k neue Stücke: 1 + 1 + 2 + 3 + …", "The k-th cut creates k new pieces: 1 + 1 + 2 + 3 + …")))),
                solution = listOf("""§1 + 1 + 2 + \dots + ${cuts} = 1 + \dfrac{${cuts} \cdot ${cuts + 1}}{2} = ${pieces}§"""),
                background = l("Die meisten denken an Tortenstücke durch die Mitte und kommen auf ${2 * cuts}. Schneidet man aber nicht durch den Mittelpunkt, sondern so, dass sich alle Schnitte überkreuzen, entstehen mehr Stücke. Die Folge 2, 4, 7, 11, 16 heißt auf Englisch „lazy caterer's sequence“. Bei einem Kuchen mit Schnitten in alle drei Raumrichtungen sind mit drei Schnitten sogar 8 Stücke möglich.", "Most people think of cake slices through the middle and get ${2 * cuts}. But if you don't cut through the centre and instead make all the cuts cross, you get more pieces. The sequence 2, 4, 7, 11, 16 is known as the lazy caterer's sequence. With a cake, cut in all three directions of space, three cuts can even give 8 pieces."),
            )
        },
    )
}
