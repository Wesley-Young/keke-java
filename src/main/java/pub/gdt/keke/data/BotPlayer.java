package pub.gdt.keke.data;

import kotlinx.serialization.json.JsonObject;

public interface BotPlayer {
    /*
     * 微壳 体力 魅力 存款 炸弹 手雷 TNT
     * 性别 对象 奴隶 牛牛长度 小主人身份 戳一戳壳壳的次数 存款数 欠款数
     * 鱼饵 鱼竿 鱼线 鱼钩 内衣 破鞋 青蛙 贝壳 黄鱼 电鳗 章鱼(好好好) 钻戒 皇冠
     * 今日老婆/今时老婆/炸弹/打劫/签到/打搅/击剑/撅/扣/超/刷新时间(牛牛大作战共用一个)
     */
    long getQID();
    long getGroupID();
    boolean isMaster();
    boolean isLittleMaster();

    // Basic Information
    void setSex(int id);
    int getSex(int id);

    void setMoney(int money);
    int getMoney();

    void setStrength(int strength);
    int getStrength();

    void setCharm(int charm);
    int getCharm();

    void setBombCount(int bombCount);
    int getBombCount();

    boolean hasStarAnger();
    void setStarAnger(long qid);
    long getStarAnger();
    void removeStarAnger();

    void tap();
    int tapCount();
    void resetTap();

    NewNew getNewNew();
    FishingBank getFishingBank();
}
