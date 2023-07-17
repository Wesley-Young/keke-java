package pub.gdt.keke.probability;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 加权概率模型。P(返回某一对象)=该对象的权重/所有对象的权重之和
 * @param <T> {@link ProbabilityModel#fetchResult()} 方法需要返回的对象类型。
 * @see Builder#put(int, Object)
 */
public class WeightedProbabilityModel<T> implements ProbabilityModel<T> {
    private WeightedProbabilityModel(Random random) {
        this.random = random;
    }
    private int capacity = 0;
    private final IntList thresholds = new IntArrayList();
    private final List<T> results = new ArrayList<>();
    private final Random random;

    @Override
    public synchronized T fetchResult() {
        int index = random.nextInt(capacity);
        for (int i = 0; i < thresholds.size() - 1; i++)
            if (thresholds.getInt(i) <= index && index < thresholds.getInt(i + 1))
                return results.get(i);
        return null;
    }

    public static class Builder<T> {
        private final WeightedProbabilityModel<T> result;
        private int capacity = 0;
        public Builder(Random random) {
            result = new WeightedProbabilityModel<>(random);
            result.thresholds.add(0);
        }

        public Builder() {
            this(new Random());
        }

        /**
         * 向 {@link WeightedProbabilityModel} 对象中增加一个对象，并赋予对应的权重。
         * @param weightFactor 给该对象所赋的概率权重
         * @param result 该概率权重所对应的对象
         * @return 对象本身；由此可以进行链式调用
         */
        public Builder<T> put(int weightFactor, T result) {
            capacity += weightFactor;
            this.result.capacity = capacity;
            this.result.thresholds.add(capacity);
            this.result.results.add(result);
            return this;
        }

        public WeightedProbabilityModel<T> build() {
            return result;
        }
    }
}
