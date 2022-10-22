/*
   Contactless Doorbell 
   Kartik Sharma
*/
#if defined(ESP32)
#include <WiFi.h>
#elif defined(ESP8266)
#include <ESP8266WiFi.h>
#endif

#define timeSeconds 3
#include <string>
#include <Firebase_ESP_Client.h>
// Provide the token generation process info.
#include <addons/TokenHelper.h>

/* 1. Define the WiFi credentials */
#define WIFI_SSID ""
#define WIFI_PASSWORD ""

/** 2. Define the Service Account credentials (required for token generation)
 *
 * This information can be taken from the service account JSON file.
 *
 * To download service account file, from the Firebase console, goto project settings,
 * select "Service accounts" tab and click at "Generate new private key" button
 */
#define FIREBASE_PROJECT_ID ""
#define FIREBASE_CLIENT_EMAIL ""
const char PRIVATE_KEY[] PROGMEM = "";

/* 3. Define the ID token for client or device to send the message */
#define DEVICE_REGISTRATION_ID_TOKEN ""

/* 4. Define the Firebase Data object */
FirebaseData fbdo;

/* 5. Define the FirebaseAuth data for authentication data */
FirebaseAuth auth;

/* 6. Define the FirebaseConfig data for config data */
FirebaseConfig config;

//ntp time for INDIA
#include <NTPClient.h>
#include <WiFiUdp.h>
// Define NTP Client to get time
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP);

// Variables to save date and time
String formattedDate;
String dayStamp;
String timeStamp;

//PIR SENSOR LED SENSOR CONSTANTS
// Set GPIOs for LED and PIR Motion Sensor
int ledPin = 15;                // choose the pin for the LED
int inputPin = 13;               // choose the input pin (for PIR sensor)
int pirState = LOW;             // we start, assuming no motion detected
int val = 0;                    // variable for reading the pin status

// Timer: Auxiliary variables
unsigned long lastTrigger = 0;
boolean startTimer = false;


unsigned long lastTime = 0;

//int count = 0;

void sendMessage()
{
    while(!timeClient.update()) {
        timeClient.forceUpdate();
    }
  // The formattedDate comes with the following format:
  // 2018-05-28T16:00:13Z
  // We need to extract date and time
    formattedDate = timeClient.getFormattedDate();

    // Extract date
    int splitT = formattedDate.indexOf("T");
    dayStamp = formattedDate.substring(0, splitT);
    Serial.print("DATE: ");
    Serial.println(dayStamp);
    // Extract time
    timeStamp = formattedDate.substring(splitT+1, formattedDate.length()-1);
    Serial.print("HOUR: ");
    Serial.println(timeStamp);
  
    
    
    Serial.print("Send Firebase Cloud Messaging... ");
    
    // Read more details about HTTP v1 API here https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages
    FCM_HTTPv1_JSON_Message msg;

    msg.token = DEVICE_REGISTRATION_ID_TOKEN;



    // For the usage of FirebaseJson, see examples/FirebaseJson/BasicUsage/Create.ino
    FirebaseJson payload;

    payload.add("DATE:", dayStamp);
    payload.add("HOUR:", timeStamp);
    //USED THESE FOR REFERENCE https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages#AndroidConfig
    msg.android.notification.title="Someone is on the Door";
    msg.android.notification.body="Time "+timeStamp+"      "+"Date "+dayStamp;
    msg.android.notification.notification_priority="PRIORITY_MAX";
    msg.android.notification.default_vibrate_timings=true;
    msg.android.notification.click_action="android.intent.action.SECOND";

    //msg.android.notification.color="#0080FF";
    //msg.android.priority="HIGH"; 
    msg.android.data = payload.raw();

    if (Firebase.FCM.send(&fbdo, &msg)) // send message to recipient
        Serial.printf("ok\n%s\n\n", Firebase.FCM.payload(&fbdo).c_str());
    else
        Serial.println(fbdo.errorReason());
}


void setup()
{

    
    pinMode(ledPin, OUTPUT);      // declare LED as output
    pinMode(inputPin, INPUT);     // declare sensor as input

    Serial.begin(115200);
    Serial.println("Hello ESP32");

    //FCM connection via wifi
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print("Connecting to Wi-Fi");
    while (WiFi.status() != WL_CONNECTED)
    {
        Serial.print(".");
        delay(300);
    }
    Serial.println();
    Serial.print("Connected with IP: ");
    Serial.println(WiFi.localIP());
    Serial.println();

    Serial.printf("Firebase Client v%s\n\n", FIREBASE_CLIENT_VERSION);

    /* Assign the sevice account credentials and private key (required) */
    config.service_account.data.client_email = FIREBASE_CLIENT_EMAIL;
    config.service_account.data.project_id = FIREBASE_PROJECT_ID;
    config.service_account.data.private_key = PRIVATE_KEY;

    /* Assign the callback function for the long running token generation task */
    config.token_status_callback = tokenStatusCallback; // see addons/TokenHelper.h

    Firebase.begin(&config, &auth);

    Firebase.reconnectWiFi(true);
    //NTP Server
    // Initialize a NTPClient to get time
    timeClient.begin();
    //INDIA TIME
    timeClient.setTimeOffset(19800);
}

void loop()
{
  // Firebase.ready() should be called repeatedly to handle authentication tasks.

    val = digitalRead(inputPin);  // read input value
    if (val == HIGH) {            // check if the input is HIGH
    digitalWrite(ledPin, HIGH);  // turn LED ON
        if (Firebase.ready()&&pirState == LOW) {
            // we have just turned on
            Serial.println("Motion detected!");
            sendMessage();
            // We only want to print on the output change, not state
            pirState = HIGH;
            delay(3000);
        }   
    } 
    else {
    digitalWrite(ledPin, LOW); // turn LED OFF
        if (pirState == HIGH) {
            // we have just turned of
            Serial.println("Motion ended!");
            // We only want to print on the output change, not state
            pirState = LOW;
        }
    }
 
}
