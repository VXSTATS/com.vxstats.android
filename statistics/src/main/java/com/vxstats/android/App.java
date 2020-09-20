/*
 * Copyright (C) 01/10/2020 VX APPS <sales@vxapps.com>
 *
 * The ownership of this document rests with the VX APPS. It is
 * strictly prohibited to change, sell or publish it in any way. In case
 * you have access to this document, you are obligated to ensure its
 * nondisclosure. Noncompliances will be prosecuted.
 *
 * Diese Datei ist Eigentum der VX APPS. Ändern, verkaufen oder
 * auf eine andere Weise verbreiten und öffentlich machen ist strikt
 * untersagt. Falls Sie Zugang zu dieser Datei haben, sind Sie
 * verpflichtet alles Mögliche für deren Geheimhaltung zu tun.
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
 * @author VX APPS <sales@vxapps.com>
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

  private Context m_ctx;

  private String m_appName;

  private String m_appVersion;

  private int m_appBuild;

  private String m_appIdentifier = "";

  private App( Activity activity ) {

    m_ctx = activity.getApplicationContext();

    setAppName();
    setAppIdent();
    setAppVersion();
    setAppBuild();
  }

  private void setAppName() {

    String mAppName;
    mAppName = ( String ) m_ctx.getPackageManager().getApplicationLabel( m_ctx.getApplicationInfo() );

    this.m_appVersion = mAppName;
  }

  private void setAppIdent() {

    if ( m_appIdentifier.equals( "" ) ) {

      try {

        String pkg = m_ctx.getPackageName();
        m_appIdentifier = m_ctx.getPackageManager().getPackageInfo( pkg, 0 ).packageName;
      }
      catch ( NameNotFoundException e ) {

        e.printStackTrace();
        Log.e( "Error", "Bad implementation of packageName. Check your AndroidManifest!" );
      }
    }
  }

  private void setAppVersion() {

    try {

      String pkg = m_ctx.getPackageName();
      m_appVersion = m_ctx.getPackageManager().getPackageInfo( pkg, 0 ).versionName;
    }
    catch ( NameNotFoundException e ) {

      e.printStackTrace();
      Log.e( "Error", "Bad implementation of versionName. Check your AndroidManifest!" );
    }
  }

  private void setAppBuild() {

    try {

      String pkg = m_ctx.getPackageName();
      m_appBuild = m_ctx.getPackageManager().getPackageInfo( pkg, 0 ).versionCode;
    }
    catch ( NameNotFoundException e ) {

      e.printStackTrace();
      Log.e( "Error", "Bad implementation of versionCode. Check your AndroidManifest!" );
    }
  }

  /**
   * @~english Set the App Identifier manual.
   * @~german Setzt den Anwendungs Identifier manuel.
   */
  public void setAppIdentifier( String name ) {

    if ( name.length() > 0 )
      m_appIdentifier = name;
  }

  /**
   * @param activity @~english Current activity to resolve the context. @~german Aktuelle Activity für den Kontext.
   * @return @~english The instance for the current application. @~german Die Instanz für die aktuelle Anwendung.
   * @~english Object for the current application.
   * @~german Objekt für die aktuelle Anwendung.
   * @~
   * @~
   */
  public synchronized static App instance( Activity activity ) {

    if ( instance == null )
      instance = new App( activity );
    return instance;
  }

  public void destroy() {

    if ( instance != null )
      instance = null;
  }

  /**
   * @return @~english The name of the application. @~german Der Name der Anwendung.
   * @~english The name of the application as defined under AndroidManifest.xml \<application android:label\>.
   * @~german Der Name der Anwendung, wie definiert unter AndroidManifest.xml \<application android:label\>.
   * @~
   */
  public String getAppName() {

    return this.m_appName;
  }

  /**
   * @return @~english The version of the application. @~german Die Version der Anwendung.
   * @~english The version of the application as defined under AndroidManifest.xml \<manifest android:versionName\>.
   * @~german Die Version der Anwendung, wie definiert unter AndroidManifest.xml \<manifest android:versionName\>.
   * @~
   */
  public String getAppVersion() {

    return this.m_appVersion;
  }

  public int getAppBuild() {

    return this.m_appBuild;
  }

  /**
   * @return @~english The identifier of the application. @~german Der Identifier der Anwendung.
   * @~english The identifier of the application as defined under AndroidManifest.xml \<manifest package\>.
   * @~german Der Identifier der Anwendung, wie definiert unter AndroidManifest.xml \<manifest package\>.
   * @~
   */
  public String getAppIdentifier() {

    return this.m_appIdentifier;
  }
}
