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

/* java imports */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;

/**
 * @author VX STATS <sales@vxstats.com>
 * @version 1.0
 * @~english
 * @b General:
 * @n The class communicates to the statistics server in order to transfer a page impression or an action/event.
 * @b Security:
 * @n There is a multi-level security concept:
 * @n 1. Communication must be authenticated via htaccess.
 * @n 2. Configuration should be carried out via HTTPS only so that all data is encrypted and cannot be manipulated.
 * @n 3. Communication should be carried out via POST only. Only specific tools make a manipulation of data possible.
 * @n 4. On the server part all values are checked for validity, invalid entries are excluded.
 * @b Threads:
 * @n The class is thread safe and can be executed in MainThread or in a BackgroundThread of the application.
 * The queries are processed asynchronously or synchronously.
 * @b Offline @b entries:
 * @n Statistic entries that have not been sent successfully are filed in a queue of local settings and sent
 * as soon as an internet connection exists. Estimations assume that there is less than 5% not received statistic
 * data.
 * @b Data @b privacy:
 * @n Unique data is processed but no position data and also no user data that can be allocated directly.
 * No third party is involved in the processing of data. The system with default settings is configured
 * with security and anonymity.
 * @b Application:
 * @n Besides an API for iPhone/iPad/iPod touch and Android, e.g. C#, C, C++, PHP, JavaScript and Java is also
 * supported. Further formats can be supported upon request.
 * @~german
 * @b Allgemein:
 * @n Diese Klasse kommuniziert zum Statistikserver um eine Seitenimpression oder eine Aktion/Event zu übertragen.
 * @b Sicherheit:
 * @n Es ist ein mehrstufiges Sicherheitskonzept vorhanden:
 * @n 1. Kommunikation muss authentifiziert werden über htaccess.
 * @n 2. Konfiguration sollte nur über HTTPS erfolgen, damit werden alle Daten verschlüsselt und können nicht
 * manipuliert werden.
 * @n 3. Kommunikation erfolgt ausschließlich über POST. Erst spezielle Tools erlauben somit eine Manipulation von
 * Daten.
 * @n 4. Serverseitig werden alle Werte auf Gültigkeit überprüft, ungültige Einträge sind ausgeschlossen.
 * @b Threads:
 * @n Die Klasse ist Thread safe und kann sowohl im MainThread ausgeführt werden oder in einem BackgroundThread der
 * Anwendung. Die Anfragen werden entsprechend asynchron oder synchron abgearbeitet.
 * @b Offline-Einträge:
 * @n Nicht erfolgreich versendete Statistikeinträge werden in einer Queue der lokalen Einstellungen abgelegt und
 * versendet sobald wieder eine Internetverbindung besteht. Schätzungen gehen von weniger als 5% nicht empfangener
 * Statistikdaten aus.
 * @b Datenschutz:
 * @n Es werden zwar eindeutige Daten verarbeitet, aber keine Positionsdaten und auch keine direkt zuordenbare
 * Benutzerdaten. Sie sind Herr der Daten, es ist kein Dritter bei der Verarbeitung der Daten involviert. Das
 * System in der Standardkonfiguration ist auf Sicherheit und Anonymität ausgelegt.
 * @b Verwendung:
 * @n Neben einer API für iPhone/iPad/iPod touch und Android wird auch z.B. C#, C, C++, PHP, JavaScript und Java
 * unterstützt. Weitere Formate erstellen wir natürlich gerne auf Anfrage.
 * @~english @b Example: @~german @b Beispiel:
 * @~english @n 1. Define a path to the statistics server. @~german @n 1. Angeben eines Pfads zum Statistikserver.
 * @~
 * @code Statistics.instance(this).setServerFilePath("https://sandbox.vxstats.com");
 * @endcode
 * @~english 2. Transfer page impression. @~german 2. Seitenimpression übermitteln.
 * @~
 * @code Statistics.instance(this).page("MyPage");
 * @endcode
 * @~english 3. Transfer action. @~german 3. Aktion übermitteln.
 * @~
 * @code Statistics.instance(this).event("action", "value");
 * @endcode
 * @~
 * @brief @~english Communication with the statistics server. @~german Kommunikation mit dem Statistikserver.
 * @~
 * @date 10/01/2020
 */
