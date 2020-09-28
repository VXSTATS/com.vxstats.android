/*
 * Copyright (C) 10/01/2020 VX APPS <sales@vxapps.com>
 *
 * This document is property of VX APPS. It is strictly prohibited
 * to modify, sell or publish it in any way. In case you have access
 * to this document, you are obligated to ensure its nondisclosure.
 * Noncompliances will be prosecuted.
 *
 * Diese Datei ist Eigentum der VX APPS. Jegliche Änderung, Verkauf
 * oder andere Verbreitung und Veröffentlichung ist strikt untersagt.
 * Falls Sie Zugang zu dieser Datei haben, sind Sie verpflichtet,
 * alles in Ihrer Macht stehende für deren Geheimhaltung zu tun.
 * Zuwiderhandlungen werden strafrechtlich verfolgt.
 */

/* local package */
package com.vxstats.android;

/* util imports */
import java.util.UUID;

/* android imports */
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * @author VX APPS <sales@vxapps.com>
 * @version 1.0
 * @~english Information of the device.
 * @~german Informationen über das Gerät.
 * @~
 * @brief @~english Information of the device. @~german Informationen über das Gerät.
 * @~
 * @date 01/09/2011
 */
public class Device {

  private static Device instance = null;

  private final Context m_ctx;

  private String m_manufacturer;

  private String m_model;

  private String m_modelVersion;

  private String m_name;

  private String m_version;

  private String m_uniqueIdentifier;

  private Device( Activity activity ) {

    m_ctx = activity.getApplicationContext();

    setUniqueIdentifier();
    setManufacturer();
    setModel();
    setModelVersion();
    setName();
    setVersion();
  }

  private void setUniqueIdentifier() {

    final String tmDevice, tmSerial;
    final TelephonyManager tm = ( TelephonyManager )m_ctx.getSystemService( Context.TELEPHONY_SERVICE );
    if ( tm != null ) {

      tmDevice = tm.getDeviceId();
      tmSerial = tm.getSimSerialNumber();
    }

    final String androidId;
    androidId = android.provider.Settings.Secure.getString( m_ctx.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID );

    UUID deviceUuid = new UUID( androidId.hashCode(), ( ( long )tmDevice.hashCode() << 32 ) | tmSerial.hashCode() );
    this.m_uniqueIdentifier = deviceUuid.toString();
  }

  private void setManufacturer() {

    String manufacturer = Build.MANUFACTURER;
    if ( manufacturer.equals( "unknown" ) )
      manufacturer = "Android Simulator";
    this.m_manufacturer = manufacturer;
  }

  private void setModel() {

    String model = Build.MODEL;
    if ( model.contains( this.m_manufacturer + " " ) )
      model = model.replace( this.m_manufacturer + " ", "" );
    this.m_model = model;
  }

  private void setModelVersion() {

    String version = Build.MODEL;
    if ( version.contains( this.m_model + " " ) )
      version = version.replace( this.m_model + " ", "" );
    this.m_modelVersion = version;
  }

  private void setName() {

    this.m_name = "Android";
  }

  private void setVersion() {

    this.m_version = android.os.Build.VERSION.RELEASE;
  }

  /**
   * @param activity @~english Current activity to resolve the context. @~german Aktuelle Activity für den Kontext.
   * @return @~english The instance for the current device. @~german Die Instanz für das aktuelle Gerät.
   * @~english Object for the current device.
   * @~german Objekt für das aktuelle Gerät.
   * @~
   * @~
   */
  public synchronized static Device instance( Activity activity ) {

    if ( instance == null )
      instance = new Device( activity );
    return instance;
  }

  public void destroy() {

    if ( instance != null )
      instance = null;
  }

  /**
   * @return @~english An unique ID of the device. @~german Eine eindeutige ID des Gerätes.
   * @~english An unique ID of the device, also known as UDID.
   * @~german Eine eindeutige ID des Gerätes oder auch bekannt als UDID.
   * @~
   */
  public String getUniqueIdentifier() {

    return this.m_uniqueIdentifier;
  }

  /**
   * @return @~english The device vendor. @~german Der Gerätehersteller.
   * @~english The device, e.g. HTC, Samsung, etc.
   * @~german Das Gerät, z.B. HTC, Samsung, usw.
   * @~
   */
  public String getManufacturer() {

    return this.m_manufacturer;
  }

  /**
   * @return @~english The device. @~german Das Gerät.
   * @~english The device, e.g. Desire, IT-9000
   * @~german Das Gerät, z.B. Desire, IT-9000
   * @~
   */
  public String getModel() {

    return this.m_model;
  }

  /**
   * @return @~english The version of the device. @~german Die Version des Gerätes.
   * @~english The version of the device, e.g. Desire, IT-9000
   * @~german Die Version des Gerätes, z.B. Desire, IT-9000
   * @~
   */
  public String getModelVersion() {

    return this.m_modelVersion;
  }

  /**
   * @return @~english The name of the operating system. @~german Der Name des Betriebssystems.
   * @~english The name of the operating system, e.g. Android
   * @~german Der Name des Betriebssystems, z.B. Android
   * @~
   */
  public String getName() {

    return this.m_name;
  }

  /**
   * @return @~english The version of the operating system. @~german Die Version des Betriebssystems.
   * @~english The version of the operating system, e.g. 2.2
   * @~german Die Version des Betriebssystems, z.B. 2.2
   * @~
   */
  public String getVersion() {

    return this.m_version;
  }
}
