package me.verwelius.treasurefix.command;

import me.verwelius.treasurefix.config.Config;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class MainCommand extends AbstractCommand {

    private final Config config;

    public MainCommand(Config config) {
        this.config = config;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if(args.length == 1 && args[0].equals("reload")) {
            config.reload();
        }
    }

    @Override
    protected List<String> suggest(CommandSender sender, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if(sender.isOp() && args.length == 1) {
            if("reload".startsWith(args[0])) {
                suggestions.add("reload");
            }
        }
        return suggestions;
    }

}