public class Statistics {

  private static Statistics instance = null;

  private Activity activity;

  private final Context context;

  /**
   * @~english Checking connection in order to determine the connection speed or to transfer pending data.
   * @~german Überprüfen der Verbindung um die Verbindungsgeschwindigkeit zu ermitteln oder ausstehende Daten zu übermitteln.
   */
  private final Reachability reachability;

  /**
   * @~english Path to statistics server.
   * @~german Der Pfad zum Statistikserver.
   */
  private String serverFilePath;

  /**
   * @~english The last used page is buffered in order to use the actions and search comfortably.
   * @~german Die zuletzt verwendete Seite wird zwischengespeichert um die Aktionen und Suchen komfortabel zu verwenden.
   */
  private String lastPageName;

  private String username = "";

  private String password = "";

  private String event = "";

  private String value = "";

  /**
   * @~english The current network state.
   * @~german Der aktuelle Netzwerkstatus.
   */
  private String status = "Offline";

  private final List<String> messageQueue = new ArrayList<>();

  private InitTask initTask;

  private Statistics( Activity activity ) {

    context = activity.getApplicationContext();

    App.instance( activity );
    Device.instance( activity );
    reachability = new Reachability( activity );
  }

  /**
   * @param activity @~english Current activity to resolve the context. @~german Aktuelle Activity für den Kontext.
   * @return @~english The instance for statistics. @~german Die Instanz für Statistiken.
   * @~english The statistics system is initialized and creates a database for offline statistics that is only been sent
   * if a connection to the statistics server could be established. Please provide necessary settings for the
   * communication to the server, its path and which explicit values should also be collected.
   * <p>
   * The communication to the server needs a token in order to get the authorization to capture statistic values.
   * The complete communication is transfered by http(s) protocol via POST.
   * @~german Das Statistisystem wird initaliziert und erstellt eine Datenbank für Offline Statistik, die nur versendet wird,
   * wenn eine Verbindung zum Statistikserver hergestellt werden konnte. Bitte hinterlegen Sie nötige Einstellungen
   * für die Kommunikation zum Server, dessen Pfad und welche explizieten Werte ebenfalls gesammelt werden sollen.
   * <p>
   * Die Kommunikation zum Server verlangt einen Token um die Erlaubnis zu erhalten statistische Werte zu erfassen.
   * Die gesamte Kommunikation wird über das http(s) Protokoll via POST übermittelt.
   * @~
   * @~
   */
  public static synchronized Statistics instance( Activity activity ) {

    if ( instance == null ) {

      instance = new Statistics( activity );
    }
    return instance;
  }

  public void destroy() {

    if ( instance != null ) {

      App.instance( activity ).destroy();
      Device.instance( activity ).destroy();
      instance = null;
    }
  }

  /**
   * @param serverFilePath @~english The file name to the statistics server. @~german Der Dateiname zum Statistikserver.
   * @~english Defines the path and name to the statistics server.
   * @~german Definiert den Pfad und Namen zum Statistikserver.
   * @~english @b Example: @~german @b Beispiel:
   * @~english @n for the HTTPS address www.vxstats.com and the folder u/stats.php.
   * @~german @n Für die HTTPS Adresse www.vxstats.com und dem Verzeichnis u/stats.php.
   * @~
   * @code Statistics.instance(this).serverFilePath("https://www.vxstats.com/u/stats.php");
   * @endcode
   * @~
   */
  public void setServerFilePath( String serverFilePath ) {

    if ( serverFilePath.contains( "@" ) ) {

      String[] login;
      URL url;

      try {

        url = new URL( serverFilePath );
      }
      catch ( MalformedURLException e ) {

        e.printStackTrace();
        this.serverFilePath = serverFilePath;
        return;
      }

      String path = url.getPath();
      String host = url.getHost();
      String proto = url.getProtocol();
      String auth = url.getUserInfo();

      login = auth.split( ":" );
      username = login[ 0 ];
      password = login[ 1 ];
      serverFilePath = proto + "://" + host + path;
    }
    this.serverFilePath = serverFilePath;
  }

