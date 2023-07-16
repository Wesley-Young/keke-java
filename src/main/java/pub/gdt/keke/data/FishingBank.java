package pub.gdt.keke.data;

public interface FishingBank {
    boolean hasRod();
    void buyRod();

    boolean hasThread();
    void buyThread();
    void destroyThread();

    boolean hasHook();
    void buyHook();
    void destroyHook();

    int getBaitCount();
    void setBaitCount(int bait);

    int getFishCount(FishType type);
    void setFishCount(FishType key, int value);
}
