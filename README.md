# Android Messaging SDK v5.13.0

Android Mobile Messaging SDK version 5.13.0 release includes new features, enhancements and bug fixes.

## New features

### Capability to turn on/off voice sharing at a run-time
- Provide API to enable/disable audio messaging at a run-time by using [enable_voice_sharing](mobile-app-messaging-sdk-for-android-sdk-attributes-5-0-and-above.html#enable_voice_sharing) configuration.
```java
LPConfig.set(R.bool.enable_voice_sharing, true)
// Or
LPConfig.set(R.bool.enable_voice_sharing, false)
```

### Capability to clear all unread badge counts from Pusher irrespective of the current conversation displayed
- Expose a branding/configuration of type boolean ([lp_pusher_clear_badge_count](mobile-app-messaging-sdk-for-android-sdk-attributes-5-0-and-above.html#lp_pusher_clear_badge_count)) that when set by host/brand app, SDK will determine whether to clear all unread badge counts from Pusher.

## Accessibility enhancements
Improvements on the TalkBack accessible experience for vision-impaired users.

## Bugs fixed
- Agent typing indicator isn't removed on conversation close.
- Consumer typos render as links.

Please note, the SDK can be integrated to the app using Gradle dependency. For more information please refer to [the developers community](https://developers.liveperson.com/android-quickstart.html).

Full release notes can be found [here](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-latest-release-notes.html).
