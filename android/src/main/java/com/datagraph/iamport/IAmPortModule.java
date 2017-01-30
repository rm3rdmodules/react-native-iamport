package com.datagraph.iamport;

import android.widget.Toast;
import android.content.Intent;
import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;

import java.util.*;

public class IAmPortModule extends ReactContextBaseJavaModule implements ActivityEventListener {

  public IAmPortModule(ReactApplicationContext reactContext) {

    super(reactContext);
    
    reactContext.addActivityEventListener(this);
  }

  @Override
  public String getName() {

    return "IAmPortModule";
  }

  @Override
  public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {

  }

  @Override
  public void onNewIntent(Intent intent) {

    Activity currentActivity = getCurrentActivity();

    Log.i("iamport", "onNewIntent - Module Listener");
    currentActivity.setIntent(intent);
  }
}
