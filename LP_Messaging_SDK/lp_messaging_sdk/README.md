# LPMessagingSDK-Android

This read.me include the quick start guide for Android project. Find the full documentation, including Java guide in the integration guide.

## Configure project settings to connect LiveEngage SDK

#### Import SDK
- Download the latest Messaging SDK and unzip it.
- Import LP_Messaging_SDK/lp_messaging_sdk Module to your project.  (In the Android Studio menu bar select: File  →  New  →  Import module)

    This module contains:
    - LivePerson.java - Main entry point for the Messaging SDK
    - Resources (.aars files)
- Add the following lines to the build.gradle of your app
```sh
  repositories {
        ...
        flatDir {
            dirs project(':lp_messaging_sdk').file('aars')
        }
    }
```
```sh
dependencies {
    ...
    compile project(':lp_messaging_sdk')
}
```

Add the following permission to your app’s AndroidManifest.xml file:
```sh
   <uses-permission android:name="android.permission.INTERNET" />
   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

#### Initialization
Add this code to initialized the SDK:
```sh
String brandID = "Your-Liveperson-Account-Id-String";
String appID = "your-app-package-name"
LivePerson.initialize(context, new InitLivePersonProperties( brandID, appID,
     new InitLivePersonCallBack() {
        @Override
        public void onInitSucceed() {
        }
        @Override
        public void onInitFailed(Exception e) {
        }
    }
);
```
- brandID - is your liveperson account id, if you don’t have one please contact your LivePerson representative.
- appID - your app id, used for registering LP pusher service.
- onInitSuccess - Callback that indicates the init process has finished successfully.
- onInitFailed - Callback that indicates the init process has failed.
>  **Note:** You can call initialize before showing LivePerson's Activity/Fragment, but it's recommended to initialized the SDK in your app's Application class.

###### Once initialization completed (onInitSucceed) you can call LivePerson methods.
###### The SDK supports 2 operation modes: Activity and Fragment. For more info about each mode check out the integration guide.
To start LivePerson's Activity mode:
```sh
LivePerson.showConversation(Activity activity);
```
Or in case of authenticated mode:
```sh
LivePerson.showConversation(Activity activity, String authKey);
```
To start LivePerson's Fragment mode: (Attach the returned fragment to a container in your activity) :
```sh
LivePerson.getConversationFragment();
```
Or in case of authenticated mode:
```sh
LivePerson.getConversationFragment(String authKey);
```
> When using fragment mode, you could use the provided SDK callbacks in your app in order to implement functionalities such as menu items, action bar indications, agent name, and typing indicator.

#### LivePerson Callbacks Interface
The SDK provides a callback mechanism to keep the host app updated on events related to the conversation. for more information about each callback check out the integration guide.
```sh
public interface LivePersonCallback{
    void onError(TaskType type, String message); void onTokenExpired();
    void onConversationStarted();
    void onConversationResolved();
    void onConnectionChanged(boolean isConnected); void onAgentTyping(boolean isTyping);
    void onAgentDetailsChanged(AgentData agentData); void onCsatDismissed();
    void onCsatSubmitted(String conversationId);
    void onConversationMarkedAsUrgent();
    void onConversationMarkedAsNormal();
    void onOfflineHoursChanges(boolean isOfflineHoursOn);
}
```

**Check out the Deployment guide for full information**

LIVEPERSON DEVELOPER LICENSE FOR SDK
----
By installing, accessing, downloading, or otherwise using the LP Messaging software development kit (“SDK”) and any related software code provided by LivePerson on Github’s website, you and your company, in consideration of the mutual agreements contained herein and intending to be legally bound hereby accept the terms of this developer license agreement (“License Agreement”) and agree to be bound the License Agreement.

License. LivePerson grants you and your company a limited, revocable, non-exclusive, non-transferable, non-sublicensable license to access and use the SDK solely for testing, development and non-production use only. You may not sell, sublicense, rent, loan or lease any portion of the SDK to any third party and you may not reverse engineer, decompile or disassemble any portion of the SDK. Prior to your and your company’s use of the SDK for commercial purposes, including in a production environment, you and your company must notify LivePerson and mutually agree in writing on the applicable pricing and license terms for such usage.

Term. This License Agreement is effective until terminated. LivePerson has the right to terminate this agreement immediately if you fail to comply with any term herein. Upon any such termination, you and your company must remove all full and partial copies of the SDK from your computer, network, and servers and discontinue use of the SDK.

Proprietary Rights. LivePerson retains all proprietary rights in and to the SDK, including know how, technologies, and trade secrets, and all derivative works, and enhancements and modifications to the foregoing. Except as stated in the above license, this License Agreement does not grant you and/or your company any rights to patents, copyrights, trade secrets, trademarks, or other rights with respect to the SDK.

Disclaimer of Warranty. With respect to your and your company’s testing, development, and non-production use of the SDK, LivePerson provides the SDK “as is.” To the maximum extent permissible under applicable law, LivePerson expressly disclaims all representations and warranties, whether express or implied warranties, concerning or related to the SDK, including but not limited to the implied warranties of non-infringement and/or fitness for a particular purpose. LivePerson does not warrant, guarantee, or make any representations regarding the use, the results of the use or the benefits, of the SDK. Your commercial and production use of the SDK will be governed the mutually agreed upon commercial agreement in writing with LivePerson.
