# 📅 Emoji Calendar

**Emoji Calendar** is a **WIP** demo Android application that demonstrates **modern Android development practices** — including the use of **Jetpack Compose**, **Clean Architecture**, and **Unidirectional Data Flow (UDF)**.

## 📷 Preview



## ⚙️ Technical Details

### 🧩 Frameworks / Tools

- [**Kotlin**](https://kotlinlang.org/) — 100% Kotlin  
- [**Koin**](https://insert-koin.io/) — dependency injection  
- [**MVIKotlin**](https://arkivanov.github.io/MVIKotlin/) — MVI framework  
- [**Coroutines**](https://kotlinlang.org/docs/reference/coroutines-overview.html) / [**Flow**](https://developer.android.com/kotlin/flow/) — asynchronous operations  
- [**Jetpack Components**](https://developer.android.com/jetpack/):
  - [**Compose**](https://developer.android.com/jetpack/compose/) — modern declarative UI toolkit  
  - [**Emoji2**](https://developer.android.com/jetpack/androidx/releases/emoji2/) — emoji picker support  
  - [**Navigation**](https://developer.android.com/topic/libraries/architecture/navigation/) — handles navigation between composables  
  - [**ViewModel**](https://developer.android.com/topic/libraries/architecture/viewmodel/) — UI state holder  
- [**Room**](https://developer.android.com/jetpack/androidx/releases/room/) — local database persistence  

##

### 🧪 Unit Testing

- [**JUnit**](https://junit.org/) — testing framework  
- [**MockK**](https://mockk.io/) — mocking library for Kotlin  
- [**Strikt**](https://strikt.io/) — fluent assertions  

##

### 🎨 UI

- [**Material Design 3**](https://m3.material.io/)  
- Custom **Compose** components  

##

### 🏗️ Modern Architectural Approaches

- **Single-activity architecture** with a nested [Navigation component](https://developer.android.com/guide/navigation/navigation-getting-started) that defines the navigation graph  
- **MVI pattern** implementing **unidirectional data flow** ([UDF](https://developer.android.com/develop/ui/compose/architecture#udf))  
- **Multi-module [Gradle](https://gradle.org/)** project structured according to **Clean Architecture** principles and aligned with the [recommended app architecture](https://developer.android.com/topic/architecture#recommended-app-arch)  