  public void setUsername( String username ) {

    this.username = username;
  }

  public void setPassword( String password ) {

    this.password = password;
  }

  /**
   * @param pageName @~english The name of the requested page. Limited to 255 characters. @~german Der Name der aufgerufenen Seite. Auf 255 Zeichen begrentzt.
   * @~german Aufruf einer Seite mit dem Namen pageName um es an den Statistikserver zu übermitteln.
   * @~english Request a page with the name pageName in order to transfer it to the statistics server.
   * @~
   */
  public void page( String pageName ) {

    if ( pageName.equals( "" ) ) {

      Log.i( "STATISTICS", "Bad implementation - page with empty 'pageName'" );
      return;
    }

    if ( pageName.length() > 255 ) {

      Log.i( "STATISTICS", "Bad implementation - 'pageName': " + pageName + " is larger than 255 signs" );
      pageName = pageName.substring( 0, 255 );
    }

    pageName = pageName.replace( "&", "%26" );
    pageName = pageName.replace( "'", "%2F" );
    pageName = pageName.replace( "|", "%7C" );

    lastPageName = pageName;

    event( "", "" );
  }

  /**
   * @param eventName @~english The event. @~german Das Event.
   * @param value     @~english The value for the event. @~german Der Wert für das Event.
   * @~english When you would like to request a page with dynamic content please use this function.
   * @~german Wenn Sie eine Seite aufrufen möchten mit dynamischem Inhalt verwenden Sie diese Funktion.
   * @~english @b Example: @~german @b Beispiel:
   * @~english @n Page with ads. @~german @n Seite mit Werbung.
   * @~
   * @code Statistics.instance(this).event("ads", campaign);
   * Statistics.instance(this).event("ads", "Google");
   * @endcode
   * @code Statistics.instance(this).ads(campaign);
   * @endcode
   * @~english @b Example: @~german @b Beispiel:
   * @~english @n Move map to geo position. @~german @n Karte auf Geoposition verschieben.
   * @~
   * @code Statistics.instance(this).event("move", latitude, longitude);
   * Statistics.instance(this).event("move", "52.523405,13.411400";
   * @endcode
   * @code Statistics.instance(this).move(latitude, longitude);
   * @endcode
   * @~english @b Example: @~german @b Beispiel:
   * @~english @n Open browser with URL. @~german @n Browser mit URL.
   * @~
   * @code Statistics.instance(this).event("open", urlOrName);
   * Statistics.instance(this).event("open", "https://www.vxstats.com");
   * @endcode
   * @code Statistics.instance(this).open(urlOrName);
   * @endcode
   * @~english @b Example: @~german @b Beispiel:
   * @~english @n Play video. @n @~german Video abspielen.
   * @~
   * @code Statistics.instance(this).event("play", urlOrName);
   * Statistics.instance(this).event("play", "https://www.vxstats.com/movie.m4v");
   * @endcode
   * @code Statistics.instance(this).play(urlOrName);
   * @endcode
   * @~english @b Example: @~german @b Beispiel:
   * @~english @n Search for 'asdf'. @~german @n Suchen nach 'asdf'.
   * @~
   * @code Statistics.instance(this).event("search", text);
   * Statistics.instance(this).event("search", "asdf");
   * @endcode
   * @code Statistics.instance(this).search(text);
   * @endcode
   * @~english @b Example: @~german @b Beispiel:
   * @~english @n Shake the device. @~german @n Das Gerät schütteln.
   * @~
   * @code Statistics.instance(this).event("shake", "");
   * @endcode
   * @code Statistics.instance(this).shake();
   * @endcode
   * @~english @b Example: @~german @b Beispiel:
   * @~english @n Touch the button for navigation. @~german @n Button für Navitation drücken.
   * @~
   * @code Statistics.instance(this).event("touch", action);
   * Statistics.instance(this).event("touch", "Navigation");
   * @endcode
   * @code Statistics.instance(this).touch(action);
   * @endcode
   * @~
   * @~
   * @see
   * @see
   * @see
   * @see
   * @see
   * @see
   * @see
   */
  public void event( String eventName, String value ) {

    if ( lastPageName.equals( "" ) ) {

      Log.i( "STATISTICS", "Bad implementation - 'event': " + eventName + " with empty 'pageName'" );
    }

    if ( ! eventName.equals( "" ) ) {

      eventName = eventName.replace( "&", "%26" );
      eventName = eventName.replace( "'", "%2F" );
      eventName = eventName.replace( "|", "%7C" );
      event = eventName;
    }
    else {

      event = "";
    }

    if ( ! value.equals( "" ) ) {

      value = value.replace( "&", "%26" );
      value = value.replace( "'", "%2F" );
      value = value.replace( "|", "%7C" );
      this.value = value;
    }
    else {

      this.value = "";
    }

    String core = coreMessage();
    //addOutstandingMessage(core);
    initTask = new InitTask();
    initTask.execute( core );

    if ( ! status.equals( reachability.getConnectionType() ) ) {

      reachabilityChanged();
    }
  }

