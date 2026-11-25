package de.funke.app.ui.challenge

import de.funke.app.ui.tr

/*
 * Philosophie (seit 11.0): Denker und ihre Gedanken, Begriffe, Gedankenexperimente und Aufgaben,
 * in denen man selbst Argumente prüft. Ziel: Man soll beim Üben wirklich etwas lernen, deshalb hat jede
 * Aufgabe eine kurze Erklärung. Alles selbst geschrieben; Zitate nur aus gemeinfreien Texten,
 * neuere Gedanken in eigenen Worten.
 */

private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

// ---------------------------------------------------------------------------------------------
// Denker und ihre Gedanken
// ---------------------------------------------------------------------------------------------

private class PhIdea(
    val key: String,
    val level: Difficulty,
    val topic: Topic,
    val who: T,
    /** Tipp: Zeit und Schule */
    val era: T,
    val idea: T,
    val why: T,
    val fact: T? = null,
)

private val ANTIQUE = t("Antikes Griechenland", "Ancient Greece")

private val IDEAS = listOf(
    PhIdea("sokrates", E, Topic.PhilThinkers, t("Sokrates", "Socrates"), ANTIQUE,
        t("Ich weiß, dass ich nichts weiß, und genau das macht mich klüger als die, die glauben, alles zu wissen.", "I know that I know nothing, and that is exactly what makes me wiser than those who think they know everything."),
        t("Laut Platons „Apologie“ hielt Sokrates sich nur deshalb für weise, weil er sein Nichtwissen kannte. Er befragte Athener so lange, bis sich zeigte, dass ihr vermeintliches Wissen nicht trug.", "According to Plato's Apology, Socrates thought himself wise only because he knew his own ignorance. He questioned Athenians until it turned out their supposed knowledge didn't hold up."),
        t("Sokrates schrieb selbst kein einziges Buch. Was wir über ihn wissen, stammt vor allem von seinem Schüler Platon.", "Socrates never wrote a single book. What we know about him comes mainly from his student Plato.")),
    PhIdea("platon", E, Topic.PhilKnowledge, t("Platon", "Plato"), ANTIQUE,
        t("Hinter den sichtbaren, vergänglichen Dingen stehen ewige, vollkommene Urbilder: die Ideen.", "Behind visible, perishable things stand eternal, perfect archetypes: the Forms."),
        t("Platons Ideenlehre: Jeder Kreis, den wir zeichnen, ist ungenau, aber wir erkennen ihn, weil wir die Idee des vollkommenen Kreises kennen. Wahres Wissen richtet sich auf diese Ideen, nicht auf die wechselnde Sinnenwelt.", "Plato's theory of Forms: every circle we draw is imperfect, yet we recognise it because we know the Form of the perfect circle. True knowledge aims at these Forms, not at the changing world of the senses.")),
    PhIdea("aristoteles", M, Topic.PhilEthics, t("Aristoteles", "Aristotle"), ANTIQUE,
        t("Eine Tugend liegt in der richtigen Mitte zwischen zwei Extremen, Mut zum Beispiel zwischen Feigheit und Tollkühnheit.", "A virtue lies at the right mean between two extremes, courage for example between cowardice and recklessness."),
        t("In der „Nikomachischen Ethik“ beschreibt Aristoteles Tugenden als Haltungen, die man durch Übung erwirbt. Die Mitte ist nicht mathematisch, sondern hängt von der Lage ab: Wer im richtigen Moment das Richtige tut, handelt gut.", "In the Nicomachean Ethics Aristotle describes virtues as dispositions you acquire through practice. The mean isn't mathematical but depends on the situation: whoever does the right thing at the right moment acts well.")),
    PhIdea("thales", E, Topic.PhilThinkers, t("Thales von Milet", "Thales of Miletus"), t("Antikes Griechenland, um 600 v. Chr.", "Ancient Greece, around 600 BC"),
        t("Der Urstoff, aus dem alles entstanden ist, ist das Wasser.", "The primal substance from which everything arose is water."),
        t("Thales gilt als erster Philosoph des Abendlands, weil er die Welt nicht mehr mit Göttergeschichten erklären wollte, sondern mit einem natürlichen Grundstoff. Die Antwort war falsch, aber die Art zu fragen war neu.", "Thales is considered the first philosopher of the West because he no longer wanted to explain the world with stories about gods but with a natural basic substance. The answer was wrong, but the way of asking was new."),
        t("Thales soll die Sonnenfinsternis des Jahres 585 v. Chr. vorhergesagt haben.", "Thales is said to have predicted the solar eclipse of 585 BC.")),
    PhIdea("demokrit", E, Topic.PhilKnowledge, t("Demokrit", "Democritus"), ANTIQUE,
        t("Alles besteht aus winzigen, unteilbaren Teilchen, die sich im leeren Raum bewegen.", "Everything consists of tiny, indivisible particles moving in empty space."),
        t("Demokrit und sein Lehrer Leukipp nannten diese Teilchen „atomos“, unteilbar. Das war reine Überlegung, kein Experiment; erst über 2000 Jahre später zeigte die Chemie, dass es Atome wirklich gibt (und später, dass sie doch teilbar sind).", "Democritus and his teacher Leucippus called these particles \"atomos\", indivisible. It was pure reasoning, not experiment; only more than 2000 years later did chemistry show that atoms really exist (and later that they can be split after all)."),
        t("Demokrit hieß der „lachende Philosoph“, weil er über die Torheiten der Menschen lieber lachte als klagte.", "Democritus was called the \"laughing philosopher\" because he preferred laughing at human folly to lamenting it.")),
    PhIdea("pythagoras", M, Topic.PhilKnowledge, t("Pythagoras", "Pythagoras"), t("Antikes Griechenland, um 530 v. Chr.", "Ancient Greece, around 530 BC"),
        t("Die Ordnung der Welt beruht auf Zahlen und ihren Verhältnissen.", "The order of the world rests on numbers and their ratios."),
        t("Die Pythagoreer entdeckten, dass harmonische Töne einfachen Zahlenverhältnissen der Saitenlängen entsprechen (Oktave 2:1, Quinte 3:2), und schlossen daraus, dass die ganze Welt mathematisch geordnet sei. Die moderne Physik gibt ihnen erstaunlich recht.", "The Pythagoreans discovered that harmonious tones correspond to simple ratios of string lengths (octave 2:1, fifth 3:2) and concluded that the whole world is mathematically ordered. Modern physics proves them surprisingly right.")),
    PhIdea("aristoteles-polis", E, Topic.PhilPolitics, t("Aristoteles", "Aristotle"), ANTIQUE,
        t("Der Mensch ist von Natur aus ein Gemeinschaftswesen; erst im Zusammenleben mit anderen kann er sich ganz entfalten.", "Humans are by nature social beings; only in living together with others can they develop fully."),
        t("Aristoteles nennt den Menschen ein „zoon politikon“, ein Lebewesen der Polis (des Stadtstaats). Wer außerhalb jeder Gemeinschaft leben könne, sei entweder ein Tier oder ein Gott. Sprache und Gerechtigkeitssinn zeigen, dass wir auf andere angelegt sind.", "Aristotle calls the human being a \"zoon politikon\", a creature of the polis (the city state). Whoever could live outside any community would be either a beast or a god. Language and a sense of justice show that we are made for others.")),
    PhIdea("sokrates-leben", M, Topic.PhilEthics, t("Sokrates", "Socrates"), ANTIQUE,
        t("Ein Leben, das sich nicht selbst prüft, ist nicht lebenswert.", "A life that does not examine itself is not worth living."),
        t("Das sagt Sokrates laut Platons „Apologie“ vor Gericht, kurz bevor er zum Tod verurteilt wird. Lieber wollte er sterben, als aufzuhören, sich und andere zu befragen. Für ihn ist Philosophieren keine Wissenschaft, sondern eine Lebensform.", "According to Plato's Apology, Socrates says this in court shortly before he is sentenced to death. He would rather die than stop questioning himself and others. For him philosophy is not a science but a way of life.")),
    PhIdea("seneca", M, Topic.PhilEthics, t("Seneca", "Seneca"), t("Stoa, Rom, 1. Jahrhundert n. Chr.", "Stoicism, Rome, 1st century AD"),
        t("Wir haben nicht zu wenig Zeit, wir vergeuden nur zu viel davon.", "It is not that we have too little time; we just waste too much of it."),
        t("Aus Senecas Schrift „Von der Kürze des Lebens“ (um 49 n. Chr.). Das Leben sei lang genug, wenn man es nicht mit Nebensächlichkeiten vertue. Im Zeitalter des Smartphones wirkt der Gedanke aktueller denn je.", "From Seneca's On the Shortness of Life (around AD 49). Life is long enough if you don't squander it on trivia. In the age of the smartphone the idea seems more topical than ever."),
        t("Seneca war Erzieher und Berater des Kaisers Nero, der ihn später zum Selbstmord zwang.", "Seneca was tutor and adviser to Emperor Nero, who later forced him to take his own life.")),
    PhIdea("epikur", M, Topic.PhilEthics, t("Epikur", "Epicurus"), ANTIQUE,
        t("Der Tod geht uns nichts an: Solange wir da sind, ist er nicht da, und wenn er da ist, sind wir nicht mehr.", "Death is nothing to us: as long as we exist, death is not here, and when it is here, we no longer exist."),
        t("Epikur wollte Menschen die Angst nehmen. Für ihn ist Glück vor allem die Abwesenheit von Schmerz und Unruhe; einfache Freuden, Freundschaft und ein ruhiger Geist zählen mehr als Luxus.", "Epicurus wanted to free people from fear. For him happiness is mainly the absence of pain and turmoil; simple pleasures, friendship and a calm mind count for more than luxury.")),
    PhIdea("epiktet", M, Topic.PhilEthics, t("Epiktet", "Epictetus"), t("Stoa, Römisches Reich", "Stoicism, Roman Empire"),
        t("Unterscheide, was in deiner Macht steht und was nicht, und kümmere dich nur um das Erste.", "Distinguish between what is in your power and what is not, and concern yourself only with the former."),
        t("Epiktet war zuerst Sklave, später Lehrer der Stoa. Nicht die Dinge beunruhigen uns, sondern unsere Urteile über sie. Darauf baut bis heute die kognitive Verhaltenstherapie auf.", "Epictetus was first a slave, later a Stoic teacher. It's not things that disturb us but our judgements about them. Cognitive behavioural therapy still builds on this idea today.")),
    PhIdea("descartes", E, Topic.PhilKnowledge, t("René Descartes", "René Descartes"), t("Rationalismus, 17. Jahrhundert", "Rationalism, 17th century"),
        t("Ich kann an allem zweifeln, nur nicht daran, dass ich gerade zweifle. Also denke ich, also bin ich.", "I can doubt everything except that I am doubting right now. So I think, therefore I am."),
        t("Descartes suchte einen Punkt, der jedem Zweifel standhält. Selbst wenn ein böser Geist ihn täuschen würde, müsste er existieren, um getäuscht zu werden: „cogito, ergo sum“.", "Descartes looked for a point that withstands all doubt. Even if an evil demon deceived him, he would have to exist in order to be deceived: \"cogito, ergo sum\".")),
    PhIdea("locke", M, Topic.PhilKnowledge, t("John Locke", "John Locke"), t("Empirismus, England, 17. Jahrhundert", "Empiricism, England, 17th century"),
        t("Der Geist ist bei der Geburt wie ein unbeschriebenes Blatt; alles Wissen kommt aus der Erfahrung.", "At birth the mind is like a blank slate; all knowledge comes from experience."),
        t("Locke widersprach der Idee angeborener Ideen. Sein Bild der „tabula rasa“ wurde zum Leitsatz des Empirismus: Erst die Sinne füllen das Blatt.", "Locke rejected the idea of innate ideas. His image of the \"tabula rasa\" became the motto of empiricism: only the senses fill the slate.")),
    PhIdea("hume-induktion", H, Topic.PhilKnowledge, t("David Hume", "David Hume"), t("Schottische Aufklärung, 18. Jahrhundert", "Scottish Enlightenment, 18th century"),
        t("Dass die Sonne morgen aufgeht, wissen wir nicht aus Vernunft, sondern nur aus Gewohnheit.", "That the sun will rise tomorrow we know not by reason but only by habit."),
        t("Humes Induktionsproblem: Aus noch so vielen Beobachtungen der Vergangenheit folgt logisch nichts über die Zukunft. Wir schließen trotzdem so, aus Gewöhnung. Bis heute gibt es keine allgemein anerkannte Lösung.", "Hume's problem of induction: no number of past observations logically implies anything about the future. We infer it anyway, out of habit. There's still no generally accepted solution.")),
    PhIdea("hume-sollen", H, Topic.PhilEthics, t("David Hume", "David Hume"), t("Schottische Aufklärung, 18. Jahrhundert", "Scottish Enlightenment, 18th century"),
        t("Aus dem, was ist, folgt allein noch nicht, was sein soll.", "From what is, it does not follow by itself what ought to be."),
        t("Hume bemerkte, dass viele Moralschriften unmerklich von „ist“-Sätzen zu „soll“-Sätzen springen. Für ein Sollen braucht es aber mindestens eine Prämisse, die selbst schon ein Sollen enthält.", "Hume noticed that many moral writings slip unnoticed from \"is\" statements to \"ought\" statements. But an ought needs at least one premise that already contains an ought.")),
    PhIdea("kant-zweck", M, Topic.PhilEthics, t("Immanuel Kant", "Immanuel Kant"), t("Aufklärung, Königsberg, 18. Jahrhundert", "Enlightenment, Königsberg, 18th century"),
        t("Handle so, dass du die Menschheit nie bloß als Mittel, sondern immer zugleich als Zweck behandelst.", "Act so that you treat humanity never merely as a means, but always at the same time as an end."),
        t("Die „Selbstzweckformel“ des kategorischen Imperativs: Andere zu brauchen ist erlaubt (der Bäcker verkauft dir Brot), sie bloß zu benutzen nicht. Jeder Mensch hat Würde, keinen Preis.", "The \"formula of humanity\" of the categorical imperative: needing others is fine (the baker sells you bread), merely using them is not. Every person has dignity, not a price.")),
    PhIdea("kant-anschauung", H, Topic.PhilKnowledge, t("Immanuel Kant", "Immanuel Kant"), t("Aufklärung, Königsberg, 18. Jahrhundert", "Enlightenment, Königsberg, 18th century"),
        t("Gedanken ohne Inhalt sind leer, Anschauungen ohne Begriffe sind blind.", "Thoughts without content are empty, intuitions without concepts are blind."),
        t("Kant versöhnt Rationalismus und Empirismus: Erkenntnis braucht beides, Sinnesdaten und Verstand. Die Sinne liefern den Stoff, der Verstand ordnet ihn mit Begriffen wie Ursache und Wirkung.", "Kant reconciles rationalism and empiricism: knowledge needs both sense data and understanding. The senses supply the material, the understanding orders it with concepts such as cause and effect.")),
    PhIdea("kant-aufklaerung", M, Topic.PhilThinkers, t("Immanuel Kant", "Immanuel Kant"), t("Aufklärung, Königsberg, 18. Jahrhundert", "Enlightenment, Königsberg, 18th century"),
        t("Aufklärung ist der Ausgang des Menschen aus seiner selbstverschuldeten Unmündigkeit. Habe Mut, dich deines eigenen Verstandes zu bedienen!", "Enlightenment is man's emergence from his self-incurred immaturity. Have the courage to use your own understanding!"),
        t("So beginnt Kants Aufsatz „Beantwortung der Frage: Was ist Aufklärung?“ von 1784. Unmündig ist, wer andere für sich denken lässt, nicht aus Mangel an Verstand, sondern aus Bequemlichkeit oder Feigheit.", "This is how Kant's 1784 essay \"An Answer to the Question: What Is Enlightenment?\" begins. Immature is whoever lets others think for them, not for lack of understanding but out of laziness or cowardice.")),
    PhIdea("bentham", M, Topic.PhilEthics, t("Jeremy Bentham", "Jeremy Bentham"), t("Utilitarismus, England, um 1800", "Utilitarianism, England, around 1800"),
        t("Richtig ist, was das größte Glück der größten Zahl hervorbringt.", "Right is whatever produces the greatest happiness of the greatest number."),
        t("Bentham begründete den Utilitarismus: Man soll die Folgen einer Handlung für alle Betroffenen zusammenrechnen, Freude positiv, Leid negativ. Er wollte damit auch Gesetze und Strafen vernünftiger machen.", "Bentham founded utilitarianism: you add up the consequences of an action for everyone affected, pleasure positive, pain negative. He also wanted to make laws and punishments more rational this way."),
        t("Bentham ließ sich nach seinem Tod präparieren; sein Skelett sitzt bis heute angezogen in einem Glaskasten im University College London.", "Bentham had his body preserved after death; his dressed skeleton still sits in a glass case at University College London.")),
    PhIdea("mill", H, Topic.PhilPolitics, t("John Stuart Mill", "John Stuart Mill"), t("Liberalismus, England, 19. Jahrhundert", "Liberalism, England, 19th century"),
        t("Die Freiheit eines Menschen darf nur eingeschränkt werden, um Schaden von anderen abzuwenden.", "A person's liberty may only be restricted to prevent harm to others."),
        t("Das Schadensprinzip aus „Über die Freiheit“ (1859): Über sich selbst ist jeder Herr. Was nur einem selbst schadet, darf der Staat nicht verbieten, wohl aber, was anderen schadet.", "The harm principle from On Liberty (1859): over himself each person is sovereign. What only harms yourself the state may not forbid, but it may forbid what harms others.")),
    PhIdea("hobbes", M, Topic.PhilPolitics, t("Thomas Hobbes", "Thomas Hobbes"), t("England, 17. Jahrhundert", "England, 17th century"),
        t("Ohne Staat herrscht ein Krieg aller gegen alle, und das Leben ist einsam, armselig, scheußlich, tierisch und kurz.", "Without a state there is a war of all against all, and life is solitary, poor, nasty, brutish and short."),
        t("Im „Leviathan“ (1651) begründet Hobbes den Staat mit einem Gesellschaftsvertrag: Aus Angst geben die Menschen ihre Macht an einen Herrscher ab, der dafür Frieden und Sicherheit garantiert.", "In Leviathan (1651) Hobbes justifies the state with a social contract: out of fear people hand their power to a ruler who in return guarantees peace and security.")),
    PhIdea("rousseau", M, Topic.PhilPolitics, t("Jean-Jacques Rousseau", "Jean-Jacques Rousseau"), t("Aufklärung, Frankreich, 18. Jahrhundert", "Enlightenment, France, 18th century"),
        t("Der Mensch wird frei geboren, und überall liegt er in Ketten.", "Man is born free, and everywhere he is in chains."),
        t("Der berühmte Satz vom Anfang des „Gesellschaftsvertrags“ (1762). Rousseau sucht eine Ordnung, in der jeder nur dem Gemeinwillen gehorcht, an dem er selbst beteiligt ist, und so frei bleibt.", "The famous sentence from the beginning of The Social Contract (1762). Rousseau looks for an order in which everyone obeys only the general will in which they themselves take part, and so remains free.")),
    PhIdea("marx", M, Topic.PhilPolitics, t("Karl Marx", "Karl Marx"), t("19. Jahrhundert, Industrialisierung", "19th century, industrialisation"),
        t("Nicht das Bewusstsein bestimmt das Sein, sondern das gesellschaftliche Sein bestimmt das Bewusstsein.", "It is not consciousness that determines being, but social being that determines consciousness."),
        t("Marx' Materialismus: Wie Menschen denken, hängt davon ab, wie sie arbeiten und wirtschaften. Wer in der Fabrik arbeitet, sieht die Welt anders als der Fabrikbesitzer.", "Marx's materialism: how people think depends on how they work and produce. Someone working in a factory sees the world differently from the factory owner.")),
    PhIdea("nietzsche", M, Topic.PhilThinkers, t("Friedrich Nietzsche", "Friedrich Nietzsche"), t("Deutschland, spätes 19. Jahrhundert", "Germany, late 19th century"),
        t("Gott ist tot, und wir haben ihn getötet.", "God is dead, and we have killed him."),
        t("Nietzsche meinte damit keine Tatsache über Gott, sondern eine Diagnose: Der Glaube trägt die Werte Europas nicht mehr. Ohne ihn droht der Nihilismus, und die Menschen müssen neue Werte schaffen.", "Nietzsche didn't mean a fact about God but a diagnosis: faith no longer supports Europe's values. Without it nihilism looms, and people must create new values.")),
    PhIdea("sartre", H, Topic.PhilThinkers, t("Jean-Paul Sartre", "Jean-Paul Sartre"), t("Existenzialismus, Frankreich, 20. Jahrhundert", "Existentialism, France, 20th century"),
        t("Der Mensch ist zuerst da und macht sich erst danach selbst zu dem, was er ist; er ist zur Freiheit verurteilt.", "A person first exists and only then makes himself into what he is; he is condemned to be free."),
        t("Sartres Existenzialismus: Es gibt kein vorgegebenes Wesen des Menschen. Wir entscheiden durch unsere Taten, wer wir sind, und tragen dafür die volle Verantwortung, auch wenn wir nichts entscheiden.", "Sartre's existentialism: there is no given human essence. Through our actions we decide who we are and bear full responsibility for it, even when we decide nothing.")),
    PhIdea("camus", H, Topic.PhilThinkers, t("Albert Camus", "Albert Camus"), t("Frankreich, 20. Jahrhundert", "France, 20th century"),
        t("Auch wer einen Stein immer wieder vergeblich den Berg hinaufrollt, kann glücklich sein; schon der Kampf selbst kann ein Herz erfüllen.", "Even someone who rolls a stone up the mountain again and again in vain can be happy; the struggle itself can fill a heart."),
        t("In „Der Mythos des Sisyphos“ beschreibt Camus das Absurde: Wir suchen Sinn, die Welt schweigt. Seine Antwort ist weder Verzweiflung noch Flucht, sondern Auflehnung: das Leben trotzdem voll zu leben.", "In The Myth of Sisyphus Camus describes the absurd: we seek meaning, the world stays silent. His answer is neither despair nor escape but revolt: living life fully anyway.")),
    PhIdea("beauvoir", H, Topic.PhilPolitics, t("Simone de Beauvoir", "Simone de Beauvoir"), t("Existenzialismus, Frankreich, 20. Jahrhundert", "Existentialism, France, 20th century"),
        t("Was eine Frau ausmacht, ist nicht angeboren; man wird durch die Gesellschaft dazu gemacht.", "What makes a woman is not innate; one is made into one by society."),
        t("In „Das andere Geschlecht“ (1949) zeigt Beauvoir, wie Rollenbilder Frauen als „das Andere“ des Mannes festlegen. Das Buch wurde zu einem Grundtext des Feminismus.", "In The Second Sex (1949) Beauvoir shows how role models define women as the \"other\" of men. The book became a founding text of feminism.")),
    PhIdea("arendt", X, Topic.PhilPolitics, t("Hannah Arendt", "Hannah Arendt"), t("20. Jahrhundert, nach dem Zweiten Weltkrieg", "20th century, after World War II"),
        t("Großes Unheil kann von ganz gewöhnlichen Menschen ausgehen, die einfach nicht nachdenken: die Banalität des Bösen.", "Great evil can come from quite ordinary people who simply don't think: the banality of evil."),
        t("Arendt beobachtete 1961 den Prozess gegen Adolf Eichmann in Jerusalem. Sie fand keinen dämonischen Täter, sondern einen Beamten, der Befehle ausführte, ohne zu urteilen. Daraus folgt: Selbst zu denken ist eine moralische Pflicht.", "In 1961 Arendt observed the trial of Adolf Eichmann in Jerusalem. She found not a demonic criminal but an official who carried out orders without judging. The consequence: thinking for yourself is a moral duty.")),
    PhIdea("popper", H, Topic.PhilKnowledge, t("Karl Popper", "Karl Popper"), t("Wissenschaftstheorie, 20. Jahrhundert", "Philosophy of science, 20th century"),
        t("Eine Theorie ist nur dann wissenschaftlich, wenn sie an der Erfahrung scheitern könnte.", "A theory is only scientific if it could fail against experience."),
        t("Poppers Falsifikationismus: „Alle Schwäne sind weiß“ lässt sich nie endgültig beweisen, aber ein einziger schwarzer Schwan widerlegt es. Gute Theorien wagen etwas und haben Prüfungen bisher überstanden.", "Popper's falsificationism: \"All swans are white\" can never be proven for good, but a single black swan refutes it. Good theories take risks and have survived tests so far.")),
    PhIdea("kuhn", X, Topic.PhilKnowledge, t("Thomas Kuhn", "Thomas Kuhn"), t("Wissenschaftsgeschichte, 20. Jahrhundert", "History of science, 20th century"),
        t("Wissenschaft wächst nicht nur gleichmäßig, sondern springt in Revolutionen von einem Paradigma zum nächsten.", "Science doesn't just grow steadily; it leaps in revolutions from one paradigm to the next."),
        t("In „Die Struktur wissenschaftlicher Revolutionen“ (1962) beschreibt Kuhn, wie ein Rahmen (etwa das Weltbild mit der Erde in der Mitte) lange trägt, bis sich Ungereimtheiten häufen und ein neuer Rahmen ihn ablöst.", "In The Structure of Scientific Revolutions (1962) Kuhn describes how a framework (such as the Earth-centred world view) holds for a long time until anomalies pile up and a new framework replaces it.")),
    PhIdea("wittgenstein-gebrauch", X, Topic.PhilKnowledge, t("Ludwig Wittgenstein", "Ludwig Wittgenstein"), t("Sprachphilosophie, 20. Jahrhundert", "Philosophy of language, 20th century"),
        t("Die Bedeutung eines Wortes ist sein Gebrauch in der Sprache.", "The meaning of a word is its use in the language."),
        t("Der späte Wittgenstein: Wörter sind keine Etiketten auf Dingen. Sie bekommen ihren Sinn in „Sprachspielen“, also in den Tätigkeiten, in denen wir sie verwenden, wie Befehlen, Fragen oder Witzen.", "The later Wittgenstein: words aren't labels stuck on things. They get their sense in \"language games\", the activities in which we use them, such as orders, questions or jokes.")),
    PhIdea("wittgenstein-schweigen", X, Topic.PhilKnowledge, t("Ludwig Wittgenstein", "Ludwig Wittgenstein"), t("Sprachphilosophie, 20. Jahrhundert", "Philosophy of language, 20th century"),
        t("Wovon man nicht sprechen kann, darüber muss man schweigen.", "Whereof one cannot speak, thereof one must be silent."),
        t("Der letzte Satz des „Tractatus“ (1921). Sinnvolle Sätze bilden für den frühen Wittgenstein Tatsachen ab; über Ethik, Sinn des Lebens oder Gott lässt sich in diesem Sinn nichts sagen, nur zeigen.", "The last sentence of the Tractatus (1921). For the early Wittgenstein meaningful sentences picture facts; about ethics, the meaning of life or God nothing can be said in this sense, only shown.")),
    PhIdea("konfuzius", M, Topic.PhilEthics, t("Konfuzius", "Confucius"), t("China, um 500 v. Chr.", "China, around 500 BC"),
        t("Was du selbst nicht wünschst, das tu auch anderen nicht an.", "What you do not wish for yourself, do not do to others."),
        t("Diese Fassung der Goldenen Regel steht in den „Gesprächen“ des Konfuzius. Ähnliche Regeln finden sich in fast allen Kulturen und Religionen, etwa in der Bibel und im Talmud.", "This version of the Golden Rule is found in the Analects of Confucius. Similar rules appear in almost every culture and religion, for example in the Bible and the Talmud.")),
    PhIdea("laozi", H, Topic.PhilThinkers, t("Laozi", "Laozi"), t("China, Daoismus", "China, Daoism"),
        t("Wer andere kennt, ist klug; wer sich selbst kennt, ist weise.", "Whoever knows others is clever; whoever knows himself is wise."),
        t("Aus dem „Daodejing“, dem Grundtext des Daoismus. Er empfiehlt, im Einklang mit dem Dao, dem Weg der Natur, zu leben: zurückhaltend, nachgiebig wie Wasser, das trotzdem den Stein aushöhlt.", "From the Daodejing, the founding text of Daoism. It recommends living in harmony with the Dao, the way of nature: restrained, yielding like water, which still wears away stone.")),
    PhIdea("buddha", H, Topic.PhilMind, t("Buddha", "Buddha"), t("Indien, um 500 v. Chr.", "India, around 500 BC"),
        t("Es gibt kein festes, unveränderliches Ich, und wer an einem solchen Ich festhält, leidet.", "There is no fixed, unchanging self, and whoever clings to such a self suffers."),
        t("Die Lehre vom Nicht-Selbst (anatta): Körper, Gefühle, Gedanken wechseln ständig. Wer am Ich festhält, leidet. Erstaunlich nah daran ist die Bündeltheorie des Ich bei David Hume, über 2000 Jahre später.", "The doctrine of non-self (anatta): body, feelings and thoughts change constantly. Clinging to a self brings suffering. David Hume's bundle theory of the self, more than 2000 years later, is remarkably close to it.")),
    PhIdea("heraklit", M, Topic.PhilThinkers, t("Heraklit", "Heraclitus"), ANTIQUE,
        t("Man kann nicht zweimal in denselben Fluss steigen.", "You cannot step into the same river twice."),
        t("Heraklit sah die Welt in ständigem Wandel („alles fließt“). Das Wasser ist beim zweiten Mal ein anderes, und auch du bist ein anderer. Trotzdem hat der Wandel eine Ordnung, den Logos.", "Heraclitus saw the world in constant flux (\"everything flows\"). The water is different the second time, and so are you. Yet change has an order, the logos.")),
    PhIdea("parmenides", X, Topic.PhilThinkers, t("Parmenides", "Parmenides"), ANTIQUE,
        t("Nur das Seiende ist; Werden und Vergehen sind bloßer Schein der Sinne.", "Only what is, is; coming to be and passing away are mere appearances of the senses."),
        t("Parmenides argumentierte rein logisch: Aus Nichts kann nichts werden, und Seiendes kann nicht zu Nichts werden. Also ist Veränderung unmöglich. Damit ist er der große Gegenspieler Heraklits.", "Parmenides argued purely logically: nothing can come from nothing, and what is cannot become nothing. So change is impossible. This makes him Heraclitus's great opponent.")),
    PhIdea("zenon", H, Topic.Philosophy, t("Zenon von Elea", "Zeno of Elea"), ANTIQUE,
        t("Der schnelle Achilles holt eine Schildkröte mit Vorsprung nie ein: Immer wenn er dort ist, wo sie war, ist sie schon ein Stück weiter.", "Swift Achilles never catches a tortoise with a head start: whenever he reaches where it was, it has moved a little further."),
        t("Zenon, ein Schüler des Parmenides, wollte zeigen, dass Bewegung widersprüchlich ist. Die Mathematik löst es heute so: Unendlich viele immer kürzere Strecken können zusammen eine endliche Länge und Zeit ergeben.", "Zeno, a student of Parmenides, wanted to show that motion is contradictory. Mathematics resolves it today: infinitely many ever-shorter stretches can add up to a finite length and time.")),
    PhIdea("aquin", H, Topic.PhilThinkers, t("Thomas von Aquin", "Thomas Aquinas"), t("Mittelalter, Scholastik", "Middle Ages, scholasticism"),
        t("Glaube und Vernunft widersprechen sich nicht; beide führen zur selben Wahrheit.", "Faith and reason do not contradict each other; both lead to the same truth."),
        t("Thomas verband im 13. Jahrhundert die Philosophie des Aristoteles mit der christlichen Theologie. Vieles lasse sich mit Vernunft allein erkennen, manches nur im Glauben, aber nie im Widerspruch zueinander.", "In the 13th century Thomas combined Aristotle's philosophy with Christian theology. Much can be known by reason alone, some things only in faith, but never in contradiction to each other.")),
    PhIdea("ockham", M, Topic.PhilKnowledge, t("Wilhelm von Ockham", "William of Ockham"), t("Mittelalter, 14. Jahrhundert", "Middle Ages, 14th century"),
        t("Man soll nicht mehr Annahmen machen als nötig.", "One should not make more assumptions than necessary."),
        t("„Ockhams Rasiermesser“: Von zwei Erklärungen, die gleich gut passen, ist die mit weniger Annahmen vorzuziehen. Das ist keine Garantie für Wahrheit, aber eine gute Faustregel in der Wissenschaft.", "\"Ockham's razor\": of two explanations that fit equally well, prefer the one with fewer assumptions. It's no guarantee of truth, but a good rule of thumb in science.")),
    PhIdea("machiavelli", M, Topic.PhilPolitics, t("Niccolò Machiavelli", "Niccolò Machiavelli"), t("Renaissance, Florenz", "Renaissance, Florence"),
        t("Für einen Herrscher ist es sicherer, gefürchtet als geliebt zu werden, wenn er nicht beides haben kann.", "For a ruler it is safer to be feared than loved, if he cannot be both."),
        t("„Der Fürst“ (1513) beschreibt Politik, wie sie ist, nicht wie sie sein sollte. Machiavelli trennt Macht von Moral, deshalb steht sein Name heute für skrupelloses Kalkül.", "The Prince (1513) describes politics as it is, not as it ought to be. Machiavelli separates power from morality, which is why his name today stands for ruthless calculation.")),
    PhIdea("spinoza", X, Topic.PhilThinkers, t("Baruch de Spinoza", "Baruch Spinoza"), t("Niederlande, 17. Jahrhundert", "Netherlands, 17th century"),
        t("Gott und die Natur sind ein und dasselbe.", "God and nature are one and the same."),
        t("„Deus sive natura“: Spinoza sah Gott nicht als Person außerhalb der Welt, sondern als die eine Substanz, die alles ist. Wegen solcher Gedanken wurde er 1656 aus der jüdischen Gemeinde Amsterdams ausgeschlossen.", "\"Deus sive natura\": Spinoza saw God not as a person outside the world but as the one substance that is everything. Because of such thoughts he was expelled from Amsterdam's Jewish community in 1656.")),
    PhIdea("leibniz", H, Topic.PhilThinkers, t("Gottfried Wilhelm Leibniz", "Gottfried Wilhelm Leibniz"), t("Barock, Deutschland, um 1700", "Baroque, Germany, around 1700"),
        t("Unsere Welt ist die beste aller möglichen Welten.", "Ours is the best of all possible worlds."),
        t("Leibniz' Antwort auf die Theodizee-Frage: Ein guter, allmächtiger Gott wählte unter allen möglichen Welten die beste, auch wenn es darin Leid gibt. Voltaire spottete darüber im Roman „Candide“.", "Leibniz's answer to the problem of evil: a good, almighty God chose the best among all possible worlds, even if there is suffering in it. Voltaire mocked this in the novel Candide."),
        t("Leibniz erfand gleichzeitig mit Newton die Differentialrechnung; unsere Schreibweise dx stammt von ihm.", "Leibniz invented calculus at the same time as Newton; our notation dx comes from him.")),
    PhIdea("berkeley", X, Topic.PhilKnowledge, t("George Berkeley", "George Berkeley"), t("Irland, 18. Jahrhundert", "Ireland, 18th century"),
        t("Sein heißt wahrgenommen werden.", "To be is to be perceived."),
        t("Berkeleys Idealismus: Dinge existieren nur als Vorstellungen in einem Geist. Der Baum im leeren Wald verschwindet trotzdem nicht, weil Gott ihn immer wahrnimmt.", "Berkeley's idealism: things exist only as ideas in a mind. The tree in the empty forest still doesn't vanish, because God always perceives it.")),
    PhIdea("schopenhauer", X, Topic.PhilMind, t("Arthur Schopenhauer", "Arthur Schopenhauer"), t("Deutschland, 19. Jahrhundert", "Germany, 19th century"),
        t("Der Mensch kann zwar tun, was er will, aber er kann nicht wollen, was er will.", "A person can do what he wills, but he cannot will what he wills."),
        t("Schopenhauer zur Willensfreiheit: Handeln ist frei, wenn nichts uns hindert. Doch unsere Wünsche selbst haben wir uns nicht ausgesucht; sie entspringen unserem Charakter, und der steht fest.", "Schopenhauer on free will: action is free when nothing stops us. But we didn't choose our desires themselves; they spring from our character, and that is fixed.")),
    PhIdea("kierkegaard", X, Topic.PhilThinkers, t("Søren Kierkegaard", "Søren Kierkegaard"), t("Dänemark, 19. Jahrhundert", "Denmark, 19th century"),
        t("Glauben heißt, den Sprung zu wagen, wo die Vernunft nicht weiterkommt.", "Faith means daring the leap where reason can go no further."),
        t("Kierkegaard gilt als Vorläufer des Existenzialismus. Für ihn ist das Wichtigste nicht eine objektive Wahrheit, sondern wie der Einzelne sich entscheidet und mit Angst und Verzweiflung umgeht.", "Kierkegaard is seen as a forerunner of existentialism. For him what matters most isn't objective truth but how the individual decides and deals with anxiety and despair.")),
    PhIdea("hegel", X, Topic.PhilThinkers, t("Georg Wilhelm Friedrich Hegel", "Georg Wilhelm Friedrich Hegel"), t("Deutscher Idealismus, um 1800", "German Idealism, around 1800"),
        t("Die Wahrheit entwickelt sich, indem Gegensätze in einer höheren Einheit aufgehoben werden.", "Truth develops as opposites are sublated in a higher unity."),
        t("Hegels Dialektik. „Aufheben“ hat bei ihm drei Bedeutungen zugleich: beenden, bewahren und auf eine höhere Stufe heben. So verstand er auch die Weltgeschichte als Fortschritt im Bewusstsein der Freiheit.", "Hegel's dialectic. \"Aufheben\" (to sublate) means three things at once for him: to end, to preserve and to lift to a higher level. He also understood world history this way, as progress in the consciousness of freedom.")),
    PhIdea("rawls", H, Topic.PhilPolitics, t("John Rawls", "John Rawls"), t("USA, 20. Jahrhundert", "USA, 20th century"),
        t("Gerecht sind die Regeln, die wir wählen würden, ohne zu wissen, welchen Platz wir selbst in der Gesellschaft bekommen.", "Just are the rules we would choose without knowing what place we ourselves will get in society."),
        t("Der „Schleier des Nichtwissens“ aus „Eine Theorie der Gerechtigkeit“ (1971): Wer nicht weiß, ob er arm oder reich, gesund oder krank sein wird, wählt faire Regeln und sorgt besonders für die Schwächsten.", "The \"veil of ignorance\" from A Theory of Justice (1971): whoever doesn't know whether they'll be poor or rich, healthy or ill, chooses fair rules and looks out especially for the weakest.")),
    PhIdea("singer", H, Topic.PhilEthics, t("Peter Singer", "Peter Singer"), t("Australien, seit den 1970er-Jahren", "Australia, from the 1970s on"),
        t("Das Leiden eines Tieres zählt genauso wie gleich starkes Leiden eines Menschen; alles andere ist Speziesismus.", "An animal's suffering counts just as much as equally intense human suffering; anything else is speciesism."),
        t("Singer überträgt den Utilitarismus auf Tiere: Entscheidend ist die Fähigkeit zu leiden, nicht die Art. „Animal Liberation“ (1975) prägte die Tierrechtsbewegung.", "Singer extends utilitarianism to animals: what matters is the capacity to suffer, not the species. Animal Liberation (1975) shaped the animal rights movement.")),
    PhIdea("nagel", X, Topic.PhilMind, t("Thomas Nagel", "Thomas Nagel"), t("USA, 20. Jahrhundert", "USA, 20th century"),
        t("Wie es ist, eine Fledermaus zu sein, verrät uns keine noch so genaue Beschreibung ihres Gehirns.", "No description of a bat's brain, however exact, tells us what it is like to be a bat."),
        t("Nagels Aufsatz von 1974: Bewusstsein hat eine Innenseite, ein „Wie-es-ist“. Die Physik beschreibt die Welt von außen, deshalb scheint sie das Erleben selbst nicht erfassen zu können.", "Nagel's 1974 paper: consciousness has an inside, a \"what it is like\". Physics describes the world from outside, so it seems unable to capture experience itself.")),
    PhIdea("turing", M, Topic.PhilMind, t("Alan Turing", "Alan Turing"), t("England, Mitte 20. Jahrhundert", "England, mid 20th century"),
        t("Ob eine Maschine denken kann, prüft man am besten, indem man schaut, ob sie sich im Gespräch nicht von einem Menschen unterscheiden lässt.", "The best way to test whether a machine can think is to see whether it can't be told apart from a human in conversation."),
        t("Das „Imitationsspiel“ von 1950, heute Turing-Test genannt. Turing ersetzte die unklare Frage „Kann eine Maschine denken?“ durch eine prüfbare. Ob Bestehen wirklich Denken beweist, ist umstritten.", "The \"imitation game\" of 1950, today called the Turing test. Turing replaced the vague question \"Can a machine think?\" with a testable one. Whether passing really proves thinking is disputed.")),
    PhIdea("montaigne", H, Topic.PhilThinkers, t("Michel de Montaigne", "Michel de Montaigne"), t("Renaissance, Frankreich", "Renaissance, France"),
        t("Was weiß ich schon? Wer sich selbst genau beobachtet, findet vor allem Widersprüche.", "What do I know? Whoever observes himself closely finds mostly contradictions."),
        t("Montaigne erfand mit seinen „Essais“ (1580) eine neue Form: tastende Versuche über sich selbst und die Welt. Sein Motto „Que sais-je?“ steht für einen gelassenen Skeptizismus.", "With his Essays (1580) Montaigne invented a new form: tentative attempts about himself and the world. His motto \"Que sais-je?\" stands for a relaxed scepticism.")),
    PhIdea("pascal", M, Topic.PhilThinkers, t("Blaise Pascal", "Blaise Pascal"), t("Frankreich, 17. Jahrhundert", "France, 17th century"),
        t("Das Herz hat seine Gründe, die der Verstand nicht kennt.", "The heart has its reasons which reason does not know."),
        t("Aus Pascals „Gedanken“ (1670). Der Mathematiker und Physiker meinte: Es gibt Gewissheiten, etwa über Liebe oder Glauben, die nicht aus Beweisen kommen.", "From Pascal's Pensées (1670). The mathematician and physicist argued that there are certainties, about love or faith for example, that don't come from proofs."),
        t("Nach Pascal ist die Einheit des Drucks benannt: 1 Pascal ist 1 Newton pro Quadratmeter.", "The unit of pressure is named after Pascal: 1 pascal is 1 newton per square metre.")),
    PhIdea("bacon", M, Topic.PhilKnowledge, t("Francis Bacon", "Francis Bacon"), t("England, um 1600", "England, around 1600"),
        t("Wissen ist Macht: Die Natur beherrscht man nur, indem man ihr gehorcht.", "Knowledge is power: nature is only commanded by obeying her."),
        t("Bacon forderte eine neue Wissenschaft: Statt alte Bücher zu zitieren, soll man planmäßig beobachten und experimentieren. Er gilt als Wegbereiter der modernen Naturwissenschaft.", "Bacon called for a new science: instead of quoting old books, observe and experiment systematically. He is seen as a pioneer of modern natural science.")),
    PhIdea("diogenes", M, Topic.PhilEthics, t("Diogenes von Sinope", "Diogenes of Sinope"), ANTIQUE,
        t("Glücklich ist, wer mit möglichst wenig auskommt; Bedürfnisse machen abhängig.", "Happy is whoever gets by with as little as possible; needs make you dependent."),
        t("Diogenes lebte als Kyniker angeblich in einer Tonne. Als Alexander der Große ihm einen Wunsch freistellte, soll er gesagt haben: „Geh mir ein wenig aus der Sonne!“", "Diogenes, a Cynic, supposedly lived in a barrel. When Alexander the Great offered him any wish, he is said to have replied: \"Stand a little out of my sun!\"")),
    PhIdea("protagoras", H, Topic.PhilKnowledge, t("Protagoras", "Protagoras"), ANTIQUE,
        t("Der Mensch ist das Maß aller Dinge.", "Man is the measure of all things."),
        t("Protagoras war ein Sophist. Sein Satz wird oft als Relativismus gelesen: Was wahr ist, hängt vom Betrachter ab. Platon hielt dagegen, dass dann auch der Satz selbst nur „für Protagoras“ wahr wäre.", "Protagoras was a Sophist. His saying is often read as relativism: what is true depends on the observer. Plato objected that then the saying itself would only be true \"for Protagoras\".")),
    PhIdea("wollstonecraft", H, Topic.PhilPolitics, t("Mary Wollstonecraft", "Mary Wollstonecraft"), t("England, Aufklärung", "England, Enlightenment"),
        t("Frauen sind nicht von Natur aus weniger vernünftig als Männer; ihnen fehlt nur die gleiche Bildung.", "Women are not by nature less rational than men; they merely lack the same education."),
        t("„Eine Verteidigung der Rechte der Frau“ (1792) gilt als einer der ersten feministischen Texte. Wollstonecraft war die Mutter von Mary Shelley, der Autorin von „Frankenstein“.", "A Vindication of the Rights of Woman (1792) is considered one of the first feminist texts. Wollstonecraft was the mother of Mary Shelley, author of Frankenstein.")),
    PhIdea("habermas", X, Topic.PhilPolitics, t("Jürgen Habermas", "Jürgen Habermas"), t("Deutschland, seit den 1960er-Jahren", "Germany, from the 1960s on"),
        t("Gültig ist eine Norm nur, wenn alle Betroffenen ihr in einem freien, gleichberechtigten Gespräch zustimmen könnten.", "A norm is only valid if everyone affected could agree to it in a free and equal discussion."),
        t("Die Diskursethik: Nicht ein einzelner Kopf legt fest, was richtig ist, sondern das beste Argument in einem Gespräch ohne Zwang. Das prägt bis heute das Verständnis von Demokratie.", "Discourse ethics: it isn't a single mind that decides what is right but the best argument in a discussion free of coercion. This still shapes how we understand democracy today.")),
    PhIdea("gettier", X, Topic.PhilKnowledge, t("Edmund Gettier", "Edmund Gettier"), t("USA, 1963", "USA, 1963"),
        t("Eine wahre, gut begründete Meinung ist noch kein Wissen, wenn sie nur durch Zufall stimmt.", "A true, well-justified belief is not yet knowledge if it is only true by luck."),
        t("Gettiers drei Seiten von 1963 erschütterten die seit Platon übliche Definition „Wissen = wahre, gerechtfertigte Meinung“. Seitdem sucht man nach der fehlenden vierten Bedingung.", "Gettier's three pages from 1963 shook the definition used since Plato, \"knowledge = justified true belief\". Ever since, philosophers have been looking for the missing fourth condition.")),
    PhIdea("parfit", X, Topic.PhilMind, t("Derek Parfit", "Derek Parfit"), t("England, 20. Jahrhundert", "England, 20th century"),
        t("Wichtig ist nicht, ob genau ich weiterlebe, sondern ob meine Erinnerungen, Pläne und Beziehungen fortbestehen.", "What matters is not whether exactly I survive, but whether my memories, plans and relationships continue."),
        t("Parfit prüfte persönliche Identität mit Gedankenexperimenten wie dem Teleporter. Sein Schluss: Identität ist nicht das, worauf es ankommt. Er fand das tröstlich, sogar im Blick auf den Tod.", "Parfit tested personal identity with thought experiments such as the teleporter. His conclusion: identity is not what matters. He found this comforting, even with regard to death.")),
)

