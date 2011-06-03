package at.co.hohl.easyunlock.commands;

import at.co.hohl.easyunlock.EasyUnlock;
import at.co.hohl.easyunlock.storage.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Executor for the invited by command.
 *
 * @author Michael Hohl
 */
public class InvitedByCommand implements CommandExecutor {
    /** EasyUnlock plugin, which holds the instance. */
    private final EasyUnlock plugin;

    /**
     * Creates a new instance of the CommandExecutor.
     *
     * @param plugin the plugin which holds the instance.
     */
    public InvitedByCommand(EasyUnlock plugin) {
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
        if (plugin.getPermissionHandler().hasPermission(sender, EasyUnlock.INVITED_BY_PERMISSION)
                && sender instanceof Player) {
            Player player = (Player) sender;
            String playerName = player.getName();

            if (player.getName().equalsIgnoreCase(args[0])) {
                sender.sendMessage(
                        ChatColor.RED + plugin.getConfiguration().getString("messages.can_not_invited_by_yourself"));
                return true;
            }


            PlayerData data = plugin.getDatabase().find(PlayerData.class).where().ieq("name", playerName).findUnique();

            if (data == null) {
                data = new PlayerData();
                data.setName(playerName);
            }

            if (data.getInvitedBy() != null && data.getInvitedBy().length() > 0) {
                sender.sendMessage(ChatColor.RED + plugin.getConfiguration().getString("messages.only_one_adviser"));
            } else {
                if (args.length != 1) {
                    sender.sendMessage(ChatColor.RED +
                            plugin.getConfiguration().getString("messages.invalid_usage_invited_by_command"));
                    return true;
                }

                if (data.isUnlocked()) {
                    sender.sendMessage(
                            ChatColor.RED + plugin.getConfiguration().getString("messages.too_late_to_set_adviser"));
                    return true;
                }

                if (grantGold(plugin.getServer(), args[0])) {
                    data.setInvitedBy(args[0]);
                    sender.sendMessage(ChatColor.LIGHT_PURPLE +
                            plugin.getConfiguration().getString("messages.adviser_recognized"));
                } else {
                    sender.sendMessage(
                            ChatColor.RED + plugin.getConfiguration().getString("messages.adviser_do_not_exist"));
                }
            }

            plugin.getDatabase().save(data);

            return true;
        } else {
            return false;
        }
    }

    /**
     * Grants the player an amount of gold barrels.
     *
     * @param server server, the player plays on.
     * @param player the player to grant the gold.
     * @return true, if the player exists.
     */
    private boolean grantGold(Server server, String player) {
        PlayerData data = plugin.getDatabase().find(PlayerData.class).where().ieq("name", player).findUnique();

        if (data == null) {
            return false;
        }

        data.setAdvertised(data.getAdvertised() + 1);

        int amountToGet = Math.min(data.getAdvertised(), plugin.getConfiguration().getInt("max_grant_adviser", 7));

        Player onlinePlayer = server.getPlayer(player);

        if (onlinePlayer != null && onlinePlayer.isOnline()) {
            onlinePlayer.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, amountToGet));
            onlinePlayer.sendMessage(ChatColor.LIGHT_PURPLE + String.format(
                    plugin.getConfiguration().getString("messages.thanks_to_advice"), amountToGet));
        } else {
            data.setGoldCredit(data.getGoldCredit() + amountToGet);
        }

        plugin.getDatabase().save(data);

        return true;
    }
}
