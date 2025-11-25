# GPA Calculator - JavaFX Application

A desktop application built with JavaFX to help students calculate their Grade Point Average (GPA) by entering courses, credits, and grades. The app supports weighted GPA calculation, editing/deleting entered courses, and exporting results into a text file, persistent storage using SQLite, real-time updates with ObservableLists, JSON import/export of result, and concurrent database operations for smooth performance.

---

## Features

- Set **Required Credits** before adding courses.
- Add courses with the following details:
  - Course Name
  - Course Code
  - Credit Hours
  - Teachers
  - Grade
- Edit or delete previously added courses.
- Weighted GPA calculation: GPA = sum(credit × grade points) / total credits.
- Reset all data to start fresh.
- Export course list and GPA to a text file.
- JSON import/export for saving or sharing course and result data.
- Persistent storage with SQLite database, ensuring data is saved between sessions.
- Real-time UI updates using ObservableLists in TableView.
- Concurrency: Database operations run on a separate thread to keep the UI responsive.


## Usability & Interactivity

- Clear navigation between scenes.
- Displays alerts or confirmation.
- GPA calculation button is disabled until required credits are reached.
- Uses Validation messages for missing or invalid course data.
- Smooth user experience with concurrent database operations.

---
