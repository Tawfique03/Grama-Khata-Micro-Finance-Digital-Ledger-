# Apply Modern UI Designs to Grama Khata Android App

We will completely overhaul the UI of the existing Android application in `c:\Grama-Khata` to match the modern "Deep Indigo" aesthetic described in the `stitch_grama_khata_digital_ledger` mockups.

> [!IMPORTANT]
> **Open Question:** The new HTML mockups are located in `c:\grama_khata_2.0`, while your previously built native Android app is in `c:\Grama-Khata`. I assume you want me to update the native Android app in `c:\Grama-Khata` to visually match these new HTML designs. Is this correct, or did you want to rebuild the app as a Web App (React/Vite) in the new folder?

> [!IMPORTANT]
> **Open Question:** For testing locally, since this is a native Android app, "running it locally" means generating a `.apk` file for you to install on your device or emulator. Do you have an Android Emulator running right now, or a physical Android phone connected via USB? If so, I can execute `adb install` to run it automatically once compiled. Otherwise, I will provide the path to the APK for manual installation.

## Proposed Changes

We will systematically migrate the HTML/Tailwind styling patterns into native Android XML layouts.

### 1. Resources (Colors, Fonts, Themes)
- **Colors**: Update `res/values/colors.xml` with the new palette:
  - Primary: `#1f108e` (Deep Indigo)
  - Secondary: `#4648d4`
  - Surface: `#fcf8ff`
  - Success (Settled): Emerald Green equivalent / `#6063ee` based on mockups
  - Error (Dues): `#ba1a1a` / Coral
- **Typography**: Add `Public Sans` font family to `res/font` and update `styles.xml` / `themes.xml` to use it as the default font.
- **Themes**: Update `res/values/themes.xml` to remove the default ActionBars (already done, but verify), ensure window backgrounds are set to the new `background` color (`#fcf8ff`), and configure ambient shadows.

### 2. Layouts (UI Overhaul)
- **Dashboard (`fragment_dashboard.xml`)**:
  - Replace current summary cards with the "Hero" Balance Summary card using a gradient background (`@drawable/bg_gradient_primary`).
  - Update the "Today's Collection" and "Pending Reminders" grid to match the "Bento Grid" style with rounded corners (`12dp`) and soft shadows (`elevation="2dp"`).
  - Update the Bottom Navigation Bar style.
- **Customer List Items (`item_customer.xml`)**:
  - Remove harsh borders and dividers.
  - Implement pill-shaped chips for "Settled" / "Due" statuses.
  - Apply `elevation="2dp"` to create the "Surface (Low)" shadow.
- **Transaction History (`activity_transaction.xml` & `item_transaction.xml`)**:
  - Update headers to bold high-contrast font weights.
  - Adapt the FAB (Floating Action Button) to use the new primary color and 16dp rounded corners (Level 2).
  - Implement soft input fields with a 2px indigo focus ring instead of heavy borders.
- **Add Dialogs (`AddTransactionDialog.kt`, etc.)**:
  - Update the bottom sheet background shapes to have larger top corner radii.

## Verification Plan

### Automated Build
- Run `./gradlew assembleDebug` to verify that all XML changes are valid and the app compiles successfully without resource errors.

### Manual Verification
- After successful compilation, I will either automatically install and launch the app via `adb` (if a device is available) or provide the `app-debug.apk` file path for you to install.
- You can then visually verify the new screens (Dashboard, Customer List, Transaction Details) against the HTML mockups.
