package ru.lexmint.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.Clan;

import java.util.HashSet;
import java.util.Set;

/**
 * Class used for sending messages to players (supports color codes, etc).
 */
public class Messenger {
    private final Config lang;

    public Messenger(Config lang) {
        this.lang = lang;
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

        msg = appendPrefix(msg, lang.getString("chat.broadcast-prefix"));
        for (String replacement : replaces) {
            msg = msg.replaceFirst("%s%", replacement);
        }
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        HSClans.instance.getServer().broadcastMessage(msg);
    }

    /**
     * Message with unbounded number of replacements. Every argument (replaces) will be replaced on letter %s% in a message
     * in order you will send them.
     *
     * @param path     path (key) to message in file
     * @param clan     clan which players will see this message
     * @param replaces things you want to replace in a message
     */
    public void broadcastToClan(String path, Clan clan, String... replaces) {
        String msg = getMessage(path);
        msg = appendPrefix(msg, lang.getString("chat.clan-broadcast"));
        msg = replaceVariables(msg, replaces);
        msg = translateColorCodes(msg);
        sendToPlayers(msg, clan.getMembersOnline());
    }


    /**
     * Sends a message with no replacements but color changes to a group of players.
     *
     * @param msg        Message which you want to be sent.
     * @param recipients Players (members of the clan) who will see this message
     */
    public void chatToClan(String msg, CPLayer cpLayer, Set<Player> recipients) {
        msg = appendPrefix(msg, lang.getString("chat.clan-format").replaceFirst("%clan_role%", cpLayer.getClanRole().getName()).replaceFirst("%name%", cpLayer.getName()));
        /*
        Copy of recipients Set is used in creation of new AsyncPlayerChatEvent because some chat plugins like Essentials, which
        support local chat, modify Collection of the recipients so other players (out of the distance) can not receive clan/ally chat messages.
        */
        AsyncPlayerChatEvent playerChatEvent = new AsyncPlayerChatEvent(false, cpLayer.getPlayer(), msg, new HashSet<>(recipients));
        HSClans.instance.getServer().getPluginManager().callEvent(playerChatEvent);
        if (playerChatEvent.isCancelled()) {
            return;
        }
        msg = translateColorCodes(msg);
        sendToPlayers(msg, recipients);
    }

    /**
     * Sends a message with no replacements but color changes to a group of players.
     *
     * @param msg        Message which you want to be sent.
     * @param recipients Players (members of the clan) who will see this message
     */
    public void chatToAlly(String msg, CPLayer cpLayer, Set<Player> recipients) {
        msg = appendPrefix(msg, lang.getString("chat.ally-format").replaceFirst("%clan_role%", cpLayer.getClanRole().getName()).replaceFirst("%name%", cpLayer.getName()).replaceFirst("%clan_name%", cpLayer.getClan().getName()));
         /*
        Copy of recipients Set is used in creation of new AsyncPlayerChatEvent because some chat plugins like Essentials, which
        support local chat, modify Collection of the recipients so other players (out of the distance) can not receive clan/ally chat messages.
         */
        AsyncPlayerChatEvent playerChatEvent = new AsyncPlayerChatEvent(false, cpLayer.getPlayer(), msg, new HashSet<>(recipients));
        HSClans.instance.getServer().getPluginManager().callEvent(playerChatEvent);
        if (playerChatEvent.isCancelled()) {
            return;
        }
        msg = translateColorCodes(msg);
        sendToPlayers(msg, recipients);
    }

    /**
     * Sends message to given recipients.
     *
     * @param msg        Message which you want to be sent.
     * @param recipients players who will receive this message
     */
    public void sendToPlayers(String msg, Set<Player> recipients) {
        for (Player recipient : recipients) {
            if (recipient != null) {
                recipient.sendMessage(msg);
            }
        }
    }


    /**
     * Replaces variables in given message.
     *
     * @param message  the message which need to be processed.
     * @param replaces things you want to replace in a message
     * @return Message with replaces
     */
    public String replaceVariables(String message, String... replaces) {
        String msg = message;
        for (String replacement : replaces) {
            msg = msg.replaceFirst("%s%", replacement);
        }
        return msg;
    }

    /**
     * Translates chat color codes in the given message.
     *
     * @param message Message which is supposed to be translated with color codes.
     * @return Message with color.
     */
    public String translateColorCodes(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Get message by the given path. If message in language file not found - writes to debug log.
     *
     * @param path Path of the message in language file.
     * @return Message or null if it has not been found.
     */
    public String getMessage(String path) {
        String msg = lang.getString(path);
        if (msg == null) {
            HSClans.instance.getDebug().error("Message in language file not found. Path: " + path);
        }
        return msg;
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
        String msg = getMessage(path);
        msg = appendPrefix(msg, lang.getString("chat.message-prefix"));
        msg = replaceVariables(msg, replaces);
        msg = translateColorCodes(msg);
        receiver.sendMessage(msg);
    }

    /**
     * Sends message to given receiver. Translates all alternative color codes to color.
     * Is used in response to player's and console's commands.
     *
     * @param path     path (key) to message in file.
     * @param receiver Receiver of the message.
     * @param replaces things you want to replace in a message
     */
    public void message(String path, CommandSender receiver, String... replaces) {
        String msg = getMessage(path);
        msg = appendPrefix(msg, lang.getString("chat.message-prefix"));
        msg = replaceVariables(msg, replaces);
        msg = translateColorCodes(msg);
        receiver.sendMessage(msg);
    }

    /**
     * @param msg Message which you want to be prefix set.
     * @return Message with prefix.
     */
    private String appendPrefix(String msg, String prefix) {
        StringBuilder sb = new StringBuilder(msg);
        sb.insert(0, prefix);
        return sb.toString();
    }

    /**
     * Formats message with given path doing replaces in it according to arguments.
     *
     * @param path     Path of the message in language file.
     * @param replaces things you want to replace in a message
     * @return Formatted message.
     */
    public String format(String path, String... replaces) {
        String msg = getMessage(path);
        msg = replaceVariables(lang.getString(path), replaces);
        msg = translateColorCodes(msg);
        return msg;
    }

}
