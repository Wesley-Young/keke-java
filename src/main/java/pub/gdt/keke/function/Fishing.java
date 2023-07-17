package pub.gdt.keke.function;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import pub.gdt.keke.PlayerManager;
import pub.gdt.keke.Utils;
import pub.gdt.keke.data.BotPlayer;
import pub.gdt.keke.data.FishType;
import pub.gdt.keke.data.FishingBank;
import pub.gdt.keke.probability.ProbabilityModel;
import pub.gdt.keke.probability.WeightedProbabilityModel;

import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static pub.gdt.keke.data.FishType.*;

public final class Fishing {
    public static final int ROD_PRICE = 150000;
    public static final int THREAD_PRICE = 100000;
    public static final int HOOK_PRICE = 50000;
    public static final int BAIT_PRICE = 10000;

    private static final Random randomForStrength = new Random();
    private static final Random randomForFishPrice = new Random();
    private static final ProbabilityModel<Consumer<GroupMessageEvent>> probabilityModel
            = new WeightedProbabilityModel.Builder<Consumer<GroupMessageEvent>>()
            .put(4, Fishing::resultNothing)
            .put(5, Fishing::resultYarn)
            .put(15, Fishing::resultFish)
            .put(1, Fishing::resultBadLuck)
            .build();

    private static final ProbabilityModel<FishType> fishTypeProbabilityModel
            = new WeightedProbabilityModel.Builder<FishType>()
            .put(10, UNDERWEAR)
            .put(10, SHOES)
            .put(10, FROG)
            .put(10, SHELL)
            .put(20, YELLOW_CROAKER)
            .put(10, ELECTRIC_EEL)
            .put(1, OCTOPUS) // 怎么能那么容易就让你们钓到章鱼
            .put(10, WHALE)
            .put(10, CROWN)
            .put(10, DIAMOND_RING)
            .build();

    public static void respondStatusCheck(GroupMessageEvent event) {
        MessageChainBuilder builder = new MessageChainBuilder();
        BotPlayer player = PlayerManager.getPlayer(event.getSender().getId(), event.getGroup().getId());
        FishingBank fishingBank = player.getFishingBank();
        builder.add(new At(player.getQID()));
        builder.add(" 你的钓鱼仓库如下：\n");
        builder.add("渔具状况 -" +
                " 鱼竿" + (fishingBank.hasRod() ? "√" : "×") +
                " 鱼线" + (fishingBank.hasThread() ? "√" : "×") +
                " 鱼钩" + (fishingBank.hasHook() ? "√" : "×") +
                " 鱼饵：" + fishingBank.getBaitCount() + 
                "\n发送【渔具商店】以查询渔具价格\n");
        StringBuilder fishes = new StringBuilder("鱼库：\n");
        for (FishType type : FishType.values())
            fishes.append("当前")
                  .append(type.getTranslation()).append("：")
                  .append(fishingBank.getFishCount(type)).append('\n');
        builder.add(fishes.toString());
        builder.add("发送【卖鱼 <卖鱼种类>】以卖掉1条所钓的鱼");
        event.getGroup().sendMessage(builder.build());
    }

    public static synchronized void respondStartFishing(GroupMessageEvent event) {
        MessageChainBuilder builder = new MessageChainBuilder();
        BotPlayer player = PlayerManager.getPlayer(event.getSender().getId(), event.getGroup().getId());
        FishingBank fishingBank = player.getFishingBank();
        if (!(fishingBank.hasRod() && fishingBank.hasThread() && fishingBank.hasHook() && fishingBank.getBaitCount() > 0)) {
            builder.add(new At(player.getQID()));
            builder.add("""
                         你缺少渔具！
                        发送【查看鱼库】以查询渔具状况和钓到的鱼
                        发送【渔具商店】以查询渔具价格""");
            event.getGroup().sendMessage(builder.build());
            return;
        }
        if (player.isFishing()) {
            builder.add(new At(player.getQID()));
            builder.add(" 你已经在钓鱼了！");
            event.getGroup().sendMessage(builder.build());
            return;
        }
        int strengthNeeded = randomForStrength.nextInt(20, 100);
        builder.add(new At(player.getQID()));
        builder.add(Utils.checkStrengthAndDo(player, strengthNeeded, aPlayer -> aPlayer.setFishingStatus(true)) ?
                    " 你的体力不足以钓鱼！" :
                    " 消耗 " + strengthNeeded + " 体力！\n" +
                    "钓鱼中……请等待一段时间");
        event.getGroup().sendMessage(builder.build());
        int sleepingInterval = randomForStrength.nextInt(10000, 30001);
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
        builder.add(" 钓到了毛线 - 自动转化为 19900 微壳！");
        player.addMoney(19900);
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
        builder.add("""
                     起竿的时候碰见霉运，鱼线断了，鱼钩也没了
                    请重新购买！""");
        event.getGroup().sendMessage(builder.build());
    }

