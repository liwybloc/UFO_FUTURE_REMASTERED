# Recipe Balance Audit

This document audits the current UFO Future recipe set and proposes buffs, nerfs, and refactors for a harder standalone `UFO Future + AE2` progression.

The audit was based on the actual repository state, with generated recipes and code treated as source of truth.

## Scope

Audited recipe files:

- `42` generated standard crafting recipes
- `103` generated DMA recipes
- `11` Stellar Nexus simulations
- `14` handcrafted universal multiblock recipes
- `2` generated universal multiblock recipes
- `10` disassembly recipes

Total audited: `182` recipe files

Coverage audit:

- `148` registered `ufo:` items and block-items checked
- `53` registered IDs currently have no recipe output in the audited data

## Executive Summary

The mod already has a strong late-game backbone:

- the DMA progression is real and coherent
- the fragment -> matter -> matrix chain is solid
- the universal multiblocks already support industrial processor throughput
- the Stellar Nexus already reaches billion-AE and million-output scale

The biggest problems are not "lack of endgame". They are:

1. Recipe coverage is incomplete for several player-facing blocks and tools.
2. A few recipe families are too flat for the power they grant.
3. Some endgame items use elegant themes but not enough cascade pressure.
4. Infinity cell pricing is too uniform across trivial and absurd targets.
5. The Stellar Nexus machine itself needs stronger structural gating if it is meant to be your true endgame.

## Source-Of-Truth Findings

Important implementation facts confirmed during audit:

- Stellar Nexus safe mode multiplies energy, fuel, and coolant cost by `2.5x`.
- Stellar Nexus overclock multiplies startup energy by `10x`, fuel and coolant by `5x`, heat by `5x`, and progress by `5x`.
- Stellar Nexus global energy capacity is `200,000,000,000 AE`.
- DMA base internal buffer is `500,000 AE`.
- DMA catalyst stacking already has strong thermal tradeoffs and 4-stack synergy.

The docs are slightly behind the code in some areas, especially Stellar Nexus cost scale.

## Coverage Audit

### Probably Missing Player-Facing Recipes

These IDs appear registered but do not appear as outputs in the audited recipes:

- `ufo:ae_energy_input_hatch`
- `ufo:me_massive_fluid_hatch`
- `ufo:me_massive_input_hatch`
- `ufo:me_massive_output_hatch`
- `ufo:entropy_assembler_core_casing`
- `ufo:entropy_computer_condensation_matrix`
- `ufo:entropy_singularity_casing`
- `ufo:stellar_field_generator_t1`
- `ufo:stellar_field_generator_t2`
- `ufo:stellar_field_generator_t3`
- `ufo:stellar_nexus_controller`
- `ufo:quantum_entropy_casing`
- `ufo:structure_scanner`
- `ufo:ufo_sword`
- `ufo:ufo_pickaxe`
- `ufo:ufo_axe`
- `ufo:ufo_shovel`
- `ufo:ufo_hoe`
- `ufo:ufo_hammer`
- `ufo:ufo_greatsword`
- `ufo:ufo_bow`
- `ufo:ufo_fishing_rod`
- `ufo:bismuth`

Recommendation:

- add real recipes for all Stellar Nexus structural parts and hatches
- either add recipes for the energy tools or convert them into transforms or upgrades from `ufo:ufo_staff`
- either implement a gameplay source for `bismuth` or remove it from player-facing progression

### Likely Intentional Non-Crafting IDs

These are acceptable without direct recipes if they are expected to come from fluid handling:

- fluid blocks
- buckets

Examples:

- `ufo:liquid_starlight_bucket`
- `ufo:temporal_fluid_bucket`
- `ufo:uu_matter_bucket`
- `ufo:raw_star_matter_plasma_fluid_block`

### Conditional Integration Gap

## Family Audit

## 1. Standard Crafting Recipes

### What exists

This layer contains the mod's direct crafting outputs such as:

