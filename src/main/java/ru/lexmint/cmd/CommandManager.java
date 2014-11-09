package ru.lexmint.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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


    public CommandManager() {
        create = new CreateCommand(ClanRole.OUTLAW, 1, HSClans.instance.getLangConfig().getString("commands.create.usage"));
        join = new JoinCommand(ClanRole.OUTLAW, 1, HSClans.instance.getLangConfig().getString("commands.join.usage"));
        invite = new InviteCommand(ClanRole.MODERATOR, 1, HSClans.instance.getLangConfig().getString("commands.invite.usage"));
        help = new HelpCommand(ClanRole.OUTLAW, 0, HSClans.instance.getLangConfig().getString("commands.help.usage"));

        // TODO: Settings. Cutomizable command names.
        commandHashMap.put("create", create);
        commandHashMap.put("join", join);
        commandHashMap.put("invite", invite);
        commandHashMap.put("help", help);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            BaseCommand executor = commandHashMap.get(args[0]);
            if (executor != null) {
                if (args.length - 1 < executor.arguments) {
                    HSClans.instance.getMessenger().message(executor.getUsage(), sender);
                    return true;
                } else {
                    executor.perform(sender, args);
                    return true;
                }

            }
        }
        help.perform(sender, args);

        return true;
    }
}
