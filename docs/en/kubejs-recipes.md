# KubeJS Recipes

This page documents how to create, modify, and remove custom recipes via [KubeJS](https://kubejs.com/) for the main UFO Future multiblocks in Minecraft 1.21.1 (NeoForge).

## Covered Recipe Types

- `ufo:dimensional_assembly`
- `ufo:stellar_simulation`
- `ufo:universal_multiblock`
- `ufo:qmf_recipe` (legacy, still supported by QMF)

---

## DMA

### Type

```txt
ufo:dimensional_assembly
```

### Basic Structure

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `item_inputs` | Array | Yes | Item ingredients with counts |
| `fluid_inputs` | Array | No | Fluid ingredients with amounts in mB |
| `item_outputs` | Array | No | Item outputs |
| `fluid_outputs` | Array | No | Fluid outputs |
| `energy` | Integer | Yes | Total AE energy consumed |
| `time` | Integer | Yes | Time in ticks |

> At least one output must exist: `item_outputs` or `fluid_outputs`.

### Example

```js
ServerEvents.recipes(event => {
  event.custom({
    type: 'ufo:dimensional_assembly',
    item_inputs: [
      {
        ingredient: { item: 'minecraft:diamond' },
        count: 4
      },
      {
        ingredient: { item: 'minecraft:netherite_ingot' },
        count: 1
      }
    ],
    fluid_inputs: [
      {
        ingredient: { fluid: 'ufo:source_liquid_starlight_fluid' },
        amount: 500
      }
    ],
    item_outputs: [
      {
        id: 'ufo:quantum_anomaly',
        amount: 1
      }
    ],
    fluid_outputs: [],
    energy: 1000000,
    time: 400
  }).id('kubejs:custom_quantum_anomaly')
})
```

### Tag Inputs

```js
ServerEvents.recipes(event => {
  event.custom({
    type: 'ufo:dimensional_assembly',
    item_inputs: [
      {
        ingredient: { tag: 'c:ingots/iron' },
        count: 16
      }
    ],
    fluid_inputs: [],
    item_outputs: [
      {
        id: 'ufo:obsidian_matrix',
        amount: 2
      }
    ],
    fluid_outputs: [],
    energy: 50000,
    time: 100
  }).id('kubejs:tagged_obsidian_matrix')
})
```

### Removal

```js
ServerEvents.recipes(event => {
  event.remove({ id: 'ufo:dma/quantum_anomaly' })
  event.remove({ type: 'ufo:dimensional_assembly', output: 'ufo:quantum_anomaly' })
})
```

### Notes

- DMA recipes are shapeless.
- DMA supports up to 9 item inputs.
- Coolant is not part of the recipe definition.

---

## Stellar Nexus

### Type

```txt
ufo:stellar_simulation
```

### Basic Structure

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `simulation_name` | String | Yes | Name shown in the controller |
| `item_inputs` | Array | Yes | Items consumed from the ME network |
| `fluid_inputs` | Array | No | Fluids consumed from the ME network |
| `item_outputs` | Array | No | Produced items in AE2 GenericStack format |
| `fluid_outputs` | Array | No | Produced fluids in AE2 GenericStack format |
| `energy` | Integer | Yes | Total AE energy |
| `time` | Integer | Yes | Duration in ticks |
| `cooling_level` | Integer | Yes | Thermal stress from 0 to 3 |
| `field_tier` | Integer | Yes | Minimum field generator tier |
| `fuel_fluid` | String | No | Fuel fluid id |
| `fuel_amount` | Integer | No | Fuel amount in mB |
| `coolant_fluid` | String | No | Coolant fluid id |
| `coolant_amount` | Integer | No | Coolant amount in mB |

### AE2 GenericStack

```json
{ "#": 15000000, "#t": "ae2:i", "id": "minecraft:iron_ingot" }
```

- `#` = amount
- `#t` = type: `ae2:i` for item, `ae2:f` for fluid
- `id` = full registry id

### Example

```js
ServerEvents.recipes(event => {
  event.custom({
    type: 'ufo:stellar_simulation',
    simulation_name: 'Custom Void Harvest',
    item_inputs: [
      {
        amount: 8,
        ingredient: { item: 'ufo:enriched_neutronium_sphere' }
      },
      {
        amount: 4,
        ingredient: { item: 'minecraft:nether_star' }
      }
    ],
    fluid_inputs: [
      {
        amount: 200000,
        ingredient: { fluid: 'ufo:raw_star_matter_plasma' }
      }
    ],
    item_outputs: [
      { '#': 5000000, '#t': 'ae2:i', id: 'minecraft:ender_pearl' },
      { '#': 2000000, '#t': 'ae2:i', id: 'minecraft:blaze_rod' }
    ],
    fluid_outputs: [
      { '#': 100000, '#t': 'ae2:f', id: 'ufo:liquid_starlight' }
    ],
    energy: 300000000,
    time: 30000,
    cooling_level: 2,
    field_tier: 2,
    fuel_fluid: 'ufo:liquid_starlight',
    fuel_amount: 20000,
    coolant_fluid: 'ufo:source_gelid_cryotheum',
    coolant_amount: 25000
  }).id('kubejs:custom_void_harvest')
})
```

### Removal

```js
ServerEvents.recipes(event => {
  event.remove({ id: 'ufo:stellar_simulation/iron_core_fusion' })
  event.remove({ type: 'ufo:stellar_simulation' })
})
```

---

## Universal Multiblock

This is the recommended format for the other mass-automation multiblocks.

### Type

```txt
ufo:universal_multiblock
```

### Supported Machines

- `machine: 'qmf'`
- `machine: 'quantum_slicer'`
- `machine: 'quantum_processor_assembler'`
- `machine: 'quantum_cryoforge'`

### Basic Structure

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `machine` | String | Yes | Target multiblock |
| `recipe_name` | String | No | Internal/display name |
| `item_inputs` | Array | Yes | Item inputs with amounts |
| `fluid_inputs` | Array | No | Fluid inputs with amounts in mB |
| `item_output` | Object | No | Single item output |
| `fluid_output` | Object | No | Single fluid output |
| `fluid_output_amount` | Integer | No | Total produced fluid amount in mB |
| `energy` | Integer/Long | Yes | Total AE energy |
| `time` | Integer | Yes | Time in ticks |
| `required_tier` | Integer | No | Minimum machine tier |

> At least one output must exist: `item_output` or `fluid_output`.

### Formats

Item input:

```json
{
  "ingredient": { "item": "minecraft:diamond" },
  "amount": 64
}
```

Tag input:

```json
{
  "ingredient": { "tag": "c:ingots/iron" },
  "amount": 256
}
```

Fluid input:

```json
{
  "fluid": {
    "id": "ufo:uu_matter",
    "amount": 1
  },
  "amount": 128000
}
```

Item output:

```json
{
  "id": "ufo:dimensional_processor",
  "count": 64
}
```

Fluid output:

```json
{
  "id": "ufo:source_stable_coolant",
  "amount": 1
}
```

Combined with:

```json
"fluid_output_amount": 128000
```

### Example - QMF

```js
ServerEvents.recipes(event => {
  event.custom({
    type: 'ufo:universal_multiblock',
    machine: 'qmf',
    recipe_name: 'kubejs/qmf/corporeal_matter_batch',
    item_inputs: [
      {
        ingredient: { item: 'ufo:proto_matter' },
        amount: 128
      },
      {
        ingredient: { item: 'minecraft:iron_block' },
        amount: 4096
      },
      {
        ingredient: { item: 'ufo:obsidian_matrix' },
        amount: 1024
      }
    ],
    fluid_inputs: [
      {
        fluid: {
          id: 'ufo:uu_matter',
          amount: 1
        },
        amount: 256000
      }
    ],
    item_output: {
      id: 'ufo:corporeal_matter',
      count: 64
    },
    energy: 1280000000,
    time: 3600,
    required_tier: 1
  }).id('kubejs:qmf_corporeal_matter_batch')
})
```

### Example - Quantum Processor Assembler

```js
ServerEvents.recipes(event => {
  event.custom({
    type: 'ufo:universal_multiblock',
    machine: 'quantum_processor_assembler',
    recipe_name: 'kubejs/quantum_processor_assembler/dimensional_processor',
    item_inputs: [
      {
        ingredient: { item: 'ufo:printed_dimensional_processor' },
        amount: 64
      },
      {
        ingredient: { item: 'ae2:printed_silicon' },
        amount: 64
      },
      {
        ingredient: { item: 'ae2:fluix_dust' },
        amount: 128
      }
    ],
    item_output: {
      id: 'ufo:dimensional_processor',
      count: 64
    },
    energy: 12000000,
    time: 1200
  }).id('kubejs:quantum_processor_assembler_dimensional_processor')
})
```

### Example - Quantum Cryoforge

```js
ServerEvents.recipes(event => {
  event.custom({
    type: 'ufo:universal_multiblock',
    machine: 'quantum_cryoforge',
    recipe_name: 'kubejs/quantum_cryoforge/stable_coolant_t3',
    item_inputs: [
      {
        ingredient: { item: 'minecraft:blue_ice' },
        amount: 256
      },
      {
        ingredient: { item: 'ufo:obsidian_matrix' },
        amount: 64
      },
      {
        ingredient: { item: 'ufo:quantum_anomaly' },
        amount: 16
      }
    ],
    fluid_inputs: [
      {
        fluid: {
          id: 'ufo:source_gelid_cryotheum',
          amount: 1
        },
        amount: 128000
      }
    ],
    fluid_output: {
      id: 'ufo:source_stable_coolant',
      amount: 1
    },
    fluid_output_amount: 128000,
    energy: 50000000,
    time: 9600,
    required_tier: 3
  }).id('kubejs:quantum_cryoforge_stable_coolant_t3')
})
```

### Example - Quantum Slicer

There are currently no generated datapack examples for Quantum Slicer, but the serializer already accepts custom recipes in the same format:

```js
ServerEvents.recipes(event => {
  event.custom({
    type: 'ufo:universal_multiblock',
    machine: 'quantum_slicer',
    recipe_name: 'kubejs/quantum_slicer/printed_singularity_core',
    item_inputs: [
      {
        ingredient: { item: 'ae2:singularity' },
        amount: 1
      },
      {
        ingredient: { item: 'ufo:obsidian_matrix' },
        amount: 8
      },
      {
        ingredient: { tag: 'c:dusts/fluix' },
        amount: 64
      }
    ],
    fluid_inputs: [
      {
        fluid: {
          id: 'ufo:source_temporal_fluid',
          amount: 1
        },
        amount: 16000
      }
    ],
    item_output: {
      id: 'ufo:printed_dimensional_processor',
      count: 8
    },
    energy: 64000000,
    time: 900,
    required_tier: 2
  }).id('kubejs:quantum_slicer_printed_singularity_core')
})
```

### Removal

```js
ServerEvents.recipes(event => {
  event.remove({ id: 'ufo:universal/qmf/corporeal_matter_batch' })
  event.remove({ type: 'ufo:universal_multiblock' })
})
```

---

## Legacy QMF Compatibility

QMF still accepts:

```txt
ufo:qmf_recipe
```

This format remains valid for backwards compatibility, but for new recipes prefer `ufo:universal_multiblock` with `machine: 'qmf'`.

### Example

```js
ServerEvents.recipes(event => {
  event.custom({
    type: 'ufo:qmf_recipe',
    recipe_name: 'kubejs/qmf/legacy_proto_matter',
    item_inputs: [
      {
        ingredient: { item: 'ufo:obsidian_matrix' },
        amount: 32
      }
    ],
    fluid_inputs: [
      {
        fluid: {
          id: 'ufo:uu_matter',
          amount: 1
        },
        amount: 32000
      }
    ],
    output: {
      id: 'ufo:proto_matter',
      count: 4
    },
    energy: 40000000,
    time: 600,
    required_tier: 1
  }).id('kubejs:qmf_legacy_proto_matter')
})
```

---

## Datapack

If you prefer datapacks instead of KubeJS, place the JSON files in:

```txt
data/<your_namespace>/recipe/<recipe_name>.json
```

Use exactly the same structure shown in the examples above.

---

*See also: [DMA](dma.md) · [Quantum Matter Fabricator](quantum-matter-fabricator.md) · [Quantum Processor Assembler](quantum-processor-assembler.md) · [Stellar Nexus](stellar-nexus.md) · [Catalysts](catalysts.md) · [Materials](materials.md)*
