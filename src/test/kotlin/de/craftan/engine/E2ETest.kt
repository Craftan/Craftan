package de.craftan.engine

import de.craftan.engine.gameflow.GameFlowImpl
import de.craftan.engine.gameflow.action.CraftanGameActionEvent
import de.craftan.engine.gameflow.action.PlaceStructureAction
import de.craftan.engine.gameflow.action.PlaceStructureActionData
import de.craftan.engine.map.TileCoordinate
import de.craftan.engine.map.graph.CraftanDirection
import de.craftan.engine.map.graph.EdgeDirection
import de.craftan.engine.map.graph.NodeDirection
import de.craftan.engine.structures.Road
import de.craftan.engine.structures.Settlement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class E2ETest {

    @Test
    fun placeStructureTest() {
        val players = listOf(
            CraftanPlayer("Devin"),
            CraftanPlayer("David"),
            )

        val game = CraftanGameImpl(
            MutableCraftanGameConfig().toCraftanGameConfig(),
            GameFlowImpl(players)
        )

        game.start()
        assertInitialState(game)

        game.eventBus.fire(
            CraftanGameActionEvent(
                game,
                PlaceStructureActionData(
                    Settlement(),
                    TileCoordinate(0, 0),
                    NodeDirection.NORTH
                ),
                players.first(),
                PlaceStructureAction::class,
            )
        )
        game.eventBus.fire(
            CraftanGameActionEvent(
                game,
                PlaceStructureActionData(
                    Road(),
                    TileCoordinate(0, 0),
                    EdgeDirection.NORTH_EAST
                ),
                players.first(),
                PlaceStructureAction::class,
            )
        )
        val centerTile = game.stateHandler.state.map.tiles[TileCoordinate(0,0)]!!

        assertNotNull(centerTile.nodes[NodeDirection.NORTH]!!.structureInfo)
        assertNotNull(centerTile.nodes[NodeDirection.NORTH]!!.structureInfo!!.structure is Settlement)
        assertNotNull(centerTile.nodes[NodeDirection.NORTH]!!.structureInfo!!.owner == players.first())

        assertNotNull(centerTile.edges[EdgeDirection.NORTH_EAST]!!.structureInfo)
        assertNotNull(centerTile.edges[EdgeDirection.NORTH_EAST]!!.structureInfo!!.structure is Road)
        assertNotNull(centerTile.edges[EdgeDirection.NORTH_EAST]!!.structureInfo!!.owner == players.first())

        assertInitialState(game)
    }

    fun assertInitialState(game: CraftanGame) {
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