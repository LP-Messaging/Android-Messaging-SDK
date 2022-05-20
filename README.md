# Android Messaging SDK v5.10.0

Android Mobile Messaging SDK version 5.10.0 release includes step-up authentication feature and enhancements.


# New Features:
### Step Up Authentication
Step up authentication allows brands to let their consumers continue the ongoing unauthenticated conversations after logging in and merging it to the authenticated conversation history.

### Custom proactive welcome message for Non-Rich content
With the support of Non-Rich content payloads, brands will be able to send "text" type proactive content messages to their consumers.

# New Attributes:
### [lp_enable_timestamps](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-sdk-attributes-5-0-and-above.html#lp_enable_timestamps)
Show or hide the timestamp text of the conversation message bubbles.

### [lp_enable_read_receipts](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-sdk-attributes-5-0-and-above.html#lp_enable_read_receipts)
Show or hide read receipt text of the consumer message bubbles.

### [lp_timestamps_font_size](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-sdk-attributes-5-0-and-above.html#lp_timestamps_font_size)
Update the timestamp text font size of the conversation message bubbles.

### [lp_urgency_menu_items_visible](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-sdk-attributes-5-0-and-above.html#lp_urgency_menu_items_visible)
Show or hide the "Mark as urgent" context menu option.

### [lp_resolve_conversation_menu_item_visible](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-sdk-attributes-5-0-and-above.html#lp_resolve_conversation_menu_item_visible)
Show or hide the "Mark as resolved" context menu option.

# Bugs Fixed:
- SDK displays a message body instead of message title from payload as a proactive welcome message.
- A color [configuration](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-sdk-attributes-5-0-and-above.html#consumer_bubble_message_link_text_color) value for links is being ignored from the conversation message containing a text as well as link together.
- The background color of buttons in horizontal type structured content will overlap over the structured content borders.
- When using SDK’s clear history feature, an infinite loading spinner will not get reset from the conversation window after history gets cleared.
- Accessibility only announces the new message but not the number of new messages received.


Please note, the SDK can be integrated to the app using Gradle dependency. For more information please refer to [the developers community](https://developers.liveperson.com/android-quickstart.html).

Full release notes can be found [here](https://developers.liveperson.com/mobile-app-messaging-sdk-for-android-latest-release-notes.html).
