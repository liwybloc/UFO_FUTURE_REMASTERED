# Receitas KubeJS

Esta pagina documenta como criar, modificar e remover receitas customizadas via [KubeJS](https://kubejs.com/) para os principais multiblocos do UFO Future em Minecraft 1.21.1 (NeoForge).

## Tipos de Receita Cobertos

- `ufo:dimensional_assembly`
- `ufo:stellar_simulation`
- `ufo:universal_multiblock`
- `ufo:qmf_recipe` (legado, ainda suportado pelo QMF)

---

## DMA

### Tipo

```txt
ufo:dimensional_assembly
```

### Estrutura Basica

| Campo | Tipo | Obrigatorio | Descricao |
|-------|------|-------------|-----------|
| `item_inputs` | Array | Sim | Ingredientes de item com quantidade |
| `fluid_inputs` | Array | Nao | Ingredientes de fluido com quantidade em mB |
| `item_outputs` | Array | Nao | Saidas de item |
| `fluid_outputs` | Array | Nao | Saidas de fluido |
| `energy` | Inteiro | Sim | Energia AE total consumida |
| `time` | Inteiro | Sim | Tempo em ticks |

> Pelo menos uma saida deve existir: `item_outputs` ou `fluid_outputs`.

### Exemplo

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

### Remocao

```js
ServerEvents.recipes(event => {
  event.remove({ id: 'ufo:dma/quantum_anomaly' })
  event.remove({ type: 'ufo:dimensional_assembly', output: 'ufo:quantum_anomaly' })
})
```

### Notas

- O DMA e sem forma.
- O DMA tem ate 9 entradas de item.
- O refrigerante nao faz parte da definicao da receita.

---

## Stellar Nexus

### Tipo

```txt
ufo:stellar_simulation
```

### Estrutura Basica

| Campo | Tipo | Obrigatorio | Descricao |
|-------|------|-------------|-----------|
| `simulation_name` | String | Sim | Nome exibido no controller |
| `item_inputs` | Array | Sim | Itens consumidos da ME |
| `fluid_inputs` | Array | Nao | Fluidos consumidos da ME |
| `item_outputs` | Array | Nao | Itens produzidos em formato AE2 GenericStack |
| `fluid_outputs` | Array | Nao | Fluidos produzidos em formato AE2 GenericStack |
| `energy` | Inteiro | Sim | Energia AE total |
| `time` | Inteiro | Sim | Duracao em ticks |
| `cooling_level` | Inteiro | Sim | Stress termico de 0 a 3 |
| `field_tier` | Inteiro | Sim | Tier minimo do field generator |
| `fuel_fluid` | String | Nao | Fluido de combustivel |
| `fuel_amount` | Inteiro | Nao | Quantidade de combustivel em mB |
| `coolant_fluid` | String | Nao | Fluido de coolant |
| `coolant_amount` | Inteiro | Nao | Quantidade de coolant em mB |

### GenericStack do AE2

```json
{ "#": 15000000, "#t": "ae2:i", "id": "minecraft:iron_ingot" }
```

- `#` = quantidade
- `#t` = tipo: `ae2:i` para item, `ae2:f` para fluido
- `id` = registry id completo

### Exemplo

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

### Remocao

```js
ServerEvents.recipes(event => {
  event.remove({ id: 'ufo:stellar_simulation/iron_core_fusion' })
  event.remove({ type: 'ufo:stellar_simulation' })
})
```

---

## Universal Multiblock

Esse e o formato recomendado para os outros multiblocos de automacao em massa.

### Tipo

```txt
ufo:universal_multiblock
```

### Maquinas Suportadas

- `machine: 'qmf'`
- `machine: 'quantum_slicer'`
- `machine: 'quantum_processor_assembler'`
- `machine: 'quantum_cryoforge'`

### Estrutura Basica

| Campo | Tipo | Obrigatorio | Descricao |
|-------|------|-------------|-----------|
| `machine` | String | Sim | Multibloco alvo |
| `recipe_name` | String | Nao | Nome interno/exibido |
| `item_inputs` | Array | Sim | Itens de entrada com quantidade |
| `fluid_inputs` | Array | Nao | Fluidos de entrada com quantidade em mB |
| `item_output` | Objeto | Nao | Saida unica de item |
| `fluid_output` | Objeto | Nao | Saida unica de fluido |
| `fluid_output_amount` | Inteiro | Nao | Quantidade total do fluido de saida em mB |
| `energy` | Inteiro/Long | Sim | Energia AE total |
| `time` | Inteiro | Sim | Tempo em ticks |
| `required_tier` | Inteiro | Nao | Tier minimo da maquina |

> Pelo menos uma saida deve existir: `item_output` ou `fluid_output`.

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

Saida de item:

```json
{
  "id": "ufo:dimensional_processor",
  "count": 64
}
```

Saida de fluido:

```json
{
  "id": "ufo:source_stable_coolant",
  "amount": 1
}
```

Combinado com:

```json
"fluid_output_amount": 128000
```

### Exemplo - QMF

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

### Exemplo - Quantum Processor Assembler

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

### Exemplo - Quantum Cryoforge

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

### Exemplo - Quantum Slicer

Atualmente nao ha exemplos gerados no datapack para o Quantum Slicer, mas o serializer ja aceita receitas customizadas do mesmo jeito:

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

### Remocao

```js
ServerEvents.recipes(event => {
  event.remove({ id: 'ufo:universal/qmf/corporeal_matter_batch' })
  event.remove({ type: 'ufo:universal_multiblock' })
})
```

---

## Compatibilidade Legada do QMF

O QMF ainda aceita:

```txt
ufo:qmf_recipe
```

Esse formato continua valido para compatibilidade, mas para receitas novas prefira `ufo:universal_multiblock` com `machine: 'qmf'`.

### Exemplo

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

Se voce preferir datapacks em vez de KubeJS, coloque os JSONs em:

```txt
data/<seu_namespace>/recipe/<nome_receita>.json
```

Use exatamente o mesmo formato mostrado nos exemplos acima.

---

*Veja tambem: [DMA](dma.md) · [Quantum Matter Fabricator](quantum-matter-fabricator.md) · [Quantum Processor Assembler](quantum-processor-assembler.md) · [Stellar Nexus](stellar-nexus.md) · [Catalysts](catalysts.md) · [Materials](materials.md)*
