package top.brightsunshine.localcache.core.expire;

import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.cacheInterface.ICacheExpire;
import top.brightsunshine.localcache.cacheInterface.ICacheRemoveListener;
import top.brightsunshine.localcache.core.entry.TimeWheelNode;
import top.brightsunshine.localcache.core.listener.remove.CacheRemoveConstant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static top.brightsunshine.localcache.core.constant.TimeWheelConstant.*;

public class CacheExpireTimeWheel<K, V> implements ICacheExpire<K, V> {
    /**
     * 缓存
     */
    private ICache<K, V> cache;

    /**
     * 默认的删除日志
     */
    private String removeLog = "remove.log";

    /**
     * 删除监听器
     */
    private ICacheRemoveListener<K, V> removeListener;

    /**
     * 毫秒级时间轮(100ms)
     */
    private TimeWheelNode<K, V>[] milliSeconds = new TimeWheelNode[10];

    /**
     * 秒级时间轮(1s)
     */
    private TimeWheelNode<K, V>[] seconds = new TimeWheelNode[60];

    /**
     * 分级时间轮(1m)
     */
    private TimeWheelNode<K, V>[] minutes = new TimeWheelNode[60];

    /**
     * 时级时间轮(1h)
     */
    private TimeWheelNode<K, V>[] hours = new TimeWheelNode[24];

    /**
     * 天级时间轮
     */
    private TimeWheelNode<K, V>[] days = new TimeWheelNode[7];

    /**
     * key和其所在的时间轮的位置的映射
     */
    private Map<K, TimeWheelNode<K, V>> keyToTimeWheelNode = new HashMap<>();

    long milli = 100;
    long second;
    long minute;
    long hour;
    long day;

    {
        second = milli * 10;
        minute = second * 60;
        hour = minute * 60;
        day = hour * 24;
    }

    /**
     * 定期执行类
     */
    private static final ScheduledExecutorService ROUND_ROBIN_MILLI = Executors.newSingleThreadScheduledExecutor();
    private static final ScheduledExecutorService ROUND_ROBIN_SEC = Executors.newSingleThreadScheduledExecutor();
    private static final ScheduledExecutorService ROUND_ROBIN_MIN = Executors.newSingleThreadScheduledExecutor();
    private static final ScheduledExecutorService ROUND_ROBIN_HOUR = Executors.newSingleThreadScheduledExecutor();
    private static final ScheduledExecutorService ROUND_ROBIN_DAY = Executors.newSingleThreadScheduledExecutor();

    private int milliIndex = 0;
    private int secIndex = 0;
    private int minuteIndex = 0;
    private int hourIndex = 0;
    private int dayIndex = 0;

    public CacheExpireTimeWheel(ICache<K, V> cache) {
        this.cache = cache;
        this.removeListener = cache.getRemoveListener();
        //初始化5个定时任务
        ROUND_ROBIN_MILLI.scheduleAtFixedRate(new MilliTask(), 0, milli, TimeUnit.MILLISECONDS);
        ROUND_ROBIN_SEC.scheduleAtFixedRate(new UpperTimerTask(SECOND), 0, 1, TimeUnit.SECONDS);
        ROUND_ROBIN_MIN.scheduleAtFixedRate(new UpperTimerTask(MINUTE), 0, 1, TimeUnit.MINUTES);
        ROUND_ROBIN_HOUR.scheduleAtFixedRate(new UpperTimerTask(HOUR), 0, 1, TimeUnit.HOURS);
        ROUND_ROBIN_DAY.scheduleAtFixedRate(new UpperTimerTask(DAY), 0, 1, TimeUnit.DAYS);
    }

    private class MilliTask implements Runnable {

        @Override
        public void run() {
            TimeWheelNode<K, V> task = milliSeconds[milliIndex];
            milliSeconds[milliIndex] = null;
            milliIndex = (milliIndex + 1) % milliSeconds.length;
            while(task != null) {
                //TODO 执行task过期
                if(task.getKey() != null && task.getExpireAt() != 0){
                    tryToDeleteExpiredKeyFromTimeWheel(task.getKey());
                }
                task = task.getNext();
            }
        }
    }

    private class UpperTimerTask implements Runnable {

        private int timeLevel;

        public UpperTimerTask(int timeLevel) {
            this.timeLevel = timeLevel;
        }

