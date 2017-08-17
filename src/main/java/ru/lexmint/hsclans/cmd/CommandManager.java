package ru.lexmint.hsclans.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hsclans.domain.ClanRole;
import ru.lexmint.hscore.cmd.AbstractCommand;
import ru.lexmint.hscore.cmd.RootCommandController;

/**
 * Class responsible for all plugin's commands.
 */
public class CommandManager extends RootCommandController {
    public CommandManager() {
        super();
        registerCommand(new CreateCommand(new String[]{"create"}, ClanRole.OUTLAW, "hsclans.command.create", 1, "commands.create.usage"));
        registerCommand(new DescriptionCommand(new String[]{"desc", "description"}, ClanRole.MODERATOR, "hsclans.command.description", 1, "commands.description.usage"));
        registerCommand(new JoinCommand(new String[]{"join"}, ClanRole.OUTLAW, "hsclans.command.join", 1, "commands.join.usage"));
        registerCommand(new InviteCommand(new String[]{"inv", "invite"}, ClanRole.MODERATOR, "hsclans.command.invite", 1, "commands.invite.usage"));
        registerCommand(new HelpCommand(new String[]{"help"}, "hsclans.command.help", 0, "commands.help.usage"));
        registerCommand(new LeaveCommand(new String[]{"leave"}, ClanRole.NEWBIE, "hsclans.command.leave", 0, "commands.leave.usage"));
        registerCommand(new UninviteCommand(new String[]{"uninv", "uninvite"}, ClanRole.MODERATOR, "hsclans.command.uninvite", 1, "commands.uninvite.usage"));
        registerCommand(new ShowCommand(new String[]{"show", "f"}, "hsclans.command.show", 0, "commands.show.usage"));
        registerCommand(new PlayerInfoCommand(new String[]{"player", "p", "power", "who"}, "hsclans.command.player", 0, "commands.player.usage"));
        registerCommand(new ClaimCommand(new String[]{"claim"}, ClanRole.MODERATOR, "hsclans.command.claim", 0, "commands.claim.usage"));
        registerCommand(new UnclaimCommand(new String[]{"unclaim"}, ClanRole.MODERATOR, "hsclans.command.unclaim", 0, "commands.unclaim.usage"));
        registerCommand(new KickCommand(new String[]{"kick"}, ClanRole.MODERATOR, "hsclans.command.kick", 1, "commands.kick.usage"));
        registerCommand(new PromoteCommand(new String[]{"promote"}, ClanRole.MODERATOR, "hsclans.command.promote", 1, "commands.promote.usage"));
        registerCommand(new DemoteCommand(new String[]{"demote"}, ClanRole.MODERATOR, "hsclans.command.demote", 1, "commands.demote.usage"));
        registerCommand(new HomeCommand(new String[]{"home"}, ClanRole.NEWBIE, "hsclans.command.home", 0, "commands.home.usage"));
        registerCommand(new SethomeCommand(new String[]{"sethome"}, ClanRole.MODERATOR, "hsclans.command.sethome", 0, "commands.sethome.usage"));
        registerCommand(new ClanChatCommand(new String[]{"c"}, ClanRole.NEWBIE, "hsclans.command.clanchat", 1, "commands.clanchat.usage"));
        registerCommand(new RegenCommand(new String[]{"regen"}, "hsclans.command.regen", 1, "commands.regen.usage"));
        registerCommand(new ListCommand(new String[]{"list"}, "hsclans.command.list", 0, "commands.list.usage"));
        registerCommand(new AllyCommand(new String[]{"ally"}, ClanRole.MODERATOR, "hsclans.command.ally", 1, "commands.ally.usage"));
        registerCommand(new AllyChatCommand(new String[]{"a"}, ClanRole.NEWBIE, "hsclans.command.allychat", 1, "commands.allychat.usage"));
        registerCommand(new DisbandCommand(new String[]{"disband"}, "hsclans.command.disband", 1, "commands.disband.usage"));
        registerCommand(new BypassCommand(new String[]{"bypass"}, ClanRole.OUTLAW, "hsclans.command.bypass", 0, "commands.bypass.usage"));
        registerCommand(new AutoclaimCommand(new String[]{"autoclaim"}, ClanRole.OUTLAW, "hsclans.command.autoclaim", 0, "commands.autoclaim.usage"));
        registerCommand(new ReloadCommand(new String[]{"reload"}, "hsclans.command.reload", 0, "commands.reload.usage"));
        registerCommand(new MapCommand(new String[]{"map"}, ClanRole.OUTLAW, "hsclans.command.map", 0, "commands.map.usage"));
        registerCommand(new PlayerListCommand(new String[]{"plist"}, "hsclans.command.playerlist", 0, "commands.player-list.usage"));
        registerCommand(new TimeResetCommand(new String[]{"timereset"}, "hsclans.command.timereset", 0, "commands.time-reset.usage"));
        registerCommand(new TournamentCommand(new String[]{"tournament"}, "hsclans.command.tournament", 0, "commands.tournament.usage"));
    }

    @Override
    protected AbstractCommand filterCommand(CommandSender sender, Command command, String label, String... args) {
        AbstractCommand executor = super.filterCommand(sender, command, label, args);
        if (executor != null) {
            if (executor instanceof AbstractClanPlayerCommand) {
                AbstractClanPlayerCommand cpc = (AbstractClanPlayerCommand) executor;
                if (HSClans.instance.getClanManager().getPlayer(sender.getName(), true).getClanRole().getLevel()
                        < cpc.getRequiredClanRole().getLevel()
                        /** Player in bypass mode should be able to promote/demote/kick anyone even if he is at low clan role */
                        && !((cpc instanceof PromoteCommand || cpc instanceof DemoteCommand || cpc instanceof KickCommand)
                        && BypassCommand.isBypassing(sender.getName()))) {
                    HSClans.instance.getMessenger().message("messages.errors.low-clan-role", sender, cpc.getRequiredClanRole().getName());
                    return null;
                }
            }
        }
        return executor;
    }

    @Override
    protected void noCommand(CommandSender sender, Command command, String label, String... args) {
        HSClans.instance.getMessenger().message("messages.errors.no-command", sender);
    }

    @Override
    protected void commandNotFound(CommandSender sender, Command command, String label, String... args) {
        HSClans.instance.getMessenger().message("messages.errors.command-not-found", sender);
    }

    @Override
    protected void noPermission(CommandSender sender, Command command, String label, AbstractCommand executor, String... args) {
        HSClans.instance.getMessenger().message("messages.errors.no-permission", sender);
    }

    @Override
    protected void onlyPlayerCommand(CommandSender sender, Command command, String label, AbstractCommand executor, String... args) {
        HSClans.instance.getMessenger().message("messages.errors.only-player-command", sender);
    }

    @Override
    protected void wrongNumberOfArguments(CommandSender sender, Command command, String label, AbstractCommand executor, String... args) {
        HSClans.instance.getMessenger().message(executor.getUsage(), sender);
    }

    /**
     * Performs claim command upon this sender.
     *
     * @param sender Sender of the command.
     */

    public void performClaimCommand(CommandSender sender) {
        handleCommand(sender, null, "claim");
    }
}
