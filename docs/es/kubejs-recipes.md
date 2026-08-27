# Recetas KubeJS

Esta pagina documenta como crear, modificar y eliminar recetas personalizadas via [KubeJS](https://kubejs.com/) para los principales multibloques de UFO Future en Minecraft 1.21.1 (NeoForge).

## Tipos de Receta Cubiertos

- `ufo:dimensional_assembly`
- `ufo:stellar_simulation`
- `ufo:universal_multiblock`
- `ufo:qmf_recipe` (legado, aun soportado por QMF)

---

## DMA

### Tipo

```txt
ufo:dimensional_assembly
```

### Estructura Basica

| Campo | Tipo | Obligatorio | Descripcion |
|-------|------|-------------|-------------|
| `item_inputs` | Array | Si | Ingredientes de item con cantidad |
| `fluid_inputs` | Array | No | Ingredientes de fluido con cantidad en mB |
| `item_outputs` | Array | No | Salidas de item |
| `fluid_outputs` | Array | No | Salidas de fluido |
| `energy` | Entero | Si | Energia AE total consumida |
| `time` | Entero | Si | Tiempo en ticks |

> Debe existir al menos una salida: `item_outputs` o `fluid_outputs`.

### Ejemplo

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

### Tags como Entrada

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

### Eliminacion

```js
ServerEvents.recipes(event => {
  event.remove({ id: 'ufo:dma/quantum_anomaly' })
  event.remove({ type: 'ufo:dimensional_assembly', output: 'ufo:quantum_anomaly' })
})
```

### Notas

- Las recetas del DMA son shapeless.
- El DMA admite hasta 9 entradas de item.
- El coolant no forma parte de la definicion de la receta.

---

## Stellar Nexus

### Tipo

```txt
ufo:stellar_simulation
```

### Estructura Basica

| Campo | Tipo | Obligatorio | Descripcion |
|-------|------|-------------|-------------|
| `simulation_name` | String | Si | Nombre mostrado en el controller |
| `item_inputs` | Array | Si | Items consumidos desde la red ME |
| `fluid_inputs` | Array | No | Fluidos consumidos desde la red ME |
| `item_outputs` | Array | No | Items producidos en formato AE2 GenericStack |
| `fluid_outputs` | Array | No | Fluidos producidos en formato AE2 GenericStack |
| `energy` | Entero | Si | Energia AE total |
| `time` | Entero | Si | Duracion en ticks |
| `cooling_level` | Entero | Si | Estres termico de 0 a 3 |
| `field_tier` | Entero | Si | Tier minimo del field generator |
| `fuel_fluid` | String | No | Fluido de combustible |
| `fuel_amount` | Entero | No | Cantidad de combustible en mB |
| `coolant_fluid` | String | No | Fluido de coolant |
| `coolant_amount` | Entero | No | Cantidad de coolant en mB |

### GenericStack de AE2

```json
{ "#": 15000000, "#t": "ae2:i", "id": "minecraft:iron_ingot" }
```

- `#` = cantidad
- `#t` = tipo: `ae2:i` para item, `ae2:f` para fluido
- `id` = registry id completo

### Ejemplo

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

### Eliminacion

```js
ServerEvents.recipes(event => {
  event.remove({ id: 'ufo:stellar_simulation/iron_core_fusion' })
  event.remove({ type: 'ufo:stellar_simulation' })
})
```

---

## Universal Multiblock

Este es el formato recomendado para los otros multibloques de automatizacion masiva.

### Tipo

```txt
ufo:universal_multiblock
```

### Maquinas Soportadas

- `machine: 'qmf'`
- `machine: 'quantum_slicer'`
- `machine: 'quantum_processor_assembler'`
- `machine: 'quantum_cryoforge'`

### Estructura Basica

| Campo | Tipo | Obligatorio | Descripcion |
|-------|------|-------------|-------------|
| `machine` | String | Si | Multibloque objetivo |
| `recipe_name` | String | No | Nombre interno/visible |
| `item_inputs` | Array | Si | Entradas de item con cantidad |
| `fluid_inputs` | Array | No | Entradas de fluido con cantidad en mB |
| `item_output` | Objeto | No | Salida unica de item |
| `fluid_output` | Objeto | No | Salida unica de fluido |
| `fluid_output_amount` | Entero | No | Cantidad total del fluido de salida en mB |
| `energy` | Entero/Long | Si | Energia AE total |
| `time` | Entero | Si | Tiempo en ticks |
| `required_tier` | Entero | No | Tier minimo de la maquina |

> Debe existir al menos una salida: `item_output` o `fluid_output`.

### Formatos

Entrada de item:

```json
{
  "ingredient": { "item": "minecraft:diamond" },
  "amount": 64
}
```

Entrada por tag:

```json
{
  "ingredient": { "tag": "c:ingots/iron" },
  "amount": 256
}
```

Entrada de fluido:

```json
{
  "fluid": {
    "id": "ufo:uu_matter",
    "amount": 1
  },
  "amount": 128000
}
```

Salida de item:

```json
{
  "id": "ufo:dimensional_processor",
  "count": 64
}
```

Salida de fluido:

```json
{
  "id": "ufo:source_stable_coolant",
  "amount": 1
}
```

Combinado con:

```json
"fluid_output_amount": 128000
```

### Ejemplo - QMF

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

### Ejemplo - Quantum Processor Assembler

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

### Ejemplo - Quantum Cryoforge

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

### Ejemplo - Quantum Slicer

Actualmente no hay ejemplos generados en datapack para Quantum Slicer, pero el serializer ya acepta recetas custom de la misma forma:

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

### Eliminacion

```js
ServerEvents.recipes(event => {
  event.remove({ id: 'ufo:universal/qmf/corporeal_matter_batch' })
  event.remove({ type: 'ufo:universal_multiblock' })
})
```

---

## Compatibilidad Legada de QMF

QMF tambien acepta:

```txt
ufo:qmf_recipe
```

Este formato sigue siendo valido por compatibilidad, pero para recetas nuevas conviene usar `ufo:universal_multiblock` con `machine: 'qmf'`.

### Ejemplo

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

Si prefieres datapacks en lugar de KubeJS, coloca los JSON en:

```txt
data/<tu_namespace>/recipe/<nombre_receta>.json
```

Usa exactamente la misma estructura mostrada en los ejemplos de arriba.

---

*Ver tambien: [DMA](dma.md) · [Quantum Matter Fabricator](quantum-matter-fabricator.md) · [Quantum Processor Assembler](quantum-processor-assembler.md) · [Stellar Nexus](stellar-nexus.md) · [Catalysts](catalysts.md) · [Materials](materials.md)*
