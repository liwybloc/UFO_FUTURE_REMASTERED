# UFO Future 2.1-fix6

Compatibility hotfix for AE2 crafting CPU add-ons.

## Fixed

- Fixed compatibility with AE2 add-ons that add their own crafting CPU co-processors, including AE2 Omni Cells Quantum Crafting Storage.
- UFO now preserves AE2's normal `CraftingCPUCluster#addBlockEntity` flow instead of cancelling it for large co-processors, allowing other mods' injections to count their parallel processing values.
- Large UFO crafting units still bypass AE2's default per-block 16-thread limit.
- Clamped extremely large exposed co-processor totals to avoid overflow in AE2 crafting CPU scheduling math.
- Kept the CPU selection UI infinity threshold aligned with the safe co-processor maximum.

## Validation

- `./gradlew.bat build --no-daemon --no-problems-report --no-configuration-cache`
