package top.brightsunshine.localcache.core.persist;

import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.cacheInterface.ICachePersist;
import top.brightsunshine.localcache.core.constant.CachePersistConstant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

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

    /**
     * aof重写线程
     */
    private static final ScheduledExecutorService REWRITE_SERVICE = Executors.newSingleThreadScheduledExecutor();

    /**
     * aof重写锁
     */
    private ReentrantLock rewriteLock = new ReentrantLock();

    /**
     * aof重写
     */
    private CacheAOFRewrite<K, V> cacheAOFRewrite;

//    private long rewriteThreshold = 1024 * 1024 * 10; //10MB
    private long rewriteThreshold = 1; //1MB

    public CachePersistAOF(ICache<K, V> cache, int mode, String aofFilePath) {
        this.cache = cache;
        this.mode = mode;
        this.aofFilePath = aofFilePath;
        this.cacheAOFRewrite = new CacheAOFRewrite<>(aofFilePath, this);
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
            rewriteLock.lock();
            cachePersist.persist();
            rewriteLock.unlock();
        }
    }

    public CachePersistAOF(ICache<K, V> cache) {
        this.cache = cache;
        this.mode = AOF_ALWAYS;
        this.aofFilePath = DEFAULT_AOF_PATH;
        this.cacheAOFRewrite = new CacheAOFRewrite(aofFilePath, this);
    }

    public int mode(){
        return mode;
    }

    @Override
    public void persist() {
        File file = new File(aofFilePath);
        if(!file.exists()){
            Path path = Paths.get(aofFilePath);
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
            writer = new PrintWriter(new FileWriter(aofFilePath, true));
            for (String json : buffer) {
                writer.println(json);
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(writer != null){
                writer.close();
            }
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
        //如果正在进行持久化，或者正在把AOF重写缓冲区里的数据刷新到磁盘文件中，加锁
        rewriteLock.lock();
        if(json != null && !json.isEmpty()){
            buffer.add(json);
        }
        rewriteLock.unlock();

        if(cacheAOFRewrite.isRewriting()){
            cacheAOFRewrite.append(json);
        }else{
            File file = new File(aofFilePath);
            if(file.length() >= rewriteThreshold){
                REWRITE_SERVICE.schedule(new RewriteThread(), 0, TimeUnit.SECONDS);
            }
        }
    }

    public ReentrantLock getRewriteLock() {
        return rewriteLock;
    }

    public void setRewriteLock(ReentrantLock rewriteLock) {
        this.rewriteLock = rewriteLock;
    }

    private class RewriteThread implements Runnable {
        @Override
        public void run() {
            System.out.println("start...");
            startAofRewrite();
            System.out.println("end...");
        }
    }

    public void startAofRewrite(){
        //1.加锁
        rewriteLock.lock();
        //设置isRewrite为true
            //获取expireTimes
            //创建cache的快照
        cacheAOFRewrite.setRewriting(true);
        Map<K, Long> expireMap = cache.getExpireStrategy().expireTimes();
        Map<K, V> replicaCache = new HashMap<>(cache.map());
        cacheAOFRewrite.setReplicaCache(replicaCache);
        //2.解锁
        rewriteLock.unlock();
            //开始将快照cache里的数据写入到重写文件
            //同时所有记录在aof和aof重写缓冲区存一份
        cacheAOFRewrite.flushCacheToFile(expireMap);
        //3.加锁（这个过程禁止处理新命令）
        rewriteLock.lock();
            //把aof缓冲区刷到旧aof文件，aof重写缓冲区刷到新aof文件
        CompletableFuture<Void> flushAofBuffer = CompletableFuture.runAsync(this::persist);
        CompletableFuture<Void> flushAofRewriteBuffer = CompletableFuture.runAsync(cacheAOFRewrite::flushRewriteBuffer);

            //在上面两个完成后，替换aof文件
        CompletableFuture<Void> finalTask = CompletableFuture.allOf(flushAofBuffer, flushAofRewriteBuffer)
                .thenRun(cacheAOFRewrite::replaceAofFile)
                .thenRun(() -> {
                    //将isRewrite设置为false
                    cacheAOFRewrite.setRewriting(false);
                });

        //4.解锁
        finalTask.join();
        rewriteLock.unlock();
    }
}
