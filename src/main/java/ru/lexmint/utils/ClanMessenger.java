package ru.lexmint.utils;

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
public class ClanMessenger extends Messenger {
    private final Config lang;

    public ClanMessenger(Config lang, Debug debug) {
        super(lang, debug, lang.getString("chat.message-prefix"), lang.getString("chat.broadcast-prefix"));
        this.lang = lang;
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
        getDebug().info("[" + cpLayer.getClan().getName() + "]: " + msg);
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
        getDebug().info(msg);
        sendToPlayers(msg, recipients);
    }

}
