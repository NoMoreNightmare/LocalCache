package top.brightsunshine.localcache.core.persist;

import com.fasterxml.jackson.databind.ObjectMapper;
import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.cacheInterface.ICachePersist;
import top.brightsunshine.localcache.core.entry.RdbPersistEntry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static top.brightsunshine.localcache.core.constant.CachePersistConstant.*;

public class CachePersistRDB<K, V> implements ICachePersist<K, V> {

    /**
     * 持有这个持久化对象的cache
     */
    private ICache<K, V> cache;

    /**
     * 默认刷新周期
     */
    private int period;

    /**
     * rdb文件地址
     */
    private String rdbFilePath;

    private static final ScheduledExecutorService RDB_EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    public CachePersistRDB(ICache<K, V> cache, int persistTimeInfo, String rdbFilePath) {
        this.cache = cache;
        this.period = persistTimeInfo;
        this.rdbFilePath = rdbFilePath;
        RDB_EXECUTOR.scheduleAtFixedRate(new RdbPersistTask<>(this), delay(), period(), timeUnit());
    }
    
    public CachePersistRDB(ICache<K, V> cache) {
        this.cache = cache;
        this.period = RDB_MEDIUM_PERIOD;
        this.rdbFilePath = DEFAULT_RDB_PATH;
    }

    private class RdbPersistTask<K, V> implements Runnable {

        private CachePersistRDB<K, V> rdbPersist;

        public RdbPersistTask(CachePersistRDB<K, V> rdbPersist) {
            this.rdbPersist = rdbPersist;
        }

        @Override
        public void run() {
            this.rdbPersist.persist();
        }
    }


    @Override
    public void persist() {
        File file = new File(rdbFilePath);
        if(!file.exists()){
            Path path = Paths.get(rdbFilePath);
            Path parent = path.getParent();
            if(parent != null){
                File parentFile = parent.toFile();
                if(!parentFile.exists()){
                    parentFile.mkdirs();
                }
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new FileWriter(rdbFilePath, false));
            ObjectMapper objectMapper = new ObjectMapper();
            for (Map.Entry<K, V> kvEntry : cache.entrySet()) {
                K key = kvEntry.getKey();
                V value = kvEntry.getValue();
                Long expireAt = cache.cacheExpire().expireTime(key);

                RdbPersistEntry<K, V> rdbPersistEntry = new RdbPersistEntry<>();
                rdbPersistEntry.setKey(key);
                rdbPersistEntry.setValue(value);
                rdbPersistEntry.setExpireAt(expireAt);
                String json = objectMapper.writeValueAsString(rdbPersistEntry);
                writer.println(json);
                writer.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            if(writer != null){
                writer.close();
            }
        }
    }

    @Override
    public int delay() {
        return period;
    }

    @Override
    public int period() {
        return period;
    }

    @Override
    public TimeUnit timeUnit() {
        return TimeUnit.SECONDS;
    }
}
