package de.craftan.engine

import de.craftan.engine.gameflow.GameFlowImpl
import de.craftan.engine.gameflow.action.CraftanGameActionEvent
import de.craftan.engine.gameflow.action.PlaceStructureAction
import de.craftan.engine.gameflow.action.PlaceStructureActionData
import de.craftan.engine.gameflow.action.RollDiceAction
import de.craftan.engine.gameflow.action.RollDiceActionData
import de.craftan.engine.gameflow.flows.NormalTurnFlow
import de.craftan.engine.gameflow.turnstates.NormalTurnState
import de.craftan.engine.gameflow.turnstates.RollDiceState
import de.craftan.engine.map.DiceNumber
import de.craftan.engine.map.TileCoordinate
import de.craftan.engine.map.graph.EdgeDirection
import de.craftan.engine.map.graph.NodeDirection
import de.craftan.engine.structures.Road
import de.craftan.engine.structures.Settlement
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import kotlin.reflect.KFunction3
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

        assert(game.gameFlow.round.currentFlow().state is RollDiceState)
        mockDiceRoll(game, 7)
        game.eventBus.fire(
            CraftanGameActionEvent(
                game,
                RollDiceActionData(),
                players.first(),
                RollDiceAction::class,
            )
        )
        assert(game.gameFlow.round.currentFlow() is NormalTurnFlow)
        assert(game.gameFlow.round.currentFlow().state is NormalTurnState)
        game.eventBus.fire(
            CraftanGameEndTurnEvent()
        )

        assert(game.gameFlow.round.currentFlow().state is RollDiceState)
        mockDiceRoll(game, 7)
        game.eventBus.fire(
            CraftanGameActionEvent(
                game,
                RollDiceActionData(),
                players.last(),
                RollDiceAction::class,
            )
        )
        assert(game.gameFlow.round.currentFlow() is NormalTurnFlow)
        assert(game.gameFlow.round.currentFlow().state is NormalTurnState)
        game.eventBus.fire(
            CraftanGameEndTurnEvent()
        )
    }

    fun assertInitialStructurePlacement() {
        for ((index, player) in players.withIndex()) {
            println("Index " + index + " Player " + player.name)
            val coords = TileCoordinate(index+1, -index-1)
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
            assertInitialResourceState()
        }
        for ((index, player) in players.reversed().withIndex()) {
            println("Index " + index + " Player " + player.name)
            val coords = TileCoordinate(-index-1, index+1)
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
            assertInitialResourceState()
        }
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

fun mockDiceRoll(
    game: CraftanGameImpl,
    value: Int
) {
    val currentFlow = game.gameFlow.round.currentFlow()
    val rollAction = currentFlow.state.actions.first { it is RollDiceAction } as RollDiceAction
    val actionSpy = spy(rollAction)

    // Only mock the dice roll, let the real invoke() run
    doReturn(value).whenever(actionSpy).rollDice()

    // Replace the action in state using reflection as before
    val actionsField = currentFlow.state::class.java.getDeclaredField("actions")
    actionsField.isAccessible = true
    val mockedActions = currentFlow.state.actions.map { if (it is RollDiceAction) actionSpy else it }
    actionsField.set(currentFlow.state, mockedActions)
}