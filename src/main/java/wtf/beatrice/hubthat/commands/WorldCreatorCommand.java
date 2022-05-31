package wtf.beatrice.hubthat.commands;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WorldCreatorCommand implements CommandExecutor
{


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {
        WorldCreator worldCreator = new WorldCreator("worldName");
        worldCreator.type(WorldType.FLAT);
        worldCreator.environment(World.Environment.NETHER);
        worldCreator.createWorld();


        return true;
    }
}
