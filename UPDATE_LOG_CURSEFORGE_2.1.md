# UFO Future 2.1

This update is focused on progression cleanup, recipe conflict fixes, and automation reliability after the 2.0.x stabilization releases.

## Progression Fixes

- Fixed the QMF component matrix batch progression.
- `Tesseract Component Matrix`, `Event Horizon Component Matrix`, and `Cosmic String Component Matrix` now require **24 of the previous tier to craft 1 of the next tier**.
- This replaces the incorrect bulk behavior where those recipes produced large batches and made late component tiers much cheaper than intended.
- Clarified the first `Quantum Matter Fabricator` multiblock setup by making the preview point players toward `Stellar Field Generator Mk.I or better`.
- The first QMF path no longer appears to require an `Event Horizon Component Matrix` through the preview/candidate display.

## Recipe Conflict Fixes

- Fixed T1 catalyst recipes sharing nearly identical ingredient signatures.
- Each T1 catalyst family now has a unique signature ingredient:
  - `Matterflow Catalyst T1` uses `White Dwarf Fragment Dust`
  - `Chrono Catalyst T1` uses `Neutron Star Fragment Dust`
  - `Overflux Catalyst T1` uses `Pulsar Fragment Dust`
  - `Quantum Catalyst T1` uses `Nuclear Star`
- Removed obsolete QMF bulk catalyst recipes that could conflict with the intended DMA catalyst progression.
- Fixed another process recipe collision where `White Dwarf` and `Neutron Star` rods shared the same item ingredients as their dust recipes.
- Fixed duplicate UFO tool crafting patterns so paired tools no longer share the exact same shaped recipe.

## Automation And AE2 Reliability

- Recipes that previously had identical or ambiguous inputs should now behave more reliably when encoded into AE2 patterns.
- Requesting one catalyst type should no longer resolve into another catalyst with a matching ingredient set.
- The generated recipe set was audited for duplicate recipe signatures and blocking progression loops.

## Notes

- This update does not add major new content.
- The main goal is to make the 2.0 progression path less confusing and more stable, especially around QMF access, catalyst crafting, and AE2 automation.

## Validation

- `runData` completed successfully.
- `build` completed successfully.
- Generated and hand-authored recipes were audited for duplicate signatures and blocking circular dependencies.
