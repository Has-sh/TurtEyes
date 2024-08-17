# TurtEyes

## Overview

This Android application fetches and displays GPS and sensor data from a server. It uses Firebase for real-time data synchronization and includes asynchronous tasks to fetch data from specified endpoints. The app displays the GPS location on Google Maps and shows sensor status updates. 

## Features

- Fetches GPS and sensor data from a server.
- Displays GPS location using Google Maps.
- Real-time updates of hatchling status.
- Swipe-to-refresh to update device list from Firebase.
- Customizable error handling and UI updates.

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

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome. Please submit issues and pull requests on the [GitHub repository](https://github.com/Has-sh/ToDoListApp).

## Acknowledgements

- Firebase Realtime Database
- Google Maps API
- Android SDK

## Contact

For any questions or feedback, please contact Muhammad Hashaam Shahid at [email@example.com](mailto:email@example.com).
```

Feel free to modify the sections based on your specific needs or additional details.
