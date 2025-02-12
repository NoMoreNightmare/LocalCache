package top.brightsunshine.localcache.core.persist;

import com.fasterxml.jackson.databind.ObjectMapper;
import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.core.entry.AofPersistEntry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;

public class CacheAOFRewrite<K, V> {
    private String aofFilePath;
    private String rewriteFilePath;

    private List<String> buffer = new ArrayList<>();

    private Map<K, V> replicaCache;

    private boolean isRewriting = false;

//    private CachePersistAOF<K, V> cachePersistAOF;

    public CacheAOFRewrite(String aofFilePath, CachePersistAOF<K, V> cachePersistAOF) {
        this.aofFilePath = aofFilePath;
        this.rewriteFilePath = aofFilePath + ".rewrite";
//        this.cachePersistAOF = cachePersistAOF;
    }

    public void append(String line) {
        buffer.add(line);
    }

    public void setReplicaCache(Map<K, V> replicaCache) {
        this.replicaCache = replicaCache;
    }

    public void flushCacheToFile(Map<K, Long> expireTimes) {
        File file = new File(rewriteFilePath);
        if(!file.exists()){
            Path path = Paths.get(rewriteFilePath);
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
            writer = new PrintWriter(new FileWriter(rewriteFilePath, true));
            for (Map.Entry<K, V> kvEntry : replicaCache.entrySet()) {
                if(expireTimes.containsKey(kvEntry.getKey())){
                    String method = "put";
                    Object[] args = {kvEntry.getKey(), kvEntry.getValue(), expireTimes.get(kvEntry.getKey())};
                    AofPersistEntry<K, V> entry = new AofPersistEntry<>();
                    entry.setMethod(method);
                    entry.setArgs(args);
                    ObjectMapper objectMapper = new ObjectMapper();
                    String json = objectMapper.writeValueAsString(entry);
                    writer.println(json);
                }else{
                    String method = "put";
                    Object[] args = {kvEntry.getKey(), kvEntry.getValue()};
                    AofPersistEntry<K, V> entry = new AofPersistEntry<>();
                    entry.setMethod(method);
                    entry.setArgs(args);
                    ObjectMapper objectMapper = new ObjectMapper();
                    String json = objectMapper.writeValueAsString(entry);
                    writer.println(json);
                }
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Throwable throwable){
            throw new RuntimeException(throwable);
        } finally {
            if(writer != null){
                writer.close();
            }
        }


    }

    /**
     * 刷新重写缓冲区
     */
    public void flushRewriteBuffer(){
        //刷新aof重写缓冲区中的数据
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new FileWriter(rewriteFilePath, true));
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

    public void replaceAofFile(){
        //替换原AOF文件
        File rewriteFile = new File(rewriteFilePath);
        File aof = new File(aofFilePath);
        if(aof.exists()){
            aof.delete();
        }

        rewriteFile.renameTo(aof);
    }

    public boolean isRewriting() {
        return isRewriting;
    }

    public void setRewriting(boolean rewriting) {
        isRewriting = rewriting;
    }

}
