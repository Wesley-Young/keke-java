package pub.gdt.keke.function;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import pub.gdt.keke.PlayerManager;
import pub.gdt.keke.data.BotPlayer;
import pub.gdt.keke.data.FishType;
import pub.gdt.keke.data.FishingBank;
import pub.gdt.keke.probability.ProbabilityModel;
import pub.gdt.keke.probability.WeightedProbabilityModel;

import java.util.Random;
import java.util.function.Consumer;

import static pub.gdt.keke.data.FishType.*;

public final class Fishing {
    private static final Random random = new Random();
    private static final ProbabilityModel<Consumer<GroupMessageEvent>> probabilityModel
            = new WeightedProbabilityModel.Builder<Consumer<GroupMessageEvent>>()
            .build();

    private static final ProbabilityModel<FishType> fishTypeProbabilityModel
            = new WeightedProbabilityModel.Builder<FishType>()
            .put(10, UNDERWEAR)
            .put(10, SHOES)
            .put(10, FROG)
            .put(10, SHELL)
            .put(30, YELLOW_CROAKER)
            .put(10, ELECTRIC_EEL)
            .put(1, OCTOPUS)
            .put(1, CROWN)
            .put(1, DIAMOND_RING)
            .build();
    private static final Long2ObjectMap<LongSet> sessions = new Long2ObjectOpenHashMap<>();
    public static void respondStatusCheck(GroupMessageEvent event) {
        MessageChainBuilder builder = new MessageChainBuilder();
        BotPlayer player = PlayerManager.getPlayer(event.getSender().getId(), event.getGroup().getId());
        builder.add(new At(player.getQID()));
        builder.add(" 你的钓鱼仓库如下：\n");
        builder.add("渔具状况 -" +
                " 鱼竿" + (player.getFishingBank().hasRod() ? "√" : "×") +
                " 鱼线" + (player.getFishingBank().hasThread() ? "√" : "×") +
                " 鱼钩" + (player.getFishingBank().hasHook() ? "√" : "×") +
                "\n");
        StringBuilder fishes = new StringBuilder("鱼库：\n");
        for (FishType type : FishType.values())
            fishes.append(type.getTranslation()).append(": ").append(player.getFishingBank().getFishCount(type)).append('\n');
        fishes.deleteCharAt(fishes.length() - 1);
        builder.add(fishes.toString());
        event.getGroup().sendMessage(builder.build());
    }

    public static synchronized void respondStartFishing(GroupMessageEvent event) {
        MessageChainBuilder builderHead = new MessageChainBuilder();
        BotPlayer player = PlayerManager.getPlayer(event.getSender().getId(), event.getGroup().getId());
        FishingBank fishingBank = player.getFishingBank();
        if (!(fishingBank.hasRod() && fishingBank.hasThread() && fishingBank.hasHook())) {
            builderHead.add(new At(player.getQID()));
            builderHead.add("""
                             你缺少渔具！
                             发送"查看鱼库"以查询渔具状况
                             发送"渔具商店"以查询渔具价格""");
            event.getGroup().sendMessage(builderHead.build());
            return;
        }
        if (player.isFishing()) {
            builderHead.add(new At(player.getQID()));
            builderHead.add(" 你已经在钓鱼了！");
            event.getGroup().sendMessage(builderHead.build());
            return;
        }
        player.setFishingStatus(true);
        builderHead.add(new At(player.getQID()));
        builderHead.add(" 正在钓鱼中…请等待10~30秒！");
        int sleepingInterval = random.nextInt(10000, 30001);
        new Thread(() -> {
            try {
                Thread.sleep(sleepingInterval);
                player.setFishingStatus(false);
                fishingBank.setBaitCount(fishingBank.getBaitCount() - 1);
                probabilityModel.fetchResult().accept(event);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private static void resultNothing(GroupMessageEvent event) {
        MessageChainBuilder builder = new MessageChainBuilder();
        BotPlayer player = PlayerManager.getPlayer(event.getSender().getId(), event.getGroup().getId());
        builder.add(new At(player.getQID()));
        builder.add(" 什么都没钓到");
        event.getGroup().sendMessage(builder.build());
    }

    private static void resultYarn(GroupMessageEvent event) {
        MessageChainBuilder builder = new MessageChainBuilder();
        BotPlayer player = PlayerManager.getPlayer(event.getSender().getId(), event.getGroup().getId());
        builder.add(new At(player.getQID()));
        builder.add(" 钓到了毛线 - 自动转化为19900微壳！");
        player.setMoney(player.getMoney() + 19900);
        event.getGroup().sendMessage(builder.build());
    }

    private static void resultFish(GroupMessageEvent event) {
        MessageChainBuilder builder = new MessageChainBuilder();
        BotPlayer player = PlayerManager.getPlayer(event.getSender().getId(), event.getGroup().getId());
        FishingBank fishingBank = player.getFishingBank();
        FishType fishType = fishTypeProbabilityModel.fetchResult();
        fishingBank.setFishCount(fishType, fishingBank.getFishCount(fishType) + 1);
        builder.add(new At(player.getQID()));
        builder.add(" 钓到" + fishType.getTranslation());
        event.getGroup().sendMessage(builder.build());
    }

    private static void resultBadLuck(GroupMessageEvent event) {
        MessageChainBuilder builder = new MessageChainBuilder();
        BotPlayer player = PlayerManager.getPlayer(event.getSender().getId(), event.getGroup().getId());
        FishingBank fishingBank = player.getFishingBank();
        fishingBank.destroyHook();
        fishingBank.destroyThread();
        builder.add(new At(player.getQID()));
        builder.add(" 起竿的时候碰见霉运，鱼线断了，鱼钩也没了");
        event.getGroup().sendMessage(builder.build());
    }
}
