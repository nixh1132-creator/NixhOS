# fogos dependency compatibility review — 2026-11-26

## Inspected immutable revisions

| Project | Branch | Commit |
|---|---|---|
| LineageOS/android_device_motorola_fogos | lineage-24.0 | fcd0be731f83e5fb80e42be061d47b0d78f627ae |
| Motorola-SM6375-Devs/android_device_motorola_sm6375-common | sixteen | f082ef714f276203452b73111115832b0363a175 |
| Motorola-SM6375-Devs/android_kernel_motorola_sm6375 | sixteen | a209f88ab062f7b430c7dc5c18921b8e46148563 |

## Positive source-level matches

- fogos BoardConfig.mk inherits device/motorola/sm6375-common/BoardConfigCommon.mk.
- The common BoardConfigCommon.mk sets TARGET_KERNEL_SOURCE := kernel/motorola/sm6375.
- The common BoardConfigCommon.mk sets the base Qualcomm defconfig and Lineage Motorola holi config.
- fogos BoardConfig.mk adds vendor/ext_config/moto-holi-fogos.config.
- The inspected kernel sixteen commit contains arch/arm64/configs/vendor/ext_config/moto-holi-fogos.config, including CONFIG_FOGOS_DTB=y and device-specific camera, NFC, fingerprint, touchscreen and charger options.
- This confirms that the selected common-tree and kernel-tree revisions contain the exact source paths referenced by the inspected fogos device tree.

## Remaining compatibility uncertainty

This is a source-path/configuration match, not a completed ROM compatibility test. The base manifest lineage-24.0 points to AOSP android-17.0.0_r1, while the device tree's build fingerprint is a stock Android 15 firmware build. The common/kernel repositories' sixteen branches are plausible matching device dependencies, but a successful full build and runtime tests are still required to prove compatibility.

The fogos device tree also requires proprietary vendor files: BoardConfig.mk includes vendor/motorola/fogos/BoardConfigVendor.mk and device.mk inherits vendor/motorola/fogos/fogos-vendor.mk. Those blobs and their exact extraction baseline have not yet been obtained or validated.

## Candidate pinned local manifest entries (not yet approved for a build)

These entries capture the revisions inspected above. They are a candidate source lock only; do not treat this as a build-ready manifest until vendor blobs, all additional dependencies, and the full target build are verified.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<manifest>
    <remote name="github" fetch="https://github.com/" />
    <project name="LineageOS/android_device_motorola_fogos" path="device/motorola/fogos" remote="github" revision="fcd0be731f83e5fb80e42be061d47b0d78f627ae" />
    <project name="Motorola-SM6375-Devs/android_device_motorola_sm6375-common" path="device/motorola/sm6375-common" remote="github" revision="f082ef714f276203452b73111115832b0363a175" />
    <project name="Motorola-SM6375-Devs/android_kernel_motorola_sm6375" path="kernel/motorola/sm6375" remote="github" revision="a209f88ab062f7b430c7dc5c18921b8e46148563" />
    <project name="LineageOS/android_hardware_samsung_slsi_nfc" path="hardware/samsung_slsi/nfc" remote="github" revision="88490b4408246e6c10ef1a6942844796f66dee5e" />
</manifest>
```

Note: The device tree's lineage.dependencies declares the common device and NFC dependency by repository name. This candidate manifest pins those and adds the kernel at the path expected by BoardConfigCommon.mk. Additional dependencies may be inherited from the common tree and must be resolved from its own dependency metadata before syncing.

## Next required work

1. Inspect the common tree's own dependency declarations and all inherited hardware repositories.
2. Inspect the fogos vendor extraction scripts and determine the exact stock firmware package/source required for XT2363-8.
3. Confirm whether the user's currently installed stock build matches the device tree's stated V1UGS35H.75-14-3-12 baseline; do not assume it does.
4. Complete a full manifest with immutable SHAs and all dependencies.
5. Only then provision a persistent Linux builder, sync sources, and attempt an unmodified upstream build.

No source sync, ROM build, SystemUI patch, APK/ROM generation, bootloader unlock, or flashing has taken place.