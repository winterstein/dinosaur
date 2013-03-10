package com.winterwell.android.recentapp1;

import com.winterwell.android.AndroidUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	private static final String LOGTAG = "dinosaur.lock";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(LOGTAG, "RecentApp CREATE");
		setContentView(R.layout.activity_main);		
	}

	protected void onStart() {
		super.onStart();
		String name = getPackageName();
		int i = Integer.valueOf(name.substring(name.length()-1));		
		Log.i(LOGTAG, "RecentApp "+i+" Start");
		Intent origin = getIntent();
		Log.i(LOGTAG, "Origin: "+origin);
		String action = null, pckg=null;
		if (origin != null) {
			action = origin.getStringExtra("action");
			pckg = origin.getStringExtra("package");
		}
		if (action==null) action = Intent.ACTION_MAIN;
		if (pckg==null) pckg = "com.winterwell.android.dinosaur";
	
		// Next in the chain?
		try {
			String next = name.replace(""+i, ""+(i+1));
			Intent intent2 = getPackageManager().getLaunchIntentForPackage(next);
			boolean fnd = AndroidUtils.pickIntentHandler(this, intent2, null);
			if (fnd) {
				intent2.putExtra("action", action);
				intent2.putExtra("package", pckg);
				intent2.putExtra("RecentApp", true);
				Log.i(LOGTAG, "Up the chain... "+intent2.toString());
				startActivity(intent2);
				return;
			}
		} catch(Exception ex) {
			Log.e(LOGTAG, ""+ex);
		}
		
		Intent intent = getPackageManager().getLaunchIntentForPackage(pckg);
		intent.putExtra("RecentApp", true);
		Log.i(LOGTAG, "Starting... "+intent.toString());
		startActivity(intent);		
	};

}
