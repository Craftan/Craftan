package de.craftan.engine

import de.craftan.engine.gameflow.GameFlowImpl
import de.craftan.engine.gameflow.action.CraftanGameActionEvent
import de.craftan.engine.gameflow.action.PlaceStructureAction
import de.craftan.engine.gameflow.action.PlaceStructureActionData
import de.craftan.engine.map.TileCoordinate
import de.craftan.engine.map.graph.EdgeDirection
import de.craftan.engine.map.graph.NodeDirection
import de.craftan.engine.structures.Road
import de.craftan.engine.structures.Settlement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class E2ETest {

    val players = listOf(
        CraftanPlayer("Devin"),
        CraftanPlayer("David"),
    )

    val game = CraftanGameImpl(
        MutableCraftanGameConfig().toCraftanGameConfig(),
        GameFlowImpl(players)
    )

    @Test
    fun placeStructureTest() {
        game.start()
        assertInitialResourceState()        
        assertInitialStructurePlacement()
    }

    fun assertInitialStructurePlacement() {
        for ((index, player) in players.withIndex()) {
            print("Index " + index + " Player " + player.name + "\n")
            val coords = TileCoordinate(index, -index)
            game.eventBus.fire(
                CraftanGameActionEvent(
                    game,
                    PlaceStructureActionData(
                        Settlement(),
                        coords,
                        NodeDirection.NORTH
                    ),
                    player,
                    PlaceStructureAction::class,
                )
            )
            game.eventBus.fire(
                CraftanGameActionEvent(
                    game,
                    PlaceStructureActionData(
                        Road(),
                        coords,
                        EdgeDirection.NORTH_EAST
                    ),
                    player,
                    PlaceStructureAction::class,
                )
            )
            val centerTile = game.stateHandler.state.map.tiles[coords]!!

            assertNotNull(centerTile)
            assertNotNull(centerTile.nodes[NodeDirection.NORTH])
            assertNotNull(centerTile.nodes[NodeDirection.NORTH]!!.structureInfo)
            assertNotNull(centerTile.nodes[NodeDirection.NORTH]!!.structureInfo!!.structure is Settlement)
            assertNotNull(centerTile.nodes[NodeDirection.NORTH]!!.structureInfo!!.owner == player)

            assertNotNull(centerTile.edges[EdgeDirection.NORTH_EAST])
            assertNotNull(centerTile.edges[EdgeDirection.NORTH_EAST]!!.structureInfo)
            assertNotNull(centerTile.edges[EdgeDirection.NORTH_EAST]!!.structureInfo!!.structure is Road)
            assertNotNull(centerTile.edges[EdgeDirection.NORTH_EAST]!!.structureInfo!!.owner == player)
        }
        assertInitialResourceState()
    }

    fun assertInitialResourceState() {
        // Resources
        assertEquals(2, game.stateHandler.state.resources.keys.size)
        game.stateHandler.state.resources.forEach { (player, ressources) ->
            assertEquals(ressources.keys.size, 5)
            ressources.forEach { (resource, amount) -> assert(amount == 0) }
        }
        // Cards
        assertEquals(2, game.stateHandler.state.cards.keys.size)
        game.stateHandler.state.cards.forEach { (player, cards) ->
            assertEquals(cards.keys.size, 5)
            cards.forEach { (resource, amount) -> assert(amount == 0) }
        }
    }
}