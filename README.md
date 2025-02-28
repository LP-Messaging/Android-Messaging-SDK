**Release date:** February 28, 2025

# Overview

Android Mobile Messaging SDK version 5.25.0 release includes new features, enhancements and bug fixes.

## Environment requirements

The Android Mobile Messaging SDK version 5.25.0 uses:

- Minimum API version 21
- Compile API version 35
- Target API version 35
- Maps SDK "com.google.android.gms:play-services-maps:18.1.0"
- Structured Content Library “com.liveperson.android:lp_structured_content:2.6.7”
- Date Picker Library “com.liveperson.android:lp-date-picker:2.2.2”
- Appointment List Library "com.liveperson.android:lp-appointment-scheduler:2.0.2"  
  (compileSdk and targetSdkVersion bumped to 35 to support Android 15)

## New features

- Added support for rich-text features in the in-app SDK. This feature is currently available only for conversations initiated through the Web experience. The new parsing, formatting, and presentation of rich-text messages apply to bot agent messages, human agent messages, and automatic messages.
- Added support for rendering and responding to [Agent Selectable Responses (previously known as IDavid)](https://knowledge.liveperson.com/secure-forms-studio-overview#Agent%20Selectable%20Responses%20(previously%20known%20as%20IDavid)).
- Changes to support Android 15. The LivePerson SDK's targetSdkVersion and compileSdkVersion are set to API 35. `ConversationActivity` is now represented correctly on Android 15.

### New configurations

- Added new a boolean branding configuration [lp_announce_new_message_content](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-sdk-attributes-5-0-and-above.html#lp_announce_new_message_content) to control TalkBack behavior for new messages. When enabled (default behaviour), TalkBack announces the content of new messages; otherwise, it announces only "New Message."
- Added new configuration for cobrowse background color [lp_cobrowse_toolbar_background_color](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-sdk-attributes-5-0-and-above.html#lp_cobrowse_toolbar_background_color).
- Added new string resource for cobrowse back/close button's description [lp_accessibility_cobrowse_back_button_description](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-string-localization-string-values.html#:~:text=lp_accessibility_cobrowse_back_button_description).
- Added new configuration for cobrowse title text color [lp_cobrowse_toolbar_title_color](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-sdk-attributes-5-0-and-above.html#lp_cobrowse_toolbar_title_color).
- Added new string resource for Toast message when device doesn't have any gallery apps [lp_photo_picker_error](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-string-localization-string-values.html#:~:text=lp_photo_picker_error).
- Added new string resource for image preview screen's title [lp_full_image_view_title](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-string-localization-string-values.html#:~:text=lp_full_image_view_title).
- Added new string resource for voice message's download button content description [lp_accessibility_audio_download_button](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-string-localization-string-values.html#:~:text=lp_accessibility_audio_download_button).
- Added new string resource for voice message's playing state [lp_accessibility_voice_message_playing_status](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-string-localization-string-values.html#:~:text=lp_accessibility_voice_message_playing_status).
- Added new string resource for announcement of bot message: [lp_accessibility_new_bot_message](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-string-localization-string-values.html#:~:text=lp_accessibility_new_bot_message).
- Added new string resource for cobrowse call's accept button content description: [lp_accessibility_accept_call_button](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-string-localization-string-values.html#:~:text=lp_accessibility_accept_call_button).
- Added new string resource for cobrowse call's decline button content description: [lp_accessibility_decline_call_button](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-string-localization-string-values.html#:~:text=lp_accessibility_decline_call_button).
- Added new string resource for cobrowse call's return to call button content description: [lp_accessibility_return_to_call_button](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-string-localization-string-values.html#:~:text=lp_accessibility_return_to_call_button).
- Added new string resource for cobrowse call's end call button content description: [lp_accessibility_end_call_button](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-string-localization-string-values.html#:~:text=lp_accessibility_end_call_button).
- Updated value for [lp_accessibility_scroll_down_indicator_description](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-string-localization-string-values.html#:~:text=lp_accessibility_scroll_down_indicator_description) string resource. Default value: "Go to recent message".
- Added new string resource for DOCX files content descriptions: [lp_accessibility_file_type_docx](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-string-localization-string-values.html#:~:text=lp_accessibility_file_type_docx)
- Added new string resource for PPTX files content descriptions: [lp_accessibility_file_type_pptx](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-string-localization-string-values.html#:~:text=lp_accessibility_file_type_pptx)
- Added new string resource for XLSX files content descriptions: [lp_accessibility_file_type_xlsx](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-string-localization-string-values.html#:~:text=lp_accessibility_file_type_xlsx)
- Added new string resource for PDF files content descriptions: [lp_accessibility_file_type_pdf](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-string-localization-string-values.html#:~:text=lp_accessibility_file_type_pdf)
- Added new string resource to announce scroll down indicator button hint [lp_accessibility_scroll_down_indicator_hint](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-string-localization-string-values.html#:~:text=lp_accessibility_scroll_down_indicator_hint). Default value: "scroll to most recent message".
- Added new string resource for file/image message's download button click action: [lp_accessibility_action_download](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-string-localization-string-values.html#:~:text=lp_accessibility_action_download)
- Added new string resource for file/image message's open button click action: [lp_accessibility_action_open](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-string-localization-string-values.html#:~:text=lp_accessibility_action_open)
- Resource `lp_title_cobrowse` is now deprecated and removed. Please, use new string resources for voice call [voice_cobrowseInvitationHeading](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-string-localization-string-values.html#:~:text=voice_cobrowseInvitationHeading) and video call [video_cobrowseInvitationHeading](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-string-localization-string-values.html#:~:text=video_cobrowseInvitationHeading).
- Resource `lp_accessibility_voice` is now deprecated and removed. Please, use new string resource [lp_accessibility_voice_message](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-string-localization-string-values.html#:~:text=lp_accessibility_voice_message) for content description of voice messages.

## Enhancements

- Improved accessibility behavior for focussing on messages.
- Improved accessibility behavior for headings.
- Improved keyboard focusability for SDK components.
- Updated documentation for date picker, appointment list and structured content configurations.
- Updated documentation for accessibility keys.
- Improved translations. Added missed supported languages for date picker, appointment list and structured content.
- Migrated internal components interaction callbacks with Fragment Result Api.
- Migrated to a newer kotlin version (1.8.21).
- Migrated DatePicker component's screen from activity for fragment. 
- Updated proguard rules page.
- Added new [logOut](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-sdk-apis-messaging-api.html#logout) API where it has LPAuthenticationType as an input.
- Introduced the [LPNotificationType](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-sdk-apis-interface-and-class-definitions.html#notificationtype) property on the [PushMessage](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-sdk-apis-interface-and-class-definitions.html#pushmessage) object to detect the type of push notification, such as REGULAR, PROACTIVE, or C2M.

## Bugs fixed

- Fixed an issue with broken image icon appearance when consumer sends a document file.
- Fixed an issue with representation of multiple types of links within one message.
- Fixed crash on Samsung devices when user enters Picture-in-Picture mode and Talkback accessibility service is enabled.
- Fixed rendering of quick replies for bot messages.
- Fixed keyboard focusability for Co-Browse invitation buttons.
- Fixed keyboard focusability for secure form message.
- Fixed content description of download voice message.
- Fixed accessibility announcement of structured content message sent by bot.
- Fixed keyboard focusability for file messages.
- Fixed crash when user tries to select an image from gallery, but no gallery apps are installed on device.
- Fixed issue with keyboard and accessibility focus when focus is placed behind feedback, Co-Browse, image preview or caption preview screen.
- Fixed announcement of empty text elements without tooltip and actions inside structured content. Such elements are not accessible by Talkback starting from this version.
- Fixed record voice button appearance when LP SDK conversation screen is moved to foreground.
- Fixed accessibility announcement of Co-Browse events. Prevented missing of "Co-Browse call ended" event.
- Fixed auto scroll to quick replies of welcome message.
- Internal bug fixes.

Please note, the SDK can be integrated to the app using Gradle dependency. For more information please refer to [the developers community](https://developers.liveperson.com/android-quickstart.html).