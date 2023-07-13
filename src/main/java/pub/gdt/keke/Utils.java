package pub.gdt.keke;

import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;

public class Utils {
    public static EventChannel<GroupMessageEvent> filterBySingleMessage(EventChannel<GroupMessageEvent> channel, String message) {
        return channel.filter(event -> event.getMessage().size() == 1)
                .filter(event -> event.getMessage().get(0).contentEquals(message, false));
    }
}
