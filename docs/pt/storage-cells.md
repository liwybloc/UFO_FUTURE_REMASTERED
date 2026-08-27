# Células de Armazenamento

UFO Future estende o sistema de armazenamento do AE2 com duas novas séries de células — **Anã Branca** (itens) e **Estrela de Nêutrons** (fluidos) — usando capacidades BigInteger muito além dos limites do AE2 vanilla. Também adiciona **Células Infinitas** para armazenamento ilimitado de um único recurso.

---

## Células de Itens Anã Branca

Células de armazenamento de itens de alta capacidade com contagem interna BigInteger. Todos os tiers usam o **Invólucro de Célula de Item Anã Branca**.

| Nome do Tier | Capacidade | Componente Necessário | ID de Registro |
|-------------|-----------|----------------------|---------------|
| **Echo** | 40M bytes | Matriz Phase Shift | `ufo:white_dwarf_cell_echo` |
| **Beacon** | 100M bytes | Matriz Hyper Dense | `ufo:white_dwarf_cell_beaco` |
| **Nexus** | 250M bytes | Matriz Tesseract | `ufo:white_dwarf_cell_nexus` |
| **Core** | 750M bytes | Matriz Event Horizon | `ufo:white_dwarf_cell_core` |
| **Singularity** | ∞ (MAX_INT) | Matriz Cosmic String | `ufo:white_dwarf_cell_singularity` |

### Fabricação
Cada célula: 1× Invólucro de Célula Anã Branca + 1× Matriz de Componente (receita sem forma).

**ID do Invólucro**: `ufo:white_dwarf_item_cell_housing`

---

## Células de Fluido Estrela de Nêutrons

| Nome do Tier | Capacidade | Componente Necessário | ID de Registro |
|-------------|-----------|----------------------|---------------|
| **Echo** | 40M bytes | Matriz Phase Shift | `ufo:neutron_star_reservoir_echo` |
| **Beacon** | 100M bytes | Matriz Hyper Dense | `ufo:neutron_star_reservoir_beaco` |
| **Nexus** | 250M bytes | Matriz Tesseract | `ufo:neutron_star_reservoir_nexus` |
| **Core** | 750M bytes | Matriz Event Horizon | `ufo:neutron_star_reservoir_core` |
| **Singularity** | ∞ (MAX_INT) | Matriz Cosmic String | `ufo:neutron_star_reservoir_singularity` |

**ID do Invólucro**: `ufo:neutron_fluid_cell_housing`

---

## Células Infinitas

Células de recurso único que armazenam quantidades **ilimitadas**. Perfeitas para automação em massa.

#### Recursos Vanilla

| Célula | ID de Registro |
|--------|---------------|
| Água | `ufo:infinity_water_cell` |
| Lava | `ufo:infinity_lava_cell` |
| Pedregulho | `ufo:infinity_cobblestone_cell` |
| Pedregulho de Ardósia | `ufo:infinity_cobbled_deepslate_cell` |
| Pedra do End | `ufo:infinity_end_stone_cell` |
| Netherrack | `ufo:infinity_netherrack_cell` |
| Areia | `ufo:infinity_sand_cell` |
| Obsidiana | `ufo:infinity_obsidian_cell` |
| Cascalho | `ufo:infinity_gravel_cell` |
| Tronco de Carvalho | `ufo:infinity_oak_log_cell` |
| Vidro | `ufo:infinity_glass_cell` |
| Fragmento de Ametista | `ufo:infinity_amethyst_shard_cell` |

#### AE2

| Célula | ID de Registro |
|--------|---------------|
| Pedra do Céu | `ufo:infinity_sky_stone_cell` |

#### Células de Corante (16 Cores)
Todas seguem o padrão: `ufo:infinity_<cor>_dye_cell`

### Fabricação (Receita DMA)
- **Entradas**: Matriz Cosmic String ×1, Anomalia Quântica ×1, Recurso ×64
- **Fluido**: 2.500 mB Matéria Transcendente
- **Energia**: 250.000.000 AE · **Tempo**: 10.000 ticks (8,3 min)

---

*Veja também: [DMA](dma.md) · [Progressão](progression.md) · [Materiais & Fluidos](materials.md)*
