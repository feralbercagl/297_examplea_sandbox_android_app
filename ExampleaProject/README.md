README
=========

Examplea Android Application.

App Version: V1.0

Building
----

The generated project is an Android Studio project.

Requirements
----
* Android Studio >= version 0.8.1
* Android SDK with build tools >= version 19
* JDK 1.6
* Update sdk.dir in local.properties to point to your Android SDK folder.

You should be able to just open the project from Android Studio. You may want to "Invalidate the Cache" from the File menu
if you see issues with dependencies.

Import into Android Studio
----
Go to File->Import Project from the file menu and import in /PATH/TO/ExampleaProject/Examplea/.

Some manual changes are needed before you can successful perform a build.

0) Add the following to /PATH/TO/ExampleaProject/Examplea/build.gradle:
android {
	
    packagingOptions {
        exclude 'META-INF/DEPENDENCIES.txt'
        exclude 'META-INF/LICENSE.txt'
        exclude 'META-INF/NOTICE.txt'
        exclude 'META-INF/NOTICE'
        exclude 'META-INF/LICENSE'
        exclude 'META-INF/DEPENDENCIES'
        exclude 'META-INF/notice.txt'
        exclude 'META-INF/license.txt'
        exclude 'META-INF/dependencies.txt'
        exclude 'META-INF/LGPL2.1'
    }

}

compile 'com.android.support:appcompat-v7:20.+'

1) Add the following to /PATH/TO/ExampleaProject/anyPresenceLibrary/build.gradle

compile 'com.android.support:appcompat-v7:20.+'

Import into Eclipse
----

0) The project has gradle build files to work with Android Studio, but their existence may interfere with the import
to Eclipse. Move /PATH/TO/ExampleaProject/Examplea/build.gradle by renaming it or move it to another directory.

1) Go to File->Import and select Android->Existing Android Code into Workspace

2) Navigate to /PATH/TO/ExampleaProject/ and select:
    * examplea
    * PlayServices
    * android-support-v7-appcompat
    * AnyPresenceLibrary

You may need to clean the project.

Debug Build
----
This can be buit from the IDE or from the command line. From the IDE, just go to Build->Rebuild Project;
From the command line,
```sh
./gradlew clean assembleDebug
```

Release Build
----
Place the release keystore in the folder APP_NAME_PROJECT/APP_NAME and rename it to release.keystore.

From the command line,
```sh
KEY_STORE_PASSWORD=store_pass KEY_PASSWORD=key_pass KEY_ALIAS=myalias ./gradlew clean assembleRelease -i
```

The APK will be in build/apk/.


