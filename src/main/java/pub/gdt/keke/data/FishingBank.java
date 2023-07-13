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

    int baitCount();
    void setBait(int bait);

    enum FishType {
        UNDERWEAR("内衣"), SHOES("鞋子"),
        FROG("青蛙"), SHELL("贝壳"),
        YELLOW_CROAKER("黄鱼"), ELECTRIC_EEL("电鳗"), OCTOPUS("章鱼(好好好)"),
        CROWN("皇冠"), DIAMOND_RING("钻戒");
        private final String translation;
        FishType(String translation) {
            this.translation = translation;
        }
        public String getTranslation() {
            return translation;
        }
    }

    int getFishCount(FishType type);
    void setFishCount(FishType key, int value);
}
