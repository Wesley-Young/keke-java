package pub.gdt.keke.function;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import net.mamoe.mirai.contact.AvatarSpec;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public final class WifeFinding {
    static {
        cachedEpochDay = LocalDate.now().toEpochDay();
        caches = new LongObjectHashMap<>();
    }
    private static long cachedEpochDay;
    private static LongObjectMap<Map<NormalMember, NormalMember>> caches;
    private static synchronized NormalMember getDailyWife(Group group, long applicant) {
        long today = LocalDate.now().toEpochDay();
        long groupId = group.getId();
        Map<NormalMember, NormalMember> map;
        if (today == cachedEpochDay) {
            if (caches.containsKey(groupId)) map = caches.get(groupId);
            else {
                List<NormalMember> members = new ArrayList<>(group.getMembers());
                map = generateMap(members, LocalDate.now().toEpochDay() * group.getId());
                caches.put(groupId, map);
            }
        } else {
            cachedEpochDay = today;
            caches = new LongObjectHashMap<>();
            List<NormalMember> members = new ArrayList<>(group.getMembers());
            map = generateMap(members, LocalDate.now().toEpochDay() * group.getId());
            caches.put(groupId, map);
        }
        return map.get(group.get(applicant));
    }
    private static Map<NormalMember, NormalMember> generateMap(List<NormalMember> members, long seed) {
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
            res.put(members.get(members.size() - 1), members.get(members.size() - 1));
        } // 这时候必然出现有一个人没有老婆的现象, 这时候给他分配个自己就是啦
        return res;
    }

    public static void installDailyWifeListener(Group group) {
        GlobalEventChannel.INSTANCE
                .filterIsInstance(GroupMessageEvent.class)
                .filter(event -> event.getGroup().getId() == group.getId())
                .subscribe(GroupMessageEvent.class, event -> {
                    MessageChain message = event.getMessage();
                    long applicantQid = event.getSender().getId();
                    if (message.contentToString().contentEquals("今日老婆")) {
                        try {
                            System.out.println("Message Verified - debug");
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
                            group.sendMessage(builder.build());
                            imageResource.close();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return ListeningStatus.LISTENING;
                });
    }
}
