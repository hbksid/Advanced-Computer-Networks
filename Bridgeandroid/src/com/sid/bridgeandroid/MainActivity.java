package com.sid.bridgeandroid;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button Exit = (Button)findViewById(R.id.btnExit);
		Exit.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
			    // Add your handling code here:

				/**
				*Exits org.multichat.client.ChatClient
				*Stops the recodrer from running
				*And calls its the recorders onExit() function to unload its thread
				*/

				if( r != null ){
					r.onExit();
				}
				imRunning = false;
				System.exit(0);
			}
		});
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
