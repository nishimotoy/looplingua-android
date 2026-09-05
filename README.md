# LoopLingua

**Your language. On loop.**

> **Learn language like a baby — with adult ears.**

LoopLingua is a **sentence player for passive listening**.

Listening to natural-speed content in a foreign language can be difficult. Words and sentences pass
by before you understand them, while reading subtitles at natural speed can be exhausting.

LoopLingua breaks audio into sentences and inserts pauses between them. It can also play
translations aloud, allowing you to keep listening without constantly looking at the screen. The
screen is there when you want to check the text.

## The Idea

Language can be acquired through massive exposure to meaningful input — much like the way children
acquire their native language through continuous exposure.

LoopLingua explores how to make that kind of exposure possible for adults.

You don't need to stop what you are doing.
You don't need to stare at subtitles.
You don't need to set aside a dedicated study session.

**Just listen.**

The goal is to **make long-term listening practice effortless**.

## What Makes LoopLingua Different

Users can bring their own content — podcasts, videos, lectures, interviews, and other audio — and
turn it into structured listening material.

The focus is simple:

**make it easier to listen more, for longer.**

## Core Concept

```text
Natural content
      ↓
  Sentences
      ↓
   Pauses
      ↓
 Translation
      ↓
 Repetition
```

**Listen. Repeat. Let the language sink in.**

---

## Demo

https://github.com/user-attachments/assets/8e485358-3138-48b6-aaba-c43af231ee33

[▶ Download the LoopLingua demo](demo/looplingua-demo.mp4)

The demo shows the current player UI and structured playback features.

---

## Player

The current player supports:

* Sentence/segment-based playback
* Multiple tracks in a project
* Automatic progression between segments
* Automatic looping
* PREV / NEXT navigation
* Pinning a segment for repeated playback
* Flagging segments
* Skipping segments
* Playback speed adjustment
* Pauses adjustment
* Multiple playback patterns
* Pattern-specific playback settings
* Persistent playback settings
* Persistent playback position
* Safe audio stopping and player release

### Playback Patterns

LoopLingua currently supports built-in playback patterns including:

* **BASIC**
* **SHADOWING**
* **ORIGINAL_ONLY**

Patterns determine how original audio, translation, memo, and pauses are combined.

Playback speed and pause multipliers can be adjusted from the player UI. User adjustments override
the values defined by the selected pattern and are restored across sessions.

---

## LoopLingua Project Format

LoopLingua uses its own `.looplingua` project format.

A project can contain multiple tracks, with each track divided into timestamped segments.

Segment data includes information such as:

* Original text
* User-edited original text
* Automatic translation
* User-edited translation
* Automatic memo
* User-edited memo
* Segment flags
* Skip state
* Audio timestamps

The player loads these projects and maps their data into the playback model.

---

## Translation Engine

LoopLingua includes a translation pipeline for turning source audio into structured learning
material.

The basic workflow is:

```text
Audio
  ↓
Whisper transcription
  ↓
Timestamped segments
  ↓
LoopLingua project
  ↓
Translation
  ↓
.looplingua
  ↓
LoopLingua Player
```

The translation engine uses OpenAI APIs for speech transcription and translation.

---

## Architecture

LoopLingua is organized as separate application and engine components.

The Android application uses **Jetpack Compose** for the UI and **StateFlow** for player state
management.

The core playback path is:

```text
UI (Jetpack Compose)
        ↓
PlayerController
        ↓
TrackQueue
        ↓
SequenceBuilder
        ↓
SegmentPlayer
        ↓
AudioPlayer
        ↓
ExoPlayer
```

The application also contains the project/data layer responsible for loading `.looplingua` projects
and mapping them into player models.

---

## Current Status

🚧 **Active development**

The playback system has reached a stable milestone and is currently tagged:

**`v1.0-player`**

The current development focus is expanding the LoopLingua data model, translation workflow, and
learning-oriented editing features.

---

## Planned Features

* User-defined playback patterns
* User-defined segment flags
* Editing of segment text and timestamps
* Translation editing
* Memo editing
* Text-to-speech generation
* Additional playback modes
* More flexible shadowing workflows
* Improved project management

---

## Vision

Most language learning apps focus on **active study** — vocabulary drills, exercises, tests, and
deliberate practice.

LoopLingua explores another approach:

> **Continuous passive listening with structured repetition.**

The aim is to make language exposure something that can happen naturally throughout the day — while
walking, commuting, cooking, or doing other activities.

LoopLingua combines the effortless exposure of passive listening with enough structure and
repetition to make that exposure useful for language learning.

**Learn language like a baby — with adult ears.**

---

## License

LoopLingua is source-available software licensed under the
PolyForm Perimeter License 1.0.1.

You are welcome to study, use, and modify LoopLingua for
personal, educational, and research purposes.

You may not use LoopLingua to provide a product that competes
with LoopLingua.

See the [PolyForm Perimeter License 1.0.1](https://polyformproject.org/licenses/perimeter/1.0.1)
for the complete license terms.