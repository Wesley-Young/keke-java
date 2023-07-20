package pub.gdt.keke;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import pub.gdt.keke.data.BotPlayer;
import pub.gdt.keke.impl.BotPlayerImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PlayerManager {
    private static final Long2ObjectMap<Long2ObjectMap<BotPlayer>> groupMap = new Long2ObjectOpenHashMap<>();
    private static final Path root = Path.of("data", "profile");
    static {
        if (Files.notExists(root)) {
            try {
                Files.createDirectories(root);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static BotPlayer getPlayer(long qid, long groupId) {
        Path groupRoot = root.resolve(String.valueOf(groupId));
        Path playerData = groupRoot.resolve(qid + ".properties");
        try {
            if (Files.notExists(groupRoot)) Files.createDirectory(groupRoot);
            if (Files.notExists(playerData)) Files.createFile(playerData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!groupMap.containsKey(groupId)) groupMap.put(groupId, new Long2ObjectOpenHashMap<>());
        Long2ObjectMap<BotPlayer> subMap = groupMap.get(groupId);
        if (!subMap.containsKey(qid)) subMap.put(qid, new BotPlayerImpl(qid, groupId, root));
        return subMap.get(qid);
    }
}
