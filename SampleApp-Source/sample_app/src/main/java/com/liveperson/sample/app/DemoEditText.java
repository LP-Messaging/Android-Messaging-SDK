package com.liveperson.sample.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

/**
 * Demo implementation of host app edit text
 * Here you can use your own logic, such as show a custom view instead of
 * the native keyboard
 */
public class DemoEditText extends EditText {

    private static final String TAG = DemoEditText.class.getSimpleName();

    public DemoEditText(Context context) {
        super(context);
        Log.i(TAG, "DemoEditText(Context context)");
    }

    public DemoEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "DemoEditText(Context context, AttributeSet attrs)");
    }

    public DemoEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i(TAG, "DemoEditText(Context context, AttributeSet attrs, int defStyleAttr)");
    }

    @TargetApi(21)
    public DemoEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Log.i(TAG, "DemoEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)");
    }
}
