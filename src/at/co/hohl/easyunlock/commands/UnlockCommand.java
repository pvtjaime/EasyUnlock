package at.co.hohl.easyunlock.commands;

import at.co.hohl.easyunlock.EasyUnlock;
import at.co.hohl.easyunlock.storage.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * Executor for unlock command.
 *
 * @author Michael Hohl
 */
public class UnlockCommand implements CommandExecutor {
    /** Plugin which holds the instance. */
    private final EasyUnlock plugin;

    /**
     * Creates a new instance of the CommandExecutor.
     *
     * @param plugin the plugin which holds the instance.
     */
    public UnlockCommand(EasyUnlock plugin) {
        this.plugin = plugin;
    }

    /**
     * Called when the command is used by a player.
     *
     * @param sender  the sender of the command.
     * @param command the command itself.
     * @param label   label used for calling the command.
     * @param args    the arguments.
     * @return true, if the command gets executed.
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (plugin.getPermissionHandler().hasPermission(sender, EasyUnlock.UNLOCK_PERMISSION)) {
            if (args.length == 0) {
                HashMap<String, Object> argumentsToCheck = new HashMap<String, Object>();
                argumentsToCheck.put("rulesAccepted", Boolean.TRUE);
                argumentsToCheck.put("unlocked", Boolean.FALSE);

                List<PlayerData> usersToUnlock = plugin.getDatabase().find(PlayerData.class)
                        .where()
                        .allEq(argumentsToCheck)
                        .findList();

                sender.sendMessage(ChatColor.LIGHT_PURPLE + "= = = Player Accepted Rules & Need To Unlock = = =");
                for (PlayerData data : usersToUnlock) {
                    sender.sendMessage(String.format("%s (%s)", data.getName(), data.getAcceptedDated()));
                }

                return true;
            } else if (args.length == 1) {
                PlayerData data = plugin.getDatabase().find(PlayerData.class).where().ieq("name", args[0]).findUnique();

                if (data == null) {
                    data = new PlayerData();
                    data.setName(args[0]);
                }

                if (data.isRulesAccepted()) {
                    data.setUnlocked(true);
                    sender.sendMessage(
                            ChatColor.GREEN + plugin.getConfiguration().getString("messages.player_unlocked"));

                    Player player = sender.getServer().getPlayer(args[0]);
                    if (player != null) {
                        player.sendMessage(ChatColor.GREEN + plugin.getConfiguration().getString("messages.unlocked"));
                    }

                    plugin.getLogger().info(String.format("Player %s unlocked.", args[0]));
                } else {
                    sender.sendMessage(
                            ChatColor.RED + plugin.getConfiguration().getString("messages.not_accepted_rules"));
                }

                plugin.getDatabase().save(data);
            } else {
                sender.sendMessage(ChatColor.RED +
                        plugin.getConfiguration().getString("messages.invalid_usage_invited_by_command"));
            }

            return true;
        } else {
            return false;
        }
    }
}