- `obsidian_matrix`
- `graviton_plated_casing`
- `dimensional_matter_assembler`
- fragment blocks and nuggets
- quantum multiblock controllers
- large crafting storages
- mega co-processors

### Verdict

- `obsidian_matrix` is a good anchor recipe and should stay simple.
- `graviton_plated_casing` and `dimensional_matter_assembler` are good early gates.
- mega storages and mega co-processors are too flat for what they represent.
- quantum controllers are thematically strong, but still light on deep processor sink.

### Buff / Nerf / Refactor

#### Keep

- `obsidian_matrix`
- `graviton_plated_casing`
- `dimensional_matter_assembler`

#### Nerf by Refactor

The following should become deeper upgrade chains instead of mostly single-hop conversions:

- mega crafting storages
- mega co-processors

Current issue:

- many storage and co-processor upgrades are effectively "previous tier + 1 prestige item"
- this feels more like compression than endgame construction

Suggested refactor:

- require multiple copies of the previous tier, not one
- add `dimensional_processor` or future advanced processor families as recurring sinks
- tie the top storage tiers to matrix families
- tie top co-processors to engineering-heavy processor chains

Example target direction:

- `mk2 ~= 4x to 8x previous tier item plus supporting processors`
- `mk3 ~= 8x to 16x previous tier item plus matter or matrix gates`

#### Buff

Quantum multiblock controllers should be more factory-defining.

Suggested change:

- keep the current item themes
- add processor-family counts and previous multiblock dependencies
- make the `quantum_processor_assembler_controller` and `quantum_matter_fabricator_controller` clearly more expensive than `quantum_slicer_controller`

## 2. DMA Bootstrap Recipes

### What exists

The DMA bootstrap layer includes:

- `dust_blizz_bootstrap`
- `cryotheum_dust_bootstrap`
- `gelid_cryotheum_bootstrap`
- dimensional processor press and first dimensional processor line

### Verdict

This section is healthy. It supports AE2-only progression without immediately hard-locking on extra mods.

### Buff / Nerf / Refactor

- keep the bootstrap recipes
- keep the low cost profile
- preserve the "simple inputs, useful outputs" feel

Only minor suggestion:

- unify bootstrap naming and doc language so players understand which recipes are fallback starters and which are permanent industrial routes

## 3. DMA Stellar Fragment Chain

### What exists

The ingot progression currently flows:

- `white_dwarf_fragment_ingot`
- `neutron_star_fragment_ingot`
- `pulsar_fragment_ingot`

Then side products convert those into:

- dust
- fluid
- rods
- blocks

### Verdict

This is one of the strongest recipe families in the mod. The identity is clear and the chain escalates cleanly.

### Buff / Nerf / Refactor

#### Keep

- the current linear escalation
- liquid starlight as the common fluid backbone

#### Buff Slightly

`pulsar_fragment` is already important enough to justify a slightly harder gate.

Suggested change:

- small AE increase
- add one more advanced ingredient or increase current counts

Reason:

- pulsar-derived content feeds anomaly, matter, and high-end progression

## 4. DMA Fluid And Matter Economy

### What exists

This family includes:

- `uu_amplifier`
- `uu_matter`
- `gelid_cryotheum`
- `stable_coolant`
- `liquid_starlight`
- `raw_star_matter_plasma`
- `transcending_matter_fluid`
- `primordial_matter_liquid`
- `spatial` and `temporal` fluids
- `neutronium_sphere`
- `enriched_neutronium_sphere`
- `proto_matter`
- `corporeal_matter`
- stellar matter items
- `dark_matter`

### Verdict

This is the real heart of UFO Future progression. It works, but a few recipes are still too cheap compared to how much later content leans on them.

### Buff / Nerf / Refactor

#### Buff

`raw_star_matter_plasma`

Reason:

- it is the backbone fluid for the Stellar Nexus
- it currently feels closer to a production fluid than to a civilization-scale precursor

Suggested change:

