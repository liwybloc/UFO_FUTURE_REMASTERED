package com.raishxn.ufo.recipe;

import com.mojang.serialization.Codec;

public enum UniversalMultiblockMachineKind {
    QMF("qmf"),
    QUANTUM_SLICER("quantum_slicer"),
    QUANTUM_PROCESSOR_ASSEMBLER("quantum_processor_assembler"),
    QUANTUM_CRYOFORGE("quantum_cryoforge");

    public static final Codec<UniversalMultiblockMachineKind> CODEC =
            Codec.STRING.xmap(UniversalMultiblockMachineKind::fromSerializedName, UniversalMultiblockMachineKind::serializedName);

    private final String serializedName;

    UniversalMultiblockMachineKind(final String serializedName) {
        this.serializedName = serializedName;
    }

    public String serializedName() {
        return this.serializedName;
    }

    public static UniversalMultiblockMachineKind fromSerializedName(final String name) {
        for (final var value : values()) {
            if (value.serializedName.equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown multiblock machine kind: " + name);
    }
}
