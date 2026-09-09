# PalashSaathi (ᱯᱟᱞᱟᱥ ᱥᱟᱛᱷᱤ)

**AI-Powered Vernacular Pedagogy and Real-Time Translation Tool**  
*100% Offline Edge Runtime for Mother-Tongue-Based Multilingual Education (MTB-MLE) in Jharkhand & Eastern India*

---

## Overview

**PalashSaathi** is an offline, AI-assisted pedagogical companion designed for non-native primary school educators teaching in tribal and multilingual classrooms. By placing **Santali (ᱥᱟᱱᱛᱟᱲᱤ)** at the forefront—rendered natively in the **Ol Chiki (`ᱚᱞ ᱪᱤᱠᱤ`)** script alongside **Devanagari (`देवनागरी`)** phonetic transliterations—the tool bridges foundational language barriers in early education (FLN Grades 1 to 3).

The application indexes a **20,000-sentence Santali-English parallel training corpus** directly on-device, providing real-time voice translation, bilingual worksheet generation, visual flashcards, and live auxiliary subtitles in **Ho** and **Mundari** without requiring any internet connection.

---

## Screenshots

| Landing Synopsis | Voice Translation | Visual Flashcards | FLN Worksheet |
| :---: | :---: | :---: | :---: |
| <img src="screenshot/00_landing_synopsis.png" width="200"/> | <img src="screenshot/01_voice_translation.png" width="200"/> | <img src="screenshot/02_visual_flashcards.png" width="200"/> | <img src="screenshot/03_fln_worksheets.png" width="200"/> |

---

## Core Capabilities

### 1. 100% Offline Edge Operation
- Runs entirely on-device on low-cost Android hardware (ARMv7/ARMv8, 2 GB RAM, Android 9.0+).
- Requires zero internet connectivity, SIM cards, or external cloud APIs.
- Real-time voice translation response under 300 milliseconds (well within the sub-3.0s ceiling).

### 2. Primary Language: Santali (Ol Chiki & Devanagari)
- Dual-script engine supports native Ol Chiki Unicode (`U+1C50` - `U+1C7F`) and Devanagari orthography.
- One-tap global script toggle across the entire application interface.
- Complete bidirectional phonetic engine for Ol Chiki numerals (`᱐` to `᱙`) and phonetic Romanization.

### 3. 20,000 Parallel Corpus Deep Integration
- Bundled offline parallel training dataset (`santali_corpus.csv`, 20,000 sentence pairs).
- High-performance asynchronous indexing via Kotlin Coroutines and background IO dispatchers.
- Fast bidirectional full-text search across English, Ol Chiki, Devanagari, and Latin phonetics.
- Corpus elements surfaced throughout the app:
  - **Voice Translate**: Featured corpus sentences with `20K Dataset` tags.
  - **Worksheet Generator**: `20K वाक्य (Corpus Reading)` and `20K शब्द (Corpus Vocab)` exercise modules.
  - **Flashcards**: Dataset vocabulary cards with pronunciation audio.
  - **Corpus Explorer**: Dedicated browser and search engine for all 20,000 sentences.

### 4. Multi-Tribal Auxiliary Subtitle HUD
- Live comparative subtitle stream in **Ho** and **Mundari** beneath every Santali phrase.
- Facilitates cross-comprehension in diverse multi-tribal classrooms where multiple Munda languages coexist.

### 5. Automated Bilingual FLN Worksheet Generator
- Generates curriculum-aligned printable exercises (NIPUN Bharat Grade 1-3).
- Categories:
  - **संख्या (Numeracy)**: Counting, digit recognition, practical math.
  - **भाषा (Literacy)**: Word-to-picture matching, object identification.
  - **निर्देश (Classroom Commands)**: Action verbs, seating, attentiveness.
  - **20K वाक्य (Corpus Reading)**: Reading comprehension from the 20K corpus.
  - **20K शब्द (Corpus Vocab)**: Vocabulary matching and translation drills.
- Print-ready A4 PDF export saved directly to device storage via `WorksheetPdfGenerator`.

### 6. Interactive Visual Flashcards
- High-contrast visual flashcards covering numeracy, literacy, and dataset vocabulary.
- Native speech audio synthesis with one-tap pronunciation playback.
- Visual badges highlighting dataset-derived items.

### 7. WCAG AAA High-Contrast UI & Theme Switcher
- High-visibility color palette designed for bright rural daylight and semi-open classrooms:
  - **Primary**: Coral Saffron (`#E65100`)
  - **Secondary**: Forest Green (`#1B5E20`)
  - **Tertiary / Accent**: Golden Amber (`#F57F17`)
