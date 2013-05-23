package com.winterwell.android.dinosaur;

import com.winterwell.android.AndroidUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class LockActivity extends Activity {

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(LOGTAG, "LockActivity START");
		
		String pn = getPackageName();
		Log.i(LOGTAG, "pn: "+pn);
		Intent intent = getPackageManager().getLaunchIntentForPackage(pn);
		Log.i(LOGTAG, "intent: "+intent);
		intent.addCategory(intent.CATEGORY_LAUNCHER);
		
		Log.i(LOGTAG, "LockActivity launch "+intent);
		startActivity(intent);
	}

	private static BroadcastReceiver receiver;

	private static boolean locked;
	
	protected static final String LOGTAG = "dinosaur.lock";
	
	public static void on(final MainActivity mainActivity) {
		Log.i(LOGTAG, "Lock ON from "+locked);
		locked = true;
		Log.i(LOGTAG, "ma "+mainActivity);
		PackageManager pm = mainActivity.getPackageManager();
		ComponentName cn = mainActivity.getComponentName();
		Log.i(LOGTAG, "cn "+cn);
		Log.i(LOGTAG, cn.getClassName());
		
		ComponentName name = new ComponentName(mainActivity.getPackageName(), LockActivity.class.getName());
		int ces = pm.getComponentEnabledSetting(name);
		Log.i(LOGTAG, name+" state = "+ces);
		pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
		ces = pm.getComponentEnabledSetting(name);
		Log.i(LOGTAG, name+" state = "+ces);
	}

	static void off(MainActivity mainActivity) {
		Log.i(LOGTAG, "Lock OFF from "+locked);		
		locked = false;
		if (receiver!=null) mainActivity.unregisterReceiver(receiver);
		PackageManager pm = mainActivity.getPackageManager();		
		ComponentName name = new ComponentName(mainActivity.getPackageName(), LockActivity.class.getName());
		int ces = pm.getComponentEnabledSetting(name);
		Log.i(LOGTAG, name+" state = "+ces);
/*		pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				pm.DONT_KILL_APP);
		ces = pm.getComponentEnabledSetting(name);
		Log.i(LOGTAG, name+" state = "+ces);*/
	}

	public static boolean startRecentApp(Context cntxt) {
		Log.d(LOGTAG, "startRecentApp");
		try {
			String pckg = "com.winterwell.android.recentapp1";
			Intent intent = cntxt.getPackageManager().getLaunchIntentForPackage(pckg);
			boolean ok = AndroidUtils.pickIntentHandler(cntxt, intent, null);
			if (ok) {		
				Log.i(LOGTAG, "Starting... "+intent.toString());
				cntxt.startActivity(intent);
				return true; 
			} else {
				Log.i(LOGTAG, "did not find... "+intent.toString());
				return false;
			}
		} catch(Exception ex) {
			Log.e(LOGTAG, ""+ex);
			return false;
		}
	}
}