    public static void respondToolShop(GroupMessageEvent event) {
        MessageChainBuilder builder = new MessageChainBuilder();
        builder.add("钓鱼 - 渔具商店\n" +
                    "鱼竿 " + ROD_PRICE + " 微壳\n" +
                    "鱼线 " + THREAD_PRICE + " 微壳\n" +
                    "鱼钩 " + HOOK_PRICE + " 微壳\n" +
                    "鱼饵 " + BAIT_PRICE + " 微壳/个\n" +
                    """
                    发送【购买渔具 <所需渔具>】以购买渔具
                    发送【购买渔具 鱼饵 [鱼饵数量|默认1]】以购买鱼饵""");
        event.getGroup().sendMessage(builder.build());
    }

    public static void respondBuyingTools(GroupMessageEvent event) {
        MessageChainBuilder builder = new MessageChainBuilder();
        BotPlayer player = PlayerManager.getPlayer(event.getSender().getId(), event.getGroup().getId());
        builder.add(new At(player.getQID()));
        String[] flags = event.getMessage().contentToString().split(" ", 3);
        if (flags.length <= 1)
            builder.add("""
                        发送【购买渔具 <所需渔具>】以购买渔具
                        发送【购买渔具 鱼饵 [鱼饵数量|默认1]】以购买鱼饵""");
        else switch (flags[1]) {
            case "鱼竿" -> builder.add(" " + Utils.buyWithMessage(player, ROD_PRICE,
                    aPlayer -> aPlayer.getFishingBank().buyRod()));
            case "鱼线" -> builder.add(" " + Utils.buyWithMessage(player, THREAD_PRICE,
                    aPlayer -> aPlayer.getFishingBank().buyThread()));
            case "鱼钩" -> builder.add(" " + Utils.buyWithMessage(player, HOOK_PRICE,
                    aPlayer -> aPlayer.getFishingBank().buyHook()));
            case "鱼饵" -> {
                try {
                    int amount = flags.length == 2 ? 1 : Integer.parseInt(flags[2]);
                    if (amount <= 0) builder.add(" 要买就买，不买滚，别在这理发店");
                    else builder.add(" " + Utils.buyWithMessage(player, HOOK_PRICE * amount,
                            aPlayer -> aPlayer.getFishingBank().setBaitCount(player.getFishingBank().getBaitCount() + amount)));
                } catch (NumberFormatException e) {
                    builder.add(" 要买就买，不买滚，别在这理发店");
                }
            }
            default -> builder.add("""
                                   发送【购买渔具 <所需渔具>】以购买渔具
                                   发送【购买渔具 鱼饵 [鱼饵数量|默认1]】以购买鱼饵""");
        }
        event.getGroup().sendMessage(builder.build());
    }

    public static void respondSellingFish(GroupMessageEvent event) {
        MessageChainBuilder builder = new MessageChainBuilder();
        BotPlayer player = PlayerManager.getPlayer(event.getSender().getId(), event.getGroup().getId());
        FishingBank fishingBank = player.getFishingBank();
        builder.add(new At(player.getQID()));
        String[] flags = event.getMessage().contentToString().split(" ", 2);
        if (flags.length <= 1)
            builder.add(" 发送【卖鱼 <卖鱼种类>】以卖掉1条所钓的鱼");
        Stream.of(FishType.values()).filter(type -> type.getTranslation().contentEquals(flags[1])).findAny()
                .ifPresentOrElse(
                        type -> builder.add(" " + type.performActionOn(player)),
                        () -> builder.add(" 发送【卖鱼 <卖鱼种类>】以卖掉1条所钓的鱼")
                );
        event.getGroup().sendMessage(builder.build());
    }
}