- increase energy and time
- add an extra high-tier material input, possibly `dimensional_processor` or `corporeal_matter`

#### Buff

`enriched_neutronium_sphere`

Reason:

- it gates a huge amount of endgame output
- current pricing is solid but still not terrifying enough for your intended mk3 factories

Suggested change:

- increase fluid volume
- add a processor requirement

#### Buff

`dark_matter`

Reason:

- this is a prestige material and should feel unforgettable
- current recipe is good, but still mostly a raw-count compression

Suggested refactor:

- keep the existing stellar matter requirements
- add component or processor pressure
- optionally require one late catalyst or one high-end matrix

#### Keep

- `proto_matter`
- `corporeal_matter`
- tiered stellar matter recipes

These already communicate progression well.

## 5. DMA Catalyst Family

### What exists

The mod already has four proper catalyst families:

- Matterflow
- Chrono
- Overflux
- Quantum

Each has T1, T2, and T3.

### Verdict

The family identities are excellent. The recipes are the part that should hit harder, especially T3.

### Buff / Nerf / Refactor

#### Buff

All T3 catalysts

Reason:

- T3 catalysts are endgame-defining power pieces
- their gameplay impact is already strong in code
- their recipe pricing should match that power more aggressively

Suggested refactor:

- require more than one previous-tier catalyst
- add processor or matrix inputs
- differentiate family identity through matter choice and fluid choice

#### Nerf

Overflux T3 relative accessibility

Reason:

- negative or neutral heat is one of the strongest utility effects in the whole system
- the recipe should not land too close to the others if it meaningfully stabilizes dangerous builds

Suggested change:

- slightly increase AE, time, or prestige input count

#### Keep

- the current family themes
- the fluid identity separation

## 6. DMA Component Matrix Chain

### What exists

The current matrix chain is:

- `phase_shift_component_matrix`
- `hyper_dense_component_matrix`
- `tesseract_component_matrix`
- `event_horizon_component_matrix`
- `cosmic_string_component_matrix`

### Verdict

This chain is excellent structurally, but the count pressure is still too light for a GTNH-style endgame.

### Buff / Nerf / Refactor

#### Buff by Refactor

Make every matrix recipe consume more of the previous matrix and more processors.

Suggested direction:

- `phase_shift`: keep approachable
- `hyper_dense`: increase dimensional processor and AE component demand
- `tesseract`: add more previous-tier matrices
- `event_horizon`: introduce serious matter pressure
- `cosmic_string`: turn into a true bottleneck part, not just a prestige craft

Target feeling:

- cosmic string should be the item players build factories around

## 7. Infinity Cells

### What exists

There are `29` generated infinity cell recipes, all with the same recipe structure:

- `cosmic_string_component_matrix`
- `quantum_anomaly`
- `64` target item
- `250,000,000 AE`
- `10,000 ticks`

### Verdict

This is elegant, but too uniform. An infinity cobblestone cell and an infinity antimatter-like cell should not be costed the same way.

### Buff / Nerf / Refactor

#### Refactor Strongly

Split infinity cells into cost bands:

- Common bulk materials
  - cobblestone, sand, water, gravel, logs
- Advanced utility materials
  - obsidian, amethyst, end stone, lava, sky stone

Suggested rule:

- keep the base pattern
- vary AE, time, fluid amount, and required matrix tier

Example direction:

- common cells: current or slightly cheaper
- advanced cells: current baseline
- exotic cells: significantly more expensive and possibly require `dark_matter` or a second matrix

## 8. Universal Multiblock Recipes

### What exists

Current universal multiblock layers:

- Quantum Slicer: mass printed processors
- Quantum Processor Assembler: mass finished processors
- QMF: stable coolant polish
- generated QPA dimensional processor recipe

### Verdict

This is one of the best parts of the mod. It solves a real scaling problem and creates room for absurd downstream crafting.

### Buff / Nerf / Refactor

#### Keep

