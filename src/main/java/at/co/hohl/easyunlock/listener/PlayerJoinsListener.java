package at.co.hohl.easyunlock.listener;

import at.co.hohl.easyunlock.EasyUnlock;
import at.co.hohl.easyunlock.storage.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA. User: hohl Date: 31.05.11 Time: 09:03 To change this template use File | Settings | File
 * Templates.
 */
public class PlayerJoinsListener extends PlayerListener {
    /** Plugin which holds the instance. */
    private final EasyUnlock plugin;

    /**
     * Creates a new listener, which listens to players which joins the game, and advertise them money, if they should
     * get some.
     *
     * @param plugin the plugin which holds the instance.
     */
    public PlayerJoinsListener(EasyUnlock plugin) {
        this.plugin = plugin;
    }

    /**
     * Called when a player joined the game.
     *
     * @param event the event which occurred.
     */
    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData data =
                plugin.getDatabase().find(PlayerData.class).where().ieq("name", player.getName()).findUnique();

        // Check if there is some you get granted when online.
        if (data != null && data.getGoldCredit() > 0) {
            player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, data.getGoldCredit()));

            player.sendMessage(ChatColor.GREEN + plugin.getConfiguration().getString("messages.thanks_to_advice1"));
            player.sendMessage(ChatColor.GREEN + String.format(plugin.getConfiguration()
                    .getString("messages.thanks_to_advice2"), data.getGoldCredit()));

            data.setGoldCredit(0);

            plugin.getDatabase().save(data);
        }

        // Check if there are user to unlock, when moderator comes online.
        if (plugin.getPermissionHandler().hasPermission(event.getPlayer(), EasyUnlock.UNLOCK_PERMISSION)) {
            HashMap<String, Object> argumentsToCheck = new HashMap<String, Object>();
            argumentsToCheck.put("rulesAccepted", true);
            argumentsToCheck.put("unlocked", false);

            int amountOfUsersToUnlock = plugin.getDatabase().find(PlayerData.class)
                    .where()
                    .allEq(argumentsToCheck)
                    .findRowCount();

            if (amountOfUsersToUnlock > 0) {
                player.sendMessage(ChatColor.GREEN + plugin.getConfiguration().getString("messages.players_to_unlock"));
            }
        }
    }
}
