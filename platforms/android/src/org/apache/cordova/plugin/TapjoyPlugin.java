package org.apache.cordova.plugin;

import java.util.Hashtable;
import java.util.Map;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.tapjoy.TJError;
import com.tapjoy.TJEvent;
import com.tapjoy.TJEventCallback;
import com.tapjoy.TJEventManager;
import com.tapjoy.TJEventRequest;
import com.tapjoy.TapjoyAwardPointsNotifier;
import com.tapjoy.TapjoyConnectNotifier;
import com.tapjoy.TapjoyConstants;
import com.tapjoy.TapjoyDailyRewardAdNotifier;
import com.tapjoy.TapjoyDisplayAdNotifier;
import com.tapjoy.TapjoyFeaturedAppNotifier;
import com.tapjoy.TapjoyFeaturedAppObject;
import com.tapjoy.TapjoyFullScreenAdNotifier;
import com.tapjoy.TapjoyLog;
import com.tapjoy.TapjoyNotifier;
import com.tapjoy.TapjoySpendPointsNotifier;
import com.tapjoy.TapjoyVideoNotifier;

@SuppressWarnings("deprecation")
public class TapjoyPlugin extends CordovaPlugin implements TJEventCallback
{
	public static final String TAG = "TapjoyPhoneGap";
	
	static Boolean result = null;
 	private static View displayAdView;
	private static LinearLayout linearLayout = null;
	private static int displayAdX;
	private static int displayAdY;
	
	// map plugin event to native event guid
	private static Hashtable<String, String> guidMap = null;
	
