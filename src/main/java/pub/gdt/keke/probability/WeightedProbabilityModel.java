package pub.gdt.keke.probability;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeightedProbabilityModel<T> implements ProbabilityModel<T> {
    private WeightedProbabilityModel() {}
    private int capacity = 0;
    private final IntList thresholds = new IntArrayList();
    private final List<T> results = new ArrayList<>();
    private final Random random = new Random();
    public synchronized T fetchResult() {
        int index = random.nextInt(capacity);
        for (int i = 0; i < thresholds.size() - 1; i++)
            if (thresholds.getInt(i) <= index && index < thresholds.getInt(i + 1))
                return results.get(i);
        return null;
    }

    public static class Builder<T> {
        private final WeightedProbabilityModel<T> result = new WeightedProbabilityModel<>();
        private int capacity = 0;
        public Builder() { result.thresholds.add(0); }
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
