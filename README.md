# Android Messaging SDK v5.8.0

In-App Messaging SDK v5.8.0 for Android includes the the following:
- Schedule Slot List allows brand agents to send the Structured Content to consumers to share available appointment slots within in-app messaging.
- Date Picker now supports Dark Mode.
- Android 12 Support.

# Bugs Fixed:
- Crash on initialization/logout.
- Deep link fails to open.
- Secure form self closed after returning to the app.
# Enhancements:
- When the conversation comes from background to foreground, instead of always requesting authCode from IDP, SDK will check if it has the token (LP_JWT), then connect to UMS and let UMS do the expiration check. If the token is not available, then request authCode before connecting to UMS.
- Support markdown hyperlink in controller bot message.

Please note, the SDK can be integrated to the app using Gradle dependency. For more information please refer to [the developers community](https://developers.liveperson.com/android-quickstart.html).

Full release notes can be found [here](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-latest-release-notes.html).