- Appearance preferences: System Default, Light Mode, and Dark Mode.
- Strictly Material 3 vector iconography throughout (zero emojis).

### 8. Branded Logo & Landing Synopsis
- Custom branded logo embodying tribal identity, folklore motifs, and linguistic heritage.
- App startup includes a 2.4-second animated landing synopsis that smoothly fades out to the main screen.

---

## Architecture & Codebase Structure

```
PalashSaathi/
├── app/
│   ├── src/main/
│   │   ├── assets/
│   │   │   └── santali_corpus.csv              # 20,000 Santali-English parallel sentences
│   │   ├── java/com/palashsaathi/app/
│   │   │   ├── MainActivity.kt                 # Application entry, theme initialization & corpus warm-up
│   │   │   ├── data/
│   │   │   │   ├── FLNDictionary.kt            # Curated FLN lexicon, classroom prompts & corpus subsets
│   │   │   │   ├── SantaliCorpusRepository.kt  # Fast in-memory 20K corpus indexer and search engine
│   │   │   │   └── model/
│   │   │   │       └── Models.kt               # Domain models: CorpusSentence, TranslationResult, etc.
│   │   │   ├── engine/
│   │   │   │   ├── OlChikiConverter.kt         # Ol Chiki <-> Devanagari transliteration & numerals
│   │   │   │   ├── AudioSynthesisEngine.kt     # Low-latency acoustic speech synthesizer
│   │   │   │   └── WorksheetPdfGenerator.kt    # A4 printable PDF generator
│   │   │   └── ui/
│   │   │       ├── screens/
│   │   │       │   ├── LandingSynopsisScreen.kt# Branded launch synopsis with auto fade-out
│   │   │       │   ├── MainScreen.kt           # Navigation Scaffold, TopAppBar & script toggle
│   │   │       │   ├── VoiceTranslateScreen.kt # Push-to-Talk voice translation & subtitle HUD
│   │   │       │   ├── WorksheetScreen.kt      # Bilingual FLN worksheet generator & PDF export
│   │   │       │   ├── FlashcardsScreen.kt     # Visual flashcards with audio pronunciation
│   │   │       │   ├── PhrasebookScreen.kt     # Classroom prompts & 20K Corpus Explorer
│   │   │       │   └── SettingsScreen.kt       # Appearance switcher & system status
│   │   │       └── theme/
│   │   │           ├── Color.kt                # High-contrast color definitions
│   │   │           ├── Theme.kt                # Theme provider (System Default, Light, Dark)
│   │   │           └── Type.kt                 # Typography hierarchy
│   │   └── res/
│   │       ├── drawable/
│   │       │   └── app_logo.png                # Master branded logo asset
│   │       ├── mipmap-*/                       # Generated launcher icons for all screen densities
│   │       └── values/
│   │           ├── colors.xml                  # XML color references
│   │           └── strings.xml                 # App metadata strings
├── build.gradle.kts                            # Top-level Gradle configuration
├── settings.gradle.kts                         # Module declarations
├── ROADMAP.md                                  # Product roadmap and milestone tracking
└── README.md                                   # Project documentation
```

---

## Getting Started & Build Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK Platform 34
- Android Device or Emulator running Android 9.0 (API 28) or higher

### Building the Debug APK
```bash
# Clone the repository
git clone https://github.com/akasajal/PalashSaathi.git
cd PalashSaathi

# Build debug APK using Gradle
./gradlew assembleDebug
```
The compiled APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

### Installing via ADB to Connected Device
```bash
# Verify connected device
adb devices

# Install APK with replacement flags
adb install -r -d app/build/outputs/apk/debug/app-debug.apk

# Launch MainActivity directly
adb shell am start -n com.palashsaathi.app/.MainActivity
```

---

## Technical Specifications

| Parameter | Specification |
| :--- | :--- |
| **Primary Language** | Santali (ᱥᱟᱱᱛᱟᱲᱤ / संथाली) |
| **Primary Script** | Ol Chiki (`ᱚᱞ ᱪᱤᱠᱤ`, Unicode U+1C50 - U+1C7F) |
| **Transliteration** | Devanagari (`देवनागरी`) and Latin Phonetics |
| **Auxiliary Subtitles** | Ho and Mundari |
| **Corpus Volume** | 20,000 Parallel Sentences (`santali_corpus.csv`) |
| **Target Latency** | <= 300 ms on-device round trip |
| **Target Hardware** | Android 9.0+, ARMv7/ARMv8, <= 2 GB RAM |
| **Network Dependency** | 0% (Air-gapped offline operation) |
| **UI Framework** | Jetpack Compose with Material 3 |
| **Accessibility** | WCAG AAA compliant contrast ratios |

---

## License

This project is licensed under the Apache 2.0 License.