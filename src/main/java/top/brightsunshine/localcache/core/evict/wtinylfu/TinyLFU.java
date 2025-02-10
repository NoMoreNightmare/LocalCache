package top.brightsunshine.localcache.core.evict.wtinylfu;

import java.util.Random;

public class TinyLFU<K, V> {
    /**
     * 热点数据的阈值
     */
    private int upgradeThreshold = 7;

    /**
     * 衰减你阈值
     */
    private long resetThreshold;

    /**
     * TinyLFU
     */
    private long[] table;

    /**
     * 总访问频率
     */
    private long accessCount;

    private long[] SEED = {0xc3a5c85c97cb3127L, 0xb492b66fbe98f273L, 0x9ae16a3b2f90404fL, 0xcbf29ce484222325L};

    private long RESET = 0x7777777777777777L;

    public TinyLFU(int capacity) {
        table = new long[nextPowerOfTwo(capacity)];
        resetThreshold = capacity * 10L;
        accessCount = 0;
        Random random = new Random();
        //随机生成4个种子
        SEED[0] = random.nextLong();
        SEED[1] = random.nextLong();
        SEED[2] = random.nextLong();
        SEED[3] = random.nextLong();
    }


    private int nextPowerOfTwo(int x) {
        // 处理特殊情况
        if (x == 0) {
            return 1;
        }
        x--; // 先减 1，防止已经是 2^n 的情况
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        return x + 1;
    }

    public void increment(K key) {
        //计算哈希值
        int hash = key.hashCode();

        int start = (hash & 3) << 2;

        //对对应位置的计数器进行++

        int index0 = indexOf(hash, 0);
        int index1 = indexOf(hash, 1);
        int index2 = indexOf(hash, 2);
        int index3 = indexOf(hash, 3);

        //start的取值是0，4，8，12；那么start + 0/1/2/3的最大值不会超过15
        boolean res = false;
        res |= incrementAt(index0, start);
        res |= incrementAt(index1, start + 1);
        res |= incrementAt(index2, start + 2);
        res |= incrementAt(index3, start + 3);

        accessCount++;
        if(res && accessCount == resetThreshold) {
            //到达衰减阈值，进行衰减操作
            //所有计数器，全部向右移动1位（即，除以2）
            int count = 0;
            for (int i = 0; i < table.length; i++) {
                count += Long.bitCount(table[i]);
                table[i] = (table[i] >> 1) & RESET;
            }

            //重置计数器
            accessCount = (accessCount - (count >>> 2)) >>> 1;
        }
    }

    private int indexOf(int hash, int i) {
        long res = (hash + SEED[i]) % SEED.length;
        res += (res >>> 32);
        return ((int) res) & (table.length - 1);
    }

    boolean incrementAt(int i, int j){
        //TODO 当计数器能够+1时，总计数++
        //因为j的范围在[0,15]，那么左移2位后，得到的offset一定是long类型里16个计数器中的某一个的起始地址
        int offset = j << 2;
        //对应计数器位置为15的mask，用于判断计数器是否已满
        long mask = (0xfL << offset);
        if((table[i] & mask) != mask){
            table[i] += (1L << offset);
            return true;
        }
        return false;
    }

    int getCount(int i, int j) {
        int offset = j << 2;
        long mask = (0xfL << offset);

        int value = (int) ((table[i] & mask) >>> offset);
        return value;
    }

    public int getCount(K key){
        //计算哈希值
        //获取计数
        //计算哈希值
        int hash = key.hashCode();

        int start = (hash & 3) << 2;

        //对对应位置的计数器进行++

        int index0 = indexOf(hash, 0);
        int index1 = indexOf(hash, 1);
        int index2 = indexOf(hash, 2);
        int index3 = indexOf(hash, 3);

        int minCount = getCount(index0, start);
        minCount = Math.min(minCount, getCount(index1, start + 1));
        minCount = Math.min(minCount, getCount(index2, start + 2));
        minCount = Math.min(minCount, getCount(index3, start + 3));
        //比较大小
        return minCount;
    }

    public boolean needToUpgrade(K key) {
        return getCount(key) >= upgradeThreshold;
    }
}
