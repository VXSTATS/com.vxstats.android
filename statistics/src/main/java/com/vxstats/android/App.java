/*
 * Copyright (C) 10/01/2020 VX STATS <sales@vxstats.com>
 *
 * This document is property of VX STATS. It is strictly prohibited
 * to modify, sell or publish it in any way. In case you have access
 * to this document, you are obligated to ensure its nondisclosure.
 * Noncompliances will be prosecuted.
 *
 * Diese Datei ist Eigentum der VX STATS. Jegliche Änderung, Verkauf
 * oder andere Verbreitung und Veröffentlichung ist strikt untersagt.
 * Falls Sie Zugang zu dieser Datei haben, sind Sie verpflichtet,
 * alles in Ihrer Macht stehende für deren Geheimhaltung zu tun.
 * Zuwiderhandlungen werden strafrechtlich verfolgt.
 */

/* local package */
package com.vxstats.android;

/* android imports */
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/**
 * @author VX STATS <sales@vxstats.com>
 * @version 1.0
 * @~english Information about the application.
 * @~german Informationen über die Anwendung.
 * @~
 * @brief @~english Information about the application. @~german Informationen über die Anwendung.
 * @~
 * @date 01/09/2011
 */
public class App {

  private static App instance = null;

  private final Context context;

  private String appName;

  private String appVersion;

  private int appBuild;

  private String appIdentifier = "";

  private App( Activity activity ) {

    context = activity.getApplicationContext();

    setAppName();
    setAppIdent();
    setAppVersion();
    setAppBuild();
  }

  private void setAppName() {

    String mAppName;
    mAppName = ( String ) context.getPackageManager().getApplicationLabel( context.getApplicationInfo() );

    this.appVersion = mAppName;
  }

  private void setAppIdent() {

    if ( appIdentifier.equals( "" ) ) {

      try {

        String pkg = context.getPackageName();
        appIdentifier = context.getPackageManager().getPackageInfo( pkg, 0 ).packageName;
      }
      catch ( NameNotFoundException exception ) {

        exception.printStackTrace();
        Log.e( "Error", "Bad implementation of packageName. Check your AndroidManifest!" );
      }
    }
  }

  private void setAppVersion() {

    try {

      String pkg = context.getPackageName();
      appVersion = context.getPackageManager().getPackageInfo( pkg, 0 ).versionName;
    }
    catch ( NameNotFoundException exception ) {

      exception.printStackTrace();
      Log.e( "Error", "Bad implementation of versionName. Check your AndroidManifest!" );
    }
  }

  private void setAppBuild() {

    try {

      String pkg = context.getPackageName();
      appBuild = context.getPackageManager().getPackageInfo( pkg, 0 ).versionCode;
    }
    catch ( NameNotFoundException exception ) {

      exception.printStackTrace();
      Log.e( "Error", "Bad implementation of versionCode. Check your AndroidManifest!" );
    }
  }

  /**
   * @~english Set the App Identifier manual.
   * @~german Setzt den Anwendungs Identifier manuel.
   */
  public void setAppIdentifier( String name ) {

    if ( name.length() > 0 ) {

      appIdentifier = name;
    }
  }

  /**
   * @param activity @~english Current activity to resolve the context. @~german Aktuelle Activity für den Kontext.
   * @return @~english The instance for the current application. @~german Die Instanz für die aktuelle Anwendung.
   * @~english Object for the current application.
   * @~german Objekt für die aktuelle Anwendung.
   * @~
   * @~
   */
  public static synchronized App instance( Activity activity ) {

    if ( instance == null ) {

      instance = new App( activity );
    }
    return instance;
  }

  public static void destroy() {

    if ( instance != null ) {

      instance = null;
    }
  }

  /**
   * @return @~english The name of the application. @~german Der Name der Anwendung.
   * @~english The name of the application as defined under AndroidManifest.xml \<application android:label\>.
   * @~german Der Name der Anwendung, wie definiert unter AndroidManifest.xml \<application android:label\>.
   * @~
   */
  public String getAppName() {

    return this.appName;
  }

  /**
   * @return @~english The version of the application. @~german Die Version der Anwendung.
   * @~english The version of the application as defined under AndroidManifest.xml \<manifest android:versionName\>.
   * @~german Die Version der Anwendung, wie definiert unter AndroidManifest.xml \<manifest android:versionName\>.
   * @~
   */
  public String getAppVersion() {

    return this.appVersion;
  }

  public int getAppBuild() {

    return this.appBuild;
  }

  /**
   * @return @~english The identifier of the application. @~german Der Identifier der Anwendung.
   * @~english The identifier of the application as defined under AndroidManifest.xml \<manifest package\>.
   * @~german Der Identifier der Anwendung, wie definiert unter AndroidManifest.xml \<manifest package\>.
   * @~
   */
  public String getAppIdentifier() {

    return this.appIdentifier;
  }
}
