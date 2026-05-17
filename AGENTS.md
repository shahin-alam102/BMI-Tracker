# AGENTS.md - BMI Tracker Codebase Guide

## Project Overview

**BMI Tracker** is an Android health application that calculates Body Mass Index with real-time results, unit conversion (metric/imperial), and visual feedback through a gauge component.

- **Type**: Native Android Application  
- **Language**: Java 11 with Gradle/Kotlin DSL  
- **Architecture**: MVVM (Model-View-ViewModel) with LiveData  
- **Min SDK**: 24 (Android 7.0) | **Target SDK**: 35 | **Compile SDK**: 36

---

## Architecture & Data Flow

### Core Structure

```
com.voxo.bmitracker/
├── model/           # Data models (immutable)
│   └── BmiResult    # Container for calculation results
├── viewmodel/       # Business logic layer
│   └── BmiViewModel # BMI calculation, state management with LiveData
└── ui/              # Presentation layer
    └── MainActivity # Single-activity UI with ViewBinding
```

### Critical Data Flow Pattern

1. **User Input** → TextWatcher in MainActivity captures height/weight changes
2. **Trigger Calculation** → `triggerCalc()` invokes `BmiViewModel.calculateBMI()`
3. **ViewModel Processing** → BmiViewModel validates input, converts units, computes BMI categories
4. **LiveData Emission** → ViewModel posts `BmiResult` to `_bmiResult` MutableLiveData
5. **UI Update** → MainActivity observer receives BmiResult and updates:
   - Category text + color (from getCategoryColor())
   - SpeedView gauge animation (speedTo method)
   - Normal weight range display
   - Weight difference text with guidance
   - Table row highlighting

**Key Design Decision**: Business logic is purely in ViewModel; UI layer is thin. Input validation happens in ViewModel to prevent null BmiResult when inputs are empty/invalid.

---

## Key Components & Patterns

### BmiViewModel - Calculation Engine

- **Dual LiveData Pattern**: Uses `_bmiResult (MutableLiveData)` internally and exposes public `bmiResult (LiveData)` read-only
- **Unit Conversion Logic** (lines 24-25):
  - Weight: `lb * 0.453592` = kg
  - Height: `ft * 0.3048` = m OR `cm / 100` = m
- **BMI Categories** (see `getCategory()` method):
  - 8 categories from "Very Severely Underweight" to "Obese Class III"
  - Use cascading float comparisons (not a map) for lookups
