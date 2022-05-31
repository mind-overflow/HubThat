package wtf.beatrice.hubthat.completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class SpawnCompleter  implements TabCompleter
{

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args)
    {

        List<String> list = new ArrayList<String>();

        if(args.length == 1)
        {
            list.add("<world>");
        }

        return list;
    }
}