  /**
   * @param campaign @~english The displayed ad. Limited to 255 characters. @~german Die angezeigte Werbung. Auf 255 Zeichen begrentzt.
   * @~english To capture ads - correspondingly the shown ad.
   * @~german Für das Erfassen von Werbeeinblendungen - entsprechend die angezeigte Werbung.
   * @~
   * @code Statistics.instance(this).event("ads", campaign);
   * @endcode
   * @~
   * @see
   */
  public void ads( String campaign ) {

    if ( campaign.equals( "" ) ) {

      Log.i( "STATISTICS", "Bad implementation - 'ads' with empty 'campaign' name" );
    }
    if ( campaign.length() > 255 ) {

      Log.i( "STATISTICS", "Bad implementation - 'campaign': " + campaign + " is larger than 255 signs" );
      campaign = campaign.substring( 0, 255 );
    }
    event( "ads", campaign );
  }

  /**
   * @param latitude  @~english Latitude of center. @~german Latitude des Zentrums.
   * @param longitude @~english Longitude of center. @~german Longitude des Zentrums.
   * @~english To capture map shifts - correspondingly the new center.
   * @~german Für die Erfassung von Kartenverschiebungen - entsprechend das neue Zentrum.
   * @~
   * @code Statistics.instance(this).event("move", latitude, longitude);
   * @endcode
   * @~
   * @~
   * @see
   */
  public void move( float latitude, float longitude ) {

    if ( latitude == 0.0 || longitude == 0.0 ) {

      Log.i( "STATISTICS", "Bad implementation - 'move' with empty 'latitude' or 'longitude'" );
    }
    event( "move", latitude + "," + longitude );
  }

  /**
   * @param urlOrName @~english The displayed website/document. Limited to 255 characters. @~german Die angezeigte Webseite/das angezeigte Dokument. Auf 255 Zeichen begrentzt.
   * @~english To capture open websites or documents including the information which page or document has been requested.
   * @~german Für das Erfassen von geöffneten Webseiten oder Dokumenten mit der Information, welche Seite, bzw.
   * welches Dokument aufgerufen wurde.
   * @~
   * @code Statistics.instance(this).event("open", urlOrName);
   * @endcode
   * @~
   * @see
   */
  public void open( String urlOrName ) {

    if ( urlOrName.equals( "" ) ) {

      Log.i( "STATISTICS", "Bad implementation - 'open' with empty 'urlOrName'" );
    }
    if ( urlOrName.length() > 255 ) {

      Log.i( "STATISTICS", "Bad implementation - 'urlOrName': " + urlOrName + " is larger than 255 signs" );
      urlOrName = urlOrName.substring( 0, 255 );
    }
    event( "open", urlOrName );
  }

