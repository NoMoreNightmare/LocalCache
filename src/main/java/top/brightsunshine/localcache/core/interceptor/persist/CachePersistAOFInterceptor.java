package top.brightsunshine.localcache.core.interceptor.persist;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import top.brightsunshine.localcache.cacheInterface.ICache;
import top.brightsunshine.localcache.cacheInterface.ICacheInterceptor;
import top.brightsunshine.localcache.cacheInterface.ICachePersist;
import top.brightsunshine.localcache.core.constant.CachePersistConstant;
import top.brightsunshine.localcache.core.entry.AofPersistEntry;
import top.brightsunshine.localcache.core.interceptor.context.CacheInterceptorContext;
import top.brightsunshine.localcache.core.persist.CachePersistAOF;

import java.lang.reflect.Method;

public class CachePersistAOFInterceptor<K, V> implements ICacheInterceptor<K, V> {
    @Override
    public void before(CacheInterceptorContext<K, V> context) {

    }

    @Override
    public void after(CacheInterceptorContext<K, V> context) {
        Method method = context.getMethod();
        Object[] args = context.getArgs();

        ICache<K, V> cache = context.getCache();
        ICachePersist<K, V> cachePersist = cache.getPersistStrategy();
        if(cachePersist == null){
            //无持久化，不进行操作
        }else{
            if(cachePersist instanceof CachePersistAOF){
                //AOF持久化
                AofPersistEntry entry = new AofPersistEntry();
                entry.setMethod(method.getName());
                entry.setArgs(args);
                ObjectMapper objectMapper = new ObjectMapper();

                if(((CachePersistAOF<K, V>) cachePersist).mode() == CachePersistConstant.AOF_ALWAYS){

                    try {
                        String json = objectMapper.writeValueAsString(entry);
                        //写入后立刻刷新到磁盘文件中
                        ((CachePersistAOF<K, V>) cachePersist).appendAof(json);
                        cachePersist.persist();
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                }else{
                    try {
                        String json = objectMapper.writeValueAsString(entry);
                        //写入buffer后，交由后台线程定期刷新
                        ((CachePersistAOF<K, V>) cachePersist).appendAof(json);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }//TODO RDB持久化，RDB和AOF混合持久化

    }
}
