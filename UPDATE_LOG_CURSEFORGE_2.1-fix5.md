# UFO Future 2.1-fix5

Maintenance release focused on multiblock, catalyst, and DMA reliability.

## Fixed

- Fixed universal multiblocks only recognizing the creative Dimensional Catalyst path.
- Matterflow, Chrono, Overflux, Quantum, and Dimensional catalysts now apply correctly in universal multiblock machines.
- Chrono catalysts now speed up universal multiblock recipes instead of leaving recipe time unchanged.
- Matterflow catalysts now reduce universal multiblock energy cost.
- Overflux catalysts now affect multiblock heat generation.
- Quantum catalysts now apply their bonus item output behavior.
- Dimensional Catalyst still acts as the creative catalyst path with instant processing, no energy cost, and no heat generation.
- Fixed displayed multiblock recipe time, energy, and output amount so the controller UI reflects catalyst effects.
- Fixed external tick acceleration on the Dimensional Matter Assembler producing extra heat without advancing recipe progress.
- DMA heat is now generated only from productive crafting ticks, so accelerators such as Time Wand or Warden Soul should no longer create heat-only acceleration bugs.
- Fixed DMA accelerated progress scaling so very fast recipes are not capped by the old fixed speed limit.
- Confirmed the 2.1-fix4 AE2 crafting CPU reconnection fix remains included: Mega Crafting Storages and Mega Co-Processors reform/reconnect after world or server load.
- Lowered the Cosmic String Component Matrix QMF batch recipe requirement from tier 3 to tier 2 so the intended progression path is reachable.

## Validation

- `./gradlew compileJava`
- `./gradlew build`
