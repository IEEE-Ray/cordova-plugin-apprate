package org.pushandplay.cordova.apprate;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;

import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.ReviewManager;

public class AppRate extends CordovaPlugin {
	private ReviewManager manager;

	private final String GETAPPVERSION = "getAppVersion";
	private final String GETAPPTITLE = "getAppTitle";
	private final String LAUNCHREVIEW = "launchAndroidReview";
	@Override
	public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException
	{
		
		try {
			PackageManager packageManager = this.cordova.getActivity().getPackageManager();
			this.manager = ReviewManagerFactory.create(this.cordova.getActivity());

			switch(action){
				case this.GETAPPVERSION:
					this.getAppVersion();
				break;

				case this.GETAPPTITLE:
					this.getAppTitle();
				break;

				case this.LAUNCHREVIEW:
					this.
				break;
			}
			return false;
		} catch (NameNotFoundException e) {
			callbackContext.success("N/A");
			return true;
		}
	}

	private boolean getAppVersion(){
		callbackContext.success(packageManager.getPackageInfo(this.cordova.getActivity().getPackageName(), 0).versionName);
		return true;
	}
	private boolean getAppTitle(){
		ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.cordova.getActivity()
		                                                                                .getApplicationContext()
																						.getApplicationInfo()
																						.packageName, 0);

		final String applicationName = (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "Unknown");
        callbackContext.success(applicationName);

        return true;
	}
	private void RequestReviewInfo(){
		Task<ReviewInfo> request = manager.requestReviewFlow();
		request.addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				// We can get the ReviewInfo object
				ReviewInfo reviewInfo = task.getResult();
				this.launchReview(reviewInfo)
			} else {
				// There was some problem, continue regardless of the result.
			}
		});
	}
	private void launchReview( Context activity ,ReviewInfo reviewInfo ){
		Task<Void> flow = manager.launchReviewFlow(this.cordova.getActivity(), reviewInfo);
		flow.addOnCompleteListener(task -> {
			// The flow has finished. The API does not indicate whether the user
			// reviewed or not, or even whether the review dialog was shown. Thus, no
			// matter the result, we continue our app flow.
		});
	}

}
