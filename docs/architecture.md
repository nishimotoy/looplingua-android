# Architecture – LoopLingua

## 1. Architectural Philosophy

LoopLingua follows a layered, domain-centered architecture.

The system separates:

- Domain logic
- Data handling
- Playback engine
- UI layer

The core principle:

The playback engine must be independent from UI and network.

This ensures:

- Offline-first playback
- Testable domain logic
- Long-term maintainability

---

## 2. High-Level Layers

The application consists of four main layers:

1. Presentation Layer
2. Application Layer
3. Domain Layer
4. Data Layer

Each layer has a strict responsibility.

Dependencies flow downward only.

Presentation depends on Application.
Application depends on Domain.
Domain does not depend on other layers.
Data depends on Domain interfaces.

---

## 3. Presentation Layer

Responsible for:

- Compose UI
- ViewModels
- State observation
- User interactions

Presentation communicates with the Application layer via use cases.

Presentation must not:

- Access database directly
- Control ExoPlayer directly
- Perform business logic

State management uses StateFlow.

---

## 4. Application Layer

This layer coordinates use cases.

Responsibilities:

- Orchestrating segmentation flow
- Managing playlist operations
- Triggering playback
- Handling online processing flow

Examples of use cases:

- ImportMediaUseCase
- AutoSegmentUseCase
- CreateTrackUseCase
- StartPlaybackUseCase

This layer connects UI to domain logic.

---

## 5. Domain Layer

The heart of LoopLingua.

Contains:

- Entities (SourceMedia, Segment, Track, Playlist)
- PlaybackEngine
- PlaybackStep
- PlaybackConfig

Domain rules:

- No Android framework dependencies
- No network dependencies
- Pure Kotlin logic where possible

PlaybackEngine belongs here.

The Domain layer defines interfaces
that the Data layer implements.

---

## 6. Data Layer

Responsible for:

- Local storage (Room or file storage)
- Online API access (transcription, translation)
- Media metadata extraction

Online operations occur only during:

- Import
- Segmentation
- Translation generation

Playback must not depend on network.

Repositories live here.

Repositories implement interfaces defined in Domain.

---

## 7. Playback Architecture

PlaybackEngine is isolated from UI.

Flow:

ViewModel → StartPlaybackUseCase → PlaybackEngine

PlaybackEngine emits:

- CurrentTrack
- CurrentSegment
- PlaybackState
- Progress

ExoPlayer is wrapped inside a PlaybackController
that the engine interacts with via abstraction.

This prevents Android-specific coupling in domain logic.

---

## 8. Offline-First Guarantee

All required playback data is stored locally:

- Audio files (user-provided)
- Segment boundaries
- Transcripts
- Translations
- Playlists

Once prepared, learning requires no internet connection.

Online functionality is limited to preparation phase only.

---

## 9. Background Playback

Playback runs inside a Foreground Service.

Responsibilities:

- MediaSession integration
- Notification controls
- Lock-screen controls
- Audio focus management

UI observes playback state via shared StateFlow.

Service does not contain business logic.
It delegates to PlaybackEngine.

---

## 10. Future Scalability

The architecture supports:

- Adaptive repetition algorithms
- Smart pause timing
- Segment-level difficulty tracking
- Cross-device sync (future option)
- Multi-language support

Core domain remains stable even if:

- UI is redesigned
- Storage mechanism changes
- API provider changes

---

## 11. Design Principles Summary

- Domain-driven separation
- Offline-first playback
- User concepts separated from technical constructs
- Clear boundaries between layers
- Extensible repetition engine

LoopLingua is built as a structured learning engine,
not as a simple media player.
