import os

base = "D:/2nd year/Mobile/APP/Women_Safety_App/app/src/main/res"

def w(path, content):
    full = base + "/" + path
    os.makedirs(os.path.dirname(full), exist_ok=True)
    with open(full, "w", encoding="utf-8") as f:
        f.write(content)
    print("WROTE: " + path)

# colors.xml
w("values/colors.xml", """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="purple_200">#FFBB86FC</color>
    <color name="purple_500">#FF6200EE</color>
    <color name="purple_700">#FF3700B3</color>
    <color name="teal_200">#FF03DAC5</color>
    <color name="teal_700">#FF018786</color>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
    <color name="yuwathi_pink">#E91E63</color>
    <color name="yuwathi_pink_dark">#C2185B</color>
    <color name="yuwathi_pink_light">#F48FB1</color>
    <color name="yuwathi_pink_ultra_light">#FCE4EC</color>
    <color name="yuwathi_bg">#FFF5F8</color>
    <color name="yuwathi_gray">#F5F5F5</color>
    <color name="yuwathi_text_dark">#333333</color>
    <color name="yuwathi_text_medium">#666666</color>
    <color name="yuwathi_text_hint">#AAAAAA</color>
    <color name="yuwathi_red">#D32F2F</color>
    <color name="yuwathi_red_dark">#B71C1C</color>
    <color name="yuwathi_red_light">#FFCDD2</color>
    <color name="yuwathi_divider">#FFCDD2</color>
</resources>""")

# strings.xml
w("values/strings.xml", """<resources>
    <string name="app_name">Yuwathi</string>
    <string name="app_name_sinhala">\u0DBA\u0DD4\u0DC0\u0DAD\u0DD2</string>
    <string name="tagline">Guarding every girl\'s journey</string>
    <string name="one_tap">One tap.\\nInstant help</string>
    <string name="enter_detail">Enter your detail here!</string>
    <string name="username_hint">Username, Email or Phone Number</string>
    <string name="password_hint">Password</string>
    <string name="sign_in">Sign In</string>
    <string name="remember_me">Remember Me</string>
    <string name="forgot_password">Forgot Password?</string>
    <string name="no_account">Don\'t have an account?</string>
    <string name="home">Home</string>
    <string name="contacts">Contacts</string>
    <string name="safety_tips">Tips</string>
    <string name="sos">SOS</string>
    <string name="profile">Profile</string>
    <string name="register">Register</string>
    <string name="complaint">Complaint</string>
    <string name="location">Location</string>
    <string name="sos_hold_message">Hold 3 sec to activate SOS</string>
    <string name="sos_activated">SOS ACTIVATED! Sending alerts</string>
    <string name="add_contact">Add Emergency Contact</string>
</resources>""")

# themes.xml
w("values/themes.xml", """<resources>
    <style name="Theme.Women_Safety_App" parent="Theme.MaterialComponents.DayNight.NoActionBar">
        <item name="colorPrimary">@color/yuwathi_pink</item>
        <item name="colorPrimaryVariant">@color/yuwathi_pink_dark</item>
        <item name="colorOnPrimary">@color/white</item>
        <item name="colorSecondary">@color/yuwathi_pink_light</item>
        <item name="colorSecondaryVariant">@color/yuwathi_pink_dark</item>
        <item name="colorOnSecondary">@color/white</item>
        <item name="android:statusBarColor">@color/yuwathi_pink_dark</item>
    </style>
    <style name="Theme.Women_Safety_App.SOS" parent="Theme.Women_Safety_App">
        <item name="colorPrimary">@color/yuwathi_red</item>
        <item name="android:statusBarColor">@color/yuwathi_red_dark</item>
    </style>
</resources>""")

# styles.xml
w("values/styles.xml", """<resources>
    <style name="Widget.Yuwathi.TextInput" parent="Widget.MaterialComponents.TextInputLayout.OutlinedBox">
        <item name="boxStrokeColor">@color/yuwathi_pink</item>
        <item name="hintTextColor">@color/yuwathi_pink</item>
    </style>
</resources>""")

# nav_icon_color.xml
w("color/nav_icon_color.xml", """<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:color="#E91E63" android:state_checked="true" />
    <item android:color="#AAAAAA" />
</selector>""")

# bottom_menu.xml
w("menu/bottom_menu.xml", """<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:id="@+id/nav_home"     android:icon="@drawable/ic_home_nav"     android:title="@string/home" />
    <item android:id="@+id/nav_contacts" android:icon="@drawable/ic_contacts_nav" android:title="@string/contacts" />
    <item android:id="@+id/nav_sos"      android:icon="@drawable/ic_sos_nav"      android:title="@string/sos" />
    <item android:id="@+id/nav_tips"     android:icon="@drawable/ic_tips_nav"     android:title="@string/safety_tips" />
    <item android:id="@+id/nav_profile"  android:icon="@drawable/ic_profile_nav"  android:title="@string/profile" />
</menu>""")

print("ALL DONE")

