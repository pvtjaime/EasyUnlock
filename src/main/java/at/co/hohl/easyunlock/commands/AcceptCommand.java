package at.co.hohl.easyunlock.commands;

import at.co.hohl.easyunlock.EasyUnlock;
import at.co.hohl.easyunlock.storage.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;

/**
 * Executor for the accept command.
 *
 * @author Michael Hohl
 */
public class AcceptCommand implements CommandExecutor {
  /**
   * Plugin which holds the instance.
   */
  private final EasyUnlock plugin;

  /**
   * Creates a new instance of the AcceptCommand.
   *
   * @param plugin the plugin which holds the instance.
   */
  public AcceptCommand(EasyUnlock plugin) {
    this.plugin = plugin;
  }

  /**
   * Called when the command is used by a player.
   *
   * @param sender  the sender of the command.
   * @param command the command itself.
   * @param args    label used for calling the command.
   * @param strings the arguments.
   * @return true, if the command gets executed.
   */
  public boolean onCommand(CommandSender sender, Command command, String args, String[] strings) {
    if (sender.hasPermission("easyunlock.command.accept") && sender instanceof Player) {
      Player player = (Player) sender;

      PlayerData data =
              plugin.getDatabase().find(PlayerData.class).where().ieq("name", player.getName()).findUnique();

      if (data == null) {
        data = new PlayerData();
        data.setName(player.getName());
      }

      if (data.isRulesAccepted()) {
        sender.sendMessage(
                ChatColor.RED + plugin.getConfiguration().getString("messages.rules_already_accepted"));
      } else {
        data.setRulesAccepted(true);
        data.setAcceptedDated(new Date());

        if (anyModOnline(plugin.getServer())) {
          sender.sendMessage(ChatColor.GREEN + plugin.getConfiguration().getString("messages.wait_for_mod"));
          notifyOnlineMods(plugin.getServer());
        } else {
          sender.sendMessage(ChatColor.LIGHT_PURPLE + plugin.getConfiguration().getString(
                  "messages.no_mod_online"));
        }
      }

      plugin.getDatabase().save(data);

      return true;
    } else {
      return false;
    }
  }

  /**
   * Checks if there is anybody online with unlock permissions.
   *
   * @param server  the server instance to look for players.
   * @return true, if there is at least one player online, who can unlock.
   */
  private boolean anyModOnline(Server server) {
    Player[] players = server.getOnlinePlayers();

    for (Player player : players) {
      if (player.hasPermission("easyunlock.command.unlock")) {
        return true;
      }
    }

    return false;
  }

  /**
   * Notifies all online mods, that there are new player to unlock.
   *
   * @param server  the server where players are to unlock.
   */
  private void notifyOnlineMods(Server server) {
    server.broadcast(ChatColor.GREEN + plugin.getConfiguration().getString("messages.players_to_unlock"),
            "easyunlock.command.unlock");

  }
}
