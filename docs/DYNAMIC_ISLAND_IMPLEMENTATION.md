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
- The official LineageOS device page lists XT2363-8 and XT2363-9 for moto g45 5G.
- The guide requires Android 14/15 stock firmware before installing the current LineageOS build.
- The guide requires a computer with ADB and Fastboot. Bootloader unlocking erases user data.
- The guide requires device-specific `dtbo` and `vendor_boot` images before installing Lineage Recovery.
- The official device repository exists at `LineageOS/android_device_motorola_fogos`; its default branch is `lineage-24.0`.
- The official `lineage-24.0` device tree product is `lineage_fogos` / `PRODUCT_DEVICE := fogos`. It inherits `device/motorola/sm6375-common/BoardConfigCommon.mk`.
- Its `lineage.dependencies` declares `android_device_motorola_sm6375-common` and `android_hardware_samsung_slsi_nfc`.
- The device tree `device.mk` includes per-model overlays `FrameworksResDeviceXT2363-8`, `FrameworksResDeviceXT2363-9`, `WifiResDeviceXT2363-8`, and `WifiResDeviceXT2363-9`.
- **Identity check resolved at the device-tree property level:** although `lineage_fogos.mk` sets a generic `PRODUCT_MODEL := moto g34 5G`, `product.prop` imports a SKU-specific property file based on `ro.boot.hardware.sku`. Both `props/hardware.sku.XT2363-8.prop` and `props/hardware.sku.XT2363-9.prop` explicitly set the displayed/product model properties to `moto g45 5G`. The device tree also includes dedicated framework and Wi-Fi overlays for both XT2363 variants. This explains the apparent model-string mismatch; it is not by itself evidence of a G34 build target. Still verify the actual phone SKU and stock fingerprint before any build/flash, and do not rely on the codename alone.
- The board config references proprietary vendor config at `vendor/motorola/fogos/BoardConfigVendor.mk`; matching proprietary blobs, kernel, common tree and firmware must be obtained and verified before a build. Kernel/common/vendor revisions are not yet pinned as a tested compatible set.
- The existing NixhOS GitHub Actions workflow runs `gradle assembleDebug` and produces an APK. It is not an Android ROM/SystemUI build and is not evidence of a working ROM build.

Reference links:
- Official install guide: https://wiki.lineageos.org/devices/fogos/install/
- Official device page: https://wiki.lineageos.org/devices/fogos/
- Official fogos tree (lineage-24.0): https://github.com/LineageOS/android_device_motorola_fogos/tree/lineage-24.0
- Official SystemUI source (lineage-24.0): https://github.com/LineageOS/android_frameworks_base/tree/lineage-24.0/packages/SystemUI
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

## SystemUI integration research
- The LineageOS 24.0 frameworks/base branch contains the SystemUI module under `packages/SystemUI`.
- Search results identify the status-bar lifecycle/hosting path around `CentralSurfacesImpl`, the per-display status bar window controller under `statusbar/window`, and existing media state in `NotificationMediaManager` / media pipeline.
- These are candidate integration points, not a finished patch. A proper implementation must inspect the exact branch source and its Dagger/lifecycle wiring before modifying it.
- The feature should be a SystemUI-owned controller/view, integrated through the existing status-bar lifecycle and media/notification state. It must not be a third-party overlay app, AccessibilityService, notification-listener app, or launcher widget.

## Required verification before flashing
1. Resolve the XT2363-8 product identity/fingerprint mismatch and pin matching fogos device tree, common tree, kernel, vendor blobs and firmware revisions.
2. Build the unmodified official LineageOS 24.0 fogos target first, then integrate the SystemUI feature and build again.
3. Run compile checks and inspect boot/SystemUI logs; test media, calls, timer, charging, downloads, privacy, rotation, and accessibility.
4. Make a complete user-data backup and verify a recovery/restore path.
5. Flash only a build explicitly verified for this exact device variant. Never flash an unverified generic or similarly named device image.

## Build-environment constraint
A full Android ROM build is much larger than an ordinary Android app build. A phone-only workflow needs a suitable remote Linux builder with enough CPU, RAM, storage, build time, and persistent workspace. The current NixhOS GitHub Actions workflow only builds an APK; it is not a ROM builder. A normal GitHub-hosted Actions runner should not be assumed sufficient for a complete LineageOS build. First establish a viable builder and finish source/firmware verification before attempting the ROM build.

## Current status
- Planning and compatibility research updated.
- Exact user-reported model XT2363-8 is listed by the official LineageOS wiki for moto g45 5G.
- Official LineageOS 24.0 fogos device tree and its declared common/NFC dependencies identified.
- Potential SystemUI lifecycle/media integration points located; no patch has been applied to frameworks/base.
- XT2363-8 and XT2363-9 SKU property files inspected: both explicitly identify the device as moto g45 5G.
- Product model string mismatch in the device tree is flagged for maintainer/fingerprint verification.
- Matching proprietary vendor blobs, kernel/common revisions and stock firmware baseline still need verification.
- No SystemUI source patch or ROM build has been completed yet.
- No installable NixhOS ROM image exists yet.
- No changes should be flashed to the phone at this stage.
