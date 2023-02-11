package wtf.beatrice.hubthat.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class PluginCache
{
    public static ArrayList<String> teleporting = new ArrayList<>();
    public static ArrayList<String>cancelRunnable = new ArrayList<>();

    public static Boolean updateChecker = true;
    public static boolean invisibilityFix = false; // sometimes, in buggy versions of Spigot, players become invisible after getting teleported. this fixes the problem.
    public static boolean sendJoinTpMessage = true;
    public static final ItemStack AIR = new ItemStack(Material.AIR, 1);

    public static final String minSupportedVersion = "1.7";
    public static final String maxSupportedVersion = "1.20";
}
