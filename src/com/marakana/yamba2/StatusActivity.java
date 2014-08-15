package com.marakana.yamba2;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends BaseActivity {
	private static final String TAG = "StatusActivity";
	EditText editText;
	Button updateButton;
	Twitter twitter;
	TextView textCount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);
		
		editText = (EditText) findViewById(R.id.editText);
		updateButton = (Button) findViewById(R.id.buttonUpdate);
		
		updateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String status = editText.getText().toString();
				
				if(status.length() > 0){
					new PostToTwitter().execute(status);
					Log.d(TAG, "Button clicked");
				}
			}
		});
		
		textCount = (TextView) findViewById(R.id.textCount);
		textCount.setText(Integer.toString(140));
		textCount.setTextColor(Color.GREEN);
		
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable statusText){
				int count = 140 - statusText.length();
			    textCount.setText(Integer.toString(count));
			    textCount.setTextColor(Color.GREEN);
			    
			    if(count < 10){
			      textCount.setTextColor(Color.YELLOW);
			    }
			    
			    if(count < 0){
			      textCount.setTextColor(Color.RED);
			    }
			}
		});
	}

	class PostToTwitter extends AsyncTask<String, Integer, String>{

		@Override
		protected String doInBackground(String... statuses) {
			try{
				Twitter.Status status = ((YambaApplication) getApplication()).getTwitter().updateStatus(statuses[0]);
				return status.text;
			}
			catch(TwitterException e){
				Log.e(TAG, e.toString());
				e.printStackTrace();
				return "Failed to post";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			editText.setText("");
			Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}
	}
}
