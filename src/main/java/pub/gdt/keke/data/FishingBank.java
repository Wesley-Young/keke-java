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
    default void addBait(int amount) { setBaitCount(getBaitCount() + amount); }
    default void subtractBait(int amount) { setBaitCount(getBaitCount() - amount); }
    default void fetchBait(int amount) { subtractBait(1); }

    int getFishCount(FishType type);
    void setFishCount(FishType key, int value);
    default void addFish(FishType key, int amount) { setFishCount(key, getFishCount(key) + amount); }
    default void obtainFish(FishType key) { addFish(key, 1); }
    default void subtractFish(FishType key, int amount) { setFishCount(key, getFishCount(key) - amount); }
    default void fetchFish(FishType key) { subtractFish(key, 1); }
}
