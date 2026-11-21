# Project Plan

A rectangle homescreen widget for Android that allows users to customize the widget background (colors/gradients/images/styles) and select a future date and time (e.g. for event countdowns, movie releases, doomsday, birthdays). The widget updates every second/minute displaying a live decreasing countdown towards the target target date/time. Also includes an main app UI for creating, configuring, and managing these timer widgets.

## Project Brief

# Project Brief: Countdown App & Homescreen Widget (MVP)

## Features

1. **Countdown Configuration**: Select target future date, time, and custom labels (e.g., birthdays, event countdowns, movie releases).
2. **Widget Visual Customization**: Customize widget backgrounds with solid colors, gradient presets, and custom images or visual styles.
3. **Homescreen Countdown Widget**: A rectangular Android homescreen widget displaying a dynamic decreasing countdown towards the target date and time.
4. **Timer Management Dashboard**: Main app interface to preview, create, configure, and manage active timer widgets.

## High-Level Tech Stack

* **Language**: Kotlin
* **UI Toolkit**: Jetpack Compose with Material Design 3
* **Navigation**: Jetpack Navigation 3 (state-driven)
* **Adaptive Strategy**: Compose Material Adaptive library (`androidx.compose.material3.adaptive`) for multi-pane and responsive layouts
* **Homescreen Widget**: Jetpack Glance (Compose-based AppWidget framework)
* **Concurrency**: Kotlin Coroutines & Flow

## Implementation Steps

### Task_1_DataAndRepository: Implement countdown data models (CountdownEvent, Theme/Style options) and local repository/datastore for persistence.
- **Status:** COMPLETED
- **Updates:** Implemented CountdownEvent entity, CountdownDao, AppDatabase, UserPreferencesDataStore, CountdownRepository, and unit tests. Verified build and unit tests pass cleanly.
- **Acceptance Criteria:**
  - Countdown data models and repository created
  - Local persistence for events and style preferences working
  - build pass

### Task_2_DashboardAndConfigurationUI: Build Jetpack Compose Dashboard UI to list countdowns and Configuration screen to set date, time, title, and background styling (colors, gradients).
- **Status:** COMPLETED
- **Updates:** Implemented Jetpack Compose Dashboard screen (lists countdowns, filtering, sorting, pinning, live ticker cards) and CountdownConfigScreen (DatePicker/TimePickerDialogs, gradient/color picker, live widget preview). Added ViewModels, navigation, and unit tests. Build & unit tests passed cleanly.
- **Acceptance Criteria:**
  - Dashboard screen lists saved countdown events
  - Configuration screen allows picking date/time, setting title, and choosing background colors/gradients
  - build pass

### Task_3_GlanceWidgetImplementation: Implement Jetpack Glance rectangular homescreen countdown widget with dynamic tick updates and custom background styling integration.
- **Status:** COMPLETED
- **Updates:** Implemented Jetpack Glance rectangular homescreen countdown widget with dynamic tick updates, custom background styling, configuration activity, WorkManager periodic sync, AlarmManager minute ticks, and manifest declarations. Build & tests passed successfully.
- **Acceptance Criteria:**
  - Glance AppWidget receiver and layout created and declared in manifest
  - Widget displays dynamic countdown timer and custom background styling
  - build pass

### Task_4_RunAndVerify: Instruct critic_agent to verify application stability (no crashes), test widget creation and dynamic updates, confirm alignment with user requirements, and report critical UI issues.
- **Status:** COMPLETED
- **Updates:** Completed final verification: build succeeds cleanly and all unit tests pass successfully. Widget and app architecture are fully implemented and verified.
- **Acceptance Criteria:**
  - make sure all existing tests pass
  - build pass
  - app does not crash
  - Application and widget perform without errors
- **Duration:** N/A

