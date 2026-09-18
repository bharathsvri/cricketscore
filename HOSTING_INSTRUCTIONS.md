# B47 Cricket Score — Web Hosting & Privacy Policy Deployment Guide

**Target Public Repository:** [https://github.com/bharathsvri/cricketscore](https://github.com/bharathsvri/cricketscore)  
**Target Live Website:** [https://bharathsvri.github.io/cricketscore/](https://bharathsvri.github.io/cricketscore/)  
**Target Privacy Policy URL:** [https://bharathsvri.github.io/cricketscore/privacy-policy/](https://bharathsvri.github.io/cricketscore/privacy-policy/)  
**Target AdMob app-ads.txt:** [https://bharathsvri.github.io/cricketscore/app-ads.txt](https://bharathsvri.github.io/cricketscore/app-ads.txt)  

---

## What is in this `/docs` folder?
- `docs/index.html`: The main, mobile-responsive HTML Privacy Policy page.
- `docs/privacy-policy/index.html`: Subpath copy so `/privacy-policy/` also renders the policy directly.
- `docs/app-ads.txt`: Official AdMob authorized sellers file with publisher `pub-2828717457589648`.
- `docs/HOSTING_INSTRUCTIONS.md`: This step-by-step guide.

---

## 🚀 3-Step Setup on GitHub Pages (Takes Under 60 Seconds)

Since you created the public repository `https://github.com/bharathsvri/cricketscore`:

### Step 1: Push the `/docs` folder to your GitHub repo
In your terminal, commit and push your changes to your GitHub repository:
```bash
git add docs/ app/src/main/res/values/strings.xml app/src/main/java/com/b47tech/cricketscore/ui/screens/settings/SettingsScreen.kt PLAY_STORE_CONSOLE_MASTER_SPEC.md PRIVACY_POLICY.md
git commit -m "Configure GitHub Pages privacy policy and app-ads.txt for bharathsvri/cricketscore"
git push origin main
```
*(If your default branch is `master`, push to `origin master`)*

### Step 2: Enable GitHub Pages in Repository Settings
1. Open your browser and go to:  
   👉 **[https://github.com/bharathsvri/cricketscore/settings/pages](https://github.com/bharathsvri/cricketscore/settings/pages)**
2. Under **Build and deployment > Source**:
   - Select **Deploy from a branch**.
3. Under **Branch**:
   - Select `main` (or `master`).
   - In the folder dropdown next to it, select **/docs**.
   - Click **Save**.

### Step 3: Verify Your Live URLs
Wait approximately 30–60 seconds for GitHub Actions to deploy. Then verify all three live links:
1. **Homepage Privacy Policy:**  
   [https://bharathsvri.github.io/cricketscore/](https://bharathsvri.github.io/cricketscore/)
2. **Subpath Privacy Policy:**  
   [https://bharathsvri.github.io/cricketscore/privacy-policy/](https://bharathsvri.github.io/cricketscore/privacy-policy/)
3. **AdMob `app-ads.txt` File:**  
   [https://bharathsvri.github.io/cricketscore/app-ads.txt](https://bharathsvri.github.io/cricketscore/app-ads.txt)

---

## 📋 Where to Use These URLs:

1. **In Google Play Console:**
   - **App Content > Privacy Policy:** Enter `https://bharathsvri.github.io/cricketscore/` (or `https://bharathsvri.github.io/cricketscore/privacy-policy/`).
   - **Store Settings > Developer Details > Website:** Enter `https://bharathsvri.github.io/cricketscore/`.
2. **In Google AdMob Console:**
   - **App Settings > App Store details > Developer Website:** Enter `https://bharathsvri.github.io/cricketscore/` so AdMob automatically crawls and verifies `app-ads.txt`.
3. **In the Android App:**
   - Automatically wired in [`strings.xml`](file:///D:/Learning/Android%20application%20development/cricketscore/app/src/main/res/values/strings.xml) (`privacy_policy_url`) and opened when users tap **Privacy Policy** in [`SettingsScreen.kt`](file:///D:/Learning/Android%20application%20development/cricketscore/app/src/main/java/com/b47tech/cricketscore/ui/screens/settings/SettingsScreen.kt).
