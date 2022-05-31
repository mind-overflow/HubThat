package wtf.beatrice.hubthat.utils;

public enum Permissions
{

    RELOAD_CONFIG("hubthat.reloadconfig"),

    NO_HUB_DELAY("hubthat.nohubdelay"),

    HUB_SET("hubthat.sethub"),

    HUB_TELEPORT("hubthat.hub"),

    HUB_TELEPORT_OTHERS("hubthat.hub.others"),

    NO_SPAWN_DELAY("hubthat.nospawndelay"),

    SPAWN_SET("hubthat.setspawn"),

    SPAWN_TELEPORT("hubthat.spawn"),

    SPAWN_TELEPORT_OTHERS("hubthat.spawn.others"),

    SPAWN_TELEPORT_ANOTHER_WORLD("hubthat.spawn.anotherworld"),

    TELEPORT_TO_WORLD("hubthat.gotoworld"),

    WORLD_LIST("hubthat.listworlds"),

    GET_UPDATES_NOTIFICATIONS("hubthat.updates"),

    HELP_MESSAGE("hubthat.help");

    public String permission;

    Permissions(String permission) { this.permission = permission; }
}
