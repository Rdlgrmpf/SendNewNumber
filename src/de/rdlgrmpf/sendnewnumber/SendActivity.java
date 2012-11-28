package de.rdlgrmpf.sendnewnumber;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Toast;

public class SendActivity extends Activity {
	
	private static final String TAG ="SendNewNumber/Send";

	Button buttonSend;
	Button buttonFinish;
	TextView log;
	EditText message;
	TextView mCounter;
	TextView sentCounter;
	ArrayList<String> mNumbers;
	SmsManager mSmsManager;
	
	private int totalSMS = 0;
	private int sentSMS = 0;
	private int deliveredSMS =0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send);
		
		if (getIntent().getExtras() != null){
			mNumbers = getIntent().getExtras().getStringArrayList(MainActivity.NUMBER_LIST);
			totalSMS = mNumbers.size();
		} else 
			mNumbers = null;
		
		log = (TextView) findViewById(R.id.textViewLog);
		message = (EditText) findViewById(R.id.textViewMessage);
		mCounter = (TextView) findViewById(R.id.textViewCounter);
		mCounter.setText("" + message.getText().length());
		sentCounter = (TextView) findViewById(R.id.textViewSent);
		sentCounter.setText(sentSMS + "/" + totalSMS + " sent\n" + deliveredSMS + "/" + totalSMS + "delivered");
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
		
		mSmsManager = SmsManager.getDefault();
		
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
		String smsMessage = message.getText().toString();
		
		if(smsMessage.length() > 0){
			for(String number : mNumbers){		
				sendSms(number, smsMessage);
			    log.append("\nsent to: " + number);
				Log.i(TAG, "\nsent to: " + number);
			}
			buttonSend.setVisibility(View.INVISIBLE);
			buttonFinish.setVisibility(View.VISIBLE);
			
		} else {
			Toast.makeText(getBaseContext(), getString(R.string.no_message_entered), Toast.LENGTH_SHORT).show();
		}
		
		
		
	}
	
	protected void sendSms(String number, String message){
		final String SENT = "sms_sent";
		final String DELIVERED = "sms_delivered";
		
		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
		
		registerReceiver(new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				switch(getResultCode()){
				case Activity.RESULT_OK:
					sentSMS++;
					sentCounter.setText(sentSMS + "/" + totalSMS + " sent\n" + deliveredSMS + "/" + totalSMS + "delivered");
					Toast.makeText(context, getString(R.string.sms_sent), Toast.LENGTH_SHORT).show();
					break;
					
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(context, getString(R.string.sms_generic_failure), Toast.LENGTH_SHORT).show();
					break;
					
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(context, getString(R.string.sms_no_service), Toast.LENGTH_SHORT).show();
					break;
				}
				
			}
		}, new IntentFilter(SENT));
		
		registerReceiver(new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				switch(getResultCode()){
				case Activity.RESULT_OK:
					deliveredSMS++;
					sentCounter.setText(sentSMS + "/" + totalSMS + " sent\n" + deliveredSMS + "/" + totalSMS + "delivered");
					Toast.makeText(context, getString(R.string.sms_delivered), Toast.LENGTH_SHORT).show();
					break;
					
				case Activity.RESULT_CANCELED:
					Toast.makeText(context, getString(R.string.sms_failed_delivery), Toast.LENGTH_SHORT).show();
					break;
				}
				
			}
		}, new IntentFilter(DELIVERED));
		
		mSmsManager.sendTextMessage(number, null, message, sentPI, deliveredPI);
	}
	
	/** Called when the user clicks the Finish button */
	public void buttonFinishMethod(View v){
		finish();
	}
	
		
	
	
}
