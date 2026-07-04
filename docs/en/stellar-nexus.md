# Stellar Nexus

The **Stellar Nexus** is the final simulation multiblock in UFO Future. It consumes immense AE power, rare fluids and high-tier materials to produce extreme-scale outputs over long cycle times.

## Machine Identity

- Massive assembled multiblock
- Reads items and fluids from the ME network
- Charges a **200B AE** internal buffer while idle
- Consumes fuel on start and coolant during runtime
- Uses heat, safe mode and overclock as core balancing mechanics
- Requires exactly one item input hatch, one item output hatch, one fluid output hatch and one AE energy input hatch

## Field Generator Tiers

The four field positions must all be the same tier:

- **MK1**
- **MK2**
- **MK3**

Mixed field tiers invalidate the structure. The field tier decides recipe access and affects charging and cooling performance.

## Coolant Ladder

- **Gelid Cryotheum** = low efficiency
- **Stable Coolant** = medium efficiency
- **Temporal Fluid** = extreme efficiency

The intended setup is to climb this ladder instead of brute-forcing the machine with weak coolant forever.

## Safe Mode

Safe Mode is the reliable automation option.

- **2.5x** AE cost
- **2.5x** fuel use
- **2.5x** coolant use
- Automatic shutdown instead of detonation at maximum heat

## Overclock Mode

- **5x faster** recipe completion
- **10x** AE cost
- **5x** fuel use
- **5x** heat generation
- **5x** coolant use

This behavior also applies to custom Stellar Nexus recipes, because it is implemented in machine logic.

## Catastrophic Explosion

If Safe Mode is disabled and the machine reaches full heat, the Stellar Nexus enters a real destruction sequence.

- The blast expands in shells instead of a single lag spike
- Real blocks are destroyed around the controller
- The inner core turns into lava
- The outer zone is ignited
- Nearby entities keep taking damage as the wave expands
