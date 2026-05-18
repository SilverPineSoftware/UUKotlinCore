# UUKotlinCore

Foundation library for Silverpine UU Android/Kotlin projects: errors, results, JSON, logging, security, threading, and common Android helpers.

## Maven coordinates

| Artifact | Coordinates |
|----------|-------------|
| Core | `com.silverpine.uu:uu-core-ktx` |

Published to [Maven Central](https://central.sonatype.com/search?q=com.silverpine.uu) under the `com.silverpine.uu` group.

## What's included

### Errors and results

- **`UUError`** — domain/code model with optional `userInfo` (`Bundle`), descriptions, and chained underlying errors.
- **`UUResult<Success, Error>`** — typed success/failure (distinct from `kotlin.Result`) with `map`, `flatMap`, `fold`, and coroutine-friendly helpers.

### JSON and serialization

- **`UUJson`** / **`UUKotlinXJsonProvider`** — pluggable JSON façade over kotlinx.serialization.
- **Custom serializers** — enums (`UUEnumSerializer`, `UUSafeEnumSerializer`), dates, hex/base64 byte arrays.

### Logging

- **`UULog`** — level-based logging with swappable writers (`UUConsoleLogWriter`, `UUPrintLogWriter`).

### Security and storage

- **`UUSecurePrefs`**, **`UUEncryptedSharedPreferences`**, **`UUCrypto`**, **`UUSecretKey`** — encrypted preferences and key handling.

### Android and I/O utilities

- **`UUPrefs`**, **`UUBundle`**, **`UUParcel`**, **`UUContentResolver`**, **`UUResources`**
- **`UUStream`**, **`UUFile`**, **`UUCompression`** — copy, unzip, read-all helpers
- **`UUTimer`**, **`UUDispatch`**, **`UUWorkerThread`** — timing and threading
- **`UUDate`**, **`UURandom`**, collections helpers (`UUList`, `UUArray`, `UUByteArray`)

## Gradle dependency

```kotlin
dependencies {
    implementation("com.silverpine.uu:uu-core-ktx:<version>")
}
```

Requires the UU Kotlin build catalog (`uu_build`) and GitHub Packages access for `UUKotlinBuild`, consistent with other UU libraries.

## Requirements

- Android `minSdk` / `targetSdk` from your UU `gradle.properties` (`uu_min_sdk`, `uu_target_sdk`)
- Kotlin and kotlinx.serialization versions aligned with the published `uu-kotlin-build-catalog`

## Changes in this release

- **`UUResult`** now supports a separate `ErrorType` generic parameter for domain-specific failures.
- Dokka Javadoc JAR published with the release artifact.
- JUnit 5 unit test coverage across core APIs.
- Compatible with UU Networking, Bluetooth, Data, UX, and Test libraries at matching `uu_build` versions.

---

For prior versions and snapshots, see [GitHub Releases](https://github.com/SilverpineSoftware/UUKotlinCore/releases).