        @Override
        public void run() {
            TimeWheelNode<K, V> task;

            switch (timeLevel){
                case SECOND: {
                    task = seconds[secIndex];
                    resetTasks(task);
                    seconds[secIndex] = null;
                    secIndex = (secIndex + 1) % seconds.length;
                    break;
                }
                case MINUTE: {
                    task = minutes[minuteIndex];
                    resetTasks(task);
                    minutes[minuteIndex] = null;
                    minuteIndex = (minuteIndex + 1) % minutes.length;
                    break;
                }
                case HOUR: {
                    task = hours[hourIndex];
                    resetTasks(task);
                    hours[hourIndex] = null;
                    hourIndex = (hourIndex + 1) % hours.length;
                    break;
                }
                case DAY: {
                    task = days[dayIndex];
                    resetTasks(task);
                    days[dayIndex] = null;
                    dayIndex = (dayIndex + 1) % days.length;
                    break;
                }
            }
        }

        private void resetTasks(TimeWheelNode<K, V> task) {
            while(task != null) {
                TimeWheelNode<K, V> next = task.getNext();
                insertNode(task);
                task = next;
            }
        }
    }

    private void insertNode(TimeWheelNode<K, V> node) {
        long remainTime = node.getExpireAt() - System.currentTimeMillis();
        if (remainTime < milli) {
            //TODO 执行任务
            if(node.getKey() != null && node.getExpireAt() != 0){
                tryToDeleteExpiredKeyFromTimeWheel(node.getKey());
            }
        }else if(remainTime < second){
            //在毫秒时间轮
            int pos = (int) (remainTime / milli);
            doInsert(MILLISECONDS, pos, node);
        }else if(remainTime < minute){
            //在秒级时间轮
            int pos = (int) (remainTime / second);
            doInsert(SECOND, pos, node);
        }else if(remainTime < hour){
            //在分级时间轮
            int pos = (int) (remainTime / minute);
            doInsert(MINUTE, pos, node);
        }else if(remainTime < day){
            //在时级时间轮
            int pos = (int) (remainTime / hour);
            doInsert(HOUR, pos, node);
        }else{
            //在天级时间轮
            int pos = (int) (remainTime / day);
            doInsert(DAY, pos, node);
        }
    }

    private void doInsert(int timeLevel, int pos, TimeWheelNode<K, V> node) {
        TimeWheelNode<K, V>[] nodes = null;
        switch (timeLevel){
            case MILLISECONDS: {
                nodes = milliSeconds;
                break;
            }
            case SECOND: {
                nodes = seconds;
                break;
            }
            case MINUTE: {
                nodes = minutes;
                break;
            }
            case HOUR: {
                nodes = hours;
                break;
            }
            case DAY: {
                nodes = days;
                break;
            }
        }

        if(nodes == null) {
            throw new RuntimeException();
        }

        TimeWheelNode<K, V> head = nodes[pos];
        if(head != null) {
            node.prev(null);
            node.next(head);
            head.prev(node);
            nodes[pos] = node;
        }else{
            node.prev(null);
            node.next(null);
            nodes[pos] = node;
        }
    }


    @Override
    public void expireKeyAt(K key, long expireAt) {
        if(cache.containsKey(key)) {
            TimeWheelNode<K, V> node = new TimeWheelNode<>(key, expireAt);
            keyToTimeWheelNode.put(key, node);
            insertNode(node);
        }
    }

    @Override
    public void expireKey(K key, long expire) {
        long expireAt = System.currentTimeMillis() + expire;
        expireKeyAt(key, expireAt);
    }

    @Override
    public void lazyDeleteAllExpiredKeys() {

    }

    @Override
    public Long expireTime(K key) {
        return keyToTimeWheelNode.get(key).getExpireAt();
    }

    @Override
    public void tryToDeleteExpiredKey(K key) {
        TimeWheelNode<K, V> node = keyToTimeWheelNode.get(key);
        if(node != null) {
            Long expireTime = node.getExpireAt();
            long currTime = System.currentTimeMillis();
            if(expireTime <= currTime) {
                node.key(null);
                node.expireAt(0);
                tryToDeleteExpiredKeyFromTimeWheel(key);
            }
        }
    }

    public void tryToDeleteExpiredKeyFromTimeWheel(K key) {
        keyToTimeWheelNode.remove(key);
        if(cache.containsKey(key)) {
            V value = cache.remove(key);
            cache.getEvictStrategy().deleteKey(key, cache);
            removeListener.listen(key, value, CacheRemoveConstant.REMOVE_EXPIRE);
        }

    }

    @Override
    public void deleteAllKeys() {
        keyToTimeWheelNode.clear();
        Arrays.fill(milliSeconds, null);
        Arrays.fill(seconds, null);
        Arrays.fill(minutes, null);
        Arrays.fill(hours, null);
        Arrays.fill(days, null);
    }
}
