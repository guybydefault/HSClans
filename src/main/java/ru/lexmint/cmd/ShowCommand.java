package ru.lexmint.cmd;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.Clan;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.domain.ClanRole;

import java.util.Iterator;

/**
 * Command which delivers information about the clan/player to the sender.
 */
public class ShowCommand extends BaseCommand {

    /**
     * Main constructor for creating a command.
     *
     * @param senderIsPlayer   Is sender required to be a player or not.
     * @param requiredClanRole Minimal role in a clan for executing the command.
     *                         required for executing the command.
     * @param permission       Required permission for executing this command.
     * @param arguments        Minimal number of sub arguments (command label is not included),
     * @param usage            String which contains information how to use this command.
     */
    public ShowCommand(boolean senderIsPlayer, ClanRole requiredClanRole, String permission, int arguments, String usage) {
        super(senderIsPlayer, requiredClanRole, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        ClanManager clanManager = HSClans.instance.getClanManager();

        Clan clan;
        if (subargs.length >= 2) {
            clan = clanManager.getClan(subargs[1]);
            if (clan == null) {
                CPLayer cpLayer = clanManager.getPlayer(subargs[1], false);
                if (cpLayer != null) {
                    clan = cpLayer.getClan();
                }
            }
        } else {
            if (sender instanceof Player) {
                clan = clanManager.getPlayer(sender.getName(), true).getClan();
            } else {
                HSClans.instance.getMessenger().message("commands.show.console-without-arg", sender);
                return;
            }
        }

        if (clan == null) {
            HSClans.instance.getMessenger().message("commands.show.not-found", sender);
            return;
        }

        String description = clan.getDescription();
        if (description == null) {
            description = HSClans.instance.getLangConfig().getString("commands.show.default-description");
        }


        HSClans.instance.getMessenger().message("commands.show.header", sender, clan.getName());
        HSClans.instance.getMessenger().message("commands.show.age", sender, clan.getClanLevel().getName(), String.valueOf(clan.getDaysSinceCreated()));
        HSClans.instance.getMessenger().message("commands.show.description", sender, description);
        HSClans.instance.getMessenger().message("commands.show.size", sender, String.valueOf(clan.getMembersOnline().size()), String.valueOf(clan.getMembersSize()));
        HSClans.instance.getMessenger().message("commands.show.power-and-land", sender, String.valueOf(clan.getClaimsNumber()), String.valueOf(clan.getPowerRounded()), String.valueOf(clan.getPowerMaxRounded()), String.valueOf(clan.getMembersSize()));
        HSClans.instance.getMessenger().message("commands.show.kdr", sender, String.valueOf(clan.getStatistics().getKDRRounded()), String.valueOf(clan.getStatistics().getKills()), String.valueOf(clan.getStatistics().getDeaths()));

        StringBuilder membersOnline = new StringBuilder();
        Iterator<Player> onIt = clan.getMembersOnline().iterator();
        while (onIt.hasNext()) {
            Player player = onIt.next();
            CPLayer memberPlayer = clanManager.getPlayer(player.getName(), true);
            membersOnline.append(ChatColor.YELLOW).append(memberPlayer.getClanRole().getName()).append(' ').append(ChatColor.GOLD).append(memberPlayer.getName());
            if (onIt.hasNext()) {
                membersOnline.append(ChatColor.YELLOW).append(", ");
            }
        }
        HSClans.instance.getMessenger().message("commands.show.members-online", sender, !membersOnline.toString().isEmpty() ? membersOnline.toString() : "-");

        StringBuilder membersOffline = new StringBuilder();
        Iterator<OfflinePlayer> offIt = clan.getMembersOffline().iterator();
        while (offIt.hasNext()) {
            OfflinePlayer player = offIt.next();
            CPLayer memberPlayer = clanManager.getPlayer(player.getName(), true);
            membersOffline.append(ChatColor.YELLOW).append(memberPlayer.getClanRole().getName()).append(' ').append(ChatColor.GOLD).append(memberPlayer.getName());
            if (offIt.hasNext()) {
                membersOffline.append(ChatColor.YELLOW).append(", ");
            }
        }
        HSClans.instance.getMessenger().message("commands.show.members-offline", sender, !membersOffline.toString().isEmpty() ? membersOffline.toString() : "-");
    }
}
