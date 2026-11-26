# fogos source verification — 2026-11-26

## Confirmed platform version

The LineageOS android manifest at branch lineage-24.0 declares its AOSP remote revision as refs/tags/android-17.0.0_r1. Therefore this candidate ROM base is Android 17, not Android 15.

Source: https://raw.githubusercontent.com/LineageOS/android/lineage-24.0/default.xml

## Device-tree facts at the inspected commit

- Repository: LineageOS/android_device_motorola_fogos
- Branch: lineage-24.0
- Inspected commit: fcd0be731f83e5fb80e42be061d47b0d78f627ae
- Device product: lineage_fogos
- Device codename: fogos
- Product build description: fogos_g-user 15 V1UGS35H.75-14-3-12
- Device tree inherits device/motorola/sm6375-common/BoardConfigCommon.mk and common.mk.
- BoardConfig.mk includes vendor/motorola/fogos/BoardConfigVendor.mk.
- device.mk inherits vendor/motorola/fogos/fogos-vendor.mk.
- Kernel config fragment referenced by BoardConfig.mk: vendor/ext_config/moto-holi-fogos.config

## Important distinction

The platform source branch is Android 17 while the inspected device build fingerprint describes a stock Android 15 vendor baseline. This is not automatically incompatible: Android device trees can target a newer platform while using an older vendor implementation when the device/vendor interface and maintainer configuration support it. However, this must be proven from the exact device-tree, common-tree, kernel and vendor configuration—not assumed.

The common-device repository currently exposes branches fourteen, fifteen and sixteen. The kernel repository currently exposes branches fourteen, fifteen and sixteen. No branch named seventeen was present in the branch listings checked on 2026-11-26. Do not select a branch solely by matching the number to the Android platform version.

## Next engineering gate

Before writing the complete local manifest, inspect the exact fogos commit's kernel references, the common tree's matching build configuration, and the maintainer's supported vendor baseline. Then record immutable SHAs for the compatible set. The source bootstrap document intentionally leaves these items unchecked.

No ROM source sync, ROM build, SystemUI patch, or device flash has been performed.