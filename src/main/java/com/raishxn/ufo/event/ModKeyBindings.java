package com.raishxn.ufo.event;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import com.raishxn.ufo.UfoMod;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(UfoMod.id("ufo"));
    public static final String KEY_CATEGORY_UFO = "key.category.ufo";
    public static final String KEY_TOGGLE_AUTO_SMELT = "key.ufo.toggle_auto_smelt";
    public static final String KEY_OPEN_UFO_TUTORIAL = "key.ufo.open_tutorial";
    public static final KeyMapping CYCLE_TOOL_FORWARD = new KeyMapping(
            "key.ufo.cycle_tool_forward",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            CATEGORY
    );

    public static final KeyMapping CYCLE_TOOL_BACKWARD = new KeyMapping(
            "key.ufo.cycle_tool_backward",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            CATEGORY
    );

    public static final KeyMapping CYCLE_MODE = new KeyMapping(
            "key.ufo.cycle_mode",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            CATEGORY
    );
    public static final KeyMapping TOGGLE_AUTO_SMELT = new KeyMapping(KEY_TOGGLE_AUTO_SMELT,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, CATEGORY);
    public static final KeyMapping OPEN_UFO_TUTORIAL = new KeyMapping(KEY_OPEN_UFO_TUTORIAL,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_W, CATEGORY);
}
