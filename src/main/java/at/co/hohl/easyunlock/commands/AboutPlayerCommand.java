package at.co.hohl.easyunlock.commands;

import at.co.hohl.easyunlock.EasyUnlock;
import at.co.hohl.easyunlock.storage.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Command to get information about player.
 *
 * @author Michael Hohl
 */
public class AboutPlayerCommand implements CommandExecutor {
    /** Plugin which holds the instance. */
    private final EasyUnlock plugin;

    /**
     * Creates a new instance of the AcceptCommand.
     *
     * @param plugin the plugin which holds the instance.
     */
    public AboutPlayerCommand(EasyUnlock plugin) {
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
        if (plugin.getPermissionHandler().hasPermission(sender, EasyUnlock.ABOUT_PLAYER_PERMISSION)) {
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "You need to pass a player name!");
                return true;
            }

            PlayerData data = plugin.getDatabase().find(PlayerData.class).where().ieq("name", args[0]).findUnique();

            if (data == null) {
                sender.sendMessage(ChatColor.RED + "There isn't any player with that name!");
                return true;
            }

            sender.sendMessage(ChatColor.LIGHT_PURPLE + "= = = ABOUT PLAYER = = =");

            sender.sendMessage(String.format("%sAccount: %s%s", ChatColor.GRAY, ChatColor.WHITE, data.getName()));

            if (data.isRulesAccepted()) {
                sender.sendMessage(String.format("%sRules Accepted: %sYES (at %s)", ChatColor.GRAY, ChatColor.WHITE,
                        data.getAcceptedDated()));
            } else {
                sender.sendMessage(String.format("%sRules Accepted: %sNO", ChatColor.GRAY, ChatColor.WHITE));
            }

            if (data.isUnlocked()) {
                sender.sendMessage(String.format("%sUnlocked: %sYES (by %s)",
                        ChatColor.GRAY, ChatColor.WHITE, data.getUnlockedBy()));
            } else {
                sender.sendMessage(String.format("%sUnlocked: %sNO", ChatColor.GRAY, ChatColor.WHITE));
            }

            if (data.getInvitedBy() != null) {
                sender.sendMessage(String.format("%sInvited By: %s%s",
                        ChatColor.GRAY, ChatColor.WHITE, data.getInvitedBy()));
            }

            List<PlayerData> invitedPlayers =
                    plugin.getDatabase().find(PlayerData.class).where().ieq("invitedBy", args[0]).findList();

            if (invitedPlayers.size() > 0) {
                sender.sendMessage(ChatColor.GRAY + "Invited Players:");
                for (PlayerData invitedPlayer : invitedPlayers) {
                    sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.WHITE + invitedPlayer.getName());
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
