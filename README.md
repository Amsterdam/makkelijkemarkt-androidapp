##Makkelijke Markt Android App

####De app om dagelijks de invulling van de Amsterdamse markten in kaart te brengen

De Makkelijke Markt app is een digitale kassa-app of "bonnenboekje", waarmee de toezichthouders op de markt dagelijks plaatsvergunningen uitgeven. De app berekent de af te rekenen bedragen en registreert de uitgegeven dagvergunningen in een centrale database. Dit maakt directe uitwisseling van gegevens tussen de verschillende markten en toezichthouders mogelijk.

Het marktbureau kan via een beheerinterface de actuele situatie direct inzien, waardoor telefonische vragen van kooplieden beter kunnen worden beantwoord. Ook kan er stuurinformatie worden gegenereerd. Tot slot maakt het centrale financiële controle eenvoudiger.

Meer informatie: [https://www.amsterdam.nl/ondernemen/markt-straathandel/](https://www.amsterdam.nl/ondernemen/markt-straathandel/)


|Home|Markten|Dagvergunningen|Drawer Menu|
|---|---|---|---|
| ![Home](./doc/screenshots/home.png?raw=true "Home") | ![Markten](./doc/screenshots/markten.png?raw=true "Markten") | ![Dagvergunningen](./doc/screenshots/dagvergunningen.png?raw=true "Dagvergunningen") | ![Drawer Menu](./doc/screenshots/menu-drawer.png?raw=true "Drawer Menu") |

|Dagvergunning - Koopman|Dagvergunning - Producten|Dagvergunning - Overzicht|About|
|---|---|---|---|
|![Dagvergunning - Koopman](./doc/screenshots/dagvergunning-koopman.png?raw=true "Dagvergunning - Koopman")| ![Dagvergunning - Producten](./doc/screenshots/dagvergunning-producten.png?raw=true "Dagvergunning - Producten") | ![Dagvergunning - Overzicht](./doc/screenshots/dagvergunning-overzicht.png?raw=true "Dagvergunning - Overzicht") | ![About](./doc/screenshots/about.png?raw=true "About") |

|Nieuwe Dagvergunning|Nieuwe Dagvergunning - Auto-complete|Nieuwe Dagvergunning - Barcode|Nieuwe Dagvergunning - NFC Scannen|
|---|---|---|---|
|![Nieuwe Dagvergunning](./doc/screenshots/dagvergunning-nieuw.png?raw=true "Nieuwe Dagvergunning")| ![Nieuwe Dagvergunning - Auto-complete](./doc/screenshots/dagvergunning-autocomplete.png?raw=true "Nieuwe Dagvergunning - Auto-complete") | ![Nieuwe Dagvergunning - Barcode](./doc/screenshots/dagvergunning-barcode.png?raw=true "Nieuwe Dagvergunning - Barcode") | ![Nieuwe Dagvergunning - NFC Scannen](./doc/screenshots/dagvergunning-nfc.png?raw=true "Nieuwe Dagvergunning - NFC Scannen") |



##Project setup
Follow these configuration steps to setup the project:



#####1. Set API endpoint

./app/src/main/res/values/properties.xml

```
<string name="makkelijkemarkt_api_base_url" translatable="false">INSERT-API-ENDPONT-HERE</string>
```


#####2. Create secret.xml file containing de API Application key:

./app/src/main/res/values/secret.xml

```
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <!-- Makkelijke Markt App Key -->
    <string name="makkelijkemarkt_api_app_key" translatable="false">INSERT-APP-KEY-HERE</string>

</resources>
```


#####3. Create Google Analytics config:

./app/src/main/res/xml/analytics_tracker_config.xml

```
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <!-- Google Analytics Tracking ID -->
    <string name="ga_trackingId">ADD-GOOGLE-ANALYTICS-TRACKING-ID-HERE</string>

    <!-- Enable uncaught Exception tracking -->
    <bool name="ga_reportUncaughtExceptions">true</bool>

    <!-- Enable automatic Activity tracking -->
    <bool name="ga_autoActivityTracking">true</bool>

    <!-- Activity screenName mappings, for automatic Activity tracking -->
    <screenName name="com.amsterdam.marktbureau.makkelijkemarkt.MainActivity">MainActivity</screenName>
    <screenName name="com.amsterdam.marktbureau.makkelijkemarkt.AboutPrivateActivity">AboutPrivateActivity</screenName>
    <screenName name="com.amsterdam.marktbureau.makkelijkemarkt.AboutPublicActivity">AboutPublicActivity</screenName>
    <screenName name="com.amsterdam.marktbureau.makkelijkemarkt.DagvergunningActivity">DagvergunningActivity</screenName>
    <screenName name="com.amsterdam.marktbureau.makkelijkemarkt.DagvergunningenActivity">DagvergunningenActivity</screenName>
    <screenName name="com.amsterdam.marktbureau.makkelijkemarkt.MarktenActivity">MarktenActivity</screenName>
    <screenName name="com.amsterdam.marktbureau.makkelijkemarkt.NotitieActivity">NotitieActivity</screenName>
    <screenName name="com.amsterdam.marktbureau.makkelijkemarkt.NotitiesActivity">NotitiesActivity</screenName>
    <screenName name="com.amsterdam.marktbureau.makkelijkemarkt.ScanBarcodeActivity">ScanBarcodeActivity</screenName>
    <screenName name="com.amsterdam.marktbureau.makkelijkemarkt.ScanNfcActivity">ScanNfcActivity</screenName>
    <screenName name="com.amsterdam.marktbureau.makkelijkemarkt.VervangerDialogActivity">VervangerDialogActivity</screenName>

</resources>
```


#####4. Disable Google Analytics tracking when not in production by setting ga_dryRun=true:

./app/src/main/res/xml/analytics_global_config.xml

```
<bool name="ga_dryRun">true</bool>
```



###Release notes 
- Vervanger detectie en Koopman selectie
- App updaten optie toegevoegd aan het actions menu
- Check for App update
- Detectie voor 'Locked account'
- Text toetsenbord ter voorbereiding op complex toezichthouder wachtwoord
- Api applicatie key header toegevoegd
- Dagvergunning opslaan pas mogelijk maken nadat de kostprijs berekend is en getoond wordt
- Bug fix voor ontbrekende koopman foto's
- Google Analytics toegevoegd



###Requirements
Android 4.1 Jelly Bean or later (API level 16)



###Documentation

Open the auto-generated [JavaDoc](./doc/javadoc/index.html) documention.


###App diagram
![app diagram](./doc/MMAndroidAppDiagram.png?raw=true "App diagram")


###Waarom is deze code gedeeld
Deze software is in opdracht van het Marktbureau ontwikkelt voor de Gemeente Amsterdam. Veel van deze software wordt vervolgens als open source gepubliceerd zodat andere gemeentes, organisaties en burgers de software als basis en inspiratie kunnen gebruiken om zelf vergelijkbare software te ontwikkelen. De Gemeente Amsterdam vindt het belangrijk dat software die met publiek geld wordt ontwikkeld ook publiek beschikbaar is.

###Wat mag ik met deze code
De Gemeente Amsterdam heeft deze code gepubliceerd onder de Mozilla Public License v2. Een kopie van de volledige licentie tekst is opgenomen in het bestand LICENSE.

### Open Source
Dit project maakt gebruik van diverse Open Source software componenten, waaronder: 

- Android Support Library package - [Revision 23.2.1 (March 2016)](https://developer.android.com/tools/support-library/index.html)
- Butter Knife - [Copyright © 2013 Jake Wharton](https://jakewharton.github.io/butterknife/)
- Simpleprovider 1.1.0 - [Copyright © 2014 Christian Becker, Björn Hurling](https://triple-t.github.io/simpleprovider/)
- Retrofit 2.0.0-beta3 - [Copyright © 2013 Square, Inc.](https://square.github.io/retrofit/)
- Google Gson - [Copyright © 2008 Google Inc.](https://github.com/google/gson)
- Glide 3.7.0 - [Copyright © 2014 Google, Inc.](https://github.com/bumptech/glide/blob/master/LICENSE)
- Google Play Services 8.4.0 - [Android Open Source Project Copyright © 2005-2008](https://developers.google.com/android/guides/overview#the_google_play_services_client_library), [Android Open Source Project Licensed under the Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)
- ZXing Android Embedded - [ZXing Android Embedded: Apache License 2.0](https://github.com/journeyapps/zxing-android-embedded), [ZXing: Copyright © 2014 ZXing authors](https://github.com/zxing/zxing)
- EventBus 3.0.0 - [Copyright © 2012-2016 Markus Junginger, greenrobot](https://github.com/greenrobot/EventBus)
