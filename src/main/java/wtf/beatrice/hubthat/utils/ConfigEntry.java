package wtf.beatrice.hubthat.utils;

public enum ConfigEntry
{
    HUB_DELAY("hub.delay"),

    SPAWN_DELAY("spawn.delay"),

    WORLD_RELATED_CHAT("chat.limit-to-worlds-sharing-spawns"),

    UPDATE_CHECKER_ENABLED("update-checker.enable"),

    MOVEMENT_DETECTION_ENABLED("movement-detection.enable"),

    GAMEMODE_SET_ON_JOIN("gamemode.set-on-join"),

    GAMEMODE("gamemode.mode"),

    TELEPORTATION_RESPAWN_HANDLER("teleportation.respawn-handler"),

    TELEPORTATION_TP_HUB_ON_JOIN("teleportation.teleport-to-hub-on-join"),

    TELEPORTATION_TP_MESSAGE_ON_JOIN("teleportation.send-tp-message-on-join"),

    TELEPORTATION_TP_HUB_ON_RESPAWN("teleportation.teleport-to-hub-on-respawn"),

    MULTIVERSE_BYPASS("settings.multiverse-bypass"),

    INVISIBILITY_FIX("settings.fix-invisible-after-tp"),

    ;

    public String path;

    ConfigEntry(String path)
    {
        this.path = path;
    }

}