/**
 * Gedanken, die sich so ähneln, dass der andere Denker als falsche Antwort unfair wäre
 * (etwa Bentham und Mill beim größten Glück). Innerhalb einer Gruppe nie gegenseitig als Ablenker.
 */
private val IDEA_CLASH = listOf(
    setOf("bentham", "mill", "singer"),
    setOf("sartre", "beauvoir"),
    setOf("beauvoir", "wollstonecraft"),
    setOf("sokrates", "platon"),
    setOf("sokrates", "montaigne"),
    setOf("sokrates", "laozi"),
    setOf("buddha", "hume-induktion", "hume-sollen", "parfit"),
    setOf("locke", "hume-induktion", "hume-sollen", "bacon"),
    setOf("diogenes", "epikur", "epiktet"),
    setOf("pascal", "kierkegaard"),
    setOf("hegel", "marx"),
    setOf("schopenhauer", "spinoza"),
    setOf("aristoteles", "konfuzius"),
    setOf("aristoteles-polis", "marx", "konfuzius", "rousseau", "hobbes"),
    setOf("seneca", "epiktet", "epikur", "diogenes"),
    setOf("thales", "heraklit", "demokrit"),
    setOf("pythagoras", "platon", "bacon"),
    setOf("demokrit", "epikur"),
)

