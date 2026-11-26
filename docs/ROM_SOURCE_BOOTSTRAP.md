# NixhOS ROM source bootstrap — fogos

## Purpose

This document records the first reproducible source-checkout step for the Moto G45 5G (fogos). It is deliberately separate from the Android app build. It does not claim that a ROM has been synced or built.

## Confirmed upstream inputs

| Component | Repository / source | Verified ref |
|---|---|---|
| Device tree | LineageOS/android_device_motorola_fogos | lineage-24.0 |
| Device tree commit observed 2026-11-26 | fcd0be731f83e5fb80e42be061d47b0d78f627ae | commit on lineage-24.0 |
| Common device dependency | Motorola-SM6375-Devs/android_device_motorola_sm6375-common | Device tree declares this dependency; compatible branch still needs confirmation |
| NFC dependency | LineageOS/android_hardware_samsung_slsi_nfc | Device tree declares this dependency; branch is lineage-24.0 |
| Kernel candidate | Motorola-SM6375-Devs/android_kernel_motorola_sm6375 | Repository has fourteen, fifteen, and sixteen branches; matching branch not yet verified |

The device tree's lineage.dependencies currently declares:
- android_device_motorola_sm6375-common → device/motorola/sm6375-common
- android_hardware_samsung_slsi_nfc → hardware/samsung_slsi/nfc

Do not infer that the common-device or kernel branch named sixteen is compatible with this exact lineage-24.0 device-tree revision merely from the branch names. The matching device maintainer configuration and build metadata must be checked first.

## Proposed local manifest entry

Once the base LineageOS manifest branch and dependency revisions are verified, add this device project to the checkout's local manifest:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<manifest>
    <project
        name="LineageOS/android_device_motorola_fogos"
        path="device/motorola/fogos"
        remote="github"
        revision="lineage-24.0" />
</manifest>
```

This entry is a source declaration only. It does not fetch proprietary vendor blobs and does not prove the complete device source set is buildable.

## Required checks before adding the full manifest

1. Confirm which Android platform version the selected LineageOS lineage-24.0 branch targets.
2. Inspect the fogos tree's current BoardConfig.mk, device.mk, lineage_fogos.mk, lineage.dependencies, and kernel references at the same commit.
3. Verify the matching common-device and kernel revisions against that exact device-tree commit and its maintainer instructions.
4. Identify the required proprietary vendor blobs and their matching stock-firmware extraction source.
5. Pin all resolved projects to immutable commit SHAs in a lock/reference file before any ROM build.
6. Build the unmodified upstream fogos target first. Only after that succeeds should the NixhOS SystemUI patch be introduced.

## Builder and device safety

- A full Android ROM build requires a suitable persistent Linux builder; the existing GitHub Actions workflow only runs gradle assembleDebug for the app.
- Do not run a ROM build on the assumption that an ordinary GitHub-hosted runner has sufficient resources.
- Do not unlock the bootloader, wipe data, or flash images as part of source setup.
- No installable NixhOS image exists at this stage.

## Current milestone status

- [x] Confirmed the NixhOS repository is not an Android ROM source checkout.
- [x] Confirmed the fogos device-tree repository and its declared common/NFC dependencies.
- [x] Recorded the device-tree commit observed on 2026-11-26.
- [ ] Verified the exact Android platform version and compatible common/kernel revisions.
- [ ] Verified vendor blobs and stock-firmware baseline.
- [ ] Established a persistent remote Linux ROM builder.
- [ ] Synced and built the unmodified upstream fogos ROM.
- [ ] Implemented and compiled the SystemUI Dynamic Island patch.