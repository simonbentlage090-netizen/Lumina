package de.funke.app

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.createLifecycleAwareWindowRecomposer
import de.funke.app.ui.DeepLink
import de.funke.app.ui.FunkeApp
import de.funke.app.ui.Lang
import de.funke.app.ui.components.SplashHandOff

/**
 * Lumina läuft immer mit normaler Animationsgeschwindigkeit.
 *
 * Hintergrund: Ist im Handy "Animationen entfernen" oder in den Entwickleroptionen
 * "Animation aus" eingestellt, springen sonst alle Animationen sofort ans Ende.
 * Dann wären z. B. die Halte-Knöpfe sofort voll und der Dampf-Timer würde nicht verdampfen.
 */
private object NormalMotion : MotionDurationScale {
    override val scaleFactor: Float get() = 1f
}

class MainActivity : ComponentActivity() {
    @OptIn(InternalComposeUiApi::class, ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CrashLog.install(this)
        // Android 12+: Der System-Startbildschirm (App-Symbol, die Sonne geht auf) verschwindet sofort,
        // sobald die App bereit ist. Die Start-Animation in der App übernimmt nahtlos an derselben Stelle
        // und genau dort, wo die Sonne gerade war.
        if (Build.VERSION.SDK_INT >= 31) {
            splashScreen.setOnExitAnimationListener { v ->
                val started = v.iconAnimationStart?.toEpochMilli()
                if (started != null && started > 0) {
                    SplashHandOff.elapsedMs = (SystemClock.uptimeMillis() - started).toFloat()
                }
                v.remove()
            }
        }
        // Edge-to-edge: Die App zeichnet hinter Statusleiste und Navigationsleiste.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )

        // Sprache vor dem ersten Bild laden, damit kein Text kurz in der falschen Sprache aufblitzt.
        Lang.load(getSharedPreferences("funke", MODE_PRIVATE))
        // Vom Widget geöffnet? Dann gleich den richtigen Reiter zeigen.
        if (savedInstanceState == null) DeepLink.consume(this, intent)

        val view = ComposeView(this)
        setContentView(view)
        // Eigener Recomposer mit fester Animationsgeschwindigkeit (siehe oben).
        val recomposer = view.createLifecycleAwareWindowRecomposer(NormalMotion, lifecycle)
        view.setParentCompositionContext(recomposer)
        view.setContent { FunkeApp() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        DeepLink.consume(this, intent)
    }

    override fun onStop() {
        super.onStop()
        // Wer die Frage offen lässt und weggeht, bekommt sie beim nächsten Öffnen der App wieder (frisch)
        if (DeepLink.gate != null && !isChangingConfigurations) DeepLink.gate = null
    }
}
