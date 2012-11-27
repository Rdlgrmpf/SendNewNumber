package de.rdlgrmpf.sendnewnumber;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class SendActivity extends Activity {

	Button buttonSend;
	Button buttonFinish;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send);

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
    	
    	default:
		}
		return super.onOptionsItemSelected(item);
	}
	
	/** Button Methods */
	
	/** Called when the user clicks the Send button */
	public void buttonSendMethod(View v){
		//Todo: do the sendings
		buttonSend.setVisibility(View.GONE);
		buttonFinish.setVisibility(View.VISIBLE);
	}
	
	/** Called when the user clicks the Finish button */
	public void buttonFinishMethod(View v){
		finish();
	}
	

}
