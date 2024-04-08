package me.verwelius.treasurefix.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public abstract class AbstractCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        execute(sender, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return suggest(sender, args);
    }

    protected abstract void execute(CommandSender sender, String[] args);
    protected abstract List<String> suggest(CommandSender sender, String[] args);

}
