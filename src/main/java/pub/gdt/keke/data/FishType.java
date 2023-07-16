package pub.gdt.keke.data;

public enum FishType {
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
