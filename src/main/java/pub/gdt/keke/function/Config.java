package pub.gdt.keke.function;

import it.unimi.dsi.fastutil.longs.Long2BooleanArrayMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import pub.gdt.keke.RobotMain;
import pub.gdt.keke.Utils;

import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Config {
    private static final Path config = Path.of("bot_config");
    private static final Path littleOwnersFile = config.resolve("little_owners");
    private static final Path groupsFile = config.resolve("groups");
    private static final LongSet masters = LongSet.of(1453973332L, 2958925805L, 2040582847L);
    private static LongSet littleOwners = new LongOpenHashSet();
    private static final Long2BooleanMap officialGroups = new Long2BooleanArrayMap();
    private static Long2BooleanMap unofficialGroups = new Long2BooleanArrayMap();

    // Initialize
    static {
        try {
            Scanner in = new Scanner(littleOwnersFile);
            while (in.hasNext()) littleOwners.add(in.nextLong());
            in.close();
        } catch (Exception e) {
            RobotMain.getBotInstance().close();
            Logger.getGlobal().log(Level.SEVERE, "little_owners 加载失败。");
            System.exit(1);
        }

        try {
            officialGroups.put(666879777L, true);
            officialGroups.put(584539924L, true);
            Scanner in = new Scanner(groupsFile);
            while (in.hasNext()) unofficialGroups.put(in.nextLong(), false);
            in.close();
        } catch (Exception e) {
            RobotMain.getBotInstance().close();
            Logger.getGlobal().log(Level.SEVERE, "groups 加载失败。");
            System.exit(1);
        }
    }

    public static boolean isMaster(long qid) {
        return masters.contains(qid);
    }
    public static boolean isLittleOwner(long qid) {
        return littleOwners.contains(qid) | masters.contains(qid);
    }
    public static boolean reloadLittleOwners() {
        try {
            Scanner in = new Scanner(littleOwnersFile);
            LongSet newLittleOwners = new LongOpenHashSet();
            while (in.hasNext()) newLittleOwners.add(in.nextLong());
            in.close();
            littleOwners = newLittleOwners;
            return true;
        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, "little_owners 加载失败。");
            return false;
        }
    }

    public static boolean isOfficialGroup(long groupId) {
        return officialGroups.containsKey(groupId);
    }
    public static boolean isVerifiedGroup(long groupId) {
        return unofficialGroups.containsKey(groupId) | officialGroups.containsKey(groupId);
    }
    public static boolean isActive(long groupId) {
        return unofficialGroups.get(groupId) | officialGroups.get(groupId);
    }
    public static void activate(long groupId) {
        if (unofficialGroups.containsKey(groupId))
            unofficialGroups.put(groupId, true);
        else if (officialGroups.containsKey(groupId))
            officialGroups.put(groupId, true);
    }
    public static void deactivate(long groupId) {
        if (unofficialGroups.containsKey(groupId))
            unofficialGroups.put(groupId, false);
        else if (officialGroups.containsKey(groupId))
            officialGroups.put(groupId, false);
    }
    public static boolean reloadGroups() {
        try {
            Scanner in = new Scanner(groupsFile);
            Long2BooleanMap newUnofficialGroups = new Long2BooleanArrayMap();
            while (in.hasNext()) {
                long groupId = in.nextLong();
                newUnofficialGroups.put(groupId,
                        unofficialGroups.containsKey(groupId) && unofficialGroups.get(groupId));
            }
            in.close();
            unofficialGroups = newUnofficialGroups;
            return true;
        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, "groups 加载失败。");
            return false;
        }
    }

    public static void installActivationListener() {
        EventChannel<GroupMessageEvent> allGroupLittleOwnerChannel
                = RobotMain.ALL_GROUP_EVENT_CHANNEL.filter(event -> isLittleOwner(event.getSender().getId()));
        Utils.filterBySingleMessage(allGroupLittleOwnerChannel, "壳壳开机")
                .subscribeAlways(GroupMessageEvent.class, event -> {
                    activate(event.getGroup().getId());
                    event.getGroup().sendMessage("开机成功！");
                });
        Utils.filterBySingleMessage(allGroupLittleOwnerChannel, "壳壳关机")
                .subscribeAlways(GroupMessageEvent.class, event -> {
                    deactivate(event.getGroup().getId());
                    event.getGroup().sendMessage("关机成功！");
                });
    }

    public static void installReloadingListener() {
        Utils.filterBySingleMessage(RobotMain.MASTER_EVENT_CHANNEL, "重载配置")
                .subscribeAlways(GroupMessageEvent.class, event -> event.getGroup().sendMessage(
                            (reloadLittleOwners() ? "重载小主人列表 - 成功\n" : "重载小主人列表 - 失败\n") +
                            (reloadGroups() ? "重载群列表 - 成功" : "重载群列表 - 失败")
                    ));
    }
}
