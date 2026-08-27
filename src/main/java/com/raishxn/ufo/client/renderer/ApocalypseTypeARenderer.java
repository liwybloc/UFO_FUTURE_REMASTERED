package com.raishxn.ufo.client.renderer;

import com.raishxn.ufo.client.model.ApocalypseTypeAModel;
import com.raishxn.ufo.entity.custom.ApocalypseTypeAEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.GeoEntityRenderer;

public class ApocalypseTypeARenderer extends GeoEntityRenderer<ApocalypseTypeAEntity, EntityRenderState> {
    public ApocalypseTypeARenderer(final EntityRendererProvider.Context context) {
        super(context, new ApocalypseTypeAModel());
        this.shadowRadius = 1.2F;
    }
}
