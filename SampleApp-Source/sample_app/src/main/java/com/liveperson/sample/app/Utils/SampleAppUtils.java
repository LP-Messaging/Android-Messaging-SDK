package com.liveperson.sample.app.Utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.liveperson.sample.app.FragmentContainerActivity;
import com.liveperson.sample.app.MainActivity;
import com.liveperson.sample.app.push.RegistrationIntentService;

/**
 * ***** Sample app class - Not related to Messaging SDK ****
 * Utils class that we use in the sample app only.
 * All these methods are just to have the {@link MainActivity} and {@link FragmentContainerActivity}
 * simple as possible.
 */
public class SampleAppUtils {


    /**
     * Validate that the text field is not empty
     *
     * @return true in case there is a string, false otherwise
     */
    public static boolean isAccountEmpty(TextView tv, Context ctx) {
        String account = tv.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            Toast.makeText(ctx, "Account field can't be empty!", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }


    /**
     * Enable a button and change the text
     * @param btn - the button to enable
     * @param enabledText - the text we want to show on the button
     */
    public static void enableButtonAndChangeText(Button btn, String enabledText){
        btn.setText(enabledText);
        btn.setEnabled(true);
    }


    /**
     * Disable a button and change the text
     * @param btn - the button to enable
     * @param disabledText - the text we want to show on the button
     */
    public static void disableButtonAndChangeText(Button btn, String disabledText){
        btn.setText(disabledText);
        btn.setEnabled(false);
    }


    /**
     * Call to the {@link RegistrationIntentService} class which was taken from Google's
     * sample app for GCM integration
     */
    public static void handleGCMRegistration(Context ctx) {
        Intent intent = new Intent(ctx, RegistrationIntentService.class);
        ctx.startService(intent);
    }

}
