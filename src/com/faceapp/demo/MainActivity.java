package com.faceapp.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Facepp SDK Android test
 * 
 * Look result at debug area.(Log cat)
 * @author moon5ckq
 */
public class MainActivity extends BaseActivity implements OnClickListener {

	private Button button1 , button2 ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(this);
        button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			Intent mIntent1 = new Intent(this , PictureDetect.class);
			startActivity(mIntent1);
			break;
		case R.id.button2:
			Intent mIntent2 = new Intent(this , Activity_Group.class);
			startActivity(mIntent2);
			break;
		}
		
	}

    
}
