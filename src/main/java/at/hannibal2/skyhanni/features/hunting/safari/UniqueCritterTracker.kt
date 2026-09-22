package at.hannibal2.skyhanni.features.hunting.safari

// copy all imports from hideyhofinder cuz the import needed detection thingy isnt working i think
import at.hannibal2.skyhanni.SkyHanniMod
import at.hannibal2.skyhanni.api.event.HandleEvent
import at.hannibal2.skyhanni.data.IslandGraphs
import at.hannibal2.skyhanni.data.IslandType
import at.hannibal2.skyhanni.data.model.graph.GraphNodeTag
import at.hannibal2.skyhanni.events.chat.SkyHanniChatEvent
//import at.hannibal2.skyhanni.features.misc.pathfind.NavigateAllApi
//import at.hannibal2.skyhanni.features.misc.pathfind.NavigationCondition
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.ChatUtils
import at.hannibal2.skyhanni.utils.DelayedRun
//import at.hannibal2.skyhanni.utils.EntityUtils.canBeSeen
//import at.hannibal2.skyhanni.utils.EntityUtils.getEntitiesNearby
//import at.hannibal2.skyhanni.utils.EntityUtils.getSkinTexture
import at.hannibal2.skyhanni.utils.LocationUtils
//import at.hannibal2.skyhanni.utils.LorenzVec
import at.hannibal2.skyhanni.utils.RegexUtils.matches
//import at.hannibal2.skyhanni.utils.SkullTextureHolder
//import at.hannibal2.skyhanni.utils.getLorenzVec
import at.hannibal2.skyhanni.utils.repopatterns.RepoPattern
import net.minecraft.client.player.RemotePlayer
import kotlin.time.Duration.Companion.seconds


@SkyHanniModule
object UniqueCritterTracker {
    private val config get() = SkyHanniMod.feature.hunting.safari

    // wait... the group name will be checked against the external SH REPO, right? so do i choose the name here, and then also add it to the external SH repo? I should ask Luna about this probably
    private val patternGroup = RepoPattern.group("hunting.safari.unique-critter-tracker")

    private val critterCatchPattern by patternGroup.pattern(
        "critter-caught", "CRITTER! i need the regex here"
    )

    private val captured = mutableSetOf<String>()
    // private var currentZone =        //uh come back to this ig

    // we need something like zoneToNamesMapping = {"Forest Biome": ["Foxtrot", "Bluebird", .....], "Cavern Biome": ["Cavernfish", ...], ...}  - whats the java equivalent of this?
    // and are all the critter names written out somewhere in this repo already? if yes, give me the file path to see it


    // wait, bc of text colors, using a Map might be a bad idea, cause i'll need two of them?? one with colors and one without colors?
    // suggested to used a data class instead:
    private data class Zone(
        val zoneNameRaw: String,    //i'll get this from SkyBlockUtils.graphArea ??
        val zoneNameColored: String,     // this one is for the hud
        val critters: List<String>,
    )

    private val zonesToCritters = listOf(
            Zone("Forest Biome", "§2Forest", listOf("Foxtrot", "Bluebird", "Honeybug", "Treefrog", "Woodchucker", "Fluffling", "Hideonfloor", "Parakeet", "Macaw")),
            Zone("Cavern Biome", "§6Cavern", listOf("Cavernfish", "Flitter", "Shyworm", "Driftling", "Chuckwalla", "Rockmite", "Scrappy", "Snoozle", "Gemzie")),
            Zone("Icy Biome", "§9Icy", listOf("Strongarm", "Tepid", "Polaris", "Shuddersquid", "Billygoat", "Mantis Shrimp", "Nozzlenose", "Troodon", "Wumpa")),
            Zone("Haunted Biome", "§5Haunted", listOf("Areita", "Bloodbat", "Duplico", "Gazer", "Litterbug", "Solsnatcher", "Gimmiegold", "Hideonwall", "Hideyho", "Doomspiral")),
        )



    @HandleEvent(onlyOnIsland = IslandType.SAFARI)
    private fun onChat(event: SkyHanniChatEvent.Allow){
        if(!config.uniqueCritterTracker) return

        if(critterCatchPattern.matches(event.cleanMessage)){

            // need to get the exact critter name, and append it to the set of caught critters
            // captured.add( whatever the critter name is )
            /*
            * Next step
Implement the feature body in UniqueCritterTracker.kt:
1. Replace the placeholder regex with the real CAPTURE! pattern (CAPTURE! You (?:caught an? |found (?:the )?)(?<critter>.+?) and (?:gained|as a reward (?:he|she|they|it) gave you) .+ Shard!) + #REGEX-TEST: lines above it (copy style from AttributeShardsData.kt:336).
2. Wire onChat to extract the critter name and captured.add(...) it.
3. Add the IslandJoinEvent reset (only when event.island == IslandType.SAFARI).
4. Add the HUD render via GuiOverlayRenderEvent / Renderable.vertical(...) at config.uniqueCritterDisplayPosition, with biome detection via SkyBlockUtils.graphArea.
5. Delete all scratch comments from all three files, then run .\gradlew.bat compileKotlin.*/
        }
    }
}
