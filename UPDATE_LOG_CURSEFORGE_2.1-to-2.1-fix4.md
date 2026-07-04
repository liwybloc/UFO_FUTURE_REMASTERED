# UFO Future 2.1 to 2.1-fix4

This changelog summarizes everything changed, fixed, and implemented from the 2.1 release through 2.1-fix4.

## Progression And Recipe Fixes

- Fixed the `Quantum Matter Fabricator` component matrix batch progression.
- `Tesseract Component Matrix`, `Event Horizon Component Matrix`, and `Cosmic String Component Matrix` now require **24 of the previous tier to craft 1 of the next tier**.
- This replaces the incorrect bulk behavior that made late component tiers much cheaper than intended.
- Clarified the first `Quantum Matter Fabricator` multiblock setup by making the preview point players toward `Stellar Field Generator Mk.I or better`.
- The first QMF path no longer appears to require an `Event Horizon Component Matrix` through the preview/candidate display.
- Fixed T1 catalyst recipes that shared nearly identical ingredient signatures.
- Each T1 catalyst family now has its own signature ingredient:
  - `Matterflow Catalyst T1` uses `White Dwarf Fragment Dust`
  - `Chrono Catalyst T1` uses `Neutron Star Fragment Dust`
  - `Overflux Catalyst T1` uses `Pulsar Fragment Dust`
  - `Quantum Catalyst T1` uses `Nuclear Star`
- Removed obsolete QMF bulk catalyst recipes that could conflict with the intended DMA catalyst progression.
- Fixed another process recipe collision where `White Dwarf Fragment` and `Neutron Star Fragment` rods shared the same item ingredients as their dust recipes.
- Fixed duplicate UFO tool crafting patterns so paired tools no longer share the exact same shaped recipe.
- Audited generated and hand-authored recipes for duplicate signatures and blocking progression loops.

## JEI And Visual Fixes

- Fixed the black/pink missing-texture background in the `Stellar Nexus` JEI recipe category.
- The Stellar Nexus JEI page now draws its recipe background explicitly.
- Fixed the remaining Stellar Nexus JEI background issue by removing the dependency on an external PNG for the recipe background.
- The Stellar Nexus recipe layout is now drawn directly in code, avoiding broken resource lookups or stale texture-cache issues.
- Fixed the Stellar Nexus JEI texture path again by explicitly using the lowercase `ufo:textures/guis/stellar_nexus_jei.png` asset and removing the duplicated uppercase asset under `assets/ae2`.
- Reworked the `Dimensional Matter Assembler` visuals with a custom Blockbench model and texture.
- Added a dedicated client-side renderer for dynamic fluid volume inside the `Dimensional Matter Assembler`.
- Fixed Dimensional Matter Assembler fluid rendering so it uses the fluids currently stored in the machine tanks.
- Both DMA fluid reservoirs now remain visible even when only one fluid is loaded.
- Fixed visual seams in the custom Dimensional Matter Assembler model by switching it to the correct `cutout` render type for its non-translucent texture.

## AE2 Automation And Machine Reliability

- Fixed UFO AE2 crafting CPUs disconnecting after server/world load.
- Mega Crafting Storages and Mega Co-Processors now attempt to reform and reconnect their crafting multiblock after loading.
- Players should no longer need to break and replace those blocks manually after a world/server restart.
- Fixed Mega Co-Processor and Mega Crafting Storage formatting in the AE2 CPU selection list.
- Large values now display in readable compact form such as `50M` and `1T` instead of long raw numbers.
- Tightened the `Entropic Assembler Matrix` pattern check so only molecular-assembler-compatible patterns enter that machine path.
- Recipes with previously identical or ambiguous inputs should now behave more reliably when encoded into AE2 patterns.
- Requesting one catalyst type should no longer resolve into another catalyst with a matching ingredient set.

## Guide And Localization

- Updated the AE2 guide page opened with `G` so mega crafting storages and mega co-processors no longer show old names.
- Replaced outdated guide names such as `Quantum Drive Matrix`, `Tesseract Unit`, `Dimensional Storage Cube`, `Singularity Accelerator`, and `Qubit Co-Processor` with the current 2.1 names.
- Added Simplified Chinese localization through `zh_cn.json`.
- The Chinese localization now matches the English language file key-for-key.
- Updated Chinese names for the mega crafting storages and mega co-processors to match the current naming scheme.

## Compatibility

- Added optional Applied Flux processing recipes:
  - `Charged Redstone Block` to `Printed Energy Processor`
  - `Printed Energy Processor` to `Energy Processor`
- Added optional runtime dependency entries for Applied Flux and MEGA Cells so release builds include those integrations correctly when present.

## Licensing And Credits

- Updated the project license notice.
- Source code is now declared as LGPLv3+.
- Art, textures, models, and other visual assets are now declared as CC BY-NC-SA 3.0.
- This matches the licensing split used by Applied Energistics 2.
- Added texture attribution and thanks for:
  - AE2 Crystal Science: https://github.com/Frostbite-time/AE2-Crystal-Science
  - GT New Horizons Modpack: https://github.com/GTNewHorizons/GT-New-Horizons-Modpack

## Validation

- `runData` completed successfully for the 2.1 release.
- `build` completed successfully for the 2.1 release.
- `compileJava` completed successfully for 2.1-fix1 and 2.1-fix2.
- `zh_cn.json` was validated against `en_us.json` with no missing localization keys.
- `./gradlew.bat processResources` completed successfully for the licensing metadata update.
- `./gradlew.bat clean build` completed successfully for the final 2.1-fix4 release artifact.
