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

/* util imports */
import java.util.UUID;

/* android imports */
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * @author VX STATS <sales@vxstats.com>
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

  private final Context context;

  private String manufacturer;

  private String model;

  private String modelVersion;

  private String name;

  private String version;

  private String uniqueIdentifier;

  private Device( Activity activity ) {

    context = activity.getApplicationContext();

    setUniqueIdentifier();
    setManufacturer();
    setModel();
    setModelVersion();
    setName();
    setVersion();
  }

  private void setUniqueIdentifier() {

    String tmDevice = "";
    String tmSerial = "";
    final TelephonyManager tm = ( TelephonyManager ) context.getSystemService( Context.TELEPHONY_SERVICE );
    if ( tm != null ) {

      tmDevice = tm.getDeviceId();
      tmSerial = tm.getSimSerialNumber();
    }

    final String androidId;
    androidId = android.provider.Settings.Secure.getString( context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID );

    UUID deviceUuid = new UUID( androidId.hashCode(), ( ( long )tmDevice.hashCode() << 32 ) | tmSerial.hashCode() );
    this.uniqueIdentifier = deviceUuid.toString();
  }

  private void setManufacturer() {

    String buildManufacturer = Build.MANUFACTURER;
    if ( buildManufacturer.equals( "unknown" ) ) {

      buildManufacturer = "Android Simulator";
    }
    this.manufacturer = buildManufacturer;
  }

  private void setModel() {

    String buildModel = Build.MODEL;
    if ( buildModel.contains( this.manufacturer ) ) {

      buildModel = buildModel.replace( this.manufacturer, "" );
      buildModel = buildModel.trim();
    }
    this.model = buildModel;
  }

  private void setModelVersion() {

    String buildModel = Build.MODEL;
    if ( buildModel.contains( this.model ) ) {

      buildModel = buildModel.replace( this.model, "" );
      buildModel = buildModel.trim();
    }
    this.modelVersion = buildModel;
  }

  private void setName() {

    this.name = "Android";
  }

  private void setVersion() {

    this.version = android.os.Build.VERSION.RELEASE;
  }

  /**
   * @param activity @~english Current activity to resolve the context. @~german Aktuelle Activity für den Kontext.
   * @return @~english The instance for the current device. @~german Die Instanz für das aktuelle Gerät.
   * @~english Object for the current device.
   * @~german Objekt für das aktuelle Gerät.
   * @~
   * @~
   */
  public static synchronized Device instance( Activity activity ) {

    if ( instance == null ) {

      instance = new Device( activity );
    }
    return instance;
  }

  public static void destroy() {

    if ( instance != null ) {

      instance = null;
    }
  }

  /**
   * @return @~english An unique ID of the device. @~german Eine eindeutige ID des Gerätes.
   * @~english An unique ID of the device, also known as UDID.
   * @~german Eine eindeutige ID des Gerätes oder auch bekannt als UDID.
   * @~
   */
  public String getUniqueIdentifier() {

    return this.uniqueIdentifier;
  }

  /**
   * @return @~english The device vendor. @~german Der Gerätehersteller.
   * @~english The device, e.g. HTC, Samsung, etc.
   * @~german Das Gerät, z.B. HTC, Samsung, usw.
   * @~
   */
  public String getManufacturer() {

    return this.manufacturer;
  }

  /**
   * @return @~english The device. @~german Das Gerät.
   * @~english The device, e.g. Desire, IT-9000
   * @~german Das Gerät, z.B. Desire, IT-9000
   * @~
   */
  public String getModel() {

    return this.model;
  }

  /**
   * @return @~english The version of the device. @~german Die Version des Gerätes.
   * @~english The version of the device, e.g. Desire, IT-9000
   * @~german Die Version des Gerätes, z.B. Desire, IT-9000
   * @~
   */
  public String getModelVersion() {

    return this.modelVersion;
  }

  /**
   * @return @~english The name of the operating system. @~german Der Name des Betriebssystems.
   * @~english The name of the operating system, e.g. Android
   * @~german Der Name des Betriebssystems, z.B. Android
   * @~
   */
  public String getName() {

    return this.name;
  }

  /**
   * @return @~english The version of the operating system. @~german Die Version des Betriebssystems.
   * @~english The version of the operating system, e.g. 2.2
   * @~german Die Version des Betriebssystems, z.B. 2.2
   * @~
   */
  public String getVersion() {

    return this.version;
  }
}
