**Release date:** February 09, 2024

# Overview

Android Mobile Messaging SDK version 5.20.1 release includes new features, enhancements, and bug fixes.

## Environment requirements

The Android Mobile Messaging SDK version 5.20.1 uses:

- Minimum API version 21
- Compile API version 34
- Target API version 34
- Maps SDK "com.google.android.gms:play-services-maps:18.1.0"
- Structured Content Library “com.liveperson.android:lp_structured_content:2.6.3”
- Date Picker Library “com.liveperson.android:lp-date-picker:2.1.0”
- Appointment List Library "com.liveperson.android:lp-appointment-scheduler:2.0.0"

## New features

### Offline messaging

Offline messages are messages that will be sent once connection with the server is established. To enable offline messages you need to set the lp_is_offline_messaging_enabled flag to true..

### Speech to text recognition

Brands can customize the welcome message when starting a new conversation without releasing a new version of the host application.


## Enhancements

- Separator for auto-closed conversation;
- Get rid of deprecated permissions;
- Add styles for Quick Reply of Welcome message;
- Added internal logging metrics (OpenTelemetry) to collect SDK performance data. This feature is turned off by default, and we only enable sampling data collection for specific brands in response to performance enhancement request.

## Bugs fixed

- NullPointerException when upgrading SDK version;
- Internal bug fixes.

Please note, the SDK can be integrated to the app using Gradle dependency. For more information please refer to [the developers community](https://developers.liveperson.com/android-quickstart.html).
