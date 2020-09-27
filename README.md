* [Preparation](#preparation)
* [Implementation](#implementation)
   * [Pre-Setup](#pre-setup)
   * [Setup](#setup)
   * [Page](#page)
   * [Event](#event)
      * [Ads](#ads)
      * [Move](#move)
      * [Open](#open)
      * [Play](#play)
      * [Search](#search)
      * [Shake](#shake)
      * [Touch](#touch)
* [Compatiblity](#compatiblity)
   * [Android](#android)

# Preparation
Checkout and open project with Android Studio.

# Implementation
## Pre-Setup
All values are defined over AndroidManifest.xml and used from there.

## Setup
Setup your environment with your credentials. Please insert your username, password and url here. For defuscation please follow our best practice documentation.
```java
Statistics.instance(this).setUsername("sandbox");
Statistics.instance(this).setPassword("sandbox");
Statistics.instance(this).setServerFilePath("https://sandbox.vxapps.com/");
```

## Page
This is the global context, where you are currently on in your application. Just name it easy and with logical app structure to identify where the user stays.
```java
Statistics.instance(this).page("Main");
```

## Event
When you would like to request a page with dynamic content please use this function.
```java
Statistics.instance(this).event("action", "value");
```

### Ads
To capture ads - correspondingly the shown ad.
```java
Statistics.instance(this).ads("$campain");
```

### Move
To capture map shifts - correspondingly the new center.
```java
Statistics.instance(this).move($latitude, $longitude);
```

### Open
To capture open websites or documents including the information which page or document has been requested.
```java
Statistics.instance(this).open("$urlOrName");
```

### Play
To capture played files including the information which file/action has been played.
```java
Statistics.instance(this).play("$urlOrName");
```

### Search
To capture searches including the information for which has been searched.
```java
Statistics.instance(this).search("$search");
```

### Shake
To capture when the device has been shaken.
```java
Statistics.instance(this).shake();
```

### Touch
To capture typed/touched actions.
```java
Statistics.instance(this).touch("$action");
```

# Compatiblity
## Android
- API 16 4.1.1 and above x
