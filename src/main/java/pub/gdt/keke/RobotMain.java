package pub.gdt.keke;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.auth.BotAuthorization;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.utils.BotConfiguration;
import pub.gdt.keke.function.Config;
import pub.gdt.keke.function.Fishing;
import pub.gdt.keke.function.WifeFinding;
import xyz.cssxsh.mirai.tool.FixProtocolVersion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RobotMain {
    private static Bot bot;
    public static final EventChannel<GroupMessageEvent> ALL_GROUP_EVENT_CHANNEL
            = GlobalEventChannel.INSTANCE.filterIsInstance(GroupMessageEvent.class)
                    .filter(event -> Config.isVerifiedGroup(event.getGroup().getId()));
    public static final EventChannel<GroupMessageEvent> ACTIVE_GROUP_EVENT_CHANNEL
            = ALL_GROUP_EVENT_CHANNEL.filter(event -> Config.isActive(event.getGroup().getId()));
    public static final EventChannel<GroupMessageEvent> LITTLE_OWNER_EVENT_CHANNEL
            = ACTIVE_GROUP_EVENT_CHANNEL.filter(event -> Config.isLittleOwner(event.getSender().getId()));
    public static final EventChannel<GroupMessageEvent> MASTER_EVENT_CHANNEL
            = ACTIVE_GROUP_EVENT_CHANNEL.filter(event -> Config.isMaster(event.getSender().getId()));
    public static final EventChannel<GroupMessageEvent> LITTLE_OWNER_OPERATING_EVENT_CHANNEL
            = ALL_GROUP_EVENT_CHANNEL.filter(event -> Config.isLittleOwner(event.getSender().getId()));
    public static final EventChannel<GroupMessageEvent> MASTER_OPERATING_EVENT_CHANNEL
            = ALL_GROUP_EVENT_CHANNEL.filter(event -> Config.isMaster(event.getSender().getId()));

    public static Bot getBotInstance() { return bot; }

    public static void main(String[] args) throws IOException {
        long qq = 3446149635L;
        if (args.length == 1) qq = Long.parseLong(args[0]);

        FixProtocolVersion.update();
        Path deviceInfoJson = Path.of("device.json");
        if (Files.notExists(deviceInfoJson)) {
            System.out.println("Device info not found. Initializing...");
            bot = BotFactory.INSTANCE.newBot(qq, BotAuthorization.byQRCode(), configuration -> {
                configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_WATCH);
                configuration.fileBasedDeviceInfo();
            });
        } else {
            System.out.println("device.json found. Loading...");
            String deviceInfo = Files.readString(deviceInfoJson);
            bot = BotFactory.INSTANCE.newBot(qq, BotAuthorization.byQRCode(), configuration -> {
                configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_WATCH);
                configuration.loadDeviceInfoJson(deviceInfo);
            });
        }

        // Install event listeners
        Utils.mapping("今日老婆", WifeFinding::respondDailyWife, ACTIVE_GROUP_EVENT_CHANNEL);

        Utils.mapping("查看鱼库", Fishing::respondStatusCheck, ACTIVE_GROUP_EVENT_CHANNEL);
        Utils.mapping("钓鱼", Fishing::respondStartFishing, ACTIVE_GROUP_EVENT_CHANNEL);
        Utils.mapping("渔具商店", Fishing::respondToolShop, ACTIVE_GROUP_EVENT_CHANNEL);
        Utils.mapping(message -> message.startsWith("购买渔具 "), Fishing::respondBuyingTools, ACTIVE_GROUP_EVENT_CHANNEL);
        Utils.mapping(message -> message.startsWith("卖鱼 "), Fishing::respondSellingFish, ACTIVE_GROUP_EVENT_CHANNEL);

        Utils.mapping("壳壳开机", Config::respondActivation, LITTLE_OWNER_OPERATING_EVENT_CHANNEL);
        Utils.mapping("壳壳关机", Config::respondDeactivation, LITTLE_OWNER_OPERATING_EVENT_CHANNEL);
        Utils.mapping("重载配置", Config::respondReload, MASTER_OPERATING_EVENT_CHANNEL);

        bot.login();
    }
}
