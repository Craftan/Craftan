import de.craftan.engine.CraftanGame
import org.bukkit.entity.Player

class CraftanBridgePlayerImpl(
    override var bukkitPlayer: Player,
    override val game: CraftanGame? = null,
    override val inventory: CraftanPlayerInventory = CraftanPlayerInventory(),
    override val victoryPoints: Int = 0,
    override var team: CraftanTeam?,
    var left: Boolean = false
): CraftanBridgePlayer