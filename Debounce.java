import java.util.concurrent.*;

/**
 * 我们可以使用ScheduledExecutorService的schedule方法实现
 * 为每个任务分配一个唯一的 key，
 * 使用ConcurrentHashMap来存储 key-future,
 * 使用future的 cancel 方法来取消线程任务
 */
public class Debounce {
    private static final ScheduledExecutorService SCHEDULE = Executors.newSingleThreadScheduledExecutor();

    private static final ConcurrentHashMap<Object, Future<?>> DELAYED_MAP = new ConcurrentHashMap<>();

    /**
     * 抖动函数
     */
    public static void debounce(final Object key, final Runnable runnable, long delay, TimeUnit unit) {
        // concurrent hashmap
        // 若put成功返回null
        // put不成功返回旧值 说明之前任务还没执行完
        final Future<?> prev = DELAYED_MAP.put(key, SCHEDULE.schedule(() -> {
            try {
                runnable.run();
            } finally {
                // 运行完后从map中移除
                DELAYED_MAP.remove(key);
            }
        }, delay, unit));

//        System.out.println("prev 状态 " + prev);

        // 如果之前的任务还没运行，则取消任务
        if (prev != null) {
            prev.cancel(true);
        }
    }

    public static void shutdown() {
        SCHEDULE.shutdownNow();
    }

    public static void main(String[] args) {
        Debounce.debounce("1", () -> {
            System.out.println(11);
        }, 3, TimeUnit.SECONDS);

        Debounce.debounce("1", () -> {
            System.out.println(11);
        }, 3, TimeUnit.SECONDS);

        Debounce.debounce("1", () -> {
            System.out.println(11);
        }, 3, TimeUnit.SECONDS);

        Debounce.debounce("2", () -> {
            System.out.println(22);
        }, 3, TimeUnit.SECONDS);

        Debounce.debounce("2", () -> {
            System.out.println(22);
        }, 3, TimeUnit.SECONDS);

        Debounce.debounce("2", () -> {
            System.out.println(22);
        }, 3, TimeUnit.SECONDS);
    }
}
