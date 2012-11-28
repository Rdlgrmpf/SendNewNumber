package de.rdlgrmpf.sendnewnumber;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SendActivity extends Activity {
	
	private static final String TAG ="SendNewNumber/Send";

	Button buttonSend;
	Button buttonFinish;
	TextView log;
	EditText message;
	TextView mCounter;
	ArrayList<String> mNumbers;
	SmsManager mSmsManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send);
		
		if (getIntent().getExtras() != null){
			mNumbers = getIntent().getExtras().getStringArrayList(MainActivity.NUMBER_LIST);
		} else 
			mNumbers = null;
		
		log = (TextView) findViewById(R.id.textViewLog);
		message = (EditText) findViewById(R.id.textViewMessage);
		mCounter = (TextView) findViewById(R.id.textViewCounter);
		mCounter.setText("" + message.getText().length());
		message.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mCounter.setText("" + message.getText().length());
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		buttonSend = (Button) findViewById(R.id.buttonSend);
		buttonFinish = (Button) findViewById(R.id.buttonFinish);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_send, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_about:
			final Intent intent = new Intent(this, AboutActivity.class);
    		startActivity(intent);
    		break;
    	
    	default:
		}
		return super.onOptionsItemSelected(item);
	}
	
	/** Button Methods */
	
	/** Called when the user clicks the Send button */
	public void buttonSendMethod(View v){
		mSmsManager = SmsManager.getDefault();
		for(String number : mNumbers){
			//mSmsManager.sendTextMessage(number, null, message.getText().toString(), null, null);
			log.append("\nsent to: " + number);
			Log.i(TAG, "\nsent to: " + number);
		}
		
		buttonSend.setVisibility(View.INVISIBLE);
		buttonFinish.setVisibility(View.VISIBLE);
		
	}
	
	/** Called when the user clicks the Finish button */
	public void buttonFinishMethod(View v){
		finish();
	}
	
	
	
}
