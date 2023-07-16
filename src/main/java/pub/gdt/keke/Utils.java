package pub.gdt.keke;

import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.function.Consumer;

public class Utils {
    public static EventChannel<GroupMessageEvent> filterBySingleMessage(EventChannel<GroupMessageEvent> channel, String message) {
        return channel.filter(event -> event.getMessage().size() == 2)
                .filter(event -> event.getMessage().get(1).contentEquals(message, false));
    }

    public static void mapping(String message, Consumer<GroupMessageEvent> response, EventChannel<GroupMessageEvent> channel) {
        filterBySingleMessage(channel, message).subscribeAlways(GroupMessageEvent.class, response);
    }
}
