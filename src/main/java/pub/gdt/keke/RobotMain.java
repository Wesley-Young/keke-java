package pub.gdt.keke;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.auth.BotAuthorization;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.MessageSyncEvent;
import net.mamoe.mirai.utils.BotConfiguration;
import pub.gdt.keke.function.WifeFinding;
import xyz.cssxsh.mirai.tool.FixProtocolVersion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RobotMain {
    private static Bot bot;
    public static Bot getBotInstance() { return bot; }

    public static void main(String[] args) throws IOException {
        long qq = 3446149635L;
        if (args.length == 1) qq = Long.parseLong(args[0]);

        FixProtocolVersion.update();
        Path deviceInfoJson = Path.of("device.json");
        if (Files.notExists(deviceInfoJson)) {
            bot.getLogger().info("Device info not found. Initializing...");
            bot = BotFactory.INSTANCE.newBot(qq, BotAuthorization.byQRCode(), configuration -> {
                configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_WATCH);
                configuration.fileBasedDeviceInfo();
            });
        } else {
            bot.getLogger().info("device.json found. Loading...");
            String deviceInfo = Files.readString(deviceInfoJson);
            bot = BotFactory.INSTANCE.newBot(qq, BotAuthorization.byQRCode(), configuration -> {
                configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_WATCH);
                configuration.loadDeviceInfoJson(deviceInfo);
            });
        }

        // Install event listeners
        GlobalEventChannel.INSTANCE.subscribeAlways(BotOnlineEvent.class, event -> {
            installClosingListener();
            for (Group each : bot.getGroups()) {
                WifeFinding.installDailyWifeListener(each);
            }
        });

        bot.login();
    }

    private static void installClosingListener() {
        GlobalEventChannel.INSTANCE.subscribeOnce(MessageSyncEvent.class, event -> {
            bot.close();
        });
    }
}
