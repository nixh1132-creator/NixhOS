# NixhOS Dynamic Island — SystemUI implementation

## Scope

This feature is for the NixhOS custom Android ROM, not a launcher, overlay app, or standalone APK. The first milestone is a compact, system-owned status pill integrated into SystemUI. Do not change the launcher, home screen, lock screen, or other UI as part of this task.

## Device target

- Device: Motorola Moto G45 5G
- Reported model: XT2363-8
- Reported codename: fogos
- Target Android base: the exact LineageOS branch selected for the verified fogos device tree

The device tree, kernel, vendor blobs, firmware requirements, and branch compatibility must be verified against the exact XT2363-8 before building or flashing. A codename match alone is not proof of compatibility.

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

1. Confirm the exact XT2363-8 device tree and its required common/vendor repositories.
2. Confirm kernel, vendor blobs, firmware baseline, partition layout, boot/recovery method, and bootloader unlock path.
3. Build the unmodified target ROM first, then integrate the feature and build again.
4. Run compile checks and inspect boot/SystemUI logs; test media, calls, timer, charging, downloads, privacy, rotation, and accessibility.
5. Make a complete user-data backup and verify a recovery/restore path.
6. Flash only a build explicitly verified for this exact device variant. Never flash an unverified generic or similarly named device image.

## Current status

- Planning/specification only.
- No SystemUI source patch or ROM build has been completed yet.
- No installable ROM image exists yet.
- No changes should be flashed to the phone at this stage.

## Build-environment constraint

A full Android ROM build is much larger than an ordinary Android app build. A phone-only workflow needs a suitable remote Linux builder with enough CPU, RAM, storage, build time, and persistent workspace. A normal GitHub-hosted Actions runner should not be assumed sufficient for a complete LineageOS build. First establish a viable builder and verify device sources before attempting the ROM build.
