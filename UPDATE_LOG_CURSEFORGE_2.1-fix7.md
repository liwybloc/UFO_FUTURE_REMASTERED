# UFO Future 2.1-fix7

Maintenance release that includes the local UFO Ponder, multiblock, and asset updates together with the AE2 compatibility hotfix from 2.1-fix6.

## Added

- Added the UFO tutorial/ponder registry and client tutorial screen infrastructure.
- Added updated tutorial and guide content for fragments, quantum hatches, quantum cryoforge, stellar fields, and Stellar Nexus.

## Changed

- Updated universal multiblock processing, catalyst handling, displayed recipe state, and client sync behavior.
- Updated Stellar Nexus controller behavior and screen state handling.
- Updated quantum pattern hatch model/texture assets and related tooltip/lang entries.
- Moved the Stellar Nexus JEI texture to the normalized `textures/guis/stellar_nexus_jei.png` path.
- Lowered the Cosmic String Component Matrix QMF batch recipe requirement to the intended tier.

## Fixed

- Included the AE2 Omni Cells compatibility fix from 2.1-fix6 so add-ons can contribute their own crafting CPU co-processor/parallel processing values.
- Preserved the 2.1-fix5 multiblock, catalyst, and DMA reliability fixes while merging the local UFO Ponder branch.

## Validation

- `./gradlew.bat clean build --no-daemon --no-problems-report --no-configuration-cache`
