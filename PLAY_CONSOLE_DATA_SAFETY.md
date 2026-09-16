# Google Play Console Data Safety Questionnaire Answers

Use these exact responses when completing the **Data Safety** section in the Google Play Console for **B47 Cricket Score** (`com.b47tech.cricketscore`).

---

### 1. Data Collection and Security
* **Does your app collect or share any of the required user data types?**  
  👉 **Yes** (Due to Google Mobile Ads / AdMob SDK).
* **Is all of the user data collected by your app encrypted in transit?**  
  👉 **Yes** (All Google AdMob network traffic uses HTTPS).
* **Do you provide a way for users to request that their data be deleted?**  
  👉 **Yes** (Users can reset/clear local app data directly in device App Settings or in-app).

---

### 2. Data Types Collected by Google Mobile Ads SDK

#### Device or Other IDs
* **Data Type:** Device or other IDs (e.g., Android Advertising ID)
* **Collected?** Yes
* **Shared?** Yes (Shared with third-party: Google AdMob)
* **Processed ephemerally?** No
* **Is this data required or optional?** Required for advertising functionality
* **Purposes:**
  * Advertising or marketing
  * Analytics / Fraud prevention

---

### 3. Permissions Checklist
* `INTERNET`: For serving ads.
* `ACCESS_NETWORK_STATE`: For checking network connection status before making ad requests.
* **Sensitive Permissions (Contacts, Location, SMS, Camera, Storage):** NONE.
