package ru.lexmint.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.lexmint.HSClans;
import ru.lexmint.domain.ClanRole;

import java.util.HashMap;

/**
 * Class responsible for all plugin's commands.
 */
public class CommandManager implements CommandExecutor {

    HashMap<String, BaseCommand> commandHashMap = new HashMap<>();

    CreateCommand create;
    HelpCommand help;
    JoinCommand join;
    InviteCommand invite;
    LeaveCommand leave;
    UninviteCommand uninvite;
    ShowCommand show;
    ClaimCommand claim;
    KickCommand kick;
    PromoteCommand promote;
    DemoteCommand demote;

    public CommandManager() {
        create = new CreateCommand(true, ClanRole.OUTLAW, "hsclans.command.create", 1, "commands.create.usage");
        join = new JoinCommand(true, ClanRole.OUTLAW, "hsclans.command.join", 1, "commands.join.usage");
        invite = new InviteCommand(true, ClanRole.MODERATOR, "hsclans.command.invite", 1, "commands.invite.usage");
        help = new HelpCommand(false, ClanRole.OUTLAW, "hsclans.command.help", 0, "commands.help.usage");
        leave = new LeaveCommand(true, ClanRole.NEWBIE, "hsclans.command.leave", 0, "commands.leave.usage");
        uninvite = new UninviteCommand(true, ClanRole.MODERATOR, "hsclans.command.uninvite", 1, "commands.uninvite.usage");
        show = new ShowCommand(false, ClanRole.OUTLAW, "hsclans.command.show", 0, "commands.show.usage");
        claim = new ClaimCommand(true, ClanRole.MODERATOR, "hsclans.command.claim", 0, "commands.claim.usage");
        kick = new KickCommand(true, ClanRole.MODERATOR, "hsclans.command.kick", 1, "commands.kick.usage");
        promote = new PromoteCommand(true, ClanRole.MODERATOR, "hsclans.commands.promote", 1, "commands.promote.usage");
        demote = new DemoteCommand(true, ClanRole.MODERATOR, "hsclans.commands.demote", 1, "commands.demote.usage");

        commandHashMap.put("create", create);
        commandHashMap.put("join", join);
        commandHashMap.put("invite", invite);
        commandHashMap.put("help", help);
        commandHashMap.put("leave", leave);
        commandHashMap.put("uninvite", uninvite);
        commandHashMap.put("show", show);
        commandHashMap.put("claim", claim);
        commandHashMap.put("kick", kick);
        commandHashMap.put("promote", promote);
        commandHashMap.put("demote", demote);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            BaseCommand executor = commandHashMap.get(args[0].toLowerCase());
            if (executor != null) {
                if (executor.getSenderIsPlayer() && !(sender instanceof Player)) {
                    HSClans.instance.getMessenger().message("messages.errors.only-player-command", sender);
                } else if (!sender.hasPermission(executor.getPermission())) {
                    HSClans.instance.getMessenger().message("messages.errors.no-permission", sender);
                } else if (args.length - 1 < executor.getArguments()) {
                    HSClans.instance.getMessenger().message(executor.getUsage(), sender);
                } else if (executor.senderIsPlayer && HSClans.instance.getClanManager().getPlayer(sender.getName(), true).getClanRole().getLevel()
                        < executor.getRequiredClanRole().getLevel()) {
                    HSClans.instance.getMessenger().message("messages.errors.low-clan-role", sender, executor.getRequiredClanRole().getName());
                } else {
                    executor.perform(sender, args);
                }
            } else {
                HSClans.instance.getMessenger().message("messages.errors.command-not-found", sender);
            }
        } else {
            HSClans.instance.getMessenger().message("messages.errors.no-command", sender);
        }

        return true;
    }
}
