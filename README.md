# Muscle Tracker

A workout tracker built for lifters who actually want to see their progress — log weight-based sets, bodyweight reps, or timed holds, get real PR detection with a proper strength-estimation formula, and export any session straight to Strava.

Website: [https://muscletracker.jpalayoor.com/](https://muscletracker.jpalayoor.com/)

## Features

- **Three tracking types per exercise** — weight + reps, reps-only, or a live stopwatch for timed holds like planks
- **Real PR detection** — estimated one-rep max using a Brzycki/Epley hybrid formula, generalized to track PRs by weight, reps, or duration depending on the exercise
- **Home dashboard** — training calendar, streak stats, and a suggested workout based on what you haven't done in a while
- **Full template management** — drag-and-reorder exercises, inline rename, add exercises to a template mid-workout
- **397 curated exercises** — trimmed and renamed from a public dataset, each tagged with a tracking type and mapped to a real category for export
- **CSV backup & restore** — export your entire history, restore it on any device, no account required
- **Strava export** — generates a real `.fit` file from any session, complete with per-set weight/reps/duration and exercise categorization, ready to upload
- **Unit conversion** — kg/lb, applied consistently everywhere weight is shown

## Tech stack

Java · MVVM · Room (SQLite)

## Build

1. Clone the repo
2. Open in Android Studio (Java, min SDK 28, compileSdk 35)
3. Run on a device or emulator

## Exercise data

Exercise database sourced from [free-exercise-db](https://github.com/yuhonas/free-exercise-db), curated down to 397 entries with additional specific-muscle, anatomical region, split-category, tracking-type, and FIT-export-category tags.

## License

[Unlicense](https://unlicense.org) — public domain, use it for anything, no attribution required.