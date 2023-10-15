**Release date:** October 13, 2023

# Overview

Android Mobile Messaging SDK version 5.18.0 release includes new features, enhancements and bug fixes.

## Environment requirements

The Android Mobile Messaging SDK version 5.18.0 uses:

- Minimum API version 21
- Compile API version 33
- Target API version 33
- Maps SDK "com.google.android.gms:play-services-maps:18.1.0"
- Structured Content Library “com.liveperson.android:lp_structured_content:2.6.2”
- Date Picker Library “com.liveperson.android:lp-date-picker:2.1.0”
- Schedule Slot List Library "com.liveperson.android:lp-appointment-scheduler:2.0.0"

## New features

### Support PKCE in addition to OAuth2 code flow for Consumer SSO 
Support PKCE in addition to OAuth2 code flow to be used in the SDK now. It adds a new function to get the required parameters for the PKCE flow - `getPKCEParams()`.

### Customize welcome message for specific consumer targeting
Brands can customize the welcome message when starting a new conversation without releasing a new version of the host application. Document can be found [here](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-advanced-features-dynamic-welcome-message.html).

## Enhancements
- A11y - Add heading role for all date headers in conversation sation list.
- Update to use more secure cipher transformation.

## Bugs fixed

- Update alert dialog buttons when using material dialog theme.
- Internal bug fixes.

Please note, the SDK can be integrated to the app using Gradle dependency. For more information please refer to [the developers community](https://developers.liveperson.com/android-quickstart.html).