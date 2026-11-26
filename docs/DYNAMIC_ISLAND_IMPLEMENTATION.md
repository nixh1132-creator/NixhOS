# NixhOS Dynamic Island — SystemUI implementation

## Scope
This feature is for the NixhOS custom Android ROM, not a launcher, overlay app, or standalone APK. The first milestone is a compact, system-owned status pill integrated into SystemUI. Do not change the launcher, home screen, lock screen, or other UI as part of this task.

## Device target
- Device: Motorola Moto G45 5G
- User-reported model: XT2363-8
- Device codename: fogos
- SoC: Qualcomm SM6375-AC (Snapdragon 6s Gen 3)
- Candidate Android base: official LineageOS 24.0, source branch `lineage-24.0`.

## Compatibility and source research (checked 2026-11-26)
- The official LineageOS install guide lists XT2363-8 and XT2363-9 as supported exact model numbers.
- The guide requires Android 14/15 stock firmware before installing the current LineageOS build.
- The guide requires a computer with ADB and Fastboot. Bootloader unlocking erases user data.
- The guide requires device-specific `dtbo` and `vendor_boot` images before installing Lineage Recovery.
- The official device repository exists at `LineageOS/android_device_motorola_fogos`; its default branch is `lineage-24.0`.
- The official `lineage-24.0` device tree product is `lineage_fogos` / `PRODUCT_DEVICE := fogos`. It inherits `device/motorola/sm6375-common/BoardConfigCommon.mk`.
- Its `lineage.dependencies` declares `android_device_motorola_sm6375-common` and `android_hardware_samsung_slsi_nfc`.
- The board config references a proprietary vendor config at `vendor/motorola/fogos/BoardConfigVendor.mk`; therefore matching proprietary blobs and firmware must be obtained and verified before a build. Kernel/common/vendor revisions are not yet pinned as a tested compatible set.
- The existing NixhOS GitHub Actions workflow runs `gradle assembleDebug` and produces an APK. It is not an Android ROM/SystemUI build and is not evidence of a working ROM build.

Reference links:
- Official install guide: https://wiki.lineageos.org/devices/fogos/install/
- Official device page: https://wiki.lineageos.org/devices/fogos/
- Official fogos tree (lineage-24.0): https://github.com/LineageOS/android_device_motorola_fogos/tree/lineage-24.0
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
- Add a dedicated SystemUI component/controller in the matching LineageOS 24.0 `frameworks/base/packages/SystemUI` source tree.
- Subscribe to platform-owned sources (MediaSessionManager, notification/call state, battery broadcasts and notification progress) using APIs and permissions available to SystemUI on this Android branch.
- Keep event parsing/state management separate from the pill view and animation layer.
- Use platform resources/overlays for dimensions, colours, and animation timing. No third-party SDK or network service.
- Add unit tests for event priority/state transitions and build-time checks for the target branch.
- Do not implement this as an AccessibilityService, notification-listener app, floating-window app, or launcher widget.

## Required verification before flashing
1. Confirm exact XT2363-8 identity and pin matching fogos device tree, common tree, kernel, vendor blobs and firmware revisions.
2. Build the unmodified official LineageOS 24.0 fogos target first, then integrate the SystemUI feature and build again.
3. Run compile checks and inspect boot/SystemUI logs; test media, calls, timer, charging, downloads, privacy, rotation, and accessibility.
4. Make a complete user-data backup and verify a recovery/restore path.
5. Flash only a build explicitly verified for this exact device variant. Never flash an unverified generic or similarly named device image.

## Build-environment constraint
A full Android ROM build is much larger than an ordinary Android app build. A phone-only workflow needs a suitable remote Linux builder with enough CPU, RAM, storage, build time, and persistent workspace. The current NixhOS GitHub Actions workflow only builds an APK; it is not a ROM builder. A normal GitHub-hosted Actions runner should not be assumed sufficient for a complete LineageOS build. First establish a viable builder and finish source/firmware verification before attempting the ROM build.

## Current status
- Planning and compatibility research updated.
- Exact user-reported model XT2363-8 is included in the official LineageOS supported model list.
- Official LineageOS 24.0 fogos device tree and its declared common/NFC dependencies identified.
- Matching proprietary vendor blobs, kernel/common revisions and stock firmware baseline still need verification.
- No SystemUI source patch or ROM build has been completed yet.
- No installable NixhOS ROM image exists yet.
- No changes should be flashed to the phone at this stage.
