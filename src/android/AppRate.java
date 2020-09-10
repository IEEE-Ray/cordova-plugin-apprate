package org.pushandplay.cordova.apprate;

import org.apache.cordova.*;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.Task;

public class AppRate extends CordovaPlugin {
  ReviewManager manager;
  ReviewInfo reviewInfo;


  private final String GETAPPVERSION = "getAppVersion";
  private final String GETAPPTITLE = "getAppTitle";
  private final String LAUNCHREVIEW = "launchAndroidReview";

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
  }

  @Override
  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException
  {
    Log.v("APPRATE:::", action);
    try {
      PackageManager packageManager = cordova.getActivity().getPackageManager();
      this.manager = ReviewManagerFactory.create(cordova.getActivity());

      switch(action){
        case GETAPPVERSION:
          getAppVersion(callbackContext, packageManager);
          break;

        case GETAPPTITLE:
          getAppTitle(callbackContext, packageManager);
          break;

        case LAUNCHREVIEW:
          Log.v("CASE:::", LAUNCHREVIEW);
          //requestReviewInfo();
          reviewInApp();
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
      callbackContext.success(packageManager.getPackageInfo(cordova.getActivity().getPackageName(), 0).versionName);
    } catch (NameNotFoundException e) {
      e.printStackTrace();
    }

    return true;
  }
  private boolean getAppTitle(CallbackContext callbackContext, PackageManager packageManager){
    ApplicationInfo applicationInfo = null;
    try {
      applicationInfo = packageManager.getApplicationInfo(cordova.getActivity().getApplicationContext()
        .getApplicationInfo()
        .packageName, 0);
    } catch (NameNotFoundException e) {
      e.printStackTrace();
    }

    final String applicationName = (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "Unknown");
    callbackContext.success(applicationName);

    return true;
  }
 
  private void reviewInApp(){
    manager.requestReviewFlow().addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
      @Override
      public void onComplete(@NonNull Task<ReviewInfo> task) {
        if(task.isSuccessful()){
          reviewInfo = task.getResult();
          manager.launchReviewFlow(cordova.getActivity(), reviewInfo).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
              Toast.makeText(cordova.getActivity(), "Rating Failed", Toast.LENGTH_SHORT).show();
            }
          }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
              Toast.makeText(cordova.getActivity(), "Review Completed, Thank You!", Toast.LENGTH_SHORT).show();
            }
          });
        }

      }
    }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(Exception e) {
        Toast.makeText(cordova.getActivity(), "In-App Request Failed", Toast.LENGTH_SHORT).show();
      }
    });
  }

}