private fun clash(a: PhIdea, b: PhIdea): Boolean = a.who.de == b.who.de || IDEA_CLASH.any { a.key in it && b.key in it }

private fun ideaDefs(): List<TaskDef> = IDEAS.map { e ->
    lab("ph.idea.${e.key}", e.topic, e.level) {
        val reverse = e.idea.s.length <= 120 && chance(0.3)
        if (!reverse) {
            // Denker, von denen keiner der Gedanken mit diesem kollidiert
            val thinkers = IDEAS.groupBy { it.who.de }.values.filter { ideas -> ideas.none { clash(it, e) } }
            val others = draw(thinkers, 3) { true }.map { it.first().who.s }
            quiz(
                "x", e.topic, e.level,
                question = e.idea.s,
                prompt = tr("Von wem stammt dieser Gedanke?", "Whose idea is this?"),
                correct = e.who.s, wrong = others,
                solution = tr("Der Gedanke stammt von ${e.who.s}.", "The idea comes from ${e.who.s}."),
                background = e.why.s,
                hint = e.era.s,
                fact = e.fact?.s,
            )
        } else {
            val others = draw(IDEAS, 3) { !clash(it, e) && it.idea.s.length <= 120 }.map { it.idea.s }
            quiz(
                "x", e.topic, e.level,
                question = tr("Welcher Gedanke stammt von ${e.who.s}?", "Which idea comes from ${e.who.s}?"),
                prompt = tr("Der passende Gedanke", "The matching idea"),
                correct = e.idea.s, wrong = others,
                solution = tr("${e.who.s}: „${e.idea.s}“", "${e.who.s}: \"${e.idea.s}\""),
                background = e.why.s,
                hint = e.era.s,
                fact = e.fact?.s,
            )
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Begriffe
// ---------------------------------------------------------------------------------------------

private class PhTerm(
    val key: String,
    val level: Difficulty,
    val topic: Topic,
    val term: T,
    val meaning: T,
    val why: T,
    /** Überschneidungen: nie gegenseitig als falsche Antwort */
    val group: String = "",
)

private val CONCEPTS = listOf(
    PhTerm("philosophie", E, Topic.PhilThinkers, t("Philosophie", "Philosophy"),
        t("Wörtlich die „Liebe zur Weisheit“: das Nachdenken über die Grundfragen von Welt, Wissen und richtigem Leben.", "Literally the \"love of wisdom\": thinking about the basic questions of the world, knowledge and the right way to live."),
        t("Das Wort kommt aus dem Griechischen: philos (Freund) und sophia (Weisheit). Pythagoras soll sich als Erster so genannt haben: als einen, der die Weisheit liebt und sucht, statt zu behaupten, er besitze sie schon.", "The word comes from Greek: philos (friend) and sophia (wisdom). Pythagoras is said to have been the first to call himself this: someone who loves and seeks wisdom instead of claiming to possess it already.")),
    PhTerm("ethik", E, Topic.PhilEthics, t("Ethik", "Ethics"),
        t("Der Teil der Philosophie, der fragt, wie man handeln soll und was gut und gerecht ist.", "The part of philosophy that asks how one should act and what is good and just."),
        t("Die Ethik denkt über Moral nach: Moral sind die Regeln, die in einer Gesellschaft tatsächlich gelten; die Ethik fragt, ob und warum sie gut begründet sind. Ihre großen Richtungen sind Pflichtethik, Utilitarismus und Tugendethik.", "Ethics reflects on morality: morality is the set of rules that actually apply in a society; ethics asks whether and why they are well founded. Its major schools are duty ethics, utilitarianism and virtue ethics."), "teilgebiet"),
    PhTerm("logik", E, Topic.PhilArgument, t("Logik", "Logic"),
        t("Die Lehre vom richtigen Schließen: wann aus Aussagen zwingend andere Aussagen folgen.", "The study of correct reasoning: when statements necessarily follow from other statements."),
        t("Aristoteles begründete die Logik mit seiner Lehre vom Syllogismus: „Alle Menschen sind sterblich. Sokrates ist ein Mensch. Also ist Sokrates sterblich.“ Heute ist die Logik auch die Grundlage jedes Computers.", "Aristotle founded logic with his theory of the syllogism: \"All humans are mortal. Socrates is a human. So Socrates is mortal.\" Today logic is also the basis of every computer."), "teilgebiet"),
    PhTerm("erkenntnistheorie", M, Topic.PhilKnowledge, t("Erkenntnistheorie", "Epistemology"),
        t("Der Teil der Philosophie, der fragt, was Wissen ist und wie wir es erlangen können.", "The part of philosophy that asks what knowledge is and how we can attain it."),
        t("Ihre Grundfragen: Was unterscheidet Wissen von bloßer Meinung? Woher stammt es, aus Erfahrung oder aus Vernunft? Und können wir uns überhaupt sicher sein? Sie heißt auch Epistemologie, von griechisch episteme, Wissen.", "Its basic questions: what distinguishes knowledge from mere opinion? Where does it come from, experience or reason? And can we be certain at all? The name comes from Greek episteme, knowledge."), "teilgebiet"),
    PhTerm("metaphysik", M, Topic.PhilThinkers, t("Metaphysik", "Metaphysics"),
        t("Der Teil der Philosophie, der nach den Grundstrukturen der Wirklichkeit fragt: Was gibt es überhaupt, und was ist sein Wesen?", "The part of philosophy that asks about the basic structures of reality: what is there at all, and what is its nature?"),
        t("Der Name entstand eher zufällig: In einer antiken Ausgabe standen diese Schriften des Aristoteles „nach der Physik“ (meta ta physika). Zur Metaphysik gehören Fragen nach Sein, Zeit, Freiheit, Seele und Gott.", "The name arose rather by chance: in an ancient edition these writings of Aristotle stood \"after the Physics\" (meta ta physika). Metaphysics includes questions about being, time, freedom, the soul and God."), "teilgebiet"),
    PhTerm("atheismus", E, Topic.PhilThinkers, t("Atheismus", "Atheism"),
        t("Die Überzeugung, dass es keinen Gott gibt.", "The conviction that there is no God."),
        t("Der Atheismus behauptet mehr als der Agnostizismus, der die Frage offen lässt. Bekannte atheistische Denker waren Ludwig Feuerbach, für den der Mensch sich Gott nach seinem eigenen Bild schuf, und Bertrand Russell.", "Atheism claims more than agnosticism, which leaves the question open. Well-known atheist thinkers were Ludwig Feuerbach, for whom humans created God in their own image, and Bertrand Russell."), "zweifel"),
    PhTerm("altruismus", E, Topic.PhilEthics, t("Altruismus", "Altruism"),
        t("Uneigennütziges Handeln zum Wohl anderer, auch wenn es einem selbst nichts bringt.", "Selfless action for the good of others, even when you gain nothing from it yourself."),
        t("Das Wort prägte Auguste Comte im 19. Jahrhundert. Sein Gegenteil ist der Egoismus. Umstritten ist, ob es echten Altruismus überhaupt gibt oder ob wir beim Helfen immer auch ein gutes Gefühl für uns selbst suchen.", "Auguste Comte coined the word in the 19th century. Its opposite is egoism. It is disputed whether true altruism exists at all or whether in helping we are always also seeking a good feeling for ourselves.")),
    PhTerm("dilemma", E, Topic.PhilEthics, t("Dilemma", "Dilemma"),
        t("Eine Zwangslage, in der man zwischen zwei Möglichkeiten wählen muss, die beide unerwünscht sind.", "A predicament in which you must choose between two options that are both undesirable."),
        t("In einem moralischen Dilemma verletzt jede Wahl eine Pflicht, wie beim Weichensteller, der einen Menschen opfern müsste, um fünf zu retten. Solche Fälle sind für die Ethik wichtig, weil sie zeigen, wo Moraltheorien an ihre Grenzen stoßen.", "In a moral dilemma every choice violates a duty, as with the person at the switch who would have to sacrifice one human being to save five. Such cases matter to ethics because they show where moral theories reach their limits."), "zwickmuehle"),
    PhTerm("utopie", E, Topic.PhilPolitics, t("Utopie", "Utopia"),
        t("Der Entwurf einer idealen Gesellschaft, die es (noch) nirgends gibt.", "The design of an ideal society that does not (yet) exist anywhere."),
        t("Thomas Morus erfand das Wort 1516 für seinen Roman über eine ideale Insel: griechisch ou-topos heißt „Nicht-Ort“. Das Gegenteil, eine abschreckende Zukunft wie in Orwells „1984“, nennt man Dystopie.", "Thomas More invented the word in 1516 for his novel about an ideal island: Greek ou-topos means \"no place\". The opposite, a frightening future as in Orwell's Nineteen Eighty-Four, is called a dystopia.")),
    PhTerm("praemisse", E, Topic.PhilArgument, t("Prämisse", "Premise"),
        t("Eine Aussage, die in einem Argument vorausgesetzt wird und aus der der Schluss folgen soll.", "A statement that is assumed in an argument and from which the conclusion is meant to follow."),
        t("Ein Argument besteht aus Prämissen und einer Schlussfolgerung (Konklusion). Wer es angreifen will, hat genau zwei Möglichkeiten: zeigen, dass eine Prämisse falsch ist, oder zeigen, dass der Schluss gar nicht aus den Prämissen folgt.", "An argument consists of premises and a conclusion. Whoever wants to attack it has exactly two options: show that a premise is false, or show that the conclusion doesn't follow from the premises at all.")),
    PhTerm("paradox", E, Topic.Philosophy, t("Paradox", "Paradox"),
        t("Eine Überlegung, die zu einem Widerspruch führt, obwohl jeder einzelne Schritt vernünftig wirkt.", "A line of thought that leads to a contradiction although every single step seems reasonable."),
        t("Berühmt sind Zenons Paradoxien der Bewegung und das Lügner-Paradox („Dieser Satz ist falsch“). Für die Philosophie sind Paradoxien wertvoll: Sie zeigen, dass an unseren scheinbar selbstverständlichen Annahmen etwas nicht stimmen kann.", "Famous examples are Zeno's paradoxes of motion and the liar paradox (\"This sentence is false\"). Paradoxes are valuable to philosophy: they show that something must be wrong with our seemingly obvious assumptions."), "zwickmuehle"),
    PhTerm("empirismus", E, Topic.PhilKnowledge, t("Empirismus", "Empiricism"),
        t("Alles Wissen stammt letztlich aus der Sinneserfahrung.", "All knowledge ultimately comes from sense experience."),
        t("Empiristen wie Locke und Hume sagen: Ohne Erfahrung wüssten wir nichts über die Welt. Ihr Gegenstück ist der Rationalismus.", "Empiricists such as Locke and Hume say: without experience we would know nothing about the world. Its counterpart is rationalism."), "wissen"),
    PhTerm("rationalismus", M, Topic.PhilKnowledge, t("Rationalismus", "Rationalism"),
        t("Die wichtigsten Wahrheiten erkennt die Vernunft auch ohne Erfahrung.", "Reason recognises the most important truths even without experience."),
        t("Rationalisten wie Descartes, Spinoza und Leibniz nahmen die Mathematik zum Vorbild: Aus sicheren Grundsätzen wird durch reines Denken gefolgert.", "Rationalists such as Descartes, Spinoza and Leibniz took mathematics as their model: from certain principles you deduce by pure thought."), "wissen"),
    PhTerm("utilitarismus", M, Topic.PhilEthics, t("Utilitarismus", "Utilitarianism"),
        t("Richtig ist die Handlung, die das Wohl aller Betroffenen am meisten steigert.", "The right action is the one that increases the well-being of everyone affected the most."),
        t("Eine Folgenethik nach Bentham und Mill: Es zählt nur das Ergebnis, und jedes Wohl zählt gleich viel. Kritiker fragen, ob man dann auch Einzelne opfern dürfte.", "A consequentialist ethics after Bentham and Mill: only the result counts, and everyone's well-being counts equally. Critics ask whether individuals could then be sacrificed."), "folgen"),
    PhTerm("deontologie", M, Topic.PhilEthics, t("Pflichtethik (Deontologie)", "Duty ethics (deontology)"),
        t("Manche Handlungen sind geboten oder verboten, ganz gleich, welche Folgen sie haben.", "Some actions are required or forbidden no matter what their consequences."),
        t("Das bekannteste Beispiel ist Kant: Lügen ist verboten, auch wenn eine Lüge im Einzelfall Gutes bewirken würde. Gegenstück: der Utilitarismus.", "The best-known example is Kant: lying is forbidden even if a lie would do good in a particular case. Counterpart: utilitarianism."), "pflicht"),
    PhTerm("tugendethik", H, Topic.PhilEthics, t("Tugendethik", "Virtue ethics"),
        t("Entscheidend ist, was ein Mensch mit gutem Charakter in der Lage tun würde.", "What matters is what a person of good character would do in the situation."),
        t("Die Tugendethik geht auf Aristoteles zurück. Sie fragt weniger „Was soll ich tun?“ als „Wer will ich sein?“. Tugenden wie Mut oder Gerechtigkeit lernt man durch Übung.", "Virtue ethics goes back to Aristotle. It asks less \"What should I do?\" than \"Who do I want to be?\". Virtues such as courage or justice are learned through practice.")),
    PhTerm("existenzialismus", M, Topic.PhilThinkers, t("Existenzialismus", "Existentialism"),
        t("Der Mensch hat kein vorgegebenes Wesen und muss seinem Leben selbst einen Sinn geben.", "Humans have no given essence and must give their lives meaning themselves."),
        t("Eine Strömung des 20. Jahrhunderts um Sartre, Beauvoir und (mit Abstand) Camus. Freiheit und Verantwortung stehen im Mittelpunkt.", "A 20th-century movement around Sartre, Beauvoir and, at some distance, Camus. Freedom and responsibility are at its centre."), "sinn"),
    PhTerm("nihilismus", M, Topic.PhilThinkers, t("Nihilismus", "Nihilism"),
        t("Es gibt keine objektiven Werte und keinen vorgegebenen Sinn.", "There are no objective values and no given meaning."),
        t("Von lateinisch nihil, nichts. Nietzsche sah den Nihilismus kommen, wenn die alten Werte ihren Halt verlieren, und wollte ihn überwinden.", "From Latin nihil, nothing. Nietzsche saw nihilism coming once the old values lost their footing, and wanted to overcome it."), "sinn"),
    PhTerm("determinismus", M, Topic.PhilMind, t("Determinismus", "Determinism"),
        t("Alles, was geschieht, ist durch frühere Ursachen festgelegt.", "Everything that happens is fixed by earlier causes."),
        t("Wenn das stimmt, scheint auch jede Entscheidung schon festzustehen. Ob dann noch Raum für Freiheit bleibt, ist eine der ältesten Fragen der Philosophie.", "If this is true, every decision seems to be fixed already. Whether there's still room for freedom is one of philosophy's oldest questions."), "frei"),
    PhTerm("kompatibilismus", X, Topic.PhilMind, t("Kompatibilismus", "Compatibilism"),
        t("Freier Wille und Determinismus schließen sich nicht aus.", "Free will and determinism are not mutually exclusive."),
        t("Kompatibilisten wie Hume oder Daniel Dennett sagen: Frei ist, wer nach seinen eigenen Gründen handelt, ohne äußeren Zwang, auch wenn diese Gründe selbst Ursachen haben.", "Compatibilists such as Hume or Daniel Dennett say: free is whoever acts on their own reasons without outside coercion, even if those reasons have causes themselves."), "frei"),
    PhTerm("solipsismus", H, Topic.PhilKnowledge, t("Solipsismus", "Solipsism"),
        t("Sicher existiert nur mein eigener Geist; alles andere könnte Einbildung sein.", "Only my own mind certainly exists; everything else could be imagination."),
        t("Von lateinisch solus ipse, „nur ich selbst“. Kaum jemand vertritt ihn ernsthaft, aber er lässt sich erstaunlich schwer widerlegen. Descartes kam sicher bis zum „Ich denke“; für den Rest der Welt brauchte er Gott als Garanten.", "From Latin solus ipse, \"only oneself\". Hardly anyone holds it seriously, but it's surprisingly hard to refute. Descartes got safely as far as \"I think\"; for the rest of the world he needed God as a guarantor."), "zweifel"),
    PhTerm("skeptizismus", M, Topic.PhilKnowledge, t("Skeptizismus", "Scepticism"),
        t("Sicheres Wissen ist nicht oder kaum möglich.", "Certain knowledge is impossible or barely possible."),
        t("Schon antike Skeptiker wie Pyrrhon rieten, sich des Urteils zu enthalten. Descartes nutzte den Zweifel als Werkzeug, um doch noch etwas Sicheres zu finden.", "Ancient sceptics such as Pyrrho advised suspending judgement. Descartes used doubt as a tool to find something certain after all."), "zweifel"),
    PhTerm("agnostizismus", E, Topic.PhilThinkers, t("Agnostizismus", "Agnosticism"),
        t("Ob es einen Gott gibt, lässt sich nicht wissen.", "Whether there is a God cannot be known."),
        t("Der Begriff stammt von Thomas Huxley (1869). Agnostiker behaupten weder, dass es Gott gibt, noch dass es ihn nicht gibt. Damit unterscheiden sie sich vom Atheismus, der sagt, dass es keinen Gott gibt.", "The term comes from Thomas Huxley (1869). Agnostics claim neither that God exists nor that God doesn't. This sets them apart from atheism, which says there is no God."), "zweifel"),
    PhTerm("dualismus", H, Topic.PhilMind, t("Dualismus (Leib und Seele)", "Dualism (mind and body)"),
        t("Geist und Körper sind zwei grundverschiedene Dinge.", "Mind and body are two fundamentally different things."),
        t("Descartes unterschied die denkende Substanz von der ausgedehnten. Das große Problem: Wie kann etwas Nicht-Körperliches den Körper bewegen?", "Descartes distinguished thinking substance from extended substance. The big problem: how can something non-physical move the body?"), "geist"),
    PhTerm("physikalismus", H, Topic.PhilMind, t("Physikalismus", "Physicalism"),
        t("Alles, auch der Geist, ist am Ende etwas Physikalisches.", "Everything, including the mind, is ultimately something physical."),
        t("Heute vertreten viele Philosophen und Hirnforscher diese Sicht. Offen bleibt, wie aus Nervenzellen Erleben entsteht, das „harte Problem“ des Bewusstseins.", "Many philosophers and neuroscientists hold this view today. What remains open is how experience arises from nerve cells, the \"hard problem\" of consciousness."), "geist"),
    PhTerm("idealismus", X, Topic.PhilKnowledge, t("Idealismus", "Idealism"),
        t("Die Wirklichkeit ist im Grunde geistig oder hängt vom Bewusstsein ab.", "Reality is basically mental or depends on consciousness."),
        t("Berkeley meinte, Dinge seien Vorstellungen; der Deutsche Idealismus (Fichte, Schelling, Hegel) baute ganze Systeme auf dem Geist auf. Sein Gegenstück ist der Materialismus.", "Berkeley held that things are ideas; German Idealism (Fichte, Schelling, Hegel) built whole systems on mind. Its opposite is materialism."), "geist"),
    PhTerm("pragmatismus", X, Topic.PhilKnowledge, t("Pragmatismus", "Pragmatism"),
        t("Ob eine Idee wahr ist, zeigt sich daran, ob sie sich in der Praxis bewährt.", "Whether an idea is true shows in whether it proves itself in practice."),
        t("Eine amerikanische Strömung um Charles S. Peirce, William James und John Dewey. Gedanken sind Werkzeuge zum Handeln. Wahr ist für James, was sich im Leben bewährt.", "An American movement around Charles S. Peirce, William James and John Dewey. Thoughts are tools for action. For James, true is what proves itself in life.")),
    PhTerm("stoizismus", M, Topic.PhilEthics, t("Stoizismus", "Stoicism"),
        t("Gelassen hinnehmen, was man nicht ändern kann; es kommt auf das eigene Urteil an.", "Calmly accept what you can't change; what matters is your own judgement."),
        t("Die Stoa entstand um 300 v. Chr. in Athen. Bekannte Stoiker sind Seneca, Epiktet und Kaiser Mark Aurel. Heute ist sie wieder sehr gefragt.", "The Stoa arose around 300 BC in Athens. Well-known Stoics are Seneca, Epictetus and Emperor Marcus Aurelius. Today it's very popular again.")),
    PhTerm("hedonismus", M, Topic.PhilEthics, t("Hedonismus", "Hedonism"),
        t("Lust ist das höchste Gut und Schmerz das größte Übel.", "Pleasure is the highest good and pain the greatest evil."),
        t("Von griechisch hedone, Lust. Schon Epikur meinte aber vor allem ruhige, dauerhafte Freuden, nicht das schnelle Vergnügen. Kritiker fragen, ob uns nicht auch Dinge wichtig sind, die keine Freude machen.", "From Greek hedone, pleasure. Even Epicurus mainly meant calm, lasting joys, not quick pleasures. Critics ask whether we don't also care about things that bring no pleasure."), "folgen"),
    PhTerm("relativismus", M, Topic.PhilKnowledge, t("Relativismus", "Relativism"),
        t("Was wahr oder gut ist, hängt von Kultur, Zeit oder Standpunkt ab.", "What is true or good depends on culture, time or point of view."),
        t("Ein Problem: Der Satz „Alles ist relativ“ will selbst absolut gelten. Kritiker sehen darin einen Selbstwiderspruch. Schon Protagoras sagte: „Der Mensch ist das Maß aller Dinge.“", "One problem: the statement \"everything is relative\" itself claims to hold absolutely. Critics see a self-contradiction in this. Protagoras already said: \"Man is the measure of all things.\"")),
    PhTerm("falsifikation", H, Topic.PhilKnowledge, t("Falsifikationismus", "Falsificationism"),
        t("Theorien lassen sich nie endgültig beweisen, nur widerlegen.", "Theories can never be finally proven, only refuted."),
        t("Karl Poppers Lehre: Wissenschaft schreitet voran, indem sie mutige Vermutungen aufstellt und versucht, sie zu widerlegen. Eine Theorie, die zu allem passt, sagt nach Popper gar nichts.", "Karl Popper's doctrine: science progresses by putting forward bold conjectures and trying to refute them. A theory that fits everything, Popper says, tells us nothing."), "wissenschaft"),
    PhTerm("paradigma", H, Topic.PhilKnowledge, t("Paradigmenwechsel", "Paradigm shift"),
        t("Ein grundlegender Umbruch in einer Wissenschaft, nach dem man die Welt anders sieht.", "A fundamental upheaval in a science after which people see the world differently."),
        t("Thomas Kuhns Begriff. Beispiele: von der Erde zur Sonne in der Mitte, von Newton zu Einstein. Heute heißt fast jede Neuerung so; Kuhn meinte nur echte Umbrüche.", "Thomas Kuhn's term. Examples: from the Earth to the Sun at the centre, from Newton to Einstein. Today almost every innovation gets called that; Kuhn meant only real upheavals."), "wissenschaft"),
    PhTerm("apriori", H, Topic.PhilKnowledge, t("a priori", "a priori"),
        t("Wissen, das nicht von Erfahrung abhängt, etwa 7 + 5 = 12.", "Knowledge that doesn't depend on experience, such as 7 + 5 = 12."),
        t("Lateinisch „vom Früheren her“. Gegenteil: a posteriori, also aus Erfahrung, etwa „Zucker ist süß“. Ob es a priori Wissen über die Welt gibt, ist der große Streit zwischen Rationalisten und Empiristen.", "Latin for \"from what comes before\". Opposite: a posteriori, from experience, such as \"sugar is sweet\". Whether there is a priori knowledge about the world is the great dispute between rationalists and empiricists."), "kant"),
    PhTerm("analytisch", H, Topic.PhilKnowledge, t("analytischer Satz", "analytic statement"),
        t("Ein Satz, der schon durch die Bedeutung seiner Wörter wahr ist, wie „Alle Junggesellen sind unverheiratet“.", "A statement that is true merely by the meaning of its words, such as \"All bachelors are unmarried\"."),
        t("Kant: Analytische Sätze erläutern nur, was im Begriff schon steckt. Synthetische Sätze erweitern unser Wissen, wie „Dieser Junggeselle ist 30“.", "Kant: analytic statements only spell out what's already in the concept. Synthetic statements extend our knowledge, such as \"This bachelor is 30\"."), "kant"),
    PhTerm("synthapriori", X, Topic.PhilKnowledge, t("synthetisches Urteil a priori", "synthetic a priori judgement"),
        t("Ein Urteil, das unser Wissen erweitert und trotzdem ohne Erfahrung gilt.", "A judgement that extends our knowledge and yet holds without experience."),
        t("Kants Kernfrage war, wie solche Urteile möglich sind. Beispiele nach Kant: die Mathematik („7 + 5 = 12“) und „Jedes Ereignis hat eine Ursache“.", "Kant's central question was how such judgements are possible. Examples according to Kant: mathematics (\"7 + 5 = 12\") and \"Every event has a cause\"."), "kant"),
    PhTerm("qualia", H, Topic.PhilMind, t("Qualia", "Qualia"),
        t("Wie sich ein Erleben von innen anfühlt, etwa das Rot einer Rose oder der Geschmack von Kaffee.", "What an experience feels like from the inside, such as the red of a rose or the taste of coffee."),
        t("Qualia sind der Kern des „harten Problems“: Selbst wenn wir alles über das Gehirn wüssten, bliebe die Frage, warum es sich überhaupt nach etwas anfühlt.", "Qualia are at the heart of the \"hard problem\": even if we knew everything about the brain, the question would remain why it feels like anything at all."), "geist"),
    PhTerm("vertrag", M, Topic.PhilPolitics, t("Gesellschaftsvertrag", "Social contract"),
        t("Menschen gründen den Staat durch eine (gedachte) Übereinkunft, um sicher und frei zu leben.", "People found the state through an (imagined) agreement in order to live safely and freely."),
        t("Hobbes, Locke und Rousseau beschrieben ihn ganz verschieden. Kaum jemand meint, es habe den Vertrag wirklich gegeben; er dient als Prüfstein für gerechte Herrschaft.", "Hobbes, Locke and Rousseau described it quite differently. Hardly anyone thinks the contract really happened; it serves as a test for legitimate rule.")),
    PhTerm("natfehl", X, Topic.PhilArgument, t("naturalistischer Fehlschluss", "naturalistic fallacy"),
        t("„Gut“ wird mit einer natürlichen Eigenschaft gleichgesetzt, etwa mit „natürlich“ oder „angenehm“.", "\"Good\" is equated with a natural property, such as \"natural\" or \"pleasant\"."),
        t("Den Begriff prägte G. E. Moore (1903): „gut“ lasse sich nicht durch solche Eigenschaften definieren. Im Alltag meint man damit oft den Schluss „natürlich, also gut“, eng verwandt mit Humes Sein-Sollen-Problem. Auch Gift und Krankheiten sind natürlich.", "G. E. Moore coined the term (1903): \"good\" cannot be defined by such properties. In everyday use it often means the inference \"natural, therefore good\", closely related to Hume's is-ought problem. Poisons and diseases are natural too."), "fehlschluss"),
    PhTerm("rasiermesser", M, Topic.PhilKnowledge, t("Ockhams Rasiermesser", "Ockham's razor"),
        t("Von zwei gleich guten Erklärungen ist die mit weniger Annahmen vorzuziehen.", "Of two equally good explanations, prefer the one with fewer assumptions."),
        t("Benannt nach Wilhelm von Ockham. Es „rasiert“ überflüssige Annahmen weg. Es sagt aber nicht, dass die einfachste Erklärung immer stimmt.", "Named after William of Ockham. It \"shaves off\" unnecessary assumptions. But it doesn't say the simplest explanation is always true."), "wissenschaft"),
    PhTerm("dialektik", X, Topic.PhilThinkers, t("Dialektik", "Dialectic"),
        t("Ein Denken, das durch Widersprüche und deren Auflösung vorankommt.", "A way of thinking that advances through contradictions and their resolution."),
        t("Ursprünglich die Kunst des Gesprächs bei Sokrates und Platon, bei Hegel und Marx eine Bewegung der Geschichte selbst. Oft wird sie vereinfacht als Dreischritt beschrieben: These, Antithese, Synthese.", "Originally the art of dialogue in Socrates and Plato, for Hegel and Marx a movement of history itself. It is often simplified as three steps: thesis, antithesis, synthesis.")),
    PhTerm("entfremdung", X, Topic.PhilPolitics, t("Entfremdung", "Alienation"),
        t("Der Mensch erkennt sich in seiner Arbeit und deren Ergebnissen nicht mehr wieder.", "People no longer recognise themselves in their work and its results."),
        t("Bei Marx: Wer am Fließband nur einen Handgriff macht und das Produkt nicht besitzt, dem wird die eigene Arbeit fremd, und damit ein Stück von sich selbst.", "In Marx: whoever performs a single movement on the assembly line and doesn't own the product becomes estranged from their own work, and thus from part of themselves.")),
    PhTerm("phaenomenologie", X, Topic.PhilMind, t("Phänomenologie", "Phenomenology"),
        t("Die genaue Beschreibung dessen, wie uns die Dinge im Bewusstsein erscheinen.", "The precise description of how things appear to us in consciousness."),
        t("Begründet von Edmund Husserl um 1900. Ihr Motto: „Zu den Sachen selbst!“ Heidegger, Sartre und Merleau-Ponty bauten darauf auf.", "Founded by Edmund Husserl around 1900. Its motto: \"To the things themselves!\" Heidegger, Sartre and Merleau-Ponty built on it.")),
    PhTerm("theodizee", H, Topic.PhilThinkers, t("Theodizee", "Theodicy"),
        t("Die Frage, wie ein guter und allmächtiger Gott das Leid in der Welt zulassen kann.", "The question of how a good and almighty God can allow suffering in the world."),
        t("Das Wort prägte Leibniz (1710). Das Erdbeben von Lissabon 1755 mit Zehntausenden Toten machte die Frage für viele Denker der Aufklärung brennend.", "Leibniz coined the word (1710). The Lisbon earthquake of 1755, with tens of thousands of dead, made the question burning for many Enlightenment thinkers.")),
    PhTerm("imperativ", M, Topic.PhilEthics, t("kategorischer Imperativ", "categorical imperative"),
        t("Ein unbedingtes Gebot: nur nach Regeln handeln, die für alle gelten könnten.", "An unconditional command: act only on rules that could hold for everyone."),
        t("Kants Grundgesetz der Moral. „Kategorisch“ heißt: ohne Wenn und Aber, anders als hypothetische Imperative wie „Wenn du gesund bleiben willst, treib Sport“.", "Kant's fundamental law of morality. \"Categorical\" means without ifs or buts, unlike hypothetical imperatives such as \"If you want to stay healthy, exercise\"."), "pflicht"),
    PhTerm("maieutik", H, Topic.PhilThinkers, t("Mäeutik (Hebammenkunst)", "Maieutics (midwifery)"),
        t("Durch geschickte Fragen jemandem helfen, selbst auf eine Erkenntnis zu kommen.", "Helping someone reach an insight themselves through skilful questions."),
        t("So beschrieb Sokrates seine Methode: Wie seine Mutter, eine Hebamme, helfe er beim Gebären, nur eben von Gedanken. Er selbst behauptete, nichts zu wissen; die Einsicht sollte im Gesprächspartner entstehen.", "This is how Socrates described his method: like his mother, a midwife, he helped with giving birth, only to thoughts. He claimed to know nothing himself; the insight was to arise in the other person.")),
    PhTerm("aporie", X, Topic.Philosophy, t("Aporie", "Aporia"),
        t("Eine Ratlosigkeit, in der ein Problem ausweglos erscheint.", "A state of perplexity in which a problem seems to have no way out."),
        t("Griechisch „Weglosigkeit“. Viele frühe Dialoge Platons enden in einer Aporie: Man weiß am Ende, dass man es nicht weiß, und das ist schon ein Fortschritt.", "Greek for \"pathlessness\". Many of Plato's early dialogues end in aporia: at the end you know that you don't know, which is already progress.")),
)

/** Weitere Überschneidungen zwischen Begriffen (zusätzlich zu [PhTerm.group]). */
private val TERM_CLASH = listOf(
    setOf("tugendethik", "stoizismus"),
    setOf("maieutik", "dialektik", "aporie"),
    setOf("solipsismus", "idealismus"),
    setOf("rationalismus", "apriori", "synthapriori"),
    setOf("phaenomenologie", "qualia"),
    setOf("paradox", "aporie", "dilemma"),
    setOf("erkenntnistheorie", "empirismus", "rationalismus", "skeptizismus"),
    setOf("ethik", "utilitarismus", "deontologie", "tugendethik"),
    setOf("metaphysik", "idealismus", "physikalismus", "dualismus"),
    setOf("altruismus", "utilitarismus"),
)

/** Tipp zu jedem Begriff: Herkunft des Wortes oder wer ihn geprägt hat, ohne die Antwort zu verraten. */
private val TERM_HINTS: Map<String, T> = mapOf(
    "philosophie" to t("Griechisch philos heißt Freund, sophia heißt Weisheit.", "Greek philos means friend, sophia means wisdom."),
    "ethik" to t("Griechisch ethos heißt Gewohnheit, Charakter.", "Greek ethos means habit, character."),
    "logik" to t("Griechisch logos heißt Wort, Vernunft.", "Greek logos means word, reason."),
    "erkenntnistheorie" to t("Sie heißt auch Epistemologie.", "Greek episteme means knowledge."),
    "metaphysik" to t("Bei Aristoteles standen diese Schriften „nach der Physik“.", "In Aristotle's works these writings stood \"after the Physics\"."),
    "atheismus" to t("Griechisch a-theos: ohne Gott.", "Greek a-theos: without god."),
    "altruismus" to t("Lateinisch alter heißt „der andere“.", "Latin alter means \"the other\"."),
    "dilemma" to t("Griechisch di- heißt zwei.", "Greek di- means two."),
    "utopie" to t("Thomas Morus, 1516", "Thomas More, 1516"),
    "praemisse" to t("Lateinisch praemittere heißt vorausschicken.", "Latin praemittere means to send ahead."),
    "paradox" to t("Griechisch para doxa: gegen die Erwartung.", "Greek para doxa: contrary to expectation."),
    "empirismus" to t("Griechisch empeiria heißt Erfahrung.", "Greek empeiria means experience."),
    "rationalismus" to t("Lateinisch ratio heißt Vernunft.", "Latin ratio means reason."),
    "utilitarismus" to t("Lateinisch utilis heißt nützlich.", "Latin utilis means useful."),
    "deontologie" to t("Griechisch deon heißt das Erforderliche, die Pflicht.", "Greek deon means what is required, duty."),
    "tugendethik" to t("Sie geht auf Aristoteles zurück und fragt nach dem Charakter.", "It goes back to Aristotle and asks about character."),
    "existenzialismus" to t("Paris nach 1945: Sartre, Beauvoir, Camus.", "Paris after 1945: Sartre, Beauvoir, Camus."),
    "nihilismus" to t("Lateinisch nihil heißt nichts.", "Latin nihil means nothing."),
    "determinismus" to t("Lateinisch determinare heißt festlegen, begrenzen.", "Latin determinare means to fix, to limit."),
    "kompatibilismus" to t("Kompatibel heißt vereinbar.", "Compatible means able to coexist."),
    "solipsismus" to t("Lateinisch solus ipse: nur ich selbst.", "Latin solus ipse: only oneself."),
    "skeptizismus" to t("Griechisch skeptesthai heißt prüfen, genau hinsehen.", "Greek skeptesthai means to examine, to look closely."),
    "agnostizismus" to t("Griechisch a-gnostos: nicht erkennbar.", "Greek a-gnostos: unknowable."),
    "dualismus" to t("Lateinisch duo heißt zwei.", "Latin duo means two."),
    "physikalismus" to t("Griechisch physis heißt Natur.", "Greek physis means nature."),
    "idealismus" to t("Berkeley, Fichte, Hegel", "Berkeley, Fichte, Hegel"),
    "pragmatismus" to t("Griechisch pragma heißt Handlung, Sache.", "Greek pragma means deed, matter."),
    "stoizismus" to t("Seneca, Epiktet, Mark Aurel", "Seneca, Epictetus, Marcus Aurelius"),
    "hedonismus" to t("Griechisch hedone heißt Lust, Freude.", "Greek hedone means pleasure."),
    "relativismus" to t("Relativ heißt: bezogen auf etwas anderes.", "Relative means: related to something else."),
    "falsifikation" to t("Karl Popper, „Logik der Forschung“ (1934)", "Karl Popper, The Logic of Scientific Discovery (1934)"),
    "paradigma" to t("Thomas Kuhn, 1962", "Thomas Kuhn, 1962"),
    "apriori" to t("Lateinisch: vom Früheren her, also vor aller Erfahrung.", "Latin: from what comes before, i.e. prior to experience."),
    "analytisch" to t("Analysieren heißt zerlegen: Man zerlegt nur den Begriff.", "To analyse means to take apart: you only take the concept apart."),
    "synthapriori" to t("Kants große Frage in der „Kritik der reinen Vernunft“", "Kant's big question in the Critique of Pure Reason"),
    "qualia" to t("Lateinisch qualis heißt: wie beschaffen.", "Latin qualis means: of what kind."),
    "vertrag" to t("Hobbes, Locke, Rousseau", "Hobbes, Locke, Rousseau"),
    "natfehl" to t("G. E. Moore, 1903", "G. E. Moore, 1903"),
    "rasiermesser" to t("Ein Werkzeug, das Überflüssiges wegschneidet", "A tool that cuts away what is superfluous"),
    "dialektik" to t("Hegel, Marx", "Hegel, Marx"),
    "entfremdung" to t("Karl Marx über die Fabrikarbeit", "Karl Marx on factory work"),
    "phaenomenologie" to t("Griechisch phainomenon: das, was erscheint.", "Greek phainomenon: that which appears."),
    "theodizee" to t("Griechisch theos (Gott) und dike (Gerechtigkeit)", "Greek theos (god) and dike (justice)"),
    "imperativ" to t("Kants Grundgesetz der Moral", "Kant's fundamental law of morality"),
    "maieutik" to t("Sokrates' Mutter war Hebamme.", "Socrates' mother was a midwife."),
    "aporie" to t("Griechisch a-poros: ohne Weg.", "Greek a-poros: without a way."),
)

private fun conceptDefs(): List<TaskDef> = CONCEPTS.map { c ->
    lab("ph.term.${c.key}", c.topic, c.level) {
        fun ok(o: PhTerm) = o !== c && (c.group.isEmpty() || o.group != c.group) && TERM_CLASH.none { c.key in it && o.key in it }
        val hint = TERM_HINTS[c.key]?.s
        val solution = tr("„${c.term.s}“: ${c.meaning.s}", "\"${c.term.s}\": ${c.meaning.s}")
        if (chance(0.5)) {
            quiz(
                "x", c.topic, c.level,
                question = tr("Was bedeutet „${c.term.s}“?", "What does \"${c.term.s}\" mean?"),
                prompt = tr("Die richtige Erklärung", "The right explanation"),
                correct = c.meaning.s,
                wrong = draw(CONCEPTS, 3, ::ok).map { it.meaning.s },
                solution = solution,
                background = c.why.s,
                hint = hint,
            )
        } else {
            quiz(
                "x", c.topic, c.level,
                question = c.meaning.s,
                prompt = tr("Wie heißt das?", "What is this called?"),
                correct = c.term.s,
                wrong = draw(CONCEPTS, 3, ::ok).map { it.term.s },
                solution = solution,
                background = c.why.s,
                hint = hint,
            )
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Gedankenexperimente
// ---------------------------------------------------------------------------------------------

private class PhExperiment(
    val key: String,
    val level: Difficulty,
    val topic: Topic,
    val name: T,
    /** null = Urheber nicht eindeutig, dann nur die Frage nach der Aussage */
    val author: T?,
    val setup: T,
    val shows: T,
    val wrong: List<T>,
    val why: T,
)

private val EXPERIMENTS = listOf(
    PhExperiment("hoehle", E, Topic.PhilKnowledge, t("Höhlengleichnis", "Allegory of the cave"), t("Platon", "Plato"),
        t("Gefesselte Menschen sehen ihr Leben lang nur Schatten an einer Höhlenwand und halten sie für die ganze Wirklichkeit.", "Chained people see only shadows on a cave wall all their lives and take them for the whole of reality."),
        t("Unsere gewohnte Sicht kann ein bloßer Schatten der Wahrheit sein; Bildung heißt, sich zum Licht umzuwenden.", "Our usual view can be a mere shadow of the truth; education means turning towards the light."),
        listOf(t("Menschen sehen im Dunkeln schlechter als im Hellen.", "People see worse in the dark than in the light."), t("Wer allein lebt, wird mit der Zeit unglücklich.", "Whoever lives alone becomes unhappy over time."), t("Die Sonne ist die Ursache aller Schatten.", "The sun is the cause of all shadows.")),
        t("Im Gleichnis aus der „Politeia“ befreit sich einer, sieht draußen die echten Dinge und die Sonne (die Idee des Guten) und kehrt zurück. Die anderen glauben ihm nicht.", "In the allegory from the Republic one prisoner frees himself, sees the real things and the sun (the Form of the Good) outside and returns. The others don't believe him.")),
    PhExperiment("gyges", M, Topic.PhilEthics, t("Ring des Gyges", "Ring of Gyges"), t("Platon", "Plato"),
        t("Ein Hirte findet einen Ring, der ihn unsichtbar macht. Würde noch jemand gerecht handeln, wenn er nie erwischt werden kann?", "A shepherd finds a ring that makes him invisible. Would anyone still act justly if they could never be caught?"),
        t("Ob wir nur aus Angst vor Strafe gerecht sind oder weil Gerechtigkeit selbst gut ist.", "Whether we are just only out of fear of punishment or because justice itself is good."),
        listOf(t("Dass Unsichtbarkeit physikalisch möglich ist.", "That invisibility is physically possible."), t("Dass Hirten früher besonders ehrlich waren.", "That shepherds used to be especially honest."), t("Dass Schmuck Menschen verändert.", "That jewellery changes people.")),
        t("Glaukon erzählt die Geschichte in Platons „Politeia“. Sokrates versucht dann zu zeigen, dass der Gerechte auch ohne Zeugen glücklicher ist, weil seine Seele in Ordnung ist.", "Glaucon tells the story in Plato's Republic. Socrates then tries to show that the just person is happier even without witnesses, because their soul is in order.")),
    PhExperiment("buridan", H, Topic.PhilMind, t("Buridans Esel", "Buridan's ass"), null,
        t("Ein hungriger Esel steht genau in der Mitte zwischen zwei völlig gleichen Heuhaufen und verhungert, weil er keinen Grund hat, einen zu wählen.", "A hungry donkey stands exactly between two completely identical haystacks and starves because it has no reason to choose one."),
        t("Ob man frei wählen kann, wenn alle Gründe gleich stark sind.", "Whether one can choose freely when all reasons are equally strong."),
        listOf(t("Dass Esel schlechte Augen haben.", "That donkeys have poor eyesight."), t("Dass Heu als Futter ungeeignet ist.", "That hay is unsuitable as food."), t("Dass Tiere nie zögern.", "That animals never hesitate.")),
        t("Benannt nach Johannes Buridan, einem Philosophen des 14. Jahrhunderts, der das Beispiel selbst so aber wohl nie verwendet hat. In Wirklichkeit lösen schon winzige Zufälle das Patt auf.", "Named after Jean Buridan, a 14th-century philosopher, who probably never used the example in this form himself. In reality tiny random factors break the tie.")),
    PhExperiment("zimmer", H, Topic.PhilMind, t("Chinesisches Zimmer", "Chinese room"), t("John Searle", "John Searle"),
        t("Jemand ohne jede Chinesischkenntnis sitzt in einem Zimmer und beantwortet chinesische Fragen perfekt, nur mit einem Regelbuch, das Zeichen auf Zeichen abbildet.", "Someone who knows no Chinese sits in a room and answers Chinese questions perfectly using only a rule book that maps symbols to symbols."),
        t("Zeichen nach Regeln zu verarbeiten ist noch kein Verstehen.", "Processing symbols according to rules is not yet understanding."),
        listOf(t("Chinesisch ist schwerer zu lernen als andere Sprachen.", "Chinese is harder to learn than other languages."), t("Regelbücher sind die beste Lernmethode.", "Rule books are the best way to learn."), t("Computer können keine Zeichen lesen.", "Computers cannot read symbols.")),
        t("Searle richtete das Argument 1980 gegen die Idee, ein Computer verstehe etwas, nur weil er die richtigen Antworten gibt. Kritiker meinen: Vielleicht versteht ja das ganze System aus Mensch, Buch und Zimmer.", "Searle aimed the argument in 1980 at the idea that a computer understands something just because it gives the right answers. Critics reply: perhaps the whole system of person, book and room understands.")),
    PhExperiment("mary", H, Topic.PhilMind, t("Marys Zimmer", "Mary's room"), t("Frank Jackson", "Frank Jackson"),
        t("Die Forscherin Mary weiß alles Physikalische über Farben, ist aber in einem schwarz-weißen Raum aufgewachsen. Dann sieht sie zum ersten Mal eine rote Tomate.", "The scientist Mary knows everything physical about colours but grew up in a black-and-white room. Then she sees a red tomato for the first time."),
        t("Dass Erleben (wie es ist, Rot zu sehen) vielleicht mehr ist als physikalisches Wissen.", "That experience (what it's like to see red) may be more than physical knowledge."),
        listOf(t("Dass Farben nur in unserem Kopf existieren.", "That colours only exist in our heads."), t("Dass man in Schwarz-Weiß besser forschen kann.", "That research is easier in black and white."), t("Dass Tomaten eigentlich Obst sind.", "That tomatoes are actually fruit.")),
        t("Jackson veröffentlichte das „Wissensargument“ 1982. Lernt Mary beim ersten Rot etwas Neues, dann gab es eine Tatsache, die nicht in der Physik stand. Jackson selbst rückte später davon ab.", "Jackson published the \"knowledge argument\" in 1982. If Mary learns something new on seeing red for the first time, there was a fact not contained in physics. Jackson himself later distanced himself from it.")),
    PhExperiment("zombie", X, Topic.PhilMind, t("Philosophischer Zombie", "Philosophical zombie"), null,
        t("Ein Wesen, das genau gebaut ist wie du und sich genau so verhält, aber innerlich überhaupt nichts erlebt.", "A being built exactly like you that behaves exactly like you but experiences nothing at all inside."),
        t("Wenn so ein Wesen denkbar ist, scheint Bewusstsein mehr zu sein als Aufbau und Verhalten: das „harte Problem“.", "If such a being is conceivable, consciousness seems to be more than structure and behaviour: the \"hard problem\"."),
        listOf(t("Dass Untote in Filmen unrealistisch sind.", "That the undead in films are unrealistic."), t("Dass Menschen im Schlaf nichts erleben.", "That people experience nothing while asleep."), t("Dass Verhalten immer Gefühle verrät.", "That behaviour always reveals feelings.")),
        t("Chalmers machte den Zombie in den 1990ern berühmt. Viele Physikalisten bestreiten, dass ein Zombie wirklich denkbar ist: Wer alles Physikalische hat, hat auch das Erleben.", "Chalmers made the zombie famous in the 1990s. Many physicalists deny that a zombie is really conceivable: whoever has everything physical has the experience too.")),
    PhExperiment("maschine", M, Topic.PhilEthics, t("Erfahrungsmaschine", "Experience machine"), t("Robert Nozick", "Robert Nozick"),
        t("Eine Maschine könnte dir für den Rest deines Lebens perfekte, von echten nicht zu unterscheidende Erlebnisse schenken. Würdest du dich anschließen?", "A machine could give you perfect experiences, indistinguishable from real ones, for the rest of your life. Would you plug in?"),
        t("Dass uns mehr wichtig ist als angenehme Gefühle, etwa wirklich etwas zu tun und wirklich jemand zu sein.", "That we care about more than pleasant feelings, such as really doing things and really being someone."),
        listOf(t("Dass Technik immer gefährlich ist.", "That technology is always dangerous."), t("Dass Glück nur aus Erlebnissen besteht.", "That happiness consists only of experiences."), t("Dass Träume wichtiger sind als die Wirklichkeit.", "That dreams are more important than reality.")),
        t("Nozick stellte die Frage 1974 als Einwand gegen den Hedonismus. Die meisten Menschen lehnen ab, obwohl die Maschine ihnen mehr Freude versprechen würde.", "Nozick posed the question in 1974 as an objection to hedonism. Most people decline, even though the machine would promise them more pleasure.")),
    PhExperiment("bruecke", H, Topic.PhilEthics, t("Mann auf der Brücke", "Man on the footbridge"), t("Judith Jarvis Thomson", "Judith Jarvis Thomson"),
        t("Ein Wagen rast auf fünf Menschen zu. Du könntest ihn nur aufhalten, indem du einen schweren Mann von einer Brücke auf die Gleise stößt.", "A trolley is racing towards five people. You could only stop it by pushing a heavy man off a bridge onto the track."),
        t("Viele finden es schlimmer, selbst zu töten, als sterben zu lassen, auch bei gleichem Ergebnis.", "Many find it worse to kill yourself than to let die, even with the same outcome."),
        listOf(t("Dass schwere Menschen Züge stoppen können.", "That heavy people can stop trains."), t("Dass Brücken sicherer sein müssen.", "That bridges need to be safer."), t("Dass die Mehrheit immer recht hat.", "That the majority is always right.")),
        t("Im ursprünglichen Weichen-Fall (Philippa Foot, 1967) würden die meisten umstellen: einer statt fünf. Hier, mit denselben Zahlen, sagen die meisten nein. Warum, darüber streitet die Ethik bis heute.", "In the original switch case (Philippa Foot, 1967) most people would pull the lever: one instead of five. Here, with the same numbers, most say no. Why that is, ethics still argues about.")),
    PhExperiment("monster", X, Topic.PhilEthics, t("Nutzenmonster", "Utility monster"), t("Robert Nozick", "Robert Nozick"),
        t("Ein Wesen hat an allem tausendmal mehr Freude als alle anderen. Rechnet man nur das gesamte Glück zusammen, müsste es alles bekommen.", "A being enjoys everything a thousand times more than everyone else. If you only add up total happiness, it should get everything."),
        t("Dass es problematisch ist, Glück einfach zu summieren, ohne auf die Verteilung zu achten.", "That it's problematic to simply add up happiness without paying attention to its distribution."),
        listOf(t("Dass Monster nicht glücklich sein können.", "That monsters can't be happy."), t("Dass Freude nicht messbar ist.", "That joy can't be measured."), t("Dass Egoismus immer schlecht ist.", "That egoism is always bad.")),
        t("Ein Einwand gegen den einfachen Utilitarismus. Rawls kritisierte ähnlich: Der Utilitarismus nehme die Verschiedenheit der Personen nicht ernst.", "An objection to simple utilitarianism. Rawls made a similar criticism: utilitarianism doesn't take the separateness of persons seriously.")),
    PhExperiment("teleporter", H, Topic.PhilMind, t("Teleporter", "Teleporter"), t("Derek Parfit", "Derek Parfit"),
        t("Ein Gerät zerlegt dich auf der Erde, sendet deinen Bauplan zum Mars und baut dich dort aus neuem Material wieder auf.", "A device takes you apart on Earth, sends your blueprint to Mars and rebuilds you there from new material."),
        t("Was persönliche Identität ausmacht: Ist der Mensch auf dem Mars noch du?", "What personal identity consists in: is the person on Mars still you?"),
        listOf(t("Ob Reisen zum Mars möglich sind.", "Whether travel to Mars is possible."), t("Wie viel Energie ein Mensch enthält.", "How much energy a human contains."), t("Dass Kopien immer schlechter sind.", "That copies are always worse.")),
        t("In „Gründe und Personen“ (1984) verschärft Parfit den Fall: Was, wenn das Original auf der Erde heil bleibt? Dann gäbe es zwei. Seine Antwort: Wichtig ist der Fortbestand von Erinnerung und Charakter, nicht die Identität selbst.", "In Reasons and Persons (1984) Parfit sharpens the case: what if the original on Earth stays intact? Then there would be two. His answer: what matters is the continuation of memory and character, not identity itself.")),
    PhExperiment("zwillingserde", X, Topic.PhilKnowledge, t("Zwillingserde", "Twin Earth"), t("Hilary Putnam", "Hilary Putnam"),
        t("Auf einer Zwillingserde ist alles wie hier, nur ist das, was dort „Wasser“ heißt, nicht H₂O, sondern ein anderer Stoff, der genauso aussieht und schmeckt.", "On a Twin Earth everything is as here, except that what is called \"water\" there isn't H₂O but a different substance that looks and tastes the same."),
        t("Dass die Bedeutung eines Wortes nicht nur im Kopf liegt, sondern auch von der Umwelt abhängt.", "That the meaning of a word isn't only in the head but also depends on the environment."),
        listOf(t("Dass es irgendwo eine zweite Erde gibt.", "That there is a second Earth somewhere."), t("Dass Wasser auf allen Planeten gleich ist.", "That water is the same on every planet."), t("Dass Chemie wichtiger ist als Sprache.", "That chemistry is more important than language.")),
        t("Putnam (1975): Zwei Menschen können exakt denselben Kopfinhalt haben und mit „Wasser“ doch Verschiedenes meinen. Sein Fazit: „Bedeutungen sind einfach nicht im Kopf.“", "Putnam (1975): two people can have exactly the same mental contents and still mean different things by \"water\". His conclusion: \"Meanings just ain't in the head.\"")),
    PhExperiment("newcomb", X, Topic.PhilKnowledge, t("Newcombs Problem", "Newcomb's problem"), null,
        t("Box A enthält sichtbar 1000 €. Box B enthält 1 Million oder nichts, je nachdem, ob ein fast unfehlbarer Vorhersager vorhergesagt hat, dass du nur Box B nimmst.", "Box A visibly contains €1000. Box B contains a million or nothing, depending on whether an almost infallible predictor predicted that you'd take only box B."),
        t("Dass zwei vernünftige Entscheidungsregeln (Dominanz und Erwartungswert) sich widersprechen können.", "That two reasonable decision rules (dominance and expected value) can contradict each other."),
        listOf(t("Dass Geld nicht glücklich macht.", "That money doesn't make you happy."), t("Dass Vorhersagen immer falsch sind.", "That predictions are always wrong."), t("Dass man nie beide Boxen nehmen darf.", "That you're never allowed to take both boxes.")),
        t("Erdacht vom Physiker William Newcomb, bekannt gemacht 1969 von Robert Nozick. Philosophen sind bis heute gespalten; in Umfragen hat keines der beiden Lager eine klare Mehrheit.", "Devised by the physicist William Newcomb, made famous in 1969 by Robert Nozick. Philosophers are still divided; in surveys neither camp has a clear majority.")),
    PhExperiment("wette", M, Topic.PhilThinkers, t("Pascals Wette", "Pascal's wager"), null,
        t("Glaubst du an Gott und er existiert, gewinnst du unendlich viel; existiert er nicht, verlierst du wenig. Glaubst du nicht, riskierst du alles.", "If you believe in God and he exists, you gain infinitely much; if he doesn't, you lose little. If you don't believe, you risk everything."),
        t("Eine Entscheidung unter Unsicherheit mit dem Erwartungswert zu begründen.", "To justify a decision under uncertainty using expected value."),
        listOf(t("Dass sich Gottes Existenz beweisen lässt.", "That God's existence can be proven."), t("Dass Glücksspiel verboten sein sollte.", "That gambling should be banned."), t("Dass Glaube reine Gefühlssache ist.", "That faith is purely a matter of feeling.")),
        t("Pascal, Mitbegründer der Wahrscheinlichkeitsrechnung, beweist nicht, dass Gott existiert; er argumentiert, dass es klug sei, zu glauben. Einwand: Welchen der vielen Götter soll man wählen?", "Pascal, a co-founder of probability theory, doesn't prove that God exists; he argues that it's wise to believe. Objection: which of the many gods should one choose?")),
    PhExperiment("luegner", M, Topic.PhilArgument, t("Lügner-Paradox", "Liar paradox"), t("Eubulides", "Eubulides"),
        t("„Dieser Satz ist falsch.“ Ist er wahr, ist er falsch. Ist er falsch, ist er wahr.", "\"This sentence is false.\" If it's true, it's false. If it's false, it's true."),
        t("Dass Sätze, die sich auf sich selbst beziehen, Widersprüche erzeugen können.", "That sentences referring to themselves can produce contradictions."),
        listOf(t("Dass Lügen immer auffliegen.", "That lies are always found out."), t("Dass jeder Satz entweder wahr oder falsch sein muss.", "That every sentence must be either true or false."), t("Dass die Logik nutzlos ist.", "That logic is useless.")),
        t("Das Paradox stammt aus dem 4. Jahrhundert v. Chr. Im 20. Jahrhundert half es Kurt Gödel, zu zeigen, dass es in der Mathematik wahre Aussagen gibt, die sich nicht beweisen lassen.", "The paradox dates from the 4th century BC. In the 20th century it helped Kurt Gödel show that there are true statements in mathematics that cannot be proven.")),
    PhExperiment("wiederkehr", H, Topic.PhilThinkers, t("Ewige Wiederkunft", "Eternal recurrence"), t("Friedrich Nietzsche", "Friedrich Nietzsche"),
        t("Ein Dämon sagt dir: Du wirst dein Leben unendlich oft genau so noch einmal leben, mit jedem Schmerz und jeder Freude.", "A demon tells you: you will live your life again infinitely often, exactly as it is, with every pain and every joy."),
        t("Ein Prüfstein: Könntest du dein Leben so bejahen, dass du diese Wiederholung wollen würdest?", "A touchstone: could you affirm your life so fully that you would want this repetition?"),
        listOf(t("Dass die Zeit physikalisch im Kreis läuft.", "That time physically runs in a circle."), t("Dass Dämonen die Wahrheit sagen.", "That demons tell the truth."), t("Dass man Fehler nicht wiederholen sollte.", "That you shouldn't repeat mistakes.")),
        t("Aus „Die fröhliche Wissenschaft“ (1882). Nietzsche ging es weniger um Kosmologie als um eine Haltung: so zu leben, dass man jeden Augenblick wieder wollen könnte.", "From The Gay Science (1882). Nietzsche was less concerned with cosmology than with an attitude: living so that you could want every moment again.")),
    PhExperiment("barbier", H, Topic.PhilArgument, t("Barbier-Paradox", "Barber paradox"), t("Bertrand Russell", "Bertrand Russell"),
        t("Im Dorf rasiert der Barbier genau alle Männer, die sich nicht selbst rasieren. Rasiert er sich selbst?", "In the village the barber shaves exactly those men who don't shave themselves. Does he shave himself?"),
        t("Dass manche Beschreibungen widersprüchlich sind; so einen Barbier kann es nicht geben.", "That some descriptions are contradictory; such a barber cannot exist."),
        listOf(t("Dass Barbiere sich nie selbst rasieren.", "That barbers never shave themselves."), t("Dass jedes Dorf einen Barbier braucht.", "That every village needs a barber."), t("Dass Rasieren eine Pflicht ist.", "That shaving is a duty.")),
        t("Russell nutzte es zur Veranschaulichung seiner Antinomie (1901): die Menge aller Mengen, die sich nicht selbst enthalten. Sie erschütterte die Grundlagen der Mathematik.", "Russell used it to illustrate his antinomy (1901): the set of all sets that don't contain themselves. It shook the foundations of mathematics.")),
)

/** Urheber der Gedankenexperimente plus Denker als Auswahl für falsche Antworten. */
private val AUTHORS: List<T> by lazy {
    (EXPERIMENTS.mapNotNull { it.author } + IDEAS.map { it.who }).distinctBy { it.de }
}

/** Tipp für die Frage nach dem Urheber: Zeit und Herkunft, nicht der Name. */
private val EXP_HINTS: Map<String, T> = mapOf(
    "hoehle" to t("Antikes Athen, ein Schüler des Sokrates", "Ancient Athens, a student of Socrates"),
    "gyges" to t("Steht in der „Politeia“, wie das Höhlengleichnis", "It's in the Republic, like the allegory of the cave"),
    "zimmer" to t("Ein US-amerikanischer Philosoph, 1980", "An American philosopher, 1980"),
    "mary" to t("Ein australischer Philosoph, 1982", "An Australian philosopher, 1982"),
    "maschine" to t("Ein US-Philosoph, Buch „Anarchie, Staat, Utopia“ (1974)", "An American philosopher, book Anarchy, State, and Utopia (1974)"),
    "bruecke" to t("Eine US-amerikanische Philosophin, 1976", "An American philosopher, 1976"),
    "monster" to t("Derselbe Philosoph wie die Erfahrungsmaschine", "The same philosopher as the experience machine"),
    "teleporter" to t("Ein Oxforder Philosoph, Buch „Gründe und Personen“ (1984)", "An Oxford philosopher, book Reasons and Persons (1984)"),
    "zwillingserde" to t("Ein Harvard-Philosoph, 1975", "A Harvard philosopher, 1975"),
    "luegner" to t("Ein griechischer Logiker aus Milet, 4. Jahrhundert v. Chr.", "A Greek logician from Miletus, 4th century BC"),
    "wiederkehr" to t("Ein deutscher Philosoph, „Die fröhliche Wissenschaft“ (1882)", "A German philosopher, The Gay Science (1882)"),
    "barbier" to t("Ein britischer Logiker, der später den Literaturnobelpreis bekam", "A British logician who later won the Nobel Prize in Literature"),
)

private fun experimentDefs(): List<TaskDef> = EXPERIMENTS.map { x ->
    lab("ph.exp.${x.key}", x.topic, x.level) {
        val a = x.author
        if (a != null && chance(0.35)) {
            quiz(
                "x", x.topic, x.level,
                question = x.setup.s,
                prompt = tr("Von wem stammt das Gedankenexperiment „${x.name.s}“?", "Who came up with the thought experiment \"${x.name.s}\"?"),
                correct = a.s,
                wrong = draw(AUTHORS, 3) { it.de != a.de }.map { it.s },
                solution = tr("„${x.name.s}“ stammt von ${a.s}. Es zeigt: ${x.shows.s}", "\"${x.name.s}\" comes from ${a.s}. It shows: ${x.shows.s}"),
                background = x.why.s,
                hint = EXP_HINTS[x.key]?.s ?: x.shows.s,
            )
        } else {
            quiz(
                "x", x.topic, x.level,
                question = x.setup.s,
                prompt = tr("Worum geht es bei „${x.name.s}“ im Kern?", "What is \"${x.name.s}\" really about?"),
                correct = x.shows.s,
                wrong = x.wrong.map { it.s },
                solution = x.shows.s,
                background = x.why.s,
                hint = tr("Frag dich: Welche große Frage steckt hinter der kleinen Geschichte?", "Ask yourself: what big question is behind the little story?"),
            )
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Selbst denken: Argumente prüfen, rechnen, unterscheiden (geschriebene Aufgaben in mehreren Schritten)
// ---------------------------------------------------------------------------------------------

private fun written(): List<TaskDef> = listOf(
    TaskDef("ph.gueltig", Topic.PhilArgument, M) {
        Task(
            "ph.gueltig", Topic.PhilArgument, M,
            question = l("„Alle Fische können fliegen. Ein Hai ist ein Fisch. Also kann ein Hai fliegen.“", "\"All fish can fly. A shark is a fish. So a shark can fly.\""),
            steps = listOf(
                choice(
                    l("Ist das Argument gültig, folgt also der Schluss aus den Prämissen?", "Is the argument valid, i.e. does the conclusion follow from the premises?"),
                    listOf(l("Ja, die Form stimmt", "Yes, the form is correct"), l("Nein, weil Haie nicht fliegen", "No, because sharks don't fly"), l("Nein, weil Haie keine Fische sind", "No, because sharks aren't fish")),
                    done = l("Gültig", "Valid"),
                    hints = listOf(l("Tu so, als wären die Prämissen wahr. Müsste der Schluss dann stimmen?", "Pretend the premises were true. Would the conclusion then have to hold?"), l("Gültigkeit betrifft nur die Form, nicht ob die Sätze wirklich stimmen.", "Validity concerns only the form, not whether the sentences are actually true.")),
                ),
                choice(
                    l("Ist es auch schlüssig (gültig und mit wahren Prämissen)?", "Is it also sound (valid with true premises)?"),
                    listOf(l("Nein, die erste Prämisse ist falsch", "No, the first premise is false"), l("Ja, weil es gültig ist", "Yes, because it is valid"), l("Ja, weil der Schluss überrascht", "Yes, because the conclusion is surprising")),
                    done = l("Nicht schlüssig", "Not sound"),
                    hints = listOf(l("Stimmt „Alle Fische können fliegen“?", "Is \"All fish can fly\" true?")),
                ),
            ),
            solution = listOf(
                l("Gültig: Wären beide Prämissen wahr, müsste auch der Schluss wahr sein. Die Form „Alle A sind B, x ist A, also ist x B“ ist immer gültig.", "Valid: if both premises were true, the conclusion would have to be true. The form \"All A are B, x is A, so x is B\" is always valid."),
                l("Nicht schlüssig: Die erste Prämisse ist falsch. Nur gültige Argumente mit wahren Prämissen garantieren einen wahren Schluss.", "Not sound: the first premise is false. Only valid arguments with true premises guarantee a true conclusion."),
            ),
            background = l(
                "Diese Unterscheidung geht auf Aristoteles zurück und ist das wichtigste Werkzeug der Logik: Erst prüft man die Form (gültig?), dann den Inhalt (sind die Prämissen wahr?). Viele Streits lösen sich, wenn man merkt, dass man sich nur über eine Prämisse uneinig ist.",
                "This distinction goes back to Aristotle and is logic's most important tool: first check the form (valid?), then the content (are the premises true?). Many arguments dissolve once you notice you only disagree about one premise.",
            ),
        )
    },
    TaskDef("ph.modus", Topic.PhilArgument, M) {
        Task(
            "ph.modus", Topic.PhilArgument, M,
            question = l("„Wer in Berlin wohnt, wohnt in Deutschland.“ Dieser Satz stimmt. Was folgt daraus, und was nicht?", "\"Whoever lives in Paris lives in France.\" This sentence is true. What follows from it, and what doesn't?"),
            steps = listOf(
                choice(
                    l("Anna wohnt nicht in Deutschland. Was folgt sicher?", "Anna does not live in France. What follows for sure?"),
                    listOf(l("Sie wohnt nicht in Berlin", "She doesn't live in Paris"), l("Sie wohnt in Berlin", "She lives in Paris"), l("Daraus folgt nichts", "Nothing follows from it")),
                    done = l("Nicht in Berlin (Modus tollens)", "Not in Paris (modus tollens)"),
                    hints = listOf(l("Würde sie in Berlin wohnen, dann wohnte sie in Deutschland. Tut sie aber nicht …", "If she lived in Paris, she would live in France. But she doesn't …")),
                ),
                choice(
                    l("Ben wohnt in Deutschland. Folgt daraus, dass er in Berlin wohnt?", "Ben lives in France. Does it follow that he lives in Paris?"),
                    listOf(l("Nein, er könnte auch in Hamburg wohnen", "No, he could live in Lyon just as well"), l("Ja, das ist dieselbe Regel", "Yes, it's the same rule"), l("Ja, sonst wäre der Satz falsch", "Yes, otherwise the sentence would be false")),
                    done = l("Nein: Bejahung des Hinterglieds", "No: affirming the consequent"),
                    hints = listOf(l("Der Satz sagt nur etwas über Berliner, nicht über alle in Deutschland.", "The sentence only says something about Parisians, not about everyone in France.")),
                ),
                choice(
                    l("Clara wohnt nicht in Berlin. Folgt daraus, dass sie nicht in Deutschland wohnt?", "Clara does not live in Paris. Does it follow that she doesn't live in France?"),
                    listOf(l("Nein, sie könnte in München wohnen", "No, she could live in Marseille"), l("Ja, das ist der Modus tollens", "Yes, that's modus tollens"), l("Ja, wer nicht in Berlin wohnt, wohnt im Ausland", "Yes, whoever doesn't live in Paris lives abroad")),
                    done = l("Nein: Verneinung des Vorderglieds", "No: denying the antecedent"),
                    hints = listOf(l("Vergleiche genau mit dem ersten Fall: Was wird hier verneint, das „wenn“ oder das „dann“?", "Compare closely with the first case: what is denied here, the \"if\" or the \"then\"?")),
                ),
            ),
            solution = listOf(
                l("Modus tollens: Aus „wenn A, dann B“ und „nicht B“ folgt „nicht A“. Das ist gültig.", "Modus tollens: from \"if A then B\" and \"not B\" follows \"not A\". This is valid."),
                l("Von „B“ auf „A“ zu schließen, ist ein Fehlschluss (Bejahung des Hinterglieds): B kann auch ohne A gelten.", "Inferring \"A\" from \"B\" is a fallacy (affirming the consequent): B can hold without A."),
                l("Auch von „nicht A“ auf „nicht B“ zu schließen, ist ein Fehlschluss (Verneinung des Vorderglieds).", "Inferring \"not B\" from \"not A\" is a fallacy too (denying the antecedent)."),
            ),
            background = l(
                "Aus „wenn A, dann B“ folgen genau zwei gültige Schlüsse: mit A auf B (Modus ponens) und mit nicht-B auf nicht-A (Modus tollens). Die beiden anderen Richtungen sind die häufigsten Denkfehler überhaupt. Auch in der Wissenschaft: Eine eingetroffene Vorhersage beweist eine Theorie nicht, eine gescheiterte widerlegt sie. Darauf baute Karl Popper seine Wissenschaftstheorie.",
                "From \"if A then B\" exactly two valid inferences follow: from A to B (modus ponens) and from not-B to not-A (modus tollens). The other two directions are the most common reasoning errors of all. In science too: a prediction that comes true doesn't prove a theory, but a failed one refutes it. Karl Popper built his philosophy of science on this.",
            ),
        )
    },
    TaskDef("ph.basisrate", Topic.PhilKnowledge, H) {
        Task(
            "ph.basisrate", Topic.PhilKnowledge, H,
            question = l("Eine Krankheit hat 1 von 1000 Menschen. Ein Test erkennt 99 % der Kranken, schlägt aber auch bei 1 % der Gesunden fälschlich an. Dein Test ist positiv. Wie wahrscheinlich bist du krank?", "A disease affects 1 in 1000 people. A test detects 99 % of those who are ill but also wrongly flags 1 % of healthy people. Your test is positive. How likely are you to be ill?"),
            given = listOf(l("Stell dir 100.000 Menschen vor.", "Imagine 100,000 people.")),
            steps = listOf(
                num(
                    l("Wie viele sind krank und testen positiv?", "How many are ill and test positive?"), 99.0, "", abs = 0.5,
                    done = l("99 Kranke positiv", "99 ill and positive"),
                    hints = listOf(l("1 von 1000 sind 100 Kranke, davon erkennt der Test 99 %.", "1 in 1000 is 100 ill people, of whom the test detects 99 %.")),
                ),
                num(
                    l("Wie viele sind gesund und testen trotzdem positiv?", "How many are healthy and still test positive?"), 999.0, "", abs = 1.0,
                    done = l("999 Gesunde positiv", "999 healthy and positive"),
                    hints = listOf(l("99.900 sind gesund, 1 % davon.", "99,900 are healthy, 1 % of them.")),
                ),
                num(
                    l("Anteil der Kranken unter allen Positiven (in %)", "Share of ill people among all positives (in %)"), 99.0 / (99 + 999) * 100, "%", abs = 0.6,
                    done = l("nur etwa 9 %", "only about 9 %"),
                    hints = listOf(l("99 von 99 + 999 positiven Tests", "99 of 99 + 999 positive tests")),
                ),
            ),
            solution = listOf(
                l("100 von 100.000 sind krank, 99 davon testen positiv.", "100 of 100,000 are ill, 99 of them test positive."),
                l("99.900 sind gesund, 1 % davon, also 999, testen fälschlich positiv.", "99,900 are healthy; 1 % of them, i.e. 999, test positive by mistake."),
                l("99 / (99 + 999) ≈ 9 %. Trotz „99 % genau“ bist du mit über 90 % Wahrscheinlichkeit gesund.", "99 / (99 + 999) ≈ 9 %. Despite \"99 % accurate\" you are healthy with over 90 % probability."),
            ),
            background = l(
                "Das ist der Basisraten-Fehler: Wir übersehen, wie selten etwas überhaupt ist. Selbst Ärzte schätzen in Studien oft 90 % statt 9 %. Die saubere Rechnung dahinter ist der Satz von Bayes, ein Kernstück der Erkenntnistheorie: Wie sehr soll ein Hinweis meine Überzeugung ändern?",
                "This is the base-rate fallacy: we overlook how rare something is in the first place. In studies even doctors often guess 90 % instead of 9 %. The rigorous calculation behind it is Bayes' theorem, a core part of epistemology: how much should evidence change my belief?",
            ),
            fact = l("Deshalb macht man bei seltenen Krankheiten nach einem positiven Test meist einen zweiten, unabhängigen Test.", "That's why, for rare diseases, a positive test is usually followed by a second, independent test."),
        )
    },
    TaskDef("ph.kanttest", Topic.PhilEthics, H) {
        Task(
            "ph.kanttest", Topic.PhilEthics, H,
            question = l("Deine Regel: „Wenn ich Geld brauche, leihe ich es mir und verspreche, es zurückzuzahlen, obwohl ich weiß, dass ich es nicht kann.“", "Your rule: \"When I need money, I borrow it and promise to pay it back, even though I know I can't.\""),
            steps = listOf(
                choice(
                    l("Was wäre, wenn alle nach dieser Regel handelten?", "What would happen if everyone acted on this rule?"),
                    listOf(l("Niemand würde Versprechen mehr glauben", "Nobody would believe promises any more"), l("Alle hätten mehr Geld", "Everyone would have more money"), l("Banken würden mehr verleihen", "Banks would lend more")),
                    done = l("Versprechen verlören ihren Sinn", "Promises would lose their meaning"),
                    hints = listOf(l("Ein Versprechen funktioniert nur, wenn man ihm glauben kann.", "A promise only works if it can be believed.")),
                ),
                choice(
                    l("Was folgt daraus nach Kant?", "What follows according to Kant?"),
                    listOf(l("Die Regel hebt sich selbst auf und ist verboten", "The rule cancels itself out and is forbidden"), l("Die Regel ist erlaubt, wenn es niemand merkt", "The rule is allowed if nobody notices"), l("Es kommt auf die Höhe des Betrags an", "It depends on the amount")),
                    done = l("Verboten (nicht verallgemeinerbar)", "Forbidden (can't be universalised)"),
                    hints = listOf(l("Kann man wollen, dass alle so handeln, ohne dass die Regel sich selbst zerstört?", "Can you will that everyone acts like this without the rule destroying itself?")),
                ),
            ),
            solution = listOf(
                l("Als allgemeines Gesetz gäbe es keine glaubwürdigen Versprechen mehr, also könnte auch niemand mehr so Geld leihen.", "As a universal law there would be no credible promises any more, so nobody could borrow money this way."),
                l("Die Regel widerspricht sich selbst, sobald alle sie befolgen. Nach dem kategorischen Imperativ ist sie deshalb verboten, unabhängig von den Folgen im Einzelfall.", "The rule contradicts itself as soon as everyone follows it. According to the categorical imperative it is therefore forbidden, regardless of the consequences in the particular case."),
            ),
            background = l(
                "Genau dieses Beispiel steht in Kants „Grundlegung zur Metaphysik der Sitten“ (1785). Der Test fragt nicht „Was passiert, wenn ich das tue?“, sondern „Könnte das eine Regel für alle sein?“. So prüft man, ob man sich selbst eine Ausnahme gönnt, die man anderen nicht zugestehen würde.",
                "This very example appears in Kant's Groundwork of the Metaphysics of Morals (1785). The test doesn't ask \"What happens if I do this?\" but \"Could this be a rule for everyone?\". It checks whether you're granting yourself an exception you wouldn't grant others.",
            ),
        )
    },
    TaskDef("ph.nutzen", Topic.PhilEthics, M) {
        Task(
            "ph.nutzen", Topic.PhilEthics, M,
            question = l("Plan A: Drei Menschen gewinnen je 4 Glückspunkte, einer verliert 6. Plan B: Zwei Menschen gewinnen je 5 Punkte, sonst ändert sich nichts.", "Plan A: three people gain 4 happiness points each, one loses 6. Plan B: two people gain 5 points each, nothing else changes."),
            steps = listOf(
                num(l("Summe bei Plan A", "Total for plan A"), 6.0, "", abs = 0.0, done = l("A: 3 · 4 − 6 = 6", "A: 3 · 4 − 6 = 6"), hints = listOf(l("3 · 4, dann 6 abziehen", "3 · 4, then subtract 6"))),
                num(l("Summe bei Plan B", "Total for plan B"), 10.0, "", abs = 0.0, done = l("B: 2 · 5 = 10", "B: 2 · 5 = 10"), hints = listOf(l("2 · 5", "2 · 5"))),
                choice(
                    l("Welchen Plan wählt ein Utilitarist wie Bentham?", "Which plan does a utilitarian like Bentham choose?"),
                    listOf(l("Plan B", "Plan B"), l("Plan A", "Plan A"), l("Keinen von beiden", "Neither")),
                    done = l("Plan B (10 > 6)", "Plan B (10 > 6)"),
                    hints = listOf(l("Es zählt nur die Summe.", "Only the total counts.")),
                ),
            ),
            solution = listOf(
                l("A: 4 + 4 + 4 − 6 = 6 Punkte.", "A: 4 + 4 + 4 − 6 = 6 points."),
                l("B: 5 + 5 = 10 Punkte.", "B: 5 + 5 = 10 points."),
                l("B bringt mehr Gesamtglück und schadet niemandem; der Utilitarist wählt B.", "B brings more total happiness and harms nobody; the utilitarian chooses B."),
            ),
            background = l(
                "Bentham wollte Moral berechenbar machen („Glückskalkül“). Das ist klar und unparteiisch, wirft aber Fragen auf: Lässt sich Glück wirklich in Punkten messen? Und was, wenn die höchste Summe nur entsteht, indem einer schwer leidet? Darauf antworten Kant und Rawls ganz anders.",
                "Bentham wanted to make morality calculable (the \"felicific calculus\"). That is clear and impartial, but raises questions: can happiness really be measured in points? And what if the highest total only comes about by making one person suffer badly? Kant and Rawls answer this very differently.",
            ),
        )
    },
    TaskDef("ph.rawls", Topic.PhilPolitics, H) {
        Task(
            "ph.rawls", Topic.PhilPolitics, H,
            question = l("Drei Gesellschaften mit je drei gleich großen Gruppen (Einkommen pro Kopf): X: 10, 10, 10. Y: 2, 20, 40. Z: 6, 12, 18.", "Three societies, each with three equally large groups (income per head): X: 10, 10, 10. Y: 2, 20, 40. Z: 6, 12, 18."),
            steps = listOf(
                choice(
                    l("Welche hat den höchsten Durchschnitt?", "Which has the highest average?"),
                    listOf("Y", "X", "Z"),
                    done = l("Y (Durchschnitt ≈ 20,7)", "Y (average ≈ 20.7)"),
                    hints = listOf(l("Zusammenzählen und durch 3 teilen.", "Add up and divide by 3.")),
                ),
                choice(
                    l("Welche wählt man hinter Rawls' Schleier des Nichtwissens nach dem Maximin-Prinzip (die schlechteste Lage soll möglichst gut sein)?", "Which do you choose behind Rawls' veil of ignorance using the maximin principle (the worst position should be as good as possible)?"),
                    listOf("X", "Y", "Z"),
                    done = l("X (die Schwächsten haben 10)", "X (the worst off have 10)"),
                    hints = listOf(l("Schau nur auf die ärmste Gruppe jeder Gesellschaft.", "Look only at the poorest group in each society.")),
                ),
            ),
            solution = listOf(
                l("Durchschnitt: X = 10, Y = 62 / 3 ≈ 20,7, Z = 12. Y ist im Schnitt am reichsten.", "Average: X = 10, Y = 62 / 3 ≈ 20.7, Z = 12. Y is richest on average."),
                l("Schlechteste Lage: X = 10, Y = 2, Z = 6. Nach Maximin wählt man X.", "Worst position: X = 10, Y = 2, Z = 6. By maximin you choose X."),
            ),
            background = l(
                "Rawls meinte: Wer nicht weiß, in welcher Gruppe er landet, sichert sich gegen das Schlimmste ab. Ungleichheit ist nur erlaubt, wenn sie auch den Schwächsten nützt (Differenzprinzip). Der Ökonom John Harsanyi hielt dagegen: Unter Unsicherheit sei der höchste Durchschnitt die vernünftige Wahl. Hier wählt Harsanyi also Y.",
                "Rawls argued that someone who doesn't know which group they'll end up in protects themselves against the worst. Inequality is only allowed if it also benefits the worst off (the difference principle). The economist John Harsanyi objected: under uncertainty the highest average is the rational choice. Here Harsanyi would choose Y.",
            ),
        )
    },
    TaskDef("ph.gettierfall", Topic.PhilKnowledge, X, master = true) {
        Task(
            "ph.gettierfall", Topic.PhilKnowledge, X,
            question = l("Smith hat gute Gründe zu glauben, dass Jones den Job bekommt, und er hat gesehen, dass Jones zehn Münzen in der Tasche hat. Er schließt: „Wer den Job bekommt, hat zehn Münzen in der Tasche.“ Tatsächlich bekommt Smith selbst den Job, und zufällig hat auch er zehn Münzen in der Tasche.", "Smith has good reason to believe that Jones will get the job, and he has seen that Jones has ten coins in his pocket. He concludes: \"The person who gets the job has ten coins in his pocket.\" In fact Smith himself gets the job, and by chance he too has ten coins in his pocket."),
            steps = listOf(
                choice(l("Ist Smiths Überzeugung wahr?", "Is Smith's belief true?"), listOf(l("Ja", "Yes"), l("Nein", "No")), done = l("Wahr", "True"), hints = listOf(l("Wer bekommt den Job, und was hat er in der Tasche?", "Who gets the job, and what's in his pocket?"))),
                choice(l("Ist sie gerechtfertigt (gut begründet)?", "Is it justified (well founded)?"), listOf(l("Ja", "Yes"), l("Nein", "No")), done = l("Gerechtfertigt", "Justified"), hints = listOf(l("Smith hatte gute Gründe, nur bezogen auf Jones.", "Smith had good reasons, just about Jones."))),
                choice(
                    l("Weiß Smith also, dass der, der den Job bekommt, zehn Münzen hat?", "So does Smith know that the person who gets the job has ten coins?"),
                    listOf(l("Nein, sie stimmt nur durch Zufall", "No, it's only true by luck"), l("Ja, wahr und gerechtfertigt reicht", "Yes, true and justified is enough"), l("Ja, weil er Münzen gezählt hat", "Yes, because he counted coins")),
                    done = l("Kein Wissen (Gettier-Fall)", "No knowledge (Gettier case)"),
                    hints = listOf(l("Seine Gründe betrafen Jones, wahr wird der Satz aber durch Smith.", "His reasons were about Jones, but the sentence is made true by Smith.")),
                ),
            ),
            solution = listOf(
                l("Wahr: Der Jobgewinner (Smith) hat tatsächlich zehn Münzen.", "True: the winner of the job (Smith) really has ten coins."),
                l("Gerechtfertigt: Smith hatte gute Belege, allerdings über Jones.", "Justified: Smith had good evidence, though about Jones."),
                l("Trotzdem kein Wissen: Die Rechtfertigung und das, was den Satz wahr macht, haben nichts miteinander zu tun. Wahr ist er nur durch Glück.", "Still no knowledge: the justification and what makes the sentence true have nothing to do with each other. It's only true by luck."),
            ),
            background = l(
                "Seit Platon galt Wissen als „wahre, gerechtfertigte Meinung“. Edmund Gettier zeigte 1963 auf drei Seiten mit Fällen wie diesem, dass das nicht reicht. Vorschläge für die fehlende Bedingung: Die Begründung darf nicht auf etwas Falschem beruhen, oder die Meinung muss zuverlässig entstanden sein. Eine allgemein akzeptierte Lösung gibt es bis heute nicht.",
                "Since Plato knowledge was considered \"justified true belief\". In 1963 Edmund Gettier showed on three pages, with cases like this one, that this isn't enough. Proposals for the missing condition: the justification must not rest on anything false, or the belief must have come about reliably. To this day there is no generally accepted solution.",
            ),
            master = true,
        )
    },
    TaskDef("ph.seinsollen", Topic.PhilArgument, H) {
        Task(
            "ph.seinsollen", Topic.PhilArgument, H,
            question = l("„Menschen streben von Natur aus nach Anerkennung. Also sollten sie nach Anerkennung streben.“", "\"People naturally strive for recognition. So they ought to strive for recognition.\""),
            steps = listOf(
                choice(
                    l("Was ist der Fehler?", "What's the mistake?"),
                    listOf(l("Aus einem „ist“ wird ohne Begründung ein „soll“", "An \"ought\" is drawn from an \"is\" without justification"), l("Die Prämisse ist sicher falsch", "The premise is certainly false"), l("Es gibt keinen Fehler", "There's no mistake")),
                    done = l("Sein-Sollen-Fehlschluss", "Is-ought fallacy"),
                    hints = listOf(l("Achte auf den Sprung von der Beschreibung zur Forderung.", "Watch the jump from description to demand.")),
                ),
                choice(
                    l("Welche zusätzliche Prämisse würde das Argument gültig machen?", "Which additional premise would make the argument valid?"),
                    listOf(l("Was Menschen von Natur aus anstreben, sollen sie anstreben", "What people naturally strive for, they ought to strive for"), l("Anerkennung macht glücklich", "Recognition makes people happy"), l("Viele Menschen wollen Anerkennung", "Many people want recognition")),
                    done = l("Eine Soll-Prämisse", "An ought premise"),
                    hints = listOf(l("Im Schluss steht „soll“, also braucht es auch in den Prämissen ein „soll“.", "The conclusion contains \"ought\", so the premises need an \"ought\" too.")),
                ),
            ),
            solution = listOf(
                l("Der Schluss enthält ein Sollen, die Prämisse nur ein Sein. Dieser Sprung ist nicht gültig (Hume).", "The conclusion contains an ought, the premise only an is. This jump isn't valid (Hume)."),
                l("Mit der Prämisse „Was Menschen von Natur aus anstreben, sollen sie anstreben“ wird es gültig, aber genau diese Prämisse ist sehr fragwürdig (auch Gewalt kommt „natürlich“ vor).", "With the premise \"What people naturally strive for, they ought to strive for\" it becomes valid, but exactly that premise is very questionable (violence also occurs \"naturally\")."),
            ),
            background = l(
                "David Hume bemerkte 1739, dass Autoren oft unbemerkt vom „ist“ zum „soll“ springen. Der Trick, die versteckte Prämisse auszuschreiben, hilft bei jeder Debatte: Plötzlich sieht man, worüber man wirklich streitet.",
                "David Hume noticed in 1739 that authors often jump unnoticed from \"is\" to \"ought\". The trick of writing out the hidden premise helps in any debate: suddenly you see what you're really arguing about.",
            ),
        )
    },
    TaskDef("ph.urteile", Topic.PhilKnowledge, H, master = false) {
        Task(
            "ph.urteile", Topic.PhilKnowledge, H,
            question = l("Kant teilt Urteile ein: analytisch (wahr durch die Bedeutung der Wörter) oder synthetisch (erweitert das Wissen); a priori (ohne Erfahrung) oder a posteriori (aus Erfahrung).", "Kant divides judgements into analytic (true by the meaning of the words) or synthetic (extends knowledge); a priori (without experience) or a posteriori (from experience)."),
            steps = listOf(
                choice(l("„Alle Kreise sind rund.“", "\"All circles are round.\""), listOf(l("analytisch a priori", "analytic a priori"), l("synthetisch a posteriori", "synthetic a posteriori"), l("synthetisch a priori", "synthetic a priori")), done = l("Kreise: analytisch a priori", "Circles: analytic a priori"), hints = listOf(l("Steckt „rund“ schon im Begriff Kreis?", "Is \"round\" already in the concept of a circle?"))),
                choice(l("„Wasser kocht auf Meereshöhe bei 100 °C.“", "\"Water boils at 100 °C at sea level.\""), listOf(l("synthetisch a posteriori", "synthetic a posteriori"), l("analytisch a priori", "analytic a priori"), l("synthetisch a priori", "synthetic a priori")), done = l("Wasser: synthetisch a posteriori", "Water: synthetic a posteriori"), hints = listOf(l("Woher weißt du das: aus dem Wort oder aus Messungen?", "How do you know: from the word or from measurements?"))),
                choice(l("„7 + 5 = 12“, so wie Kant es sah.", "\"7 + 5 = 12\", as Kant saw it."), listOf(l("synthetisch a priori", "synthetic a priori"), l("analytisch a priori", "analytic a priori"), l("synthetisch a posteriori", "synthetic a posteriori")), done = l("7 + 5 = 12: synthetisch a priori (Kant)", "7 + 5 = 12: synthetic a priori (Kant)"), hints = listOf(l("Steckt die 12 schon im Begriff „7 + 5“? Kant meinte: nein, man muss zählen.", "Is 12 already contained in the concept \"7 + 5\"? Kant said no, you have to count."), l("Und braucht man Erfahrung, um sicher zu sein, dass es stimmt?", "And do you need experience to be sure it's true?"))),
            ),
            solution = listOf(
                l("„Rund“ ist Teil der Definition von Kreis: analytisch, und man braucht keine Erfahrung.", "\"Round\" is part of the definition of a circle: analytic, and no experience is needed."),
                l("Den Siedepunkt kennt man nur aus Messungen, und er steckt nicht im Wort Wasser: synthetisch a posteriori.", "You only know the boiling point from measurements, and it isn't in the word water: synthetic a posteriori."),
                l("Kant: Die Summe ist nicht im Begriff enthalten (synthetisch), gilt aber notwendig und ohne Erfahrung (a priori).", "Kant: the sum isn't contained in the concept (synthetic) but holds necessarily and without experience (a priori)."),
            ),
            background = l(
                "Kants große Frage in der „Kritik der reinen Vernunft“ (1781) war, wie synthetische Urteile a priori möglich sind. Seine Antwort: Raum, Zeit und Kategorien wie Ursache stammen aus unserem eigenen Erkenntnisapparat, deshalb gelten sie für jede mögliche Erfahrung. Ob Mathematik wirklich synthetisch ist, ist bis heute umstritten.",
                "Kant's big question in the Critique of Pure Reason (1781) was how synthetic a priori judgements are possible. His answer: space, time and categories such as cause come from our own cognitive apparatus, which is why they hold for every possible experience. Whether mathematics is really synthetic is still debated.",
            ),
        )
    },
    TaskDef("ph.fehler3", Topic.PhilArgument, M) {
        Task(
            "ph.fehler3", Topic.PhilArgument, M,
            question = l("Drei kurze Wortwechsel. Welcher Denkfehler steckt jeweils darin?", "Three short exchanges. Which reasoning error is in each?"),
            steps = listOf(
                choice(l("„Sein Vorschlag zum Klimaschutz taugt nichts, er fährt ja selbst ein dickes Auto.“", "\"His climate proposal is worthless, he drives a big car himself.\""), listOf(l("Angriff auf die Person (ad hominem)", "Attacking the person (ad hominem)"), l("Strohmann", "Straw man"), l("Falsches Dilemma", "False dilemma")), done = l("Ad hominem", "Ad hominem"), hints = listOf(l("Wird das Argument geprüft oder die Person?", "Is the argument examined or the person?"))),
                choice(l("A: „Wir sollten weniger Fleisch essen.“ B: „Du willst also allen das Essen verbieten?“", "A: \"We should eat less meat.\" B: \"So you want to ban everyone's food?\""), listOf(l("Strohmann", "Straw man"), l("Angriff auf die Person (ad hominem)", "Attacking the person (ad hominem)"), l("Zirkelschluss", "Circular reasoning")), done = l("Strohmann", "Straw man"), hints = listOf(l("Hat A das wirklich gesagt?", "Did A really say that?"))),
                choice(l("„Entweder du bist für uns, oder du bist gegen uns.“", "\"Either you're with us or you're against us.\""), listOf(l("Falsches Dilemma", "False dilemma"), l("Strohmann", "Straw man"), l("Angriff auf die Person (ad hominem)", "Attacking the person (ad hominem)")), done = l("Falsches Dilemma", "False dilemma"), hints = listOf(l("Gibt es wirklich nur zwei Möglichkeiten?", "Are there really only two options?"))),
            ),
            solution = listOf(
                l("Ad hominem: Ob jemand selbst konsequent ist, sagt nichts darüber, ob sein Argument stimmt.", "Ad hominem: whether someone is consistent themselves says nothing about whether their argument is right."),
                l("Strohmann: B widerlegt eine übertriebene Fassung, die A nie vertreten hat.", "Straw man: B refutes an exaggerated version A never held."),
                l("Falsches Dilemma: Man kann auch neutral sein oder teils zustimmen.", "False dilemma: you can also be neutral or partly agree."),
            ),
            background = l(
                "Solche Fehlschlüsse sammelte schon Aristoteles in den „Sophistischen Widerlegungen“. Sie wirken, weil sie sich wie Argumente anfühlen. Wer sie benennen kann, diskutiert ruhiger: Man kann sagen „Das ist ein Strohmann, ich meinte etwas anderes“, statt sich zu ärgern.",
                "Aristotle already collected such fallacies in the Sophistical Refutations. They work because they feel like arguments. Whoever can name them discusses more calmly: you can say \"That's a straw man, I meant something else\" instead of getting angry.",
            ),
        )
    },
    TaskDef("ph.zirkel", Topic.PhilArgument, E) {
        Task(
            "ph.zirkel", Topic.PhilArgument, E,
            question = l("„Dieses Lexikon ist absolut zuverlässig. Das steht nämlich in seinem eigenen Vorwort.“", "\"This encyclopaedia is completely reliable. It says so in its own preface.\""),
            steps = listOf(
                choice(
                    l("Was stimmt mit der Begründung nicht?", "What's wrong with the reasoning?"),
                    listOf(l("Sie setzt voraus, was sie beweisen will", "It assumes what it wants to prove"), l("Lexika sind nie zuverlässig", "Encyclopaedias are never reliable"), l("Vorwörter sind zu kurz", "Prefaces are too short")),
                    done = l("Zirkelschluss", "Circular reasoning"),
                    hints = listOf(l("Warum sollte man dem Vorwort glauben?", "Why should you believe the preface?")),
                ),
            ),
            solution = listOf(l("Dem Vorwort zu glauben setzt schon voraus, dass das Lexikon zuverlässig ist. Die Begründung dreht sich im Kreis.", "Believing the preface already assumes the encyclopaedia is reliable. The reasoning goes round in a circle.")),
            background = l(
                "Ein Zirkelschluss (lateinisch petitio principii) beweist nichts, auch wenn der Schluss zufällig stimmt. Gute Begründungen stützen sich auf etwas Unabhängiges, hier etwa auf Vergleiche mit anderen Quellen.",
                "Circular reasoning (Latin petitio principii) proves nothing, even if the conclusion happens to be true. Good reasons rely on something independent, here for example on comparisons with other sources.",
            ),
        )
    },
    TaskDef("ph.mehrheit", Topic.PhilArgument, E) {
        Task(
            "ph.mehrheit", Topic.PhilArgument, E,
            question = l("„Millionen Menschen nutzen dieses Wundermittel. Also muss es wirken.“", "\"Millions of people use this miracle cure. So it must work.\""),
            steps = listOf(
                choice(
                    l("Welcher Denkfehler ist das?", "Which reasoning error is this?"),
                    listOf(l("Berufung auf die Masse", "Appeal to popularity"), l("Strohmann", "Straw man"), l("Zirkelschluss", "Circular reasoning")),
                    done = l("Argumentum ad populum", "Argumentum ad populum"),
                    hints = listOf(l("Wird etwas wahr, weil viele es glauben?", "Does something become true because many believe it?")),
                ),
            ),
            solution = listOf(l("Wie viele etwas glauben oder kaufen, sagt nichts darüber, ob es stimmt. Ob ein Mittel wirkt, zeigen Studien mit Vergleichsgruppen.", "How many people believe or buy something says nothing about whether it's true. Whether a remedy works is shown by studies with control groups.")),
            background = l(
                "Jahrhundertelang glaubte fast jeder, die Sonne kreise um die Erde. Die Mehrheit kann irren. In der Demokratie entscheidet die Mehrheit trotzdem, was gilt, aber nicht, was wahr ist.",
                "For centuries almost everyone believed the sun circled the Earth. The majority can be wrong. In a democracy the majority still decides what applies, but not what is true.",
            ),
        )
    },
    TaskDef("ph.barbierlogik", Topic.PhilArgument, H) {
        Task(
            "ph.barbierlogik", Topic.PhilArgument, H,
            question = l("Der Barbier des Dorfes rasiert genau alle Männer des Dorfes, die sich nicht selbst rasieren. Er ist selbst ein Mann aus dem Dorf.", "The village barber shaves exactly those men of the village who don't shave themselves. He is himself a man from the village."),
            steps = listOf(
                choice(l("Angenommen, er rasiert sich selbst. Was folgt?", "Suppose he shaves himself. What follows?"), listOf(l("Dann darf er sich nicht rasieren", "Then he mustn't shave himself"), l("Alles passt", "Everything fits"), l("Dann rasiert er alle", "Then he shaves everyone")), done = l("Widerspruch", "Contradiction"), hints = listOf(l("Er rasiert nur die, die sich nicht selbst rasieren.", "He only shaves those who don't shave themselves."))),
                choice(l("Angenommen, er rasiert sich nicht. Was folgt?", "Suppose he doesn't shave himself. What follows?"), listOf(l("Dann muss er sich rasieren", "Then he must shave himself"), l("Alles passt", "Everything fits"), l("Dann rasiert ihn niemand", "Then nobody shaves him")), done = l("Wieder ein Widerspruch", "Contradiction again"), hints = listOf(l("Er rasiert alle, die sich nicht selbst rasieren, also auch …", "He shaves everyone who doesn't shave himself, so also …"))),
                choice(l("Was lernt man daraus?", "What do we learn from this?"), listOf(l("So einen Barbier kann es nicht geben", "Such a barber cannot exist"), l("Er hat einen Bart", "He has a beard"), l("Die Logik versagt", "Logic fails")), done = l("Die Beschreibung ist widersprüchlich", "The description is contradictory"), hints = listOf(l("Beide Möglichkeiten führen zum Widerspruch.", "Both possibilities lead to contradiction."))),
            ),
            solution = listOf(
                l("Rasiert er sich, gehört er zu denen, die sich selbst rasieren, und die rasiert er laut Regel nicht.", "If he shaves himself, he is one of those who shave themselves, and by the rule he doesn't shave them."),
                l("Rasiert er sich nicht, gehört er zu denen, die er laut Regel rasiert.", "If he doesn't shave himself, he is one of those he shaves by the rule."),
                l("Beides ist unmöglich, also gibt es keinen solchen Barbier. Nicht jede Beschreibung trifft auf etwas zu.", "Both are impossible, so there is no such barber. Not every description applies to something."),
            ),
            background = l(
                "Bertrand Russell veranschaulichte damit seine Antinomie: Die Menge aller Mengen, die sich nicht selbst enthalten, kann es ebenfalls nicht geben. Das zwang die Mathematik um 1900, ihre Grundlagen neu und vorsichtiger aufzubauen.",
                "Bertrand Russell used this to illustrate his antinomy: the set of all sets that don't contain themselves cannot exist either. Around 1900 this forced mathematics to rebuild its foundations more carefully.",
            ),
        )
    },
    TaskDef("ph.newcombrechnung", Topic.PhilKnowledge, X, master = true) {
        Task(
            "ph.newcombrechnung", Topic.PhilKnowledge, X,
            question = l("Box A enthält sichtbar 1000 €. Box B enthält 1.000.000 €, falls ein Vorhersager vorhergesagt hat, dass du nur B nimmst, sonst nichts. Er liegt in 99 % der Fälle richtig.", "Box A visibly contains €1000. Box B contains €1,000,000 if a predictor predicted you'd take only B, otherwise nothing. He is right 99 % of the time."),
            steps = listOf(
                num(l("Erwarteter Gewinn, wenn du nur Box B nimmst (in €)", "Expected gain if you take only box B (in €)"), 990_000.0, "€", rel = 0.001, done = l("990.000 €", "€990,000"), hints = listOf(l("Mit 99 % ist die Million drin.", "With 99 % the million is in it."))),
                num(l("Erwarteter Gewinn, wenn du beide Boxen nimmst (in €)", "Expected gain if you take both boxes (in €)"), 11_000.0, "€", rel = 0.001, done = l("11.000 €", "€11,000"), hints = listOf(l("Mit 1 % hat er sich geirrt und die Million liegt trotzdem drin, die 1000 € bekommst du sicher.", "With 1 % he was wrong and the million is there anyway; you get the €1000 for sure."))),
                choice(
                    l("Was sagt dagegen das Dominanz-Argument?", "What does the dominance argument say instead?"),
                    listOf(l("Nimm beide: Der Inhalt steht schon fest, A bringt immer 1000 € mehr", "Take both: the contents are already fixed, A always adds €1000"), l("Nimm nur B: der Erwartungswert ist höher", "Take only B: the expected value is higher"), l("Nimm keine Box", "Take neither box")),
                    done = l("Beide Boxen (Dominanz)", "Both boxes (dominance)"),
                    hints = listOf(l("Deine Wahl kann den schon gepackten Inhalt nicht mehr ändern.", "Your choice can no longer change contents that are already packed.")),
                ),
            ),
            solution = listOf(
                l("Nur B: 0,99 · 1.000.000 € = 990.000 €.", "Only B: 0.99 · €1,000,000 = €990,000."),
                l("Beide: 1000 € + 0,01 · 1.000.000 € = 11.000 €.", "Both: €1000 + 0.01 · €1,000,000 = €11,000."),
                l("Dominanz: Was auch immer in B liegt, mit A hast du 1000 € mehr. Beide Regeln klingen vernünftig und widersprechen sich.", "Dominance: whatever is in B, with A you have €1000 more. Both rules sound reasonable and contradict each other."),
            ),
            background = l(
                "Newcombs Problem trennt zwei Arten, über Entscheidungen nachzudenken: nach dem, was meine Wahl über die Welt verrät (evidentiell: nur B), oder nach dem, was meine Wahl ursächlich bewirkt (kausal: beide). In Umfragen unter Philosophen hat keines der beiden Lager eine klare Mehrheit.",
                "Newcomb's problem separates two ways of thinking about decisions: by what my choice reveals about the world (evidential: only B), or by what my choice causally brings about (causal: both). In surveys of philosophers neither camp has a clear majority.",
            ),
            master = true,
        )
    },
    TaskDef("ph.schaden", Topic.PhilPolitics, M) {
        Task(
            "ph.schaden", Topic.PhilPolitics, M,
            question = l("Mills Schadensprinzip: Der Staat darf die Freiheit nur beschränken, um Schaden von anderen abzuwenden.", "Mill's harm principle: the state may only restrict liberty to prevent harm to others."),
            steps = listOf(
                choice(l("Welches Verbot passt zu Mill?", "Which ban fits Mill?"), listOf(l("Betrunken Auto fahren", "Drunk driving"), l("Allein zu Hause ungesund essen", "Eating unhealthily alone at home"), l("Riskante Hobbys wie Klettern", "Risky hobbies such as climbing")), done = l("Betrunken fahren: gefährdet andere", "Drunk driving: endangers others"), hints = listOf(l("Wen gefährdet die Handlung?", "Whom does the action endanger?"))),
                choice(l("Und welches lehnt Mill ab?", "And which does Mill reject?"), listOf(l("Ein Verbot, im eigenen Garten zu rauchen", "A ban on smoking in your own garden"), l("Ein Verbot von Körperverletzung", "A ban on assault"), l("Ein Verbot, Abwasser in den Fluss zu leiten", "A ban on dumping sewage into the river")), done = l("Rauchen im Garten: schadet vor allem einem selbst", "Smoking in the garden: mainly harms oneself"), hints = listOf(l("Was betrifft nur einen selbst?", "What only affects oneself?"))),
            ),
            solution = listOf(
                l("Betrunken fahren gefährdet andere: Ein Verbot ist erlaubt.", "Drunk driving endangers others: a ban is allowed."),
                l("Im eigenen Garten zu rauchen schadet vor allem dir selbst: Der Staat darf warnen und überzeugen, aber nicht verbieten.", "Smoking in your own garden mainly harms yourself: the state may warn and persuade, but not forbid."),
            ),
            background = l(
                "Mill schrieb 1859 „Über die Freiheit“. Schwierig wird es bei Grenzfällen: Schadet Rauchen nicht doch anderen, etwa der Familie oder der Krankenkasse? Genau darüber streiten Liberale bis heute, etwa bei Helmpflicht oder Zuckersteuer.",
                "Mill wrote On Liberty in 1859. Borderline cases are hard: doesn't smoking harm others after all, such as one's family or the health insurance? Liberals still argue about exactly this today, for instance about helmet laws or sugar taxes.",
            ),
        )
    },
    TaskDef("ph.mitte", Topic.PhilEthics, M) {
        Task(
            "ph.mitte", Topic.PhilEthics, M,
            question = l("Nach Aristoteles liegt eine Tugend in der Mitte zwischen einem Zuviel und einem Zuwenig.", "According to Aristotle a virtue lies in the mean between too much and too little."),
            steps = listOf(
                choice(l("Zwischen Geiz und Verschwendung liegt …", "Between stinginess and extravagance lies …"), listOf(l("Großzügigkeit", "Generosity"), l("Sparsamkeit", "Thrift"), l("Reichtum", "Wealth")), done = l("Großzügigkeit", "Generosity"), hints = listOf(l("Gemeint ist der richtige Umgang mit Geld gegenüber anderen.", "It means the right way of dealing with money towards others."))),
                choice(l("Zwischen Angeberei und falscher Bescheidenheit liegt …", "Between boastfulness and false modesty lies …"), listOf(l("Wahrhaftigkeit", "Truthfulness"), l("Schüchternheit", "Shyness"), l("Selbstbewusstsein", "Self-confidence")), done = l("Wahrhaftigkeit", "Truthfulness"), hints = listOf(l("Es geht darum, sich weder größer noch kleiner zu machen, als man ist.", "It's about making yourself neither bigger nor smaller than you are."))),
            ),
            solution = listOf(
                l("Großzügig gibt, wer im richtigen Maß, zur richtigen Zeit und den Richtigen gibt.", "Generous is whoever gives in the right measure, at the right time and to the right people."),
                l("Wahrhaftig ist, wer sich so darstellt, wie er ist, ohne Angeberei und ohne Tiefstapelei.", "Truthful is whoever presents themselves as they are, without boasting and without understating."),
            ),
            background = l(
                "Die Mitte ist bei Aristoteles keine Kompromiss-Formel, sondern das Treffende: Mut liegt näher an der Tollkühnheit als an der Feigheit. Man erwirbt Tugenden wie ein Handwerk, durch Üben, bis das Richtige zur Gewohnheit wird.",
                "For Aristotle the mean isn't a formula for compromise but what hits the mark: courage lies closer to recklessness than to cowardice. You acquire virtues like a craft, through practice, until the right thing becomes a habit.",
            ),
        )
    },
    TaskDef("ph.turing", Topic.PhilMind, M) {
        Task(
            "ph.turing", Topic.PhilMind, M,
            question = l("Ein Chatprogramm führt so gute Gespräche, dass Testpersonen es nicht mehr von einem Menschen unterscheiden können.", "A chat program converses so well that test subjects can no longer tell it apart from a human."),
            steps = listOf(
                choice(l("Was hätte Alan Turing daraus geschlossen?", "What would Alan Turing have concluded?"), listOf(l("Wir haben guten Grund, ihm Denken zuzuschreiben", "We have good reason to attribute thinking to it"), l("Es ist ein Mensch", "It is a human"), l("Es fühlt Schmerzen", "It feels pain")), done = l("Es besteht den Turing-Test", "It passes the Turing test"), hints = listOf(l("Turing ersetzte „Kann es denken?“ durch eine prüfbare Frage.", "Turing replaced \"Can it think?\" with a testable question."))),
                choice(l("Was würde John Searle einwenden?", "What would John Searle object?"), listOf(l("Zeichen richtig zu verarbeiten ist noch kein Verstehen", "Processing symbols correctly isn't yet understanding"), l("Der Test dauert zu kurz", "The test is too short"), l("Computer sind zu langsam", "Computers are too slow")), done = l("Chinesisches Zimmer", "Chinese room"), hints = listOf(l("Denk an das Gedankenexperiment mit dem Regelbuch.", "Think of the thought experiment with the rule book."))),
            ),
            solution = listOf(
                l("Turing (1950): Wenn man im Gespräch keinen Unterschied merkt, gibt es keinen besseren Grund, der Maschine Denken abzusprechen als einem Menschen.", "Turing (1950): if you can't tell the difference in conversation, there's no better reason to deny the machine thinking than to deny it to a human."),
                l("Searle (1980): Das Programm folgt nur Regeln für Zeichen, ohne zu wissen, was sie bedeuten.", "Searle (1980): the program only follows rules for symbols without knowing what they mean."),
            ),
            background = l(
                "Mit heutigen Sprachmodellen ist diese Debatte sehr praktisch geworden. Sie fragt: Ist Verstehen etwas, das sich im Verhalten zeigt, oder braucht es mehr, etwa Erleben, Ziele oder einen Körper in der Welt?",
                "With today's language models this debate has become very practical. It asks: is understanding something that shows in behaviour, or does it take more, such as experience, goals or a body in the world?",
            ),
        )
    },
    TaskDef("ph.libet", Topic.PhilMind, H) {
        Task(
            "ph.libet", Topic.PhilMind, H,
            question = l("Im Libet-Experiment (1983) sollten Versuchspersonen irgendwann frei die Hand bewegen und sich merken, wann sie den Drang dazu spürten. Gleichzeitig wurde ihre Hirnaktivität gemessen.", "In the Libet experiment (1983) participants were to move their hand freely at some point and note when they felt the urge to do so. At the same time their brain activity was measured."),
            steps = listOf(
                choice(l("Was zeigte die Messung?", "What did the measurement show?"), listOf(l("Ein Bereitschaftspotenzial begann einige hundert Millisekunden vor dem bewussten Entschluss", "A readiness potential began a few hundred milliseconds before the conscious decision"), l("Das Gehirn war beim Entschluss völlig still", "The brain was completely silent at the moment of decision"), l("Die Hand bewegte sich vor jeder Hirnaktivität", "The hand moved before any brain activity")), done = l("Hirnaktivität vor dem bewussten Entschluss", "Brain activity before the conscious decision"), hints = listOf(l("Es ging um die Reihenfolge: Gehirn, Entschluss, Bewegung.", "It was about the order: brain, decision, movement."))),
                choice(l("Wie deutete Libet selbst das Ergebnis?", "How did Libet himself interpret the result?"), listOf(l("Das Bewusstsein kann die Handlung noch per Veto stoppen", "Consciousness can still stop the action by veto"), l("Freier Wille ist damit widerlegt", "Free will is thereby refuted"), l("Die Messung war fehlerhaft", "The measurement was faulty")), done = l("Ein Veto bleibt möglich", "A veto remains possible"), hints = listOf(l("Zwischen Entschluss und Bewegung blieben etwa 0,2 Sekunden.", "About 0.2 seconds remained between decision and movement."))),
            ),
            solution = listOf(
                l("Das Bereitschaftspotenzial begann bei spontanen Bewegungen etwa 0,35 Sekunden vor dem gemeldeten Drang; die Bewegung folgte rund 0,2 Sekunden nach dem Drang.", "For spontaneous movements the readiness potential began about 0.35 seconds before the reported urge; the movement followed about 0.2 seconds after the urge."),
                l("Libet meinte, das Bewusstsein könne die angestoßene Bewegung noch verhindern („free won't“).", "Libet thought consciousness could still prevent the triggered movement (\"free won't\")."),
            ),
            background = l(
                "Viele lasen das Experiment als Beweis gegen den freien Willen. Kritiker wenden ein: Spontanes Handbewegen ist keine echte Entscheidung, das Messen des „Drangs“ ist ungenau, und neuere Studien deuten das Potenzial eher als zufälliges Hintergrundrauschen, das eine Bewegung begünstigt.",
                "Many read the experiment as proof against free will. Critics object: spontaneously moving your hand isn't a real decision, measuring the \"urge\" is imprecise, and newer studies interpret the potential rather as random background noise that favours a movement.",
            ),
        )
    },
)

object LabPhilosophy {
    val all: List<TaskDef> by lazy { ideaDefs() + conceptDefs() + experimentDefs() + written() }
}
