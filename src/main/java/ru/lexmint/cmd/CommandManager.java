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

    private CreateCommand create;
    private HelpCommand help;
    private JoinCommand join;
    private InviteCommand invite;
    private LeaveCommand leave;
    private UninviteCommand uninvite;
    private ShowCommand show;
    private PlayerCommand player;
    private ClaimCommand claim;
    private UnclaimCommand unclaim;
    private KickCommand kick;
    private PromoteCommand promote;
    private DemoteCommand demote;
    private DescriptionCommand description;
    private HomeCommand home;
    private SethomeCommand setHome;
    private ClanChatCommand clanChat;
    private RegenCommand regen;
    private ListCommand list;
    private AllyCommand ally;
    private AllyChatCommand allyChat;
    private DisbandCommand disband;
    private BypassCommand bypass;
    private AutoclaimCommand autoclaim;
    private ReloadCommand reload;

    public CommandManager() {
        create = new CreateCommand(true, ClanRole.OUTLAW, "hsclans.command.create", 1, "commands.create.usage");
        description = new DescriptionCommand(true, ClanRole.MODERATOR, "hsclans.command.description", 1, "commands.description.usage");
        join = new JoinCommand(true, ClanRole.OUTLAW, "hsclans.command.join", 1, "commands.join.usage");
        invite = new InviteCommand(true, ClanRole.MODERATOR, "hsclans.command.invite", 1, "commands.invite.usage");
        help = new HelpCommand(false, ClanRole.OUTLAW, "hsclans.command.help", 0, "commands.help.usage");
        leave = new LeaveCommand(true, ClanRole.NEWBIE, "hsclans.command.leave", 0, "commands.leave.usage");
        uninvite = new UninviteCommand(true, ClanRole.MODERATOR, "hsclans.command.uninvite", 1, "commands.uninvite.usage");
        show = new ShowCommand(false, ClanRole.OUTLAW, "hsclans.command.show", 0, "commands.show.usage");
        player = new PlayerCommand(false, ClanRole.OUTLAW, "hsclans.command.player", 0, "commands.player.usage");
        claim = new ClaimCommand(true, ClanRole.MODERATOR, "hsclans.command.claim", 0, "commands.claim.usage");
        unclaim = new UnclaimCommand(true, ClanRole.MODERATOR, " hsclans.command.unclaim", 0, "commands.unclaim.usage");
        kick = new KickCommand(true, ClanRole.MODERATOR, "hsclans.command.kick", 1, "commands.kick.usage");
        promote = new PromoteCommand(true, ClanRole.MODERATOR, "hsclans.command.promote", 1, "commands.promote.usage");
        demote = new DemoteCommand(true, ClanRole.MODERATOR, "hsclans.command.demote", 1, "commands.demote.usage");
        home = new HomeCommand(true, ClanRole.NEWBIE, "hsclans.command.home", 0, "commands.home.usage");
        setHome = new SethomeCommand(true, ClanRole.MODERATOR, "hsclans.command.sethome", 0, "commands.sethome.usage");
        clanChat = new ClanChatCommand(true, ClanRole.NEWBIE, "hsclans.command.clanchat", 1, "commands.clanchat.usage");
        regen = new RegenCommand(false, ClanRole.OUTLAW, "hsclans.command.regen", 1, "commands.regen.usage");
        list = new ListCommand(false, ClanRole.OUTLAW, "hsclans.command.list", 0, "commands.list.usage");
        ally = new AllyCommand(true, ClanRole.MODERATOR, "hsclans.command.ally", 1, "commands.ally.usage");
        allyChat = new AllyChatCommand(true, ClanRole.NEWBIE, "hsclans.command.allychat", 1, "commands.allychat.usage");
        disband = new DisbandCommand(false, ClanRole.OUTLAW, "hsclans.command.disband", 1, "commands.disband.usage");
        bypass = new BypassCommand(true, ClanRole.OUTLAW, "hsclans.command.bypass", 0, "commands.bypass.usage");
        autoclaim = new AutoclaimCommand(true, ClanRole.OUTLAW, "hsclans.command.autoclaim", 0, "commands.autoclaim.usage");
        reload = new ReloadCommand(false, ClanRole.OUTLAW, "hsclans.command.reload", 0, "commands.reload.usage");

        commandHashMap.put("create", create);

        commandHashMap.put("description", description);
        commandHashMap.put("desc", description);

        commandHashMap.put("join", join);

        commandHashMap.put("invite", invite);
        commandHashMap.put("inv", invite);

        commandHashMap.put("help", help);
        commandHashMap.put("leave", leave);

        commandHashMap.put("uninvite", uninvite);
        commandHashMap.put("uninv", uninvite);

        commandHashMap.put("show", show);
        commandHashMap.put("f", show);

        commandHashMap.put("player", player);
        commandHashMap.put("p", player);
        commandHashMap.put("power", player);

        commandHashMap.put("claim", claim);

        commandHashMap.put("unclaim", unclaim);

        commandHashMap.put("kick", kick);
        commandHashMap.put("promote", promote);
        commandHashMap.put("demote", demote);
        commandHashMap.put("home", home);
        commandHashMap.put("sethome", setHome);

        commandHashMap.put("c", clanChat);

        commandHashMap.put("regen", regen);
        commandHashMap.put("list", list);
        commandHashMap.put("ally", ally);

        commandHashMap.put("a", allyChat);
        commandHashMap.put("disband", disband);
        commandHashMap.put("bypass", bypass);
        commandHashMap.put("autoclaim", autoclaim);
        commandHashMap.put("reload", reload);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            BaseCommand executor = commandHashMap.get(args[0].toLowerCase());
            performCommand(sender, executor, false, args);
        } else {
            HSClans.instance.getMessenger().message("messages.errors.no-command", sender);
        }
        return true;
    }

    private void performCommand(CommandSender sender, BaseCommand executor, boolean ignoreArgs, String... args) {
        if (executor != null) {
            if (executor.getSenderIsPlayer() && !(sender instanceof Player)) {
                HSClans.instance.getMessenger().message("messages.errors.only-player-command", sender);
            } else if (!sender.hasPermission(executor.getPermission())) {
                HSClans.instance.getMessenger().message("messages.errors.no-permission", sender);
            } else if (!ignoreArgs && args.length - 1 < executor.getArguments()) {
                HSClans.instance.getMessenger().message(executor.getUsage(), sender);
            } else if (executor.senderIsPlayer && HSClans.instance.getClanManager().getPlayer(sender.getName(), true).getClanRole().getLevel()
                    < executor.getRequiredClanRole().getLevel()
                    /** Player in bypass mode should be able to promote/demote/kick anyone even if he is at low clan role */
                    && !((executor instanceof PromoteCommand || executor instanceof DemoteCommand || executor instanceof KickCommand)
                    && BypassCommand.isBypassing(sender.getName()))) {
                HSClans.instance.getMessenger().message("messages.errors.low-clan-role", sender, executor.getRequiredClanRole().getName());
            } else {
                executor.perform(sender, args);
            }
        } else {
            HSClans.instance.getMessenger().message("messages.errors.command-not-found", sender);
        }
    }

    /**
     * Performs claim command upon this sender.
     * @param sender Sender of the command.
     */
    public void performClaimCommand(CommandSender sender) {
        performCommand(sender, claim, true);
    }
}
