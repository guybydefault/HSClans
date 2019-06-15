package ru.lexmint.hsclans.integration;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.CPLayer;

/**
 * This class will be registered through the register-method in the
 * plugins onEnable-method.
 */
public class ClansPlaceholderExpansion extends PlaceholderExpansion {

    private final static String HS_CLANS_IDENTIFIER = "hsclans";

    private HSClans plugin;

    public static void setup() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ClansPlaceholderExpansion(HSClans.instance).register();
        }
    }

    /**
     * Since we register the expansion inside our own plugin, we
     * can simply use this method here to get an instance of our
     * plugin.
     *
     * @param plugin The instance of our plugin.
     */
    public ClansPlaceholderExpansion(HSClans plugin) {
        this.plugin = plugin;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier() {
        return HS_CLANS_IDENTIFIER;
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     * <p>
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param player     A {@link org.bukkit.entity.Player Player}.
     * @param identifier A String containing the identifier/value.
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {

        if (player == null) {
            return "";
        }

        CPLayer cpLayer = HSClans.instance.getClanManager().getPlayer(player.getName(), false);
        // %shsclans_clan%
        if (identifier.equals("clan")) {
            if (cpLayer.hasClan()) {
                return cpLayer.getClan().getName();
            } else {
                return "";
            }
        } else if (identifier.equals("clan_in_brackets")) {
            if (cpLayer.hasClan()) {
                return "[" + cpLayer.getClan().getName() + "]";
            } else {
                return "";
            }
        }

        // invalid placeholder was provided
        return null;
    }
}