package pub.gdt.keke.function;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import net.mamoe.mirai.contact.AvatarSpec;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MemberJoinEvent;
import net.mamoe.mirai.event.events.MemberLeaveEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public final class WifeFinding {
    static {
        GlobalEventChannel.INSTANCE.filterIsInstance(MemberJoinEvent.class)
                .filter(event -> Config.isVerifiedGroup(event.getGroup().getId()))
                .subscribeAlways(MemberJoinEvent.class, event -> buildDailyWifeCacheAndReturn(event.getGroup()));
        GlobalEventChannel.INSTANCE.filterIsInstance(MemberLeaveEvent.class)
                .filter(event -> Config.isVerifiedGroup(event.getGroup().getId()))
                .subscribeAlways(MemberLeaveEvent.class, event -> buildDailyWifeCacheAndReturn(event.getGroup()));
    }
    private static long cachedEpochDay;
    private static LongObjectMap<Map<NormalMember, NormalMember>> caches;
    static {
        cachedEpochDay = LocalDate.now().toEpochDay();
        caches = new LongObjectHashMap<>();
    }
    private static synchronized NormalMember getDailyWife(Group group, long applicant) {
        long today = LocalDate.now().toEpochDay();
        long groupId = group.getId();
        Map<NormalMember, NormalMember> map;
        if (today == cachedEpochDay) {
            if (caches.containsKey(groupId)) map = caches.get(groupId); // load cache
            else map = buildDailyWifeCacheAndReturn(group);
        } else {
            cachedEpochDay = today;
            caches = new LongObjectHashMap<>();
            map = buildDailyWifeCacheAndReturn(group);
        }
        return map.get(group.get(applicant));
    }
    private static synchronized Map<NormalMember, NormalMember> buildDailyWifeCacheAndReturn(Group source) {
        Map<NormalMember, NormalMember> res = generateMap(source, cachedEpochDay * source.getId());
        caches.put(source.getId(), res);
        return res;
    }
    private static Map<NormalMember, NormalMember> generateMap(Group source, long seed) {
        List<NormalMember> members = new ArrayList<>(source.getMembers());
        Collections.shuffle(members, new Random(seed));
        HashMap<NormalMember, NormalMember> res = new HashMap<>();
        if (members.size() % 2 == 0)
            for (int i = 0; i + 1 < members.size(); i += 2) {
                res.put(members.get(i), members.get(i + 1));
                res.put(members.get(i + 1), members.get(i));
            }
        // 恰好两两成对
        else {
            for (int i = 0; i + 1 < members.size() - 1; i += 2) {
                res.put(members.get(i), members.get(i + 1));
                res.put(members.get(i + 1), members.get(i));
            }
            res.put(members.get(members.size() - 1), source.getBotAsMember());
        } // 必然有一个人分不到老婆，这时候机器人就是他老婆
        return res;
    }

    public static void respondDailyWife(GroupMessageEvent event) {
        Group group = event.getGroup();
        long applicantQid = event.getSender().getId();
        try {
            MessageChainBuilder builder = new MessageChainBuilder();
            builder.add(new At(applicantQid));
            builder.add(" 你今天的群友老婆是: \n");
            NormalMember wife = getDailyWife(group, applicantQid);
            ExternalResource imageResource = ExternalResource.create(
                    new URL(wife.getAvatarUrl(AvatarSpec.LARGEST)).openStream());
            Image avatar = group.uploadImage(imageResource);
            builder.add(avatar);
            String memberName = wife.getNameCard().isEmpty() ? wife.getNick() : wife.getNameCard();
            builder.add(memberName + "(" + wife.getId() + ")");
            imageResource.close();
            group.sendMessage(builder.build());
        } catch (Exception e) {
            group.sendMessage("今日老婆加载失败: " + e);
        }
    }
}
