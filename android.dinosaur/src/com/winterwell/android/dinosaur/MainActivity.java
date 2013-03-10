package com.winterwell.android.dinosaur;

import java.io.File;

import com.androidquery.AQuery;
import com.winterwell.android.dinosaur.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MainActivity extends Activity {
	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_FULLSCREEN; // FLAG_HIDE_NAVIGATION;

	protected static final String LOGTAG = "dinosaur";

	private static final int REQ_BOOKCASE = 34;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	private VideoView vv;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		log("Key " + keyCode + " " + event.getAction() + " "
				+ event.getDisplayLabel());
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStop() {
		Log.i(LOGTAG, "STOP");
		LockActivity.off(this);
		super.onStop();
	}

	boolean isLock = true;

	private int video;

	private boolean bookcaseInitFlag;

	protected boolean exitFlag;


	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		Log.v(LOGTAG, "dispatchKey " + event.getKeyCode() + " " + event);		
		if ( ! isLock) {
			return super.dispatchKeyEvent(event);
		}
		// try to block home & silence
		int kc = event.getKeyCode();
		if (kc == event.KEYCODE_HOME || kc==event.KEYCODE_VOLUME_MUTE
			|| kc==event.KEYCODE_VOLUME_DOWN) {
			return true;
		}
		return super.dispatchKeyEvent(event);
		/*// Let number presses through
		if (event.getKeyCode() >= event.KEYCODE_0
			&& event.getKeyCode() <= event.KEYCODE_9) {
			return super.dispatchKeyEvent(event);
		}
		return false;*/
	}

	@Override
	public void onBackPressed() {
		Log.i(LOGTAG, "Back Pressed");
		if (isLock) {
			return;
		}
		super.onBackPressed();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(LOGTAG, "PAUSE");		
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(LOGTAG, "RESUME");		
		LockActivity.on(this);
		if (video!=0) {
			videoChosen(video);
		}
/*		Log.i(LOGTAG, "set keyguard off");
		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
		lock.disableKeyguard();
*/	
	}

	@Override
	public void onDetachedFromWindow() {
		Log.i(LOGTAG, "DETACHED FROM WINDOW");
		super.onDetachedFromWindow();
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		Log.d(LOGTAG, "On activity attached");		
		Log.d(LOGTAG, "SIZE " + getWindow().getAttributes().width + " x "
				+ getWindow().getAttributes().height);
	}

	private void setupWindow() {
		try {
			Log.i(LOGTAG, "set window type dialog");
//			getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} catch (Exception ex) {
			Log.i(LOGTAG, ex + "");
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(LOGTAG, "CREATE :) "+ (!exitFlag));

		setupWindow();
		
		LockActivity.on(this);

		super.onCreate(savedInstanceState);

		Log.i(LOGTAG, "Make VV");
		vv = new VideoView(this);
		OnErrorListener el = new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Log.e(LOGTAG, "VV ERROR " + what + " " + extra);
				Toast.makeText(getApplication(), "VV Error what:" + what
						+ " extra:" + extra, Toast.LENGTH_LONG);
				return false;
			}
		};
		vv.setOnErrorListener(el);
		
		vv.setOnCompletionListener(new OnCompletionListener() {			
			@Override
			public void onCompletion(MediaPlayer mp) {
				video = -1;
				openBookcase();
			}
		});

