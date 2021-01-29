# Android Image Classification App using TensorFlow Lite (FYP)

An android based mobile application that classifies objects or images via device camera using TensorFlow library and pre-trained models.

## Running application on Android Studio

- First off, clone the project onto your system.

- Open Android Studio application on your system. Click `Open` an existing Android Studio Project.

> You can also follow the [instruction manual](https://github.com/iamsarthakjoshi/android_image_classification_with_tensorflowL/blob/master/Instruction_Manual_ITC539SI_JOSHI_11678187_A4_201960.docx) which has visual representation of all the instructions (including below instructions).

- Choose the cloned project folder (android_image_classification_with_tensorflowL) and click “Open” to launch Android Studio with this app.

- If you run into `Cause: archive is not a ZIP archive` error while building then please comment out line `apply from:'download_model.gradle'` line from **build.gradle** file and use this [link](https://drive.google.com/drive/folders/19y9yTCPwYyh_21tg4RN_YjuU1YP_9yUh?usp=sharing) to download model and label file need for TensorFlow Library. Lastly, just copy the whole `assets` folder inside this project's assets folder such as, `/src/main/assets/assets/<model_and_label_files>`

- Now, click on green play icon which is called `Run app`.

**Please make sure this app is built completely in Android Studio. Specially, `“Execute download”` process should be completed since it downloads all the Model files through cloud.**
 
-	Now select one of your emulator devices, for instance `“Pixel 3 API 28”`, or if you have your android device connected, that will show in `“Connected Devices”` and simply select your own device and click ok `“OK”` button to launch this app.

 
Thank you. :)
