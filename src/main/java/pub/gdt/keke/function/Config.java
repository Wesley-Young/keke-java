package pub.gdt.keke.function;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import pub.gdt.keke.RobotMain;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Config {
    private static final Path config = Path.of("config");
    private static final Path permissionsJson = config.resolve("permissions.json");
    private static final Path groupsJson = config.resolve("groups.json");
    private static LongSet masters = new LongOpenHashSet(),
                           littleOwners = new LongOpenHashSet(),
                           officialGroups = LongSet.of(666879777L, 584539924L),
                           unofficialGroups = new LongOpenHashSet();
    private enum PermType { MASTER, LITTLE_OWNER }

    static {
        // load permissions.json
        try {
            JsonArray permissions = JsonParser.parseReader(Files.newBufferedReader(permissionsJson)).getAsJsonArray();
            for (JsonElement element : permissions) {
                JsonObject object = element.getAsJsonObject();
                long qid = object.get("qq").getAsLong();
                PermType permType = PermType.valueOf(object.get("permission").getAsString());
                switch (permType) {
                    case MASTER -> masters.add(qid);
                    case LITTLE_OWNER -> littleOwners.add(qid);
                }
            }
        } catch (Exception e) {
            RobotMain.getBotInstance().close();
            Logger.getGlobal().log(Level.SEVERE, "permissions.json 加载失败。");
            System.exit(1);
        }

        // load groups.json
        try {
            JsonArray groups = JsonParser.parseReader(Files.newBufferedReader(groupsJson)).getAsJsonArray();
            for (JsonElement element : groups)
                unofficialGroups.add(element.getAsLong());
        } catch (Exception e) {
            RobotMain.getBotInstance().close();
            Logger.getGlobal().log(Level.SEVERE, "groups.json 加载失败。");
            System.exit(1);
        }
    }

    public static boolean isMaster(long qid) {
        return masters.contains(qid);
    }
    public static boolean isLittleOwner(long qid) {
        return littleOwners.contains(qid) | masters.contains(qid);
    }
    public static boolean reloadPermissions() {
        try {
            JsonArray permissions = JsonParser.parseReader(Files.newBufferedReader(permissionsJson)).getAsJsonArray();
            for (JsonElement element : permissions) {
                LongSet newMasters = new LongOpenHashSet(), newLittleOwners = new LongOpenHashSet();
                JsonObject object = element.getAsJsonObject();
                long qid = object.get("qq").getAsLong();
                PermType permType = PermType.valueOf(object.get("permission").getAsString());
                switch (permType) {
                    case MASTER -> newMasters.add(qid);
                    case LITTLE_OWNER -> newLittleOwners.add(qid);
                }
                masters = newMasters;
                littleOwners = newLittleOwners;
            }
            return true;
        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, "permissions.json 加载失败。");
            return false;
        }
    }

    public static boolean isOfficialGroup(long groupId) {
        return officialGroups.contains(groupId);
    }
    public static boolean isVerifiedGroup(long groupId) {
        return unofficialGroups.contains(groupId) | officialGroups.contains(groupId);
    }

}
