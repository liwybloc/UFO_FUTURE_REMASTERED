# 存储单元

UFO Future 扩展了 AE2 的存储系统，新增两个系列 — **白矮星**（物品）和 **中子星**（流体）— 使用 BigInteger 容量。还添加了**无限单元**用于无限存储单一资源。

---

## 白矮星物品单元

| 等级 | 容量 | 所需组件 | ID |
|------|------|---------|-----|
| **Echo** | 40M 字节 | Phase Shift 组件矩阵 | `ufo:white_dwarf_cell_echo` |
| **Beacon** | 100M 字节 | Hyper Dense 组件矩阵 | `ufo:white_dwarf_cell_beaco` |
| **Nexus** | 250M 字节 | Tesseract 组件矩阵 | `ufo:white_dwarf_cell_nexus` |
| **Core** | 750M 字节 | Event Horizon 组件矩阵 | `ufo:white_dwarf_cell_core` |
| **Singularity** | ∞ (MAX_INT) | Cosmic String 组件矩阵 | `ufo:white_dwarf_cell_singularity` |

**外壳**: `ufo:white_dwarf_item_cell_housing`

---

## 中子星流体单元

| 等级 | 容量 | 所需组件 | ID |
|------|------|---------|-----|
| **Echo** | 40M 字节 | Phase Shift 组件矩阵 | `ufo:neutron_star_reservoir_echo` |
| **Beacon** | 100M 字节 | Hyper Dense 组件矩阵 | `ufo:neutron_star_reservoir_beaco` |
| **Nexus** | 250M 字节 | Tesseract 组件矩阵 | `ufo:neutron_star_reservoir_nexus` |
| **Core** | 750M 字节 | Event Horizon 组件矩阵 | `ufo:neutron_star_reservoir_core` |
| **Singularity** | ∞ (MAX_INT) | Cosmic String 组件矩阵 | `ufo:neutron_star_reservoir_singularity` |

**外壳**: `ufo:neutron_fluid_cell_housing`

---

## 无限单元

存储**无限**数量的单一资源。包括：水、岩浆、圆石、沙子、黑曜石、玻璃等，以及 16 色染料。

### 合成（DMA 配方）
- **输入**: Cosmic String 组件矩阵 ×1, 量子异常 ×1, 目标资源 ×64
- **流体**: 2,500 mB 超越物质
- **能量**: 250,000,000 AE · **时间**: 10,000 tick (8.3 分钟)

---

*另见: [DMA](dma.md) · [进阶](progression.md) · [材料](materials.md)*
