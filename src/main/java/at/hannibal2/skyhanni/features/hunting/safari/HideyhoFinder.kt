package at.hannibal2.skyhanni.features.hunting.safari

// the package didn't autogenerate for my UniqueCritterTracker.kt file when i made it, do i just use the exact same package as this file?
// also is there a way to easily check for needed imports in IntelliJ IDEA ?
import at.hannibal2.skyhanni.SkyHanniMod
import at.hannibal2.skyhanni.api.event.HandleEvent
import at.hannibal2.skyhanni.data.IslandGraphs
import at.hannibal2.skyhanni.data.IslandType
import at.hannibal2.skyhanni.data.model.graph.GraphNodeTag
import at.hannibal2.skyhanni.events.chat.SkyHanniChatEvent
import at.hannibal2.skyhanni.features.misc.pathfind.NavigateAllApi
import at.hannibal2.skyhanni.features.misc.pathfind.NavigationCondition
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.ChatUtils
import at.hannibal2.skyhanni.utils.DelayedRun
import at.hannibal2.skyhanni.utils.EntityUtils.canBeSeen
import at.hannibal2.skyhanni.utils.EntityUtils.getEntitiesNearby
import at.hannibal2.skyhanni.utils.EntityUtils.getSkinTexture
import at.hannibal2.skyhanni.utils.LocationUtils
import at.hannibal2.skyhanni.utils.LorenzVec
import at.hannibal2.skyhanni.utils.RegexUtils.matches
import at.hannibal2.skyhanni.utils.SkullTextureHolder
import at.hannibal2.skyhanni.utils.getLorenzVec
import at.hannibal2.skyhanni.utils.repopatterns.RepoPattern
import net.minecraft.client.player.RemotePlayer
import kotlin.time.Duration.Companion.seconds

@SkyHanniModule
object HideyhoFinder {


    //ok so get the config stuff?
    private val config get() = SkyHanniMod.feature.hunting.safari

    // it's a computed getter, it re-fetches SkyHanniMod.feature.hunting.safari every time "config" is read in this file i think


    // what does RepoPattern mean? what is a patternGroup?
    private val patternGroup = RepoPattern.group("hunting.safari.hideyho-finder")
    // its sh's pattern system, RepoPattern.group makes a named group of patterns (allows these patterns to be controlled/hotfixed from the external skyhanni repo, so if the chat messages change it wont require a whole new mod release)



    // is this what the chat message has to match? so i'd do something like val hauntedPattern by patternGroup.pattern... ??
    private val startPattern by patternGroup.pattern(
        "first-found", "\\[MOB] Hideyho: Hehe, you found me!",
    )
    // first arg is the key, second is the regex string, patternGroup.pattern(..., ...) registers one named pattern onto the group?
    // 'by' - lazy delegated properrty? the pattern is built once on first access?  so like = but instead of defining it at the start, it's defined when it's accessed for the first time?

    private val beginHidingPattern by patternGroup.pattern(
        "begin-hiding", "\\[MOB] Hideyho: No peeking!",
    )

    private val endPattern by patternGroup.pattern(
        "found-after-hiding", "\\[MOB] Hideyho: Aah! You found me!",
    )

    private val SKIN_TEXTURE by SkullTextureHolder.texture("HIDEYHO")

    private var currentlyNavigating = false
    private var startLocation: LorenzVec? = null
    private var reportBug = false

    //i'm assuming i'll need this exact line in my unique critter tracker feature    @HandleEvent(onlyOnIsland = IslandType.SAFARI)
    @HandleEvent(onlyOnIsland = IslandType.SAFARI)
    private fun onChat(event: SkyHanniChatEvent.Allow) {
        // so every time there's a chat event this function runs?   yeah pretty much,
        if (!config.hideyhoFinder) return

        val playerLocation = LocationUtils.playerLocation()

        if (startPattern.matches(event.cleanMessage)) {
            startLocation = playerLocation.nearbyLocation(10.0)
            return
        }

        if (endPattern.matches(event.cleanMessage)) {
            if (!reportBug) return

            val playerLocation = playerLocation
            val nearbyHideyho = playerLocation.nearbyLocation(10.0)

            IslandGraphs.reportLocation(
                playerLocation,
                userFacingReason = "unknown hideyho location",
                technicalInfo = "user found a hideyho while far from known hideyho locations",
                "nearbyHideyho" to nearbyHideyho,
            )

            reportBug = false
        }

        if (beginHidingPattern.matches(event.cleanMessage)) {
            // Wait for Hideyho to teleport first
            DelayedRun.runDelayed(2.seconds) {
                beginNavigation()
            }
        }
    }

    // TODO in future once NavigateAllApi has the technology we can skip nodes that don't have a hideyho after doing a sight check
    private fun beginNavigation() {
        val startLocation = startLocation ?: return
        val graph = IslandGraphs.currentIslandGraph ?: return
        val locations = graph.getNodesWithTags(GraphNodeTag.HIDEYHO_LOCATION).toMutableList()
        val current = locations.minByOrNull { it.position.distance(startLocation) }

        locations.remove(current)
        if (locations.isEmpty()) return

        currentlyNavigating = true
        NavigateAllApi.navigateAll(
            locations,
            GraphNodeTag.HIDEYHO_LOCATION.displayName,
            GraphNodeTag.HIDEYHO_LOCATION.color.toColor(),
            onFinish = {
                // If we finish going to all locations but do not find a Hideyho then we have a new location, so we get users to report
                // Or maybe someone else already found one in your lobby
                // TODO is there a lootshare message we can use to end navigation early in this case
                ChatUtils.chat("Could not find any hidden Hideyho, maybe someone else already found it.")
                reportBug = true
                currentlyNavigating = false
                NavigateAllApi.handleStop()
            },
            continueNavigationCondition = NavigationCondition.SecondPassed { node ->
                val isNearby = node.position.nearbyLocation(5.0) != null

                if (isNearby) {
                    finishNavigation()
                }

                // Always return true as we either go to next or stop navigating
                return@SecondPassed true
            },
            condition = { config.hideyhoFinder && currentlyNavigating },
        )
    }

    private fun finishNavigation() {
        ChatUtils.chat("§aFound Hideyho!")
        currentlyNavigating = false
        NavigateAllApi.handleStop()
    }

    private fun LorenzVec.nearbyLocation(radius: Double): LorenzVec? {
        val nearbyEntities = this.getEntitiesNearby<RemotePlayer>(radius)
        return nearbyEntities.firstOrNull {
            it.getSkinTexture() == SKIN_TEXTURE && it.canBeSeen()
        }?.getLorenzVec()
    }

    @HandleEvent
    private fun onIslandLeave() {
        currentlyNavigating = false
        reportBug = false
    }

}