//		setContentView(vv);

		String uriPath = "android.resource://" + getPackageName() + "/"
				+ R.raw.dino480_360;
		Uri uri = Uri.parse(uriPath);
		log("SET URI " + uri);
		vv.setVideoURI(uri);

		openBookcase();
	}

	protected void openBookcase() {
		/*if (video==-1) {
			Log.i(LOGTAG, "no open");
			return;
		}*/
		Log.i(LOGTAG, "open");
		video = -1;
		//Intent intent = new Intent("com.winterwell.android.dinosaur.bookcase"); //BookcaseActivity.class.getName());
		//intent.addCategory(Intent.CATEGORY_DEFAULT);
		//startActivityForResult(intent, REQ_BOOKCASE);
		setContentView(R.layout.activity_bookcase);
		//if (bookcaseInitFlag) return;
		bookcaseInitFlag = true;
		ImageButton play1 = (ImageButton) findViewById(R.id.broccoli);
		ImageButton play2 = (ImageButton) findViewById(R.id.grapes);
		ImageButton play3 = (ImageButton) findViewById(R.id.chicken);
		play1.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				videoChosen(1);
			}
		});
		play2.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				videoChosen(2);
			}
		});
		play3.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				videoChosen(3);
			}
		});
		
		EditText exit = (EditText) findViewById(R.id.exitPassword);
		exit.setOnEditorActionListener(new EditText.OnEditorActionListener() {			
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				String s = arg0.getEditableText().toString();
				Log.i(LOGTAG, "editor action "+arg2+" "+s);
				if ("1234".equals(s)) {
					LockActivity.off(MainActivity.this);
					Log.i(LOGTAG, "EXIT!");
					exitFlag = true;
					setResult(0);
					finish();					
				}
				return false;	
			}
		});		
	}

	protected void videoChosen(int resultCode) {
		Log.i(LOGTAG, "videoChosen "+resultCode);
		setContentView(vv);		
		if (resultCode==1) {
			video = 1;
			String uriPath = "android.resource://" + getPackageName() + "/"
					+ R.raw.dino480_360;
			Uri uri = Uri.parse(uriPath);
			log("SET URI " + uri);
			vv.setVideoURI(uri);
		}
		if (resultCode==2) {
			video = 2;
			String uriPath = "android.resource://" + getPackageName() + "/"
					+ R.raw.grapes480_360;
			Uri uri = Uri.parse(uriPath);
			log("SET URI " + uri);
			vv.setVideoURI(uri);
		}
		if (resultCode==3) {
			video = 3;
			String uriPath = "android.resource://" + getPackageName() + "/"
					+ R.raw.chicken480_360;
			Uri uri = Uri.parse(uriPath);
			log("SET URI " + uri);
			vv.setVideoURI(uri);
		}
		vv.bringToFront();
		vv.start();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		Log.i(LOGTAG, "postcreate");
		super.onPostCreate(savedInstanceState);

	}

	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		Log.d(LOGTAG, "Focus changed !");

		if (!hasFocus) {
			Log.d(LOGTAG, "Lost focus !");

			Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
			sendBroadcast(closeDialog);
			// Intent openUs = new Intent(); relaunch this app??
		}
	}

	@Override
	protected void onDestroy() {
		Log.i(LOGTAG, "DESTROY");
		if (vv!=null) {
			vv.stopPlayback();
		}
		LockActivity.off(this);
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		Log.i(LOGTAG, "Result "+requestCode+" "+resultCode+" "+data);
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==REQ_BOOKCASE) {
			if (resultCode==0) {
				finish();
				return;
			}
			if (resultCode==1) {
				video = 1;
				String uriPath = "android.resource://" + getPackageName() + "/"
						+ R.raw.dino480_360;
				Uri uri = Uri.parse(uriPath);
				log("SET URI " + uri);
				vv.setVideoURI(uri);
				vv.start();
				return;
			}
			if (resultCode==2) {
				video = 2;
				String uriPath = "android.resource://" + getPackageName() + "/"
						+ R.raw.dino480_360;
				Uri uri = Uri.parse(uriPath);
				log("SET URI " + uri);
				vv.setVideoURI(uri);
				vv.start();
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		LockActivity.on(this);
		log("START "+ (!exitFlag));
		Log.i(LOGTAG, "SIZE " + vv.getWidth() + " x " + vv.getHeight());		

		// TODO ask the user start or exit?
		Intent intent = getIntent();

		// Spin up Recent Apps?
		if ( ! intent.getBooleanExtra("RecentApp", false)) {
			boolean ok = LockActivity.startRecentApp(this);
			if (ok) return;
		}
		
		isUs = true;
//		vv.start();
//		openBookcase();
	}

	static boolean isUs = false;

	private void log(String string) {
		Log.i(LOGTAG, string);
		Toast.makeText(this, string, Toast.LENGTH_SHORT);
	}

}
