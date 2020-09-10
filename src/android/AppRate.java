package org.pushandplay.cordova.apprate;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.tasks.Task;

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
				case GETAPPVERSION:
					this.getAppVersion(callbackContext, packageManager);
				break;

				case GETAPPTITLE:
					this.getAppTitle(callbackContext, packageManager);
				break;

				case LAUNCHREVIEW:
					this.requestReviewInfo();
				break;
			}
			return false;
		} catch (Exception e) {
      e.printStackTrace();
      return true;
    }
  }

	private boolean getAppVersion(CallbackContext callbackContext, PackageManager packageManager){
	  try{
      callbackContext.success(packageManager.getPackageInfo(this.cordova.getActivity().getPackageName(), 0).versionName);
    } catch (NameNotFoundException e) {
      e.printStackTrace();
    }

    return true;
	}
	private boolean getAppTitle(CallbackContext callbackContext, PackageManager packageManager){
    ApplicationInfo applicationInfo = null;
    try {
      applicationInfo = packageManager.getApplicationInfo(this.cordova.getActivity().getApplicationContext()
                                                                                    .getApplicationInfo()
                                                                                    .packageName, 0);
    } catch (NameNotFoundException e) {
      e.printStackTrace();
    }

    final String applicationName = (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "Unknown");
        callbackContext.success(applicationName);

        return true;
	}
	private void requestReviewInfo(){
		Task<ReviewInfo> request = manager.requestReviewFlow();
		request.addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				// We can get the ReviewInfo object
				ReviewInfo reviewInfo = task.getResult();
				this.launchReview(reviewInfo);
			} else {
				// There was some problem, continue regardless of the result.
			}
		});
	}
	private void launchReview(ReviewInfo reviewInfo ){
		Task<Void> flow = manager.launchReviewFlow(this.cordova.getActivity(), reviewInfo);
		flow.addOnCompleteListener(task -> {
			// The flow has finished. The API does not indicate whether the user
			// reviewed or not, or even whether the review dialog was shown. Thus, no
			// matter the result, we continue our app flow.
		});
	}

}
