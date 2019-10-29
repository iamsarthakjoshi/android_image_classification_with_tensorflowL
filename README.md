# android_image_classification_with_tensorflowL

**Android Studio**
- Open Android Studio application on your system. Click Open an existing Android Studio Project.

- Choose ObjectIdentifier project folder and click “Open” to launch Android Studio with this app.

- If you run into ***“Cause: archive is not a ZIP archive”*** error while building then please comment out apply line
***from:'download_model.gradle'*** line from *build.gradle* file and use this link to download model and label file need for TensorFlow Library. Lastly, just copy the whole assets folder inside this project's assets folder such as, */src/main/assets/assets/<model_files>*

 
- Now, click on green play icon which is called “Run app”.
 

**Please make sure this app is built completely in Android Studio. Specially, “Execute download” process should be completed since it downloads all the Model files through cloud.**
 

-	Now select one of your emulator devices, for instance “Pixel 3 API 28”, or if you have your android device connected, that will show in “Connected Devices” and simply select your own device and click ok “OK” button to launch this app.

 

Great! You have successfully installed the app on your emulator device or on your own android device.
