android-facebook
================

Facebook SDK for Android

Facebook SDK (v3.16.0) for Android

For information on how to get started, visit https://developers.facebook.com/docs/android/getting-started

1. Download Facebook SDK from this link: https://developers.facebook.com/docs/android
2. Extract the SDK and import the facebook folder (FacebookSDK project) to your project in Eclipse.

Creating a Facebook App

1. Open Facebook dashboard: https://developers.facebook.com/apps
2. Go to Apps > Create a New App and add App Display Name and Namespace. Write down the App ID.
3. Associate Android key hashes with the app. Go to Settings > Add Platform in the App site. Select Android Platform and write the debug key hash and then release key hash.
   (To find the key hash, go to this link and follow instructions: http://stackoverflow.com/questions/5306009/facebook-android-generate-key-hash)

Android project setup

1. Create a new Android project.
2. In the Facebook dashboard, add the package name and the class name of your app.
3. Add the Facebook SDK Project to your Android project (Project properties -> Android -> Library -> Add).
4. In strings.xml, add a string called app_id with your Facebook App ID.
5. In AndroidManifest.xml, add Internet permission.
6. In AndroidManifest.xml, add a metadata tag with the app_id string: <meta-data android:value="@string/app_id" android:name="com.facebook.sdk.ApplicationId"/>
7. Add activity com.facebook.LoginActivity in AndroidManifest.xml: <activity android:name="com.facebook.LoginActivity"></activity>

Facebook Login

Code in activity_main layout and MainFragment.java.

1. Add a Facebook login button in your layout (activity_main.xml).
2. Use the layout on a Fragment or an activity so the user can see the Facebook login button.


Managing Session Changes

Code in MainFragment.java class.

1. Write the method onSessionStateChange(Session session, SessionState state, Exception exception).
2. Add the logic to listen to session changes. Use Session.StatusCallback.
3. Use UiLifecycleHelper to manage sessions. Pass the StatusCallback to the UiLifecycleHelper constructor.
4. Add uiLifecycleHelper methods: onCreate(), onResume(), onPause(), onDestroy(), onActivityResult() and onSaveInstanceState().
5. Sometimes the session state change notification is not triggered. Handle this in the fragment onResume() method. You should trigger the session state changes 
   if the session is not null.
6. You should add UiLifecycleHelper and Session.StatusCallback to all the activities and fragments that you want to track session changes.

Ask Permissions

More information about permissions can be found here: https://developers.facebook.com/docs/facebook-login/permissions/v2.0

1. Set read permissions to the login button in MainFragment.java.


Sharing 

More information about sharing can be found here: https://developers.facebook.com/docs/android/share

1. You need to create the UiLifecycleHelper as before.
2. Configure callback handler that will be invoked when the share dialog is closed. Do this in the onActivityResult() method.
3. Add all the uiLifecycleHelper methods as before (onCreate, onResume, etc).
4. To share a link, set the link in the ShareDialogBuilder. See share() method in ShareDialogActivity.java class.
5. You can add description, caption, picture and more to the sharing message. Visit https://developers.facebook.com/docs/reference/android/current/class/FacebookDialog.ShareDialogBuilder/ for more information.
6. You can handle responses from share dialog in the onActivityResult method: FacebookDialog.getNativeDialogDidComplete, FacebookDialog.getNativeDialogCompletionGesture, FacebookDialog.getNativeDialogPostId.
7. If the user doesn't have Facebook app install, you should use the FeedDialog instead. See share() method in ShareDialogActivity.java class.
