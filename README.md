**Release date:** July 28, 2023

# Android Messaging SDK v5.17.0

Android Mobile Messaging SDK version 5.17.0 release includes new features, enhancements and bug fixes.

## Environment requirements

The Android Mobile Messaging SDK version 5.17.0 uses:

- Minimum API version 21
- Compile API version 33
- Target API version 33
- Maps SDK "com.google.android.gms:play-services-maps:18.1.0"
- Structured Content Library “com.liveperson.android:lp_structured_content:2.6.1”
- Date Picker Library “com.liveperson.android:lp-date-picker:2.1.0”
- Schedule Slot List Library "com.liveperson.android:lp-appointment-scheduler:2.0.0"

## New features
### Enable initial message to be shown to agent. 
Giving agent the context of the first/initial message to be able to continue a conversation without asking consumers to explain their reason for reaching out.

### More control over the way SDK displays the PII data to consumers.
Brands can decide how long their consumers can view the PII data before SDK applies masking to the messages.  
#### How to configure
- Site settings: messaging.consumer.unmasked.conversation.after.closed.minutes  

| Value (minute)        | Description              |
|-----------------------|--------------------------|
| -1 (Default)          | Pull closed conversation(s) from UMS if available (unmasked data) |
| 0                     | Pull closed conversation(s) from [MIA](https://developers.liveperson.com/messaging-interactions-api-overview.html) (masked data) |
| 1 → 20160 (14 days)   | Validate to decide source (UMS/MIA) of closed conversation(s) |


## Enhancements
- Enable or disable queue new message announcement. See [lp_announce_events_sequentially configuration](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-sdk-attributes-5-0-and-above.html#lp_announce_events_sequentially).
- Enable announce_agent_typing configuration in Fragment mode. See [announce_agent_typing configuration](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-sdk-attributes-5-0-and-above.html#announce_agent_typing).

## Bugs fixed
- Missing content on horizontal structured content message.
- Show link preview for PDF link incorrectly.
- Internal bug fixes.

Please note, the SDK can be integrated to the app using Gradle dependency. For more information please refer to [the developers community](https://developers.liveperson.com/android-quickstart.html).

Full release notes can be found [here](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-latest-release-notes.html).
