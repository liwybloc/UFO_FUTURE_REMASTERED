package com.raishxn.ufo.fluid;

import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidType;
import org.joml.Vector3f;

/**
 * Classe base utilitária para facilitar a criação de tipos de fluidos com texturas e cores personalizadas no NeoForge.
 */
public final class BaseFluidType extends FluidType {
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

}
