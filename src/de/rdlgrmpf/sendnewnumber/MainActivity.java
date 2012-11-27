package de.rdlgrmpf.sendnewnumber;

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
import android.widget.TextView;

public class MainActivity extends Activity {
	
	Button buttonNext;
	TextView contactsTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		contactsTextView = (TextView) findViewById(R.id.contacts_text_view);
		contactsTextView.setText(getList());
		
		buttonNext = (Button) findViewById(R.id.button_next);
		
		buttonNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displaySendActivity();				
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
		switch(item.getItemId()){
		case R.id.menu_about:
			final Intent intent = new Intent(this, AboutActivity.class);
    		startActivity(intent);
    	
    	default:
		}
		return super.onOptionsItemSelected(item);
	}
	
	//Call other Activities
	private void displaySendActivity(){
		final Intent intent = new Intent(this, SendActivity.class);
		startActivity(intent);
	}
	//End
	
	private String getList(){
		String s = "";
		
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
		
		Cursor phones = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
		while (phones.moveToNext())
		{
		  String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
		  String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		  s = s + name + "  " + phoneNumber + "\n";
		}
		phones.close();
		
		return s;
	}

}
