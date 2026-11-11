# CinePulse (Android)

Original streaming-app UI (Jetpack Compose + Media3/ExoPlayer) that streams **public-domain
classic films** from the Internet Archive. No proprietary code, assets, or branding from any
commercial service.

Films included (all verified public domain, streamed directly from archive.org):

| Film | Year | Genre |
|---|---|---|
| His Girl Friday | 1940 | Screwball comedy |
| Meet John Doe | 1941 | Drama |
| The General | 1926 | Silent action-comedy |
| The Little Shop of Horrors | 1960 | Horror-comedy |
| Suddenly | 1954 | Thriller |

## Build

The APK is built by GitHub Actions (`.github/workflows/build-apk.yml`): JDK 17, Gradle 8.11,
AGP 8.7.3. Every push to `main` produces a debug APK artifact and an `apk-v1.0` release.

## Structure

- `app/src/main/java/pk/cinepulse/app/MainActivity.kt` — the whole app: catalog, home rows,
  detail screen, ExoPlayer playback
- `app/build.gradle` — Compose BOM 2024.10.01, Media3 1.4.1, Coil 2.7.0

## Adding films

Append to `FILMS` in `MainActivity.kt`: find a public-domain item on archive.org, verify its
MP4 URL with `curl -I`, add the entry. Do not add copyrighted content.
