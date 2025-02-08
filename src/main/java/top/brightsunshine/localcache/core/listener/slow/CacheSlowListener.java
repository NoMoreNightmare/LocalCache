package top.brightsunshine.localcache.core.listener.slow;

import com.fasterxml.jackson.databind.ObjectMapper;
import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.cacheInterface.ICacheSlowListener;
import top.brightsunshine.localcache.core.interceptor.context.CacheInterceptorContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

public class CacheSlowListener<K, V> implements ICacheSlowListener<K, V> {
    private long slowerThan = 100L;

    private String slowLog = "slowlog.log";

    public CacheSlowListener(long slowerThan) {
        this.slowerThan = slowerThan;
    }

    public CacheSlowListener() {}

    @Override
    public long slowerThan() {
        return slowerThan;
    }

    @Override
    public void listen(CacheInterceptorContext<K, V> context) {
        File file = new File(slowLog);
        if(!file.exists()){
            Path path = Paths.get(slowLog);
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
        try{
            writer = new PrintWriter(new FileWriter(file));
            ObjectMapper objectMapper = new ObjectMapper();
            String format = MessageFormat.format("slow method: {0}, arguments: {1}, execution time: {2}\n", context.getMethod().getName(), objectMapper.writeValueAsString(context.getArgs()), context.getEndTime() - context.getStartTime());
            writer.println(format);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
