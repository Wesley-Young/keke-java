package pub.gdt.keke.probability;

import java.util.List;
import java.util.Random;

/**
 * @
 * 简单概率模型。等可能地返回构造器 {@link SimpleProbabilityModel <T>#SimpleProbablityModel(T[], Random)}
 *  中数组 T[] 中的任何一项。
 */
public class SimpleProbabilityModel<T> implements ProbabilityModel<T> {
    private final List<T> results;
    private final Random random;
    public SimpleProbabilityModel(List<T> results, Random random) {
        this.results = results;
        this.random = random;
    }
    public SimpleProbabilityModel(List<T> results) {
        this(results, new Random());
    }
    @Override
    public T fetchResult() {
        return results.get(random.nextInt(results.size()));
    }
}
