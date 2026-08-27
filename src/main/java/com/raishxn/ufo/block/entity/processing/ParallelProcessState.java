package com.raishxn.ufo.block.entity.processing;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import com.mojang.serialization.Codec;

public class ParallelProcessState {
    private Identifier recipeId;
    private long energyBuffer;
    private long[] itemBuffers = new long[0];
    private long[] fluidBuffers = new long[0];
    private long[] chemicalBuffers = new long[0];
    private int progress;
    private boolean patternPushed;

    public Identifier getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(final Identifier recipeId) {
        this.recipeId = recipeId;
    }

    public boolean isActive() {
        return this.recipeId != null;
    }

    public void clear() {
        this.recipeId = null;
        this.energyBuffer = 0L;
        this.itemBuffers = new long[0];
        this.fluidBuffers = new long[0];
        this.chemicalBuffers = new long[0];
        this.progress = 0;
        this.patternPushed = false;
    }

    public void resizeBuffers(final int itemSize, final int fluidSize, final int chemicalSize) {
        if (this.itemBuffers.length != itemSize) {
            this.itemBuffers = new long[itemSize];
        }
        if (this.fluidBuffers.length != fluidSize) {
            this.fluidBuffers = new long[fluidSize];
        }
        if (this.chemicalBuffers.length != chemicalSize) {
            this.chemicalBuffers = new long[chemicalSize];
        }
    }

    public void clearBuffers() {
        java.util.Arrays.fill(this.itemBuffers, 0L);
        java.util.Arrays.fill(this.fluidBuffers, 0L);
        java.util.Arrays.fill(this.chemicalBuffers, 0L);
    }

    public long getEnergyBuffer() {
        return energyBuffer;
    }

    public void setEnergyBuffer(final long energyBuffer) {
        this.energyBuffer = energyBuffer;
    }

    public long[] getItemBuffers() {
        return itemBuffers;
    }

    public long[] getFluidBuffers() {
        return fluidBuffers;
    }

    public long[] getChemicalBuffers() {
        return chemicalBuffers;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(final int progress) {
        this.progress = progress;
    }

    public boolean isPatternPushed() {
        return patternPushed;
    }

    public void setPatternPushed(final boolean patternPushed) {
        this.patternPushed = patternPushed;
    }

    public boolean hasBufferedWork() {
        if (this.energyBuffer > 0L || this.progress > 0) {
            return true;
        }

        for (final long amount : this.itemBuffers) {
            if (amount > 0L) {
                return true;
            }
        }

        for (final long amount : this.fluidBuffers) {
            if (amount > 0L) {
                return true;
            }
        }

        for (final long amount : this.chemicalBuffers) {
            if (amount > 0L) {
                return true;
            }
        }

        return false;
    }

    public void save(final ValueOutput tag) {
        if (this.recipeId != null) {
            tag.putString("recipeId", this.recipeId.toString());
        }
        tag.putLong("energyBuffer", this.energyBuffer);
        writeLongArray(tag, "itemBuffers", this.itemBuffers);
        writeLongArray(tag, "fluidBuffers", this.fluidBuffers);
        writeLongArray(tag, "chemicalBuffers", this.chemicalBuffers);
        tag.putInt("progress", this.progress);
        tag.putBoolean("patternPushed", this.patternPushed);
    }

    public void load(final ValueInput tag) {
        final String savedRecipeId = tag.getStringOr("recipeId", "");
        this.recipeId = savedRecipeId.isEmpty() ? null : Identifier.parse(savedRecipeId);
        this.energyBuffer = tag.getLongOr("energyBuffer", 0L);
        this.itemBuffers = readLongArray(tag, "itemBuffers");
        this.fluidBuffers = readLongArray(tag, "fluidBuffers");
        this.chemicalBuffers = readLongArray(tag, "chemicalBuffers");
        this.progress = tag.getIntOr("progress", 0);
        this.patternPushed = tag.getBooleanOr("patternPushed", false);
    }

    private static void writeLongArray(final ValueOutput output, final String name, final long[] values) {
        final var list = output.list(name, Codec.LONG);
        for (final long value : values) list.add(value);
    }

    private static long[] readLongArray(final ValueInput input, final String name) {
        return input.listOrEmpty(name, Codec.LONG).stream().mapToLong(Long::longValue).toArray();
    }
}
