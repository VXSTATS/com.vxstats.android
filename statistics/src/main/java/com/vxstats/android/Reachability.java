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

/* android imports */
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class Reachability {

  private Context context;

  private NetworkInfo network;

  public String availableNetwork;
  public String availableRadio;

  public Reachability( Activity activity ) {

    context = activity.getApplicationContext();

    ConnectivityManager cm = ( ConnectivityManager ) context.getSystemService( context.CONNECTIVITY_SERVICE );
    network = cm.getActiveNetworkInfo();
  }

  public String getConnectionType() {

    availableNetwork = "Offline";
    if ( network != null ) {

      switch ( network.getType() ) {

        case ConnectivityManager.TYPE_MOBILE:
          availableNetwork = "WWAN";
          break;
        case ConnectivityManager.TYPE_WIFI:
          availableNetwork = "Wifi";
          break;
        case ConnectivityManager.TYPE_WIMAX:
          availableNetwork = "WiMAX";
          break;
        case ConnectivityManager.TYPE_ETHERNET:
          availableNetwork = "Ethernet";
          break;
        case ConnectivityManager.TYPE_BLUETOOTH:
          availableNetwork = "Bluetooth";
          break;
      }
    }
    return availableNetwork;
  }

  public String getRadioType() {

    availableRadio = "None";
    if ( network != null ) {

      /* 'None','GPRS','Edge','WCDMA','HSDPA','HSUPA','CDMA1x','CDMAEVDORev0','CDMAEVDORevA','CDMAEVDORevB','HRPD','LTE','2G','3G','4G','5G' */
      switch ( network.getSubtype() ) {

        case TelephonyManager.NETWORK_TYPE_1xRTT:
//          availableRadio = "3G";
          break;
        case TelephonyManager.NETWORK_TYPE_CDMA:
          availableRadio = "CDMA1x";
          break;
        case TelephonyManager.NETWORK_TYPE_EDGE:
          availableRadio = "Edge";
          break;
        case TelephonyManager.NETWORK_TYPE_EHRPD:
//          availableRadio = "HRPD";
          break;
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
          availableRadio = "CDMAEVDORev0";
          break;
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
          availableRadio = "CDMAEVDORevA";
          break;
        case TelephonyManager.NETWORK_TYPE_EVDO_B:
          availableRadio = "CDMAEVDORevB";
          break;
        case TelephonyManager.NETWORK_TYPE_GPRS:
          availableRadio = "GPRS";
          break;
        case TelephonyManager.NETWORK_TYPE_HSDPA:
          availableRadio = "HSDPA";
          break;
        case TelephonyManager.NETWORK_TYPE_HSPA:
//          availableRadio = "UMTS";
          break;
        case TelephonyManager.NETWORK_TYPE_HSPAP:
//          availableRadio = "UMTS";
          break;
        case TelephonyManager.NETWORK_TYPE_HSUPA:
          availableRadio = "HSUPA";
          break;
        case TelephonyManager.NETWORK_TYPE_IDEN:
//          availableRadio = "UMTS";
          break;
        case TelephonyManager.NETWORK_TYPE_LTE:
          availableRadio = "LTE";
          break;
        case TelephonyManager.NETWORK_TYPE_UMTS:
          availableRadio = "3G";
          break;
      }
    }
    return availableRadio;
  }
}
