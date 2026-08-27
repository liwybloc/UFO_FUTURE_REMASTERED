package com.raishxn.ufo.fluid;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.function.Consumer;

/**
 * Classe base utilitária para facilitar a criação de tipos de fluidos com texturas e cores personalizadas no NeoForge.
 */
public class BaseFluidType extends FluidType {
    private final Identifier stillTexture;
    private final Identifier flowingTexture;
    private final Identifier overlayTexture;
    private final int tintColor;
    private final Vector3f fogColor;

    /**
     * Construtor padrão para definir todas as propriedades visuais e físicas de uma vez.
     *
     * @param stillTexture   Localização da textura do fluido quando parado (obrigatório).
     * @param flowingTexture Localização da textura do fluido quando escorrendo (obrigatório).
     * @param overlayTexture Localização da textura de sobreposição ao entrar no fluido (opcional, pode ser null).
     * @param tintColor      Cor de tingimento em formato ARGB (ex: 0xFFFFFFFF para não tingir).
     * @param fogColor       Vetor RGB para a cor da neblina quando o jogador está submerso.
     * @param properties     As propriedades físicas padrão do FluidType (densidade, viscosidade, sons, etc).
     */
    public BaseFluidType(final Identifier stillTexture, final Identifier flowingTexture, final Identifier overlayTexture,
                         final int tintColor, final Vector3f fogColor, final Properties properties) {
        super(properties);
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.overlayTexture = overlayTexture;
        this.tintColor = tintColor;
        this.fogColor = fogColor;
    }

    public Identifier getStillTexture() {
        return stillTexture;
    }

    public Identifier getFlowingTexture() {
        return flowingTexture;
    }

    public int getTintColor() {
        return tintColor;
    }

    public Identifier getOverlayTexture() {
        return overlayTexture;
    }

    public Vector3f getFogColor() {
        return fogColor;
    }

    public void initializeClient(final Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            public @NotNull Identifier getStillTexture() {
                return stillTexture;
            }

            public @NotNull Identifier getFlowingTexture() {
                return flowingTexture;
            }

            public @Nullable Identifier getOverlayTexture() {
                return overlayTexture;
            }

            public @NotNull Identifier getStillTexture(final FluidStack stack) {
                return stillTexture;
            }

            public @NotNull Identifier getFlowingTexture(final FluidStack stack) {
                return flowingTexture;
            }

            public @Nullable Identifier getOverlayTexture(final FluidStack stack) {
                return overlayTexture;
            }

            public int getTintColor() {
                return tintColor;
            }

            public int getTintColor(final FluidStack stack) {
                return tintColor;
            }
        });
    }
}