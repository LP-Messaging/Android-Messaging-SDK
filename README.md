# Android Messaging SDK v5.12.0

Android Messaging SDK version 5.12.0 release includes support for Android 13, provides improved accessibility support, bug fixes and enhancements.

## Features:
### SDK File Sharing Thumbnail Obfuscation
SDK now supports file sharing image thumbnail obfuscation to protect PII of a consumer.

## New Permissions

To support SDK [Photo and File sharing feature](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-advanced-features-photo-and-file-sharing.html) on Android 13 and above, a new permission has been introduced:

```xml
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

## Bugs Fixed:
- Phone number sent with other text in the message are not marked as link.
- Failed to detect email address in a message.
- SDK fails to display link preview for some of the valid links.

## Accessibility enhancements
Improvements on the TalkBack accessible experience for vision-impaired users.

Please note, the SDK can be integrated to the app using Gradle dependency. For more information please refer to [the developers community](https://developers.liveperson.com/android-quickstart.html).

Full release notes can be found [here](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-latest-release-notes.html).
