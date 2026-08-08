# Destination Video for PICO

This project is a PICO/Android port of Apple's Destination Video sample. It keeps
the reference catalog, artwork, navigation, detail pages, Up Next queue, video
sources, and Studio environment.

## Features

- Watch Now hero, featured videos, collections, animations, and Up Next
- Filterable video library and category pages
- Video metadata, cast and crew, and persistent Up Next state
- Local HEVC and remote HLS playback through Media3 ExoPlayer
- Share-space playback through `SurfaceView`
- Full Space video through PICO `VideoComponent` and `SurfaceRenderTexture`
- Imported Light and Dark Studio USD environments

Apple-only SwiftData, SharePlay, and RealityKit environments are represented by
Android preferences, ExoPlayer, and PICO Spatial SDK entities respectively.

## Build

Use JDK 17 or the bundled Android Studio JBR:

```shell
./gradlew assembleDebug
```

Build, install, and launch on the connected device:

```shell
scripts/install_debug.sh
```

Launch the installed app without rebuilding or reinstalling:

```shell
scripts/install_debug.sh open
```
