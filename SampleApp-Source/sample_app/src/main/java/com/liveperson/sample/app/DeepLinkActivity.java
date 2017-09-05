package com.liveperson.sample.app;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * This activity is used to demonstrate the deep link from structured content link action
 */
public class DeepLinkActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deep_link);

		// Get the data from the intent
		Intent intent = getIntent();
		String action = intent.getAction();
		Uri data = intent.getData();

		// Display the URI
		TextView pathTextView = (TextView) findViewById(R.id.deep_link_path);
		pathTextView.setText(data.toString());
	}
}
