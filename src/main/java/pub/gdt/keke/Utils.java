package pub.gdt.keke;

import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import pub.gdt.keke.data.BotPlayer;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class Utils {
    public static EventChannel<GroupMessageEvent> filterBySingleMessage(EventChannel<GroupMessageEvent> channel, String message) {
        return channel.filter(event -> event.getMessage().size() == 2)
                .filter(event -> event.getMessage().get(1).contentEquals(message, false));
    }

    public static EventChannel<GroupMessageEvent> filterByPredicate(EventChannel<GroupMessageEvent> channel, Predicate<String> predicate) {
        return channel.filter(event -> event.getMessage().size() == 2)
                .filter(event -> predicate.test(event.getMessage().get(1).contentToString()));
    }

    public static EventChannel<GroupMessageEvent> filterByRegex(EventChannel<GroupMessageEvent> channel, Pattern pattern) {
        return channel.filter(event -> event.getMessage().size() == 2)
                .filter(event -> pattern.matcher(event.getMessage().get(1).contentToString()).matches());
    }

    public static void mapping(String message, Consumer<GroupMessageEvent> response, EventChannel<GroupMessageEvent> channel) {
        filterBySingleMessage(channel, message).subscribeAlways(GroupMessageEvent.class, response);
    }

    public static void mapping(Predicate<String> predicate, Consumer<GroupMessageEvent> response, EventChannel<GroupMessageEvent> channel) {
        filterByPredicate(channel, predicate).subscribeAlways(GroupMessageEvent.class, response);
    }

    public static void mappingByRegex(String regex, Consumer<GroupMessageEvent> response, EventChannel<GroupMessageEvent> channel) {
        filterByRegex(channel, Pattern.compile(regex)).subscribeAlways(GroupMessageEvent.class, response);
    }

    public static IntSupplier randomIntSupplier(Random random, int origin, int exclusiveBound) {
        return () -> random.nextInt(origin, exclusiveBound);
    }

    public static boolean checkAndBuy(BotPlayer player, int price, Consumer<BotPlayer> ifSuccess) {
        if (player.getMoney() < price) return false;
        else {
            player.subtractMoney(price);
            ifSuccess.accept(player);
            return true;
        }
    }
    public static String buyWithMessage(BotPlayer player, int price, Consumer<BotPlayer> ifSuccess) {
        return checkAndBuy(player, price, ifSuccess) ? "购买成功！" : "你的微壳不足以购买！";
    }

    public static boolean checkStrengthAndDo(BotPlayer player, int amount, Consumer<BotPlayer> ifSuccess) {
        if (player.getStrength() < amount) return false;
        else {
            player.subtractStrength(amount);
            ifSuccess.accept(player);
            return true;
        }
    }
}
