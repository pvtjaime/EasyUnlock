package at.co.hohl.easyunlock;

import at.co.hohl.easyunlock.commands.AboutPlayerCommand;
import at.co.hohl.easyunlock.commands.AcceptCommand;
import at.co.hohl.easyunlock.commands.InvitedByCommand;
import at.co.hohl.easyunlock.commands.UnlockCommand;
import at.co.hohl.easyunlock.listener.PlayerJoinsListener;
import at.co.hohl.easyunlock.storage.PlayerData;
import at.co.hohl.permissions.Permission;
import at.co.hohl.permissions.PermissionHandler;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * EasyUnlock for Bukkit
 *
 * @author Michael Hohl
 */
public class EasyUnlock extends JavaPlugin {
    /** Needed to unlock player. */
    public static final Permission UNLOCK_PERMISSION = new Permission("easyunlock.command.unlock", true);

    /** Needed to accept the rules. */
    public static final Permission ACCEPT_PERMISSION = new Permission("easyunlock.command.accept", false);

    /** Needed to accept the rules. */
    public static final Permission INVITED_BY_PERMISSION = new Permission("easyunlock.command.invitedby", false);

    /** Needed to get information about players. */
    public static final Permission ABOUT_PLAYER_PERMISSION = new Permission("easyunlock.command.aboutplayer", false);

    /** Handler for the permissions. */
    private PermissionHandler permissionHandler;

    /** Logger used by the plugin. */
    private Logger logger;

    /** Creates a new instance of the EasyUnlock plugin. */
    public EasyUnlock() {
    }

    /** Called when the plugin is enabled. */
    public void onEnable() {
        logger = getServer().getLogger();

        permissionHandler = new PermissionHandler(this);

        setupDatabase();

        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, new PlayerJoinsListener(this),
                Event.Priority.Normal, this);

        getCommand("unlock").setExecutor(new UnlockCommand(this));
        getCommand("accept").setExecutor(new AcceptCommand(this));
        getCommand("invitedby").setExecutor(new InvitedByCommand(this));
        getCommand("aboutplayer").setExecutor(new AboutPlayerCommand(this));

        logger.info(String.format("[%s] version %s enabled!",
                getDescription().getName(), getDescription().getVersion()));
    }

    /** Called when the plugin is disabled. */
    public void onDisable() {
        logger.info(String.format("[%s] disabled!", getDescription().getName()));
    }

    /** @return the logger used by the plugin. */
    public Logger getLogger() {
        return logger;
    }

    /** @return permissions handler used by the plugin. */
    public PermissionHandler getPermissionHandler() {
        return permissionHandler;
    }

    /** Creates needed databases. */
    private void setupDatabase() {
        try {
            getDatabase().find(PlayerData.class).findRowCount();
        } catch (PersistenceException ex) {
            logger.info(String.format("[%s] Installing database due to first time usage.!", getDescription().getName(),
                    getDescription().getVersion()));
            installDDL();
        }
    }

    /** @return all daos of this plugins. */
    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(PlayerData.class);
        return list;
    }
}