  /**
   * @param urlOrName @~english The played file. Limited to 255 characters. @~german Die abgespielte Datei. Auf 255 Zeichen begrentzt.
   * @~english To capture played files including the information which file/action has been played.
   * @~german Für das Erfassen von abgespielten Dateien mit der Info, welche Datei/Aktion abgespielt wurde.
   * @~
   * @code Statistics.instance(this).event("play", urlOrName);
   * @endcode
   * @~
   * @see
   */
  public void play( String urlOrName ) {

    if ( urlOrName.equals( "" ) ) {

      Log.i( "STATISTICS", "Bad implementation - 'play' with empty 'urlOrName'" );
    }
    if ( urlOrName.length() > 255 ) {

      Log.i( "STATISTICS", "Bad implementation - 'urlOrName': " + urlOrName + " is larger than 255 signs" );
      urlOrName = urlOrName.substring( 0, 255 );
    }
    event( "play", urlOrName );
  }

  /**
   * @param text @~german Der gesuchte Text. Auf 255 Zeichen begrentzt. @~english The searched text. Limited to 255 characters.
   * @~english To capture searches including the information for which has been searched.
   * @~german Für die Erfassung von Suchen mit der Info, nach was gesucht wurde.
   * @~
   * @code Statistics.instance(this).event("search", text);
   * @endcode
   * @~
   * @see
   */
  public void search( String text ) {

    if ( text.equals( "" ) ) {

      Log.i( "STATISTICS", "Bad implementation - 'search' with empty 'text'" );
    }
    if ( text.length() > 255 ) {

      Log.i( "STATISTICS", "Bad implementation - 'text': " + text + " is larger than 255 signs" );
      text = text.substring( 0, 255 );
    }
    event( "search", text );
  }

  /**
   * @~english To capture when the device has been shaken.
   * @~german Für das Erfassen, wann das Gerät geschüttelt wurde.
   * @~
   * @code Statistics.instance(this).event("shake", "");
   * @endcode
   * @see
   */
  public void shake() {

    event( "shake", "" );
  }

  /**
   * @param action @~english The name of the touched action. Limited to 255 characters. @~german Der Name der getippten/gedrückten Aktion. Auf 255 Zeichen begrentzt.
   * @~english To capture typed/touched actions.
   * @~german Für die Erfassung von getippten/gedrückten Aktionen.
   * @~
   * @code Statistics.instance(this).event("touch", action);
   * @endcode
   * @~
   * @see
   */
  public void touch( String action ) {

    if ( action.equals( "" ) ) {

      Log.i( "STATISTICS", "Bad implementation - 'touch' with empty 'action'" );
    }
    if ( action.length() > 255 ) {

      Log.i( "STATISTICS", "Bad implementation - 'action': " + action + " is larger than 255 signs" );
      action = action.substring( 0, 255 );
    }
    event( "touch", action );
  }

