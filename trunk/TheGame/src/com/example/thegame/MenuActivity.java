package com.example.thegame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

public class MenuActivity extends Activity {

@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_menu);
	Button b = (Button)findViewById(R.id.button2);
	b.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			 Intent returnIntent = new Intent();
			 CheckBox rb = (CheckBox)findViewById(R.id.checkBox1);
				returnIntent.putExtra("accelerometer", rb.isChecked());
			 	 setResult(RESULT_OK, returnIntent);     
			finish();
		}
	});
}
}
