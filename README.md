**Release date:** March 15, 2024

# Overview

Android Mobile Messaging SDK version 5.20.2 release includes enhancements and bug fixes.

## Environment requirements

The Android Mobile Messaging SDK version 5.20.2 uses:

- Minimum API version 21
- Compile API version 34
- Target API version 34
- Maps SDK "com.google.android.gms:play-services-maps:18.1.0"
- Structured Content Library “com.liveperson.android:lp_structured_content:2.6.4”
- Date Picker Library “com.liveperson.android:lp-date-picker:2.1.0”
- Appointment List Library "com.liveperson.android:lp-appointment-scheduler:2.0.0"

## Enhancements

- Support alternatives to swiping in the carousel component for accessibility users. Check [here](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-sdk-attributes-5-0-and-above.html#structured-content-carousel-navigation-buttons) for more info.
- Replace accessibility configuration [`lp_announce_events_sequentially`](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-sdk-attributes-5-0-and-above.html#lp_announce_events_sequentially) by [`lp_announce_events_with_live_region`](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-sdk-attributes-5-0-and-above.html#lp_announce_events_with_live_region).
- Accessibility improvement on announcing newly received messages.

## Bugs fixed

- Fixed [INVALID_SDK_VERSION](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-sdk-apis-callbacks-index.html#lperror-enum) callbacks takes long time to fire [LP_ON_ERROR_TYPE_INTENT_ACTION](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-sdk-apis-callbacks-index.html#multi-type-error-indication).
- Added custom implementation of FileProvider to prevent collision with libraries and app that are relying on default FileProvider.
- Internal bug fixes.

Please note, the SDK can be integrated to the app using Gradle dependency. For more information please refer to [the developers community](https://developers.liveperson.com/android-quickstart.html).
