# Playback Design – LoopLingua

## 1. Core Philosophy

LoopLingua is not just an audio player.

It is a sequence-driven repetition engine built around structured learning units.

Playback is defined as a controlled execution of learning segments,
not as a continuous file stream.

The system separates:

- Physical audio files
- Time-based segments
- User-visible tracks
- Playlists
- Playback execution logic

This separation ensures flexibility and future extensibility.

---

## 2. Domain Model Overview

The playback domain consists of five core concepts:

1. SourceMedia
2. Transcript
3. Segment
4. Track
5. Playlist

Each has a distinct responsibility.

---

## 3. SourceMedia

Represents a user-provided local audio file.

Properties:

- fileUri
- durationMs
- metadata (optional)

SourceMedia is never modified.
It is the immutable learning material.

---

## 4. Transcript

Represents the full transcription result of a SourceMedia.

It contains:

- fullText
- time-aligned sentence or word units

Transcript is generated via online API
during the import / preparation phase.

It is stored locally after generation.

Playback does not require network access.

---

## 5. Segment

Segment is the fundamental time-based learning unit.

It represents a clipped portion of a SourceMedia.

Properties:

- id
- sourceMediaId
- startTimeMs
- endTimeMs
- originalText
- translationText (nullable)

Segment is editable by the user.

Segmentation process:

1. Auto-segmentation (online processing)
2. User review and correction
3. Stored locally

Segment is the smallest playback-capable unit.

---

## 6. Track

Track is a user-facing playback concept.

It represents a logical playback unit in the playlist.

Important distinction:

Segment is technical.
Track is conceptual.

Track may contain:

- One Segment
- Multiple Segments grouped together

Conceptually:

Track = ordered list of Segment IDs

Track allows:

- User-controlled grouping
- Custom repetition structure
- Flexible learning design

---

## 7. Playlist

Playlist represents an ordered collection of Tracks.

Structure:

Playlist → List of Track  
Track → List of Segment  
Segment → Time range within SourceMedia

This enables:

- Continuous background playback
- Custom start point selection
- Reordering
- Looping behavior

---

## 8. Playback Engine Flow

PlaybackEngine receives:

- Track
- PlaybackConfig

Internal transformation:

Track → Segments → PlaybackSteps

PlaybackStep types:

- PlaySegment
- Pause

For each Segment:

1. Seek to startTimeMs
2. Clip playback to endTimeMs
3. Execute repetition logic
4. Insert pause based on configuration

Playback is fully offline.

No network access is required during playback.

---

## 9. Online vs Offline Responsibilities

Online phase:

- Speech-to-text transcription
- Auto segmentation
- Translation generation

Offline phase:

- Segment editing
- Track building
- Playlist management
- Playback execution

All processed data is stored locally.

Playback must function without network connectivity.

---

## 10. Design Principles

- Clear separation of domain layers
- User concepts separated from technical constructs
- Fully local playback
- Extensible segmentation model
- Future-ready for adaptive repetition algorithms

LoopLingua is a structured repetition system,
not merely an audio player.
