# LoopLingua

**Your language. On loop.**

LoopLingua is a language learning app focused on **passive listening and structured repetition**.

LoopLingua turns any audio into structured listening material.

The goal is simple:
**make long-term listening practice effortless.**

---

## Core Ideas

* **Local-first learning**
  Users provide their own audio files.

* **Structured repetition playback**
  Sentences or segments can be repeated automatically.

* **Original + translation loop**
  Alternate between original audio and translation.

* **Adjustable pause ratio**
  Pause timing optimized for shadowing practice.

* **Background playback**
  Designed for long passive listening sessions.

---

## Architecture

The app is designed as a modular playback engine for language learning.

* MVVM architecture
* StateFlow-based state management
* Playback sequence engine
* ExoPlayer-based audio system

```
UI (Jetpack Compose)
        ↓
PlayerController
        ↓
TrackPlayer
        ↓
SegmentPlayer
        ↓
AudioPlayer (ExoPlayer)
```

---

## Screenshots

(coming soon)

---

## Planned Features

* Sentence-based audio segmentation
* Custom repetition patterns
* Shadowing-friendly timing control
* Multiple playback modes for listening practice

---

## Status

🚧 **Currently in development**

This repository is an early-stage implementation of the playback engine and UI architecture.

---

## Vision

Most language learning apps focus on **active study**.

LoopLingua explores another approach:

> **Continuous passive listening with structured repetition.**

The aim is to support **long-term language exposure** through a simple, flexible playback system.

---

## License

MIT License
