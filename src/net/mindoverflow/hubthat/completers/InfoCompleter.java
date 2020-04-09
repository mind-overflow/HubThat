package net.mindoverflow.hubthat.completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class InfoCompleter implements TabCompleter
{
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args)
    {
        List<String> list = new ArrayList<String>();
        if(args.length == 1)
        {
            list.add("help");
            list.add("reload");
            if(args[0].startsWith("h"))
            {
                list.clear();
                list.add("help");
            } else
            if(args[0].startsWith("r"))
            {
                list.clear();
                list.add("reload");
            }
        }
        return list;
    }
}
