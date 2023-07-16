package pub.gdt.keke.impl;

import pub.gdt.keke.data.FishType;
import pub.gdt.keke.data.FishingBank;

public class FishingBankImpl implements FishingBank {
    private final BotPlayerImpl parent;
    public FishingBankImpl(BotPlayerImpl player) {
        parent = player;
    }
    @Override
    public boolean hasRod() {
        return Boolean.parseBoolean(parent.data.getProperty("fishing.rod", "false"));
    }

    @Override
    public void buyRod() {
        parent.data.setProperty("fishing.rod", "true");
        parent.saveToFile();
    }

    @Override
    public boolean hasThread() {
        return Boolean.parseBoolean(parent.data.getProperty("fishing.thread", "false"));
    }

    @Override
    public void buyThread() {
        parent.data.setProperty("fishing.thread", "true");
        parent.saveToFile();
    }

    @Override
    public void destroyThread() {
        parent.data.setProperty("fishing.thread", "false");
        parent.saveToFile();
    }

    @Override
    public boolean hasHook() {
        return Boolean.parseBoolean(parent.data.getProperty("fishing.hook", "false"));
    }

    @Override
    public void buyHook() {
        parent.data.setProperty("fishing.hook", "true");
        parent.saveToFile();
    }

    @Override
    public void destroyHook() {
        parent.data.setProperty("fishing.hook", "false");
        parent.saveToFile();
    }

    @Override
    public int getBaitCount() {
        return Integer.parseInt(parent.data.getProperty("fishing.baitCount", "0"));
    }

    @Override
    public void setBaitCount(int baitCount) {
        parent.data.setProperty("fishing.baitCount", String.valueOf(baitCount));
        parent.saveToFile();
    }

    @Override
    public int getFishCount(FishType type) {
        return Integer.parseInt(parent.data.getProperty("fishing.fishCount." + type.name(), "0"));
    }

    @Override
    public void setFishCount(FishType key, int value) {
        parent.data.setProperty("fishing.fishCount." + key.name(), String.valueOf(value));
        parent.saveToFile();
    }
}
