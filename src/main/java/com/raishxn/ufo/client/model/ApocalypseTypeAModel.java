package com.raishxn.ufo.client.model;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.entity.custom.ApocalypseTypeAEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.DefaultedEntityGeoModel;

public final class ApocalypseTypeAModel extends DefaultedEntityGeoModel<ApocalypseTypeAEntity> {
    public ApocalypseTypeAModel() {
        super(UfoMod.id("apocalypse_type_a"));
        withAltTexture(UfoMod.id("apocalypse_type_a"));
    }
}
