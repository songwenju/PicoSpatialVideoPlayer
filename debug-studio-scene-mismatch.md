# Debug Session: studio-scene-mismatch
- **Status**: [OPEN]
- **Issue**: PICO 版本还原度不足，Full Space 中 Studio 场景没有成功显示。
- **Debug Server**: http://10.200.4.198:7777/event
- **Log File**: .dbg/trae-debug-log-studio-scene-mismatch.ndjson

## Reproduction Steps
1. Launch Destination Video in the PICO emulator.
2. Open the hero details and select Play Movie.
3. Select Studio to enter Full Space.
4. Compare the visible environment and UI against the AVP simulator.

## Hypotheses & Verification
| ID | Hypothesis | Likelihood | Effort | Evidence |
|----|------------|------------|--------|----------|
| A | RealityKit-specific USD materials or referenced assets fail to resolve | High | Medium | Pending |
| B | LightState or DarkState descendants are disabled or transparent | Medium | Low | Pending |
| C | AVP scene coordinates or scale place geometry outside the PICO view | High | Low | Pending |
| D | Async scene loading is canceled or only the video entity is added | Medium | Low | Pending |
| E | UI mismatch is mainly caused by SpaceOS material/layout behavior | High | Medium | Pending |

## Log Evidence
Pre-fix instrumentation added:
- A/D: USD load start, success/failure, and elapsed time.
- A/B/C: root children, Light/Dark discovery, enabled state, and visual bounds.
- C/D: video entity, render surface, and control attachment readiness.
- E: catalog root and sidebar measured pixel sizes.

Pre-fix:
- `AAA_MainScene.usda` emitted only load-start after 30 seconds.
- APK inspection showed USD/GLB model assets were deflated.

Control:
- After adding model extensions to `androidResources.noCompress`, `TinyCube.glb`
  loaded in 998 ms with non-empty bounds.

Post-fix:
- `StudioPicoLight.glb` loaded in 6982 ms.
- Root contains 10 children and has non-empty visual bounds.
- The video surface is valid and the control attachment is present.

Visibility follow-up:
- User reported the loaded Studio geometry was still completely invisible.
- GLB inspection confirmed all materials are double-sided.
- `visibility-v1` replaced 9 model materials with PICO-native unlit materials,
  added functional Light/Dark roots, and added a cyan marker at `z=-3m`.
- `visibility-v2` scales Studio to 45% and moves its center from the camera
  boundary to `(0, 2, -8)`.
- Full Space playback was moved to MediaPack `0.3.8-alpha.2.1-bugfix` with
  Spatial SDK `6.1.5`.
- Runtime verification shows MediaPack created a `TYPE_2D` entity, attached the
  shared `PicoPlayer`, bound a valid `2560x1440` surface in 7 ms, and rendered
  the first frame. The Studio nodes were attached to the same Stage.

## Root Cause
PICO's native model loader requires model assets to remain uncompressed in the
APK. Reality Composer Pro's full scene also contains RealityKit/MaterialX data
that is not portable to PICO, so the Apple Studio geometry and baked textures
are packaged as a PICO-compatible GLB.

## Fix
- Added `glb`, `gltf`, `usdz`, `usda`, and `usdc` to `androidResources.noCompress`.
- Added `StudioPicoLight.glb`, preserving Apple Studio geometry and optimized
  baked light textures.
- Updated the Full Space stage to load the PICO-compatible Studio asset.
- Replaced the hand-built Full Space `VideoComponent` and render texture with
  MediaPack `PlaybackController.updateEntity()` and the shared `PicoPlayer`.
- Corrected debug sampling so ECS state is read on the main thread.

## Verification Conclusion
Pending.
