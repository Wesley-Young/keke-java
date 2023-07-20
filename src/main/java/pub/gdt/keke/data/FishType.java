package pub.gdt.keke.data;

import pub.gdt.keke.Utils;
import pub.gdt.keke.probability.SimpleProbabilityModel;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.IntSupplier;

public enum FishType {
    UNDERWEAR("内衣", 10, randomPrice(8000, 9000)),
    SHOES("破鞋", 10, randomPrice(2000, 5000)),
    FROG("青蛙", 10, randomPrice(38000, 45000)),
    SHELL("贝壳", 10, randomPrice(15000, 20000)),
    YELLOW_CROAKER("黄鱼", 20, randomPrice(50000, 60000)),
    ELECTRIC_EEL("电鳗", 10, player -> new SimpleProbabilityModel<>(
            List.of(
                    actionObtainingMoney(randomPrice(100000, 180000)),
                    aPlayer -> {
                        aPlayer.subtractMoney(68800);
                        return "你卖鱼的时候不小心被电鳗电伤了，损失了 68800 微壳";
                        // 后续加入电晕
                    }
            )
    ).fetchResult().apply(player)),
    OCTOPUS("章鱼(好好好)", 1, randomPrice(58000, 65000)),
    WHALE("鲸鱼",10, 100000),
    CROWN("皇冠",10, 300000),
    DIAMOND_RING("钻戒", 10, player -> {
        player.addMoney(180000);
        int charm = randomPrice(50, 150).getAsInt();
        player.addCharm(charm);
        return "卖鱼成功！获得 180000 微壳和 " + charm + " 魅力";
    });

    private final String translation;
    private final int probabilityWeight;
    private final Function<BotPlayer, String> action;

    private static IntSupplier randomPrice(int origin, int boundInclusive) {
        return Utils.randomIntSupplier(new Random(), origin, boundInclusive + 1);
    }

    private static Function<BotPlayer, String> actionObtainingMoney(IntSupplier priceSupplier) {
        return player -> {
            int price = priceSupplier.getAsInt();
            player.addMoney(price);
            return "卖鱼成功！获得 " + price + " 微壳";
        };
    }

    FishType(String translation, int probabilityWeight, Function<BotPlayer, String> action) {
        this.translation = translation;
        this.probabilityWeight = probabilityWeight;
        this.action = action;
    }

    FishType(String translation, int probabilityWeight, IntSupplier priceSupplier) {
        this(translation, probabilityWeight, actionObtainingMoney(priceSupplier));
    }

    FishType(String translation, int probabilityWeight, int price) {
        this(translation, probabilityWeight, () -> price);
    }

    public String getTranslation() { return translation; }
    public int getProbabilityWeight() { return probabilityWeight; }
    public String performActionOn(BotPlayer player) {
        return action.apply(player);
    }
}
