# Changelog

All notable changes to Highway Star will be documented in this file.

## [2.0.0] - 2025-11-11

### Added

#### Architecture
- **Models Package**: Clean data models for Activity, UserProfile
- **Services Package**: Business logic layer with singleton services
  - `FirebaseService`: Centralized Firebase connection management
  - `AuthService`: Authentication with proper validation
  - `ActivityService`: Full CRUD operations for activities
  - `ProfileService`: User profile management and statistics
  - `GeoLocationService`: IP-based geolocation using MaxMind GeoIP2
- **Utils Package**: Reusable utility classes
  - `ValidationUtils`: Email and password validation with strength checking
  - `DistanceCalculator`: Distance, pace, and calorie calculations
  - `DateUtils`: Date formatting and manipulation
  - `Config`: Configuration management system
- **UI Package**: Restructured UI with modern components
  - `MainFrame`: Single window with CardLayout navigation
  - Panel-based architecture for different screens
  - Reusable components (StatsCard, ActivityTypeSelector)

#### Features
- **Enhanced Authentication**
  - Real-time email validation
  - Password strength indicator (Very Weak to Very Strong)
  - Proper error messages and user feedback
  - Input validation before submission

- **Activity Types**
  - Walk (0.8m per step)
  - Run (1.2m per step)
  - Cycle (15m per second)
  - Hike (0.9m per step)
  - Type-specific distance calculations

- **Dashboard**
  - Live statistics cards showing:
    - Total Steps
    - Total Distance
    - Total Activities
    - Current Streak
  - Activity type selector with icons
  - Real-time activity tracking with timer
  - Interactive map with user location

- **Activity Tracking**
  - Real-time duration tracking
  - Step counting via spacebar
  - Distance calculation based on activity type
  - Pace calculation (min/km or min/mile)
  - Route point recording
  - Activity notes

- **Activity History**
  - Comprehensive activity list with all details
  - Filter by time period:
    - All Time
    - Last 7 Days
    - Last 30 Days
    - This Month
  - Click to view detailed activity information

- **Activity Details**
  - Full activity statistics
  - Route visualization on map
  - Edit activity notes
  - Delete activities
  - Calorie estimation based on user weight

- **Profile Management**
  - Display user information (email, member since)
  - Update personal details:
    - Display name
    - Weight (for calorie calculation)
    - Height
  - Comprehensive statistics display
  - Achievement badges visualization
  - Change password with validation

- **Achievements System**
  - First Activity (🎯)
  - 10,000 Steps (👟)
  - 100,000 Steps (💯)
  - 7-Day Streak (🔥)
  - 30-Day Streak (⭐)
  - Marathon Distance - 42.195km (🏃)

- **Settings Panel**
  - User preferences:
    - Unit system (Metric/Imperial)
    - Default map zoom level
    - Daily step goal
    - Auto-pause toggle
    - Theme selection (Light/Dark)
  - Goal tracking with progress bar
  - Data management

- **Data Export**
  - Export activities to CSV format
  - Export activities to JSON format
  - Complete activity history with all metrics

- **IP-based Geolocation**
  - Automatic location detection on startup
  - Map centers to user's approximate location
  - Falls back to Singapore if geolocation unavailable

- **Modern UI**
  - FlatLaf theme integration
  - Clean, modern component design
  - Better color scheme and typography
  - Improved spacing and layout
  - Smooth transitions between panels

#### Technical Improvements
- **Asynchronous Operations**: CompletableFuture for non-blocking Firebase calls
- **Proper Error Handling**: Try-catch blocks with user-friendly error messages
- **Input Validation**: Pre-submission validation with real-time feedback
- **Singleton Pattern**: Services use singleton pattern for resource management
- **CardLayout Navigation**: No more window disposal, smooth panel transitions
- **Separation of Concerns**: Clear boundaries between layers
- **Firebase Integration**: Proper data persistence for all features
- **Route Tracking**: Store and visualize activity routes
- **Streak Calculation**: Automatic tracking of consecutive activity days

### Changed
- **Main.java**: Simplified initialization with proper error handling
- **Navigation**: From multiple JFrame windows to single window with CardLayout
- **Data Persistence**: All activities and profiles now properly saved to Firebase
- **Password Handling**: Improved validation and strength checking
- **Map Implementation**: Better default location handling

### Fixed
- **Authentication**: Password is now validated (previously only checked email existence)
- **Activity Persistence**: Activities are now saved to Firebase (previously only in-memory)
- **History Window**: Connected to real Firebase data (previously hardcoded dummy data)
- **Step Counting**: Total steps properly tracked across sessions
- **Profile Stats**: Automatically updated after each activity
- **Window Disposal**: No more window flashing with CardLayout navigation

### Removed
- Old window classes (AuthWindow, MainWindow, HistoryWindow, ProfileWindow)
- Hardcoded activity data
- Direct Firebase database access from UI components

## [1.0.0] - Initial Release

### Added
- Basic authentication (email only, no password verification)
- Simple activity tracking with spacebar
- Static map view at Singapore coordinates
- Basic profile window
- In-memory step counting
- Hardcoded activity history

---

## Migration Notes

### From v1.0.0 to v2.0.0

**Database Schema Changes:**
- User data now stored under `/users/{uid}/profile/`
- Activities stored under `/users/{uid}/activities/{activityId}/`
- Each activity includes: type, duration, steps, distance, route, notes
- Profile includes: personal info, preferences, achievements, statistics

**Code Structure:**
- Old window classes moved to `.old` extension
- New panel-based architecture in `ui/panels/`
- All Firebase operations moved to service layer
- Business logic extracted from UI components

**Configuration:**
- Firebase URL now in Config.java
- Can be overridden with app.properties file
- GeoIP database optional but recommended

**Breaking Changes:**
- Old activities (if any existed) may not be compatible
- Recommend fresh Firebase database for v2.0.0

---

**Note**: This was a complete rewrite maintaining the core concept while implementing production-ready features and architecture.
