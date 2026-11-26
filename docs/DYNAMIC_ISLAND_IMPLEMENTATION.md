# NixhOS Dynamic Island — SystemUI implementation

## Scope
This feature is for the NixhOS custom Android ROM, not a launcher, overlay app, or standalone APK. The first milestone is a compact, system-owned status pill integrated into SystemUI. Do not change the launcher, home screen, lock screen, or other UI as part of this task.

## Device target
- Device: Motorola Moto G45 5G
- User-reported model: XT2363-8
- Device codename: fogos
- SoC: Qualcomm SM6375-AC (Snapdragon 6s Gen 3)
- Display: 720 x 1600, 120 Hz
- Candidate ROM base: official LineageOS for fogos; select and pin an exact source branch before coding.

### Compatibility research (checked 2026-11-26)
- The official LineageOS install guide lists XT2363-8 and XT2363-9 as supported exact model numbers.
- The guide requires the device to be on Android 14/15 stock firmware before installing the current LineageOS build.
- The guide requires a computer with ADB and Fastboot, and warns that bootloader unlocking erases user data.
- The guide also requires flashing device-specific dtbo and vendor_boot images before installing Lineage Recovery.
- Public device-source repositories exist for fogos. The Motorola-SM6375-Devs device tree's `sixteen` branch declares dependencies on:
  - `android_device_motorola_sm6375-common`
  - `android_hardware_samsung_slsi_nfc`
- Its BoardConfig inherits the common SM6375 board configuration and a proprietary vendor configuration. This means the device tree alone is not a complete build input; matching common, kernel, vendor blobs, firmware and manifest revisions must be pinned together.
- The existing NixhOS GitHub Actions workflow is an APK build workflow (`gradle assembleDebug`), not an Android ROM/SystemUI build. It must not be treated as evidence that a ROM can be built or installed.

Reference links:
- Official install guide: https://wiki.lineageos.org/devices/fogos/install/
- Official device page: https://wiki.lineageos.org/devices/fogos/
- Device tree: https://github.com/Motorola-SM6375-Devs/android_device_motorola_fogos
- SM6375 common tree: https://github.com/Motorola-SM6375-Devs/android_device_motorola_sm6375-common
- Kernel source: https://github.com/Motorola-SM6375-Devs/android_kernel_motorola_sm6375

## First milestone: useful Dynamic Island
The pill is rendered by SystemUI so it is present above ordinary apps and is not dependent on a launcher.

### Events to support
1. Media: use active MediaSession information and transport controls (play/pause, previous, next) where the session exposes them.
2. Calls: reflect supported ongoing-call/CallStyle notification state; tapping opens the owning call UI. Never intercept or replace the phone call service.
3. Timer: show an active timer using supported system timer/notification signals; tapping opens the clock app.
4. Charging: show charging state and battery percentage from system battery signals.
5. Downloads: show progress only when a system/app notification exposes progress; tapping opens the originating app.

### Interaction and behaviour
- Compact pill in the status-bar area; expand into a small SystemUI-owned detail surface on tap.
- Prioritise ongoing calls, then user-started media/timers, then charging/download activity.
- Hide when there is no active event, except for a brief transition animation.
- Respect notification privacy on the lock screen and the user's sensitive-notification settings.
- Respect accessibility services, font scaling, display cut-outs, landscape, multi-window, and gesture/navigation modes.
- Avoid collecting, uploading, or storing personal notification content.

## Implementation approach
- Add a dedicated SystemUI component/controller in the matching LineageOS framework/SystemUI source tree.
- Subscribe to platform-owned sources (MediaSessionManager, notification/call state, battery broadcasts and notification progress) using APIs and permissions available to SystemUI on the chosen Android branch.
- Keep event parsing/state management separate from the pill view and animation layer.
- Use platform resources/overlays for dimensions, colours, and animation timing. No third-party SDK or network service.
- Add unit tests for event priority/state transitions and build-time checks for the target branch.
- Do not implement this as an AccessibilityService, notification-listener app, floating-window app, or launcher widget.

## Required verification before flashing
1. Confirm the exact XT2363-8 device tree and its required common/vendor repositories and pin compatible revisions.
2. Confirm kernel, vendor blobs, firmware baseline, partition layout, boot/recovery method, and bootloader unlock path.
3. Build the unmodified target ROM first, then integrate the feature and build again.
4. Run compile checks and inspect boot/SystemUI logs; test media, calls, timer, charging, downloads, privacy, rotation, and accessibility.
5. Make a complete user-data backup and verify a recovery/restore path.
6. Flash only a build explicitly verified for this exact device variant. Never flash an unverified generic or similarly named device image.

## Build-environment constraint
A full Android ROM build is much larger than an ordinary Android app build. A phone-only workflow needs a suitable remote Linux builder with enough CPU, RAM, storage, build time, and persistent workspace. The current repository workflow only builds an APK and is not a ROM builder. A normal GitHub-hosted Actions runner should not be assumed sufficient for a complete LineageOS build. First establish a viable builder and verify device sources before attempting the ROM build.

## Current status
- Planning and compatibility research updated.
- Exact user-reported model XT2363-8 is included in the official LineageOS install guide's supported model list.
- Candidate public device tree and declared dependencies identified; exact source revisions and vendor/kernel pairing are not yet pinned.
- No SystemUI source patch or ROM build has been completed yet.
- No installable NixhOS ROM image exists yet.
- No changes should be flashed to the phone at this stage.
