package pub.gdt.keke.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import pub.gdt.keke.data.BotPlayer;
import pub.gdt.keke.data.FishingBank;
import pub.gdt.keke.data.NewNew;
import pub.gdt.keke.data.SexType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

public final class BotPlayerImpl implements BotPlayer {
    private static final URL sexTypesJson = BotPlayerImpl.class.getClassLoader().getResource("pub/gdt/keke/sex_types.json");
    private static final ArrayList<SexType> sexTypes = new ArrayList<>();

    static {
        try {
            JsonArray sexTypesJsonArray = JsonParser.parseReader(
                    new InputStreamReader(Objects.requireNonNull(sexTypesJson).openStream())).getAsJsonArray();
            for (JsonElement element : sexTypesJsonArray) {
                JsonObject sexTypeJsonObject = element.getAsJsonObject();
                sexTypes.add(new SexType(
                        sexTypeJsonObject.get("name").getAsString(),
                        sexTypeJsonObject.get("translation").getAsString(),
                        sexTypeJsonObject.get("description").getAsString()
                ));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private final long qid, groupId;
    private final Path dataPath;
    private final NewNew newnew;
    private final FishingBank fishingBank;
    private boolean fishingStatus = false;
    final Properties data = new Properties();
    public BotPlayerImpl(long qid, long groupId, Path root) {
        this.qid = qid;
        this.groupId = groupId;
        dataPath = root.resolve(String.valueOf(groupId)).resolve(qid + ".properties");
        newnew = new NewNewImpl(this);
        fishingBank = new FishingBankImpl(this);
        try {
            InputStream dataStream = Files.newInputStream(dataPath);
            data.load(dataStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getQID() {
        return qid;
    }

    @Override
    public long getGroupId() {
        return groupId;
    }

    @Override
    public void setSex(int id) {
        data.setProperty("sex", String.valueOf(id));
        saveToFile();
    }

    @Override
    public int getSex() {
        return Integer.parseInt(data.getProperty("sex", "0"));
    }

    @Override
    public void setMoney(int money) {
        data.setProperty("money", String.valueOf(money));
        saveToFile();
    }

    @Override
    public int getMoney() {
        return Integer.parseInt(data.getProperty("money", "100000000" /* debug */));
    }

    @Override
    public void setStrength(int strength) {
        data.setProperty("strength", String.valueOf(strength));
        saveToFile();
    }

    @Override
    public int getStrength() {
        return Integer.parseInt(data.getProperty("strength", "10000000" /* debug */));
    }

    @Override
    public void setCharm(int charm) {
        data.setProperty("charm", String.valueOf(charm));
        saveToFile();
    }

    @Override
    public int getCharm() {
        return Integer.parseInt(data.getProperty("charm", "10000000" /* debug */));
    }

    @Override
    public void setBombCount(int bombCount) {
        data.setProperty("bombCount", String.valueOf(bombCount));
        saveToFile();
    }

    @Override
    public int getBombCount() {
        return Integer.parseInt(data.getProperty("bombCount", "1000000" /* debug */));
    }

    @Override
    public int tap() {
        int newTapCount = getTapCount() + 1;
        data.setProperty("tapCount", String.valueOf(newTapCount));
        saveToFile();
        return newTapCount;
    }

    @Override
    public int getTapCount() {
        return Integer.parseInt(data.getProperty("tapCount", "0"));
    }

    @Override
    public void resetTap() {
        data.setProperty("tapCount", "0");
        saveToFile();
    }

    @Override
    public NewNew getNewNew() {
        return newnew;
    }

    @Override
    public FishingBank getFishingBank() {
        return fishingBank;
    }

    @Override
    public void setFishingStatus(boolean status) {
        fishingStatus = status;
    }

    @Override
    public boolean isFishing() {
        return fishingStatus;
    }

    synchronized void saveToFile() {
        try {
            data.store(Files.newOutputStream(dataPath), "Player data");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