  private String coreMessage() {

    String core;

    /* device */
    core = "uuid=" + Device.instance( activity ).getUniqueIdentifier() + "&";
    core += "os=" + Device.instance( activity ).getName() + "&";
    core += "osversion=" + Device.instance( activity ).getVersion() + "&";
    core += "vendor=" + Device.instance( activity ).getManufacturer() + "&";
    core += "model=" + Device.instance( activity ).getModel() + "&";
    core += "modelversion=" + Device.instance( activity ).getModelVersion() + "&";

    /* locale */
    String locale = Locale.getDefault().toString();
    String[] split = locale.split( "_" );
    String language = split[ 0 ];
    String country = split[ 1 ];

    core += "language=" + language + "&";
    core += "country=" + country + "&";

    /* connection */
    core += "connection=" + reachability.getConnectionType() + "&";
    core += "radio=" + reachability.getRadioType() + "&";

    /* app block */
    core += "appid=" + App.instance( activity ).getAppIdentifier() + "&";
    core += "appversion=" + App.instance( activity ).getAppVersion() + "&";
    core += "appbuild=" + App.instance( activity ).getAppBuild() + "&";

    int nightModeFlag = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    if ( nightModeFlag == Configuration.UI_MODE_NIGHT_YES ) {

      core += "dark=1&";
    }

    /* is this app fairly used */
//    core += "TODO: fair=" + "1" + "&";

    //is the device jailbroken?
//    core += "TODO: free=" + "0" + "&";

    core += "tabletmode=1&";
    if ( context.getPackageManager().hasSystemFeature( "android.hardware.touchscreen" ) ) {

      core += "touch=1&";
    }

    AccessibilityManager am = ( AccessibilityManager ) context.getSystemService( Context.ACCESSIBILITY_SERVICE );
    if ( am != null && am.isTouchExplorationEnabled() ) {

      core += "voiceover=1&";
    }

    WindowManager wm = ( WindowManager ) context.getSystemService( Context.WINDOW_SERVICE );
    if ( wm != null ) {

      Display display = wm.getDefaultDisplay();
      if ( display != null ) {

        Point size = new Point();
        display.getSize( size );
        core += "width=" + size.x + "&";
        core += "height=" + size.y + "&";

        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        core += "dpr=" + metrics.density + "&";
      }
    }

    core += "created=" + System.currentTimeMillis() / 1000L + "&";

    /* data block */
    core += "page=" + lastPageName;
    if ( ! event.equals( "" ) ) {

      core += "&action=" + event;
    }
    if ( ! value.equals( "" ) ) {

      core += "&value=" + value;
    }
    return core;
  }

  private void reachabilityChanged() {

    status = reachability.getConnectionType();
    if ( ! status.equals( "Offline" ) ) {

      sendOutstandingMessage();
    }
  }

  private void addOutstandingMessage( String message ) {

    messageQueue.add( message );
  }

  private void writeOutstandingMessages() {

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( context );
    String messages = preferences.getString( "offline", "" );

    List<String> tempMessageQueue = new ArrayList<>();
    if ( messages != null ) {

      String[] splitArray = messages.split( "\\|" );
      if ( ! splitArray[ 0 ].equals( "" ) ) {

        tempMessageQueue.addAll( Arrays.asList( splitArray ) );
      }
    }

    if ( !messageQueue.isEmpty() ) {

      tempMessageQueue.addAll( messageQueue );
      messageQueue.clear();
    }

    StringBuilder stringBuilder = new StringBuilder();
    for ( int i = 0; i < tempMessageQueue.size(); i++ ) {

      if ( i > 0 ) {

        stringBuilder.append( "|" );
      }
      stringBuilder.append( tempMessageQueue.get( i ) );
    }
    messages = stringBuilder.toString();

    SharedPreferences.Editor editor = preferences.edit();
    editor.putString( "offline", messages );
    editor.apply();
  }

  private void sendOutstandingMessage() {

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( context );
    String messages = preferences.getString( "offline", "" );

    SharedPreferences.Editor editor = preferences.edit();
    editor.putString( "offline", "" );
    editor.apply();

    if ( messages != null && messages.length() != 0 ) {

      initTask = new InitTask();
      initTask.execute( messages );
    }
  }

/*  private class SSLHostnameVerifier implements HostnameVerifier {

    public boolean verify(String hostname, SSLSession session) {

      if(hostname.equals(session.getPeerHost()))
        return true;
      else
        return false;
    }
  }*/

