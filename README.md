# TurtEyes

## Overview
The Sea Turtle Hatchling Protection System mobile application `TurtEyes` is designed to interface with the Automated Sea Turtle Hatchling Protection Project. This application supports in the automated detection and protection of Loggerhead (Caretta caretta) and Green (Chelonia mydas) turtle hatchlings on Akdeniz Beach, TRNC.

## Purpose
The application is designed to integrate with the hardware and software components of the sea turtle hatchling protection system. It helps monitor and protect turtle hatchlings by providing real-time notification based updates on their status and location, ensuring prompt action can be taken if needed.

## Features
- Device Management: The application connects to Firebase Realtime Database to dynamically list and manage devices involved in the hatchling protection system.
- Real-Time Data Fetching: Utilizes asynchronous tasks to fetch and display sensor data and GPS location updates related to hatchling detection.
- User Interface: Provides a user-friendly interface with dynamic button creation for device interaction and real-time status updates on hatchling protection.
- Error Handling: Displays error messages if network issues are encountered or if the data cannot be retrieved.
- Data Visualization: Shows GPS location and hatchling status, with live updates and links to Google Maps for location visualization.

## Components

### MainActivity

The main activity handles the device list, which is fetched from Firebase Realtime Database. It dynamically creates buttons for each device, allowing users to select a device and view its data.

### FetchGpsDataTask

An `AsyncTask` that fetches GPS data from a specified server URL. It handles SSL configuration, fetches data, and parses latitude and longitude from JSON responses. It also constructs a Google Maps URL for displaying the location.

### FetchSensorDataTask

An `AsyncTask` that fetches sensor data from a specified server URL. It processes the server response and updates the app UI accordingly.

### StatsActivity

This activity displays GPS location and hatchling status using data provided by the `StatsViewModel`.

### StatsViewModel

A ViewModel that handles fetching and managing sensor and GPS data. It interacts with `FetchGpsDataTask` and `FetchSensorDataTask` to get data and updates the LiveData objects used by `StatsActivity`.

### SSLUtils

A utility class to configure SSL settings, providing an `SSLContext` that trusts all certificates. This is mainly for testing purposes.

## Setup

1. **Clone the Repository**

   ```sh
   git clone https://github.com/Has-sh/ToDoListApp.git
   cd ToDoListApp
   ```

2. **Open the Project**

   Open the project in Android Studio.

3. **Configure Firebase**

   - Go to the [Firebase Console](https://console.firebase.google.com/).
   - Create a new project or use an existing one.
   - Add your Android app to the Firebase project and download the `google-services.json` file.
   - Place the `google-services.json` file in the `app/` directory of your project.

4. **Update Server URLs**

   In `MainActivity`, update `ServerUrl` with your actual server URL:
   ```java
   private static String ServerUrl = "http://YOUR_SERVER_URL";
   ```

5. **Build and Run**

   - Sync your project with Gradle files.
   - Build and run the app on an Android device or emulator.

## Usage

- **MainActivity**: Swipe down to refresh the device list. Tap a device button to view its GPS location and sensor status.
- **StatsActivity**: Displays the current GPS location and hatchling status. The data updates based on the provided ViewModel.

## Acknowledgements

- Firebase Realtime Database
- Google Maps API
- Android SDK
