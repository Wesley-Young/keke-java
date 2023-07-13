package pub.gdt.keke.function;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import pub.gdt.keke.data.BotPlayer;
import pub.gdt.keke.data.FishingBank;

public final class Fishing {
    public static MessageChain checkStatus(BotPlayer player) {
        MessageChainBuilder builder = new MessageChainBuilder();
        builder.add(new At(player.getQID()));
        builder.add("\n");
        builder.add("渔具状况：");
        return builder.build();
    }
    public static void fish(Group group, BotPlayer player) {
        FishingBank bank = player.getFishingBank();
    }
}
