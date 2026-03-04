package com.liveperson.sample.app.activities;

import static com.liveperson.sample.app.utils.InsetsUtilsKt.applyInsets;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.liveperson.sample.app.R;

/**
 * This activity is used to demonstrate the deep link from structured content link action
 */
public class DeepLinkActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deep_link);
		applyInsets(this);

		// Get the data from the intent
		Intent intent = getIntent();
		String action = intent.getAction();
		Uri data = intent.getData();

		// Display the URI
		TextView pathTextView = findViewById(R.id.deep_link_path);
		pathTextView.setText(data.toString());
	}
}
