package pub.gdt.keke;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.auth.BotAuthorization;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.utils.BotConfiguration;
import pub.gdt.keke.function.Config;
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
        GlobalEventChannel.INSTANCE.subscribeAlways(BotOnlineEvent.class, event -> {
            Config.installActivationListener();
            Config.installReloadingListener();
            WifeFinding.installWifeCacheRefreshListener();
            WifeFinding.installDailyWifeListener();
        });

        bot.login();
    }
}
