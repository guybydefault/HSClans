package ru.lexmint.hsclans.cmd;

import org.bukkit.command.CommandSender;
import ru.lexmint.hsclans.HSClans;
import ru.lexmint.hscore.cmd.AbstractCommand;

/**
 * Reloads plugin.
 */
class ReloadCommand extends AbstractCommand {


    public ReloadCommand(String[] aliases, String permission, int arguments, String usage) {
        super(aliases, permission, arguments, usage);
    }

    @Override
    public void perform(CommandSender sender, String[] subargs) {
        HSClans.instance.onDisable();
        HSClans.instance.onEnable();
        HSClans.instance.getMessenger().message("commands.reload.success", sender);
    }
}
