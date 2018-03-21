package com.jeongjuwon.iamport;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.uimanager.ViewManager;

import java.util.*;

public class IAmPortPackage implements ReactPackage {
  @Override
  public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {

      return Arrays.<ViewManager>asList(
        new IAmPortViewManager()
      );
  }

  @Override
  public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {

    List<NativeModule> modules = new ArrayList<>();

    modules.add(new IAmPortModule(reactContext));
    return modules;
  }
}
