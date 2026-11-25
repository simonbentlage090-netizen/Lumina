# Lumina

**A calm focus app for Android.** Lumina helps you put the phone down for a while: start a focus session and distracting apps stay locked until you're done. Read your own books, take notes, or think through a small puzzle in the meantime. Lumi, a little light bulb, keeps you company.

No account, no ads, no tracking. Everything stays on your phone.

[Deutsche Version](README.de.md)

## Features

**Focus sessions**
- Pick a duration, hold the button, and go. Apps you didn't allow are locked while you focus.
- Modes such as "Deep focus", each with its own list of allowed apps.
- A daily goal and a streak that encourages instead of nagging.

**Think Break**
- Before an app you want to use less opens, Lumi asks a short question. A moment to pause instead of scrolling.

**Your own books**
- Read PDF and EPUB files page by page, without distractions.
- Table of contents with chapters and sub-chapters, read from the book itself.
- Bookmarks, notes on every page (typed or dictated) and a calculator next to the page.

**Notes**
- Type or dictate notes. Optionally let Lumi tidy them into clean text or bullet points.

**Also**
- Small thinking tasks in maths, physics, logic and more, in three levels.
- Progress with screen time, reminders and home-screen widgets.
- Four looks (Glass, Modern, Flat, Cartoon), light and dark, in English and German.

## Download

- **GitHub:** the latest APK is on the [Releases](../../releases) page.
- **F-Droid:** submitted, coming soon.
- **Google Play:** planned.

Requires Android 8.0 or newer.

## Privacy and permissions

Lumina works offline. Your books, notes and statistics are stored only on your device.

| Permission | Why |
|---|---|
| Accessibility service "Lumina Lock" | Only to notice which app is in front: to lock apps during a focus session, to show the Think Break and full-screen reminders. Lumina does not read screen content or typed text. You see a clear explanation and agree before it is turned on. |
| Usage access | To show how long you used which app (screen time). |
| Notifications, exact alarms | For reminders you set yourself. |
| Microphone | Only while you dictate a note. Speech recognition is done by your phone's speech service. |
| Internet | Only for the optional Lumina AI (see below). |

**Optional Lumina AI.** "Clean up" for notes and "Find chapters" for books can use a small server (a Cloudflare Worker, source code in [`server/`](server)). It is off until you agree in the app. Only the text needed for that one request is sent. The text is not stored; the server only counts requests per day to keep within its free limit. Without it, Lumina tidies notes and finds chapters on the device.

The full privacy policy and terms are inside the app under Settings → Legal.

## Building from source

1. Install [Android Studio](https://developer.android.com/studio).
2. Clone this repository and open the folder in Android Studio.
3. Wait for the Gradle sync, then press Run, or build from the command line:

```
./gradlew assembleRelease
```

Tech: Kotlin, Jetpack Compose, Material 3. Only AndroidX libraries, no Google Play Services, no analytics.

## Feedback

Found a bug or have an idea? Please open an [issue](../../issues), or write to s.bentlage.lumina@gmail.com.

Version history: [CHANGELOG.md](CHANGELOG.md) (development notes in German).

## License

Lumina is free software under the [GNU General Public License v3.0](LICENSE).

Third-party content (fonts Inter, Nunito and Fredoka, Lucide icons, GSM8K word problems and others) is listed in [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) and in the app under Settings → About Lumina → Sources & licenses.
