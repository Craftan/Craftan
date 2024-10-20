package de.craftan.engine

enum class CraftanGameState {
    /**
     * Game is in lobby and waiting to be started
     */
    WAITING,

    /**
     * Before the actual game runs in its usual rounds, to determine the sequence of players
     */
    PRE_GAME,

    /**
     * Game is currently running
     */
    RUNNING,

    /**
     * Game is finished, either by a leaver, or somebody won
     */
    FINISHED,
}
