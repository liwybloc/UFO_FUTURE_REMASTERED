# UFO Ponder Prototype

This branch contains the first client-only prototype for an in-house UFO Future tutorial system inspired by Ponder, without depending on Create or `net.createmod.ponder`.

## Goals

- Reuse existing UFO multiblock definitions instead of duplicating structure data.
- Keep the first version client-only and non-authoritative.
- Replace LDLib previews gradually only after the new screen is better in practice.
- Make future addon/KubeJS-style exposure possible without designing that scripting layer first.

## Current MVP

- `com.raishxn.ufo.api.tutorial` defines tutorial entries, scenes, steps, and the registry.
- `com.raishxn.ufo.client.tutorial.UfoTutorials` creates default tutorials from `MultiblockControllerDefinitions`.
- `UfoTutorialScreen` renders a step-based multiblock preview with layer filtering, highlighted symbols, tooltips, and previous/next controls.
- The preview uses Minecraft block model rendering in a full-screen black scene with a right-side control panel and bottom timeline.
- The screen explicitly disables vanilla background blur/transparent backdrop rendering so the tutorial stays sharp instead of inheriting the previous menu blur.
- The bottom timeline now plays a mini construction timelapse for the visible step, with pause/play, speed changes, scrub seeking, elapsed/total time, and visible block count.
- The preview now has basic Ponder-style camera controls: mouse drag rotates, mouse wheel zooms, `Center` resets the view, and `<` / `>` rotate in fixed increments.
- The right panel supports structure filters for all blocks, hatches, or the controller. Hatch tooltips explain their role when hovered.
- The preview scales automatically for both compact structures like QMF and large structures like Stellar Nexus.
- QMF has a dedicated first tutorial instead of only the generic structure flow.
- Each step now surfaces a small material list for the currently highlighted parts.
- Universal multiblock controller screens and Stellar Nexus expose a `?` tutorial button.
- Looking at a supported controller and pressing the remappable `Open UFO Tutorial` key opens the same tutorial directly in-world.

## Controls

- `Back`: closes the tutorial.
- `U` by default while looking at a supported controller: opens the UFO tutorial screen. This is remappable in Controls.
- `Previous` / `Next`: changes the current tutorial step.
- `Down` / `Up`: cycles visible layers; cycling past the ends returns to the step default.
- `All` / `Hatches` / `Controller`: changes the visible block filter.
- `Center`: resets camera pitch, yaw, and zoom.
- `<` / `>` or Q / E: rotates the structure.
- Drag in the scene: free-rotates the structure.
- Mouse wheel in the scene: zooms the structure.
- Left / right or A / D: previous / next step.
- Up / down or W / S: layer up / layer down.
- Mouse wheel over the right panel: layer up / layer down.
- `Pause` / `Play` or Space: pauses/resumes the construction timelapse.
- `1x` / `2x` / `4x` / `8x`: cycles the timelapse speed.
- Clicking the bottom timeline scrubs the current construction timelapse.
- Shift-clicking the bottom timeline jumps to a tutorial step marker.

## Next Iterations

- Add timeline actions: show section, hide section, pulse highlight, camera move, item/fluid/energy callouts.
- Add dedicated scenes for machine behavior, not only structure assembly.
- Add a stable public API for addons to register tutorials.
- Consider a script layer only after the Java API is stable.

## Guardrails

- Tutorial code must not mutate the world or send gameplay packets.
- Structure render data should be cached per entry or per step.
- Server logic, recipe execution, and multiblock validation remain authoritative outside this screen.
