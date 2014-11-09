package ru.lexmint.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.lexmint.HSClans;

/**
 * Class used for sending messages to players (supports color codes, etc).
 */
public class Messenger {
    private final Config lang;

    public Messenger() {
        lang = HSClans.instance.getLangConfig();
    }

    /**
     * Broadcast message to all players on the server. Message with unbounded number of replacement.
     * Every argument (replaces) will be replaced on letter %s% in a message in order you will send them.
     *
     * @param path     path (key) to message in file
     * @param replaces things you want to replace in a message
     */
    public void broadcastToAll(String path, String... replaces) {
        String msg = lang.getString(path);
        StringBuilder sb = new StringBuilder(msg);
        sb.insert(0, lang.getString("chat.default-prefix"));
        String m = sb.toString();
        for (String replacement : replaces) {
            m = m.replaceFirst("%s%", replacement);
        }
        m = ChatColor.translateAlternateColorCodes('&', m);
        HSClans.instance.getServer().broadcastMessage(m);
    }


    /**
     * Message with unbounded number of replacements. Every argument (replaces) will be replaced on letter %s% in a message
     * in order you will send them.
     *
     * @param path     path (key) to message in file
     * @param receiver player who will see message
     * @param replaces things you want to replace in a message
     */
    public void message(String path, Player receiver, String... replaces) {
        String msg = lang.getString(path);
        for (String replacement : replaces) {
            msg = msg.replaceFirst("%s%", replacement);
        }
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        receiver.sendMessage(msg);
    }

    /**
     * Sends message to given receiver. Translates all alternative color codes to color.
     * Is used in response to player's and console's commands.
     * @param msg Message.
     * @param receiver Receiver of the message.
     */
    public void message(String msg, CommandSender receiver) {
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        receiver.sendMessage(msg);
    }
}