- **Color Coding** (see `getCategoryColor()` method):
  - Blue (#3498DB) = Underweight
  - Green (#27AE60) = Healthy (18.5-24.9)
  - Yellow (#F1C40F) = Overweight
  - Red (#E74C3C & #C0392B) = Obese categories

**Critical Pattern**: Always check `heightM > 0` before division to prevent calculation errors.

### MainActivity - UI Orchestration

- **Initialization Flow** (onCreate → initUI → setupObservers → setupListeners)
- **ViewBinding**: Auto-generated `ActivityMainBinding` class replaces findViewById
- **Dark Mode Logic** (lines 38-40): Detects night mode and adjusts text colors accordingly
- **SpeedView Gauge Sections** (lines 49-53): Define 4 visual zones:
  - 0-37%: Blue (underweight)
  - 37-50%: Green (healthy)
  - 50-60%: Yellow (overweight)
  - 60-100%: Red (obese)

**Real-time Trigger Pattern**: TextWatcher on height/weight EditTexts fires `triggerCalc()` on every keystroke, ensuring instant feedback.

### Table Row Highlighting

- **Category Matching** (lines 103-110): Uses string.contains() and equals() to find matching row
- **View Traversal** (lines 113-119): Iterates children to find TextViews and apply color + bold formatting
- **Reset Pattern** (lines 123-139): Resets all rows to default color from `R.color.text_secondary_dynamic`

---

## Build & Development Workflow

### Build Commands

```bash
# Debug build (development)
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run on emulator/device
./gradlew installDebug && adb shell am start -n com.voxo.bmitracker/.ui.MainActivity

# Run unit tests
./gradlew test
```

### Key Gradle Config (app/build.gradle.kts)

- **Java Version**: 11 (sourceCompatibility & targetCompatibility)
- **View Binding**: Enabled (`buildFeatures { viewBinding = true }`)
- **ProGuard**: Disabled in release (isMinifyEnabled = false)
- **Test Framework**: JUnit 4 + Espresso 3.7.0

### Dependencies to Know

- `androidx.lifecycle:lifecycle-viewmodel:2.10.0` - ViewModel class
- `androidx.lifecycle:lifecycle-livedata:2.10.0` - LiveData/MutableLiveData
- `com.github.anastr:speedviewlib:1.6.0` - Gauge visualization (custom library)
- Material Design 1.13.0 - UI components

---

## Project-Specific Conventions

### Naming & Organization

- **Package Structure**: Strict separation (model, viewmodel, ui packages)
- **Activity Naming**: Single activity `MainActivity` handles all UI
- **ViewModel Naming**: `<Feature>ViewModel` pattern (BmiViewModel)

### Color Management

- **Hardcoded Hex Colors**: Used in ViewModel (`#3498DB`, `#E74C3C`, `#27AE60`, `#F1C40F`)
- **Resource Colors**: Dark mode uses `R.color.text_secondary_dynamic` reference
- **Why Mix**: ViewModel needs specific BMI category colors; UI needs theme-aware defaults

### SpeedView Customization

- **Sections**: Added at runtime in initUI() to partition visual zones
- **Text Color Sync**: Manual setTextColor, setUnitTextColor, setSpeedTextColor calls
- **Indicator Color**: Synced with BMI category color on each result update

---

## Adding Features: Typical Patterns

### To Add New BMI Category or Adjust Ranges

1. Update `getCategory()` method in BmiViewModel (add/modify if-conditions)
2. Update corresponding color in `getCategoryColor()` 
3. If new table row exists, add matching case in `highlightRow()` method

### To Add New Units (e.g., stone)

1. Add spinne adapter resource in `res/values/arrays.xml`
2. Update `triggerCalc()` to pass new unit string
3. Add conversion logic in `calculateBMI()` before height/weight unit checks

### To Persist Results (Not Currently Implemented)

- Add database dependency (e.g., Room)
- Create Entity class in model package
- Add DAO and Database class
- Modify ViewModel to save results on calculation complete

---

## Critical Files Reference

| File | Purpose | Key Methods |
|------|---------|------|
| `app/build.gradle.kts` | Build configuration | View binding, Java 11 config |
| `BmiViewModel.java` | Calculation engine | calculateBMI(), getCategory(), getCategoryColor() |
| `MainActivity.java` | UI controller | initUI(), setupObservers(), triggerCalc() |
| `BmiResult.java` | Result DTO | Immutable data holder |
| `activity_main.xml` | Layout (generated) | Not tracked; use DataBinding preview |

---

## Common Debugging

**Issue**: Results not updating when text changes  
→ Check TextWatcher is attached to etHeight and etWeight  
→ Verify triggerCalc() calls viewModel.calculateBMI()

**Issue**: Wrong colors displayed  
→ Verify hex color strings match expected values (e.g., `#27AE60` for green)  
→ Check dark mode detection in initUI() is working

**Issue**: SpeedView not animating  
→ Verify speedView.speedTo(bmi) is called with valid float  
→ Check SpeedView library version compatibility (1.6.0)

---

## External Dependencies & Integration

- **SpeedViewLib**: Auto-animating gauge widget; used via `binding.speedView` ViewBinding reference
- **AndroidX Lifecycle**: Provides ViewModel & LiveData; automatically retained across activity recreations
- **Material Design**: Provides standard UI components for consistency