- AE2 processor mass production
- the idea of one block input becoming a large processor batch

#### Buff Slightly

Advanced lines should unlock later than basic AE2 lines.

Suggested change:

- keep AE2 printed logic, calculation, and engineering at tier 1
- move dimensional and cross-mod advanced processors to higher multiblock tier or higher machine cost

#### Refactor

Use these machines as the backbone for future mk2 and mk3 controller recipes.

Meaning:

- endgame controllers should consume outputs from Quantum Slicer and Quantum Processor Assembler in enormous quantities

## 9. Stellar Nexus Simulation Recipes

### What exists

The current recipe set already spans:

- low-cost integration simulations
- mid-cost advanced material simulations
- true billion-AE mass synthesis

Notable current mk3 simulations:

- `Massive Iron Synthesis`: `2B AE`, `45 min`, `15M iron`
- `Massive Copper Synthesis`: `2B AE`, `45 min`, `15M copper`
- `Massive Gold Synthesis`: `2.5B AE`, `50 min`, `15M gold`
- `Massive Netherite Synthesis`: `5B AE`, `80 min`, `5M netherite`

### Verdict

The direction is correct. The machine already does what an endgame machine should do. The weak point is structural gating, not reward philosophy.

### Buff / Nerf / Refactor

#### Keep

- million-scale outputs
- billion-AE startup costs
- long runtimes
- high precursor fluid demand

#### Buff

The multiblock build cost itself

Reason:

- if this is the final industrial payoff, the structure parts need to feel like a megaproject

Suggested new rule:

- Mk1 machine = first real industrial breakthrough
- Mk2 machine = around `10x` Mk1 total cost
- Mk3 machine = around `50x` Mk2 total cost

#### Refactor

Split the machine into explicit tiered construction:

- `field_generator_mk1`
- `field_generator_mk2`
- `field_generator_mk3`
- controller upgrades or tiered controller cores

Then scale:

- previous-tier machine parts
- processor counts
- matrix counts
- matter counts
- casing counts

#### Buff

The "soft" integration recipes such as very low-cost modded outputs

Reason:

- some of them feel closer to showcase recipes than to true Stellar Nexus programs

Suggested fix:

- either move them down conceptually as entry or tutorial simulations
- or make them materially more expensive so they belong in the same machine fantasy

## 10. Disassembly Recipes

### What exists

There are `10` AE2 storage cell disassembly recipes for white dwarf and neutron-star storage cells.

### Verdict

This is good quality-of-life and should stay.

### Buff / Nerf / Refactor

- keep as-is
- no major rebalance needed

If you later make storage cells dramatically more expensive, keep disassembly generous enough that experimentation does not feel punishing.

## Recommended Priority Order

If you want the highest-impact rebalance first, do it in this order:

1. Add missing recipes for Stellar Nexus parts, hatches, field generators, controller, and scanner.
2. Refactor mega storages and mega co-processors into deeper cascade upgrades.
3. Refactor infinity cell pricing into common, advanced, and exotic bands.
4. Buff T3 catalysts and high-tier matrix recipes.
5. Buff `raw_star_matter_plasma`, `enriched_neutronium_sphere`, and `dark_matter`.
6. Turn Quantum Slicer and Quantum Processor Assembler outputs into large sinks for future mk2 and mk3 multiblocks.
7. Introduce true Mk1, Mk2, and Mk3 Stellar Nexus construction costs with your `10x` then `50x` scaling rule.

## Concrete Design Direction For Your Vision

For the exact fantasy you described, the best direction is:

- keep current million-scale Stellar Nexus outputs
- raise the machine build cost much more than the simulation output cost
- move the absurdity into processor cascades and tiered machine parts
- make `cosmic_string`, `dark_matter`, `charged_enriched_neutronium_sphere`, and advanced processor lines the signature bottlenecks

That gives you a late-game that feels punishing on the way up, but truly rewarding once the player completes the infrastructure.
