# Storage Cells

UFO Future extends AE2's storage system with two new cell series — **White Dwarf** (items) and **Neutron Star** (fluids) — using BigInteger capacities far beyond vanilla AE2 limits. It also adds **Infinity Cells** for unlimited single-resource storage.

---

## White Dwarf Item Cells

High-capacity item storage cells with BigInteger internal counting. All tiers use the **White Dwarf Item Cell Housing**.

| Tier Name | Capacity | Component Required | Registry ID |
|-----------|----------|-------------------|-------------|
| **Echo** | 40M bytes | Phase Shift Component Matrix | `ufo:white_dwarf_cell_echo` |
| **Beacon** | 100M bytes | Hyper Dense Component Matrix | `ufo:white_dwarf_cell_beaco` |
| **Nexus** | 250M bytes | Tesseract Component Matrix | `ufo:white_dwarf_cell_nexus` |
| **Core** | 750M bytes | Event Horizon Component Matrix | `ufo:white_dwarf_cell_core` |
| **Singularity** | ∞ (MAX_INT) | Cosmic String Component Matrix | `ufo:white_dwarf_cell_singularity` |

### Crafting
Each cell is crafted as a **shapeless recipe** combining:
- 1× White Dwarf Item Cell Housing
- 1× Component Matrix (tier-specific)

### White Dwarf Item Cell Housing
**Shaped crafting**:
```
G R G
R   R
I I I
```
- **G**: AE2 Quartz Vibrant Glass
- **R**: White Dwarf Fragment Rod
- **I**: White Dwarf Fragment Ingot

**ID**: `ufo:white_dwarf_item_cell_housing`

---

## Neutron Star Fluid Cells

High-capacity fluid storage cells. All tiers use the **Neutron Star Fluid Cell Housing**.

| Tier Name | Capacity | Component Required | Registry ID |
|-----------|----------|-------------------|-------------|
| **Echo** | 40M bytes | Phase Shift Component Matrix | `ufo:neutron_star_reservoir_echo` |
| **Beacon** | 100M bytes | Hyper Dense Component Matrix | `ufo:neutron_star_reservoir_beaco` |
| **Nexus** | 250M bytes | Tesseract Component Matrix | `ufo:neutron_star_reservoir_nexus` |
| **Core** | 750M bytes | Event Horizon Component Matrix | `ufo:neutron_star_reservoir_core` |
| **Singularity** | ∞ (MAX_INT) | Cosmic String Component Matrix | `ufo:neutron_star_reservoir_singularity` |

### Crafting
Each cell is crafted as a **shapeless recipe** combining:
- 1× Neutron Star Fluid Cell Housing
- 1× Component Matrix (tier-specific)

### Neutron Star Fluid Cell Housing
**Shaped crafting**:
```
G R G
R   R
I I I
```
- **G**: AE2 Quartz Vibrant Glass
- **R**: Neutron Star Fragment Rod
- **I**: Neutron Star Fragment Ingot

**ID**: `ufo:neutron_fluid_cell_housing`

---

## Infinity Cells

Single-resource cells that store **unlimited** amounts of a specific item or fluid. Perfect for bulk resource automation.

### Available Infinity Cells

#### Vanilla Resources

| Cell | Stored Resource | Registry ID |
|------|----------------|-------------|
| Water | Water (fluid) | `ufo:infinity_water_cell` |
| Lava | Lava (fluid) | `ufo:infinity_lava_cell` |
| Cobblestone | Cobblestone | `ufo:infinity_cobblestone_cell` |
| Cobbled Deepslate | Cobbled Deepslate | `ufo:infinity_cobbled_deepslate_cell` |
| End Stone | End Stone | `ufo:infinity_end_stone_cell` |
| Netherrack | Netherrack | `ufo:infinity_netherrack_cell` |
| Sand | Sand | `ufo:infinity_sand_cell` |
| Obsidian | Obsidian | `ufo:infinity_obsidian_cell` |
| Gravel | Gravel | `ufo:infinity_gravel_cell` |
| Oak Log | Oak Log | `ufo:infinity_oak_log_cell` |
| Glass | Glass | `ufo:infinity_glass_cell` |
| Amethyst Shard | Amethyst Shard | `ufo:infinity_amethyst_shard_cell` |

#### AE2 Resources

| Cell | Stored Resource | Registry ID |
|------|----------------|-------------|
| Sky Stone | AE2 Sky Stone Block | `ufo:infinity_sky_stone_cell` |

#### Dye Cells (All 16 Colors)

| Cell | Registry ID |
|------|-------------|
| White Dye | `ufo:infinity_white_dye_cell` |
| Orange Dye | `ufo:infinity_orange_dye_cell` |
| Magenta Dye | `ufo:infinity_magenta_dye_cell` |
| Light Blue Dye | `ufo:infinity_light_blue_dye_cell` |
| Yellow Dye | `ufo:infinity_yellow_dye_cell` |
| Lime Dye | `ufo:infinity_lime_dye_cell` |
| Pink Dye | `ufo:infinity_pink_dye_cell` |
| Gray Dye | `ufo:infinity_gray_dye_cell` |
| Light Gray Dye | `ufo:infinity_light_gray_dye_cell` |
| Cyan Dye | `ufo:infinity_cyan_dye_cell` |
| Purple Dye | `ufo:infinity_purple_dye_cell` |
| Blue Dye | `ufo:infinity_blue_dye_cell` |
| Brown Dye | `ufo:infinity_brown_dye_cell` |
| Green Dye | `ufo:infinity_green_dye_cell` |
| Red Dye | `ufo:infinity_red_dye_cell` |
| Black Dye | `ufo:infinity_black_dye_cell` |

### Infinity Cell Crafting (DMA Recipe)

All infinity cells follow the same pattern:
- **Inputs**: Cosmic String Component Matrix ×1, Quantum Anomaly ×1, Target Resource ×64
- **Fluid**: 2,500 mB Transcending Matter
- **Energy**: 250,000,000 AE
- **Time**: 10,000 ticks (8.3 minutes)

---

## ME Drive Display

All custom cells render as proper **3D rectangles** inside the ME Drive block:

| Cell Series | Drive Color |
|-------------|------------|
| White Dwarf (Item) | Silver / Gray |
| Neutron Star (Fluid) | Deep Blue |
| Infinity Cells | Purple / Cosmic |

---

## Component Matrices (Shared)

Components are used across both cell types and other recipes:

| Component | Registry ID |
|-----------|-------------|
| Phase Shift Component Matrix | `ufo:phase_shift_component_matrix` |
| Hyper Dense Component Matrix | `ufo:hyper_dense_component_matrix` |
| Tesseract Component Matrix | `ufo:tesseract_component_matrix` |
| Event Horizon Component Matrix | `ufo:event_horizon_component_matrix` |
| Cosmic String Component Matrix | `ufo:cosmic_string_component_matrix` |

See [Progression](progression.md) for component crafting chains.

---

*See also: [DMA](dma.md) · [Progression](progression.md) · [Materials & Fluids](materials.md)*
