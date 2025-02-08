package top.brightsunshine.localcache.core.persist;

import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.cacheInterface.ICachePersist;
import top.brightsunshine.localcache.core.constant.CachePersistConstant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static top.brightsunshine.localcache.core.constant.CachePersistConstant.*;

public class CachePersistAOF<K, V> implements ICachePersist<K, V> {

    /**
     * 持有这个持久化对象的cache
     */
    private ICache<K, V> cache;

    /**
     * aof的执行模式
     */
    private int mode;

    /**
     * aof的缓冲区
     */
    private List<String> buffer = new ArrayList<>();

    /**
     * aof文件地址
     */
    private String aofFilePath;

    /**
     * 执行线程
     */
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public CachePersistAOF(ICache<K, V> cache, int mode, String aofFilePath) {
        this.cache = cache;
        this.mode = mode;
        this.aofFilePath = aofFilePath;
        this.startAOFFlush();
    }

    private void startAOFFlush() {
        switch (mode){
            case AOF_EVERYSEC: {
                EXECUTOR_SERVICE.scheduleAtFixedRate(new FlushThread(this), delay(), period(), timeUnit());
            }
            case AOF_THIRTY_SEC: {
                EXECUTOR_SERVICE.scheduleAtFixedRate(new FlushThread(this), delay(), period(), timeUnit());
            }
            default: {
                return;
            }
        }
    }

    private class FlushThread implements Runnable {

        private ICachePersist<K, V> cachePersist;

        public FlushThread(ICachePersist<K, V> cachePersist) {
            this.cachePersist = cachePersist;
        }

        @Override
        public void run() {
            //把buffer中的数据刷新到磁盘文件
            cachePersist.persist();
        }
    }

    public CachePersistAOF(ICache<K, V> cache) {
        this.cache = cache;
        this.mode = AOF_ALWAYS;
        this.aofFilePath = DEFAULT_AOF_PATH;
    }

    public int mode(){
        return mode;
    }

    @Override
    public void persist() {
        File file = new File(aofFilePath);
        if(!file.exists()){
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            PrintWriter writer = new PrintWriter(new FileWriter(aofFilePath, true));
            for (String json : buffer) {
                writer.println(json);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        buffer.clear();
    }

    @Override
    public int delay() {
        switch (mode){
            case AOF_EVERYSEC: {
                return 1;
            }
            case AOF_THIRTY_SEC: {
                return 30;
            }
            default: {
                return 0;
            }
        }
    }

    @Override
    public int period() {
        switch (mode){
            case AOF_EVERYSEC: {
                return 1;
            }
            case AOF_THIRTY_SEC: {
                return 30;
            }
            default: {
                return 0;
            }
        }
    }

    @Override
    public TimeUnit timeUnit() {
        return TimeUnit.SECONDS;
    }

    public void appendAof(String json){
        if(json != null && !json.isEmpty()){
            buffer.add(json);
        }
    }
}
