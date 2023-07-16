package pub.gdt.keke.impl;

import pub.gdt.keke.data.NewNew;

public final class NewNewImpl implements NewNew {
    private final BotPlayerImpl parent;
    public NewNewImpl(BotPlayerImpl player) {
        parent = player;
    }
    @Override
    public void setLength(int length) {
        parent.data.setProperty("newnew.length", String.valueOf(length));
        parent.data.setProperty("newnew.lastModificationTime", String.valueOf(System.currentTimeMillis()));
        parent.saveToFile();
    }

    @Override
    public int getLength() {
        return Integer.parseInt(parent.data.getProperty("newnew.length", "0"));
    }

    @Override
    public long getLastModificationTime() {
        return Integer.parseInt(parent.data.getProperty("newnew.lastModificationTime", "0"));
    }
}
