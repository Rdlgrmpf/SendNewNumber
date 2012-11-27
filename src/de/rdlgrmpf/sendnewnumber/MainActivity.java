package de.rdlgrmpf.sendnewnumber;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	Button buttonNext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
	

}