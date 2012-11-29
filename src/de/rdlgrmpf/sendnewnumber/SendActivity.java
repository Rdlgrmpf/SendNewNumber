package de.rdlgrmpf.sendnewnumber;

import java.util.ArrayList;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SendActivity extends Activity {
	
	private static final String TAG ="SendNewNumber/Send";
	
	final String SENT = "sms_sent";
	final String DELIVERED = "sms_delivered";

	Button buttonSend;
	Button buttonFinish;
	CheckBox checkBoxDatabase;
	EditText textSleepTime;
	EditText message;
	TextView mCounter;
	TextView sentCounter;
	ArrayList<String> mNumbers;
	SmsManager mSmsManager;
	BroadcastReceiver sentReceiver;
	BroadcastReceiver deliveredReceiver;
	
	private int totalSMS = 0;
	private int sentSMS = 0;
	private int deliveredSMS =0;
	private int sleepTime = 2000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send);
		
		if (getIntent().getExtras() != null){
			mNumbers = getIntent().getExtras().getStringArrayList(MainActivity.NUMBER_LIST);
			totalSMS = mNumbers.size();
		} else 
			mNumbers = null;
		
		textSleepTime = (EditText) findViewById(R.id.textViewSleepTime);
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
		checkBoxDatabase = (CheckBox) findViewById(R.id.checkBox_database);
		
		mSmsManager = SmsManager.getDefault();
		
		sentReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				switch(getResultCode()){
				case Activity.RESULT_OK:
					sentSMS++;
					sentCounter.setText(sentSMS + "/" + totalSMS + " sent\n" + deliveredSMS + "/" + totalSMS + " delivered");
					Toast.makeText(context, getString(R.string.sms_sent), Toast.LENGTH_SHORT).show();
					break;
					
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(context, getString(R.string.sms_generic_failure), Toast.LENGTH_SHORT).show();
					break;
					
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(context, getString(R.string.sms_no_service), Toast.LENGTH_SHORT).show();
					break;
					
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(context, getString(R.string.sms_null_pdu), Toast.LENGTH_SHORT).show();
					break;
					
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(context, getString(R.string.sms_radio_off), Toast.LENGTH_SHORT).show();
					break;
				}
				
			}
		};
		deliveredReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				switch(getResultCode()){
				case Activity.RESULT_OK:
					deliveredSMS++;
					sentCounter.setText(sentSMS + "/" + totalSMS + " sent\n" + deliveredSMS + "/" + totalSMS + " delivered");
					Toast.makeText(context, getString(R.string.sms_delivered), Toast.LENGTH_SHORT).show();
					break;
					
				case Activity.RESULT_CANCELED:
					Toast.makeText(context, getString(R.string.sms_failed_delivery), Toast.LENGTH_SHORT).show();
					break;
				}
				
			}
		};
		registerReceiver(sentReceiver, new IntentFilter(SENT));
		registerReceiver(deliveredReceiver, new IntentFilter(DELIVERED));
	}

	@Override
	protected void onStop() {
		unregisterReceiver(sentReceiver);
		unregisterReceiver(deliveredReceiver);
		super.onStop();
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
		String tempTime = textSleepTime.getText().toString();
		
		if((smsMessage.length() > 0) && (tempTime.length() > 0)){
			sleepTime = Integer.parseInt(textSleepTime.getText().toString());
			new smsSender().execute(null, null, null); //start sending
			buttonSend.setVisibility(View.INVISIBLE);
			buttonFinish.setVisibility(View.VISIBLE);
			
		} else if(!(tempTime.length() < 1)) {
			Toast.makeText(getBaseContext(), getString(R.string.no_message_entered), Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getBaseContext(), getString(R.string.no_time), Toast.LENGTH_SHORT).show();
			sleepTime = 2000;
		}
		
	}
	
	/** Called when the user clicks the Finish button */
	public void buttonFinishMethod(View v){
		if((sentSMS == totalSMS) && checkBoxDatabase.isChecked()){
			new databaseWriter().execute(null, null, null);
		} else {
			finish(); //databaseWriter handles it in the other case
		}		
		
	}
	
	private String getContactNameFromNumber(String number) {
		
		Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
		// define the columns for the query to return
		String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
											ContactsContract.PhoneLookup.NUMBER};
		// query time
		Cursor c = getContentResolver().query(uri, projection, null, null, null);

		// if the query returns 1 or more results
		// return the first result
		if (c.moveToFirst()) {
			String name = c.getString(c.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
			return name;
		}

		// return the original number if no match was found
		return number;
	}
	
	protected void sendSms(String number, String message){
		
		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
		
		mSmsManager.sendTextMessage(number, null, message, sentPI, deliveredPI);
	}
	
	/** Background Tasks */
	private class smsSender extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			String smsMessage = message.getText().toString();
			for(String number : mNumbers){		
				sendSms(number, smsMessage);
				Log.i(TAG, "sent to: " + number);				
				try {
					Log.i(TAG, "wait for " + sleepTime + " ms");
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
			}
			return null;
		}
		
	}
	
	private class databaseWriter extends AsyncTask<Void, String, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			for (String n : mNumbers){
				ContentValues values = new ContentValues();
				values.put("address", n);
				values.put("body", message.getText().toString());
				getContentResolver().insert(Uri.parse("content://sms/sent"), values);
				publishProgress("Message added to " + getContactNameFromNumber(n));
				//Toast.makeText(getBaseContext(), "Message added to " + n, Toast.LENGTH_SHORT).show();
			}
			publishProgress(getString(R.string.sms_added_to_database));
			//Toast.makeText(getBaseContext(), , Toast.LENGTH_SHORT).show();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			finish();
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(String...values) {
			Toast.makeText(getBaseContext(), values[0], Toast.LENGTH_SHORT).show();
			super.onProgressUpdate(values);
		}
		
		
	}
}
