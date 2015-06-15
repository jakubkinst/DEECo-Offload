# DEECo Offloading for Android

## Project structure

The build system is Gradle (Created in Android Studio/IntelliJ Idea)

### Modules
- **jdeeco-java-library** - JDEECo JARs with it's dependencies ([JDEECo repository](https://github.com/d3scomp/JDEECo))
- **restlet-android** - [Restlet](restlet.com) library with slight modification to run properly on Android platform
- **jdeeco-udp-broadcast-library** - JDEECo extension providing knowledge cloning over UDP broadcast
- **jdeeco-offloading-java-library** - Offloading framework developed on top of JDEECo and Restlet
- **jdeeco-offloading-android-library** - Extension of *jdeeco-offloading-java-library* with Android-specific implementation
- **jdeeco-offloading-android-demo** - Demo Android application using offloading capabilities (Simple app for testing purposes)
- **jdeeco-offloading-android-demo-java-backend** - Java command-line application with running backend for *jdeeco-offloading-android-demo*
- **tess-two** - Android library module providing OCR via Android NDK (just a build of [tess-two](https://github.com/rmtheis/tess-two))
- **jdeeco-offloading-android-demo-offloadableocr** - Demo Android application with simple OCR functionality taking advantage of offloading (Work in progress)
- **jdeeco-offloading-android-demo-offloadableocr-java-backend** - Java command-line application with running backend for *jdeeco-offloading-android-demo-offloadableocr*

## Presentation (in Czech)
[https://goo.gl/X9yLQ7](https://goo.gl/X9yLQ7)
