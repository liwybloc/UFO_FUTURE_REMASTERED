# KubeJS Pei Fang

Ben ye shuo ming ru he tong guo [KubeJS](https://kubejs.com/) wei UFO Future zai Minecraft 1.21.1 (NeoForge) zhong de zhu yao duo fang kuai ji qi chuang jian, xiu gai he shan chu zi ding yi pei fang.

## Yi Zhi Chi De Pei Fang Lei Xing

- `ufo:dimensional_assembly`
- `ufo:stellar_simulation`
- `ufo:universal_multiblock`
- `ufo:qmf_recipe` (lao ge shi, QMF reng ran zhi chi)

---

## DMA

### Lei Xing

```txt
ufo:dimensional_assembly
```

### Ji Ben Jie Gou

| Zi Duan | Lei Xing | Bi Xu | Shuo Ming |
|--------|----------|-------|----------|
| `item_inputs` | Array | Shi | Wu pin shu ru ji shu liang |
| `fluid_inputs` | Array | Fou | Liu ti shu ru ji shu liang, dan wei mB |
| `item_outputs` | Array | Fou | Wu pin shu chu |
| `fluid_outputs` | Array | Fou | Liu ti shu chu |
| `energy` | Integer | Shi | Zong AE neng liang xiao hao |
| `time` | Integer | Shi | Chu li shi jian, dan wei tick |

> Zhi shao yao you yi ge shu chu: `item_outputs` huo `fluid_outputs`.

### Shi Li

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

### Shi Yong Tag Zuo Wei Shu Ru

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

### Shan Chu

```js
ServerEvents.recipes(event => {
  event.remove({ id: 'ufo:dma/quantum_anomaly' })
  event.remove({ type: 'ufo:dimensional_assembly', output: 'ufo:quantum_anomaly' })
})
```

### Bei Zhu

- DMA pei fang shi wu xu xing zhuang de.
- DMA zui duo zhi chi 9 zhong wu pin shu ru.
- Coolant bu shu yu pei fang ding yi de yi bu fen.

---

## Stellar Nexus

### Lei Xing

```txt
ufo:stellar_simulation
```

### Ji Ben Jie Gou

| Zi Duan | Lei Xing | Bi Xu | Shuo Ming |
|--------|----------|-------|----------|
| `simulation_name` | String | Shi | Controller zhong xian shi de ming cheng |
| `item_inputs` | Array | Shi | Cong ME wang luo xiao hao de wu pin |
| `fluid_inputs` | Array | Fou | Cong ME wang luo xiao hao de liu ti |
| `item_outputs` | Array | Fou | AE2 GenericStack ge shi de wu pin shu chu |
| `fluid_outputs` | Array | Fou | AE2 GenericStack ge shi de liu ti shu chu |
| `energy` | Integer | Shi | Zong AE neng liang |
| `time` | Integer | Shi | Chi xu shi jian, dan wei tick |
| `cooling_level` | Integer | Shi | Re fu he deng ji, 0 dao 3 |
| `field_tier` | Integer | Shi | Zui di field generator deng ji |
| `fuel_fluid` | String | Fou | Ran liao liu ti id |
| `fuel_amount` | Integer | Fou | Ran liao shu liang, mB |
| `coolant_fluid` | String | Fou | Leng que ye liu ti id |
| `coolant_amount` | Integer | Fou | Leng que ye shu liang, mB |

### AE2 GenericStack

```json
{ "#": 15000000, "#t": "ae2:i", "id": "minecraft:iron_ingot" }
```

- `#` = shu liang
- `#t` = lei xing: `ae2:i` biao shi wu pin, `ae2:f` biao shi liu ti
- `id` = wan zheng registry id

### Shi Li

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

### Shan Chu

```js
ServerEvents.recipes(event => {
  event.remove({ id: 'ufo:stellar_simulation/iron_core_fusion' })
  event.remove({ type: 'ufo:stellar_simulation' })
})
```

---

## Universal Multiblock

Zhe shi qi ta da gui mo zi dong hua duo fang kuai ji qi tui jian shi yong de ge shi.

### Lei Xing

```txt
ufo:universal_multiblock
```

### Zhi Chi De Ji Qi

- `machine: 'qmf'`
- `machine: 'quantum_slicer'`
- `machine: 'quantum_processor_assembler'`
- `machine: 'quantum_cryoforge'`

### Ji Ben Jie Gou

| Zi Duan | Lei Xing | Bi Xu | Shuo Ming |
|--------|----------|-------|----------|
| `machine` | String | Shi | Mu biao duo fang kuai ji qi |
| `recipe_name` | String | Fou | Nei bu huo xian shi ming cheng |
| `item_inputs` | Array | Shi | Wu pin shu ru ji shu liang |
| `fluid_inputs` | Array | Fou | Liu ti shu ru ji shu liang, mB |
| `item_output` | Object | Fou | Dan ge wu pin shu chu |
| `fluid_output` | Object | Fou | Dan ge liu ti shu chu |
| `fluid_output_amount` | Integer | Fou | Liu ti shu chu zong liang, mB |
| `energy` | Integer/Long | Shi | Zong AE neng liang |
| `time` | Integer | Shi | Shi jian, dan wei tick |
| `required_tier` | Integer | Fou | Ji qi zui di deng ji |

> Zhi shao yao you yi ge shu chu: `item_output` huo `fluid_output`.

### Ge Shi

Wu pin shu ru:

```json
{
  "ingredient": { "item": "minecraft:diamond" },
  "amount": 64
}
```

Tag shu ru:

```json
{
  "ingredient": { "tag": "c:ingots/iron" },
  "amount": 256
}
```

Liu ti shu ru:

```json
{
  "fluid": {
    "id": "ufo:uu_matter",
    "amount": 1
  },
  "amount": 128000
}
```

Wu pin shu chu:

```json
{
  "id": "ufo:dimensional_processor",
  "count": 64
}
```

Liu ti shu chu:

```json
{
  "id": "ufo:source_stable_coolant",
  "amount": 1
}
```

Xu yao da pei:

```json
"fluid_output_amount": 128000
```

### Shi Li - QMF

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

### Shi Li - Quantum Processor Assembler

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

### Shi Li - Quantum Cryoforge

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

### Shi Li - Quantum Slicer

Mu qian Quantum Slicer hai mei you sheng cheng hao de datapack shi li, dan serializer yi jing ke yi yong tong yang de ge shi jie shou zi ding yi pei fang:

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

### Shan Chu

```js
ServerEvents.recipes(event => {
  event.remove({ id: 'ufo:universal/qmf/corporeal_matter_batch' })
  event.remove({ type: 'ufo:universal_multiblock' })
})
```

---

## QMF Lao Ge Shi Jian Rong

QMF reng ran zhi chi:

```txt
ufo:qmf_recipe
```

Zhe ge ge shi reng ran ke yong yu xiang rong lao nei rong, dan xin pei fang geng tui jian shi yong `ufo:universal_multiblock` bing pei he `machine: 'qmf'`.

### Shi Li

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

Ru guo ni xi huan yong datapack er bu shi KubeJS, qing ba JSON fang zai:

```txt
data/<your_namespace>/recipe/<recipe_name>.json
```

Shi yong yu shang mian shi li wan quan xiang tong de jie gou ji ke.

---

*Can kao: [DMA](dma.md) · [Quantum Matter Fabricator](quantum-matter-fabricator.md) · [Quantum Processor Assembler](quantum-processor-assembler.md) · [Stellar Nexus](stellar-nexus.md) · [Catalysts](catalysts.md) · [Materials](materials.md)*
