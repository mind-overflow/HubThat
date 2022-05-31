package wtf.beatrice.hubthat.utils;

public enum LocalizedMessages
{


    NO_PERMISSION("error.no-permission"),

    WARNING_TELEPORTATION_CANCELLED("warning.teleportation-cancelled"),

    ERROR_ALREADY_TELEPORTING("error.already-teleporting"),

    ERROR_HUB_NOT_SET("error.hub-not-set"),

    ERROR_SPAWN_NOT_SET("error.spawn-not-set"),

    ERROR_WORLD_NOT_EXISTING("error.unknown-world"),

    ERROR_WRONG_USAGE("error.wrong-usage"),

    ERROR_CONSOLE_ACCESS_BLOCKED("error.console-access-blocked"),

    ERROR_PLAYER_OFFLINE("error.player-offline"),

    INFO_HUB_TELEPORTED("info.hub.teleported"),

    INFO_HUB_TELEPORTED_OTHER("info.hub.teleported-other"),

    INFO_SPAWN_TELEPORTED("info.spawn.teleported"),

    INFO_SPAWN_TELEPORTED_OTHER("info.spawn.teleported-other"),

    INFO_WORLDTP_TELEPORTED("info.worldtp.teleported"),

    INFO_TELEPORT_DELAY("info.global.teleport-delay"),

    INFO_HUB_SET("info.hub.set"),

    INFO_SPAWN_SET("info.spawn.set"),

    INFO_WORLDS_LIST("info.worlds-list"),

    INFO_PREFIX("info.global.prefix"),




    ;

    public String path;

    LocalizedMessages(String path)
    {
        this.path = path;
    }

}
