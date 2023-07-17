package pub.gdt.keke.probability;

/**
 * 概率模型的基接口。
 * @param <T> {@link ProbabilityModel#fetchResult()} 方法需要返回的对象类型。
 */
public interface ProbabilityModel<T> {
    T fetchResult();
}
