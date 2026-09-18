package at.hannibal2.skyhanni.config.features.foraging

import at.hannibal2.skyhanni.config.FeatureToggle
import at.hannibal2.skyhanni.config.core.config.Position
import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import io.github.notenoughupdates.moulconfig.annotations.ConfigLink


class SafariConfig {

    @Expose
    @ConfigOption(
        name = "Names in Center",
        desc = "Shows the names of the 4 areas while in the center of the Critter Safari.",
    )
    @ConfigEditorBoolean
    @FeatureToggle
    var namesInCenter: Boolean = false

    @Expose
    @ConfigOption(
        name = "Hideyho Finder",
        desc = "Helps you find where Hideyho is hiding.",
    )
    @ConfigEditorBoolean
    @FeatureToggle
    var hideyhoFinder: Boolean = true

    // this is the 'wiring' for the config, not the actual feature just yet. ig i could still start here?
    // @Expose means it saves to the config file
    // @ConfigOption is from 'MoulConfig' - wtf is moulconfig? oh its some library for minecraft mods
    // for the config menu and stuff (which is why most of the config menus for mods like sh, skyblocker, etc look the same)
    // @ConfigEditorBoolean - also from moulconfig, it's saying this is an on/off toggle - wait no thats the @FeatureToggle, this is just saying to render it as a checkbox on/off toggle
    // @FeatureToggle - from skyhanni, this marks the feature as an on/off toggle (different from telling the GUI to render a on/off switch)


    @Expose
    @ConfigOption(
        name = "Unique Critter Tracker",
        desc = "Helps track which unique critters you've captured in a safari zone.",
    )
    @ConfigEditorBoolean
    @FeatureToggle
    var uniqueCritterTracker: Boolean = true

    @Expose
    @ConfigLink(owner = SafariConfig::class, field = "uniqueCritterTracker")
    val uniqueCritterDisplayPosition: Position = Position(430, 260)     // I took the lassoDisplayPosition and did +50 to both values
    // so now i need a position for this - example of how its done for the lasso hud
    // from config/features/hunting/HuntingConfig.kt:37-44
    /*
        @Expose
        @ConfigOption(name = "Lasso Display", desc = "Displays your lasso progress on screen.")
        @ConfigEditorBoolean
        @FeatureToggle
        var lassoDisplay = true

        @Expose         # why second expose here? - it's a second field, so the toggle and the screen position are two different fields, and need to be saved to the config file independently
                            #HuntingConfig here bc it's in HuntingConfig.kt, what does the "::" mean? - it's Kotlin's "class literal" (so a reference to the class object of HuntingConfig.class itself?) so it's basically just how you write HuntingConfig.class in kotlin
        @ConfigLink(owner = HuntingConfig::class, field = "lassoDisplay")       # field = "lassoDisplay" - means its for the lassoDisplay var - it's an "annotation argument" (the @ sign) so it has to be a compile-time constant (references to runtime objects cant be in annotations)
        # why val here, but var for lassoDisplay above?
            # wait how tf do types work in java again? is this saying we're making an object named "lassoDisplayPosition" of type "val"?
            # no, it's kotlin for wheter the reference can be reassigned. var means code can reassign it, val means its basically a static value?
            # eh not really static value. it's a read-only reference, but the object itself can be mutated (ex. changing Position's x and y coords when dragging the hud element around). so it's like a static handle that points to an object that can be changed and stuff
        val lassoDisplayPosition: Position = Position(380, 210)


     */

}
