# UFO Future

UFO Future is an Applied Energistics 2 addon focused on large-scale storage,
advanced processing, stellar materials, multiblock machines, and endgame tools.

This repository is a community-maintained port and optimization effort based on
the original UFO Future project created by **Raishxn**. It is not the original
upstream repository and is not presented as Raishxn's own release. The current
maintainer is updating the mod for Minecraft 26.1.2, improving performance, removing some stolen / poorly made AI code, and
continuing development while preserving attribution to the original author.

## Target platform

- Minecraft 26.1.2
- NeoForge 26.1.2
- Java 25
- Applied Energistics 2
- AE2 Addon Lib
- GuideME
- GeckoLib

Optional integrations may include JEI, KubeJS, Applied Flux, and MEGA
Cells when compatible 26.1.2 builds are available.

## Features

- AE2-powered Dimensional Matter Assembler with recipes, catalysts, heat, and
  coolant management
- Universal processing multiblocks and Stellar Nexus progression
- Very-large-capacity item and fluid storage cells
- Stellar materials, custom fluids, containment systems, and catalysts
- RF-powered armor and transformable tools
- Recipe-viewer and scripting integrations
- English, Brazilian Portuguese, Spanish, and Simplified Chinese localization

## Building

Clone the repository and run:

```bash
./gradlew build
```

Gradle will provision the required Java 25 toolchain when toolchain downloads
are available. During the port, `compileJava` is the quickest task for checking
source migration progress:

```bash
./gradlew compileJava --no-configuration-cache
```

## Development status

The project is being migrated from Minecraft 1.21.1 to 26.1.2. Contributions
that fix help are welcome. Please keep changes focused.

I am also working on cleaning up the poorly written code and sloppiness of it.

## Attribution and licensing

UFO Future was originally created by **Raishxn**. This maintained version is a
modified work and includes porting, maintenance, and optimization changes by
the current maintainer and other contributors.

See [COPYING](COPYING) for the origin and redistribution notice and
[LICENSE.md](LICENSE.md) for the applicable code and asset licenses. The
NeoForge template notice remains in [TEMPLATE_LICENSE.txt](TEMPLATE_LICENSE.txt).

Third-party projects credited by the original project include:

- [Applied Energistics 2](https://github.com/AppliedEnergistics/Applied-Energistics-2)
- [AE2 Crystal Science](https://github.com/Frostbite-time/AE2-Crystal-Science)
- [GT New Horizons](https://github.com/GTNewHorizons/GT-New-Horizons-Modpack)
- [GregTech Odyssey](https://github.com/GregTech-Odyssey)
