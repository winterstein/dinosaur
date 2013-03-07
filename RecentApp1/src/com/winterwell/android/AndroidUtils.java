package com.winterwell.android;
/**
 * (c) Winterwell Associates Ltd, used under MIT License. This file is background IP.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AndroidUtils {
	

	public static <X> int plus(Map<X, Integer> counts, X key, int dx) {
		Integer x = counts.get(key);
		if (x == null)
			x = 0;
		x += dx;
		counts.put(key, x);
		return x;
	}
	
	/**
	 * Create a map from a list of key, value pairs. An easy way to make small
	 * maps, basically the equivalent of {@link Arrays#asList(Object...)}. If
	 * the value is null, the key will not be included.
	 */
	public static <K,V> Map<K, V> asMap(Object... keyValuePairs) {
		assert keyValuePairs.length % 2 == 0;
		Map m = new HashMap(keyValuePairs.length / 2);
		for (int i = 0; i < keyValuePairs.length; i += 2) {
			Object v = keyValuePairs[i + 1];
			if (v == null) {
				continue;
			}
			m.put(keyValuePairs[i], v);
		}
		return m;
	}
	
	public static <X extends View> X getChild(ViewGroup row, Class<X> klass) {
		assert klass != null;
		for (int i = 0; i < row.getChildCount(); i++) {
			View child = row.getChildAt(i);
			if (klass.isAssignableFrom(child.getClass())) {
			    return klass.cast(child);
			}
			
			if (child instanceof ViewGroup) {
                X kid = getChild((ViewGroup) child, klass);
                if (kid != null) return kid;
            }
		}
		// fail
		return null;
	}

	/**
	 * The equivalent of instanceof, but for Class objects. 'cos I always forget
	 * how to do this.
	 * 
	 * @param possSubType
	 * @param superType
	 * @return true if possSubType <i>is</i> a subType of superType
	 */
	public static boolean isa(Class<?> possSubType, Class<?> superType) {
		return superType.isAssignableFrom(possSubType);
	}

	/**
	 * 
	 * @param pounds e.g. "�"
	 * @param pence e.g. "p". Can be null, in which case the pounds symbol is
	 *            always used.
	 * @param price
	 * @return e.g. "�1.50", "50p", "free"
	 */
	public static String priceToString(String pounds, String pence, int price) {
		assert price >= 0 : price;
		assert pounds != null;
		if (price == 0) return "free";
		int numPounds = price / 100;
		int numPence = price % 100;
		if (price < 100 && pence != null)
			return numPence + pence;
		
		return pounds + String.format("%d.%02d", numPounds, numPence);
	}


	/**
	 * Read a text file from assets
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String read(Context context, String fileName) {
		try {
			AssetManager am = context.getAssets();
			InputStream in = am.open(fileName);
			// String pckg = context.getPackageName();
			// ContentResolver cr = context.getContentResolver();
			// Uri uri = Uri.parse("android.resource://"+pckg+"/"+fileName);
			// InputStream in = cr.openInputStream(uri);
			String txt = read(new InputStreamReader(in));
			return txt;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param r Will be read and closed
	 * @return The contents of input
	 */
	public static String read(Reader r) {
		try {
			BufferedReader reader = r instanceof BufferedReader ? (BufferedReader) r
					: new BufferedReader(r);
			final int bufSize = 8192; // this is the default BufferredReader
			// buffer size
			StringBuilder sb = new StringBuilder(bufSize);
			char[] cbuf = new char[bufSize];
			while (true) {
				int chars = reader.read(cbuf);
				if (chars == -1) break;
				sb.append(cbuf, 0, chars);
			}
			return sb.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				r.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void sleep(int msecs) {
		try {
			Thread.sleep(msecs);
		} catch (InterruptedException e) {
			// oh well
		}
	}

	public static boolean hasMethod(Object obj, String method) {
		assert method != null;
		Method[] ms = obj.getClass().getMethods();
		for (Method m : ms) {
			if (m.getName().equals(method)) return true;
		}
		return false;
	}

	public static void callMethod(Object obj, String method, Object... params) {
		Method[] ms = obj.getClass().getMethods();
		for (Method m : ms) {
			if ( ! m.getName().equals(method)) continue;
			// TODO check type args
			Class<?>[] types = m.getParameterTypes();
			if (params.length != types.length) continue;
			for(int i=0; i<params.length; i++) {
				
			}
			// call
			try {
				m.invoke(obj, params);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		throw new RuntimeException(obj+"."+method+"("+params.length+")");
	}

	public static Point getScreenResolution(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	    Display display = manager.getDefaultDisplay();
	    Point wh = new Point(display.getWidth(), display.getHeight());
	    if (wh.x==0 || wh.y==0) {
	    	Log.e("AndroidUtils", "Screen resolution "+wh+" for "+context);
	    }
	    return wh;
	}

	public static View addSpacer(LinearLayout row) {
		View spacer = new View(row.getContext());	
//		if (DEBUG) spacer.setBackgroundColor(Color.GREEN);
		android.widget.LinearLayout.LayoutParams params =
				new android.widget.LinearLayout.LayoutParams(DEBUG?1:0, DEBUG?1:0, 1);
		row.addView(spacer, params);
		return spacer;
	}

	private static final Random rnd = new Random();
	
	public static Random getRandom() {
		return rnd;
	}

	public static final boolean DEBUG =
											false;
//									 		true;

	/**
	 * For use only with LinearLayout!
	 * @param v
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public static void setMargin(View v, int left, int top, int right, int bottom) 
	{
		setMargin(v, left, top, right, bottom, true);
	}
	public static void setMargin(View v, int left, int top, int right, int bottom, boolean horizontalWrap) {
		LinearLayout.LayoutParams layout = (LinearLayout.LayoutParams) v.getLayoutParams();
		if (layout==null) {
			// create a "default" layout
			layout = new LinearLayout.LayoutParams(
					horizontalWrap? LayoutParams.WRAP_CONTENT : LayoutParams.FILL_PARENT, 
							LayoutParams.WRAP_CONTENT);
		}
		layout.setMargins(left, top, right, bottom);
		v.setLayoutParams(layout);
	}


	public static void setSize(View view, int w, int h) {
		view.setMinimumWidth(w);
		view.setMinimumHeight(h);
		if (view instanceof ImageView) {
			ImageView iv = (ImageView) view;
			iv.setAdjustViewBounds(true);
			iv.setMaxWidth(w);
			iv.setMaxHeight(h);
		}
	}

	public static String read(InputStream inputStream) {
		return read(new InputStreamReader(inputStream));
	}

	
	
	
	/**
	 * Try to pick the app to handle this intent.
	 * @param context
	 * @param intent
	 * @param matchMe E.g. "twitter" Pick the first app whose name contains this.
	 * Can be null for pick-anything.
	 * @return true if a match was found
	 */
	public static boolean pickIntentHandler(Context context, Intent intent, String matchMe) 
	{
		final PackageManager pm = context.getPackageManager();
	    final List<ResolveInfo> activityList = pm.queryIntentActivities(intent, 0);
	    List<String> handlers = new ArrayList(activityList.size());
	    for (ResolveInfo app : activityList) {
	    	String name = app.activityInfo.name;
	    	handlers.add(name);
	    	if (matchMe==null || name.contains(matchMe)) {
	            ActivityInfo activity=app.activityInfo;
	            ComponentName compname = new ComponentName(activity.applicationInfo.packageName, activity.name);
//	            intent.addCategory(Intent.CATEGORY_LAUNCHER);
//	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
	            intent.setComponent(compname);
	            return true;
	        }
	    }
	    Log.d("pick-intent", "No match for "+matchMe+" in "+handlers);
	    return false;
	}

	/**
	 * rainbow colours for exploring layout bugs
	 * @param tv
	 */
	public static void debugColor(View tv) {
		if (AndroidUtils.DEBUG) {	
			int r = AndroidUtils.getRandom().nextInt(128);
			int g = AndroidUtils.getRandom().nextInt(128);
			int b = AndroidUtils.getRandom().nextInt(128);
			tv.setBackgroundColor(Color.argb(128, r, g, b));
		}
	}

	/**
	 * Lenient equals: null = ""
	 * @param a
	 * @param b
	 * @return true if a=b
	 */
	public static boolean equals(String a, String b) {
		if (a==null || a.length()==0) return b==null || b.length()==0;
		return a.equals(b);
	}

	public static String getDeviceId(Context context) {
		try {
			TelephonyManager TelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			String szImei = TelephonyMgr.getDeviceId(); // Requires READ_PHONE_STATE
			return szImei;
		} catch (Exception e) {
			// Oh well
		}
		try {
			String m_szDevIDShort = "35" + //we make this look like a valid IMEI
		        	Build.BOARD.length()%10+ Build.BRAND.length()%10 +
		        	Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
		        	Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
		        	Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
		        	Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
		        	Build.TAGS.length()%10 + Build.TYPE.length()%10 +
		        	Build.USER.length()%10 ; //13 digits
			return m_szDevIDShort;
		} catch (Exception e) {
			// Oh well
		}
		return "tempid"+getRandom().nextInt();
	}

}
