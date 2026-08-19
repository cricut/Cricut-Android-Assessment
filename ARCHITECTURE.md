# Architecture

A small app, structured like a big one. The design follows the
[Android architecture recommendations](https://developer.android.com/topic/architecture/recommendations):
three layers with strict dependency direction, unidirectional data flow, and a single
source of truth for every piece of state.

```
┌─────────────────────────────────────────────────────────────┐
│  UI layer                    com.cricut.androidassessment.ui │
│                                                              │
│   AssessmentScreen ──events──▶ AssessmentViewModel           │
│        ▲                            │                        │
│        └────── AssessmentUiState ◀──┘  (StateFlow)           │
│                                                              │
│   QuizProgress ◀──▶ SavedStateHandle (process-death proof)   │
├─────────────────────────────────────────────────────────────┤
│  Domain layer            com.cricut.androidassessment.domain │
│                                                              │
│   Question / Answer (sealed models) · QuizGrader (pure)      │
├─────────────────────────────────────────────────────────────┤
│  Data layer                com.cricut.androidassessment.data │
│                                                              │
│   QuizRepository (interface) ◀── LocalQuizRepository         │
│   AndroidTriviaQuestions (bundled content, fake network)     │
└─────────────────────────────────────────────────────────────┘
```

## Layer responsibilities

### Domain (`domain/`)
- **`Question`** — a sealed hierarchy of the four question types (true/false,
  multiple choice, multiple select, open-ended). `when` branches over it are
  exhaustive, so adding a question type is a compile-time checklist.
  `init` blocks enforce content invariants (e.g. a multiple-choice question must
  have exactly one correct option).
- **`Answer`** — mirrors the question types; `isComplete()` is the single
  definition of "answered enough to move on" used to gate navigation.
- **`QuizGrader`** — pure, stateless grading. No Android types, trivially unit
  testable.

The one pragmatic impurity: `Answer` implements `Parcelable` (via `@Parcelize`)
so answers can live in `SavedStateHandle`. In a multi-module app this could be
split into a UI-layer parcelable mapping; here that ceremony would outweigh the
benefit.

### Data (`data/`)
- **`QuizRepository`** is the interface the rest of the app sees;
  **`LocalQuizRepository`** implements it with a simulated network delay on an
  injected dispatcher, so the UI's loading path is honest and tests can use
  virtual time. A real API client would slot in behind the same interface.
- Question content lives here (not in `strings.xml`) because it is *data* —
  in production it would come from a server, not the APK's resources.
- Hilt wires the binding in `DataModule`.

### UI (`ui/`)
- **`AssessmentViewModel`** owns the session. It `combine`s two sources —
  questions from the repository and a `QuizProgress` value backed by
  `SavedStateHandle` — into one `StateFlow<AssessmentUiState>`. All user
  interactions are events (`onAnswerChange`, `onNextClick`, …) that reduce into
  a new `QuizProgress`; the UI never mutates state directly.
- **`AssessmentUiState`** is sealed: `Loading`, `InProgress` (exactly one
  question plus derived flags like `canAdvance`), `Complete` (a graded
  `QuizReport`). Impossible states are unrepresentable.
- **`AssessmentScreen`** is a thin stateful wrapper; `AssessmentScreenContent`
  is stateless and preview/test friendly. `AnimatedContent` keys on the *step*
  (not the whole state) so typing recomposes in place while step changes slide.
- System back mirrors the on-screen Back button via `BackHandler`.

## State survival

| Event | Mechanism | Verified |
|---|---|---|
| Screen rotation | ViewModel outlives the Activity | on-device |
| Backgrounding | same | on-device |
| Process death | `QuizProgress` is `@Parcelize`d into `SavedStateHandle` | on-device (`am kill` + relaunch) |

Because everything the user has done is one parcelable value under one key,
save/restore has no partial-state failure modes.

## Testing

19 JVM unit tests, no mocking framework needed (a hand-rolled
`FakeQuizRepository` with an optional gate stands in for the data layer):
- `QuizGraderTest` — grading rules per type, including type-mismatch and
  unanswered cases.
- `AnswerTest` — the `isComplete()` gating rules.
- `LocalQuizRepositoryTest` — bundled content sanity, virtual-time delay.
- `AssessmentViewModelTest` — loading → first question, gated navigation,
  back-preserves-answers, grading, restart, and ViewModel recreation against a
  shared `SavedStateHandle`.

`detekt` (with the Compose ruleset) passes clean.

## Deliberate scope choices

- **All four** question types are implemented (the brief asks for two) because
  the sealed-model design makes each additional type ~30 lines of editor UI.
- Single-screen content replacement instead of Jetpack Navigation: with one
  logical destination and ViewModel-owned step state, a NavHost would add a
  second source of truth for "where the user is" without buying anything.
- No error state for loading: the only data source is local and infallible; the
  repository interface is where a real one would hang off.