	@Override
	public boolean execute(String action, JSONArray data, final CallbackContext callbackContext) {
		
		Log.d("TapjoyPlugin", "Plugin Called: " + action);
		result = true;
		
		try
		{
			if (action.equals("setFlagKeyValue"))
			{
				com.tapjoy.TapjoyConnect.setFlagKeyValue(data.getString(0), data.getString(1));
			}
			else
			if (action.equals("requestTapjoyConnect"))
			{
				final String appID = data.getString(0);
				final String secretKey = data.getString(1);
				
				com.tapjoy.TapjoyConnectCore.setPlugin(TapjoyConstants.TJC_PLUGIN_PHONEGAP);
				cordova.getThreadPool().execute(new Runnable() {
					
					public void run() {
						com.tapjoy.TapjoyConnect.requestTapjoyConnect(cordova.getActivity().getApplicationContext(), appID, secretKey, null, new TapjoyConnectNotifier()
						{
							@Override
							public void connectFail(){
								callbackContext.error("Connect Fail");
							}
							@Override
							public void connectSuccess(){
								callbackContext.success();
							}
						});
					}
				});
			}
			else
			if (action.equals("setUserID"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().setUserID(data.getString(0));
				callbackContext.success();
			}
			else
			if (action.equals("enablePaidAppWithActionID"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().enablePaidAppWithActionID(data.getString(0));
				callbackContext.success();
			}
			//--------------------------------------------------------------------------------
			// PPA
			//--------------------------------------------------------------------------------
			else
			if (action.equals("actionComplete"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().actionComplete(data.getString(0));
				callbackContext.success();
			}
			//--------------------------------------------------------------------------------
			// OFFERS RELATED
			//--------------------------------------------------------------------------------
			else
			if (action.equals("showOffers"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().showOffers();
				callbackContext.success();
			}
			else
			if (action.equals("showOffersWithCurrencyID"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().showOffersWithCurrencyID(data.getString(0), data.getBoolean(1));
				callbackContext.success();
			}
			else
			if (action.equals("getTapPoints"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().getTapPoints(new TapjoyNotifier()
				{
					@Override
					public void getUpdatePointsFailed(String error)
					{
						callbackContext.error(error);
					}
					
					@Override
					public void getUpdatePoints(String currencyName, int pointTotal)
					{
						callbackContext.success(Integer.toString(pointTotal));
					}
				});
			}
			else
			if (action.equals("spendTapPoints"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().spendTapPoints(data.getInt(0), new TapjoySpendPointsNotifier()
				{
					@Override
					public void getSpendPointsResponseFailed(String error)
					{
						callbackContext.error(error);
					}
					
					@Override
					public void getSpendPointsResponse(String currencyName, int pointTotal)
					{
						callbackContext.success(Integer.toString(pointTotal));
					}
				});
			}
			else
			if (action.equals("awardTapPoints"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().awardTapPoints(data.getInt(0), new TapjoyAwardPointsNotifier()
				{					
					@Override
					public void getAwardPointsResponseFailed(String error)
					{
						callbackContext.error(error);
					}
					
					@Override
					public void getAwardPointsResponse(String currencyName, int pointTotal)
					{
						callbackContext.success(Integer.toString(pointTotal));
					}
				});
			}
			//--------------------------------------------------------------------------------
			// FEATURED AD and FULL SCREEN AD RELATED
			//--------------------------------------------------------------------------------
			else
			if (action.equals("getFeaturedApp"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().getFeaturedApp(new TapjoyFeaturedAppNotifier()
				{
					@Override
					public void getFeaturedAppResponseFailed(String error)
					{
						callbackContext.error(error);
					}
					
					@Override
					public void getFeaturedAppResponse(TapjoyFeaturedAppObject featuredAppObject)
					{
						callbackContext.success();
					}
				});
			}
			else
			if (action.equals("getFeaturedAppWithCurrencyID"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().getFeaturedAppWithCurrencyID(data.getString(0), new TapjoyFeaturedAppNotifier()
				{
					@Override
					public void getFeaturedAppResponseFailed(String error)
					{
						callbackContext.error(error);
					}
					
					@Override
					public void getFeaturedAppResponse(TapjoyFeaturedAppObject featuredAppObject)
					{
						callbackContext.success();
					}
				});
			}
			else
			if (action.equals("setFeaturedAppDisplayCount"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().setFeaturedAppDisplayCount(data.getInt(0));
				callbackContext.success();
			}
			else
			if (action.equals("showFeaturedAppFullScreenAd"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().showFeaturedAppFullScreenAd();
				callbackContext.success();
			}
			else
			if (action.equals("getFullScreenAd"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().getFullScreenAd(new TapjoyFullScreenAdNotifier()
				{
					@Override
					public void getFullScreenAdResponseFailed(int error)
					{
						callbackContext.error(error);
					}
					
					@Override
					public void getFullScreenAdResponse()
					{
						callbackContext.success();
					}
				});
			}
			else
			if (action.equals("getFullScreenAdWithCurrencyID"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().getFullScreenAdWithCurrencyID(data.getString(0), new TapjoyFullScreenAdNotifier()
				{
					@Override
					public void getFullScreenAdResponseFailed(int error)
					{
						callbackContext.error(error);
					}
					
					@Override
					public void getFullScreenAdResponse()
					{
						callbackContext.success();
					}
				});
			}
			else
			if (action.equals("showFullScreenAd"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().showFullScreenAd();
				callbackContext.success();
			}
			//--------------------------------------------------------------------------------
			// DAILY REWARD RELATED
			//--------------------------------------------------------------------------------
			else
			if (action.equals("getDailyRewardAd"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().getDailyRewardAd(new TapjoyDailyRewardAdNotifier()
				{
					@Override
					public void getDailyRewardAdResponseFailed(int error)
					{
						callbackContext.error(error);
					}
					
					@Override
					public void getDailyRewardAdResponse()
					{
						callbackContext.success();
					}
				});
			}
			else
			if (action.equals("getDailyRewardAdWithCurrencyID"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().getDailyRewardAdWithCurrencyID(data.getString(0), new TapjoyDailyRewardAdNotifier()
				{
					@Override
					public void getDailyRewardAdResponseFailed(int error)
					{
						callbackContext.error(error);
					}
					
					@Override
					public void getDailyRewardAdResponse()
					{
						callbackContext.success();
					}
				});
			}
			else
			if (action.equals("showDailyRewardAd"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().showDailyRewardAd();
				callbackContext.success();
			}
			//--------------------------------------------------------------------------------
			// VIDEO RELATED
			//--------------------------------------------------------------------------------
			else
			if (action.equals("initVideoAd"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().initVideoAd(new TapjoyVideoNotifier()
				{
					
					@Override
					public void videoStart()
					{
						callbackContext.success();
					}
					
					@Override
					public void videoError(int statusCode)
					{
						callbackContext.error(statusCode);
					}
					
					@Override
					public void videoComplete()
					{
						callbackContext.success();
					}
				});
				
			}
			else
			if (action.equals("setVideoCacheCount"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().setVideoCacheCount(data.getInt(0));
				callbackContext.success();
			}
			else
			if (action.equals("enableVideoCache"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().enableVideoCache(data.getBoolean(0));
				callbackContext.success();
			}
			else
			if (action.equals("cacheVideos"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().cacheVideos();
				callbackContext.success();
			}
			else
			if (action.equals("setVideoNotifier"))
			{
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().setVideoNotifier(new TapjoyVideoNotifier()
				{
					
					@Override
					public void videoStart()
					{
						callbackContext.success();
					}
					
					@Override
					public void videoError(int statusCode)
					{
						callbackContext.error(statusCode);
					}
					
					@Override
					public void videoComplete()
					{
						callbackContext.success();
					}
				});
			}
			//--------------------------------------------------------------------------------
			// DISPLAY AD RELATED
			//--------------------------------------------------------------------------------
			else
			if (action.equals("getDisplayAd"))
			{
				// altering UI views need to run on UI thread
				cordova.getActivity().runOnUiThread(new Runnable(){
					public void run(){
						com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().getDisplayAd(cordova.getActivity(), new TapjoyDisplayAdNotifier()
						{
							public void getDisplayAdResponse(View adView)
							{
								displayAdView = adView;
								updateResultsInUi();
								callbackContext.success();
							}
					
							public void getDisplayAdResponseFailed(String error)
							{
								callbackContext.error(error);
							}
						});
					}
				});                                 
			}
			else
			if (action.equals("hideDisplayAd"))
			{
				// altering UI views need to run on UI thread
		        cordova.getActivity().runOnUiThread(new Runnable() {
		            public void run() {
						if (linearLayout != null)
						{
							linearLayout.removeAllViews();
							displayAdView = null;
						}
						callbackContext.success();
		            }
		        });

			}
			else
			if (action.equals("enableDisplayAdAutoRefresh"))
			{
				boolean shouldAutoRefresh = data.getBoolean(0);
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().enableDisplayAdAutoRefresh(shouldAutoRefresh);
				callbackContext.success();
			}
			else
			if (action.equals("moveDisplayAd"))
			{
				displayAdX = data.getInt(0);
				displayAdY = data.getInt(1);
				
				// altering UI views need to run on UI thread
		        cordova.getActivity().runOnUiThread(new Runnable() {
		            public void run() {
						updateResultsInUi();
						callbackContext.success();
		            }
		        });
			}
			else
			if (action.equals("setDisplayAdSize"))
			{
				String dimensions = data.getString(0);
				com.tapjoy.TapjoyConnect.getTapjoyConnectInstance().setDisplayAdSize(dimensions);
			}
			//--------------------------------------------------------------------------------
			// EVENTS FRAMEWORK
			//--------------------------------------------------------------------------------
			else
			if (action.equals("createEvent"))
			{
				final String guid = data.getString(0);
				final String eventName = data.getString(1);
				final String eventParam = data.getString(2);
				
		        cordova.getActivity().runOnUiThread(new Runnable() {
		            public void run() {
						if(guidMap == null)
							guidMap = new Hashtable<String, String>();
						
						TJEvent myEvent = new TJEvent(cordova.getActivity(), eventName, eventParam, TapjoyPlugin.this);
						
						// Key = Android guid, Value = PhoneGap guid
						guidMap.put(myEvent.getGUID(), guid);
						
						callbackContext.success();
		            }
		        });
			}
			else
			if (action.equals("sendEvent"))
			{
				final String guid = data.getString(0);
				
				cordova.getActivity().runOnUiThread(new Runnable()
				{
					public void run()
					{
						String androidGuid = getAndroidGuid(guid);

						if (androidGuid != null)
						{
							TJEvent evt = TJEventManager.get(androidGuid);
							evt.send();
						}
					}
				});
			}
			else
			if (action.equals("showEvent"))
			{
				final String guid = data.getString(0);
				
				cordova.getActivity().runOnUiThread(new Runnable()
				{
					public void run()
					{
						String androidGuid = getAndroidGuid(guid);

						if (androidGuid != null)
						{
							TJEvent evt = TJEventManager.get(androidGuid);
							evt.showContent();
						}
					}
				});
			}
			//--------------------------------------------------------------------------------
			//--------------------------------------------------------------------------------
			else 
			{
				result = false;
			}
		}
		catch (Exception e)
		{
			Log.d("Tapjoy Plugin", "exception: "+ e.toString());
			result = false;
			
			// requestTapjoyConnect has not been called yet.
			if (com.tapjoy.TapjoyConnect.getTapjoyConnectInstance() == null)
			{
				callbackContext.error("Tapjoy instance is null.  Call requestTapjoyConnect first");
			}
			else
			{
				callbackContext.error(e.toString());
			}
		}
		
		Log.d("Tapjoy Plugin", "result status: " + result);
		
		return result;
	}
	
	private void updateResultsInUi() 
	{
		try
		{
			// Get the screen size.
			WindowManager mW = (WindowManager)cordova.getActivity().getSystemService(Context.WINDOW_SERVICE);
			int screenWidth = mW.getDefaultDisplay().getWidth();
			int screenHeight = mW.getDefaultDisplay().getHeight();
			
			// Null check.
			if (displayAdView == null)
			{
				return;
			}
					
			int ad_width = displayAdView.getLayoutParams().width;
			int ad_height = displayAdView.getLayoutParams().height;
			
			// Resize display ad if it's too big.
			if (screenWidth < ad_width)
			{
				// Using screen width, but substitute for the any width.
				int desired_width = screenWidth;

				int scale;
				Double val = Double.valueOf(desired_width) / Double.valueOf(ad_width);
				val = val * 100d;
				scale = val.intValue();

				((android.webkit.WebView) (displayAdView)).getSettings().setSupportZoom(true);
				((android.webkit.WebView) (displayAdView)).setPadding(0, 0, 0, 0);
				((android.webkit.WebView) (displayAdView)).setVerticalScrollBarEnabled(false);
				((android.webkit.WebView) (displayAdView)).setHorizontalScrollBarEnabled(false);
				// ((WebView)(displayAdView)).getSettings().setLoadWithOverviewMode(true);
				((android.webkit.WebView) (displayAdView)).setInitialScale(scale);
			
				// Resize display ad to desired width and keep aspect ratio.
				LayoutParams layout = new LayoutParams(desired_width, (desired_width*ad_height)/ad_width);
				displayAdView.setLayoutParams(layout);
			}
			
			if (linearLayout != null)
			{
				linearLayout.removeAllViews();
			}
			
			linearLayout = new LinearLayout(cordova.getActivity());
			linearLayout.setLayoutParams(new ViewGroup.LayoutParams(screenWidth, screenHeight));
			
			// Use padding to set the x/y coordinates.
			linearLayout.setPadding(displayAdX, displayAdY, 0, 0);
			linearLayout.addView(displayAdView);
			
			cordova.getActivity().getWindow().addContentView(linearLayout, new ViewGroup.LayoutParams(screenWidth, screenHeight));
		}
		catch (Exception e)
		{
			Log.e("TapjoyPluginActivity", "exception adding display ad: " + e.toString());
		}
	}

	//----------------------------------------------------------------------
	// EVENTS Interface
	//----------------------------------------------------------------------
	public void sendEventCompleted(TJEvent event, boolean contentAvailable)
	{
		TapjoyLog.i(TAG, "sendEventCompleted - contentAvailable = " + contentAvailable);

		if (contentAvailable)
			webView.sendJavascript("Tapjoy.sendEventCompleteWithContent('" + guidMap.get(event.getGUID()) + "');");
		else
			webView.sendJavascript("Tapjoy.sendEventComplete('" + guidMap.get(event.getGUID()) + "');");
	}
	
	public void sendEventFail(TJEvent event, TJError error)
	{
		TapjoyLog.i(TAG, "sendEventFail");

		webView.sendJavascript("Tapjoy.sendEventFail('" + guidMap.get(event.getGUID()) + "', '"+ error.message + "');");
	}
	
	public void contentDidShow(TJEvent event)
	{
		TapjoyLog.i(TAG, "contentDidShow");
	}
	
	public void contentDidDisappear(TJEvent event)
	{
		TapjoyLog.i(TAG, "contentDidDisappear");
	}

	public void didRequestAction(TJEvent event, TJEventRequest request)
	{
		TapjoyLog.i(TAG, "didRequestAction");

		// TODO: Use json encoding
		String message = guidMap.get(event.getGUID()) + "," + request.type + "," + request.identifier + "," + request.quantity;
	}
	
	private static String getAndroidGuid(String phoneGapGuid)
	{
		String androidGuid = null;

		if (guidMap != null)
		{
			for(Map.Entry<String, String> entry: guidMap.entrySet())
			{
				if (entry.getValue().equals(phoneGapGuid))
					androidGuid = entry.getKey();
			}
		}
		else
		{
			TapjoyLog.e(TAG, "Cannot get the AndroidGuid, the guidmap is null");
		}

		return androidGuid;
	}
}