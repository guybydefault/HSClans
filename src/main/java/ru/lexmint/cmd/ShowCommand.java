package ru.lexmint.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
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
            clan = clanManager.getPlayer(sender.getName(), true).getClan();
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
        HSClans.instance.getMessenger().message("commands.show.description", sender, description);
        HSClans.instance.getMessenger().message("commands.show.league-and-power", sender, clan.getClanLeague().getName(), String.valueOf(clan.getClaimsNumber()), String.valueOf(clan.getPowerRounded()), String.valueOf(clan.getPowerMaxRounded()));

        StringBuilder sb = new StringBuilder();
        Iterator<String> it = clan.getMembers().iterator();
        while (it.hasNext()) {
            String member = it.next();
            sb.append(ChatColor.GOLD).append(member).append(ChatColor.RESET);
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        HSClans.instance.getMessenger().message("commands.show.members", sender, sb.toString());

    }
}
