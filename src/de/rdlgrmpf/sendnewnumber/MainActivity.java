package de.rdlgrmpf.sendnewnumber;

import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	public static final String NUMBER_LIST = "main_activity_number_list";

	Button buttonNext;
	Button buttonReload;
	TextView contactsTextView;
	EditText filterText;
	CheckBox checkBoxMobile;
	ArrayList<String> mNumbers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		contactsTextView = (TextView) findViewById(R.id.contacts_text_view);
		filterText = (EditText) findViewById(R.id.textViewFilter);
 		checkBoxMobile = (CheckBox) findViewById(R.id.checkBox_only_mobile);
		contactsTextView.setText(getList());

		buttonNext = (Button) findViewById(R.id.button_next);
		buttonNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				displaySendActivity();
			}
		});
		
		buttonReload = (Button) findViewById(R.id.button_reload);
		buttonReload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				contactsTextView.setText(getList());
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			final Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);

		default:
		}
		return super.onOptionsItemSelected(item);
	}

	// Call other Activities
	private void displaySendActivity() {
		final Intent intent = new Intent(this, SendActivity.class);
		intent.putStringArrayListExtra(NUMBER_LIST, mNumbers);
		startActivity(intent);
	}

	// End

	private String getList() {
		String s = "";
		mNumbers = new ArrayList<String>();
		
		
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

		Cursor phones = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
		while (phones.moveToNext()) {

			String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			int numberType = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
			
			if (phoneNumber != null) {
				if(checkNumber(phoneNumber, numberType)){
					mNumbers.add(phoneNumber);
					s = s + name + "  " + phoneNumber + " "+ numberType +",\n";
				}
			}

		}
		phones.close();

		return s;
	}
	private boolean checkNumber(String number, int type){
		boolean mobileOnly = checkBoxMobile.isChecked();
		String[] filterList = seperateFilter(filterText.getText().toString());
		
		if(mobileOnly && type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE){ // whether we want only mobile and number is mobile
			if (filterList.length == 0){											//if no filters are applied every mobile number is ok
				return true;
			} else {																//if not, then we check against every filter 
				boolean returnValue = true;
				for(String f : filterList){
					if (number.startsWith(f)){
						returnValue = true;
						break;
					} else if (!number.startsWith("00") && !number.startsWith("+")){ //if number doesn't start with international prefix, we assume it's local and we want it
						returnValue = true;
					} else {
						returnValue = false;
					}
				}
				return returnValue;
			}
			
		} else if(!mobileOnly){													 	//if we don't need mobile numbers start here
			if (filterList.length == 0){											//if no filters are applied every number is ok
				return true;
			} else {																//if not, then we check against every filter 
				boolean returnValue = true;
				for(String f : filterList){
					if (number.startsWith(f)){
						returnValue = true;
						break;
					} else if (!number.startsWith("00") && !number.startsWith("+")){ //if number doesn't start with international prefix, we assume it's local and we want it hard on the kitchen table :)
						returnValue = true;
					} else {
						returnValue = false;
					}
				}
				return returnValue;
			}
		}
		return false;																//if we want mobile numbers, but number is not mobile return false		
	}
	
	private String[] seperateFilter(String filter){
		String[] filterList = filter.split(" ");
		return filterList;
		
		
	}

}