  private static class SSLTrustManager implements X509TrustManager {

    public void checkClientTrusted( X509Certificate[] chain, String authType ) {

    }

    public void checkServerTrusted( X509Certificate[] chain, String authType ) {

    }

    public java.security.cert.X509Certificate[] getAcceptedIssuers() {

      return null;
    }

    public void checkClientTrusted( java.security.cert.X509Certificate[] arg0, String arg1 ) throws CertificateException {

    }

    public void checkServerTrusted( java.security.cert.X509Certificate[] chain, String authType ) throws CertificateException {

    }
  }

  private InputStream UrlConnection( String url, String data ) throws KeyManagementException, IOException {

    InputStream stream = null;
    if ( url.contains( "https" ) ) {

      try {

        /* TODO: Insert digest auth: https://stackoverflow.com/a/40079688 */
        HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.STRICT_HOSTNAME_VERIFIER;
        SSLContext sc = SSLContext.getInstance( "TLSv1.2" );
        sc.init( null, new TrustManager[]{ new SSLTrustManager() }, new SecureRandom() );
        HttpsURLConnection.setDefaultSSLSocketFactory( sc.getSocketFactory() );
        HttpsURLConnection.setDefaultHostnameVerifier( hostnameVerifier );

        HttpsURLConnection connection = ( HttpsURLConnection ) new URL( url ).openConnection();
        Authenticator.setDefault(new Authenticator() {

          @Override
          protected PasswordAuthentication getPasswordAuthentication() {

            return new PasswordAuthentication(username, password.toCharArray());
          }
        });
        connection.setRequestMethod( "POST" );
        connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );
        connection.setDoOutput( true );

        OutputStreamWriter streamWriter = new OutputStreamWriter( connection.getOutputStream() );
        streamWriter.write( data );
        streamWriter.flush();

        stream = connection.getInputStream();

        streamWriter.close();
      }
      catch ( NoSuchAlgorithmException exception ) {

        exception.printStackTrace();
      }
    }
    else {

      HttpURLConnection connection = ( HttpURLConnection ) new URL( url ).openConnection();
      Authenticator.setDefault(new Authenticator() {

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {

          return new PasswordAuthentication(username, password.toCharArray());
        }
      });
      connection.setRequestMethod( "POST" );
      connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );
      connection.setDoOutput( true );

      OutputStreamWriter streamWriter = new OutputStreamWriter( connection.getOutputStream() );
      streamWriter.write( data );
      streamWriter.flush();

      stream = connection.getInputStream();

      streamWriter.close();
    }
    return stream;
  }

  protected class InitTask extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground( String... params ) {

      String message = params[ 0 ];
      if ( message.length() > 0 ) {

        String[] splitArray = message.split( "\\|" );
        if ( splitArray.length == 1 ) {

          sendMessage( splitArray[ 0 ] );
        }
        else {

          List<String> tempMessageQueue = new ArrayList<>( Arrays.asList( splitArray ) );

          for ( int i = 0; i < tempMessageQueue.size(); i++ ) {

            sendMessage( tempMessageQueue.get( i ) );
          }
        }
        writeOutstandingMessages();
      }
      return "";
    }

    @Override
    protected void onPreExecute() {

      super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate( Integer... values ) {

      super.onProgressUpdate( values );
    }

    @Override
    protected void onCancelled() {

      super.onCancelled();
    }

    @Override
    protected void onPostExecute( String result ) {

      super.onPostExecute( result );
    }

    private void sendMessage( String message ) {

      if ( ! reachability.getConnectionType().equals( "Offline" ) ) {

        try {

          String url = serverFilePath;
          UrlConnection( url, message );

        }
        catch ( IOException | KeyManagementException exception ) {

          addOutstandingMessage( message );
          exception.printStackTrace();
        }
      }
      else {

        addOutstandingMessage( message );
      }
    }
  }
}
