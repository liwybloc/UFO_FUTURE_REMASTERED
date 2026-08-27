# Celdas de Almacenamiento

UFO Future extiende el sistema de almacenamiento de AE2 con dos nuevas series — **Enana Blanca** (ítems) y **Estrella de Neutrones** (fluidos) — con capacidades BigInteger. También añade **Celdas Infinitas** para almacenamiento ilimitado.

---

## Celdas de Ítems Enana Blanca

| Nivel | Capacidad | Componente | ID |
|-------|----------|-----------|-----|
| **Echo** | 40M bytes | Matriz Phase Shift | `ufo:white_dwarf_cell_echo` |
| **Beacon** | 100M bytes | Matriz Hyper Dense | `ufo:white_dwarf_cell_beaco` |
| **Nexus** | 250M bytes | Matriz Tesseract | `ufo:white_dwarf_cell_nexus` |
| **Core** | 750M bytes | Matriz Event Horizon | `ufo:white_dwarf_cell_core` |
| **Singularity** | ∞ (MAX_INT) | Matriz Cosmic String | `ufo:white_dwarf_cell_singularity` |

**Carcasa**: `ufo:white_dwarf_item_cell_housing`

---

## Celdas de Fluido Estrella de Neutrones

| Nivel | Capacidad | Componente | ID |
|-------|----------|-----------|-----|
| **Echo** | 40M bytes | Matriz Phase Shift | `ufo:neutron_star_reservoir_echo` |
| **Beacon** | 100M bytes | Matriz Hyper Dense | `ufo:neutron_star_reservoir_beaco` |
| **Nexus** | 250M bytes | Matriz Tesseract | `ufo:neutron_star_reservoir_nexus` |
| **Core** | 750M bytes | Matriz Event Horizon | `ufo:neutron_star_reservoir_core` |
| **Singularity** | ∞ (MAX_INT) | Matriz Cosmic String | `ufo:neutron_star_reservoir_singularity` |

**Carcasa**: `ufo:neutron_fluid_cell_housing`

---

## Celdas Infinitas

Almacenamiento **ilimitado** de un solo recurso. Incluye: Agua, Lava, Piedra, Arena, Obsidiana, Vidrio, y más. También incluye los 16 colores de tinte.

### Fabricación (DMA)
- **Entradas**: Matriz Cosmic String ×1, Anomalía Cuántica ×1, Recurso ×64
- **Fluido**: 2,500 mB Materia Trascendente
- **Energía**: 250,000,000 AE · **Tiempo**: 10,000 ticks (8.3 min)

---

*Ver también: [DMA](dma.md) · [Progresión](progression.md) · [Materiales](materials.md)*
